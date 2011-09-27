package com.bendude56.bencmd.mobs;

import org.bukkit.Location;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EndermanPickupEvent;
import org.bukkit.event.entity.EndermanPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.inventory.ItemStack;

import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.User;

public class EndermenListener extends EntityListener {
	BenCmd plugin;
	
	public EndermenListener(BenCmd instance) {
		plugin = instance;
	}
	
	public void onEntityTarget(EntityTargetEvent event) {
		if (event.getEntity().toString().equalsIgnoreCase("CraftEnderman")
				&& plugin.mainProperties.getBoolean("endermenPassive", true))
			event.setCancelled(true);
		if (event.getTarget() instanceof Player
				&& User.getUser(plugin, (Player) event.getTarget()).isPoofed())
			event.setCancelled(true);
	}
	
	public void onEndermanPickup(EndermanPickupEvent event) {
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
	
	public void onEndermanPlace(EndermanPlaceEvent event) {
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
	
	
	
	public void onEndermenEvent(EntityDeathEvent event) {
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
	
	
	
}