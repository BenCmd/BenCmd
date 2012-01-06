package com.bendude56.bencmd.multiworld;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.Commands;
import com.bendude56.bencmd.User;

public class MultiworldCommands implements Commands {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		User user = User.getUser(sender);
		if (commandLabel.equalsIgnoreCase("world")) {
			world(user, args);
			return true;
		}
		return false;
	}

	private void world(User user, String[] args) {
		if (args.length == 0) {
			user.sendMessage(ChatColor.YELLOW + "Proper use is: /world {create|delete|reset|info}");
		} else if (args[0].equalsIgnoreCase("create")) {
			if (!user.hasPerm("bencmd.world.create")) {
				user.sendMessage(ChatColor.RED + "You don't have permission to do that!");
				BenCmd.getPlugin().logPermFail();
				return;
			}
			if (args.length < 2|| args.length > 3) {
				user.sendMessage(ChatColor.YELLOW + "Proper use is: /world create <name> [seed]");
				return;
			}
			long seed = 0;
			if (args.length > 2) {
				try {
					seed = Long.parseLong(args[2]);
				} catch (NumberFormatException e) {
					seed = args[2].hashCode();
				}
			}
			try {
				BenCmd.getWorlds().createWorld(args[1], seed);
				user.sendMessage(ChatColor.GREEN + "World '" + args[1] + "' has been created! Use /spawn " + args[1] + " to go there!");
			} catch (IOException e) {
				user.sendMessage(ChatColor.RED + "Failed to create world '" + args[1] + "'!");
				BenCmd.log(Level.SEVERE, "Failed to create world '" + args[1] + "':");
				BenCmd.log(e);
			}
		} else if (args[0].equalsIgnoreCase("delete")) {
			if (!user.hasPerm("bencmd.world.delete")) {
				user.sendMessage(ChatColor.RED + "You don't have permission to do that!");
				BenCmd.getPlugin().logPermFail();
				return;
			}
			BenCmdWorld w;
			if (args.length == 1) {
				w = BenCmd.getWorlds().getWorld(((Player) user.getHandle()).getWorld());
				if (w == null) {
					user.sendMessage(ChatColor.RED + "Your current world isn't controlled by BenCmd!");
					return;
				}
			} else if (args.length == 2) {
				w = BenCmd.getWorlds().getWorld(args[1]);
				if (w == null) {
					user.sendMessage(ChatColor.RED + "That world doesn't exist or isn't controlled by BenCmd!");
					return;
				}
			} else {
				user.sendMessage(ChatColor.YELLOW + "Proper use is: /world delete [world]");
				return;
			}
			if (new Date().getTime() < w.getDangerTime()) {
				try {
					w.delete();
					user.sendMessage(ChatColor.GREEN + "World '" + w.getName() + "' has been deleted!");
				} catch (IOException e) {
					user.sendMessage(ChatColor.RED + "Failed to delete that world!");
					BenCmd.log(Level.SEVERE, "Failed to delete world '" + w.getName() + "':");
					BenCmd.log(e);
				}
			} else {
				w.setDangerTime(new Date().getTime() + 20000);
				user.sendMessage(ChatColor.RED + "WARNING: You are about to permanently delete this world!");
				user.sendMessage(ChatColor.RED + "All current users will be sent to spawn! Repeat this");
				user.sendMessage(ChatColor.RED + "command within 20 seconds to verify your intention!");
			}
		} else if (args[0].equalsIgnoreCase("reset")) {
			if (!user.hasPerm("bencmd.world.reset")) {
				user.sendMessage(ChatColor.RED + "You don't have permission to do that!");
				BenCmd.getPlugin().logPermFail();
				return;
			}
			BenCmdWorld w;
			if (args.length == 1) {
				w = BenCmd.getWorlds().getWorld(((Player) user.getHandle()).getWorld());
				if (w == null) {
					user.sendMessage(ChatColor.RED + "Your current world isn't controlled by BenCmd!");
					return;
				}
			} else if (args.length == 2) {
				w = BenCmd.getWorlds().getWorld(args[1]);
				if (w == null) {
					user.sendMessage(ChatColor.RED + "That world doesn't exist or isn't controlled by BenCmd!");
					return;
				}
			} else {
				user.sendMessage(ChatColor.YELLOW + "Proper use is: /world reset [world]");
				return;
			}
			if (new Date().getTime() < w.getDangerTime()) {
				try {
					w.reset();
					user.sendMessage(ChatColor.GREEN + "World '" + w.getName() + "' has been reset!");
				} catch (IOException e) {
					user.sendMessage(ChatColor.RED + "Failed to reset that world!");
					BenCmd.log(Level.SEVERE, "Failed to reset world '" + w.getName() + "':");
					BenCmd.log(e);
				}
			} else {
				w.setDangerTime(new Date().getTime() + 20000);
				user.sendMessage(ChatColor.RED + "WARNING: You are about to permanently reset this world!");
				user.sendMessage(ChatColor.RED + "All current users will be sent to spawn! Repeat this");
				user.sendMessage(ChatColor.RED + "command within 20 seconds to verify your intention!");
			}
		} else if (args[0].equalsIgnoreCase("info")) {
			if (!user.hasPerm("bencmd.world.info")) {
				
			}
		}
	}

}
