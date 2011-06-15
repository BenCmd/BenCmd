package ben_dude56.plugins.bencmd.multiworld;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import org.bukkit.Location;
import org.bukkit.World;

import ben_dude56.plugins.bencmd.BenCmd;
import ben_dude56.plugins.bencmd.permissions.PermissionGroup;
import ben_dude56.plugins.bencmd.warps.Warp;

public class PortalFile extends Properties {
	private static final long serialVersionUID = 0L;
	private BenCmd plugin;
	private HashMap<Location, Portal> portals;
	private String fileName;

	public PortalFile(BenCmd instance, String fileName) {
		plugin = instance;
		this.fileName = fileName;
		portals = new HashMap<Location, Portal>();
		loadFile();
		loadPortals();
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
				store(new FileOutputStream(file), "-BenCmd Portal List-");
			} catch (IOException e) {
				System.out.println("BenCmd had a problem:");
				e.printStackTrace();
			}
		}
	}

	public void loadPortals() {
		portals.clear();
		for (int i = 0; i < this.size(); i++) {
			String key = (String) this.keySet().toArray()[i];
			int x;
			int y;
			int z;
			World world;
			Location location;
			Warp warp = null;
			PermissionGroup group;
			Integer homeNum = null;
			try {
				x = Integer.parseInt(key.split(",")[1]);
				y = Integer.parseInt(key.split(",")[2]);
				z = Integer.parseInt(key.split(",")[3]);
			} catch (NumberFormatException e) {
				plugin.log.warning("Portal (" + this.keySet().toArray()[i]
						+ ")'s location is invalid!");
				continue;
			} catch (IndexOutOfBoundsException e) {
				plugin.log.warning("Portal (" + this.keySet().toArray()[i]
						+ ")'s location is invalid!");
				continue;
			}
			if ((world = plugin.getServer().getWorld(key.split(",")[0])) == null) {
				plugin.log.warning("Portal (" + this.keySet().toArray()[i]
						+ ")'s location is invalid!");
				continue;
			}
			location = new Location(world, x, y, z);
			try {
				if (this.getProperty(key).split("/")[1].startsWith("home")) {
					homeNum = Integer
							.parseInt(this.getProperty(key).split("/")[1]
									.replaceFirst("home", ""));
				} else if ((warp = plugin.warps.getWarp(this.getProperty(key)
						.split("/")[1])) == null) {
					plugin.log.warning("Portal (" + this.keySet().toArray()[i]
							+ ")'s warp name is invalid or has been removed!");
					continue;
				}
			} catch (IndexOutOfBoundsException e) {
				plugin.log.warning("Portal (" + this.keySet().toArray()[i]
						+ ")'s warp name is invalid or has been removed!");
				continue;
			} catch (NumberFormatException e) {
				plugin.log.warning("Portal (" + this.keySet().toArray()[i]
						+ ")'s warp name is invalid or has been removed!");
				continue;
			}
			try {
				group = plugin.perm.groupFile.getGroup(this.getProperty(key)
						.split("/")[0]);
			} catch (NullPointerException e) {
				group = null;
			}
			if (homeNum == null) {
				portals.put(location, new Portal(location, group, warp));
			} else {
				portals.put(location, new HomePortal(plugin, location, group,
						homeNum));
			}
		}
	}

	public Portal getPortalAt(Location loc) {
		loc = Portal.getHandleBlock(loc);
		for (int i = 0; i < portals.size(); i++) {
			Location key = (Location) portals.keySet().toArray()[i];
			if (key.getBlockX() == loc.getBlockX()
					&& key.getBlockY() == loc.getBlockY()
					&& key.getBlockZ() == loc.getBlockZ()
					&& key.getWorld().getName()
							.equals(loc.getWorld().getName())) {
				return portals.get(key);
			}
		}
		return null;
	}

	public void updatePortal(Portal portal) {
		Location loc = portal.getLocation();
		String groupname;
		if (portal.getGroup() == null) {
			groupname = "";
		} else {
			groupname = portal.getGroup().getName();
		}
		if (portal instanceof HomePortal) {
			this.put(loc.getWorld().getName() + "," + loc.getBlockX() + ","
					+ loc.getBlockY() + "," + loc.getBlockZ(), groupname
					+ "/home" + ((HomePortal) portal).getHomeNumber());
		} else {
			this.put(loc.getWorld().getName() + "," + loc.getBlockX() + ","
					+ loc.getBlockY() + "," + loc.getBlockZ(), groupname + "/"
					+ portal.getWarp().warpName);
		}
		saveFile();
	}

	public void addPortal(Portal portal) {
		portals.put(portal.getLocation(), portal);
		updatePortal(portal);
	}
}
