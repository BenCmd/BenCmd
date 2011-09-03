package com.bendude56.bencmd.advanced;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.Commands;
import com.bendude56.bencmd.User;


public class AdvancedCommands implements Commands {

	private BenCmd plugin;

	public AdvancedCommands(BenCmd instance) {
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
		if (commandLabel.equalsIgnoreCase("write")) {
			Write(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("inv")
				&& user.hasPerm("bencmd.inv.look")) {
			Inv(args, user);
			return true;
		}
		return false;
	}

	public void Write(String[] args, User user) {
		if (user.getHandle().getTargetBlock(null, 4).getType() != Material.BOOKSHELF) {
			user.sendMessage(ChatColor.RED
					+ "You're not pointing at a bookshelf!");
			return;
		}
		if (!plugin.lots.canBuildHere(user.getHandle(), user.getHandle()
				.getTargetBlock(null, 4).getLocation())) {
			user.sendMessage(ChatColor.RED
					+ "You're not allowed to do that here!");
			return;
		}
		String message = "";
		for (int i = 0; i < args.length; i++) {
			String word = args[i];
			if (message == "") {
				message += word;
			} else {
				message += " " + word;
			}
		}
		plugin.shelff.addShelf(new Shelf(user.getHandle()
				.getTargetBlock(null, 4).getLocation(), message));
		user.sendMessage(ChatColor.GREEN
				+ "Magically, writing appears on that shelf.");
	}

	public void Inv(String[] args, User user) {
		if (args.length != 1) {
			user.sendMessage(ChatColor.YELLOW + "Proper usage: /inv <player>");
			return;
		}
		User target;
		if ((target = User.matchUserIgnoreCase(args[0], plugin)) == null) {
			user.sendMessage(ChatColor.RED + "That player isn't online!");
			return;
		}
		if (target.hasPerm("bencmd.inv.protect") && !user.hasPerm("bencmd.inv.all")) {
			user.sendMessage(ChatColor.RED + "That player's inventory is protected!");
			return;
		}
		if (!(((CraftPlayer) target.getHandle()).getHandle().inventory instanceof ViewableInventory)) {
			ViewableInventory.replInv((CraftPlayer) target.getHandle());
		}
		((CraftPlayer) user.getHandle()).getHandle().a(
				((CraftPlayer) target.getHandle()).getHandle().inventory);
	}
}
