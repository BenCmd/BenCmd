package com.bendude56.bencmd.lots.sparea;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.User;


public class DamageArea extends TimedArea {

	public DamageArea(BenCmd instance, String key, String value)
			throws NumberFormatException, NullPointerException,
			IndexOutOfBoundsException {
		super(instance, key, value);
	}

	public DamageArea(BenCmd instance, Integer id, Location corner1,
			Location corner2, Integer minimumTime) {
		super(instance, id, corner1, corner2, minimumTime);
	}

	public void tick(List<Player> players) {
		for (Player p : players) {
			if (p.isDead() || User.getUser(plugin, p).isGod()) {
				continue;
			}
			p.damage(1);
		}
	}

	public String getValue() {
		return "dmg" + super.getInternalValue();
	}
}
