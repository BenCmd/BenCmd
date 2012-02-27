package com.bendude56.bencmd.multiworld;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;

import org.bukkit.Bukkit;
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
			user.sendMessage(ChatColor.YELLOW + "Proper use is: /world {create|import|remove|delete|reset|info|spawn}");
		} else if (args[0].equalsIgnoreCase("create")) {
			if (!user.hasPerm("bencmd.world.create")) {
				user.sendMessage(BenCmd.getLocale().getString("basic.noPermission"));
				BenCmd.getPlugin().logPermFail();
				return;
			}
			if (args.length < 2|| args.length > 3) {
				user.sendMessage(ChatColor.YELLOW + "Proper use is: /world create <name> [seed]");
				return;
			}
			if (new File(args[1]).exists()) {
				user.sendMessage(ChatColor.RED + "A folder or file already exists with that name!");
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
		} else if (args[0].equalsIgnoreCase("import")) {
			if (!user.hasPerm("bencmd.world.import")) {
				user.sendMessage(BenCmd.getLocale().getString("basic.noPermission"));
				BenCmd.getPlugin().logPermFail();
				return;
			}
			if (args.length != 2) {
				user.sendMessage(ChatColor.YELLOW + "Proper use is: /world import <name>");
				return;
			}
			if (BenCmd.getWorlds().getWorld(args[1]) != null) {
				user.sendMessage(ChatColor.RED + "That world is already under BenCmd control!");
				return;
			} else if (Bukkit.getWorld(args[1]) != null) {
				user.sendMessage(ChatColor.RED + "That world is under the control of another plugin!");
				return;
			} else if (!new File(args[1]).exists() || !new File(args[1]).isDirectory()) {
				user.sendMessage(ChatColor.RED + "That world was not found!");
				return;
			}
			try {
				BenCmd.getWorlds().importWorld(args[1]);
				user.sendMessage(ChatColor.GREEN + "World '" + args[1] + "' has been imported! Use /spawn " + args[1] + " to go there!");
			} catch (IOException e) {
				user.sendMessage(ChatColor.RED + "Failed to import world '" + args[1] + "'!");
				BenCmd.log(Level.SEVERE, "Failed to import world '" + args[1] + "':");
				BenCmd.log(e);
			}
		} else if (args[0].equalsIgnoreCase("remove")) {
			if (!user.hasPerm("bencmd.world.remove")) {
				user.sendMessage(BenCmd.getLocale().getString("basic.noPermission"));
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
				user.sendMessage(ChatColor.YELLOW + "Proper use is: /world remove [world]");
				return;
			}
			if (new Date().getTime() < w.getDangerTime()) {
				w.remove();
				user.sendMessage(ChatColor.GREEN + "World '" + w.getName() + "' has been unloaded and removed from BenCmd control!");
			} else {
				w.setDangerTime(new Date().getTime() + 20000);
				user.sendMessage(ChatColor.RED + "WARNING: You are about to remove this world from BenCmd");
				user.sendMessage(ChatColor.RED + "control! All current users will be sent to spawn! Repeat");
				user.sendMessage(ChatColor.RED + "this command within 20 seconds to verify your intention!");
			}
		} else if (args[0].equalsIgnoreCase("delete")) {
			if (!user.hasPerm("bencmd.world.delete")) {
				user.sendMessage(BenCmd.getLocale().getString("basic.noPermission"));
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
				user.sendMessage(BenCmd.getLocale().getString("basic.noPermission"));
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
				user.sendMessage(BenCmd.getLocale().getString("basic.noPermission"));
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
				user.sendMessage(ChatColor.YELLOW + "Proper use is: /world info [world]");
				return;
			}
			user.sendMessage(ChatColor.GRAY + "Information for world \"" + w.getName() + "\"");
			user.sendMessage(ChatColor.GRAY + "Seed: " + w.getSeed());
			user.sendMessage(ChatColor.GRAY + "Spawn mode: " + ((w.getAllowSpawnAggressive()) ? "A" : "a") + ((w.getAllowSpawnNeutral()) ? "N" : "n") + ((w.getAllowSpawnPassive()) ? "P" : "p"));
		} else if (args[0].equalsIgnoreCase("spawn")) {
			if (!user.hasPerm("bencmd.world.spawn")) {
				user.sendMessage(BenCmd.getLocale().getString("basic.noPermission"));
				BenCmd.getPlugin().logPermFail();
				return;
			}
			BenCmdWorld w;
			if (args.length == 3) {
				w = BenCmd.getWorlds().getWorld(((Player) user.getHandle()).getWorld());
				if (w == null) {
					user.sendMessage(ChatColor.RED + "Your current world isn't controlled by BenCmd!");
					return;
				}
			} else if (args.length == 4) {
				w = BenCmd.getWorlds().getWorld(args[1]);
				if (w == null) {
					user.sendMessage(ChatColor.RED + "That world doesn't exist or isn't controlled by BenCmd!");
					return;
				}
			} else {
				user.sendMessage(ChatColor.YELLOW + "Proper use is: /world spawn {passive|neutral|aggressive} {true|false} [world]");
				return;
			}
			boolean allow;
			if (args[2].equalsIgnoreCase("true")) {
				allow = true;
			} else if (args[2].equalsIgnoreCase("false")) {
				allow = false;
			} else {
				user.sendMessage(ChatColor.YELLOW + "Proper use is: /world spawn {passive|neutral|aggressive} {true|false} [world]");
				return;
			}
			if (args[1].equalsIgnoreCase("passive")) {
				w.setAllowSpawnPassive(allow);
				BenCmd.getWorlds().updateWorldEntry(w, true);
				if (allow) {
					user.sendMessage(ChatColor.GREEN + "Passive mobs will now spawn in that world!");
				} else {
					user.sendMessage(ChatColor.GREEN + "Passive mobs will no longer spawn in that world!");
				}
			} else if (args[1].equalsIgnoreCase("neutral")) {
				w.setAllowSpawnNeutral(allow);
				BenCmd.getWorlds().updateWorldEntry(w, true);
				if (allow) {
					user.sendMessage(ChatColor.GREEN + "Neutral mobs will now spawn in that world!");
				} else {
					user.sendMessage(ChatColor.GREEN + "Neutral mobs will no longer spawn in that world!");
				}
			} else if (args[1].equalsIgnoreCase("aggressive")) {
				w.setAllowSpawnAggressive(allow);
				BenCmd.getWorlds().updateWorldEntry(w, true);
				if (allow) {
					user.sendMessage(ChatColor.GREEN + "Aggressive mobs will now spawn in that world!");
				} else {
					user.sendMessage(ChatColor.GREEN + "Aggressive mobs will no longer spawn in that world!");
				}
			}
		}
	}

}
