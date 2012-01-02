package com.bendude56.bencmd.permissions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.ChatColor;

import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.InvalidPermissionError;

class InternalGroup {
	private String			groupName;
	private List<String>	permissions;
	private List<String>	users;
	private List<String>	groups;
	private String			prefix;
	private Integer			color;
	private Integer			level;

	static InternalGroup highestLevel(Iterable<InternalGroup> groups) {
		InternalGroup highest = null;
		for (InternalGroup group : groups) {
			if (highest == null || group.getLevel() > highest.getLevel()) {
				highest = group;
			}
		}
		return highest;
	}

	static PermissionGroup highestLevelP(List<PermissionGroup> groups) {
		InternalGroup highest = null;
		for (PermissionGroup pgroup : groups) {
			InternalGroup group = pgroup.getInternal();
			if (highest == null || group.getLevel() > highest.getLevel()) {
				highest = group;
			}
		}
		return new PermissionGroup(highest);
	}

	protected InternalGroup(String groupName, List<String> permissions, List<String> users, List<String> groups, String prefix, Integer color, Integer level) {
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

	public List<String> getPermissions(boolean testGroup, boolean includeVars) {
		List<String> perms = new ArrayList<String>();
		if (includeVars) {
			perms.addAll(permissions);
		} else {
			for (String p : permissions) {
				if (!p.contains("=")) {
					perms.add(p);
				}
			}
		}
		if (testGroup) {
			for (InternalGroup group : BenCmd.getPermissionManager().getGroupFile().getGroupGroups(this)) {
				perms.addAll(group.getPermissions(true, includeVars));
			}
		}
		return perms;
	}

	public boolean hasPerm(String perm, boolean testStar, boolean testGroup) {
		if (perm.contains(",")) {
			throw new InvalidPermissionError(perm, "Permissions cannot contain commas!");
		} else if (!perm.contains(".") && !perm.equals("*")) {
			throw new InvalidPermissionError(perm, "Permissions in the root namespace are not allowed!");
		}
		List<String> perms = getPermissions(testGroup, true);
		List<String> possibleStars = new ArrayList<String>();
		String currentNamespace = "";
		for (String splt : perm.split("\\.")) {
			possibleStars.add(currentNamespace + "*");
			currentNamespace += splt + ".";
		}
		for (String perm2 : perms) {
			if (possibleStars.contains(perm2) && testStar) {
				return true;
			}
			if (perm.equalsIgnoreCase(perm2)) {
				return true;
			}
		}
		return false;
	}

	public String getVar(String variable) {
		return getVar(variable, null);
	}

	public String getVar(String variable, String def) {
		if (variable.contains(",")) {
			throw new InvalidPermissionError(variable, "Variable names cannot contain commas!");
		} else if (!variable.contains(".")) {
			throw new InvalidPermissionError(variable, "Variables in the root namespace are not allowed!");
		}
		for (String perm : getPermissions(false, true)) {
			if (perm.startsWith(variable + "=")) {
				return perm.split("=", 2)[1];
			}
		}
		HashMap<InternalGroup, String> s = new HashMap<InternalGroup, String>();
		for (InternalGroup i : BenCmd.getPermissionManager().getGroupFile().getGroupGroups(this)) {
			String v = i.getVar(variable, null);
			if (v != null) {
				s.put(i, v);
			}
		}
		if (!s.isEmpty()) {
			return s.get(highestLevel(s.keySet()));
		} else if (def == null) {
			return null;
		} else {
			addPerm(variable + "=" + def);
			return def;
		}
	}

	public void remVar(String variable) {
		String key = variable + "=" + getVar(variable);
		permissions.remove(key);
	}

	public void setVar(String variable, String value) {
		if (getVar(variable) != null) {
			remVar(variable);
		}
		addPerm(variable + "=" + value);
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
			for (InternalGroup group : BenCmd.getPermissionManager().getGroupFile().getGroupGroups(this)) {
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
			for (InternalGroup group : BenCmd.getPermissionManager().getGroupFile().getGroupGroups(this)) {
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
		BenCmd.getPermissionManager().getGroupFile().updateGroup(this, true);
	}

	public void setColor(Integer value) {
		color = value;
		BenCmd.getPermissionManager().getGroupFile().updateGroup(this, true);
	}

	public void setLevel(Integer value) {
		level = value;
		BenCmd.getPermissionManager().getGroupFile().updateGroup(this, true);
	}

	public void addUser(String user) {
		users.add(user);
		BenCmd.getPermissionManager().getGroupFile().updateGroup(this, true);
	}

	public void remUser(String user) {
		users.remove(user);
		BenCmd.getPermissionManager().getGroupFile().updateGroup(this, true);
	}

	public void addGroup(String group) {
		groups.add(group);
		BenCmd.getPermissionManager().getGroupFile().updateGroup(this, true);
	}

	public void remGroup(String group) {
		groups.remove(group);
		BenCmd.getPermissionManager().getGroupFile().updateGroup(this, true);
	}

	public void addPerm(String perm) {
		permissions.add(perm);
		BenCmd.getPermissionManager().getGroupFile().updateGroup(this, true);
	}

	public void remPerm(String perm) {
		permissions.remove(perm);
		BenCmd.getPermissionManager().getGroupFile().updateGroup(this, true);
	}
}
