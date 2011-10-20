package com.bendude56.bencmd.warps;

import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.bendude56.bencmd.*;


public class PreWarp {
	HashMap<String, Warp> prewarps = new HashMap<String, Warp>();

	public void ClearWarps() {
		prewarps.clear();
		Bukkit.broadcastMessage(
				ChatColor.GRAY + "All pre-warp checkpoints have been cleared.");
	}

	public boolean returnPreWarp(Player player) {
		String name = player.getName() + "_check";
		if (prewarps.containsKey(name)) {
			prewarps.get(name).WarpHere(new WarpableUser(player));
			return true;
		} else {
			return false;
		}
	}

	public void RemovePreWarp(Player player) {
		String name = player.getName() + "_check";
		if (prewarps.containsKey(name)) {
			prewarps.remove(name);
		}
	}

	public void SetPreWarp(Player player) {
		try {
			double x = player.getLocation().getX();
			double y = player.getLocation().getY();
			double z = player.getLocation().getZ();
			double yaw = player.getLocation().getYaw();
			double pitch = player.getLocation().getPitch();
			String world = player.getWorld().getName();
			String name = player.getName() + "_check";
			prewarps.put(name, new Warp(x, y, z, yaw, pitch, world, name, ""));
		} catch (Exception e) {
			player.sendMessage(ChatColor.RED
					+ "There was a problem creating the checkpoint!");
			BenCmd.log(Level.WARNING, "Couldn't create new checkpoint:");
			BenCmd.log(e);
			return;
		}
	}
}
