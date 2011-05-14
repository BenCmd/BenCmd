package ben_dude56.plugins.bencmd.chat;

import java.util.HashMap;
import java.util.Timer;
import java.util.logging.Logger;

import org.bukkit.ChatColor;

import ben_dude56.plugins.bencmd.BenCmd;
import ben_dude56.plugins.bencmd.User;

public class SlowMode {
	BenCmd plugin;
	private boolean enabled;
	private Integer defTime;
	private Integer origDefTime;
	public HashMap<String, Integer> playerList = new HashMap<String, Integer>();
	public Timer slowTimer = new Timer();
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

	public void EnableSlow(int millis, User user) {
		enabled = true;
		defTime = millis;
		log.info(user.getName() + " has enabled slow mode.");
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
		enabled = plugin.mainProperties.getBoolean("slowByDefault", false);
		slowTimer.schedule(new SlowModeTimer(this), 0, 1);
	}

	public void playerAdd(String player) {
		if (enabled) {
			playerList.put(player, defTime);
		}
	}

	public int playerBlocked(String player) {
		if (playerList.containsKey(player)) {
			return playerList.get(player);
		} else {
			return 0;
		}
	}

	public void run() {
		for (int i = 0; i < playerList.size(); i++) {
			String playerName = (String) playerList.keySet().toArray()[i];
			Integer timeLeft = (Integer) playerList.values().toArray()[i];
			timeLeft--;
			if (timeLeft <= 0) {
				playerList.remove(playerName);
			} else {
				playerList.put(playerName, timeLeft);
			}
		}
	}
}
