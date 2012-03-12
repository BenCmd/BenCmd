package com.bendude56.bencmd.chat;

import java.util.Date;
import java.util.HashMap;

import org.bukkit.Bukkit;

import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.User;

public class SlowMode {

	private boolean					enabled;
	private Integer					defTime;
	private Integer					origDefTime;
	public HashMap<String, Long>	playerList	= new HashMap<String, Long>();

	public int getDefTime() {
		return defTime;
	}

	public void disableSlow() {
		playerList.clear();
		enabled = false;
		defTime = origDefTime;
	}

	public void enableSlow() {
		enabled = true;
	}

	public void enableSlow(int millis) {
		enabled = true;
		defTime = millis;
	}
	
	public void toggleSlow(User user) {
		if (isEnabled()) {
			disableSlow();
		} else {
			enableSlow();
		}
	}

	public boolean isEnabled() {
		return enabled;
	}

	public SlowMode(Integer defaultTime) {
		defTime = defaultTime;
		origDefTime = defaultTime;
		enabled = false;
		Bukkit.getScheduler().scheduleAsyncRepeatingTask(BenCmd.getPlugin(), new SlowModeTimer(), 2, 2);
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
