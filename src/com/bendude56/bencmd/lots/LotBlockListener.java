package com.bendude56.bencmd.lots;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.User;


public class LotBlockListener extends BlockListener {

	Player player;
	Location loc;
	BenCmd plugin;

	public LotBlockListener(BenCmd instance) {
		plugin = instance;
	}

	public void onBlockBreak(BlockBreakEvent event) {

		Player player = event.getPlayer();
		User user = User.getUser(plugin, player);

		if (player.getItemInHand().getType() == Material.WOOD_SPADE
				&& user.hasPerm("bencmd.lot.select")) {
			event.setCancelled(true);
		}

		if (!plugin.lots.canBuildHere(player, event.getBlock().getLocation())) {
			event.setCancelled(true);
			player.sendMessage("You cannot build here.");
		}
	}

	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();

		if (!plugin.lots.canBuildHere(player, event.getBlock().getLocation())) {
			event.setCancelled(true);
			player.sendMessage("You cannot build here.");
		}
	}
}