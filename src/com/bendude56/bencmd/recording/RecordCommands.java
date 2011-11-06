package com.bendude56.bencmd.recording;

import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.Commands;
import com.bendude56.bencmd.User;
import com.bendude56.bencmd.recording.RecordEntry.SuspicionLevel;

public class RecordCommands implements Commands {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		User user;
		try {
			user = User.getUser((Player) sender);
		} catch (ClassCastException e) {
			user = User.getUser();
		}
		if (commandLabel.equalsIgnoreCase("log") && user.hasPerm("bencmd.log.view")) {
			Log(args, user);
			return true;
		}
		return false;
	}

	public void Log(String[] args, User user) {
		if (args.length == 0) {
			// TODO Add usage
		} else if (args[0].equalsIgnoreCase("wand")) {
			if (!user.hasPerm("bencmd.log.wand")) {
				user.sendMessage(ChatColor.RED + "You don't have permission to do that!");
				BenCmd.getPlugin().logPermFail();
				return;
			}
			if (BenCmd.getRecordingFile().wandEnabled(user.getName())) {
				BenCmd.getRecordingFile().disableWand(user.getName());
				user.sendMessage(ChatColor.GREEN + "Log wand disabled!");
			} else {
				if (!((Player) user.getHandle()).getInventory().contains(Material.STICK)) {
					((Player) user.getHandle()).getInventory().addItem(new ItemStack(Material.STICK, 1));
				}
				BenCmd.getRecordingFile().enableWand(user.getName());
				user.sendMessage(ChatColor.GREEN + "Log wand enabled!");
			}
		} else if (args[0].equalsIgnoreCase("block")) {
			int page = 1;
			if (args.length > 1) {
				try {
					page = Integer.parseInt(args[1]);
				} catch (NumberFormatException e) {
					user.sendMessage(ChatColor.RED + "'" + args[1] + "' isn't a valid page number!");
					return;
				}
			}
			BenCmd.getRecordingFile().listBlock(user, ((Player) user.getHandle()).getTargetBlock(null, 10).getLocation(), page);
		} else if (args[0].equalsIgnoreCase("all")) {
			int page = 1;
			if (args.length > 1) {
				try {
					page = Integer.parseInt(args[1]);
				} catch (NumberFormatException e) {
					user.sendMessage(ChatColor.RED + "'" + args[1] + "' isn't a valid page number!");
					return;
				}
			}
			BenCmd.getRecordingFile().showRecords(user, BenCmd.getRecordingFile().getLoaded(user.getName()).getEntries(), true, true, page);
		} else if (args[0].equalsIgnoreCase("extreme")) {
			int page = 1;
			if (args.length > 1) {
				try {
					page = Integer.parseInt(args[1]);
				} catch (NumberFormatException e) {
					user.sendMessage(ChatColor.RED + "'" + args[1] + "' isn't a valid page number!");
					return;
				}
			}
			BenCmd.getRecordingFile().listLevel(user, SuspicionLevel.EXTREME, page);
		} else if (args[0].equalsIgnoreCase("load")) {
			if (args.length == 1) {
				user.sendMessage(ChatColor.RED + "Not enough arguments!");
				return;
			}
			if (BenCmd.getRecordingFile().recordingExists(args[1])) {
				if (BenCmd.getRecordingFile().hasRecordingLoaded(user.getName())) {
					BenCmd.getRecordingFile().unloadRecording(user.getName());
				}
				if (BenCmd.getRecordingFile().loadRecording(user.getName(), args[1])) {
					user.sendMessage(ChatColor.GREEN + "Recording loaded!");
				} else {
					user.sendMessage(ChatColor.RED + "Error loading recording!");
				}
			} else {
				user.sendMessage(ChatColor.RED + "No recording exists with the name '" + args[1] + "'!");
			}
		} else if (args[0].equalsIgnoreCase("unload")) {
			if (BenCmd.getRecordingFile().hasRecordingLoaded(user.getName())) {
				BenCmd.getRecordingFile().unloadRecording(user.getName());
				user.sendMessage(ChatColor.GREEN + "Recording unloaded!");
			} else {
				user.sendMessage(ChatColor.RED + "No recording is loaded!");
			}
		} else if (args[0].equalsIgnoreCase("save")) {
			if (!user.hasPerm("bencmd.record.copy")) {
				user.sendMessage(ChatColor.RED + "You don't have permission to do that!");
				BenCmd.getPlugin().logPermFail();
				return;
			}
			if (BenCmd.getRecordingFile().getLoaded(user.getName()).isTemporary()) {
				try {
					BenCmd.getRecordingFile().turnPermanent(BenCmd.getRecordingFile().getLoaded(user.getName()));
					user.sendMessage(ChatColor.GREEN + "Recording saved!");
				} catch (IOException e) {
					user.sendMessage(ChatColor.RED + "Failed to save recording!");
				}
			} else {
				user.sendMessage(ChatColor.RED + "You can't save a saved recording! Use /log copy instead!");
			}
		} else if (args[0].equalsIgnoreCase("copy")) {
			if (!user.hasPerm("bencmd.record.copy")) {
				user.sendMessage(ChatColor.RED + "You don't have permission to do that!");
				BenCmd.getPlugin().logPermFail();
				return;
			}
			if (args[1].startsWith("~") || args[1].equals("temp")) {
				user.sendMessage(ChatColor.RED + "Invalid recording name!");
				return;
			}
			if (BenCmd.getRecordingFile().recordingExists(args[1])) {
				user.sendMessage(ChatColor.RED + "That recording already exists!");
				return;
			}
			if (BenCmd.getRecordingFile().getLoaded(user.getName()).isTemporary()) {
				user.sendMessage(ChatColor.RED + "You can't copy an in-progress recording! Use /log save first!");
			} else {
				try {
					BenCmd.getRecordingFile().copy(BenCmd.getRecordingFile().getLoaded(user.getName()), args[1]);
					user.sendMessage(ChatColor.GREEN + "Recording copied!");
				} catch (IOException e) {
					user.sendMessage(ChatColor.RED + "Failed to copy recording!");
				}
			}
		} else if (args[0].equalsIgnoreCase("start")) {
			if (!user.hasPerm("bencmd.record.new")) {
				user.sendMessage(ChatColor.RED + "You don't have permission to do that!");
				BenCmd.getPlugin().logPermFail();
				return;
			}
			if (args[1].startsWith("~") || args[1].equals("temp")) {
				user.sendMessage(ChatColor.RED + "Invalid recording name!");
				return;
			}
			if (BenCmd.getRecordingFile().recordingExists(args[1])) {
				user.sendMessage(ChatColor.RED + "That recording already exists!");
				return;
			}
			try {
				BenCmd.getRecordingFile().newRecording(args[1]);
				user.sendMessage(ChatColor.GREEN + "Recording started!");
			} catch (IOException e) {
				user.sendMessage(ChatColor.RED + "Failed to start recording!");
			}
		} else if (args[0].equalsIgnoreCase("delete")) {
			if (!user.hasPerm("bencmd.record.delete")) {
				user.sendMessage(ChatColor.RED + "You don't have permission to do that!");
				BenCmd.getPlugin().logPermFail();
				return;
			}
			try {
				BenCmd.getRecordingFile().delete(BenCmd.getRecordingFile().getLoaded(user.getName()));
				user.sendMessage(ChatColor.GREEN + "Recording deleted!");
			} catch (IOException e) {
				user.sendMessage(ChatColor.RED + "Failed to delete recording!");
			}
		} else if (args[0].equalsIgnoreCase("list")) {
			BenCmd.getRecordingFile().listRecordings(user);
		}
	}

}
