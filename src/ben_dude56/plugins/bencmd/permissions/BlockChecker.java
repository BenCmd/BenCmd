package ben_dude56.plugins.bencmd.permissions;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

import ben_dude56.plugins.bencmd.*;
import ben_dude56.plugins.bencmd.multiworld.Portal;
import ben_dude56.plugins.bencmd.warps.Warp;

import java.util.logging.Logger;

public class BlockChecker extends BlockListener {
	BenCmd plugin;
	static final Logger log = Logger.getLogger("minecraft");

	public BlockChecker(BenCmd instance) {
		plugin = instance;
	}

	public void onBlockPlace(BlockPlaceEvent event) {
		User user = User.getUser(plugin, event.getPlayer());
		if (!user.canBuild()) {
			event.setCancelled(true);
			user.sendMessage(ChatColor.RED
					+ "You don't have permission to build.");
			return;
		}
		Warp warpTo;
		if (event.getBlockPlaced().getType() == Material.PORTAL
				&& (warpTo = plugin.warps.getWarp(plugin.mainProperties
						.getString("defaultPortalWarp", "portals"))) != null) {
			plugin.portals.addPortal(new Portal(Portal.getHandleBlock(event
					.getBlockPlaced().getLocation()), null, warpTo));
		}
	}

	public void onBlockBreak(BlockBreakEvent event) {
		User user = User.getUser(plugin, event.getPlayer());
		if (event.getBlock().getType() == Material.TNT
				&& user.getHandle().getItemInHand().getType() == Material.WATER) {
			event.getBlock().setType(Material.AIR);
			event.setCancelled(true);
		} else if (event.getBlock().getType() == Material.TNT) {
			String logMessage = user.getDisplayName() + " tried to detonate TNT at X:"
					+ event.getBlock().getX() + "  Y:"
					+ event.getBlock().getY() + "  Z:"
					+ event.getBlock().getZ();
			log.info(logMessage);
			plugin.getServer().broadcastMessage(
					ChatColor.RED + user.getDisplayName() + " tried to detonate TNT!");
			user.Kick(plugin.mainProperties.getString("TNTKick",
					"You can't detonate TNT!"));
			event.setCancelled(true);
		}
		if (!user.canBuild()) {
			event.setCancelled(true);
			user.sendMessage(ChatColor.RED
					+ "You don't have permission to build.");
		}
	}

	public void onBlockBurn(BlockBurnEvent event) {
		if (event.getBlock().getType() != Material.NETHERRACK) {
			event.setCancelled(true);
		}
	}

	public void onBlockIgnite(BlockIgniteEvent event) {
		if(event.getCause() == IgniteCause.SPREAD) {
			if(plugin.canIgnite(event.getBlock().getLocation())) {
				plugin.canSpread.add(event.getBlock().getLocation());
				event.setCancelled(false);
			} else {
				event.setCancelled(true);
			}
			return;
		}
		try {
			User user = User.getUser(plugin, event.getPlayer());
			Material material = user
					.getHandle()
					.getWorld()
					.getBlockAt(event.getBlock().getX(),
							event.getBlock().getY() - 1,
							event.getBlock().getZ()).getType();
			if (event.getBlock().getLocation().getBlockY() <= 0) {
				event.setCancelled(true);
				return;
			}
			if (!user.hasPerm("canBurn", false)
					&& material != Material.AIR
					&& material != Material.NETHERRACK
					&& user.getHandle().getTargetBlock(null, 4).getType() != Material.NETHERRACK) {
				event.setCancelled(true);
			}
			if (user.hasPerm("canBurnAll")) {
				event.setCancelled(false);
			}
		} catch (NullPointerException e) {
			event.setCancelled(true);
			return;
		}
	}
}
