package ben_dude56.plugins.bencmd.chat;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;

import ben_dude56.plugins.bencmd.*;
import ben_dude56.plugins.bencmd.advanced.Grave;
import ben_dude56.plugins.bencmd.advanced.ViewableInventory;

public class ChatPlayerListener extends PlayerListener {
	BenCmd plugin;
	public SlowMode slow;

	public ChatPlayerListener(BenCmd instance) {
		plugin = instance;
		slow = new SlowMode(this.plugin, instance.mainProperties.getInteger(
				"slowTime", 10000));
	}

	public void ToggleSlow(User user) {
		if (slow.isEnabled()) {
			slow.DisableSlow();
			plugin.log.info(user.getDisplayName() + " has disabled slow mode.");
			plugin.bLog
					.info(user.getDisplayName() + " has disabled slow mode.");
			plugin.getServer().broadcastMessage(
					ChatColor.GRAY + "Slow mode has been disabled.");
		} else {
			slow.EnableSlow();
			plugin.log.info(user.getDisplayName() + " has enabled slow mode.");
			plugin.bLog.info(user.getDisplayName() + " has enabled slow mode.");
			plugin.getServer().broadcastMessage(
					ChatColor.GRAY
							+ "Slow mode has been enabled. You must wait "
							+ (slow.getDefTime() / 1000)
							+ " seconds between each chat message.");
		}
	}

	public void onPlayerChat(PlayerChatEvent event) {
		if (plugin.mainProperties.getBoolean("externalChat", false)) {
			return;
		}
		String message = event.getMessage();
		User user = User.getUser(plugin, event.getPlayer());
		if (user.isMuted() != null) {
			event.setCancelled(true);
			user.sendMessage(ChatColor.GRAY
					+ plugin.mainProperties.getString("muteMessage",
							"You are muted..."));
			return;
		}
		if (plugin.mainProperties.getBoolean("channelsEnabled", false)) {
			if (user.inChannel()) {
				user.getActiveChannel().sendChat(user, message);
			} else {
				user.sendMessage(ChatColor.RED
						+ "You must be in a chat channel to talk!");
			}
			event.setCancelled(true);
			return;
		}
		boolean blocked = ChatChecker.checkBlocked(message, plugin);
		if (blocked) {
			event.setCancelled(true);
			user.sendMessage(ChatColor.GRAY
					+ plugin.mainProperties.getString("blockMessage",
							"You used a blocked word..."));
			return;
		}
		long slowTimeLeft = slow.playerBlocked(user.getName());
		if ((!user.hasPerm("bencmd.chat.noslow")) && slow.isEnabled()) {
			if (slowTimeLeft > 0) {
				user.sendMessage(ChatColor.GRAY
						+ "Slow mode is enabled! You must wait "
						+ (int) Math.ceil(slowTimeLeft / 1000)
						+ " more second(s) before you can talk again.");
				event.setCancelled(true);
				return;
			} else {
				slow.playerAdd(user.getName());
			}
		}
		String prefix;
		plugin.log.info(user.getDisplayName() + ": " + message);
		plugin.bLog.info(user.getDisplayName() + ": " + message);
		if (!(prefix = user.getPrefix()).isEmpty()) {
			message = user.getColor() + "[" + prefix + "] "
					+ user.getDisplayName() + ": " + ChatColor.WHITE + message;
			plugin.getServer().broadcastMessage(message);
			event.setCancelled(true);
		} else {
			message = user.getColor() + user.getDisplayName() + ": "
					+ ChatColor.WHITE + message;
			plugin.getServer().broadcastMessage(message);
			event.setCancelled(true);
		}
	}

