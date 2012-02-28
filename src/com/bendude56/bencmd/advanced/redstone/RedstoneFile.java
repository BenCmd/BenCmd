package com.bendude56.bencmd.advanced.redstone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import com.bendude56.bencmd.BenCmdFile;

public class RedstoneFile extends BenCmdFile {
	private HashMap<Location, RedstoneLever>	levers;

	public RedstoneFile() {
		super("lever.db", "--BenCmd Lever File--", true);
		levers = new HashMap<Location, RedstoneLever>();
		loadFile();
		loadAll();
	}

	public void loadAll() {
		for (int i = 0; i < getFile().size(); i++) {
			String key = (String) getFile().keySet().toArray()[i], value = getFile().getProperty(key);
			Location l = toLocation(key);
			if (value.equals("d")) {
				levers.put(l, new RedstoneLever(l, RedstoneLever.LeverType.DAY));
			} else if (value.equals("n")) {
				levers.put(l, new RedstoneLever(l, RedstoneLever.LeverType.NIGHT));
			}
		}
	}

	public void timeTick() {
		List<Location> remove = new ArrayList<Location>();
		for (RedstoneLever lever : levers.values()) {
			if (!lever.getLocation().getWorld().isChunkLoaded(lever.getLocation().getBlock().getChunk())) {
				return;
			}
			if (!lever.timeUpdate()) {
				remove.add(lever.getLocation());
			}
		}
		for (Location l : remove) {
			levers.remove(l);
		}
	}

	private Location toLocation(String s) {
		String[] splt = s.split(",");
		World w = Bukkit.getWorld(splt[0]);
		Integer x = Integer.parseInt(splt[1]);
		Integer y = Integer.parseInt(splt[2]);
		Integer z = Integer.parseInt(splt[3]);
		return new Location(w, x, y, z);
	}

	public void addLever(RedstoneLever lever) {
		levers.put(lever.getLocation(), lever);
		saveLever(lever);
	}

	public void removeLever(Location l) {
		levers.remove(l);
		getFile().remove(l.getWorld().getName() + "," + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ());
		saveFile();
	}
	
	public boolean isLever(Location l) {
		return getFile().containsKey(l.getWorld().getName() + "," + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ());
	}

	public void saveLever(RedstoneLever lever) {
		saveLever(lever, true);
	}

	public void saveLever(RedstoneLever lever, boolean saveFile) {
		Location l = lever.getLocation();
		getFile().put(l.getWorld().getName() + "," + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ(), lever.getValue());
		if (saveFile) {
			saveFile();
		}
	}

	public void saveAll() {
		for (RedstoneLever lever : levers.values()) {
			saveLever(lever);
		}
	}
}
