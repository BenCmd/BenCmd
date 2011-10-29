package com.bendude56.bencmd.invtools;

import org.bukkit.Location;

import com.bendude56.bencmd.BenCmdFile;

public class DispChest extends BenCmdFile {

	public DispChest() {
		super("chest.db", "--BenCmd Disposal Chest File--", false);
		loadFile();
	}

	public boolean isDisposalChest(Location loc) {
		String property = loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + "," + loc.getWorld().getName();
		return getFile().containsKey(property);
	}

	public boolean isDisposalChest(int x, int y, int z, String world) {
		String property = x + "," + y + "," + z + "," + world;
		return getFile().containsKey(property);
	}

	public void addChest(Location loc) {
		String property = loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + "," + loc.getWorld().getName();
		getFile().put(property, "");
		saveFile();
	}

	public void addChest(int x, int y, int z, String world) {
		String property = x + "," + y + "," + z + "," + world;
		getFile().put(property, "");
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
