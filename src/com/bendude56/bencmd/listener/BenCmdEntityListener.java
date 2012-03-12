package com.bendude56.bencmd.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.block.CraftSign;
import org.bukkit.craftbukkit.entity.CraftCreeper;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.entity.CraftSkeleton;
import org.bukkit.craftbukkit.entity.CraftSpider;
import org.bukkit.craftbukkit.entity.CraftZombie;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.painting.PaintingBreakByEntityEvent;
import org.bukkit.event.painting.PaintingBreakEvent;
import org.bukkit.event.painting.PaintingPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.*;

import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.User;
import com.bendude56.bencmd.advanced.Grave;
import com.bendude56.bencmd.advanced.npc.Damageable;
import com.bendude56.bencmd.advanced.npc.EntityNPC;
import com.bendude56.bencmd.advanced.npc.NPC;
import com.bendude56.bencmd.lots.sparea.DropTable;
import com.bendude56.bencmd.lots.sparea.PVPArea;
import com.bendude56.bencmd.lots.sparea.SPArea;
import com.bendude56.bencmd.multiworld.BenCmdWorld;

public class BenCmdEntityListener implements Listener, EventExecutor {

	// Singleton instancing

	private static BenCmdEntityListener	instance	= null;

	public static BenCmdEntityListener getInstance() {
		if (instance == null) {
			return instance = new BenCmdEntityListener();
		} else {
			return instance;
		}
	}

	public static void destroyInstance() {
		instance = null;
	}

	private BenCmdEntityListener() {
		EntityDamageEvent.getHandlerList().register(new RegisteredListener(this, this, EventPriority.LOWEST, BenCmd.getPlugin(), false));
		EntityDeathEvent.getHandlerList().register(new RegisteredListener(this, this, EventPriority.LOWEST, BenCmd.getPlugin(), false));
		EntityTargetEvent.getHandlerList().register(new RegisteredListener(this, this, EventPriority.LOWEST, BenCmd.getPlugin(), false));
		ExplosionPrimeEvent.getHandlerList().register(new RegisteredListener(this, this, EventPriority.LOWEST, BenCmd.getPlugin(), false));
		PaintingBreakEvent.getHandlerList().register(new RegisteredListener(this, this, EventPriority.LOWEST, BenCmd.getPlugin(), false));
		PaintingPlaceEvent.getHandlerList().register(new RegisteredListener(this, this, EventPriority.LOWEST, BenCmd.getPlugin(), false));
		CreatureSpawnEvent.getHandlerList().register(new RegisteredListener(this, this, EventPriority.LOWEST, BenCmd.getPlugin(), false));
	}

