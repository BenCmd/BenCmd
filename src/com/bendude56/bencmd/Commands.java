package com.bendude56.bencmd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public interface Commands {

	/**
	 * Checks the commands that this class handles and will execute the command
	 * if the user has permission to.
	 * 
	 * @param sender
	 *            The CommandSender that sent the command
	 * @param command
	 *            The command sent
	 * @param commandLabel
	 *            The name of the command to check
	 * @param args
	 *            The arguments given to the command
	 * @return Returns true if a matching command is found that the user has
	 *         permission to use, otherwise, returns false.
	 */
	public boolean onCommand(CommandSender sender, Command command,
			String commandLabel, String[] args);
}
