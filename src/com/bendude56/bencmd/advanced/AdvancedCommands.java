package com.bendude56.bencmd.advanced;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.Commands;
import com.bendude56.bencmd.User;

public class AdvancedCommands implements Commands {

	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		User user = User.getUser(sender);
		if (commandLabel.equalsIgnoreCase("write")) {
			Write(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("inv") && user.hasPerm("bencmd.inv.look")) {
			Inv(args, user);
			return true;
		}
		return false;
	}

	public void Write(String[] args, User user) {
		if (user.isServer()) {
			BenCmd.getLocale().sendMessage(user, "basic.noServerUse");
			return;
		}
		if (((Player) user.getHandle()).getTargetBlock(null, 4).getType() != Material.BOOKSHELF) {
			BenCmd.getLocale().sendMessage(user, "command.write.notAShelf");
			return;
		}
		Location l = ((Player) user.getHandle()).getTargetBlock(null, 4).getLocation();
		if (!BenCmd.getLots().canBuildHere(((Player) user.getHandle()), l)) {
			BenCmd.getLocale().sendMessage(user, "command.write.notAllowed");
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
		BenCmd.getShelfFile().addShelf(new Shelf(((Player) user.getHandle()).getTargetBlock(null, 4).getLocation(), message));
		BenCmd.getLocale().sendMessage(user, "command.write.success");
		BenCmd.log(BenCmd.getLocale().getString("log.write.success", user.getName(), l.getX() + "", l.getY() + "", l.getZ() + "", l.getWorld().getName(), message));
	}

	public void Inv(String[] args, User user) {
		if (user.isServer()) {
			BenCmd.getLocale().sendMessage(user, "basic.noServerUse");
			return;
		}
		if (args.length != 1) {
			BenCmd.showUse(user, "inv");
			return;
		}
		User target;
		if ((target = User.matchUserAllowPartial(args[0])) == null) {
			BenCmd.getLocale().sendMessage(user, "basic.userNotFound", args[0]);
			return;
		}
		if (target.hasPerm("bencmd.inv.protect") && !user.hasPerm("bencmd.inv.all")) {
			BenCmd.getLocale().sendMessage(user, "command.inv.protected", args[0]);
			return;
		}
		if (!(((CraftPlayer) target.getHandle()).getHandle().inventory instanceof ViewableInventory)) {
			ViewableInventory.replInv((CraftPlayer) target.getHandle());
		}
		BenCmd.log(BenCmd.getLocale().getString("log.inv.look", user.getName(), target.getName()));
		((CraftPlayer) user.getHandle()).getHandle().a(((CraftPlayer) target.getHandle()).getHandle().inventory);
	}
}
