package ben_dude56.plugins.bencmd.permissions;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;

import ben_dude56.plugins.bencmd.*;
import ben_dude56.plugins.bencmd.multiworld.Portal;
import ben_dude56.plugins.bencmd.warps.Warp;

public class BlockChecker extends BlockListener {
	BenCmd plugin;

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
		if (!user.canBuild()) {
			event.setCancelled(true);
			user.sendMessage(ChatColor.RED
					+ "You don't have permission to build.");
		}
	}

	public void onBlockBurn(BlockBurnEvent event) {
		if (plugin.mainProperties.getBoolean("allowFireDamage", false)) {
			return;
		}
		if (event.getBlock().getType() != Material.NETHERRACK) {
			event.setCancelled(true);
		}
	}

	public void onBlockIgnite(BlockIgniteEvent event) {
		if (plugin.mainProperties.getBoolean("allowFireSpread", false)) {
			return;
		}
		if (event.getCause() == IgniteCause.SPREAD) {
			if (plugin.canIgnite(event.getBlock().getLocation())) {
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

	public void onSignChange(SignChangeEvent event) {
		Location l = event.getBlock().getLocation();
		Player p = event.getPlayer();
		String[] ls = event.getLines();
		plugin.log.info(p.getDisplayName() + " put a sign at (" + l.getBlockX()
				+ ", " + l.getBlockY() + ", " + l.getBlockZ() + ", "
				+ l.getWorld().getName() + "):");
		plugin.bLog.info(p.getDisplayName() + " put a sign at ("
				+ l.getBlockX() + ", " + l.getBlockY() + ", " + l.getBlockZ()
				+ ", " + l.getWorld().getName() + "):");
		int firstLine = -1;
		for (int i = 0; i < ls.length; i++) {
			String line = ls[i];
			if (!line.isEmpty()) {
				if (firstLine == -1) {
					firstLine = i;
				}
				plugin.log.info("Line " + String.valueOf(i) + ": " + line);
				plugin.bLog.info("Line " + String.valueOf(i) + ": " + line);
			}
		}
		for (User spy : plugin.perm.userFile.allWithPerm("hearAllSigns")) {
			if (spy.getName().equals(p.getName()) || firstLine == -1) {
				continue;
			}
			spy.sendMessage(ChatColor.GRAY + p.getName() + " placed a sign: "
					+ ls[firstLine]);
		}
	}
}
