package com.bendude56.bencmd;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.World;

public class TimeManager {

	private HashMap<World, Boolean>	timeRunning;
	private HashMap<World, Long>	lastTime;

	public TimeManager() {
		timeRunning = new HashMap<World, Boolean>();
		lastTime = new HashMap<World, Long>();
		for (World w : Bukkit.getWorlds()) {
			timeRunning.put(w, true);
			lastTime.put(w, w.getFullTime());
		}
	}

	protected void tick() {
		for (World w : Bukkit.getWorlds()) {
			if (!timeRunning.containsKey(w)) {
				timeRunning.put(w, true);
				lastTime.put(w, w.getFullTime());
				continue;
			}
			if (timeRunning.get(w)) {
				if (w.getTime() >= 0 && w.getTime() < 12000) {
					w.setFullTime(lastTime.get(w) + BenCmd.getMainProperties().getInteger("daySpeed", 100));
				} else {
					w.setFullTime(lastTime.get(w) + BenCmd.getMainProperties().getInteger("nightSpeed", 100));
				}
				lastTime.put(w, w.getFullTime());
			} else {
				w.setTime(lastTime.get(w) % 24000);
			}
		}
	}

	public void setFrozen(World w, boolean frozen) {
		timeRunning.put(w, !frozen);
	}

	public boolean isFrozen(World w) {
		return !timeRunning.get(w);
	}

	public void syncLastTime(World w) {
		lastTime.put(w, w.getFullTime());
	}
}
