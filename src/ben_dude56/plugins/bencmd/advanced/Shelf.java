package ben_dude56.plugins.bencmd.advanced;

import org.bukkit.Location;

public class Shelf {
	private Location loc;
	private String value;

	public Shelf(Location location, String text) {
		loc = location;
		value = text;
	}

	public String getText() {
		return value;
	}
	
	public Location getLocation() {
		return loc;
	}
}
