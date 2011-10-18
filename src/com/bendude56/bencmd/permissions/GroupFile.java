package com.bendude56.bencmd.permissions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.bukkit.util.FileUtil;

import com.bendude56.bencmd.BenCmd;

public class GroupFile extends Properties {

	private static final long serialVersionUID = 0L;
	HashMap<String, InternalGroup> groups = new HashMap<String, InternalGroup>();

	public GroupFile() {
		BenCmd plugin = BenCmd.getPlugin();
		if (new File("plugins/BenCmd/_groups.db").exists()) {
			plugin.log.warning("Group backup file found... Restoring...");
			if (FileUtil.copy(new File("plugins/BenCmd/_groups.db"), new File(
					"plugins/BenCmd/groups.db"))) {
				new File("plugins/BenCmd/_groups.db").delete();
				plugin.log.info("Restoration suceeded!");
			} else {
				plugin.log.warning("Failed to restore from backup!");
			}
		}
		this.loadFile(); // Load the values into memory.
		this.loadGroups();
	}

	public List<String> listGroups() {
		return new ArrayList<String>(groups.keySet());
	}

	public void loadFile() {
		BenCmd plugin = BenCmd.getPlugin();
		File file = new File("plugins/BenCmd/groups.db"); // Prepare the file
		if (!file.exists()) {
			try {
				file.createNewFile(); // If the file doesn't exist, create it!
			} catch (IOException ex) {
				// If you can't, produce an error.
				plugin.log.severe("BenCmd had a problem:");
				ex.printStackTrace();
				return;
			}
		}
		try {
			load(new FileInputStream(file)); // Load the values
		} catch (IOException ex) {
			// If you can't, produce an error.
			plugin.log.severe("BenCmd had a problem:");
			ex.printStackTrace();
		}
	}

	public void updateGroup(InternalGroup group) {
		BenCmd plugin = BenCmd.getPlugin();
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
		this.put(
				group.getName(),
				permissions + "/" + users + "/" + groups + "/"
						+ group.getPrefix() + "/"
						+ Integer.toString(group.getColorCode(), 16) + "/"
						+ group.getLevel().toString());
		try {
			new File("plugins/BenCmd/_groups.db").createNewFile();
			if (!FileUtil.copy(new File("plugins/BenCmd/groups.db"), new File(
					"plugins/BenCmd/_groups.db"))) {
				plugin.log.warning("Failed to back up group database!");
			}
		} catch (IOException e) {
			plugin.log.warning("Failed to back up group database!");
		}
		saveFile();
		try {
			new File("plugins/BenCmd/_groups.db").delete();
		} catch (Exception e) { }
	}

	public void addGroup(PermissionGroup group) {
		updateGroup(group.getInternal());
	}

	public void removeGroup(PermissionGroup group) {
		BenCmd plugin = BenCmd.getPlugin();
		for (PermissionGroup group2 : getGroupGroups(group)) {
			group2.removeGroup(group);
		}
		this.remove(group.getName());
		groups.remove(group.getName());
		try {
			new File("plugins/BenCmd/_groups.db").createNewFile();
			if (!FileUtil.copy(new File("plugins/BenCmd/groups.db"), new File(
					"plugins/BenCmd/_groups.db"))) {
				plugin.log.warning("Failed to back up group database!");
			}
		} catch (IOException e) {
			plugin.log.warning("Failed to back up group database!");
		}
		saveFile();
		try {
			new File("plugins/BenCmd/_groups.db").delete();
		} catch (Exception e) { }
	}

	public void loadGroups() {
		BenCmd plugin = BenCmd.getPlugin();
		groups.clear();
		for (int i = 0; i < this.size(); i++) {
			String name = (String) this.keySet().toArray()[i];
			try {
				String value = this.getProperty(name);
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
				this.groups.put(name, new InternalGroup(plugin, name,
						permissions, users, groups, prefix, color, level));
			} catch (Exception e) {
				plugin.bLog.log(Level.WARNING, "Group " + name
						+ " failed to load:", e);
			}
		}
	}

	public void saveFile() {
		BenCmd plugin = BenCmd.getPlugin();
		File file = new File("plugins/BenCmd/groups.db"); // Prepare the file
		if (!file.exists()) {
			try {
				file.createNewFile(); // If the file doesn't exist, create it!
			} catch (IOException ex) {
				// If you can't, produce an error.
				plugin.log.severe("BenCmd had a problem:");
				ex.printStackTrace();
				return;
			}
		}
		try {
			// Save the values
			store(new FileOutputStream(file), "BenCmd User Permissions File");
		} catch (IOException ex) {
			// If you can't, produce an error.
			plugin.log.severe("BenCmd had a problem:");
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
