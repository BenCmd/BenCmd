package com.bendude56.bencmd.permissions;

import org.bukkit.Location;

import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.PluginProperties;
import com.bendude56.bencmd.warps.Warp;

public class MainPermissions {
	private UserFile userFile;
	private GroupFile groupFile;
	private ItemBW itemList;
	private ActionFile action;
	private ActionLog alog;
	private KickList kick;
	private MaxPlayers mp;
	private Warp jail;

	public MainPermissions() {
		PluginProperties prop = BenCmd.getMainProperties();
		userFile = new UserFile();
		groupFile = new GroupFile();
		itemList = new ItemBW();
		action = new ActionFile();
		alog = new ActionLog(BenCmd.propDir + "action.log");
		kick = new KickList();
		mp = new MaxPlayers(prop.getInteger(
				"maxPlayers", 10), prop.getInteger("maxReserve", 4),
				prop.getBoolean("reserveActive", true),
				prop.getBoolean("indefActive", true));
		loadJail();
	}
	
	private void loadJail() {
		String str = BenCmd.getMainProperties().getString("jailLocation",
		"0,0,0,0.0,0.0,world");
		double x = Integer.parseInt(str.split(",")[0]);
		double y = Integer.parseInt(str.split(",")[1]);
		double z = Integer.parseInt(str.split(",")[2]);
		double yaw = Double.parseDouble(str.split(",")[3]);
		double pitch = Double.parseDouble(str.split(",")[4]);
		String world = str.split(",")[5];
		jail = new Warp(x, y, z, yaw, pitch, world, "jail", "");
	}
	
	public UserFile getUserFile() {
		return userFile;
	}
	
	public GroupFile getGroupFile() {
		return groupFile;
	}
	
	public ItemBW getItemLists() {
		return itemList;
	}
	
	public ActionFile getActionFile() {
		return action;
	}
	
	public ActionLog getActionLog() {
		return alog;
	}
	
	public KickList getKickTracker() {
		return kick;
	}
	
	public MaxPlayers getMaxPlayerHandler() {
		return mp;
	}
	
	public Warp getJailWarp() {
		return jail;
	}
	
	public void setJailWarp(Location l) {
		jail = new Warp(l, "jail", "");
		int x = (int) l.getX();
		int y = (int) l.getY();
		int z = (int) l.getZ();
		Double yaw = (double) l.getYaw();
		Double pitch = (double) l.getPitch();
		String world = l.getWorld().getName();
		BenCmd.getMainProperties().setProperty("jailLocation", x + "," + y + "," + z
				+ "," + yaw.toString() + "," + pitch.toString() + "," + world);
		BenCmd.getMainProperties().saveFile("-BenCmd Main Config-");
	}
	
	public void saveAll() {
		userFile.saveAll();
		groupFile.saveAll();
		action.saveAll();
		setJailWarp(jail.loc);
	}
}
