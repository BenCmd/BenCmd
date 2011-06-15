package ben_dude56.plugins.bencmd.permissions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

public class GroupFile extends Properties {

	// TODO For version 1.2.1 : Overhaul group system.

	private static final long serialVersionUID = 0L;
	private MainPermissions mainPerm;
	HashMap<String, InternalGroup> groups = new HashMap<String, InternalGroup>();

	public GroupFile(MainPermissions mainPermissions) {
		mainPerm = mainPermissions; // Initialize the value of the parent
		this.loadFile(); // Load the values into memory.
		this.loadGroups();
	}

	public void loadFile() {
		File file = new File("plugins/BenCmd/groups.db"); // Prepare the file
		if (!file.exists()) {
			try {
				file.createNewFile(); // If the file doesn't exist, create it!
			} catch (IOException ex) {
				// If you can't, produce an error.
				mainPerm.plugin.log.severe("BenCmd had a problem:");
				ex.printStackTrace();
				return;
			}
		}
		try {
			load(new FileInputStream(file)); // Load the values
		} catch (IOException ex) {
			// If you can't, produce an error.
			mainPerm.plugin.log.severe("BenCmd had a problem:");
			ex.printStackTrace();
		}
	}

	public void updateGroup(InternalGroup group) {
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
		this.put(group.getName(), permissions + "/" + users + "/" + groups
				+ "/" + group.getPrefix() + "/"
				+ Integer.toString(group.getColorCode(), 16) + "/" + group.getLevel().toString());
		saveFile();
	}

	public void addGroup(PermissionGroup group) {
		updateGroup(group.getInternal());
	}

	public void removeGroup(PermissionGroup group) {
		groups.remove(group.getName());
		this.remove(group.getName());
		for (PermissionGroup group2 : getGroupGroups(group)) {
			group2.removeGroup(group);
		}
		saveFile();
	}

	public void loadGroups() {
		groups.clear();
		for (int i = 0; i < this.size(); i++) {
			String name = (String) this.keySet().toArray()[i];
			String value = this.getProperty(name);
			List<String> permissions = new ArrayList<String>();
			List<String> users = new ArrayList<String>();
			List<String> groups = new ArrayList<String>();
			String prefix;
			Integer color;
			Integer level;
			permissions.addAll(Arrays.asList(value.split("/")[0].split(",")));
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
			this.groups.put(name, new InternalGroup(mainPerm.plugin, name,
					permissions, users, groups, prefix, color, level));
		}
	}

	public void saveFile() {
		File file = new File("plugins/BenCmd/groups.db"); // Prepare the file
		if (!file.exists()) {
			try {
				file.createNewFile(); // If the file doesn't exist, create it!
			} catch (IOException ex) {
				// If you can't, produce an error.
				mainPerm.plugin.log.severe("BenCmd had a problem:");
				ex.printStackTrace();
				return;
			}
		}
		try {
			// Save the values
			store(new FileOutputStream(file), "BenCmd User Permissions File");
		} catch (IOException ex) {
			// If you can't, produce an error.
			mainPerm.plugin.log.severe("BenCmd had a problem:");
			ex.printStackTrace();
		}
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
		List<PermissionGroup> groups = getUserGroups(user);
		List<PermissionGroup> toCheck = groups;
		while (!toCheck.isEmpty()) {
			toCheck.addAll(getGroupGroups(toCheck.get(0)));
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
			if(internal.getName().equals(group.getName())) {
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
