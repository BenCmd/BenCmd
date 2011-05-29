package ben_dude56.plugins.bencmd.chat;

import java.util.Timer;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;

import ben_dude56.plugins.bencmd.*;

public class ChatPlayerListener extends PlayerListener {
	BenCmd plugin;
	Logger log = Logger.getLogger("minecraft");
	Timer slowMode = new Timer();
	public SlowMode slow;

	public ChatPlayerListener(BenCmd instance) {
		plugin = instance;
		slow = new SlowMode(this.plugin, instance.mainProperties.getInteger(
				"slowTime", 10000));
	}

	public void ToggleSlow(User user) {
		if (slow.isEnabled()) {
			slow.DisableSlow();
			log.info(user.getName() + " has disabled slow mode.");
			plugin.getServer().broadcastMessage(
					ChatColor.GRAY + "Slow mode has been disabled.");
		} else {
			slow.EnableSlow();
			log.info(user.getName() + " has enabled slow mode.");
			plugin.getServer().broadcastMessage(
					ChatColor.GRAY
							+ "Slow mode has been enabled. You must wait "
							+ (slow.getDefTime() / 1000)
							+ " seconds between each chat message.");
		}
	}

	public void onPlayerChat(PlayerChatEvent event) {
		String message = event.getMessage();
		User user = User.getUser(plugin, event.getPlayer());
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
		if (user.isMuted()) {
			event.setCancelled(true);
			user.sendMessage(ChatColor.GRAY
					+ plugin.mainProperties.getString("muteMessage",
							"You are muted..."));
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
		if ((!user.hasPerm("ignoreSlowMode")) && slow.isEnabled()) {
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
		log.info(user.getName() + ": " + message);
		if (user.getGroup() != null
				&& !(prefix = user.getGroup().getPrefix()).isEmpty()) {
			message = user.getGroup().getPrefixColor() + "[" + prefix + "] "
					+ user.getColor() + user.getName() + ": " + ChatColor.WHITE
					+ message;
			plugin.getServer().broadcastMessage(message);
			event.setCancelled(true);
		} else {
			message = user.getColor() + user.getName() + ": " + ChatColor.WHITE
					+ message;
			plugin.getServer().broadcastMessage(message);
			event.setCancelled(true);
		}
	}

	public void onPlayerJoin(PlayerJoinEvent event) {
		User user = User.getUser(plugin, event.getPlayer());
		Player[] playerList = plugin.getServer().getOnlinePlayers();
		if (user.hasPerm("canListPlayers")) {
			if (playerList.length == 1) {
				user.sendMessage(ChatColor.GREEN
						+ "You are the only one online. :(");
			} else {
				String playerString = "";
				for (Player player2 : playerList) {
					if (User.getUser(plugin, player2).isOffline()) {
						continue;
					}
					playerString += plugin.perm.groupFile
							.getColor(plugin.perm.userFile.getGroup(player2
									.getName()))
							+ player2.getName() + ChatColor.WHITE + ", ";
				}
				user.sendMessage("The following players are online: "
						+ playerString);
			}
		}
		if (user.isMuted()) {
			user.sendMessage(ChatColor.RED
					+ "Please note that you are currently muted and cannot speak.");
		} else if (slow.isEnabled()) {
			user.sendMessage(ChatColor.RED
					+ "Please note that slow mode is currently enabled. You must wait "
					+ (slow.getDefTime() / 1000)
					+ " seconds between each chat message.");
		}
		if (user.hasPerm("isTicketAdmin") && plugin.reports.unreadTickets()) {
			user.sendMessage(ChatColor.RED
					+ "There are unread reports! Use /ticket list to see them!");
		}
		event.setJoinMessage(user.getColor() + user.getName() + ChatColor.WHITE
				+ " has joined the game...");
	}

	public void onPlayerQuit(PlayerQuitEvent event) {
		User user = User.getUser(plugin, event.getPlayer());
		if (user.isOffline()) {
			user.goOnlineNoMsg();
			event.setQuitMessage("");
		} else {
			event.setQuitMessage(user.getColor() + user.getName()
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
	}

	public void onPlayerKick(PlayerKickEvent event) {
		User user = User.getUser(plugin, event.getPlayer());
		if (user.isOffline()) {
			user.goOnlineNoMsg();
			event.setLeaveMessage("");
		} else {
			event.setLeaveMessage(user.getColor() + user.getName()
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
	}

}
