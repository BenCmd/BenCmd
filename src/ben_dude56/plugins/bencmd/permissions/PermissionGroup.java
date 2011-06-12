package ben_dude56.plugins.bencmd.permissions;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import ben_dude56.plugins.bencmd.BenCmd;

public class PermissionGroup {
	private BenCmd plugin;
	private MainPermissions perm;
	private String name;

	public PermissionGroup(BenCmd instance, String groupName)
			throws NullPointerException {
		plugin = instance;
		perm = plugin.perm;
		if (perm.groupFile.groupExists(groupName)) {
			name = groupName;
		} else {
			throw new NullPointerException("Group not in database");
		}
	}

	public boolean canSpawnItem(Material material) {
		return perm.itemList.canSpawn(material, name);
	}

	public boolean hasPerm(String permission) {
		return perm.groupFile.hasPermission(name, permission, true, true);
	}

	public boolean hasPerm(String permission, boolean testStar) {
		return perm.groupFile.hasPermission(name, permission, testStar, true);
	}

	public boolean hasPerm(String permission, boolean testStar,
			boolean testGroup) {
		return perm.groupFile.hasPermission(name, permission, testStar,
				testGroup);
	}

	public PermChangeResult addPermission(String permission) {
		return perm.groupFile.addPermission(name, permission);
	}

	public PermChangeResult deletePermission(String permission) {
		return perm.groupFile.removePermission(name, permission);
	}

	public String getPrefix() {
		return perm.groupFile.getPrefix(name);
	}

	public void setPrefix(String prefix) {
		perm.groupFile.setPrefix(name, prefix);
	}

	public ChatColor getPrefixColor() {
		return perm.groupFile.getColor(name);
	}

	public void setPrefixColor(ChatColor color) {
		perm.groupFile.setColor(name, color);
	}

	public PermissionGroup getGroup() {
		try {
			return new PermissionGroup(plugin, perm.groupFile.getGroup(name));
		} catch (NullPointerException e) {
			return null;
		}
	}

	public PermChangeResult changeGroup(String groupName) {
		return perm.groupFile.changeGroup(name, groupName);
	}

	public PermChangeResult changeGroup(PermissionGroup group) {
		return perm.groupFile.changeGroup(name, group.getName());
	}

	public String getName() {
		return name;
	}
}
