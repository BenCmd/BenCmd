package com.bendude56.bencmd.advanced.redstone;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.Commands;
import com.bendude56.bencmd.User;

public class RedstoneCommands implements Commands {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		User user = User.getUser(sender);
		if (commandLabel.equalsIgnoreCase("lever") && user.hasPerm("bencmd.lever")) {
			Lever(args, user);
			return true;
		}
		return false;
	}

	public void Lever(String[] args, User user) {
		if (((Player) user.getHandle()).getTargetBlock(null, 4).getType() != Material.LEVER) {
			user.sendMessage(BenCmd.getLocale().getString("command.lever.notALever"));
			return;
		}
		Location l = ((Player) user.getHandle()).getTargetBlock(null, 4).getLocation();
		if (args.length != 1) {
			BenCmd.showUse(user, "lever");
			return;
		} else if (args[0].equalsIgnoreCase("day")) {
			BenCmd.getRedstoneFile().addLever(new RedstoneLever(l, RedstoneLever.LeverType.DAY));
			user.sendMessage(BenCmd.getLocale().getString("command.lever.success.day"));
			BenCmd.log(BenCmd.getLocale().getString("log.lever.day", user.getName(), l.getBlockX() + "", l.getBlockY() + "", l.getBlockZ() + "", l.getWorld().getName()));
		} else if (args[0].equalsIgnoreCase("night")) {
			BenCmd.getRedstoneFile().addLever(new RedstoneLever(l, RedstoneLever.LeverType.NIGHT));
			user.sendMessage(BenCmd.getLocale().getString("command.lever.success.night"));
			BenCmd.log(BenCmd.getLocale().getString("log.lever.night", user.getName(), l.getBlockX() + "", l.getBlockY() + "", l.getBlockZ() + "", l.getWorld().getName()));
		} else if (args[0].equalsIgnoreCase("none")) {
			if (!BenCmd.getRedstoneFile().isLever(l)) {
				user.sendMessage(BenCmd.getLocale().getString("command.lever.notTimed"));
				return;
			}
			BenCmd.getRedstoneFile().removeLever(l);
			user.sendMessage(BenCmd.getLocale().getString("command.lever.success.none"));
			BenCmd.log(BenCmd.getLocale().getString("log.lever.none", user.getName(), l.getBlockX() + "", l.getBlockY() + "", l.getBlockZ() + "", l.getWorld().getName()));
		} else {
			BenCmd.showUse(user, "lever");
			return;
		}
	}

}
