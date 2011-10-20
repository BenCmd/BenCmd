package com.bendude56.bencmd.advanced;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.BenCmdFile;


public class ShelfFile extends BenCmdFile {
	private HashMap<Location, Shelf> shelves;

	public ShelfFile() {
		super("shelves.db", "--BenCmd Shelf File--", true);
		shelves = new HashMap<Location, Shelf>();
		loadFile();
		loadAll();
	}

	public void loadAll() {
		shelves.clear();
		for (int i = 0; i < getFile().size(); i++) {
			String key = (String) getFile().keySet().toArray()[i];
			int x;
			int y;
			int z;
			World world;
			Location location;
			try {
				x = Integer.parseInt(key.split(",")[1]);
				y = Integer.parseInt(key.split(",")[2]);
				z = Integer.parseInt(key.split(",")[3]);
			} catch (NumberFormatException e) {
				BenCmd.log(Level.WARNING, "Shelf (" + getFile().keySet().toArray()[i]
						+ ")'s location is invalid!");
				continue;
			} catch (IndexOutOfBoundsException e) {
				BenCmd.log(Level.WARNING, "Shelf (" + getFile().keySet().toArray()[i]
				                                           						+ ")'s location is invalid!");
				continue;
			}
			if ((world = Bukkit.getWorld(key.split(",")[0])) == null) {
				BenCmd.log(Level.WARNING, "Shelf (" + getFile().keySet().toArray()[i]
				                                           						+ ")'s location is invalid!");
				continue;
			}
			location = new Location(world, x, y, z);
			String text = getFile().getProperty(key);
			shelves.put(location, new Shelf(location, text));
		}
	}
	
	public void saveAll() {
		for (Map.Entry<Location, Shelf> e : shelves.entrySet()) {
			Location loc = e.getKey();
			Shelf s = e.getValue();
			getFile().put(
					loc.getWorld().getName() + "," + loc.getBlockX() + ","
							+ loc.getBlockY() + "," + loc.getBlockZ(),
					s.getText());
		}
		saveFile();
	}

	public Shelf getShelf(Location loc) {
		for (int i = 0; i < shelves.size(); i++) {
			Location key = (Location) shelves.keySet().toArray()[i];
			if (key.getBlockX() == loc.getBlockX()
					&& key.getBlockY() == loc.getBlockY()
					&& key.getBlockZ() == loc.getBlockZ()
					&& key.getWorld().getName()
							.equals(loc.getWorld().getName())) {
				return shelves.get(key);
			}
		}
		return null;
	}

	public void addShelf(Shelf shelf) {
		shelves.put(shelf.getLocation(), shelf);
		Location loc = shelf.getLocation();
		getFile().put(
				loc.getWorld().getName() + "," + loc.getBlockX() + ","
						+ loc.getBlockY() + "," + loc.getBlockZ(),
				shelf.getText());
		saveFile();
	}

	public void remShelf(Location loc) {
		Shelf shelf = null;
		for (int i = 0; i < shelves.size(); i++) {
			Location key = (Location) shelves.keySet().toArray()[i];
			if (key.getBlockX() == loc.getBlockX()
					&& key.getBlockY() == loc.getBlockY()
					&& key.getBlockZ() == loc.getBlockZ()
					&& key.getWorld().getName()
							.equals(loc.getWorld().getName())) {
				shelf = shelves.get(key);
				break;
			}
		}
		if (shelf != null) {
			shelves.remove(shelf.getLocation());
			getFile().remove(loc.getWorld().getName() + "," + loc.getBlockX() + ","
					+ loc.getBlockY() + "," + loc.getBlockZ());
			saveFile();
		}
	}
}
