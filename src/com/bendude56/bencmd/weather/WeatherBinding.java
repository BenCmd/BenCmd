package com.bendude56.bencmd.weather;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.bendude56.bencmd.invtools.InventoryBackend;

public class WeatherBinding {
	private HashMap<Player, Material>	bindings	= new HashMap<Player, Material>();

	public WeatherBinding() {}

	public void clearBinding(Player player) {
		if (bindings.containsKey(player)) {
			bindings.remove(player);
		}
	}

	public void clearBindings() {
		bindings.clear();
	}

	public boolean tryBind(Player player) {
		if (InventoryBackend.getInstance().canBind(player.getItemInHand().getType())) {
			bindings.put(player, player.getItemInHand().getType());
			return true;
		} else {
			return false;
		}
	}

	public boolean bindEquipped(Player player) {
		if (!bindings.containsKey(player)) {
			return false;
		}
		if (player.getItemInHand().getType() == bindings.get(player)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean hasBoundItem(Player player) {
		return bindings.containsKey(player);
	}
}
