package com.bendude56.bencmd.invisible;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.Commands;
import com.bendude56.bencmd.User;

public class InvisibleCommands implements Commands {

	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		User user;
		try {
			user = User.getUser((Player) sender);
		} catch (ClassCastException e) {
			user = User.getUser();
		}
		if (commandLabel.equalsIgnoreCase("poof") && user.hasPerm("bencmd.poof.poof")) {
			Poof(user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("nopoof") && user.hasPerm("bencmd.poof.nopoof")) {
			NoPoof(user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("allpoof") && user.hasPerm("bencmd.poof.allpoof")) {
			AllPoof(user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("offline") && user.hasPerm("bencmd.poof.offline")) {
			Offline(user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("online") && user.hasPerm("bencmd.poof.offline")) {
			Online(user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("monitor") && user.hasPerm("bencmd.poof.monitor")) {
			Monitor(args, user);
			return true;
		}
		return false;
	}

	public void Poof(User user) {
		if (!user.isPoofed()) {
			user.poof();
			user.sendMessage(ChatColor.GREEN + "POOF!");
		} else {
			if (user.isOffline()) {
				user.sendMessage(ChatColor.RED + "You cannot unpoof while offline!");
				return;
			} else if (BenCmd.getMonitorController().isMonitoring((Player) user.getHandle())) {
				user.sendMessage(ChatColor.RED + "You cannot unpoof while monitoring a player!");
			} else if (user.isAllPoofed()) {
				user.unAllPoof();
				user.unPoof();
				user.sendMessage(ChatColor.GREEN + "REVERSE POOF!");
			} else {
				user.unPoof();
				user.sendMessage(ChatColor.GREEN + "REVERSE POOF!");
			}
		}
	}

	public void AllPoof(User user) {
		if (!user.isPoofed()) {
			user.poof();
			user.allPoof();
			user.sendMessage(ChatColor.GREEN + "ALLPOOF!");
		} else if (!user.isAllPoofed()) {
			user.allPoof();
			user.sendMessage(ChatColor.GREEN + "ALLPOOF!");
		} else {
			user.unAllPoof();
			user.sendMessage(ChatColor.GREEN + "REVERSE ALLPOOF! (STILL POOFED!)");
		}
	}

	public void NoPoof(User user) {
		if (!user.isNoPoofed()) {
			user.noPoof();
			user.sendMessage(ChatColor.GREEN + "NOPOOF!");
		} else {
			user.unNoPoof();
			user.sendMessage(ChatColor.GREEN + "REVERSE NOPOOF!");
		}
	}

	public void Offline(User user) {
		if (!user.isPoofed()) {
			user.sendMessage(ChatColor.RED + "You must be poofed to do that!");
		} else {
			if (user.isOffline()) {
				user.sendMessage(ChatColor.RED + "You are already offline!");
			} else {
				user.goOffline();
			}
		}
	}

	public void Online(User user) {
		if (!user.isOffline()) {
			user.sendMessage(ChatColor.RED + "You are already online!");
		} else {
			user.goOnline();
		}
	}
	
	public void Monitor(String[] args, User user) {
		if (args.length == 0) {
			user.sendMessage(ChatColor.YELLOW + "Proper usage is: /monitor {<player>|none}");
		} else if (args[0].equalsIgnoreCase("none")) {
			BenCmd.getMonitorController().cancelMonitor((Player) user.getHandle());
		} else {
			Player p2 = Bukkit.getPlayer(args[0]);
			if (p2 == null) {
				user.sendMessage(ChatColor.RED + "'" + args[0] + "' isn't online!");
				return;
			}
			BenCmd.getMonitorController().setMonitor((Player) user.getHandle(), p2);
		}
	}
}
