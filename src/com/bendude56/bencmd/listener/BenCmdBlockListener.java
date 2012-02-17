package com.bendude56.bencmd.listener;

import java.util.Date;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.block.CraftChest;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.*;

import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.User;
import com.bendude56.bencmd.advanced.Grave;
import com.bendude56.bencmd.advanced.Shelf;
import com.bendude56.bencmd.invtools.InventoryBackend;
import com.bendude56.bencmd.protect.ProtectedBlock;
import com.bendude56.bencmd.recording.RecordEntry.BlockBreakEntry;
import com.bendude56.bencmd.recording.RecordEntry.BlockPlaceEntry;
import com.bendude56.bencmd.warps.Portal;
import com.bendude56.bencmd.warps.Warp;

public class BenCmdBlockListener implements EventExecutor, Listener {

	// Singleton instancing

	private static BenCmdBlockListener	instance	= null;

	public static BenCmdBlockListener getInstance() {
		if (instance == null) {
			return instance = new BenCmdBlockListener();
		} else {
			return instance;
		}
	}

	public static void destroyInstance() {
		instance = null;
	}

	private BenCmdBlockListener() {
		BlockBreakEvent.getHandlerList().register(new RegisteredListener(this, this, EventPriority.LOWEST, BenCmd.getPlugin(), false));
		BlockPlaceEvent.getHandlerList().register(new RegisteredListener(this, this, EventPriority.LOWEST, BenCmd.getPlugin(), false));
		BlockIgniteEvent.getHandlerList().register(new RegisteredListener(this, this, EventPriority.NORMAL, BenCmd.getPlugin(), false));
		BlockBurnEvent.getHandlerList().register(new RegisteredListener(this, this, EventPriority.NORMAL, BenCmd.getPlugin(), false));
		SignChangeEvent.getHandlerList().register(new RegisteredListener(this, this, EventPriority.MONITOR, BenCmd.getPlugin(), false));
		BlockRedstoneEvent.getHandlerList().register(new RegisteredListener(this, this, EventPriority.MONITOR, BenCmd.getPlugin(), false));
	}

	private void bookshelfBreak(BlockBreakEvent event) {
		for (int i = 0; i < Grave.graves.size(); i++) {
			if (Grave.graves.get(i).getBlock().getLocation().equals(event.getBlock().getLocation())) {
				event.setCancelled(true);
				Grave.graves.get(i).destroyBy(event.getPlayer());
				return;
			}
		}
		if (event.isCancelled()) {
			return;
		}
		Shelf shelf;
		if ((shelf = BenCmd.getShelfFile().getShelf(event.getBlock().getLocation())) != null) {
			BenCmd.getShelfFile().remShelf(shelf.getLocation());
		}
	}

