package com.bendude56.bencmd.warps;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.User;
import com.bendude56.bencmd.permissions.PermissionUser;

public class WarpableUser extends PermissionUser {
	private CommandSender	sender;
	private boolean			isConsole;

	public WarpableUser(CommandSender s) throws NullPointerException {
		super(s.getName(), new ArrayList<String>());
		sender = s;
		isConsole = false;
	}

	public WarpableUser() {
		super("*", new ArrayList<String>());
		isConsole = true;
	}

	public boolean canWarpTo(String warpName) {
		if (isServer()) {
			throw new UnsupportedOperationException();
		}
		return BenCmd.getWarps().getWarp(warpName).canWarpHere(this);
	}

	public void warpTo(String warpName) {
		if (isServer()) {
			throw new UnsupportedOperationException();
		}
		BenCmd.getWarps().getWarp(warpName).WarpHere(this);
	}

	public void warpTo(String warpName, User sender) {
		if (isServer()) {
			throw new UnsupportedOperationException();
		}
		BenCmd.getWarps().getWarp(warpName).WarpHere(this, sender.getWarpableUser());
	}

	public List<Warp> listWarps() {
		if (isServer()) {
			return BenCmd.getWarps().listAllWarps();
		} else {
			return BenCmd.getWarps().listWarps((Player) sender);
		}
	}

	public void warpTo(Warp warp) {
		if (isServer()) {
			throw new UnsupportedOperationException();
		}
		warp.WarpHere(this);
	}

	public void warpTo(Warp warp, User sender) {
		if (isServer()) {
			throw new UnsupportedOperationException();
		}
		warp.WarpHere(this, sender.getWarpableUser());
	}

	public void homeWarp(Integer homeNumber) {
		if (isServer()) {
			throw new UnsupportedOperationException();
		}
		BenCmd.getHomes().WarpOwnHome((Player) sender, homeNumber);
	}

	public void homeWarp(Integer homeNumber, PermissionUser homeOf) {
		if (isServer() || homeOf.getName().equalsIgnoreCase("*")) {
			throw new UnsupportedOperationException();
		}
		BenCmd.getHomes().WarpOtherHome((Player) sender, homeOf.getName(), homeNumber);
	}

	public boolean lastCheck() {
		if (isServer()) {
			throw new UnsupportedOperationException();
		}
		return BenCmd.getWarpCheckpoints().returnPreWarp((Player) sender);
	}

	public void setHome(Integer homeNumber) {
		if (isServer()) {
			throw new UnsupportedOperationException();
		}
		BenCmd.getHomes().SetOwnHome((Player) sender, homeNumber);
	}

	public void setHome(Integer homeNumber, PermissionUser homeOf) {
		if (isServer() || homeOf.getName().equalsIgnoreCase("*")) {
			throw new UnsupportedOperationException();
		}
		BenCmd.getHomes().SetOtherHome((Player) sender, homeOf.getName(), homeNumber);
	}

	public void spawn() {
		if (isServer()) {
			throw new UnsupportedOperationException();
		}
		if (!BenCmd.getMainProperties().getBoolean("perWorldSpawn", false)) {
			spawn(BenCmd.getMainProperties().getString("defaultWorld", "world"));
		} else {
			spawn(((Player) sender).getWorld().getName());
		}
	}

	public void spawn(String world) {
		if (isServer()) {
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

		new Warp(spawn.getX(), spawn.getY(), spawn.getZ(), spawn.getYaw(), spawn.getPitch(), spawn.getWorld().getName(), spawn.getWorld().getName() + "-spawn", "").WarpHere(this);
	}

	public WarpableUser getWarpableUser() {
		return this;
	}

	public void sendMessage(String message) {
		if (isConsole) {
			message = message.replaceAll("§.", "");
			BenCmd.log(message);
		} else {
			sender.sendMessage(message);
		}
	}

	public boolean isServer() {
		return super.isServer() || !(sender instanceof Player);
	}

	public CommandSender getHandle() {
		if (isConsole) {
			return null;
		}
		return sender;
	}
}
