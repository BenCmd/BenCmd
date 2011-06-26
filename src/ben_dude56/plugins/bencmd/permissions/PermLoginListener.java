package ben_dude56.plugins.bencmd.permissions;

import java.util.ArrayList;

import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerPickupItemEvent;

import ben_dude56.plugins.bencmd.BenCmd;
import ben_dude56.plugins.bencmd.User;

public class PermLoginListener extends PlayerListener {

	BenCmd plugin;

	public PermLoginListener(BenCmd instance) {
		plugin = instance;
	}

	public void onPlayerLogin(PlayerLoginEvent event) {
		PermissionUser user;
		if (!plugin.perm.userFile.userExists(event.getPlayer().getName())) {
			plugin.perm.userFile.addUser(user = new PermissionUser(plugin, event.getPlayer().getName(), new ArrayList<String>()));
		} else {
			user = PermissionUser.matchUser(event.getPlayer().getName(), plugin);
		}
		if(plugin.perm.groupFile.getAllUserGroups(user).isEmpty()) {
			plugin.perm.groupFile.getGroup(plugin.mainProperties.getString("defaultGroup", "group")).addUser(user);
		}
		if ((User.getUser(plugin, event.getPlayer())).isBanned() != null) {
			event.disallow(Result.KICK_BANNED,
					"You are currently banned from this server!");
			return;
		}
		long timeLeft;
		if ((timeLeft = plugin.kicked.isBlocked(event.getPlayer().getName())) > 0) {
			event.disallow(Result.KICK_OTHER, "You cannot connect for "
					+ String.valueOf((int) Math.ceil(timeLeft / 60000.0))
					+ " more minutes...");
			return;
		}
		switch (plugin.maxPlayers.join(User.getUser(plugin, event.getPlayer()))) {
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
		User.finalizeUser(User.getUser(plugin, event.getPlayer()));
	}

	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		User user = User.getUser(plugin, event.getPlayer());
		if (user.hasPerm("isJailed", false)) {
			event.setCancelled(true);
		}
	}

	public void onPlayerDropItem(PlayerDropItemEvent event) {
		User user = User.getUser(plugin, event.getPlayer());
		if (user.hasPerm("isJailed", false)) {
			event.setCancelled(true);
		}
	}

}
