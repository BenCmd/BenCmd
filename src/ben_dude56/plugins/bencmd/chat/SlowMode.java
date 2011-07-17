package ben_dude56.plugins.bencmd.chat;

import java.util.Date;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import ben_dude56.plugins.bencmd.BenCmd;

public class SlowMode {
	BenCmd plugin;
	private boolean enabled;
	private Integer defTime;
	private Integer origDefTime;
	public HashMap<String, Long> playerList = new HashMap<String, Long>();
	Logger log = Logger.getLogger("minecraft");

	public int getDefTime() {
		return defTime;
	}

	public void DisableSlow() {
		playerList.clear();
		enabled = false;
		defTime = origDefTime;
	}

	public void EnableSlow() {
		enabled = true;
	}

	public void EnableSlow(int millis) {
		enabled = true;
		defTime = millis;
		if (plugin.mainProperties.getBoolean("channelsEnabled", false)) {
			return;
		}
		plugin.getServer().broadcastMessage(
				ChatColor.GRAY + "Slow mode has been enabled. You must wait "
						+ (defTime / 1000)
						+ " seconds between each chat message.");
	}

	public boolean isEnabled() {
		return enabled;
	}

	public SlowMode(BenCmd instance, Integer defaultTime) {
		plugin = instance;
		defTime = defaultTime;
		origDefTime = defaultTime;
		enabled = false;
		Bukkit.getServer()
				.getScheduler()
				.scheduleAsyncRepeatingTask(plugin, new SlowModeTimer(this), 2,
						2);
	}

	public void playerAdd(String player) {
		if (enabled) {
			playerList.put(player, new Date().getTime() + defTime);
		}
	}

	public long playerBlocked(String player) {
		if (playerList.containsKey(player)) {
			return playerList.get(player) - new Date().getTime();
		} else {
			return 0;
		}
	}

	public void runCheck() {
		for (int i = 0; i < playerList.size(); i++) {
			String playerName = (String) playerList.keySet().toArray()[i];
			Long timeDone = (Long) playerList.values().toArray()[i];
			if (new Date().getTime() >= timeDone) {
				playerList.remove(playerName);
			}
		}
	}

	public class SlowModeTimer implements Runnable {
		SlowMode parent;

		public SlowModeTimer(SlowMode instance) {
			parent = instance;
		}

		public void run() {
			if (parent.isEnabled()) {
				runCheck();
			}
		}
	}
}
