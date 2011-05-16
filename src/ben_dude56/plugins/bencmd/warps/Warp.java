package ben_dude56.plugins.bencmd.warps;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;

import ben_dude56.plugins.bencmd.*;

public class Warp {
	public Location loc;
	public String warpName;
	public String mustInheritGroup;
	public BenCmd plugin;
	public Logger log = Logger.getLogger("minecraft");

	public Warp(double x, double y, double z, double yaw, double pitch,
			String world, String name, String group, BenCmd instance) {
		warpName = name;
		plugin = instance;
		try {
			loc = new Location(plugin.getServer().getWorld(world), x, y, z,
					(float) yaw, (float) pitch);
		} catch (NullPointerException e) {
			log.severe("Couldn't load warp " + warpName + "!");
			return;
		}
		mustInheritGroup = group;
	}

	public Warp(Location loc, String name, String group, BenCmd instance) {
		double x = loc.getX();
		double y = loc.getY();
		double z = loc.getZ();
		float yaw = loc.getYaw();
		float pitch = loc.getPitch();
		World world = loc.getWorld();
		warpName = name;
		plugin = instance;
		try {
			loc = new Location(world, x, y, z, yaw, pitch);
		} catch (NullPointerException e) {
			log.severe("Couldn't load warp " + warpName + "!");
			return;
		}
		mustInheritGroup = group;
	}

	public void WarpHere(WarpableUser player) {
		if (!this.canWarpHere(player)) {
			player.sendMessage(ChatColor.RED
					+ "You don't have permission to warp there!");
			log.info(player.getName() + " tried to warp to " + warpName
					+ ", but they don't have permission.");
			return;
		}
		try {
			plugin.checkpoints.SetPreWarp(player.getHandle());
			player.getHandle().teleport(loc);
			player.sendMessage(ChatColor.YELLOW + "Woosh!");
			log.info(player.getName() + " just warped to warp " + warpName
					+ ".");
		} catch (NullPointerException e) {
			log.severe("There was an error warping player " + player.getName()
					+ " to warp " + warpName + "!");
		}
	}

	public void WarpHere(WarpableUser player, WarpableUser sender) {
		if (!this.canWarpHere(sender)) {
			player.sendMessage(ChatColor.RED
					+ "You don't have permission to warp them there!");
			log.info(sender.getName() + " tried to warp " + player.getName()
					+ " to " + warpName + ", but they don't have permission.");
		}
		try {
			plugin.checkpoints.SetPreWarp(player.getHandle());
			player.getHandle().teleport(loc);
			player.sendMessage(ChatColor.YELLOW + "Woosh!");
			log.info(sender.getName() + " just warped " + player.getName()
					+ " to warp " + warpName + ".");
		} catch (NullPointerException e) {
			log.severe("There was an error warping player " + player.getName()
					+ " to warp " + warpName + "!");
		}
	}

	public boolean canWarpHere(WarpableUser player) {
		if (player.isServer()) {
			return true;
		}
		if (mustInheritGroup == "") {
			return true;
		}
		if (plugin.perm.userFile
				.userInGroup(player.getName(), mustInheritGroup)) {
			return true;
		}
		if (player.hasPerm("canWarpAnywhere")) {
			return true;
		}
		return false;
	}
}
