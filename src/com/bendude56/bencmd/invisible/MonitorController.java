package com.bendude56.bencmd.invisible;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.User;

public class MonitorController {
	private HashMap<Player, Player> currentMonitoring;
	private HashMap<Player, Location> previousLocation;
	
	public MonitorController() {
		currentMonitoring = new HashMap<Player, Player>();
		previousLocation = new HashMap<Player, Location>();
	}

	public void cancelMonitor(Player p) {
		if (isMonitoring(p)) {
			BenCmd.getPoofController().uninvisible(currentMonitoring.get(p), p);
			currentMonitoring.remove(p);
			p.teleport(previousLocation.get(p), TeleportCause.PLUGIN);
			previousLocation.remove(p);
		}
	}

	public void setMonitor(Player p1, Player p2) {
		User u = User.getUser(p1);
		if (!u.isPoofed()) {
			u.poof();
		}
		if (!isMonitoring(p1)) {
			previousLocation.put(p1,  p1.getLocation());
		}
		currentMonitoring.put(p1, p2);
		p1.teleport(p2.getLocation());
		BenCmd.getPoofController().invisible(p2, p1);
	}
	
	public void playerMove(Player p) {
		for (Map.Entry<Player, Player> monitor : currentMonitoring.entrySet()) {
			if (monitor.getValue().equals(p)) {
				monitor.getKey().teleport(monitor.getValue().getLocation());
			}
		}
	}

	public boolean isMonitoring(Player p) {
		return currentMonitoring.containsKey(p);
	}
	
	public void disconnect(Player p) {
		for (Map.Entry<Player, Player> monitor : currentMonitoring.entrySet()) {
			if (monitor.getKey().equals(p) || monitor.getValue().equals(p)) {
				cancelMonitor(monitor.getKey());
			}
		}
	}
}
