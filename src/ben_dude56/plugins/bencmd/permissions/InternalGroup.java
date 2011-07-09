package ben_dude56.plugins.bencmd.permissions;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;

import ben_dude56.plugins.bencmd.BenCmd;

class InternalGroup {
	protected BenCmd plugin;
	private String groupName;
	private List<String> permissions;
	private List<String> users;
	private List<String> groups;
	private String prefix;
	private Integer color;
	private Integer level;

	static InternalGroup highestLevel(List<InternalGroup> groups) {
		InternalGroup highest = null;
		for (InternalGroup group : groups) {
			if (highest == null || group.getLevel() > highest.getLevel()) {
				highest = group;
			}
		}
		return highest;
	}

	protected InternalGroup(BenCmd instance, String groupName,
			List<String> permissions, List<String> users, List<String> groups,
			String prefix, Integer color, Integer level) {
		this.plugin = instance;
		this.groupName = groupName;
		this.permissions = permissions;
		this.users = users;
		this.groups = groups;
		this.prefix = prefix;
		this.color = color;
		this.level = level;
	}

	public boolean userInGroup(PermissionUser user) {
		for (String suser : users) {
			if (user.getName().equalsIgnoreCase(suser)) {
				return true;
			}
		}
		return false;
	}

	public boolean groupInGroup(PermissionGroup group) {
		for (String sgroup : groups) {
			if (group.getName().equalsIgnoreCase(sgroup)) {
				return true;
			}
		}
		return false;
	}

	protected boolean groupInGroup(InternalGroup group) {
		for (String sgroup : groups) {
			if (group.getName().equalsIgnoreCase(sgroup)) {
				return true;
			}
		}
		return false;
	}

	public List<String> getPermissions(boolean testGroup) {
		List<String> perms = new ArrayList<String>();
		perms.addAll(permissions);
		if (testGroup) {
			for (InternalGroup group : plugin.perm.groupFile
					.getGroupGroups(this)) {
				perms.addAll(group.getPermissions(true));
			}
		}
		return perms;
	}

	public boolean hasPerm(String perm, boolean testStar, boolean testGroup) {
		List<String> perms = getPermissions(testGroup);
		if (perms.contains("*") && testStar) {
			return true;
		}
		for (String perm2 : perms) {
			if (perm.equalsIgnoreCase(perm2)) {
				return true;
			}
		}
		return false;
	}

	protected List<String> getGroups() {
		return groups;
	}

	protected List<String> getUsers() {
		return users;
	}

	public String getName() {
		return groupName;
	}

	public String getPrefix() {
		if (prefix.isEmpty()) {
			List<InternalGroup> possibleGroups = new ArrayList<InternalGroup>();
			for (InternalGroup group : plugin.perm.groupFile
					.getGroupGroups(this)) {
				if (!group.getPrefix().isEmpty()) {
					possibleGroups.add(group);
				}
			}
			if (possibleGroups.isEmpty()) {
				return "";
			} else {
				return InternalGroup.highestLevel(possibleGroups).getPrefix();
			}
		} else {
			return prefix;
		}
	}

	public Integer getColorCode() {
		return color;
	}

	public ChatColor getColor() {
		if (color == -1) {
			List<InternalGroup> possibleGroups = new ArrayList<InternalGroup>();
			for (InternalGroup group : plugin.perm.groupFile
					.getGroupGroups(this)) {
				if (group.getColor() != ChatColor.YELLOW) {
					possibleGroups.add(group);
				}
			}
			if (possibleGroups.isEmpty()) {
				return ChatColor.YELLOW;
			} else {
				return InternalGroup.highestLevel(possibleGroups).getColor();
			}
		} else {
			return ChatColor.getByCode(color);
		}
	}

	public Integer getLevel() {
		return level;
	}

	public void setPrefix(String value) {
		prefix = value;
		plugin.perm.groupFile.updateGroup(this);
	}

	public void setColor(Integer value) {
		color = value;
		plugin.perm.groupFile.updateGroup(this);
	}

	public void setLevel(Integer value) {
		level = value;
		plugin.perm.groupFile.updateGroup(this);
	}

	public void addUser(String user) {
		users.add(user);
		plugin.perm.groupFile.updateGroup(this);
	}

	public void remUser(String user) {
		users.remove(user);
		plugin.perm.groupFile.updateGroup(this);
	}

	public void addGroup(String group) {
		groups.add(group);
		plugin.perm.groupFile.updateGroup(this);
	}

	public void remGroup(String group) {
		groups.remove(group);
		plugin.perm.groupFile.updateGroup(this);
	}

	public void addPerm(String perm) {
		permissions.add(perm);
		plugin.perm.groupFile.updateGroup(this);
	}

	public void remPerm(String perm) {
		permissions.remove(perm);
		plugin.perm.groupFile.updateGroup(this);
	}
}
