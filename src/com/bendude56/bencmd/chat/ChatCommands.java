package com.bendude56.bencmd.chat;

import net.minecraft.server.EntityHuman;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.Commands;
import com.bendude56.bencmd.User;
import com.bendude56.bencmd.listener.BenCmdPlayerListener;

public class ChatCommands implements Commands {

	public boolean channelsEnabled() {
		return BenCmd.getMainProperties().getBoolean("channelsEnabled", false);
	}

	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		User user;
		try {
			user = User.getUser((Player) sender);
		} catch (ClassCastException e) {
			user = User.getUser();
		}
		if (commandLabel.equalsIgnoreCase("tell")) {
			tell(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("list") && user.hasPerm("bencmd.chat.list")) {
			list(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("display") && user.hasPerm("bencmd.chat.imitate")) {
			User user2 = User.matchUser(args[0]);
			String message = "";
			for (int i = 1; i < args.length; i++) {
				String word = args[i];
				if (message == "") {
					message += word;
				} else {
					message += " " + word;
				}
			}
			((Player) user.getHandle()).setDisplayName(message);
			BenCmd.log(user2.getName() + " is now imitating " + message + "!");
			((EntityHuman) ((CraftPlayer) user2.getHandle()).getHandle()).name = message;
			return true;
		}
		if (channelsEnabled()) {
			return false;
		}
		if (commandLabel.equalsIgnoreCase("slow") && user.hasPerm("bencmd.chat.slow")) {
			slowMode(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("me")) {
			me(args, user);
			return true;
		}
		return false;
	}

	public void slowMode(String[] args, User user) {
		if ((!SlowMode.getInstance().isEnabled()) && args.length > 0) {
			int millis;
			try {
				millis = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				user.sendMessage(ChatColor.RED + "Invalid delay!");
				return;
			}
			SlowMode.getInstance().EnableSlow(millis);
		} else {
			BenCmdPlayerListener.getInstance().ToggleSlow(user);
		}
	}

	public void list(String[] args, User user) {
		Player[] playerList = Bukkit.getOnlinePlayers();
		if (playerList.length == 1 && !user.isServer()) {
			user.sendMessage(ChatColor.GREEN + "You are the only one online. :(");
		} else if (playerList.length == 0) {
			user.sendMessage("The server is empty. :(");
		} else {
			String playerString = "";
			for (Player player2 : playerList) {
				User user2 = User.getUser(player2);
				if (user2.isOffline() && !user.isServer()) {
					continue;
				}
				playerString += user2.getColor() + user2.getName() + ChatColor.WHITE + ", ";
			}
			user.sendMessage("The following players are online:(" + playerList.length + ")");
			user.sendMessage(playerString);
		}
	}

	public void me(String[] args, User user) {
		if (args.length == 0) {
			user.sendMessage(ChatColor.YELLOW + "Proper usage: /me <message>");
			return;
		}
		if (user.isMuted() != null) {
			user.sendMessage(ChatColor.GRAY + BenCmd.getMainProperties().getString("muteMessage", "You are muted..."));
			return;
		}
		String message = "";
		for (String word : args) {
			if (message == "") {
				message += word;
			} else {
				message += " " + word;
			}
		}
		boolean blocked = ChatChecker.checkBlocked(message);
		if (blocked) {
			user.sendMessage(ChatColor.GRAY + BenCmd.getMainProperties().getString("blockMessage", "You used a blocked word..."));
			return;
		}
		long slowTimeLeft = SlowMode.getInstance().playerBlocked(user.getName());
		if (!user.hasPerm("bencmd.chat.noslow") && SlowMode.getInstance().isEnabled()) {
			if (slowTimeLeft > 0) {
				user.sendMessage(ChatColor.GRAY + "Slow mode is enabled! You must wait " + (int) Math.ceil(slowTimeLeft / 1000) + " more second(s) before you can talk again.");
				return;
			} else {
				SlowMode.getInstance().playerAdd(user.getName());
			}
		}
		message = ChatColor.WHITE + "*" + user.getColor() + user.getName() + " " + ChatColor.WHITE + message;
		Bukkit.broadcastMessage(message);
		User.getUser().sendMessage(message);
	}

	public void tell(String[] args, User user) {
		if (args.length <= 1) {
			user.sendMessage(ChatColor.YELLOW + "Proper usage: /tell <player> <message>");
			return;
		}
		if (user.isMuted() != null) {
			user.sendMessage(ChatColor.GRAY + BenCmd.getMainProperties().getString("muteMessage", "You are muted..."));
			return;
		}
		User user2;
		if ((user2 = User.matchUser(args[0])) == null) {
			user.sendMessage(ChatColor.RED + "That user doesn't exist!");
			return;
		}
		if (user2.getName().equalsIgnoreCase(user.getName())) {
			user.sendMessage(ChatColor.RED + "Are you trying to talk to yourself? Weirdo...");
			return;
		}
		String message = "";
		for (int i = 0; i < args.length; i++) {
			if (i == 0) {
				continue;
			}
			String word = args[i];
			if (message == "") {
				message += word;
			} else {
				message += " " + word;
			}
		}
		boolean blocked = ChatChecker.checkBlocked(message);
		if (blocked) {
			user.sendMessage(ChatColor.GRAY + BenCmd.getMainProperties().getString("blockMessage", "You used a blocked word..."));
			return;
		}
		long slowTimeLeft = SlowMode.getInstance().playerBlocked(user.getName());
		if (!user.hasPerm("bencmd.chat.noslow") && SlowMode.getInstance().isEnabled()) {
			if (slowTimeLeft > 0) {
				user.sendMessage(ChatColor.GRAY + "Slow mode is enabled! You must wait " + (int) Math.ceil(slowTimeLeft / 1000) + " more second(s) before you can talk again.");
				return;
			} else {
				SlowMode.getInstance().playerAdd(user.getName());
			}
		}
		user2.sendMessage(ChatColor.GRAY + "(" + ((user.isDev()) ? ChatColor.DARK_GREEN + "*" : "") + user.getColor() + user.getDisplayName() + ChatColor.GRAY + " => You) " + message);
		user.sendMessage(ChatColor.GRAY + "(You => " + ((user2.isDev()) ? ChatColor.DARK_GREEN + "*" : "") + user2.getColor() + user2.getDisplayName() + ChatColor.GRAY + ") " + message);
		for (User spy : BenCmd.getPermissionManager().getUserFile().allWithPerm("bencmd.chat.tellspy")) {
			if (spy.getName().equals(user.getName()) || spy.getName().equals(user2.getName())) {
				continue;
			}
			spy.sendMessage(ChatColor.GRAY + "(" + ((user.isDev()) ? ChatColor.DARK_GREEN + "*" : "") + user.getColor() + user.getDisplayName() + ChatColor.GRAY + " => " + ((user2.isDev()) ? ChatColor.DARK_GREEN + "*" : "") + user2.getColor() + user2.getDisplayName() + ChatColor.GRAY + ") " + message);
		}
		BenCmd.log("(" + user.getDisplayName() + " => " + user2.getDisplayName() + ") " + message);
	}
}
