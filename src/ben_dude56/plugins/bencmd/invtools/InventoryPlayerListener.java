package ben_dude56.plugins.bencmd.invtools;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.block.CraftChest;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;

import ben_dude56.plugins.bencmd.BenCmd;

public class InventoryPlayerListener extends PlayerListener {
	BenCmd plugin;
	InventoryBackend back;

	public InventoryPlayerListener(BenCmd instance) {
		plugin = instance;
		back = new InventoryBackend(plugin);
	}

	public void onPlayerInteract(PlayerInteractEvent event) {
		if (!(event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
			return;
		}
		Block block = event.getClickedBlock();
		Player player = event.getPlayer();
		if (back.TryDispense(block)) {
			event.setCancelled(true);
		}
		if (block.getType() == Material.CHEST
				&& plugin.chests.isDisposalChest(block.getLocation())) {
			CraftChest chest = new CraftChest(block);
			chest.getInventory().clear();
			player.sendMessage(ChatColor.RED
					+ "ALERT: The chest you have opened is a disposal chest! Anything you put inside will disappear FOREVER!");
		}
	}

	public void onRedstoneChange(Block block, int oldLevel, int newLevel) {

	}
}
