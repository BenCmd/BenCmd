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
		} else {
			if (plugin.lastTime == 0) {
				plugin.lastTime = plugin.getServer().getWorlds().get(0).getTime();
				return;
			}
			for (World world : plugin.getServer().getWorlds()) {
				if (world.getTime() >= 0 && world.getTime() < 12000) {
					world.setTime(plugin.lastTime + plugin.mainProperties.getInteger("daySpeed", 100));
				}
				else {
					world.setTime(plugin.lastTime + plugin.mainProperties.getInteger("nightSpeed", 100));
				}
			}
		}
	}

}
