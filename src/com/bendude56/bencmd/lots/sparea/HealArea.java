package com.bendude56.bencmd.lots.sparea;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.bendude56.bencmd.BenCmd;


public class HealArea extends TimedArea {

	public HealArea(BenCmd instance, String key, String value)
			throws NumberFormatException, NullPointerException,
			IndexOutOfBoundsException {
		super(instance, key, value);
	}

	public HealArea(BenCmd instance, Integer id, Location corner1,
			Location corner2, Integer minimumTime) {
		super(instance, id, corner1, corner2, minimumTime);
	}

	public void tick(List<Player> players) {
		for (Player p : players) {
			if (p.getHealth() == 20 || p.isDead()) {
				continue;
			}
			p.setHealth(p.getHealth() + 1);
		}
	}

	public String getValue() {
		return "heal" + super.getInternalValue();
	}
}
