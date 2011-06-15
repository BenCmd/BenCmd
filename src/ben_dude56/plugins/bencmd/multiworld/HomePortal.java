package ben_dude56.plugins.bencmd.multiworld;

import org.bukkit.Location;

import ben_dude56.plugins.bencmd.BenCmd;
import ben_dude56.plugins.bencmd.User;
import ben_dude56.plugins.bencmd.permissions.PermissionGroup;
import ben_dude56.plugins.bencmd.warps.Warp;

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
