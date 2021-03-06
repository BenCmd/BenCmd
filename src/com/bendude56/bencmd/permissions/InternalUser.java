package com.bendude56.bencmd.permissions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.InvalidPermissionError;

public class InternalUser {
	private static final List<String>	jailPerm = Arrays.asList(new String[] { "bencmd.chat.list", "bencmd.chat.noslow", "bencmd.ticket.readown", "bencmd.lot.info", "bencmd.lock.info" });
	
	private String						name;
	private List<String>				permissions;
	private List<String>				ignoredUsers;

	protected InternalUser(String name, List<String> permissions, List<String> ignoredUsers) {
		this.name = name;
		this.permissions = permissions;
		this.ignoredUsers = ignoredUsers;
	}
	
	public boolean isIgnoring(String name) {
		return ignoredUsers.contains(name.toLowerCase());
	}
	
	public void ignore(String name) {
		ignoredUsers.add(name.toLowerCase());
		BenCmd.getPermissionManager().getUserFile().updateUser(this, true);
	}
	
	public void unignore(String name) {
		ignoredUsers.remove(name.toLowerCase());
		BenCmd.getPermissionManager().getUserFile().updateUser(this, true);
	}
	
	public List<String> getIgnoring() {
		return ignoredUsers;
	}

	public String getName() {
		return name;
	}

	public boolean isDev() {
		for (String dev : BenCmd.devs) {
			if (dev.equalsIgnoreCase(name)) {
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
				if (!p.contains("=") || includeVars) {
					perms.add(p);
				}
			}
		}
		if (testGroup) {
			for (PermissionGroup group : BenCmd.getPermissionManager().getGroupFile().getAllUserGroups(this)) {
				perms.addAll(group.getInternal().getPermissions(false, includeVars));
			}
		}
		return perms;
	}

	public Action isMuted() {
		return BenCmd.getPermissionManager().getActionFile().isMuted(new PermissionUser(this));
	}

	public Action isJailed() {
		return BenCmd.getPermissionManager().getActionFile().isJailed(new PermissionUser(this));
	}

	public Action isBanned() {
		return BenCmd.getPermissionManager().getActionFile().isBanned(new PermissionUser(this));
	}

	public boolean hasPerm(String perm, boolean testStar, boolean testGroup) {
		if (perm.contains(",")) {
			throw new InvalidPermissionError(perm, "Permissions cannot contain commas!");
		} else if (!perm.contains(".") && !perm.equals("*")) {
			throw new InvalidPermissionError(perm, "Permissions in the root namespace are not allowed!");
		}
		if ((isServer() || isDev()) && testGroup) {
			return testStar;
		}
		boolean isStarred = false;
		boolean isJailed = isJailed() != null;
		boolean isDenied = false;
		boolean isAllowed = false;
		List<String> perms = new ArrayList<String>(permissions);
		if (testGroup) {
			for (PermissionGroup group : BenCmd.getPermissionManager().getGroupFile().getAllUserGroups(this)) {
				perms.addAll(group.getInternal().getPermissions(true, true));
			}
			Player p = Bukkit.getPlayerExact(name);
			if (p != null) {
				for (PermissionGroup group : BenCmd.getPermissionManager().getGroupFile().getAllUserGroups(this)) {
					if (BenCmd.getPermissionManager().getGroupFile().groupExists(group.getName() + "@" + p.getWorld().getName())) {
						perms.addAll(BenCmd.getPermissionManager().getGroupFile().getGroup(group.getName() + "@" + p.getWorld().getName()).getInternal().getPermissions(true, true));
					}
				}
			}
		}
		List<String> possibleStars = new ArrayList<String>();
		String currentNamespace = "";
		for (String splt : perm.split("\\.")) {
			possibleStars.add(currentNamespace + "*");
			currentNamespace += splt + ".";
		}
		for (String perm2 : perms) {
			if (possibleStars.contains(perm2)) {
				isStarred = true;
			}
			if (perm2.equalsIgnoreCase("-" + perm)) {
				isDenied = true;
			}
			if (perm.equalsIgnoreCase(perm2)) {
				isAllowed = true;
			}
		}
		if (isJailed && jailPerm.contains(perm)) {
			isJailed = false;
		}
		if ((isDenied || isJailed) && testStar) {
			return false;
		} else if (isStarred && testStar) {
			return true;
		} else {
			return isAllowed;
		}
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
		for (InternalGroup i : BenCmd.getPermissionManager().getGroupFile().getUserGroups(this)) {
			String v = i.getVar(variable, null);
			if (v != null) {
				s.put(i, v);
			}
		}
		if (!s.isEmpty()) {
			return s.get(InternalGroup.highestLevel(s.keySet()));
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
		BenCmd.getPermissionManager().getUserFile().updateUser(this, true);
	}

	public void setVar(String variable, String value) {
		if (getVar(variable) != null) {
			remVar(variable);
		}
		addPerm(variable + "=" + value);
	}

	public void addPerm(String perm) {
		if (isServer()) {
			return;
		}
		permissions.add(perm);
		BenCmd.getPermissionManager().getUserFile().updateUser(this, true);
	}

	public void remPerm(String perm) {
		if (isServer()) {
			return;
		}
		permissions.remove(perm);
		BenCmd.getPermissionManager().getUserFile().updateUser(this, true);
	}

	public boolean inGroup(PermissionGroup group) {
		if (isServer()) {
			return false;
		}
		for (PermissionGroup group2 : BenCmd.getPermissionManager().getGroupFile().getAllUserGroups(this)) {
			if (group.getName().equals(group2.getName())) {
				return true;
			}
		}
		return false;
	}

	public boolean isServer() {
		return (name == "*");
	}
}
