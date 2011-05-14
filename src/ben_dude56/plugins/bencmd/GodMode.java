package ben_dude56.plugins.bencmd;

import java.util.TimerTask;

import org.bukkit.entity.Player;

public class GodMode extends TimerTask {

	BenCmd plugin;

	public GodMode(BenCmd instance) {
		plugin = instance;
	}

	public void run() {
		for (Player player : plugin.godmode.keySet()) {
			player.setHealth(200);
			player.setRemainingAir(10);
		}
	}
}