	public void onPlayerJoin(PlayerJoinEvent event) {
		ViewableInventory.replInv((CraftPlayer) event.getPlayer());
		User user = User.getUser(plugin, event.getPlayer());
		if (BenCmd.updateAvailable && user.hasPerm("bencmd.update")) {
			user.sendMessage(ChatColor.RED
					+ "A new BenCmd update was detected! Use \"/bencmd update\" to update your server...");
		}
		Player[] playerList = plugin.getServer().getOnlinePlayers();
		if (user.hasPerm("bencmd.chat.list")) {
			if (playerList.length == 1) {
				user.sendMessage(ChatColor.GREEN
						+ "You are the only one online. :(");
			} else {
				String playerString = "";
				for (Player player2 : playerList) {
					if (User.getUser(plugin, player2).isOffline()) {
						continue;
					}
					playerString += User.getUser(plugin, player2).getColor()
							+ player2.getDisplayName() + ChatColor.WHITE + ", ";
				}
				user.sendMessage("The following players are online: "
						+ playerString);
			}
		}
		if (user.isMuted() != null) {
			user.sendMessage(ChatColor.RED
					+ "Please note that you are currently muted and cannot speak.");
		} else if (slow.isEnabled()) {
			user.sendMessage(ChatColor.RED
					+ "Please note that slow mode is currently enabled. You must wait "
					+ (slow.getDefTime() / 1000)
					+ " seconds between each chat message.");
		}
		if (user.hasPerm("bencmd.ticket.readall") && plugin.reports.unreadTickets()) {
			user.sendMessage(ChatColor.RED
					+ "There are unread reports! Use /ticket list to see them!");
		}
		plugin.getServer().dispatchCommand(user.getHandle(),
				"channel join general");
		event.setJoinMessage(user.getColor() + user.getDisplayName()
				+ ChatColor.WHITE + " has joined the game...");
		if (plugin.actions.isUnjailed(user) != null) {
			user.Spawn();
			plugin.actions.removeAction(plugin.actions.isUnjailed(user));
		}
		if (user.isJailed() != null) {
			plugin.jail.SendToJail(event.getPlayer());
		}
	}

	public void onPlayerQuit(PlayerQuitEvent event) {
		for (int i = 0; i < plugin.graves.size(); i++) {
			Grave g = plugin.graves.get(i);
			if (g.getPlayer().equals(event.getPlayer())) {
				g.delete();
				plugin.graves.remove(i);
			}
		}
		if (plugin.returns.containsKey(event.getPlayer())) {
			plugin.returns.remove(event.getPlayer());
		}
		User user = User.getUser(plugin, event.getPlayer());
		if (user.isOffline()) {
			user.goOnlineNoMsg();
			event.setQuitMessage("");
		} else {
			event.setQuitMessage(user.getColor() + user.getDisplayName()
					+ ChatColor.WHITE + " has left the game...");
		}
		plugin.maxPlayers.leave(user);
		if (user.isPoofed()) {
			user.UnPoof();
		}
		if (user.isNoPoofed()) {
			user.UnNoPoof();
		}
		if (user.isAllPoofed()) {
			user.UnAllPoof();
		}
		if (user.inChannel()) {
			user.leaveChannel();
		}
		user.unspyAll();
		User.finalizeUser(user);
	}

	public void onPlayerKick(PlayerKickEvent event) {
		for (int i = 0; i < plugin.graves.size(); i++) {
			Grave g = plugin.graves.get(i);
			if (g.getPlayer().equals(event.getPlayer())) {
				g.delete();
				plugin.graves.remove(i);
			}
		}
		User user = User.getUser(plugin, event.getPlayer());
		if (user.isOffline()) {
			user.goOnlineNoMsg();
			event.setLeaveMessage("");
		} else {
			event.setLeaveMessage(user.getColor() + user.getDisplayName()
					+ ChatColor.WHITE + " has left the game...");
		}
		plugin.maxPlayers.leave(user);
		if (user.isPoofed()) {
			user.UnPoof();
		}
		if (user.isNoPoofed()) {
			user.UnNoPoof();
		}
		if (user.inChannel()) {
			user.leaveChannel();
		}
		user.unspyAll();
		User.finalizeUser(user);
		plugin.log.info(user.getName() + " lost connection: User was kicked");
	}

}
