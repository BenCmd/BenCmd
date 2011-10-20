package com.bendude56.bencmd.lots;

import org.bukkit.Location;


public class Corner {

	Location corner1, corner2;
	public boolean corner1set, corner2set;

	public Corner() {

		corner1set = false;
		corner2set = false;

	}

	public void setCorner1(Location loc) {
		corner1 = loc;
		if (corner2set && !corner1.getWorld().equals(corner2.getWorld())) {
			corner2 = null;
			corner2set = false;
		}
		corner1set = true;
	}

	public void setCorner2(Location loc) {
		corner2 = loc;
		if (corner2set && !corner1.getWorld().equals(corner2.getWorld())) {
			corner1 = null;
			corner1set = false;
		}
		corner2set = true;
	}

	public Location getCorner1() {
		if (corner1set)
			return corner1;

		else
			return null;
	}

	public Location getCorner2() {
		if (corner2set)
			return corner2;

		else
			return null;
	}

}
