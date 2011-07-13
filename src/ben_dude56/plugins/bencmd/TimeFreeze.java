package ben_dude56.plugins.bencmd;

import java.util.TimerTask;

import org.bukkit.World;

public class TimeFreeze extends TimerTask {
	// TODO For v1.2.6: Change to Bukkit Scheduler

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
				plugin.lastTime = plugin.getServer().getWorlds().get(0)
						.getFullTime();
				return;
			}
			for (World world : plugin.getServer().getWorlds()) {
				if (world.getTime() >= 0 && world.getTime() < 12000) {
					world.setFullTime(plugin.lastTime
							+ plugin.mainProperties.getInteger("daySpeed", 100));
				} else {
					world.setFullTime(plugin.lastTime
							+ plugin.mainProperties.getInteger("nightSpeed",
									100));
				}
			}
			plugin.lastTime = plugin.getServer().getWorlds().get(0)
					.getFullTime();
		}
	}

}
