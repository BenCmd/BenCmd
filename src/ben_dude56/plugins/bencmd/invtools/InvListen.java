package ben_dude56.plugins.bencmd.invtools;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.getspout.spoutapi.event.inventory.InventoryCraftEvent;
import org.getspout.spoutapi.event.inventory.InventoryListener;

import ben_dude56.plugins.bencmd.BenCmd;
import ben_dude56.plugins.bencmd.User;

public class InvListen extends InventoryListener {
	private BenCmd plugin;
	
	public InvListen(BenCmd instance) {
		plugin = instance;
	}
	
	public void onInventoryCraft(InventoryCraftEvent event) {
		User user = User.getUser(plugin, event.getPlayer());
		Material m = event.getResult().getType();
		if (user.hasPerm("bencmd.inv.craft.disallow." + m.getId(), false) && !user.hasPerm("bencmd.inv.craft.override")) {
			if (plugin.spoutconnect.enabled(event.getPlayer())) {
				plugin.spoutconnect.sendNotification(event.getPlayer(), "Item disabled", "You can't craft that!", m);
			} else {
				user.sendMessage(ChatColor.RED + "Crafting of that item has been disabled.");
				user.sendMessage(ChatColor.RED + "Please see an administrator for more information.");
			}
			event.setCancelled(true);
			return;
		}
	}
}
