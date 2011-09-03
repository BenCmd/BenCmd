package com.bendude56.bencmd.permissions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.bukkit.Material;

public class ItemBW extends Properties {
	private static final long serialVersionUID = 0L;
	File file;
	MainPermissions mainPerm;

	/**
	 * This is used to initialize a GroupFile for use. Use it ONLY ONCE!
	 * 
	 * @param mainPermissions
	 *            The parent of this GroupFile.
	 */
	public ItemBW(MainPermissions mainPermissions) {
		mainPerm = mainPermissions; // Intialize the value of the parent
		this.reload(); // Load the values into memory.
	}

	/**
	 * This method reloads the item database from the hard drive.
	 */
	public void reload() {
		file = new File("plugins/BenCmd/itembw.db"); // Prepare the file
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

	/**
	 * This method saves all of the items.
	 */
	public void save() {
		file = new File("plugins/BenCmd/itembw.db"); // Prepare the file
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
			store(new FileOutputStream(file), "BenCmd User Permissions File"); // Save
																				// the
																				// values
		} catch (IOException ex) {
			// If you can't, produce an error.
			mainPerm.plugin.log.severe("BenCmd had a problem:");
			ex.printStackTrace();
		}
	}

	public List<Material> getListed(String group) {
		if (!groupExists(group) && !addGroup(group)) {
			mainPerm.plugin.bLog
					.warning("Failed to retrieve item blacklist/whitelist for group "
							+ group + "!");
			mainPerm.plugin.log
					.warning(group
							+ " was not in plugins/BenCmd/itemsbw.db and couldn't be added due to an unknown error! Returning null...");
			return null;
		}
		List<Material> matList = new ArrayList<Material>();
		try {
			for (String material : this.getProperty(group).split("/")[1]
					.split(",")) {
				Material mat;
				try {
					mat = Material.getMaterial(Integer.parseInt(material));
				} catch (NumberFormatException e) {
					mainPerm.plugin.bLog.warning(material + " in group "
							+ group + " in itembw.db is NaN!");
					mainPerm.plugin.log
							.warning("Cannot get a number from input: "
									+ material
									+ " in plugins/BenCmd/itemsbw.db (Entry: "
									+ group + ")! Skipping...");
					continue;
				}
				matList.add(mat);
			}
		} catch (IndexOutOfBoundsException e) {
		}
		return matList;
	}

	public boolean canSpawn(Material mat, String group) {
		boolean returnValue = false;
		switch (getSetting(group)) {
		case BWUnknown:
			mainPerm.plugin.bLog
					.warning("Unknown blacklist/whitelist setting for group "
							+ group + "! Assuming blacklist...");
		case BWBlack:
			try {
				returnValue = !(getListed(group).contains(mat));
			} catch (NullPointerException e) {
				returnValue = true;
			}
			break;
		case BWWhite:
			try {
				returnValue = getListed(group).contains(mat);
			} catch (NullPointerException e) {
				returnValue = false;
			}
			break;
		case BWNoRestriction:
			mainPerm.plugin.bLog.info("Group " + group
					+ " is using a deprecated blacklist/whitelist setting");
			returnValue = true;
			break;
		default:
			assert false : "Unknown blacklist/whitelist setting!";
			break;
		}
		return returnValue;
	}

	public BWSetting getSetting(String group) {
		if (!groupExists(group) && !addGroup(group)) {
			mainPerm.plugin.bLog
					.warning("Failed to retrieve item blacklist/whitelist setting for group "
							+ group + "!");
			mainPerm.plugin.log
					.warning(group
							+ " was not in plugins/BenCmd/itemsbw.db and couldn't be added due to an unknown error! Returning BWUnknown...");
			return BWSetting.BWUnknown;
		}
		String set = this.getProperty(group).split("/")[0];
		if (set.equalsIgnoreCase("b")) {
			return BWSetting.BWBlack;
		} else if (set.equalsIgnoreCase("w")) {
			return BWSetting.BWWhite;
		} else if (set.equalsIgnoreCase("nr")) {
			return BWSetting.BWNoRestriction;
		} else {
			mainPerm.plugin.bLog.warning("Group " + group
					+ " has an unknown blacklist/whitelist setting! (" + set
					+ ")");
			mainPerm.plugin.log.warning("Cannot get a BWSetting from input: "
					+ set + " in plugins/BenCmd/itemsbw.db (Entry: " + group
					+ ")! Returning BWUnknown...");
			return BWSetting.BWUnknown;
		}
	}

	public boolean addGroup(String group) {
		if (this.groupExists(group))
			return false; // The group already exists and cannot be added again!
		try {
			this.put(
					group,
					mainPerm.plugin.mainProperties.getString(
							"defaultItemAction", "b") + "/"); // Put the new
																// group into
																// the
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
		return true;
	}

	public boolean groupExists(String group) {
		boolean exists = this.containsKey(group);
		return exists; // Return whether the group's name is in
						// the database.
	}
}
