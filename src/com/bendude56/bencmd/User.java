package com.bendude56.bencmd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.bendude56.bencmd.chat.channels.ChatChannel;
import com.bendude56.bencmd.permissions.PermissionUser;
import com.bendude56.bencmd.warps.Warp;

public class User extends PermissionUser {
	private static HashMap<String, User>	activeUsers	= new HashMap<String, User>();

	public static User matchUser(String name) {
		for (Player online : Bukkit.getOnlinePlayers()) {
			if (online.getName().equalsIgnoreCase(name) || online.getDisplayName().equalsIgnoreCase(name)) {
				return User.getUser(online);
			}
		}
		return null;
	}

	public static HashMap<String, User> getActiveUsers() {
		return activeUsers;
	}

	public static User matchUserIgnoreCase(String name) {
		return User.matchUser(name);
	}

	public static void finalizeAll() {
		User.activeUsers.clear();
	}

	public static void finalizeUser(User user) {
		if (User.activeUsers.containsKey(user.getName())) {
			User.activeUsers.remove(user.getName());
		}
		assert (!User.activeUsers.containsKey(user.getName()));
	}

	public static User getUser(CommandSender s) {
		if (User.activeUsers.containsKey(s.getName())) {
			return User.activeUsers.get(s.getName());
		} else {
			return new User(s);
		}
	}

	private CommandSender		sender;
	private boolean				isConsole;
	private boolean				god;
	private ChatChannel			activeChannel;
	private List<ChatChannel>	spying;

	private User(CommandSender s) throws NullPointerException {
		super((s instanceof ConsoleCommandSender) ? "*" : s.getName(), new ArrayList<String>());
		sender = s;
		isConsole = s instanceof ConsoleCommandSender;
		if (!(s instanceof ConsoleCommandSender)) {
			setActiveChannel(null);
			spying = new ArrayList<ChatChannel>();
		}
		User.activeUsers.put(s.getName(), this);
	}

	public boolean inChannel() {
		return (getActiveChannel() != null);
	}

	public boolean joinChannel(ChatChannel channel, boolean announce) {
		if (inChannel()) {
			leaveChannel(true);
		}
		if (channel.attemptJoin(this, announce)) {
			setActiveChannel(channel);
			return true;
		} else {
			return false;
		}
	}

	public void leaveChannel(boolean announce) {
		getActiveChannel().leaveChannel(this, announce);
		setActiveChannel(null);
	}

	public ChatChannel getActiveChannel() {
		return activeChannel;
	}

	public void spyChannel(ChatChannel channel) {
		spying.add(channel);
	}

	public void unspyChannel(ChatChannel channel) {
		spying.remove(channel);
	}

	public void unspyAll() {
		for (ChatChannel channel : spying) {
			unspyChannel(channel);
		}
	}

	public void setActiveChannel(ChatChannel activeChannel) {
		this.activeChannel = activeChannel;
	}

	public void poof() {
		if (isServer()) {
			throw new UnsupportedOperationException();
		}
		BenCmd.getPoofController().addInv((Player) getHandle());
	}

	public void unPoof() {
		if (isServer()) {
			throw new UnsupportedOperationException();
		}
		BenCmd.getPoofController().remInv((Player) getHandle());
	}

	public void noPoof() {
		if (isServer()) {
			throw new UnsupportedOperationException();
		}
		BenCmd.getPoofController().addNoInv((Player) getHandle());
	}

	public void unNoPoof() {
		if (isServer()) {
			throw new UnsupportedOperationException();
		}
		BenCmd.getPoofController().remNoInv((Player) getHandle());
	}

	public void allPoof() {
		if (isServer()) {
			throw new UnsupportedOperationException();
		}
		BenCmd.getPoofController().addAInv((Player) getHandle());
	}

	public void unAllPoof() {
		if (isServer()) {
			throw new UnsupportedOperationException();
		}
		BenCmd.getPoofController().remAInv((Player) getHandle());
	}

	public boolean isPoofed() {
		if (isServer()) {
			return false;
		} else {
			return BenCmd.getPoofController().isInv((Player) getHandle());
		}
	}

	public boolean isNoPoofed() {
		if (isServer()) {
			return false;
		} else {
			return BenCmd.getPoofController().isNoInv((Player) getHandle());
		}
	}

