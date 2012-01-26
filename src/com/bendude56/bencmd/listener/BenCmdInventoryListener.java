package com.bendude56.bencmd.listener;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.*;
import org.bukkit.plugin.*;
import org.getspout.spoutapi.event.inventory.InventoryCraftEvent;

import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.User;

public class BenCmdInventoryListener implements Listener, EventExecutor {

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
		instance = null;
	}

	private BenCmdInventoryListener() {
		InventoryCraftEvent.getHandlerList().register(new RegisteredListener(this, this, EventPriority.NORMAL, BenCmd.getPlugin()));
	}

	public void checkCraft(InventoryCraftEvent event) {
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
	
	// Split-off events

	@Override
	public void execute(Listener listener, Event event) throws EventException {
		if (event instanceof InventoryCraftEvent) {
			InventoryCraftEvent e = (InventoryCraftEvent) event;
			checkCraft(e);
		}
	}
}
