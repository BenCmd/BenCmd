package com.bendude56.bencmd.permissions;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Material;

import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.BenCmdFile;

public class ItemBW extends BenCmdFile {

	/**
	 * This is used to initialize a GroupFile for use. Use it ONLY ONCE!
	 * 
	 * @param mainPermissions
	 *            The parent of this GroupFile.
	 */
	public ItemBW() {
		super("itembw.db", "--BenCmd Item B/W File--", false);
		this.loadFile(); // Load the values into memory.
	}

	public List<Material> getListed(String group) {
		if (!groupExists(group) && !addGroup(group)) {
			BenCmd.log(Level.SEVERE, group
							+ " was not in plugins/BenCmd/itemsbw.db and couldn't be added due to an unknown error! Returning null...");
			return null;
		}
		List<Material> matList = new ArrayList<Material>();
		try {
			for (String material : getFile().getProperty(group).split("/")[1]
					.split(",")) {
				Material mat;
				try {
					mat = Material.getMaterial(Integer.parseInt(material));
				} catch (NumberFormatException e) {
					BenCmd.log(Level.SEVERE, "Cannot get a number from input: "
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
			BenCmd.log(Level.WARNING, "Group " + group
					+ " is using a deprecated blacklist/whitelist setting");
			returnValue = true;
			break;
		default:
			throw new AssertionError("Unknown blacklist/whitelist setting!");
		}
		return returnValue;
	}

	public BWSetting getSetting(String group) {
		if (!groupExists(group) && !addGroup(group)) {
			BenCmd.log(Level.SEVERE, group
							+ " was not in plugins/BenCmd/itemsbw.db and couldn't be added due to an unknown error! Returning BWUnknown...");
			return BWSetting.BWUnknown;
		}
		String set = getFile().getProperty(group).split("/")[0];
		if (set.equalsIgnoreCase("b")) {
			return BWSetting.BWBlack;
		} else if (set.equalsIgnoreCase("w")) {
			return BWSetting.BWWhite;
		} else if (set.equalsIgnoreCase("nr")) {
			return BWSetting.BWNoRestriction;
		} else {
			BenCmd.log(Level.WARNING, "Cannot get a BWSetting from input: "
					+ set + " in plugins/BenCmd/itemsbw.db (Entry: " + group
					+ ")! Returning BWUnknown...");
			return BWSetting.BWUnknown;
		}
	}

	public boolean addGroup(String group) {
		if (this.groupExists(group))
			return false; // The group already exists and cannot be added again!
		try {
			getFile().put(
					group,
					BenCmd.getMainProperties().getString(
							"defaultItemAction", "b") + "/");
			saveFile();
			return true; // Return success
		} catch (Exception e) {
			return false; // An unknown error was encountered!
		}
	}

	public boolean removeGroup(String group) {
		if (!this.groupExists(group))
			return false;
		try {
			getFile().remove(group);
			saveFile();
		} catch (Exception ex) {
			return false;
		}
		return true;
	}

	public boolean groupExists(String group) {
		boolean exists = getFile().containsKey(group);
		return exists;
	}

	@Override
	public void saveAll() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void loadAll() {
		throw new UnsupportedOperationException();
	}
}
