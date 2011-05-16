package ben_dude56.plugins.bencmd.weather;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import ben_dude56.plugins.bencmd.BenCmd;
import ben_dude56.plugins.bencmd.invtools.InventoryBackend;

public class WeatherBinding {
	private HashMap<Player, Material> bindings = new HashMap<Player, Material>();
	BenCmd plugin;
	InventoryBackend back;

	public WeatherBinding(BenCmd instance) {
		plugin = instance;
		back = new InventoryBackend(plugin);
	}

	public void clearBinding(Player player) {
		if (bindings.containsKey(player)) {
			bindings.remove(player);
		}
	}

	public void clearBindings() {
		bindings.clear();
	}

	public boolean tryBind(Player player) {
		if (back.canBind(player.getItemInHand().getType())) {
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
