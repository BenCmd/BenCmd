package ben_dude56.plugins.bencmd.permissions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@SuppressWarnings("unused")
public class UserFile extends Properties {
	private static final long serialVersionUID = 0L;
	File file;
	MainPermissions mainPerm;
	static final Logger log = Logger.getLogger("minecraft");

	/**
	 * This is used to initialize a UserFile for use. Use it ONLY ONCE!
	 * 
	 * @param mainPermissions
	 *            The parent of this UserFile.
	 */
	public UserFile(MainPermissions mainPermissions) {
		mainPerm = mainPermissions; // Intialize the value of the parent
		this.reload(); // Load the values into memory.
	}

	/**
	 * This method reloads the user permissions database from the hard drive.
	 */
	public void reload() {
		file = new File("plugins/BenCmd/users.db"); // Prepare the file
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
		file = new File("plugins/BenCmd/users.db"); // Prepare the file
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
	 * This method lists all of the users in the user database.
	 * 
	 * @return This method returns a hash map of all users and their respective
	 *         permissions.
	 */
	public HashMap<String, String> listUsers() {
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
	 * This method gets a list of permissions that a user has. DO NOT USE TO
	 * CHECK FOR PERMISSIONS! Use UserFile.hasPermission() instead!
	 * 
	 * @param userName
	 *            The name of the user to retrieve permissions for.
	 * @return This method returns a HashMap of the user's permissions.
	 */
	public HashMap<String, String> getPermissions(String userName) {
		HashMap<String, String> hash = new HashMap<String, String>();
		if (!this.userExists(userName)) {
			return null; // The user doesn't exist, so return null
		}
		if (this.getProperty(userName).split("/").length < 2) {
			return null; // The permissions file is messed up, so return null.
		}
		// Add all of the permissions to the hash map.
		for (String str : this.getProperty(userName).split("/")[1].split(",")) {
			hash.put(str, userName);
		}
		return hash; // Return the permissions.
	}

	/**
	 * This method is used to retrieve the group that a user is in.
	 * 
	 * @param userName
	 *            The name of the user to retrieve the group of.
	 * @return This method returns the group that the user is in.
	 */
	public String getGroup(String userName) {
		if (!this.userExists(userName)) {
			return null; // The user doesn't exist, so return null
		}
		return this.getProperty(userName).split("/")[0]; // Return the group
															// name
	}

	/**
	 * This method checks if a user has a specific defined permission.
	 * 
	 * @param player
	 *            The player to check for permissions.
	 * @param permission
	 *            The name of the permission to check for.
	 * @return This method returns whether the player has the permission.
	 * @deprecated This method now has extra starIsTrue and testGroup arguments.
	 */
	public boolean hasPermission(String player, String permission) {
		Collection<String> userPerm; // The user's permissions
		Collection<String> groupPerm; // The user's group inheritance
										// permissions
		if (this.userExists(player)) {
			try {
				userPerm = this.getPermissions(player).keySet(); // Setup the
																	// user's
																	// permissions
				if (userPerm.contains(permission)) {
					// Check for the permission
					return true;
				}
			} catch (NullPointerException e) {
			}
			try {
				groupPerm = mainPerm.groupFile.getPermissions(
						this.getGroup(player)).keySet(); // Setup the group's
															// permissions
				if (groupPerm.contains(permission)) {
					// Check for the permission
					return true;
				}
			} catch (NullPointerException e) {
			}
		}
		return false;
	}

	/**
	 * This method checks if a user has a specific defined permission.
	 * 
	 * @param player
	 *            The player to check for permissions.
	 * @param permission
	 *            The name of the permission to check for.
	 * @param starIsTrue
	 *            Whether a * returns true.
	 * @param testGroup
	 *            Whether to test the user's group.
	 * @return This method returns whether the player has the permission.
	 */
	public boolean hasPermission(String player, String permission,
			boolean starIsTrue, boolean testGroup) {
		Collection<String> userPerm; // The user's permissions
		Collection<String> groupPerm; // The user's group inheritance
										// permissions
		if (this.userExists(player)) {
			try {
				userPerm = this.getPermissions(player).keySet(); // Setup the
																	// user's
																	// permissions
				if (userPerm.contains("isJailed") && starIsTrue) {
					return false;
				}
				if ((permission == "NoBuild" || permission == "isJailed") && userPerm.contains("isJailed")) {
					return true;
				}
				if (userPerm.contains(permission)
						|| (userPerm.contains("*") && starIsTrue)) {
					// Check for the permission
					return true;
				}
			} catch (NullPointerException e) {
			}
			if (!testGroup) {
				return false; // Return false if you don't want to test the
								// group file.
			}
			try {
				groupPerm = mainPerm.groupFile.getPermissions(
						this.getGroup(player)).keySet(); // Setup the group's
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
	 * This method adds a new user to the database.
	 * 
	 * @param player
	 *            The name of the player to add to the database
	 */
	public boolean addUser(String player) {
		if (this.userExists(player))
			return false; // The user already exists and cannot be added again!
		try {
			this.put(
					player,
					mainPerm.plugin.mainProperties.getString("defaultGroup",
							"default") + "/ /e"); // Put the new user into the
												// database
			save();
			return true; // Return success
		} catch (Exception e) {
			return false; // An unknown error was encountered!
		}
	}

	/**
	 * This method removes a user from the database.
	 * 
	 * @param player
	 *            The name of the player to remove from the database
	 */
	public boolean removeUser(String player) {
		if (!this.userExists(player))
			return false; // The user doesn't exist and cannot be removed
		try {
			this.remove(player); // Remove the user from the database
			save();
			return true; // Return success
		} catch (Exception e) {
			return false; // An unknown error was encountered!
		}
	}

	/**
	 * This method adds a new permission to a user. DANGER: This method is
	 * untested!
	 * 
	 * @param player
	 *            The player to change to the permissions of.
	 * @param permission
	 *            The permission to add.
	 */
	public PermChangeResult addPermission(String player, String permission) {
		if (!this.userExists(player))
			return PermChangeResult.DBTargetNotExist; // The user doesn't exist
		if (this.hasPermission(player, permission, false, false))
			return PermChangeResult.DBAlreadyHas; // The user already has that
													// permission
		String permissions = "";
		// Prep to rewrite the permission value
		try {
			for (Object perm : this.getPermissions(player).keySet().toArray()) {
				permissions += perm + ",";
			}
		} catch (NullPointerException ex) {
		}
		permissions += permission;
		// Rewrite the permission value
		try {
			this.put(player, this.getGroup(player) + "/" + permissions + "/"
					+ this.getProperty(player).split("/")[2]);
		} catch (IndexOutOfBoundsException e) {
			this.put(player, this.getGroup(player) + "/" + permissions + "/");
		}
		save();
		return PermChangeResult.Success; // Return success
	}

	/**
	 * This method removes a permission from a user. DANGER: This method is
	 * untested!
	 * 
	 * @param player
	 *            The player to change to the permissions of.
	 * @param permission
	 *            The permission to remove.
	 */
	public PermChangeResult removePermission(String player, String permission) {
		if (!this.userExists(player))
			return PermChangeResult.DBTargetNotExist; // The user doesn't exist
		if (!this.hasPermission(player, permission, false, false))
			return PermChangeResult.DBNotHave; // The user doesn't have that
												// permission
		String permissions = "";
		Object[] perms = this.getPermissions(player).keySet().toArray();
		// Prep to rewrite the permission value
		for (Object perm : this.getPermissions(player).keySet().toArray()) {
			if (!perm.equals(permission))
				permissions += perm + ",";
		}
		// Remove the last comma, if it exists
		if (permissions.length() > 0) {
			permissions = permissions.substring(0, permissions.length() - 1);
		}
		// Rewrite the permission value
		try {
			this.put(player, this.getGroup(player) + "/" + permissions + "/"
					+ this.getProperty(player).split("/")[2]);
		} catch (IndexOutOfBoundsException e) {
			this.put(player, this.getGroup(player) + "/" + permissions + "/");
		}
		save();
		return PermChangeResult.Success; // Return success
	}

	/**
	 * This method changes the group a user is assigned to. DANGER: This method
	 * is untested!
	 * 
	 * @param player
	 *            The name of the player to edit.
	 * @param groupName
	 *            The group to add to this player to.
	 */
	public PermChangeResult changeGroup(String player, String groupName) {
		if (!this.userExists(player))
			return PermChangeResult.DBTargetNotExist; // The user doesn't exist
		if (!mainPerm.groupFile.groupExists(groupName))
			return PermChangeResult.DBGroupNotExist; // The assigned group
														// doesn't exist
		if (this.getGroup(player).equals(groupName))
			return PermChangeResult.AlreadyInGroup; // The user is already part
													// of that group
		try {
			this.put(player,
					groupName + "/" + this.getProperty(player).split("/")[1]
							+ "/" + this.getProperty(player).split("/")[2]); // Change
			// their
			// group
			save();
			log.info(player + " has been moved into group " + groupName + ".");
			if (mainPerm.plugin.mainProperties.getString(
					"broadcastGroupChange", "false").equalsIgnoreCase("true")) {
				mainPerm.plugin.getServer().broadcastMessage(
						ChatColor.GRAY + "User " + player
								+ " has been moved into group " + groupName
								+ "!");
			}
			return PermChangeResult.Success; // Return success
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
			return PermChangeResult.MalformedPermissions; // Problem with
															// permissions file
		}
	}

	/**
	 * This function is used to see if a user has inherited permissions from a
	 * specific group.
	 * 
	 * @param user
	 *            The user to check for inheritance
	 * @param groupName
	 *            The name of the group to check for.
	 * @return This method returns whether the user inherits from that group.
	 */
	public boolean userInGroup(String user, String groupName) {
		if ((getGroup(user) == null || getGroup(user) == "")) {
			return false;
		}
		if (getGroup(user).equals(groupName)) {
			return true;
		}
		List<String> groupsChecked = new ArrayList<String>();
		String lastgroup = getGroup(user);
		while (true) {
			if (lastgroup.equals(groupName)) {
				return true;
			}
			if (groupsChecked.contains(lastgroup)) {
				log.warning("Infinite permissions inheritance found! Returning false...");
				return false;
			}
			groupsChecked.add(lastgroup);
			if (mainPerm.groupFile.getGroup(lastgroup) != null
					&& mainPerm.groupFile.getGroup(lastgroup) != "") {
				lastgroup = mainPerm.groupFile.getGroup(lastgroup);
			} else {
				break;
			}
		}
		return false;
	}

	/**
	 * This method is used to check whether a player exists in the user
	 * database.
	 * 
	 * @param user
	 *            The name of a user to check for.
	 * @return This method returns whether the user exists.
	 */
	public boolean userExists(String user) {
		return this.containsKey(user); // Return whether the player's name is in
										// the database.
	}

	public ChatColor getColor(String user) {
		int clrint;
		try {
			clrint = Integer.parseInt(this.getProperty(user).split("/")[2], 10);
		} catch (IndexOutOfBoundsException e) {
			return ChatColor.YELLOW;
		} catch (NumberFormatException e) {
			return ChatColor.YELLOW;
		}
		try {
			return ChatColor.getByCode(clrint);
		} catch (Exception e) {
			return ChatColor.YELLOW;
		}
	}
}
