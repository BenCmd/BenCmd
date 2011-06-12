package ben_dude56.plugins.bencmd.multiworld;

import org.bukkit.Location;
import org.bukkit.Material;

import ben_dude56.plugins.bencmd.permissions.PermissionGroup;
import ben_dude56.plugins.bencmd.warps.Warp;

public class Portal {

	public static Location getHandleBlock(Location loc) {
		loc.setX(loc.getBlockX());
		loc.setY(loc.getBlockY());
		loc.setZ(loc.getBlockZ());
		// Increase to highest possible value that is a portal on the X axis
		while (new Location(loc.getWorld(), loc.getBlockX() + 1,
				loc.getBlockY(), loc.getBlockZ()).getBlock().getType() == Material.PORTAL) {
			loc.setX(loc.getBlockX() + 1);
		}
		// Increase to highest possible value that is a portal on the Y axis
		while (new Location(loc.getWorld(), loc.getBlockX(),
				loc.getBlockY() + 1, loc.getBlockZ()).getBlock().getType() == Material.PORTAL) {
			loc.setY(loc.getBlockY() + 1);
		}
		// Increase to highest possible value that is a portal on the Z axis
		while (new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(),
				loc.getBlockZ() + 1).getBlock().getType() == Material.PORTAL) {
			loc.setZ(loc.getBlockZ() + 1);
		}
		return loc;
	}

	private Location location;
	private PermissionGroup allowed;
	private Warp warp;

	public Portal(Location location, PermissionGroup allowableGroup, Warp warpTo) {
		this.location = location;
		allowed = allowableGroup;
		warp = warpTo;
	}

	public Location getLocation() {
		return location;
	}

	public PermissionGroup getGroup() {
		return allowed;
	}

	public Warp getWarp() {
		return warp;
	}
}
