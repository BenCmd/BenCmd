package com.bendude56.bencmd.permissions;

import java.util.List;

import org.bukkit.ChatColor;

import com.bendude56.bencmd.BenCmd;

public class PermissionGroup {
	private InternalGroup	group;

	protected PermissionGroup(InternalGroup internal) {
		group = internal;
	}

	public PermissionGroup(String groupName, List<String> permissions, List<String> users, List<String> groups, String prefix, Integer color, Integer level) {
		group = new InternalGroup(groupName, permissions, users, groups, prefix, color, level);
	}

	private void updateInternal() {
		group = BenCmd.getPermissionManager().getGroupFile().getInternal(group.getName());
	}

	public boolean userInGroup(PermissionUser user) {
		updateInternal();
		return group.userInGroup(user);
	}

	public boolean groupInGroup(PermissionGroup group) {
		updateInternal();
		return this.group.groupInGroup(group);
	}

	public boolean groupInGroup(InternalGroup group) {
		updateInternal();
		return group.groupInGroup(group);
	}

	public boolean hasPerm(String perm) {
		updateInternal();
		return group.hasPerm(perm, true, true);
	}

	public boolean hasPerm(String perm, boolean testStar) {
		updateInternal();
		return group.hasPerm(perm, testStar, true);
	}

	public boolean hasPerm(String perm, boolean testStar, boolean testGroup) {
		updateInternal();
		return group.hasPerm(perm, testStar, testGroup);
	}

	public String getName() {
		updateInternal();
		return group.getName();
	}

	public String getPrefix() {
		updateInternal();
		return group.getPrefix();
	}

	public Integer getColorCode() {
		updateInternal();
		return group.getColorCode();
	}

	public ChatColor getColor() {
		updateInternal();
		return group.getColor();
	}

	public Integer getLevel() {
		updateInternal();
		return group.getLevel();
	}

	protected InternalGroup getInternal() {
		return group;
	}

	public void setPrefix(String value) {
		updateInternal();
		group.setPrefix(value);
	}

	public void setColor(Integer value) {
		updateInternal();
		group.setColor(value);
	}

	public void setLevel(Integer value) {
		updateInternal();
		group.setLevel(value);
	}

	public void addUser(PermissionUser user) {
		updateInternal();
		group.addUser(user.getName());
	}

	public void removeUser(PermissionUser user) {
		updateInternal();
		group.remUser(user.getName());
	}

	public void addGroup(PermissionGroup group) {
		updateInternal();
		this.group.addGroup(group.getName());
	}

	public void removeGroup(PermissionGroup group) {
		updateInternal();
		this.group.remGroup(group.getName());
	}

	public void addPermission(String permission) {
		updateInternal();
		group.addPerm(permission);
	}

	public void removePermission(String permission) {
		updateInternal();
		group.remPerm(permission);
	}

	public String listPermissions() {
		String list = "";
		for (String s : group.getPermissions(true, false)) {
			if (s.isEmpty()) {
				continue;
			}
			if (group.getPermissions(false, false).contains(s)) {
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

	public String listUsers() {
		String list = "";
		if (group.getUsers().size() >= 50) {
			return ChatColor.GRAY + "Too many to list...";
		}
		for (String s : group.getUsers()) {
			if (s.isEmpty()) {
				continue;
			}
			if (list.isEmpty()) {
				list = ChatColor.GRAY + s;
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
		String list = "";
		if (group.getGroups().size() >= 50) {
			return ChatColor.GRAY + "Too many to list...";
		}
		for (String s : group.getGroups()) {
			if (s.isEmpty()) {
				continue;
			}
			if (list.isEmpty()) {
				list = ChatColor.GRAY + s;
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
}
