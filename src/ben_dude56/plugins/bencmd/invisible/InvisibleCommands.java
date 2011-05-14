package ben_dude56.plugins.bencmd.invisible;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ben_dude56.plugins.bencmd.BenCmd;
import ben_dude56.plugins.bencmd.Commands;
import ben_dude56.plugins.bencmd.User;

public class InvisibleCommands implements Commands {
	BenCmd plugin;
	
	public InvisibleCommands(BenCmd instance) {
		plugin = instance;
	}
	
	public boolean onCommand(CommandSender sender, Command command,
			String commandLabel, String[] args) {
		User user;
		try {
			user = new User(plugin, (Player) sender);
		} catch (ClassCastException e) {
			user = new User(plugin);
		}
		if (commandLabel.equalsIgnoreCase("poof")
				&& user.hasPerm("canPoof")) {
			Poof(user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("nopoof")
				&& user.hasPerm("canNoPoof")) {
			NoPoof(user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("offline")
				&& user.hasPerm("canOffline")) {
			Offline(user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("online")
				&& user.hasPerm("canOffline")) {
			Online(user);
			return true;
		}
		return false;
	}
	
	public void Poof(User user) {
		if(!user.isPoofed()) {
			user.Poof();
			user.sendMessage(ChatColor.GREEN + "POOF!");
		} else {
			if(user.isOffline()) {
				user.sendMessage(ChatColor.RED + "You cannot unpoof while offline!");
				return;
			}
			user.UnPoof();
			user.sendMessage(ChatColor.GREEN + "REVERSE POOF!");
		}
	}
	
	public void NoPoof(User user) {
		if(!user.isNoPoofed()) {
			user.NoPoof();
			user.sendMessage(ChatColor.GREEN + "NOPOOF!");
		} else {
			user.UnNoPoof();
			user.sendMessage(ChatColor.GREEN + "REVERSE NOPOOF!");
		}
	}
	
	public void Offline(User user) {
		if(!user.isPoofed()) {
			user.sendMessage(ChatColor.RED + "You must be poofed to do that!");
		} else {
			if(user.isOffline()) {
				user.sendMessage(ChatColor.RED + "You are already offline!");
			} else {
				user.goOffline();
			}
		}
	}
	
	public void Online(User user) {
		if(!user.isOffline()) {
			user.sendMessage(ChatColor.RED + "You are already online!");
		} else {
			user.goOnline();
		}
	}
}
