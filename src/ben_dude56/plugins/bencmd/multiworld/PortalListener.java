package ben_dude56.plugins.bencmd.multiworld;

import org.bukkit.Location;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerPortalEvent;

import ben_dude56.plugins.bencmd.BenCmd;

public class PortalListener extends PlayerListener {
	BenCmd plugin;
	//TODO For version 1.2.0: Add customizable portals
	
	public PortalListener(BenCmd instance) {
		plugin = instance;
	}
	
	public void onPlayerPortal(PlayerPortalEvent event) {
		Location loc = getHandleBlock(event.getFrom());
		plugin.log.info(loc.toString());
		event.setCancelled(true);
		event.getPlayer().teleport(new Location(plugin.getServer().getWorld("world"), 0, 64, 0));
	}
	
	public Location getHandleBlock(Location loc) {
		return loc;
	}
}
