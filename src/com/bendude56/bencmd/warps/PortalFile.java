package com.bendude56.bencmd.warps;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.BenCmdFile;
import com.bendude56.bencmd.permissions.PermissionGroup;

public class PortalFile extends BenCmdFile {
	private HashMap<Location, Portal>	portals;

	public PortalFile() {
		super("portals.db", "--BenCmd Portal File--", true);
		portals = new HashMap<Location, Portal>();
		loadFile();
		loadAll();
	}

	public void loadAll() {
		portals.clear();
		for (Entry<Object, Object> e : getFile().entrySet()) {
			int x;
			int y;
			int z;
			World world;
			Location location;
			Warp warp = null;
			PermissionGroup group;
			Integer homeNum = null;
			try {
				x = Integer.parseInt(((String) e.getKey()).split(",")[1]);
				y = Integer.parseInt(((String) e.getKey()).split(",")[2]);
				z = Integer.parseInt(((String) e.getKey()).split(",")[3]);
			} catch (NumberFormatException ex) {
				BenCmd.log(Level.WARNING, "Portal (" + ((String) e.getKey()) + ")'s location is invalid!");
				continue;
			} catch (IndexOutOfBoundsException ex) {
				BenCmd.log(Level.WARNING, "Portal (" + ((String) e.getKey()) + ")'s location is invalid!");
				continue;
			}
			if ((world = Bukkit.getWorld(((String) e.getKey()).split(",")[0])) == null) {
				BenCmd.log(Level.WARNING, "Portal (" + ((String) e.getKey()) + ")'s location is invalid!");
				continue;
			}
			location = new Location(world, x, y, z);
			try {
				if (((String) e.getValue()).split("/")[1].startsWith("home")) {
					homeNum = Integer.parseInt(((String) e.getValue()).split("/")[1].replaceFirst("home", ""));
				} else if ((warp = BenCmd.getWarps().getWarp(((String) e.getValue()).split("/")[1])) == null) {
					BenCmd.log(Level.WARNING, "Portal (" + ((String) e.getKey()) + ")'s warp name is invalid or has been removed!");
					continue;
				}
			} catch (IndexOutOfBoundsException ex) {
				BenCmd.log(Level.WARNING, "Portal (" + ((String) e.getKey()) + ")'s warp name is invalid or has been removed!");
				continue;
			} catch (NumberFormatException ex) {
				BenCmd.log(Level.WARNING, "Portal (" + ((String) e.getKey()) + ")'s warp name is invalid or has been removed!");
				continue;
			}
			try {
				group = BenCmd.getPermissionManager().getGroupFile().getGroup(((String) e.getValue()).split("/")[0]);
			} catch (NullPointerException ex) {
				group = null;
			}
			if (homeNum == null) {
				portals.put(location, new Portal(location, group, warp));
			} else {
				portals.put(location, new HomePortal(location, group, homeNum));
			}
		}
	}

	public void saveAll() {
		for (Entry<Location, Portal> e : portals.entrySet()) {
			updatePortal(e.getValue(), false);
		}
		saveFile();
	}

	public Portal getPortalAt(Location loc) {
		loc = Portal.getHandleBlock(loc);
		for (int i = 0; i < portals.size(); i++) {
			Location key = (Location) portals.keySet().toArray()[i];
			if (key.getBlockX() == loc.getBlockX() && key.getBlockY() == loc.getBlockY() && key.getBlockZ() == loc.getBlockZ() && key.getWorld().getName().equals(loc.getWorld().getName())) {
				return portals.get(key);
			}
		}
		return null;
	}

	public void updatePortal(Portal portal, boolean saveFile) {
		Location loc = portal.getLocation();
		String groupname;
		if (portal.getGroup() == null) {
			groupname = "";
		} else {
			groupname = portal.getGroup().getName();
		}
		if (portal instanceof HomePortal) {
			getFile().put(loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ(), groupname + "/home" + ((HomePortal) portal).getHomeNumber());
		} else {
			getFile().put(loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ(), groupname + "/" + portal.getWarp().warpName);
		}
		if (saveFile)
			saveFile();
	}

	public void addPortal(Portal portal) {
		portals.put(portal.getLocation(), portal);
		updatePortal(portal, true);
	}

	public void remPortal(Location loc) {
		portals.remove(loc);
		getFile().remove(loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ());
		saveFile();
	}
}
