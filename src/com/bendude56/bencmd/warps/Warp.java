package com.bendude56.bencmd.warps;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.bendude56.bencmd.*;


public class Warp {
	public Location loc;
	public String warpName;
	public String mustInheritGroup;

	public Warp(double x, double y, double z, double yaw, double pitch,
			String world, String name, String group) {
		warpName = name;
		try {
			loc = new Location(Bukkit.getWorld(world), x, y, z,
					(float) yaw, (float) pitch);
		} catch (NullPointerException e) {
			BenCmd.log(Level.SEVERE, "Couldn't load warp " + warpName + "!");
			BenCmd.log(e);
			return;
		}
		mustInheritGroup = group;
	}

	public Warp(Location loc, String name, String group) {
		double x = loc.getX();
		double y = loc.getY();
		double z = loc.getZ();
		float yaw = loc.getYaw();
		float pitch = loc.getPitch();
		World world = loc.getWorld();
		warpName = name;
		try {
			loc = new Location(world, x, y, z, yaw, pitch);
		} catch (NullPointerException e) {
			BenCmd.log(Level.SEVERE, "Couldn't load warp " + warpName + "!");
			BenCmd.log(e);
			return;
		}
		mustInheritGroup = group;
	}

	public void WarpHere(WarpableUser player) {
		if (!this.canWarpHere(player)) {
			player.sendMessage(ChatColor.RED
					+ "You don't have permission to warp there!");
			BenCmd.log(player.getName() + " tried to warp to " + warpName
					+ ", but they don't have permission.");
			return;
		}
		try {
			BenCmd.getWarpCheckpoints().SetPreWarp(((Player)player.getHandle()));
			((Player)player.getHandle()).teleport(loc);
			player.sendMessage(ChatColor.YELLOW + "Woosh!");
			BenCmd.log(player.getName() + " just warped to warp "
					+ warpName + ".");
		} catch (NullPointerException e) {
			BenCmd.log(Level.SEVERE, "There was an error warping player "
					+ player.getName() + " to warp " + warpName + "!");
			BenCmd.log(e);
		}
	}

	public void WarpHere(WarpableUser player, WarpableUser sender) {
		if (!this.canWarpHere(sender)) {
			player.sendMessage(ChatColor.RED
					+ "You don't have permission to warp them there!");
			BenCmd.log(sender.getName() + " tried to warp "
					+ player.getName() + " to " + warpName
					+ ", but they don't have permission.");
		}
		try {
			BenCmd.getWarpCheckpoints().SetPreWarp(((Player)player.getHandle()));
			((Player)player.getHandle()).teleport(loc);
			player.sendMessage(ChatColor.YELLOW + "Woosh!");
			BenCmd.log(sender.getName() + " just warped "
					+ player.getName() + " to warp " + warpName + ".");
		} catch (NullPointerException e) {
			BenCmd.log(Level.SEVERE, "There was an error warping player "
					+ player.getName() + " to warp " + warpName + "!");
			BenCmd.log(e);
		}
	}

	public boolean canWarpHere(WarpableUser player) {
		if (player.isServer()) {
			return true;
		}
		if (mustInheritGroup == "") {
			return true;
		}
		if (BenCmd.getPermissionManager().getGroupFile().getGroup(mustInheritGroup)
				.userInGroup(player)) {
			return true;
		}
		if (player.hasPerm("bencmd.warp.all")) {
			return true;
		}
		return false;
	}
}
