package com.bendude56.bencmd.warps;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.User;
import com.bendude56.bencmd.permissions.PermissionUser;


public class WarpableUser extends PermissionUser {
	private Player player;
	private BenCmd plugin;
	private boolean isConsole;

	public WarpableUser(BenCmd instance, Player entity)
			throws NullPointerException {
		super(instance, entity.getName(), new ArrayList<String>());
		plugin = instance;
		player = entity;
		isConsole = false;
	}

	public WarpableUser(BenCmd instance) {
		super(instance, "*", new ArrayList<String>());
		plugin = instance;
		isConsole = true;
	}

	public boolean CanWarpTo(String warpName) {
		if (isConsole) {
			return false;
		}
		return plugin.warps.getWarp(warpName).canWarpHere(this);
	}

	public void WarpTo(String warpName) {
		if (isConsole) {
			return;
		}
		plugin.warps.getWarp(warpName).WarpHere(this);
	}

	public void WarpTo(String warpName, User sender) {
		if (isConsole) {
			return;
		}
		plugin.warps.getWarp(warpName).WarpHere(this, sender.getWarpableUser());
	}

	public List<Warp> ListWarps() {
		if (this.isConsole) {
			return plugin.warps.listAllWarps();
		} else {
			return plugin.warps.listWarps(player);
		}
	}

	public void WarpTo(Warp warp) {
		if (isConsole) {
			return;
		}
		warp.WarpHere(this);
	}

	public void WarpTo(Warp warp, User sender) {
		if (isConsole) {
			return;
		}
		warp.WarpHere(this, sender.getWarpableUser());
	}

	public void HomeWarp(Integer homeNumber) {
		if (isConsole) {
			return;
		}
		plugin.homes.WarpOwnHome(player, homeNumber);
	}

	public void HomeWarp(Integer homeNumber, PermissionUser homeOf) {
		if (isConsole || homeOf.getName().equalsIgnoreCase("*")) {
			return;
		}
		plugin.homes.WarpOtherHome(player, homeOf.getName(), homeNumber);
	}

	public boolean LastCheck() {
		if (isConsole) {
			return false;
		}
		return plugin.checkpoints.returnPreWarp(player);
	}

	public void SetHome(Integer homeNumber) {
		if (isConsole) {
			return;
		}
		plugin.homes.SetOwnHome(player, homeNumber);
	}

	public void SetHome(Integer homeNumber, PermissionUser homeOf) {
		if (isConsole || homeOf.getName().equalsIgnoreCase("*")) {
			return;
		}
		plugin.homes.SetOtherHome(player, homeOf.getName(), homeNumber);
	}

	public void Spawn() {
		if (isConsole) {
			return;
		}
		if (!plugin.mainProperties.getBoolean("perWorldSpawn", false)) {
			Spawn(plugin.mainProperties.getString("defaultWorld", "world"));
		} else {
			Spawn(player.getWorld().getName());
		}
		// *ABOVE* Get the spawn location
	}

	public void Spawn(String world) {
		if (isConsole) {
			return;
		}

		Location spawn;

		try {
			spawn = plugin.getServer().getWorld(world).getSpawnLocation();
		} catch (NullPointerException e) {
			Spawn();
			return;
		}
		// Get the spawn location

		new Warp(spawn.getX(), spawn.getY(), spawn.getZ(), spawn.getYaw(),
				spawn.getPitch(), spawn.getWorld().getName(), spawn.getWorld()
						.getName() + "-spawn", "", plugin).WarpHere(this);
	}

	public WarpableUser getWarpableUser() {
		return this;
	}

	public void sendMessage(String message) {
		if (isConsole) {
			message = message.replaceAll("§.", "");
			plugin.log.info(message);
		} else {
			player.sendMessage(message);
		}
	}

	public Player getHandle() {
		if (isConsole) {
			return null;
		}
		return player;
	}
}
