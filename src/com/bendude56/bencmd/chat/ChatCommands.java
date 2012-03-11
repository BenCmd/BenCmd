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
import com.bendude56.bencmd.permissions.PermissionUser;

public class ChatCommands implements Commands {

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
			BenCmd.log(BenCmd.getLocale().getString("log.display.success", user2.getName(), message));
			String origName = ((EntityHuman) ((CraftPlayer) user2.getHandle()).getHandle()).name;
			((EntityHuman) ((CraftPlayer) user2.getHandle()).getHandle()).name = message;
			PlayerLoginEvent el = new PlayerLoginEvent((Player) user2.getHandle());
			Bukkit.getPluginManager().callEvent(el);
			((EntityHuman) ((CraftPlayer) user2.getHandle()).getHandle()).name = origName;
			((Player) user2.getHandle()).setDisplayName(origName);
			if (el.getResult() != Result.ALLOWED) {
				BenCmd.getLocale().sendMessage(user, "command.display.error", el.getKickMessage());
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
			BenCmd.getLocale().sendMessage(user2, "command.display.success", message);
			return true;
		} else if (commandLabel.equalsIgnoreCase("ignore") && user.hasPerm("bencmd.chat.ignore")) {
			if (user.isServer()) {
				BenCmd.getLocale().sendMessage(user, "basic.noServerUse");
				return true;
			}
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
					BenCmd.getLocale().sendMessage(user, "command.ignore.listNone");
					return true;
				} else {
					BenCmd.getLocale().sendMessage(user, "command.ignore.list");
					return true;
				}
			} else if (args.length == 1) {
				PermissionUser u = PermissionUser.matchUserAllowPartial(args[0]);
				if (u == null) {
					BenCmd.getLocale().sendMessage(user, "basic.userNotFound", args[0]);
					return true;
				}
				if (user.isIgnoring(u)) {
					BenCmd.getLocale().sendMessage(user, "command.ignore.alreadyIgnoring");
					return true;
				}
				if (u.hasPerm("bencmd.chat.noignore")) {
					BenCmd.getLocale().sendMessage(user, "command.ignore.cannotIgnore");
					return true;
				}
				user.ignore(u);
				BenCmd.getLocale().sendMessage(user, "command.ignore.success", u.getName());
				return true;
			} else {
				BenCmd.showUse(user, "ignore");
			}
		} else if (commandLabel.equalsIgnoreCase("unignore") && user.hasPerm("bencmd.chat.ignore")) {
			if (user.isServer()) {
				BenCmd.getLocale().sendMessage(user, "basic.noServerUse");
				return true;
			}
			if (args.length == 1) {
				PermissionUser u = PermissionUser.matchUserAllowPartial(args[0]);
				if (u == null) {
					BenCmd.getLocale().sendMessage(user, "basic.userNotFound", args[0]);
					return true;
				}
				if (!user.isIgnoring(u)) {
					BenCmd.getLocale().sendMessage(user, "command.unignore.notIgnoring");
					return true;
				}
				user.unignore(u);
				BenCmd.getLocale().sendMessage(user, "command.unignore.success", u.getName());
				return true;
			} else {
				BenCmd.showUse(user, "unignore");
			}
		}
		return false;
	}

	public void list(String[] args, User user) {
		Player[] playerList = Bukkit.getOnlinePlayers();
		if ((playerList.length == 1 && !user.isServer()) || playerList.length == 0) {
			BenCmd.getLocale().sendMessage(user, "command.list.empty");
		} else {
			String playerString = "";
			for (Player player2 : playerList) {
				User user2 = User.getUser(player2);
				if (user2.isOffline() && !user.isServer()) {
					continue;
				}
				playerString += user2.getColor() + user2.getName() + ChatColor.WHITE + ", ";
			}
			BenCmd.getLocale().sendMessage(user, "command.list.list");
			user.sendMessage(playerString);
		}
	}

	public void tell(String[] args, User user) {
		if (args.length <= 1) {
			BenCmd.showUse(user, "tell");
			return;
		}
		if (user.isMuted() != null) {
			BenCmd.getLocale().sendMessage(user, "command.tell.muted");
			return;
		}
		User user2;
		if (args[0].equalsIgnoreCase("server")) {
			user2 = User.getUser(Bukkit.getServer().getConsoleSender());
		} else if ((user2 = User.matchUser(args[0])) == null) {
			BenCmd.getLocale().sendMessage(user, "basic.userNotFound", args[0]);
			return;
		}
		if (user2.getName().equalsIgnoreCase(user.getName())) {
			BenCmd.getLocale().sendMessage(user, "command.tell.tellSpy");
			return;
		}
		if (user2.isIgnoring(user) && !user.hasPerm("bencmd.chat.noignore")) {
			BenCmd.getLocale().sendMessage(user, "command.tell.ignore.other");
			return;
		}
		if (user.isIgnoring(user2) && !user2.hasPerm("bencmd.chat.noignore")) {
			BenCmd.getLocale().sendMessage(user, "command.tell.ignore.self");
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
			BenCmd.getLocale().sendMessage(user, "misc.channel.noTalk.block");
			return;
		}
		long slowTimeLeft = SlowMode.getInstance().playerBlocked(user.getName());
		if (!user.hasPerm("bencmd.chat.noslow") && SlowMode.getInstance().isEnabled()) {
			if (slowTimeLeft > 0) {
				BenCmd.getLocale().sendMessage(user, "misc.channel.noTalk.slow", ((int) Math.ceil(slowTimeLeft / 1000)) + "");
				return;
			} else {
				SlowMode.getInstance().playerAdd(user.getName());
			}
		}
		String name1 = ((user.isDev()) ? ChatColor.DARK_GREEN + "*" : "") + user.getColor() + user.getDisplayName();
		String name2 = ((user2.isDev()) ? ChatColor.DARK_GREEN + "*" : "") + user2.getColor() + user2.getDisplayName();
		BenCmd.getLocale().sendMessage(user2, "command.tell.message", name1, BenCmd.getLocale().getString("command.tell.you"), message);
		BenCmd.getLocale().sendMessage(user, "command.tell.message", BenCmd.getLocale().getString("command.tell.you"), name2, message);
		for (User spy : BenCmd.getPermissionManager().getUserFile().allWithPerm("bencmd.chat.tellspy")) {
			if (spy.getName().equals(user.getName()) || spy.getName().equals(user2.getName())) {
				continue;
			}
			BenCmd.getLocale().sendMessage(spy, "command.tell.message", name1, name2, message);
		}
		BenCmd.log(BenCmd.getLocale().getString("log.tell.message", user.getName(), user2.getName(), message));
	}
}
