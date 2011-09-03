package com.bendude56.bencmd.advanced.redstone;

import org.bukkit.Location;
import org.bukkit.Material;

public class RedstoneLever {
	Location l;
	LeverType f;

	public RedstoneLever(Location loc, LeverType type) {
		l = loc;
		f = type;
	}

	public boolean timeUpdate() {
		if (l.getBlock().getType() != Material.LEVER) {
			return false;
		}
		long t = l.getWorld().getTime();
		if (f == LeverType.DAY) {
			if (t >= 0 && t < 12000 && l.getBlock().getData() <= 7) {
				l.getBlock().setData((byte) (l.getBlock().getData() + 0x08));
				update();
			} else if (t >= 12000 && l.getBlock().getData() > 7) {
				l.getBlock().setData((byte) (l.getBlock().getData() - 0x08));
				update();
			}
		} else if (f == LeverType.NIGHT) {
			if (t >= 0 && t < 12000 && l.getBlock().getData() > 7) {
				l.getBlock().setData((byte) (l.getBlock().getData() - 0x08));
				update();
			} else if (t >= 12000 && l.getBlock().getData() <= 7) {
				l.getBlock().setData((byte) (l.getBlock().getData() + 0x08));
				update();
			}
		}
		return true;
	}

	public void update() {
		Location l1 = l.clone();
		l1.setY(l1.getY() + 1);
		l1.getBlock().getState().update();
		l1.setY(l1.getY() - 2);
		l1.getBlock().getState().update();
		l1.setY(l1.getY() + 1);
		l1.setX(l1.getX() + 1);
		l1.getBlock().getState().update();
		l1.setX(l1.getX() - 2);
		l1.getBlock().getState().update();
		l1.setX(l1.getX() + 1);
		l1.setZ(l1.getZ() + 1);
		l1.getBlock().getState().update();
		l1.setZ(l1.getZ() - 2);
		l1.getBlock().getState().update();
		l1.setZ(l1.getZ() + 1);
		l1.getBlock().getState().update();
	}

	public Location getLocation() {
		return l;
	}

	public String getValue() {
		return f.toString();
	}

	public enum LeverType {
		DAY, NIGHT;
		public String toString() {
			if (this == LeverType.DAY) {
				return "d";
			} else if (this == LeverType.NIGHT) {
				return "n";
			} else {
				return "";
			}
		}
	}
}
