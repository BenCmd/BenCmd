package com.bendude56.bencmd.multiworld;

import org.bukkit.Location;

import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.User;
import com.bendude56.bencmd.permissions.PermissionGroup;
import com.bendude56.bencmd.warps.Warp;


public class HomePortal extends Portal {

	private BenCmd plugin;
	private int num;

	public HomePortal(BenCmd instance, Location location,
			PermissionGroup allowableGroup, int homeNumber) {
		super(location, allowableGroup, null);
		plugin = instance;
		num = homeNumber;
	}

	public Warp getWarp() {
		return null;
	}

	public Warp getWarp(User user) {
		return plugin.homes.homes.getHome(user.getName() + num);
	}

	public Integer getHomeNumber() {
		return num;
	}
}
