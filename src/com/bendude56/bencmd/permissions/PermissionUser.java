package com.bendude56.bencmd.permissions;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;

import com.bendude56.bencmd.BenCmd;

public class PermissionUser {
	private InternalUser	user;

	public static PermissionUser matchUserIgnoreCase(String name) {
		for (Object oUser : BenCmd.getPermissionManager().getUserFile().listUsers().values()) {
			if (((InternalUser) oUser).getName().equalsIgnoreCase(name)) {
				return new PermissionUser((InternalUser) oUser);
			}
		}
		return null;
	}

	public static PermissionUser matchUser(String name) {
		for (Object oUser : BenCmd.getPermissionManager().getUserFile().listUsers().values()) {
			if (((InternalUser) oUser).getName().equals(name)) {
				return new PermissionUser((InternalUser) oUser);
			}
		}
		return null;
	}

	public static PermissionUser newUser(String name, List<String> permissions) {
		return new PermissionUser(name, permissions);
	}

	public PermissionUser() {
		user = new InternalUser("*", new ArrayList<String>());
	}

	public PermissionUser(InternalUser internal) {
		user = internal;
	}

	protected PermissionUser(String name, List<String> permissions) {
		user = new InternalUser(name, permissions);
	}

	private void updateInternal() {
		if (user.isServer()) {
			return;
		}
		InternalUser ouser = user;
		user = BenCmd.getPermissionManager().getUserFile().getInternal(user.getName());
		if (user == null && ouser == null) {
			throw new NullPointerException();
		} else if (user == null) {
			user = ouser;
		}
	}

	public PermissionGroup highestLevelGroup() {
		return InternalGroup.highestLevelP(BenCmd.getPermissionManager().getGroupFile().getAllUserGroups(this));
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
		for (PermissionGroup group : BenCmd.getPermissionManager().getGroupFile().getAllUserGroups(user)) {
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
		for (PermissionGroup group : BenCmd.getPermissionManager().getGroupFile().getAllUserGroups(user)) {
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
		for (String s : user.getPermissions(true, false)) {
			if (s.isEmpty()) {
				continue;
			}
			if (user.getPermissions(false, false).contains(s)) {
				s = ChatColor.AQUA + s + ChatColor.GRAY;
			} else {
				s = ChatColor.GRAY + s;
			}
			if (list.isEmpty()) {
				list = s;
			} else {
				list += ",  " + s;
			}
		}
		if (list.isEmpty()) {
			return ChatColor.GRAY + "(None)";
		} else {
			return list;
		}
	}

	public boolean isDev() {
		return user.isDev();
	}

	public String listGroups() {
		String groups = "";
		for (PermissionGroup group : BenCmd.getPermissionManager().getGroupFile().getAllUserGroups(this)) {
			boolean direct = false;
			for (PermissionGroup group2 : BenCmd.getPermissionManager().getGroupFile().getUserGroups(this)) {
				if (group.getName().equals(group2.getName())) {
					direct = true;
					break;
				}
			}
			String gname = group.getName();
			if (direct) {
				gname = ChatColor.GREEN + gname + ChatColor.GRAY;
			} else {
				gname = ChatColor.GRAY + "*" + gname;
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
