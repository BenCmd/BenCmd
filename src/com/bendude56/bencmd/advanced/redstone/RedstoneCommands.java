package com.bendude56.bencmd.advanced.redstone;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.Commands;
import com.bendude56.bencmd.User;


public class RedstoneCommands implements Commands {

	private BenCmd plugin;

	public RedstoneCommands(BenCmd instance) {
		plugin = instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String commandLabel, String[] args) {
		User user;
		try {
			user = User.getUser(plugin, (Player) sender);
		} catch (ClassCastException e) {
			user = User.getUser(plugin);
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
			plugin.levers.addLever(new RedstoneLever(user.getHandle()
					.getTargetBlock(null, 4).getLocation(),
					RedstoneLever.LeverType.DAY));
		} else if (args[0].equalsIgnoreCase("night")) {
			plugin.levers.addLever(new RedstoneLever(user.getHandle()
					.getTargetBlock(null, 4).getLocation(),
					RedstoneLever.LeverType.NIGHT));
		} else if (args[0].equalsIgnoreCase("none")) {
			plugin.levers.removeLever(user.getHandle().getTargetBlock(null, 4)
					.getLocation());
		}
	}

}
