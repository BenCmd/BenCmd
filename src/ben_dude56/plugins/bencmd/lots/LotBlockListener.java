package ben_dude56.plugins.bencmd.lots;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

import ben_dude56.plugins.bencmd.BenCmd;
import ben_dude56.plugins.bencmd.User;

public class LotBlockListener extends BlockListener {

	Player player;
	Location loc;
	BenCmd plugin;

	public LotBlockListener(BenCmd instance) {
		plugin = instance;
	}

	public void onBlockBreak(BlockBreakEvent event) {
		
		if (player.getItemInHand().getType() == Material.WOOD_SPADE && User.getUser(plugin, player).hasPerm("isLandlord")) {
			event.setCancelled(true);
		}
		
		Player player = event.getPlayer();
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