	public boolean isAllPoofed() {
		if (isServer()) {
			return false;
		} else {
			return BenCmd.getPoofController().isAInv((Player) getHandle());
		}
	}

	public void kick(String reason, User sender) {
		if (isServer()) {
			throw new UnsupportedOperationException();
		}
		BenCmd.getPermissionManager().getKickTracker().addUser(this);
		((Player) getHandle()).kickPlayer("You have been kicked by user: " + sender.getDisplayName() + ". Reason: " + reason + ".");
	}

	public void kick(String reason) {
		if (isServer()) {
			throw new UnsupportedOperationException();
		}
		BenCmd.getPermissionManager().getKickTracker().addUser(this);
		((Player) getHandle()).kickPlayer("You have been kicked. Reason: " + reason + ".");
	}

	public void kick(User sender) {
		if (isServer()) {
			throw new UnsupportedOperationException();
		}
		BenCmd.getPermissionManager().getKickTracker().addUser(this);
		((Player) getHandle()).kickPlayer("You have been kicked by user: " + sender.getDisplayName() + ".");
	}

	public void kick() {
		if (isServer()) {
			throw new UnsupportedOperationException();
		}
		BenCmd.getPermissionManager().getKickTracker().addUser(this);
		((Player) getHandle()).kickPlayer("You have been kicked.");
	}

	public boolean kill() {
		if (isServer()) {
			throw new UnsupportedOperationException();
		}
		if (god) {
			return false;
		} else {
			((Player) getHandle()).setHealth(1);
			((Player) getHandle()).damage(1);
			return true;
		}
	}

	public void heal() {
		if (isServer()) {
			throw new UnsupportedOperationException();
		}
		((Player) getHandle()).setHealth(20);
	}

	public void feed() {
		if (isServer()) {
			throw new UnsupportedOperationException();
		}
		((Player) getHandle()).setFoodLevel(20);
		((Player) getHandle()).setSaturation(1.0F);
	}

	public void makeGod() {
		if (isServer()) {
			throw new UnsupportedOperationException();
		}
		god = true;
	}

	public void makeNonGod() {
		if (isServer()) {
			throw new UnsupportedOperationException();
		}
		god = false;
	}

	public boolean isGod() {
		if (isServer()) {
			return true;
		}
		return god;
	}

	public boolean isOffline() {
		return BenCmd.getPoofController().isOffline(this);
	}

	public void goOffline() {
		if (isServer()) {
			throw new UnsupportedOperationException();
		}
		Bukkit.broadcastMessage(this.getColor() + this.getDisplayName() + ChatColor.YELLOW + " has left the game...");
		BenCmd.getPoofController().goOffline(this);
	}

	public void goOnline() {
		if (isServer()) {
			throw new UnsupportedOperationException();
		}
		Bukkit.broadcastMessage(this.getColor() + this.getDisplayName() + ChatColor.YELLOW + " has joined the game...");
		BenCmd.getPoofController().goOnline(this);
	}

	public void goOnlineNoMsg() {
		if (isServer()) {
			throw new UnsupportedOperationException();
		}
		BenCmd.getPoofController().goOnline(this);
	}

	public String getDisplayName() {
		if (isServer()) {
			if (getPermissionUser().getName().equals("*")) {
				return "Server";
			} else {
				return getHandle().getName();
			}
		}
		return ((Player) getHandle()).getDisplayName();
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
		BenCmd.getWarps().getWarp(warpName).warpHere(this);
	}

	public void warpTo(String warpName, User sender) {
		if (isServer()) {
			throw new UnsupportedOperationException();
		}
		BenCmd.getWarps().getWarp(warpName).warpHere(this, sender);
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
		warp.warpHere(this);
	}

	public void warpTo(Warp warp, User sender) {
		if (isServer()) {
			throw new UnsupportedOperationException();
		}
		warp.warpHere(this, sender);
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

		new Warp(spawn.getX(), spawn.getY(), spawn.getZ(), spawn.getYaw(), spawn.getPitch(), spawn.getWorld().getName(), spawn.getWorld().getName() + "-spawn", "").warpHere(this);
	}

	public void sendMessage(String message) {
		sender.sendMessage(message);
	}

	public boolean isServer() {
		return !(sender instanceof Player);
	}

	public CommandSender getHandle() {
		if (isConsole) {
			return null;
		}
		return sender;
	}
}