	private void dcudDestroy(BlockBreakEvent event) {
		if (event.isCancelled()) {
			return;
		}
		Block block = event.getBlock();
		Player player = event.getPlayer();
		if (block.getType() == Material.DISPENSER && BenCmd.getDispensers().isUnlimitedDispenser(block.getLocation())) {
			if (!User.getUser(player).hasPerm("bencmd.inv.unlimited.remove")) {
				player.sendMessage(ChatColor.RED + "You can't destroy unlimited dispensers!");
				event.setCancelled(true);
				return;
			}
			BenCmd.getDispensers().removeDispenser(block.getLocation());
			player.sendMessage(ChatColor.RED + "You destroyed an unlimited dispenser!");
		}
		if (block.getType() == Material.CHEST && BenCmd.getDisposals().isDisposalChest(block.getLocation())) {
			if (!User.getUser(player).hasPerm("bencmd.inv.disposal.remove")) {
				player.sendMessage(ChatColor.RED + "You can't destroy disposal chests!");
				event.setCancelled(true);
				return;
			}
			event.setCancelled(true);
			new CraftChest(block).getInventory().clear();
			block.setType(Material.AIR);
			block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.CHEST, 1));
			BenCmd.getDisposals().removeDispenser(block.getLocation());
			player.sendMessage(ChatColor.RED + "You destroyed a disposal chest!");
		}
	}

	private void unlDispRedstone(BlockRedstoneEvent event) {
		if (!(event.getOldCurrent() == 0) || !(event.getNewCurrent() > 0) || !BenCmd.getMainProperties().getBoolean("redstoneUnlDisp", true)) {
			return;
		}
		Block block = event.getBlock();
		Block block2;
		block2 = block.getWorld().getBlockAt(block.getX() + 1, block.getY(), block.getZ());
		if (InventoryBackend.getInstance().TryDispense(block2)) {
			return;
		}
		block2 = block.getWorld().getBlockAt(block.getX() - 1, block.getY(), block.getZ());
		if (InventoryBackend.getInstance().TryDispense(block2)) {
			return;
		}
		block2 = block.getWorld().getBlockAt(block.getX(), block.getY(), block.getZ() + 1);
		if (InventoryBackend.getInstance().TryDispense(block2)) {
			return;
		}
		block2 = block.getWorld().getBlockAt(block.getX(), block.getY(), block.getZ() - 1);
		if (InventoryBackend.getInstance().TryDispense(block2)) {
			return;
		}
		block2 = block.getWorld().getBlockAt(block.getX(), block.getY() + 1, block.getZ());
		if (InventoryBackend.getInstance().TryDispense(block2)) {
			return;
		}
	}

	private void lotBreakCheck(BlockBreakEvent event) {
		Player player = event.getPlayer();
		User user = User.getUser(player);

		if (player.getItemInHand().getType() == Material.WOOD_SPADE && user.hasPerm("bencmd.lot.select")) {
			event.setCancelled(true);
		}

		if (!BenCmd.getLots().canBuildHere(player, event.getBlock().getLocation())) {
			event.setCancelled(true);
			player.sendMessage(ChatColor.RED + "You cannot build here.");
		}
	}

	private void lotPlaceCheck(BlockPlaceEvent event) {
		Player player = event.getPlayer();

		if (!BenCmd.getLots().canBuildHere(player, event.getBlock().getLocation())) {
			event.setCancelled(true);
			player.sendMessage(ChatColor.RED + "You cannot build here.");
		}
	}

	private void newPortalCheck(BlockPlaceEvent event) {
		Warp warpTo;
		if (event.getBlockPlaced().getType() == Material.PORTAL && (warpTo = BenCmd.getWarps().getWarp(BenCmd.getMainProperties().getString("defaultPortalWarp", "portals"))) != null) {
			BenCmd.getPortalFile().addPortal(new Portal(Portal.getHandleBlock(event.getBlockPlaced().getLocation()), null, warpTo));
		}
	}

	private void burnCheck(BlockBurnEvent event) {

		if (BenCmd.getMainProperties().getBoolean("allowFireDamage", false)) {
			return;
		}
		if (event.getBlock().getType() != Material.NETHERRACK) {
			event.setCancelled(true);
		}
	}

	private void igniteCheck(BlockIgniteEvent event) {
		if (BenCmd.getMainProperties().getBoolean("allowFireSpread", false)) {
			return;
		}
		if (event.getCause() == IgniteCause.SPREAD) {
			if (BenCmd.getPlugin().canIgnite(event.getBlock().getLocation())) {
				BenCmd.getPlugin().canSpread.add(event.getBlock().getLocation());
				event.setCancelled(false);
			} else {
				event.setCancelled(true);
			}
			return;
		}
		try {
			User user = User.getUser(event.getPlayer());
			Material material = ((Player) user.getHandle()).getWorld().getBlockAt(event.getBlock().getX(), event.getBlock().getY() - 1, event.getBlock().getZ()).getType();
			if (user.isJailed() != null) {
				event.setCancelled(true);
				return;
			}
			if (event.getBlock().getLocation().getBlockY() <= 0) {
				event.setCancelled(true);
				return;
			}
			if (!user.hasPerm("bencmd.fire.netherrack", false) && material != Material.AIR && material != Material.NETHERRACK && ((Player) user.getHandle()).getTargetBlock(null, 4).getType() != Material.NETHERRACK) {
				event.setCancelled(true);
			}
			if (user.hasPerm("bencmd.fire.all")) {
				event.setCancelled(false);
			}
		} catch (NullPointerException e) {
			event.setCancelled(true);
			return;
		}
	}

	private void signLog(SignChangeEvent event) {
		Location l = event.getBlock().getLocation();
		Player p = event.getPlayer();
		String[] ls = event.getLines();
		BenCmd.log(p.getDisplayName() + " put a sign at (" + l.getBlockX() + ", " + l.getBlockY() + ", " + l.getBlockZ() + ", " + l.getWorld().getName() + "):");
		int firstLine = -1;
		for (int i = 0; i < ls.length; i++) {
			String line = ls[i];
			if (!line.isEmpty()) {
				if (firstLine == -1) {
					firstLine = i;
				}
				BenCmd.log("Line " + String.valueOf(i) + ": " + line);
			}
		}
		for (User spy : BenCmd.getPermissionManager().getUserFile().allWithPerm("bencmd.signspy")) {
			if (spy.getName().equals(p.getName()) || firstLine == -1) {
				continue;
			}
			if (BenCmd.isSpoutConnected() && BenCmd.getSpoutConnector().enabled(((Player) spy.getHandle()))) {
				BenCmd.getSpoutConnector().sendNotification(((Player) spy.getHandle()), "Sign by " + p.getName(), ls[firstLine], Material.SIGN);
			} else {
				spy.sendMessage(ChatColor.GRAY + p.getName() + " placed a sign: " + ls[firstLine]);
			}

		}
	}

	private void lockDestroyCheck(BlockBreakEvent event) {
		if (event.isCancelled()) {
			return;
		}
		int id;
		ProtectedBlock block;
		if ((id = BenCmd.getProtections().getProtection(event.getBlock().getLocation())) != -1) {
			block = BenCmd.getProtections().getProtection(id);
			User user = User.getUser(event.getPlayer());
			if (!block.canChange(user.getName())) {
				event.setCancelled(true);
				user.sendMessage(ChatColor.RED + "That block is protected from use!  Use /protect info for more information...");
			} else {
				BenCmd.getProtections().removeProtection(block.getLocation());
				Location loc = block.getLocation();
				String w = loc.getWorld().getName();
				String x = String.valueOf(loc.getX());
				String y = String.valueOf(loc.getY());
				String z = String.valueOf(loc.getZ());
				BenCmd.log(user.getDisplayName() + " removed " + block.getOwner() + "'s protected chest (id: " + String.valueOf(block.GetId()) + ") at position (" + w + "," + x + "," + y + "," + z + ")");
				user.sendMessage(ChatColor.GREEN + "The protection on that block was removed.");
			}
		}
	}

	private void logBlockPlace(BlockPlaceEvent event) {
		if (!event.isCancelled()) {
			BlockPlaceEntry e = new BlockPlaceEntry(event.getPlayer().getName(), event.getBlock().getLocation(), new Date().getTime(), event.getBlock().getType());
			BenCmd.getRecordingFile().logEvent(e);
		}
	}

	private void logBlockBreak(BlockBreakEvent event) {
		if (!event.isCancelled()) {
			BlockBreakEntry e = new BlockBreakEntry(event.getPlayer().getName(), event.getBlock().getLocation(), new Date().getTime(), event.getBlock().getType());
			BenCmd.getRecordingFile().logEvent(e);
		}
	}
	
	private void jailedDestroyCheck(BlockBreakEvent event) {
		if (event.isCancelled()) {
			return;
		}
		if (User.getUser(event.getPlayer()).isJailed() != null) {
			event.setCancelled(true);
		}
	}
	
	private void jailedPlaceCheck(BlockPlaceEvent event) {
		if (event.isCancelled()) {
			return;
		}
		if (User.getUser(event.getPlayer()).isJailed() != null) {
			event.setCancelled(true);
		}
	}

	// Split-off events

	@Override
	public void execute(Listener listener, Event event) throws EventException {
		if (event instanceof BlockBreakEvent) {
			BlockBreakEvent e = (BlockBreakEvent) event;
			lotBreakCheck(e);
			bookshelfBreak(e);
			dcudDestroy(e);
			lockDestroyCheck(e);
			logBlockBreak(e);
			jailedDestroyCheck(e);
		} else if (event instanceof BlockPlaceEvent) {
			BlockPlaceEvent e = (BlockPlaceEvent) event;
			lotPlaceCheck(e);
			newPortalCheck(e);
			logBlockPlace(e);
			jailedPlaceCheck(e);
		} else if (event instanceof BlockIgniteEvent) {
			BlockIgniteEvent e = (BlockIgniteEvent) event;
			igniteCheck(e);
		} else if (event instanceof BlockBurnEvent) {
			BlockBurnEvent e = (BlockBurnEvent) event;
			burnCheck(e);
		} else if (event instanceof SignChangeEvent) {
			SignChangeEvent e = (SignChangeEvent) event;
			signLog(e);
		} else if (event instanceof BlockRedstoneEvent) {
			BlockRedstoneEvent e = (BlockRedstoneEvent) event;
			unlDispRedstone(e);
		}
	}

}
