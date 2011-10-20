package com.bendude56.bencmd.warps;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.User;
import com.bendude56.bencmd.permissions.PermissionUser;


public class WarpableUser extends PermissionUser {
	private Player player;
	private boolean isConsole;

	public WarpableUser(Player entity)
			throws NullPointerException {
		super(entity.getName(), new ArrayList<String>());
		player = entity;
		isConsole = false;
	}

	public WarpableUser() {
		super("*", new ArrayList<String>());
		isConsole = true;
	}

	public boolean canWarpTo(String warpName) {
		if (isConsole) {
			throw new UnsupportedOperationException();
		}
		return BenCmd.getWarps().getWarp(warpName).canWarpHere(this);
	}

	public void warpTo(String warpName) {
		if (isConsole) {
			throw new UnsupportedOperationException();
		}
		BenCmd.getWarps().getWarp(warpName).WarpHere(this);
	}

	public void warpTo(String warpName, User sender) {
		if (isConsole) {
			throw new UnsupportedOperationException();
		}
		BenCmd.getWarps().getWarp(warpName).WarpHere(this, sender.getWarpableUser());
	}

	public List<Warp> listWarps() {
		if (this.isConsole) {
			return BenCmd.getWarps().listAllWarps();
		} else {
			return BenCmd.getWarps().listWarps(player);
		}
	}

	public void warpTo(Warp warp) {
		if (isConsole) {
			throw new UnsupportedOperationException();
		}
		warp.WarpHere(this);
	}

	public void warpTo(Warp warp, User sender) {
		if (isConsole) {
			throw new UnsupportedOperationException();
		}
		warp.WarpHere(this, sender.getWarpableUser());
	}

	public void homeWarp(Integer homeNumber) {
		if (isConsole) {
			throw new UnsupportedOperationException();
		}
		BenCmd.getHomes().WarpOwnHome(player, homeNumber);
	}

	public void homeWarp(Integer homeNumber, PermissionUser homeOf) {
		if (isConsole || homeOf.getName().equalsIgnoreCase("*")) {
			throw new UnsupportedOperationException();
		}
		BenCmd.getHomes().WarpOtherHome(player, homeOf.getName(), homeNumber);
	}

	public boolean lastCheck() {
		if (isConsole) {
			throw new UnsupportedOperationException();
		}
		return BenCmd.getWarpCheckpoints().returnPreWarp(player);
	}

	public void setHome(Integer homeNumber) {
		if (isConsole) {
			throw new UnsupportedOperationException();
		}
		BenCmd.getHomes().SetOwnHome(player, homeNumber);
	}

	public void setHome(Integer homeNumber, PermissionUser homeOf) {
		if (isConsole || homeOf.getName().equalsIgnoreCase("*")) {
			throw new UnsupportedOperationException();
		}
		BenCmd.getHomes().SetOtherHome(player, homeOf.getName(), homeNumber);
	}

	public void spawn() {
		if (isConsole) {
			throw new UnsupportedOperationException();
		}
		if (!BenCmd.getMainProperties().getBoolean("perWorldSpawn", false)) {
			spawn(BenCmd.getMainProperties().getString("defaultWorld", "world"));
		} else {
			spawn(player.getWorld().getName());
		}
	}

	public void spawn(String world) {
		if (isConsole) {
			throw new UnsupportedOperationException();
		}
		Location spawn;
		try {
			spawn = Bukkit.getWorld(world).getSpawnLocation();
		} catch (NullPointerException e) {
			spawn();
			return;
		}
		// Get the spawn location

		new Warp(spawn.getX(), spawn.getY(), spawn.getZ(), spawn.getYaw(),
				spawn.getPitch(), spawn.getWorld().getName(), spawn.getWorld()
						.getName() + "-spawn", "").WarpHere(this);
	}

	public WarpableUser getWarpableUser() {
		return this;
	}

	public void sendMessage(String message) {
		if (isConsole) {
			message = message.replaceAll("§.", "");
			BenCmd.log(message);
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
