package com.bendude56.bencmd.invtools;

import org.bukkit.Location;
import org.bukkit.Material;

import com.bendude56.bencmd.BenCmdFile;

public class UnlimitedDisp extends BenCmdFile {

	public UnlimitedDisp() {
		super("disp.db", "--BenCmd Unlimited Dispenser File--", false);
		loadFile();
	}

	public boolean isUnlimitedDispenser(Location loc) {
		String property = loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + "," + loc.getWorld().getName();
		return getFile().containsKey(property);
	}

	public boolean isUnlimitedDispenser(int x, int y, int z, String world) {
		String property = x + "," + y + "," + z + "," + world;
		return getFile().containsKey(property);
	}

	public BCItem getDispensedItem(Location loc) {
		try {
			String property = getFile().getProperty(loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + "," + loc.getWorld().getName());
			return new BCItem(Material.getMaterial(Integer.parseInt(property.split(":")[0])), Integer.parseInt(property.split(":")[1]));
		} catch (NumberFormatException e) {
			return null;
		}
	}

	public Material getDispensedItem(int x, int y, int z, String world) {
		try {
			String property = getFile().getProperty(x + "," + y + "," + z + "," + world);
			return Material.getMaterial(Integer.parseInt(property));
		} catch (NumberFormatException e) {
			return null;
		}
	}

	public void addDispenser(Location loc, String id) {
		String property = loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + "," + loc.getWorld().getName();
		getFile().put(property, id);
		saveFile();
	}

	public void addDispenser(int x, int y, int z, String world, String id) {
		String property = x + "," + y + "," + z + "," + world;
		getFile().put(property, id);
		saveFile();
	}

	public void removeDispenser(Location loc) {
		String property = loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + "," + loc.getWorld().getName();
		getFile().remove(property);
		saveFile();
	}

	public void removeDispenser(int x, int y, int z, String world) {
		String property = x + "," + y + "," + z + "," + world;
		getFile().remove(property);
		saveFile();
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
