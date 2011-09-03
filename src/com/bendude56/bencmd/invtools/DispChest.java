package com.bendude56.bencmd.invtools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.bukkit.Location;

public class DispChest extends Properties {
	private static final long serialVersionUID = 0L;
	private String proFile;

	public DispChest(String propertiesFile) {
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

	public boolean isDisposalChest(Location loc) {
		String property = loc.getBlockX() + "," + loc.getBlockY() + ","
				+ loc.getBlockZ() + "," + loc.getWorld().getName();
		return this.containsKey(property);
	}

	public boolean isDisposalChest(int x, int y, int z, String world) {
		String property = x + "," + y + "," + z + "," + world;
		return this.containsKey(property);
	}

	public void addChest(Location loc) {
		String property = loc.getBlockX() + "," + loc.getBlockY() + ","
				+ loc.getBlockZ() + "," + loc.getWorld().getName();
		this.put(property, "");
		saveFile("BenCmd disposal chest list");
	}

	public void addChest(int x, int y, int z, String world) {
		String property = x + "," + y + "," + z + "," + world;
		this.put(property, "");
		saveFile("BenCmd disposal chest list");
	}

	public void removeDispenser(Location loc) {
		String property = loc.getBlockX() + "," + loc.getBlockY() + ","
				+ loc.getBlockZ() + "," + loc.getWorld().getName();
		this.remove(property);
		saveFile("BenCmd disposal chest list");
	}

	public void removeDispenser(int x, int y, int z, String world) {
		String property = x + "," + y + "," + z + "," + world;
		this.remove(property);
		saveFile("BenCmd disposal chest list");
	}
}
