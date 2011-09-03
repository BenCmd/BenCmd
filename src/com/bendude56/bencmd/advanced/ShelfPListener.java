package com.bendude56.bencmd.advanced;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;

import com.bendude56.bencmd.BenCmd;


public class ShelfPListener extends PlayerListener {
	private BenCmd plugin;

	public ShelfPListener(BenCmd instance) {
		plugin = instance;
	}

	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK
				&& event.getClickedBlock().getType() == Material.BOOKSHELF) {
			Shelf shelf;
			if ((shelf = plugin.shelff.getShelf(event.getClickedBlock()
					.getLocation())) != null) {
				event.setCancelled(true);
				event.getPlayer().sendMessage(
						ChatColor.YELLOW + "The books on this shelf read:");
				event.getPlayer().sendMessage(
						ChatColor.YELLOW + shelf.getText());
			}
		}
	}
}
