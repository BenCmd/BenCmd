package ben_dude56.plugins.bencmd.permissions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Logger;

import org.bukkit.ChatColor;

public class GroupFile extends Properties {
	private static final long serialVersionUID = 0L;
	File file;
	MainPermissions mainPerm;
	static final Logger log = Logger.getLogger("minecraft");

	/**
	 * This is used to initialize a GroupFile for use. Use it ONLY ONCE!
	 * 
	 * @param mainPermissions
	 *            The parent of this GroupFile.
	 */
	public GroupFile(MainPermissions mainPermissions) {
		mainPerm = mainPermissions; // Initialize the value of the parent
		this.reload(); // Load the values into memory.
	}

	/**
	 * This method reloads the user permissions database from the hard drive.
	 */
	public void reload() {
		file = new File("plugins/BenCmd/groups.db"); // Prepare the file
		if (!file.exists()) {
			try {
				file.createNewFile(); // If the file doesn't exist, create it!
			} catch (IOException ex) {
				// If you can't, produce an error.
				log.severe("BenCmd had a problem:");
				ex.printStackTrace();
				return;
			}
		}
		try {
			load(new FileInputStream(file)); // Load the values
		} catch (IOException ex) {
			// If you can't, produce an error.
			log.severe("BenCmd had a problem:");
			ex.printStackTrace();
		}
	}

	/**
	 * This method saves all of the user permissions.
	 */
	public void save() {
		file = new File("plugins/BenCmd/groups.db"); // Prepare the file
		if (!file.exists()) {
			try {
				file.createNewFile(); // If the file doesn't exist, create it!
			} catch (IOException ex) {
				// If you can't, produce an error.
				log.severe("BenCmd had a problem:");
				ex.printStackTrace();
				return;
			}
		}
		try {
			store(new FileOutputStream(file), "BenCmd User Permissions File"); // Save
																				// the
																				// values
		} catch (IOException ex) {
			// If you can't, produce an error.
			log.severe("BenCmd had a problem:");
			ex.printStackTrace();
		}
	}

	/**
	 * This method gets a list of permissions that a group has. DO NOT USE TO
	 * CHECK FOR PERMISSIONS! Use GroupFile.hasPermission() instead!
	 * 
	 * @param groupName
	 *            The name of the group to retrieve permissions for.
	 * @return This method returns a HashMap of the group's permissions.
	 */
	public HashMap<String, String> getPermissions(String groupName) {
		HashMap<String, String> hash = new HashMap<String, String>();
		if (!this.groupExists(groupName)) {
			return null; // The user doesn't exist, so return null
		}
		if (this.getProperty(groupName).split("/").length < 2) {
			return null; // The permissions file is messed up, so return null.
		}
		// Add all of the permissions to the hash map.
		String[] permissions = this.getProperty(groupName).split("/");
		for (String str : permissions[1].split(",")) {
			hash.put(str, groupName);
		}
		// Add inherited permissions to the hash map.
		if (this.getGroup(groupName) != null) {
			try {
				for (String str : this.getPermissions(getGroup(groupName))
						.keySet()) {
					hash.put(str, getGroup(groupName));
				}
			} catch (NullPointerException ex) {
			}
		}
		return hash; // Return the permissions.
	}

