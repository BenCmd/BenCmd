package ben_dude56.plugins.bencmd.advanced;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import org.bukkit.Location;
import org.bukkit.World;

import ben_dude56.plugins.bencmd.BenCmd;

public class ShelfFile extends Properties {

	private static final long serialVersionUID = 0L;
	private BenCmd plugin;
	private String fileName;
	private HashMap<Location, Shelf> shelves;

	public ShelfFile(BenCmd instance, String file) {
		plugin = instance;
		fileName = file;
		shelves = new HashMap<Location, Shelf>();
		loadFile();
		loadList();
	}

	public void loadFile() {
		File file = new File(fileName);
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
		File file = new File(fileName);
		if (file.exists()) {
			try {
				store(new FileOutputStream(file), "-BenCmd Shelf List-");
			} catch (IOException e) {
				System.out.println("BenCmd had a problem:");
				e.printStackTrace();
			}
		}
	}

	public void loadList() {
		shelves.clear();
		for (int i = 0; i < this.size(); i++) {
			String key = (String) this.keySet().toArray()[i];
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
				plugin.bLog.warning("A shelf location was discovered to be invalid...");
				plugin.log.warning("Shelf (" + this.keySet().toArray()[i]
						+ ")'s location is invalid!");
				continue;
			} catch (IndexOutOfBoundsException e) {
				plugin.bLog.warning("A shelf location was discovered to be invalid...");
				plugin.log.warning("Shelf (" + this.keySet().toArray()[i]
						+ ")'s location is invalid!");
				continue;
			}
			if ((world = plugin.getServer().getWorld(key.split(",")[0])) == null) {
				plugin.bLog.warning("A shelf location was discovered to be invalid...");
				plugin.log.warning("Shelf (" + this.keySet().toArray()[i]
						+ ")'s location is invalid!");
				continue;
			}
			location = new Location(world, x, y, z);
			String text = this.getProperty(key);
			shelves.put(location, new Shelf(location, text));
		}
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
		this.put(
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
			this.remove(loc.getWorld().getName() + "," + loc.getBlockX() + ","
					+ loc.getBlockY() + "," + loc.getBlockZ());
			saveFile();
		}
	}
}
