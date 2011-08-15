package ben_dude56.plugins.bencmd.invtools;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.block.CraftChest;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.inventory.ItemStack;

import ben_dude56.plugins.bencmd.BenCmd;
import ben_dude56.plugins.bencmd.User;

public class InventoryBlockListener extends BlockListener {
	BenCmd plugin;
	InventoryBackend back;

	public InventoryBlockListener(BenCmd instance) {
		plugin = instance;
		back = new InventoryBackend(plugin);
	}

	public void onBlockBreak(BlockBreakEvent event) {
		if (event.isCancelled()) {
			return;
		}
		Block block = event.getBlock();
		Player player = event.getPlayer();
		if (block.getType() == Material.DISPENSER
				&& plugin.dispensers.isUnlimitedDispenser(block.getLocation())) {
			if (!User.getUser(plugin, player).hasPerm("bencmd.inv.unlimited.remove")) {
				player.sendMessage(ChatColor.RED
						+ "You can't destroy unlimited dispensers!");
				event.setCancelled(true);
				return;
			}
			plugin.dispensers.removeDispenser(block.getLocation());
			player.sendMessage(ChatColor.RED
					+ "You destroyed an unlimited dispenser!");
		}
		if (block.getType() == Material.CHEST
				&& plugin.chests.isDisposalChest(block.getLocation())) {
			if (!User.getUser(plugin, player).hasPerm("bencmd.inv.disposal.remove")) {
				player.sendMessage(ChatColor.RED
						+ "You can't destroy disposal chests!");
				event.setCancelled(true);
				return;
			}
			event.setCancelled(true);
			new CraftChest(block).getInventory().clear();
			block.setType(Material.AIR);
			block.getWorld().dropItemNaturally(block.getLocation(),
					new ItemStack(Material.CHEST, 1));
			plugin.chests.removeDispenser(block.getLocation());
			player.sendMessage(ChatColor.RED
					+ "You destroyed a disposal chest!");
		}
	}

	public void onBlockRedstoneChange(BlockRedstoneEvent event) {
		if (!(event.getOldCurrent() == 0) || !(event.getNewCurrent() > 0)
				|| !plugin.mainProperties.getBoolean("redstoneUnlDisp", true)) {
			return;
		}
		Block block = event.getBlock();
		Block block2;
		block2 = block.getWorld().getBlockAt(block.getX() + 1, block.getY(),
				block.getZ());
		if (back.TryDispense(block2)) {
			return;
		}
		block2 = block.getWorld().getBlockAt(block.getX() - 1, block.getY(),
				block.getZ());
		if (back.TryDispense(block2)) {
			return;
		}
		block2 = block.getWorld().getBlockAt(block.getX(), block.getY(),
				block.getZ() + 1);
		if (back.TryDispense(block2)) {
			return;
		}
		block2 = block.getWorld().getBlockAt(block.getX(), block.getY(),
				block.getZ() - 1);
		if (back.TryDispense(block2)) {
			return;
		}
		block2 = block.getWorld().getBlockAt(block.getX(), block.getY() + 1,
				block.getZ());
		if (back.TryDispense(block2)) {
			return;
		}
	}
}
