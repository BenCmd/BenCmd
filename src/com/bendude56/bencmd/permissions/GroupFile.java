package com.bendude56.bencmd.permissions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.BenCmdFile;

public class GroupFile extends BenCmdFile {
	HashMap<String, InternalGroup> groups = new HashMap<String, InternalGroup>();

	public GroupFile() {
		super("groups.db", "--BenCmd Group File--", true);
		loadFile();
		loadAll();
	}

	public List<String> listGroups() {
		return new ArrayList<String>(groups.keySet());
	}

	public void updateGroup(InternalGroup group, boolean saveFile) {
		groups.put(group.getName(), group);
		String permissions = "";
		for (String permission : group.getPermissions(false)) {
			if (permissions.isEmpty()) {
				permissions += permission;
			} else {
				permissions += "," + permission;
			}
		}
		String users = "";
		for (String user : group.getUsers()) {
			if (users.isEmpty()) {
				users += user;
			} else {
				users += "," + user;
			}
		}
		String groups = "";
		for (String groupn : group.getGroups()) {
			if (groups.isEmpty()) {
				groups += groupn;
			} else {
				groups += "," + groupn;
			}
		}
		getFile().put(
				group.getName(),
				permissions + "/" + users + "/" + groups + "/"
						+ group.getPrefix() + "/"
						+ Integer.toString(group.getColorCode(), 16) + "/"
						+ group.getLevel().toString());
		if (saveFile)
			saveFile();
	}

	public void addGroup(PermissionGroup group) {
		updateGroup(group.getInternal(), true);
	}

	public void removeGroup(PermissionGroup group) {
		for (PermissionGroup group2 : getGroupGroups(group)) {
			group2.removeGroup(group);
		}
		getFile().remove(group.getName());
		groups.remove(group.getName());
		saveFile();
	}

	public void loadAll() {
		groups.clear();
		for (int i = 0; i < getFile().size(); i++) {
			String name = (String) getFile().keySet().toArray()[i];
			try {
				String value = getFile().getProperty(name);
				List<String> permissions = new ArrayList<String>();
				List<String> users = new ArrayList<String>();
				List<String> groups = new ArrayList<String>();
				String prefix;
				Integer color;
				Integer level;
				permissions
						.addAll(Arrays.asList(value.split("/")[0].split(",")));
				users.addAll(Arrays.asList(value.split("/")[1].split(",")));
				groups.addAll(Arrays.asList(value.split("/")[2].split(",")));
				prefix = value.split("/")[3];
				try {
					color = Integer.parseInt(value.split("/")[4], 16);
				} catch (NumberFormatException e) {
					color = -1;
				}
				try {
					level = Integer.parseInt(value.split("/")[5]);
				} catch (NumberFormatException e) {
					level = 0;
				}
				this.groups.put(name, new InternalGroup(name,
						permissions, users, groups, prefix, color, level));
			} catch (Exception e) {
				BenCmd.log(Level.SEVERE,  "Group " + name + " failed to load:");
				BenCmd.log(e);
			}
		}
	}
	
	public void saveAll() {
		for (Map.Entry<String, InternalGroup> e : groups.entrySet()) {
			updateGroup(e.getValue(), false);
		}
		saveFile();
	}

	public PermissionGroup getGroup(String groupName) {
		InternalGroup group;
		if ((group = getInternal(groupName)) != null) {
			return new PermissionGroup(group);
		} else {
			return null;
		}
	}

	protected InternalGroup getInternal(String groupName) {
		for (InternalGroup group : groups.values()) {
			if (group.getName().equalsIgnoreCase(groupName)) {
				return group;
			}
		}
		return null;
	}

	public boolean groupExists(String groupName) {
		return getGroup(groupName) != null;
	}

	public List<PermissionGroup> getAllUserGroups(PermissionUser user) {
		List<PermissionGroup> groups = new ArrayList<PermissionGroup>();
		List<PermissionGroup> toCheck = getUserGroups(user);
		while (!toCheck.isEmpty()) {
			toCheck.addAll(getGroupGroups(toCheck.get(0)));
			groups.add(toCheck.get(0));
			toCheck.remove(0);
		}
		return groups;
	}

	public List<PermissionGroup> getAllUserGroups(InternalUser user) {
		List<PermissionGroup> groups = new ArrayList<PermissionGroup>();
		List<PermissionGroup> toCheck = getUserGroups(user);
		while (!toCheck.isEmpty()) {
			toCheck.addAll(getGroupGroups(toCheck.get(0)));
			groups.add(toCheck.get(0));
			toCheck.remove(0);
		}
		return groups;
	}

	public List<PermissionGroup> getUserGroups(PermissionUser user) {
		List<PermissionGroup> groups = new ArrayList<PermissionGroup>();
		for (InternalGroup internal : this.groups.values()) {
			if (internal.userInGroup(user)) {
				groups.add(new PermissionGroup(internal));
			}
		}
		return groups;
	}

	public List<PermissionGroup> getUserGroups(InternalUser user) {
		List<PermissionGroup> groups = new ArrayList<PermissionGroup>();
		for (InternalGroup internal : this.groups.values()) {
			if (internal.userInGroup(new PermissionUser(user))) {
				groups.add(new PermissionGroup(internal));
			}
		}
		return groups;
	}

	public List<PermissionGroup> getGroupGroups(PermissionGroup group) {
		List<PermissionGroup> groups = new ArrayList<PermissionGroup>();
		for (InternalGroup internal : this.groups.values()) {
			if (internal.getName().equals(group.getName())) {
				continue;
			}
			if (internal.groupInGroup(group)) {
				groups.add(new PermissionGroup(internal));
			}
		}
		return groups;
	}

	public List<InternalGroup> getGroupGroups(InternalGroup group) {
		List<InternalGroup> groups = new ArrayList<InternalGroup>();
		for (InternalGroup internal : this.groups.values()) {
			if (internal.groupInGroup(group)) {
				groups.add(internal);
			}
		}
		return groups;
	}
}
