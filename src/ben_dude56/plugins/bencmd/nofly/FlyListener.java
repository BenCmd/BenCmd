package ben_dude56.plugins.bencmd.nofly;

import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import ben_dude56.plugins.bencmd.BenCmd;
import ben_dude56.plugins.bencmd.advanced.npc.NPC;

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
			for (NPC n : BenCmd.getPlugin().npcs.allNPCs()) {
				if (n.isSpawned() && event.getTo().distance(n.getCurrentLocation()) < 50) {
					plugin.spoutconnect.sendSkin(event.getPlayer(), n.getEntityId(), n.getSkinURL());
				}
			}
		}
	}

	public void onPlayerPortal(PlayerPortalEvent event) {
		if (event.isCancelled()) {
			return;
		}
		plugin.flyDetect.lastL.put(event.getPlayer(), event.getTo());
		if (plugin.spoutcraft) { 
			for (NPC n : BenCmd.getPlugin().npcs.allNPCs()) {
				if (n.isSpawned() && event.getTo().distance(n.getCurrentLocation()) < 50) {
					plugin.spoutconnect.sendSkin(event.getPlayer(), n.getEntityId(), n.getSkinURL());
				}
			}
		}
	}
}
