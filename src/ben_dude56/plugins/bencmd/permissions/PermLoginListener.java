package ben_dude56.plugins.bencmd.permissions;

import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerPickupItemEvent;

import ben_dude56.plugins.bencmd.ActionableUser;
import ben_dude56.plugins.bencmd.BenCmd;
import ben_dude56.plugins.bencmd.User;

public class PermLoginListener extends PlayerListener {

	BenCmd plugin;

	public PermLoginListener(BenCmd instance) {
		plugin = instance;
	}

	public void onPlayerLogin(PlayerLoginEvent event) {
		if (!plugin.perm.userFile.userExists(event.getPlayer().getName())) {
			plugin.perm.userFile.addUser(event.getPlayer().getName());
		}
		if ((new ActionableUser(plugin, event.getPlayer())).hasPerm("isBanned",
				false)) {
			event.disallow(Result.KICK_BANNED,
					"You are currently banned from this server!");
			return;
		}
		long timeLeft;
		if((timeLeft = plugin.kicked.isBlocked(event.getPlayer().getName())) > 0) {
			event.disallow(Result.KICK_OTHER, "You cannot connect for " + String.valueOf(Math.ceil(timeLeft / 60000.0)) + " more minutes...");
			return;
		}
		switch (plugin.maxPlayers.join(new User(plugin, event.getPlayer()))) {
		case NO_SLOT_NORMAL:
			event.disallow(Result.KICK_FULL, plugin.mainProperties.getString(
					"noNormal",
					"There are no normal slots currently available!"));
			break;
		case NO_SLOT_RESERVED:
			event.disallow(
					Result.KICK_FULL,
					plugin.mainProperties
							.getString("noReserved",
									"There are no normal slots or reserved slots currently available!"));
			break;
		}
	}

	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		User user = new User(plugin, event.getPlayer());
		if (user.hasPerm("isJailed", false)) {
			event.setCancelled(true);
		}
	}

	public void onPlayerDropItem(PlayerDropItemEvent event) {
		User user = new User(plugin, event.getPlayer());
		if (user.hasPerm("isJailed", false)) {
			event.setCancelled(true);
		}
	}

}
