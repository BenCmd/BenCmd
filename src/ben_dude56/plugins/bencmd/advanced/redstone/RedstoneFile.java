package ben_dude56.plugins.bencmd.advanced.redstone;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.bukkit.Location;
import org.bukkit.World;

import ben_dude56.plugins.bencmd.BenCmd;

public class RedstoneFile extends Properties {
	private static final long serialVersionUID = 0L;

	private String filename;
	private HashMap<Location, RedstoneLever> levers;
	private BenCmd plugin;

	public RedstoneFile(BenCmd instance, String file) {
		plugin = instance;
		filename = file;
		levers = new HashMap<Location, RedstoneLever>();
		loadFile();
		loadLevers();
	}

	public void loadFile() {
		File file = new File(filename);
		if (file.exists()) {
			try {
				load(new FileInputStream(file));
			} catch (IOException e) {
				System.out.println("BenCmd had a problem:");
				e.printStackTrace();
			}
		}
	}

	public void saveFile() {
		File file = new File(filename);
		if (file.exists()) {
			try {
				store(new FileOutputStream(file), "-BenCmd Lever List-");
			} catch (IOException e) {
				System.out.println("BenCmd had a problem:");
				e.printStackTrace();
			}
		}
	}

	public void loadLevers() {
		for (int i = 0; i < this.size(); i++) {
			String key = (String) this.keySet().toArray()[i], value = this
					.getProperty(key);
			Location l = toLocation(key);
			if (value.equals("d")) {
				levers.put(l, new RedstoneLever(l, RedstoneLever.LeverType.DAY));
			} else if (value.equals("n")) {
				levers.put(l, new RedstoneLever(l,
						RedstoneLever.LeverType.NIGHT));
			}
		}
	}

	public void timeTick() {
		List<Location> remove = new ArrayList<Location>();
		for (RedstoneLever lever : levers.values()) {
			if (!lever.getLocation().getWorld()
					.isChunkLoaded(lever.getLocation().getBlock().getChunk())) {
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
		World w = plugin.getServer().getWorld(splt[0]);
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
		this.remove(l.getWorld().getName() + "," + l.getBlockX() + ","
				+ l.getBlockY() + "," + l.getBlockZ());
		saveFile();
	}

	public void saveLever(RedstoneLever lever) {
		Location l = lever.getLocation();
		this.put(
				l.getWorld().getName() + "," + l.getBlockX() + ","
						+ l.getBlockY() + "," + l.getBlockZ(), lever.getValue());
		saveFile();
	}

	public void saveAll() {
		for (RedstoneLever lever : levers.values()) {
			saveLever(lever);
		}
	}
}
