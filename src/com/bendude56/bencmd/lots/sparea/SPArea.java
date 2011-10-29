package com.bendude56.bencmd.lots.sparea;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.bendude56.bencmd.BenCmd;

public class SPArea {
	private Location	corner1;
	private Location	corner2;
	private Integer		AreaId;

	public SPArea(String key, String value) throws NumberFormatException, NullPointerException, IndexOutOfBoundsException {
		AreaId = Integer.parseInt(key);
		String[] splt = value.split("/")[1].split(",");
		int x, y, z;
		World w;
		x = Integer.parseInt(splt[0]);
		y = Integer.parseInt(splt[1]);
		z = Integer.parseInt(splt[2]);
		w = Bukkit.getWorld(splt[3]);
		corner1 = new Location(w, x, y, z);
		splt = value.split("/")[2].split(",");
		x = Integer.parseInt(splt[0]);
		y = Integer.parseInt(splt[1]);
		z = Integer.parseInt(splt[2]);
		w = Bukkit.getWorld(splt[3]);
		corner2 = new Location(w, x, y, z);
	}

	protected SPArea(Integer id, Location corner1, Location corner2) {
		AreaId = id;
		this.corner1 = corner1;
		this.corner2 = corner2;
	}

	public Location getCorner1() {
		return corner1;
	}

	public Location getCorner2() {
		return corner2;
	}

	public Integer getAreaID() {
		return AreaId;
	}

	public boolean insideArea(Location loc) {
		return (BenCmd.getLots().isBetween(corner1.getBlockX(), loc.getBlockX(), corner2.getBlockX()) && BenCmd.getLots().isBetween(corner1.getBlockZ(), loc.getBlockZ(), corner2.getBlockZ()) && BenCmd.getLots().isBetween(corner1.getBlockY(), loc.getBlockY(), corner2.getBlockY()));
	}

	public String getValue() {
		throw new UnsupportedOperationException("getValue() cannot be used on a generic area!");
	}

	protected String getInternalValue() {
		String value = "";
		value += "/" + corner1.getBlockX() + "," + corner1.getBlockY() + "," + corner1.getBlockZ() + "," + corner1.getWorld().getName();
		value += "/" + corner2.getBlockX() + "," + corner2.getBlockY() + "," + corner2.getBlockZ() + "," + corner2.getWorld().getName();
		value += "/";
		return value;
	}

	public List<Player> getPlayersInside() {
		List<Player> players = new ArrayList<Player>();
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (insideArea(p.getLocation())) {
				players.add(p);
			}
		}
		return players;
	}

	public void delete() {
		// Do Nothing
	}
}
