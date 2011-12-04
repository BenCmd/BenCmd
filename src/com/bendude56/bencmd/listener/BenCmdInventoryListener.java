package com.bendude56.bencmd.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.getspout.spoutapi.event.inventory.InventoryCraftEvent;
import org.getspout.spoutapi.event.inventory.InventoryListener;

import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.User;

public class BenCmdInventoryListener extends InventoryListener {

	// Singleton instancing

	private static BenCmdInventoryListener	instance	= null;

	public static BenCmdInventoryListener getInstance() {
		if (instance == null) {
			return instance = new BenCmdInventoryListener();
		} else {
			return instance;
		}
	}

	public static void destroyInstance() {
		instance.enabled = false;
		instance = null;
	}
	
	private boolean enabled = true;

	private BenCmdInventoryListener() {
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvent(Event.Type.CUSTOM_EVENT, this, Event.Priority.Normal, BenCmd.getPlugin());
	}

	public void onInventoryCraft(InventoryCraftEvent event) {
		if (!enabled) {
			return;
		}
		User user = User.getUser(event.getPlayer());
		Material m = event.getResult().getType();
		if (user.hasPerm("bencmd.inv.craft.disallow." + m.getId(), false) && !user.hasPerm("bencmd.inv.craft.override")) {
			if (BenCmd.getSpoutConnector().enabled(event.getPlayer())) {
				BenCmd.getSpoutConnector().sendNotification(event.getPlayer(), "Item disabled", "You can't craft that!", m);
			} else {
				user.sendMessage(ChatColor.RED + "Crafting of that item has been disabled.");
				user.sendMessage(ChatColor.RED + "Please see an administrator for more information.");
			}
			event.setCancelled(true);
			return;
		}
	}
}
