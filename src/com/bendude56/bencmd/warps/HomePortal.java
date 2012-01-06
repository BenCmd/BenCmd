package com.bendude56.bencmd.warps;

import org.bukkit.Location;

import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.User;
import com.bendude56.bencmd.permissions.PermissionGroup;

public class HomePortal extends Portal {

	private int	num;

	public HomePortal(Location location, PermissionGroup allowableGroup, int homeNumber) {
		super(location, allowableGroup, null);
		num = homeNumber;
	}

	public Warp getWarp() {
		return null;
	}

	public Warp getWarp(User user) {
		return BenCmd.getHomes().homes.getHome(user.getName() + num);
	}

	public Integer getHomeNumber() {
		return num;
	}
}
