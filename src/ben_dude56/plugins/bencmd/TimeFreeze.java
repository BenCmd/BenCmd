package ben_dude56.plugins.bencmd;

import java.util.TimerTask;

import org.bukkit.World;

public class TimeFreeze extends TimerTask {

	private BenCmd plugin;

	public TimeFreeze(BenCmd instance) {
		plugin = instance;
	}

	public void run() {
		if (!plugin.timeRunning) {
			for (World world : plugin.getServer().getWorlds()) {
				world.setTime(plugin.timeFrozenAt);
			}
		}
	}

}
