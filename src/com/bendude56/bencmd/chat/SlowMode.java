package com.bendude56.bencmd.chat;

import java.util.Date;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import com.bendude56.bencmd.BenCmd;


public class SlowMode {
	
	private static SlowMode instance = null;
	
	public static SlowMode getInstance() {
		if (instance == null) {
			return instance = new SlowMode(BenCmd.getMainProperties().getInteger(
				"slowTime", 1000));
		} else {
			return instance;
		}
	}
	
	public static SlowMode newUnhandledInstance() {
		return new SlowMode(BenCmd.getMainProperties().getInteger(
				"slowTime", 1000));
	}
	
	public static void destroyInstance() {
		instance = null;
	}
	
	private boolean enabled;
	private Integer defTime;
	private Integer origDefTime;
	public HashMap<String, Long> playerList = new HashMap<String, Long>();

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
		if (BenCmd.getMainProperties().getBoolean("channelsEnabled", false)) {
			return;
		}
		Bukkit.broadcastMessage(
				ChatColor.GRAY + "Slow mode has been enabled. You must wait "
						+ (defTime / 1000)
						+ " seconds between each chat message.");
	}

	public boolean isEnabled() {
		return enabled;
	}

	private SlowMode(Integer defaultTime) {
		defTime = defaultTime;
		origDefTime = defaultTime;
		enabled = false;
		Bukkit.getScheduler()
				.scheduleAsyncRepeatingTask(BenCmd.getPlugin(), new SlowModeTimer(), 2,
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

		public void run() {
			if (isEnabled()) {
				runCheck();
			}
		}
	}
}
