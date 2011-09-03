package com.bendude56.bencmd.invtools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.bukkit.Location;
import org.bukkit.Material;

public class UnlimitedDisp extends Properties {
	private static final long serialVersionUID = 0L;
	private String proFile;

	public UnlimitedDisp(String propertiesFile) {
		proFile = propertiesFile;
		loadFile();
	}

	public void loadFile() {
		File file = new File(proFile);
		if (file.exists()) {
			try {
				load(new FileInputStream(file));
			} catch (IOException e) {
				System.out.println("BenCmd had a problem:");
				e.printStackTrace();
			}
		}
	}

	public void saveFile(String header) {
		File file = new File(proFile);
		if (file.exists()) {
			try {
				store(new FileOutputStream(file), header);
			} catch (IOException e) {
				System.out.println("BenCmd had a problem:");
				e.printStackTrace();
			}
		}
	}

	public boolean isUnlimitedDispenser(Location loc) {
		String property = loc.getBlockX() + "," + loc.getBlockY() + ","
				+ loc.getBlockZ() + "," + loc.getWorld().getName();
		return this.containsKey(property);
	}

	public boolean isUnlimitedDispenser(int x, int y, int z, String world) {
		String property = x + "," + y + "," + z + "," + world;
		return this.containsKey(property);
	}

	public BCItem getDispensedItem(Location loc) {
		try {
			String property = this.getProperty(loc.getBlockX() + ","
					+ loc.getBlockY() + "," + loc.getBlockZ() + ","
					+ loc.getWorld().getName());
			return new BCItem(Material.getMaterial(Integer.parseInt(property
					.split(":")[0])), Integer.parseInt(property.split(":")[1]));
		} catch (NumberFormatException e) {
			return null;
		}
	}

	public Material getDispensedItem(int x, int y, int z, String world) {
		try {
			String property = this.getProperty(x + "," + y + "," + z + ","
					+ world);
			return Material.getMaterial(Integer.parseInt(property));
		} catch (NumberFormatException e) {
			return null;
		}
	}

	public void addDispenser(Location loc, String id) {
		String property = loc.getBlockX() + "," + loc.getBlockY() + ","
				+ loc.getBlockZ() + "," + loc.getWorld().getName();
		this.put(property, id);
		saveFile("BenCmd unlimited dispenser list");
	}

	public void addDispenser(int x, int y, int z, String world, String id) {
		String property = x + "," + y + "," + z + "," + world;
		this.put(property, id);
		saveFile("BenCmd unlimited dispenser list");
	}

	public void removeDispenser(Location loc) {
		String property = loc.getBlockX() + "," + loc.getBlockY() + ","
				+ loc.getBlockZ() + "," + loc.getWorld().getName();
		this.remove(property);
		saveFile("BenCmd unlimited dispenser list");
	}

	public void removeDispenser(int x, int y, int z, String world) {
		String property = x + "," + y + "," + z + "," + world;
		this.remove(property);
		saveFile("BenCmd unlimited dispenser list");
	}
}
