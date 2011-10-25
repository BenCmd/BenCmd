package com.bendude56.bencmd.advanced.redstone;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.Commands;
import com.bendude56.bencmd.User;


public class RedstoneCommands implements Commands {

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String commandLabel, String[] args) {
		User user;
		try {
			user = User.getUser((Player) sender);
		} catch (ClassCastException e) {
			user = User.getUser();
		}
		if (commandLabel.equalsIgnoreCase("lever")
				&& user.hasPerm("bencmd.lever")) {
			Lever(args, user);
			return true;
		}
		return false;
	}

	public void Lever(String[] args, User user) {
		if (args[0].equalsIgnoreCase("day")) {
			BenCmd.getRedstoneFile().addLever(new RedstoneLever(((Player) user.getHandle())
					.getTargetBlock(null, 4).getLocation(),
					RedstoneLever.LeverType.DAY));
		} else if (args[0].equalsIgnoreCase("night")) {
			BenCmd.getRedstoneFile().addLever(new RedstoneLever(((Player) user.getHandle())
					.getTargetBlock(null, 4).getLocation(),
					RedstoneLever.LeverType.NIGHT));
		} else if (args[0].equalsIgnoreCase("none")) {
			BenCmd.getRedstoneFile().removeLever(((Player) user.getHandle()).getTargetBlock(null, 4)
					.getLocation());
		}
	}

}
