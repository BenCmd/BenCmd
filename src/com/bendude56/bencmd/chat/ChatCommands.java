package com.bendude56.bencmd.chat;

import net.minecraft.server.EntityHuman;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;

import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.Commands;
import com.bendude56.bencmd.User;
import com.bendude56.bencmd.listener.BenCmdPlayerListener;
import com.bendude56.bencmd.permissions.PermissionUser;

public class ChatCommands implements Commands {

	public boolean channelsEnabled() {
		return BenCmd.getMainProperties().getBoolean("channelsEnabled", true);
	}

	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		User user = User.getUser(sender);
		if (commandLabel.equalsIgnoreCase("tell")) {
			tell(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("list") && user.hasPerm("bencmd.chat.list")) {
			list(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("display") && user.hasPerm("bencmd.chat.imitate")) {
			User user2 = User.matchUserAllowPartial(args[0]);
			String message = "";
			for (int i = 1; i < args.length; i++) {
				String word = args[i];
				if (message == "") {
					message += word;
				} else {
					message += " " + word;
				}
			}
			BenCmd.log(user2.getName() + " is now imitating " + message + "!");
			String origName = ((EntityHuman) ((CraftPlayer) user2.getHandle()).getHandle()).name;
			((EntityHuman) ((CraftPlayer) user2.getHandle()).getHandle()).name = message;
			((Player) user2.getHandle()).setDisplayName(message);
			PlayerLoginEvent el = new PlayerLoginEvent((Player) user2.getHandle());
			Bukkit.getPluginManager().callEvent(el);
			((EntityHuman) ((CraftPlayer) user2.getHandle()).getHandle()).name = origName;
			((Player) user2.getHandle()).setDisplayName(origName);
			if (el.getResult() != Result.ALLOWED) {
				user2.sendMessage(ChatColor.RED + "Error changing name: " + el.getKickMessage());
				return true;
			}
			PlayerQuitEvent eq = new PlayerQuitEvent((Player) user2.getHandle(), "");
			Bukkit.getPluginManager().callEvent(eq);
			if (eq.getQuitMessage() != "" && eq.getQuitMessage() != null) {
				Bukkit.broadcastMessage(eq.getQuitMessage());
			}
			((EntityHuman) ((CraftPlayer) user2.getHandle()).getHandle()).name = message;
			((Player) user2.getHandle()).setDisplayName(message);
			PlayerJoinEvent ej = new PlayerJoinEvent((Player) user2.getHandle(), "");
			Bukkit.getPluginManager().callEvent(ej);
			if (ej.getJoinMessage() != "" && ej.getJoinMessage() != null) {
				Bukkit.broadcastMessage(ej.getJoinMessage());
			}
			return true;
		} else if (commandLabel.equalsIgnoreCase("ignore") && user.hasPerm("bencmd.chat.ignore")) {
			if (args.length == 0) {
				String ignoring = "";
				for (String i : user.getIgnoring()) {
					if (ignoring.isEmpty()) {
						ignoring = i;
					} else {
						ignoring += ", " + i;
					}
				}
				if (ignoring.isEmpty()) {
					user.sendMessage(ChatColor.RED + "You're not ignoring anybody!");
					return true;
				} else {
					user.sendMessage(ChatColor.GRAY + "You are ignoring the following users:");
					return true;
				}
			} else if (args.length == 1) {
				PermissionUser u = PermissionUser.matchUserAllowPartial(args[0]);
				if (u == null) {
					BenCmd.getLocale().sendMessage(user, "basic.userNotFound", args[0]);
					return true;
				}
				if (user.isIgnoring(u)) {
					user.sendMessage(ChatColor.RED + "You are already ignoring that user!");
					return true;
				}
				if (u.hasPerm("bencmd.chat.noignore")) {
					user.sendMessage(ChatColor.RED + "You cannot ignore that user!");
					return true;
				}
				user.ignore(u);
				user.sendMessage(ChatColor.GREEN + "You are now ignoring that user!");
				return true;
			} else {
				user.sendMessage(ChatColor.YELLOW + "Proper usage is: /ignore [player]");
			}
		} else if (commandLabel.equalsIgnoreCase("unignore") && user.hasPerm("bencmd.chat.ignore")) {
			if (args.length == 1) {
				PermissionUser u = PermissionUser.matchUserAllowPartial(args[0]);
				if (u == null) {
					BenCmd.getLocale().sendMessage(user, "basic.userNotFound", args[0]);
					return true;
				}
				if (!user.isIgnoring(u)) {
					user.sendMessage(ChatColor.RED + "You aren't ignoring that user!");
					return true;
				}
				user.unignore(u);
				user.sendMessage(ChatColor.GREEN + "You are no longer ignoring that user!");
				return true;
			} else {
				user.sendMessage(ChatColor.YELLOW + "Proper usage is: /unignore <player>");
			}
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
		BenCmd.log(message);
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
			BenCmd.getLocale().sendMessage(user, "basic.userNotFound", args[0]);
			return;
		}
		if (user2.getName().equalsIgnoreCase(user.getName())) {
			user.sendMessage(ChatColor.RED + "Are you trying to talk to yourself? Weirdo...");
			return;
		}
		if (user2.isIgnoring(user) && !user.hasPerm("bencmd.chat.noignore")) {
			user.sendMessage(ChatColor.RED + "That user is ignoring you...");
			return;
		}
		if (user.isIgnoring(user2) && !user2.hasPerm("bencmd.chat.noignore")) {
			user.sendMessage(ChatColor.RED + "You are ignoring that user...");
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
