package ben_dude56.plugins.bencmd.weather;

import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;

import ben_dude56.plugins.bencmd.BenCmd;

public class WeatherPListener extends PlayerListener {
	BenCmd plugin;

	public WeatherPListener(BenCmd instance) {
		plugin = instance;
	}

	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK
				&& event.getAction() != Action.RIGHT_CLICK_AIR) {
			return;
		}
		if (plugin.strikeBind.bindEquipped(event.getPlayer())) {
			plugin.getServer().dispatchCommand(event.getPlayer(), "strike");
		}
	}
}
