package com.bendude56.bencmd.warps;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.Commands;
import com.bendude56.bencmd.User;
import com.bendude56.bencmd.permissions.PermissionGroup;

public class PortalCommands implements Commands {

	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		User user = User.getUser(sender);
		if (commandLabel.equalsIgnoreCase("setportal") && user.hasPerm("bencmd.portal.set")) {
			SetPortal(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("remportal") && user.hasPerm("bencmd.portal.remove")) {
			RemPortal(args, user);
			return true;
		}
		return false;
	}

	public void RemPortal(String[] args, User user) {
		Block pointedAt = ((Player) user.getHandle()).getTargetBlock(null, 4);
		if (pointedAt.getType() != Material.PORTAL) {
			user.sendMessage(ChatColor.RED + "You're not pointing at a portal!");
		}
		Location handle = Portal.getHandleBlock(pointedAt.getLocation());
		BenCmd.getPortalFile().remPortal(handle);
		user.sendMessage(ChatColor.GREEN + "That portal was successfully removed!");
	}

	public void SetPortal(String[] args, User user) {
		if (args.length == 0) {
			user.sendMessage(ChatColor.YELLOW + "Proper use is /setportal <warp> [group]");
		} else {
			Block pointedAt = ((Player) user.getHandle()).getTargetBlock(null, 4);
			if (pointedAt.getType() != Material.PORTAL) {
				user.sendMessage(ChatColor.RED + "You're not pointing at a portal!");
			}
			Location handle = Portal.getHandleBlock(pointedAt.getLocation());
			Warp warp = null;
			Integer homeNumber = null;
			if (args[0].startsWith("home")) {
				try {
					homeNumber = Integer.parseInt(args[0].replaceFirst("home", ""));
				} catch (NumberFormatException e) {
					user.sendMessage(ChatColor.RED + args[0].replaceFirst("home", "") + " cannot be converted into a number!");
					return;
				}
			} else if ((warp = BenCmd.getWarps().getWarp(args[0])) == null) {
				user.sendMessage(ChatColor.RED + "That warp doesn't exist!");
				return;
			}
			PermissionGroup group = null;
			if (args.length == 2) {
				try {
					group = BenCmd.getPermissionManager().getGroupFile().getGroup(args[1]);
				} catch (NullPointerException e) {
					user.sendMessage(ChatColor.RED + "That group doesn't exist!");
					return;
				}
			}
			if (homeNumber == null) {
				BenCmd.getPortalFile().addPortal(new Portal(handle, group, warp));
				user.sendMessage(ChatColor.GREEN + "That portal has been set to warp " + warp.warpName + "!");
			} else {
				BenCmd.getPortalFile().addPortal(new HomePortal(handle, group, homeNumber));
				user.sendMessage(ChatColor.GREEN + "That portal has been set to home #" + homeNumber + "!");
			}
		}
	}

}