	private void pvpHit(EntityDamageEvent e) {
		if (e instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
			if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
				if (((CraftPlayer) event.getDamager()).getHandle() instanceof EntityNPC) {
					if (User.getUser((Player) event.getEntity()).isGod())
						event.setCancelled(true);
					return;
				}
				if (((CraftPlayer) event.getEntity()).getHandle() instanceof EntityNPC) {
					NPC npc = BenCmd.getNPCFile().getNPC((EntityNPC) ((CraftPlayer) event.getEntity()).getHandle());
					if (npc instanceof Damageable) {
						((Damageable) npc).onDamage(event.getDamager(), event.getDamage());
						return;
					}
				}
				event.setCancelled(!(inPVP((Player) event.getDamager()) != null && inPVP((Player) event.getEntity()) != null));
			}
		}
	}

	private void pvpDie(EntityDeathEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		User user = User.getUser((Player) event.getEntity());
		PVPArea a;
		if ((a = inPVP(user.getPlayerHandle())) != null) {
			HashMap<ItemStack, PVPArea.DropMode> result = a.getDrops(event.getDrops());
			List<ItemStack> toReturn = new ArrayList<ItemStack>();
			event.getDrops().clear();
			for (int i = 0; i < result.size(); i++) {
				ItemStack item = (ItemStack) result.keySet().toArray()[i];
				PVPArea.DropMode mode = result.get(item);
				if (mode == PVPArea.DropMode.DROP) {
					event.getDrops().add(item);
				} else if (mode == PVPArea.DropMode.KEEP) {
					toReturn.add(item);
				}
			}
			Grave.returns.put(user.getPlayerHandle(), toReturn);
			return;
		}
		if (BenCmd.getMainProperties().getBoolean("gravesEnabled", true)) {
			for (int i = 0; i < Grave.graves.size(); i++) {
				Grave g = Grave.graves.get(i);
				if (g.getPlayer().equals((Player) event.getEntity())) {
					if (BenCmd.getMainProperties().getBoolean("newerGraveOverwrites", false)) {
						g.delete();
						Grave.graves.remove(i);
					} else {
						return;
					}
				}
			}
			Location graveLoc = new Location(user.getPlayerHandle().getWorld(), ((Player) user.getHandle()).getLocation().getBlockX(), ((Player) user.getHandle()).getLocation().getBlockY(), ((Player) user.getHandle()).getLocation().getBlockZ());
			Block grave = user.getPlayerHandle().getWorld().getBlockAt(graveLoc);
			while (grave.getType() != Material.AIR) {
				if (graveLoc.getBlockY() == 1) {
					return;
				}
				graveLoc.setY(graveLoc.getY() + 1);
				grave = user.getPlayerHandle().getWorld().getBlockAt(graveLoc);
			}
			grave.setType(Material.SIGN_POST);
			CraftSign graveSign = new CraftSign(grave);
			graveSign.setLine(1, "R.I.P.");
			graveSign.setLine(2, user.getDisplayName());
			graveSign.update();
			Grave.graves.add(new Grave(grave, user.getPlayerHandle(), event.getDrops(), BenCmd.getMainProperties().getInteger("graveDuration", 180)));
			BenCmd.getLocale().sendMessage(user, "misc.grave.death");
			event.getDrops().clear();
		}
	}

	private void endermanPassive(EntityTargetEvent event) {
		if (event.getEntity().toString().equalsIgnoreCase("CraftEnderman") && BenCmd.getMainProperties().getBoolean("endermenPassive", true))
			event.setCancelled(true);
		if (event.getTarget() instanceof Player && User.getUser((Player) event.getTarget()).isPoofed())
			event.setCancelled(true);
	}

	private void endermanDropBlock(EntityDeathEvent event) {
		if (BenCmd.getMainProperties().getBoolean("endermenDropCarriedBlock", false)) {
			return;
		} else if (event.getEntity() instanceof Enderman) {
			Enderman ender = (Enderman) event.getEntity();
			if (ender.getCarriedMaterial().getItemType() != Material.AIR) {
				ItemStack block = new org.bukkit.inventory.ItemStack(ender.getCarriedMaterial().getItemType(), 1);
				event.getDrops().add(block);
			}
			return;
		}
	}

	private void creeperPassive(EntityTargetEvent event) {

		if (event.getEntity().toString().equalsIgnoreCase("CraftCreeper") && BenCmd.getMainProperties().getBoolean("creepersPassive", true))
			event.setCancelled(true);
		if (event.getTarget() instanceof Player && User.getUser((Player) event.getTarget()).isPoofed())
			event.setCancelled(true);
	}

	private void tntExplode(ExplosionPrimeEvent event) {

		if (event.getEntity() instanceof TNTPrimed) {
			event.setCancelled(true);
			String logMessage = "REDSTONE tried to detonate TNT at X:" + event.getEntity().getLocation().getBlockX() + "  Y:" + event.getEntity().getLocation().getBlockZ() + ". ";
			Player nearPlayer = nearestPlayer(event.getEntity().getLocation());
			logMessage += "Nearest detected player: " + nearPlayer.getDisplayName();
			Bukkit.broadcastMessage(ChatColor.RED + "Attempted redstone TNT detonation detected! Nearest player:  " + nearPlayer.getDisplayName());
			Logger.getLogger("minecraft").info(logMessage);
			if (BenCmd.getMainProperties().getBoolean("attemptRedstoneTntKick", false)) {
				User user = User.getUser(nearPlayer);
				Bukkit.broadcastMessage(ChatColor.RED + user.getDisplayName() + " tried to detonate TNT!");
				user.kick(BenCmd.getMainProperties().getString("TNTKick", "You can't detonate TNT!"));
			}
			return;
		}
	}

	private void mobDrop(EntityDeathEvent event) {
		if (event.getEntity() instanceof CraftZombie) {
			if (BenCmd.getMainProperties().getString("zombieDrop", "").isEmpty()) {
				return;
			}
			DropTable table = new DropTable(BenCmd.getMainProperties().getString("zombieDrop", ""));
			event.getDrops().clear();
			for (ItemStack item : table.getRandomDrops(new Random())) {
				event.getDrops().add(item);
			}
		}
		if (event.getEntity() instanceof CraftSkeleton) {
			if (BenCmd.getMainProperties().getString("skeletonDrop", "").isEmpty()) {
				return;
			}
			DropTable table = new DropTable(BenCmd.getMainProperties().getString("skeletonDrop", ""));
			event.getDrops().clear();
			for (ItemStack item : table.getRandomDrops(new Random())) {
				event.getDrops().add(item);
			}
		}
		if (event.getEntity() instanceof CraftSpider) {
			if (BenCmd.getMainProperties().getString("spiderDrop", "").isEmpty()) {
				return;
			}
			DropTable table = new DropTable(BenCmd.getMainProperties().getString("spiderDrop", ""));
			event.getDrops().clear();
			for (ItemStack item : table.getRandomDrops(new Random())) {
				event.getDrops().add(item);
			}
		}
		if (event.getEntity() instanceof CraftCreeper) {
			if (BenCmd.getMainProperties().getString("creeperDrop", "").isEmpty()) {
				return;
			}
			DropTable table = new DropTable(BenCmd.getMainProperties().getString("creeperDrop", ""));
			event.getDrops().clear();
			for (ItemStack item : table.getRandomDrops(new Random())) {
				event.getDrops().add(item);
			}
		}
	}

	private void playerDie(EntityDeathEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}

		if (BenCmd.getMainProperties().getBoolean("noDeathMessages", true)) {
			((PlayerDeathEvent) event).setDeathMessage(null);
		}

		User user = User.getUser((Player) event.getEntity());
		if (user.hasPerm("bencmd.warp.back") && user.hasPerm("bencmd.warp.deathback")) {
			BenCmd.getWarpCheckpoints().SetPreWarp(user.getPlayerHandle());
			BenCmd.getLocale().sendMessage(user, "misc.warp.dieBack");
		}
	}

	private void invincible(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		if (User.getUser((Player) event.getEntity()).isJailed() != null) {
			event.setCancelled(true);
		}
		if (User.getUser((Player) event.getEntity()).isPoofed()) {
			event.setCancelled(true);
		}
		Player player = (Player) event.getEntity();
		if (User.getUser(player).isGod()) {
			event.setCancelled(true);
		}
	}

	private Player nearestPlayer(Location loc) {
		Player lastPlayer = null;
		double olddistance = 0.0;
		for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			if (lastPlayer == null) {
				lastPlayer = onlinePlayer;
				olddistance = distanceBetween(loc, lastPlayer.getLocation());
			} else {
				double newdistance = distanceBetween(loc, onlinePlayer.getLocation());
				if (newdistance < olddistance) {
					lastPlayer = onlinePlayer;
					olddistance = newdistance;
				}
			}
		}
		return lastPlayer;
	}

	private Double distanceBetween(Location loc1, Location loc2) {
		Double distance;
		distance = Math.abs(loc1.getX() - loc2.getX());
		distance += Math.abs(loc1.getY() - loc2.getY());
		distance += Math.abs(loc1.getZ() - loc2.getZ());
		return distance;
	}

	private PVPArea inPVP(Player p) {
		for (SPArea a : BenCmd.getAreas().listAreas()) {
			if (a instanceof PVPArea) {
				if (a.insideArea(p.getLocation())) {
					return (PVPArea) a;
				}
			}
		}
		return null;
	}
	
	private void paintingPlaceCheck(PaintingPlaceEvent event) {
		Player player = event.getPlayer();

		if (!BenCmd.getLots().canBuildHere(player, event.getBlock().getLocation())) {
			event.setCancelled(true);
			BenCmd.getLocale().sendMessage(User.getUser(player), "basic.noBuild");
		}
	}
	
	private void paintingBreakCheck(PaintingBreakEvent event) {
		if (event instanceof PaintingBreakByEntityEvent && ((PaintingBreakByEntityEvent)event).getRemover() instanceof Player) {
			Player player = (Player) ((PaintingBreakByEntityEvent)event).getRemover();
			if (!BenCmd.getLots().canBuildHere(player, event.getPainting().getLocation())) {
				event.setCancelled(true);
				BenCmd.getLocale().sendMessage(User.getUser(player), "basic.noBuild");
			}
		}
	}
	
	private void worldAllowSpawn(CreatureSpawnEvent event) {
		if (event.isCancelled() || (event.getSpawnReason() != SpawnReason.NATURAL && event.getSpawnReason() != SpawnReason.SPAWNER)) {
			return;
		}
		BenCmdWorld world = BenCmd.getWorlds().getWorld(event.getLocation().getWorld());
		if (world == null) {
			return; // World is not under BenCmd control
		}
		if (!world.getAllowSpawnPassive() && isPassive(event.getCreatureType())) {
			event.setCancelled(true);
			return;
		}
		if (!world.getAllowSpawnNeutral() && isNeutral(event.getCreatureType())) {
			event.setCancelled(true);
			return;
		}
		if (!world.getAllowSpawnAggressive() && isAggressive(event.getCreatureType())) {
			event.setCancelled(true);
			return;
		}
	}
	
	private boolean isPassive(CreatureType type) {
		switch (type) {
			case CHICKEN:
				return true;
			case COW:
				return true;
			case MUSHROOM_COW:
				return true;
			case PIG:
				return true;
			case SHEEP:
				return true;
			case SQUID:
				return true;
			case VILLAGER:
				return true;
			default:
				return false;
		}
	}
	
	private boolean isNeutral(CreatureType type) {
		switch (type) {
			case GIANT:
				return true;
			case PIG_ZOMBIE:
				return true;
			case SNOWMAN:
				return true;
			case WOLF:
				return true;
			default:
				return false;
		}
	}
	
	private boolean isAggressive(CreatureType type) {
		switch (type) {
			case BLAZE:
				return true;
			case CAVE_SPIDER:
				return true;
			case CREEPER:
				return true;
			case ENDER_DRAGON:
				return true;
			case ENDERMAN:
				return true;
			case GHAST:
				return true;
			case MAGMA_CUBE:
				return true;
			case SILVERFISH:
				return true;
			case SKELETON:
				return true;
			case SPIDER:
				return true;
			case ZOMBIE:
				return true;
			default:
				return false;
		}
	}

	// Split-off events

	@Override
	public void execute(Listener listener, Event event) throws EventException {
		if (event instanceof CreatureSpawnEvent) {
			CreatureSpawnEvent e = (CreatureSpawnEvent) event;
			worldAllowSpawn(e);
		} else if (event instanceof EntityDamageEvent) {
			EntityDamageEvent e = (EntityDamageEvent) event;
			invincible(e);
			pvpHit(e);
		} else if (event instanceof EntityDeathEvent) {
			EntityDeathEvent e = (EntityDeathEvent) event;
			pvpDie(e);
			playerDie(e);
			mobDrop(e);
			endermanDropBlock(e);
		} else if (event instanceof EntityTargetEvent) {
			EntityTargetEvent e = (EntityTargetEvent) event;
			endermanPassive(e);
			creeperPassive(e);
		} else if (event instanceof ExplosionPrimeEvent) {
			ExplosionPrimeEvent e = (ExplosionPrimeEvent) event;
			tntExplode(e);
		} else if (event instanceof PaintingBreakEvent) {
			PaintingBreakEvent e = (PaintingBreakEvent) event;
			paintingBreakCheck(e);
		} else if (event instanceof PaintingPlaceEvent) {
			PaintingPlaceEvent e = (PaintingPlaceEvent) event;
			paintingPlaceCheck(e);
		}
	}
}