	/**
	 * This method checks if a group has a specific defined permission.
	 * 
	 * @param group
	 *            The group to check for permissions.
	 * @param permission
	 *            The name of the permission to check for.
	 * @return This method returns whether the group has the permission.
	 * @deprecated This method now has extra starIsTrue and testGroup arguments.
	 */
	public boolean hasPermission(String group, String permission) {
		Collection<String> groupPerm; // The group's permissions
		if (this.groupExists(group)) {
			try {
				groupPerm = mainPerm.groupFile.getPermissions(
						this.getGroup(group)).keySet(); // Setup the group's
														// permissions
				if (groupPerm.contains(permission) || groupPerm.contains("*")) {
					// Check for the permission
					return true;
				}
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * This method checks if a group has a specific defined permission.
	 * 
	 * @param group
	 *            The group to check for permissions.
	 * @param permission
	 *            The name of the permission to check for.
	 * @param starIsTrue
	 *            Whether a * returns true.
	 * @param testGroup
	 *            Whether to test the group's inheritance tree.
	 * @return This method returns whether the group has the permission.
	 */
	public boolean hasPermission(String group, String permission,
			boolean starIsTrue, boolean testGroup) {
		Collection<String> groupPerm; // The group's permissions
		try {
			groupPerm = mainPerm.groupFile.getPermissions(group).keySet(); // Setup
																			// the
																			// group's
																			// permissions
			if (groupPerm.contains(permission)
					|| (groupPerm.contains("*") && starIsTrue)) {
				// Check for the permission
				return true;
			}
		} catch (NullPointerException e) {
		}
		if (this.getGroup(group) != null
				&& this.groupExists(this.getGroup(group)) && testGroup) {
			try {
				groupPerm = mainPerm.groupFile.getPermissions(
						this.getGroup(group)).keySet(); // Setup the group's
														// permissions
				if (groupPerm.contains(permission)
						|| (groupPerm.contains("*") && starIsTrue)) {
					// Check for the permission
					return true;
				}
			} catch (NullPointerException e) {
			}
		}
		return false;
	}

	/**
	 * This method adds a new group to the database.
	 * 
	 * @param group
	 *            The name of the group to add to the database
	 */
	public boolean addGroup(String group) {
		if (this.groupExists(group))
			return false; // The group already exists and cannot be added again!
		try {
			this.put(
					group,
					mainPerm.plugin.mainProperties.getString("defaultGroup",
							"default") + "///15"); // Put the new group into the
													// database
			save();
			return true; // Return success
		} catch (Exception e) {
			return false; // An unknown error was encountered!
		}
	}

	public boolean removeGroup(String group) {
		if (!this.groupExists(group))
			return false;
		try {
			this.remove(group);
		} catch (Exception ex) {
			return false;
		}
		for (String str : this.listGroups().keySet()) {
			try {
				if (this.getGroup(str).equals(group)) {
					this.changeGroup(str, mainPerm.plugin.mainProperties
							.getString("defaultGroup", "default"));
				}
			} catch (Exception ex) {
			}
		}
		for (String str : mainPerm.userFile.listUsers().values()) {
			try {
				if (mainPerm.userFile.getGroup(str).equals(group)) {
					mainPerm.userFile.changeGroup(str,
							mainPerm.plugin.mainProperties.getString(
									"defaultGroup", "default"));
				}
			} catch (Exception ex) {
			}
		}
		return true;
	}

	/**
	 * This method adds a new permission to a group. DANGER: This method is
	 * untested!
	 * 
	 * @param group
	 *            The group to change to the permissions of.
	 * @param permission
	 *            The permission to add.
	 */
	public PermChangeResult addPermission(String group, String permission) {
		if (!this.groupExists(group))
			return PermChangeResult.DBTargetNotExist; // The group doesn't exist
		if (this.hasPermission(group, permission, false, false))
			return PermChangeResult.DBAlreadyHas; // The group already has that
													// permission
		String permissions = "";
		// Prep to rewrite the permission value
		try {
			for (Object perm : this.getPermissions(group).keySet().toArray()) {
				permissions += perm + ",";
			}
		} catch (NullPointerException e) {
		}
		permissions += permission;
		// Rewrite the permission value
		this.put(
				group,
				this.getGroup(group) + "/" + permissions + "/"
						+ this.getPrefix(group) + "/"
						+ this.getColor(group).getCode());
		save();
		return PermChangeResult.Success; // Return success
	}

	/**
	 * This method removes a permission from a group. DANGER: This method is
	 * untested!
	 * 
	 * @param group
	 *            The group to change to the permissions of.
	 * @param permission
	 *            The permission to remove.
	 */
	public PermChangeResult removePermission(String group, String permission) {
		if (!this.groupExists(group))
			return PermChangeResult.DBTargetNotExist; // The group doesn't exist
		if (!this.hasPermission(group, permission, false, false))
			return PermChangeResult.DBNotHave; // The group doesn't have that
												// permission
		String permissions = "";
		// Prep to rewrite the permission value
		for (Object perm : this.getPermissions(group).keySet().toArray()) {
			if (!perm.equals(permission))
				permissions += perm + ",";
		}
		// Remove the last comma, if it exists
		if (permissions.length() > 0) {
			permissions = permissions.substring(0, permissions.length() - 1);
		}
		// Rewrite the permission value
		this.put(
				group,
				this.getGroup(group) + "/" + permissions + "/"
						+ this.getPrefix(group) + "/"
						+ this.getColor(group).getCode());
		save();
		return PermChangeResult.Success; // Return success
	}

	/**
	 * This method changes the group a group is assigned to. DANGER: This method
	 * is untested!
	 * 
	 * @param group
	 *            The name of the group to edit.
	 * @param groupName
	 *            The group to add to this group to.
	 */
	public PermChangeResult changeGroup(String group, String groupName) {
		if (!this.groupExists(group))
			return PermChangeResult.DBTargetNotExist; // The group doesn't exist
		if (!this.groupExists(groupName))
			return PermChangeResult.DBGroupNotExist; // The assigned group
														// doesn't exist
		if (this.getGroup(group).equals(groupName))
			return PermChangeResult.AlreadyInGroup; // The group is already part
													// of that group
		try {
			this.put(group,
					groupName + "/" + this.getProperty(group).split("/")[1]
							+ "/" + this.getPrefix(group) + "/"
							+ this.getColor(group).getCode());
			save();
			return PermChangeResult.Success; // Return success
		} catch (IndexOutOfBoundsException e) {
			return PermChangeResult.MalformedPermissions; // Problem with
															// permissions file
		}
	}

	/**
	 * This method lists all of the groups in the group database.
	 * 
	 * @return This method returns a hash map of all groups and their respective
	 *         permissions.
	 */
	public HashMap<String, String> listGroups() {
		HashMap<String, String> hash = new HashMap<String, String>();
		int i = 0;
		while (i < this.keySet().size()) {
			try {
				hash.put((String) this.keySet().toArray()[i], (String) this
						.values().toArray()[i]);
			} catch (NullPointerException ex) {
				try {
					hash.put((String) this.keySet().toArray()[i], "");
				} catch (NullPointerException e) {
				}
			} catch (Exception ex) {
			}
			i++;
		}
		return hash;
	}

	/**
	 * This method is used to retrieve the group that a group is in.
	 * 
	 * @param groupName
	 *            The name of the group to retrieve the group of.
	 * @return This method returns the group that the group is in.
	 */
	public String getGroup(String groupName) {
		if (!this.groupExists(groupName)) {
			return null; // The group doesn't exist, so return null
		}
		try {
			return this.getProperty(groupName).split("/")[0]; // Return the
																// group
																// name
		} catch (IndexOutOfBoundsException e) {
			return "";
		}
	}

	/**
	 * This method is used to check whether a group exists in the group
	 * database.
	 * 
	 * @param group
	 *            The name of a group to check for.
	 * @return This method returns whether the group exists.
	 */
	public boolean groupExists(String group) {
		boolean exists = this.containsKey(group);
		return exists; // Return whether the group's name is in
						// the database.
	}

	public String getPrefix(String group) {
		if (!groupExists(group)) {
			return "";
		}
		String prefix;
		try {
			prefix = this.getProperty(group).split("/")[2];
		} catch (IndexOutOfBoundsException e) {
			prefix = "";
		}
		return prefix;
	}

	public ChatColor getColor(String group) {
		int clrint;
		try {
			clrint = Integer
					.parseInt(this.getProperty(group).split("/")[3], 10);
		} catch (IndexOutOfBoundsException e) {
			return ChatColor.WHITE;
		} catch (NumberFormatException e) {
			return ChatColor.WHITE;
		}
		try {
			return ChatColor.getByCode(clrint);
		} catch (Exception e) {
			return ChatColor.WHITE;
		}
	}
}
