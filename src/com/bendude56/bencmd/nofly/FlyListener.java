package com.bendude56.bencmd.nofly;

import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.bendude56.bencmd.BenCmd;


public class FlyListener extends PlayerListener {
	private BenCmd plugin;

	public FlyListener(BenCmd instance) {
		plugin = instance;
	}

	public void onPlayerTeleport(PlayerTeleportEvent event) {
		if (event.isCancelled()) {
			return;
		}
		plugin.flyDetect.lastL.put(event.getPlayer(), event.getTo());
		if (plugin.spoutcraft) { 
			plugin.spaplisten.sendSkins(event.getPlayer(), event.getTo());
		}
	}

	public void onPlayerPortal(PlayerPortalEvent event) {
		if (event.isCancelled()) {
			return;
		}
		plugin.flyDetect.lastL.put(event.getPlayer(), event.getTo());
		if (plugin.spoutcraft) { 
			plugin.spaplisten.sendSkins(event.getPlayer(), event.getTo());
		}
	}
}
