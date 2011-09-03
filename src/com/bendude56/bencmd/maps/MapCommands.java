package com.bendude56.bencmd.maps;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;

import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.Commands;
import com.bendude56.bencmd.User;


public class MapCommands implements Commands {
	BenCmd plugin;

	public MapCommands(BenCmd instance) {
		plugin = instance;
	}

	public boolean onCommand(CommandSender sender, Command command,
			String commandLabel, String[] args) {
		User user;
		try {
			user = User.getUser(plugin, (Player) sender);
		} catch (ClassCastException e) {
			user = User.getUser(plugin);
		}
		if (commandLabel.equalsIgnoreCase("map")
				&& (user.hasPerm("bencmd.map.zoom") || user.hasPerm("bencmd.map.center"))) {
			Map(args, user);
			return true;
		}
		return false;
	}

	public void Map(String[] args, User user) {
		if (args.length == 0) {
			return;
		}
		if (user.getHandle().getItemInHand().getType() != Material.MAP) {
			return;
		}
		BCMap map = new BCMap(user.getHandle().getItemInHand().getDurability(),
				((CraftWorld) user.getHandle().getWorld()).getHandle());
		if (args[0].equalsIgnoreCase("zoomin")) {
			if (!user.hasPerm("bencmd.map.zoom")) {
				user.sendMessage(ChatColor.RED
						+ "You don't have permission to do that!");
				return;
			}
			map.zoomIn();
		} else if (args[0].equalsIgnoreCase("zoomout")) {
			if (!user.hasPerm("bencmd.map.zoom")) {
				user.sendMessage(ChatColor.RED
						+ "You don't have permission to do that!");
				return;
			}
			map.zoomOut();
		} else if (args[0].equalsIgnoreCase("center")) {
			if (!user.hasPerm("bencmd.map.center")) {
				user.sendMessage(ChatColor.RED
						+ "You don't have permission to do that!");
				return;
			}
			map.setCenter(user.getHandle().getLocation().getBlockX(), user
					.getHandle().getLocation().getBlockZ());
		}
	}
}