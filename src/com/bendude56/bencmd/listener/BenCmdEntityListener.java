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
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EndermanPickupEvent;
import org.bukkit.event.entity.EndermanPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;

import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.User;
import com.bendude56.bencmd.advanced.Grave;
import com.bendude56.bencmd.advanced.npc.Damageable;
import com.bendude56.bencmd.advanced.npc.EntityNPC;
import com.bendude56.bencmd.advanced.npc.NPC;
import com.bendude56.bencmd.lots.sparea.DropTable;
import com.bendude56.bencmd.lots.sparea.PVPArea;
import com.bendude56.bencmd.lots.sparea.SPArea;


public class BenCmdEntityListener extends EntityListener {
	
	// Singleton instancing
	
	private static BenCmdEntityListener instance = null;
	
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
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvent(Event.Type.ENTITY_DAMAGE, this,
				Event.Priority.Highest, BenCmd.getPlugin());
		pm.registerEvent(Event.Type.ENTITY_DEATH, this,
				Event.Priority.Monitor, BenCmd.getPlugin());
		pm.registerEvent(Event.Type.ENTITY_TARGET, this,
				Event.Priority.Highest, BenCmd.getPlugin());
		pm.registerEvent(Event.Type.EXPLOSION_PRIME, this,
				Event.Priority.Highest, BenCmd.getPlugin());
		pm.registerEvent(Event.Type.ENDERMAN_PICKUP, this,
				Event.Priority.Highest, BenCmd.getPlugin());
		pm.registerEvent(Event.Type.ENDERMAN_PLACE, this,
				Event.Priority.Highest, BenCmd.getPlugin());
	}
	
	private void pvpHit(EntityDamageEvent e) {
		BenCmd plugin = BenCmd.getPlugin();
		
		if (e instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
			if (event.getDamager() instanceof Player
					&& event.getEntity() instanceof Player) {
				if (((CraftPlayer)event.getDamager()).getHandle() instanceof EntityNPC) {
					if (plugin.isGod((Player)event.getEntity())) 
						event.setCancelled(true);
					return;
				}
				if (((CraftPlayer)event.getEntity()).getHandle() instanceof EntityNPC) {
					NPC npc = plugin.npcs.getNPC((EntityNPC) ((CraftPlayer) event.getEntity()).getHandle());
					if(npc instanceof Damageable) {
						((Damageable) npc).onDamage(event.getDamager(), event.getDamage());
						return;
					}
				}
				event.setCancelled(!(inPVP((Player) event.getDamager()) != null && inPVP((Player) event
						.getEntity()) != null));
			}
		}
	}

	private void pvpDie(EntityDeathEvent event) {
		BenCmd plugin = BenCmd.getPlugin();
		
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		User user = User.getUser(plugin, (Player) event.getEntity());
		PVPArea a;
		if ((a = inPVP(user.getHandle())) != null) {
			HashMap<ItemStack, PVPArea.DropMode> result = a.getDrops(event
					.getDrops());
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
			plugin.returns.put(user.getHandle(), toReturn);
			return;
		}
		if (plugin.mainProperties.getBoolean("gravesEnabled", true)) {
			for (int i = 0; i < plugin.graves.size(); i++) {
				Grave g = plugin.graves.get(i);
				if (g.getPlayer().equals((Player) event.getEntity())) {
					if (plugin.mainProperties.getBoolean(
							"newerGraveOverwrites", false)) {
						g.delete();
						plugin.graves.remove(i);
					} else {
						return;
					}
				}
			}
			Location graveLoc = new Location(user.getHandle().getWorld(), user
					.getHandle().getLocation().getBlockX(), user.getHandle()
					.getLocation().getBlockY(), user.getHandle().getLocation()
					.getBlockZ());
			Block grave = user.getHandle().getWorld().getBlockAt(graveLoc);
			while (grave.getType() != Material.AIR) {
				if (graveLoc.getBlockY() == 1) {
					return;
				}
				graveLoc.setY(graveLoc.getY() + 1);
				grave = user.getHandle().getWorld().getBlockAt(graveLoc);
			}
			grave.setType(Material.SIGN_POST);
			CraftSign graveSign = new CraftSign(grave);
			graveSign.setLine(1, "R.I.P.");
			graveSign.setLine(2, user.getDisplayName());
			graveSign.update();
			plugin.graves.add(new Grave(plugin, grave, (Player) event
					.getEntity(), event.getDrops(), plugin.mainProperties
					.getInteger("graveDuration", 180)));
			((Player) event.getEntity())
					.sendMessage(ChatColor.RED
							+ "You have died... You can retrieve your items by breaking your gravestone...");
			event.getDrops().clear();
		}
	}
	
	private void endermanPassive(EntityTargetEvent event) {
		BenCmd plugin = BenCmd.getPlugin();
		
		if (event.getEntity().toString().equalsIgnoreCase("CraftEnderman")
				&& plugin.mainProperties.getBoolean("endermenPassive", true))
			event.setCancelled(true);
		if (event.getTarget() instanceof Player
				&& User.getUser(plugin, (Player) event.getTarget()).isPoofed())
			event.setCancelled(true);
	}
	
	private void endermanGriefTake(EndermanPickupEvent event) {
		BenCmd plugin = BenCmd.getPlugin();
		
		if (!plugin.mainProperties.getBoolean("endermenGriefing", true)) {
			event.setCancelled(true);
			return;
		}
		Location BlockLocation = event.getBlock().getLocation();
		if (!plugin.lots.isInLot(BlockLocation).equalsIgnoreCase("-1")
				&& !plugin.mainProperties.getBoolean("endermenLotGriefing", false)) {
			event.setCancelled(true);
		}
	}
	
	private void endermanGriefPlace(EndermanPlaceEvent event) {
		BenCmd plugin = BenCmd.getPlugin();
		
		if (!plugin.mainProperties.getBoolean("endermenGriefing", true)) {
			event.setCancelled(true);
		}
		if (!plugin.mainProperties.getBoolean("endermenLotGriefing", false)) {
			int range=4;
			int xoffset=-range;
			int yoffset=-range;
			int zoffset=-range;
			Location loc = event.getEntity().getLocation();
			double x = loc.getX();
			double y = loc.getY();
			double z = loc.getZ();
			loc.setWorld(event.getEntity().getWorld());
			for (int i=0; i<range*range*range; i++) {
				loc.setX(x+xoffset);
				loc.setY(y+yoffset);
				loc.setZ(z+zoffset);
				if (!plugin.lots.isInLot(loc).equalsIgnoreCase("-1")) {
					event.setCancelled(true);
					return;
				} 
				xoffset++;
				if (xoffset > range) {
					xoffset = -range;
					yoffset++;
					if (yoffset > range) {
						yoffset = -range;
						zoffset ++;
					}
				}
			}
		}
	}
	
	private void endermanDropBlock(EntityDeathEvent event) {
		BenCmd plugin = BenCmd.getPlugin();
		
		if (plugin.mainProperties.getBoolean("endermenDropCarriedBlock", false)) {
			return;
		} else if (event.getEntity() instanceof Enderman) {
			Enderman ender = (Enderman)event.getEntity();
			if (ender.getCarriedMaterial() != null) {
				ItemStack block = new org.bukkit.inventory.ItemStack(ender.getCarriedMaterial().getItemType(),1);
				event.getDrops().add(block);
			}
			return;
		}
	}
	
	private void creeperPassive(EntityTargetEvent event) {
		BenCmd plugin = BenCmd.getPlugin();
		
		if (event.getEntity().toString().equalsIgnoreCase("CraftCreeper")
				&& plugin.mainProperties.getBoolean("creepersPassive", true))
			event.setCancelled(true);
		if (event.getTarget() instanceof Player
				&& User.getUser(plugin, (Player) event.getTarget()).isPoofed())
			event.setCancelled(true);
	}
	
	private void tntExplode(ExplosionPrimeEvent event) {
		BenCmd plugin = BenCmd.getPlugin();
		
		if (event.getEntity() instanceof TNTPrimed) {
			event.setCancelled(true);
			String logMessage = "REDSTONE tried to detonate TNT at X:"
					+ event.getEntity().getLocation().getBlockX() + "  Y:"
					+ event.getEntity().getLocation().getBlockZ() + ". ";
			Player nearPlayer = nearestPlayer(event.getEntity().getLocation());
			logMessage += "Nearest detected player: "
					+ nearPlayer.getDisplayName();
			plugin.getServer()
					.broadcastMessage(
							ChatColor.RED
									+ "Attempted redstone TNT detonation detected! Nearest player:  "
									+ nearPlayer.getDisplayName());
			Logger.getLogger("minecraft").info(logMessage);
			if (plugin.mainProperties.getBoolean("attemptRedstoneTntKick",
					false)) {
				User user = User.getUser(plugin, nearPlayer);
				plugin.getServer().broadcastMessage(
						ChatColor.RED + user.getDisplayName()
								+ " tried to detonate TNT!");
				user.Kick(plugin.mainProperties.getString("TNTKick",
						"You can't detonate TNT!"));
			}
			return;
		}
	}
	
	private void mobDrop(EntityDeathEvent event) {
		BenCmd plugin = BenCmd.getPlugin();
		
		if (event.getEntity() instanceof CraftZombie) {
			if (plugin.mainProperties.getString("zombieDrop", "").isEmpty()) {
				return;
			}
			DropTable table = new DropTable(plugin,
					plugin.mainProperties.getString("zombieDrop", ""));
			event.getDrops().clear();
			for (ItemStack item : table.getRandomDrops(new Random())) {
				event.getDrops().add(item);
			}
		}
		if (event.getEntity() instanceof CraftSkeleton) {
			if (plugin.mainProperties.getString("skeletonDrop", "").isEmpty()) {
				return;
			}
			DropTable table = new DropTable(plugin,
					plugin.mainProperties.getString("skeletonDrop", ""));
			event.getDrops().clear();
			for (ItemStack item : table.getRandomDrops(new Random())) {
				event.getDrops().add(item);
			}
		}
		if (event.getEntity() instanceof CraftSpider) {
			if (plugin.mainProperties.getString("spiderDrop", "").isEmpty()) {
				return;
			}
			DropTable table = new DropTable(plugin,
					plugin.mainProperties.getString("spiderDrop", ""));
			event.getDrops().clear();
			for (ItemStack item : table.getRandomDrops(new Random())) {
				event.getDrops().add(item);
			}
		}
		if (event.getEntity() instanceof CraftCreeper) {
			if (plugin.mainProperties.getString("creeperDrop", "").isEmpty()) {
				return;
			}
			DropTable table = new DropTable(plugin,
					plugin.mainProperties.getString("creeperDrop", ""));
			event.getDrops().clear();
			for (ItemStack item : table.getRandomDrops(new Random())) {
				event.getDrops().add(item);
			}
		}
	}
	
	private void playerDie(EntityDeathEvent event) {
		BenCmd plugin = BenCmd.getPlugin();
		
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		
		if (plugin.mainProperties.getBoolean("noDeathMessages", true)) {
			((PlayerDeathEvent) event).setDeathMessage(null);
		}
		
		User user = User.getUser(plugin, (Player) event.getEntity());
		if (user.hasPerm("bencmd.warp.back") && user.hasPerm("bencmd.warp.deathback")) {
			plugin.checkpoints.SetPreWarp(user.getHandle());
			user.sendMessage(ChatColor.RED
					+ "Use /back to return to your death point...");
		}
	}

	private void invincible(EntityDamageEvent event) {
		BenCmd plugin = BenCmd.getPlugin();
		
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		if (User.getUser(plugin, (Player) event.getEntity()).isJailed() != null) {
			event.setCancelled(true);
		}
		if (User.getUser(plugin, (Player) event.getEntity()).isPoofed()) {
			event.setCancelled(true);
		}
		Player player = (Player) event.getEntity();
		if (plugin.isGod(player)) {
			event.setCancelled(true);
		}
	}

	private Player nearestPlayer(Location loc) {
		BenCmd plugin = BenCmd.getPlugin();
		
		Player lastPlayer = null;
		double olddistance = 0.0;
		for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
			if (lastPlayer == null) {
				lastPlayer = onlinePlayer;
				olddistance = distanceBetween(loc, lastPlayer.getLocation());
			} else {
				double newdistance = distanceBetween(loc,
						onlinePlayer.getLocation());
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
		for (SPArea a : BenCmd.getPlugin().spafile.listAreas()) {
			if (a instanceof PVPArea) {
				if (a.insideArea(p.getLocation())) {
					return (PVPArea) a;
				}
			}
		}
		return null;
	}
	
	// Split-off events
	
	public void onEntityDamage(EntityDamageEvent event) {
		invincible(event);
		pvpHit(event);
	}
	
	public void onEntityDeath(EntityDeathEvent event) {
		pvpDie(event);
		playerDie(event);
		mobDrop(event);
		endermanDropBlock(event);
	}
	
	public void onEntityTarget(EntityTargetEvent event) {
		endermanPassive(event);
		creeperPassive(event);
	}
	
	public void onExplosionPrime(ExplosionPrimeEvent event) {
		tntExplode(event);
	}
	
	public void onEndermanPickup(EndermanPickupEvent event) {
		endermanGriefTake(event);
	}
	
	public void onEndermanPlace(EndermanPlaceEvent event) {
		endermanGriefPlace(event);
	}
}
