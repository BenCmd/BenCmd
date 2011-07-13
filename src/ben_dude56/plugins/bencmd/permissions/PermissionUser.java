package ben_dude56.plugins.bencmd.permissions;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;

import ben_dude56.plugins.bencmd.BenCmd;

public class PermissionUser {
	private InternalUser user;

	public static PermissionUser matchUserIgnoreCase(String name,
			BenCmd instance) {
		for (Object oUser : instance.perm.userFile.listUsers().values()) {
			if (((InternalUser) oUser).getName().equalsIgnoreCase(name)) {
				return new PermissionUser((InternalUser) oUser);
			}
		}
		return null;
	}

	public static PermissionUser matchUser(String name, BenCmd instance) {
		for (Object oUser : instance.perm.userFile.listUsers().values()) {
			if (((InternalUser) oUser).getName().equals(name)) {
				return new PermissionUser((InternalUser) oUser);
			}
		}
		return null;
	}

	public PermissionUser(BenCmd instance) {
		user = new InternalUser(instance, "*", new ArrayList<String>());
	}

	public PermissionUser(InternalUser internal) {
		user = internal;
	}

	protected PermissionUser(BenCmd instance, String name,
			List<String> permissions) {
		user = new InternalUser(instance, name, permissions);
	}

	private void updateInternal() {
		if (user.isServer()) {
			return;
		}
		user = user.plugin.perm.userFile.getInternal(user.getName());
	}

	public String getName() {
		updateInternal();
		return user.getName();
	}

	public Action isMuted() {
		updateInternal();
		return user.isMuted();
	}

	public Action isJailed() {
		updateInternal();
		return user.isJailed();
	}

	public Action isBanned() {
		updateInternal();
		return user.isBanned();
	}

	public boolean hasPerm(String perm) {
		updateInternal();
		return user.hasPerm(perm, true, true);
	}

	public boolean hasPerm(String perm, boolean testStar) {
		updateInternal();
		return user.hasPerm(perm, testStar, true);
	}

	public boolean hasPerm(String perm, boolean testStar, boolean testGroup) {
		updateInternal();
		return user.hasPerm(perm, testStar, testGroup);
	}

	public void addPermission(String permission) {
		updateInternal();
		user.addPerm(permission);
	}

	public void removePermission(String permission) {
		updateInternal();
		user.remPerm(permission);
	}

	public boolean inGroup(PermissionGroup group) {
		updateInternal();
		return user.inGroup(group);
	}

	public String getPrefix() {
		List<InternalGroup> hasPrefix = new ArrayList<InternalGroup>();
		for (PermissionGroup group : user.plugin.perm.groupFile
				.getUserGroups(user)) {
			if (!group.getPrefix().isEmpty()) {
				hasPrefix.add(group.getInternal());
			}
		}
		if (hasPrefix.isEmpty()) {
			return "";
		} else {
			return InternalGroup.highestLevel(hasPrefix).getPrefix();
		}
	}

	public ChatColor getColor() {
		if (isServer()) {
			return ChatColor.BLUE;
		}
		List<InternalGroup> hasColor = new ArrayList<InternalGroup>();
		for (PermissionGroup group : user.plugin.perm.groupFile
				.getUserGroups(user)) {
			if (group.getColor() != ChatColor.YELLOW) {
				hasColor.add(group.getInternal());
			}
		}
		if (hasColor.isEmpty()) {
			return ChatColor.YELLOW;
		} else {
			return InternalGroup.highestLevel(hasColor).getColor();
		}
	}

	public boolean isServer() {
		return user.isServer();
	}

	protected InternalUser getInternal() {
		return user;
	}

	public PermissionUser getPermissionUser() {
		return this;
	}

	public String listPermissions() {
		String list = "";
		for (String s : user.getPermissions(true)) {
			if (s.isEmpty()) {
				continue;
			}
			if (user.getPermissions(false).contains(s)) {
				s = ChatColor.GREEN + s + ChatColor.GRAY;
			} else {
				s = ChatColor.GRAY + s;
			}
			if (list.isEmpty()) {
				list = s;
			} else {
				list += ", " + s;
			}
		}
		if (list.isEmpty()) {
			return ChatColor.GRAY + "(None)";
		} else {
			return list;
		}
	}

	public String listGroups() {
		String groups = "";
		for (PermissionGroup group : user.plugin.perm.groupFile
				.getAllUserGroups(this)) {
			boolean direct = false;
			for (PermissionGroup group2 : user.plugin.perm.groupFile
					.getUserGroups(this)) {
				if (group.getName().equals(group2.getName())) {
					direct = true;
					break;
				}
			}
			String gname = group.getName();
			if (direct) {
				gname = ChatColor.GREEN + gname + ChatColor.GRAY;
			} else {
				gname = ChatColor.GRAY + gname;
			}
			if (groups.isEmpty()) {
				groups = gname;
			} else {
				groups += ", " + gname;
			}
		}
		if (groups.isEmpty()) {
			return "(None)";
		} else {
			return groups;
		}
	}
}
