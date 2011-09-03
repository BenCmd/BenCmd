package com.bendude56.bencmd.invisible;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.bendude56.bencmd.BenCmd;


import net.minecraft.server.Packet20NamedEntitySpawn;
import net.minecraft.server.Packet29DestroyEntity;

public class Invisibility {
	BenCmd plugin;

	public Invisibility(BenCmd instance) {
		plugin = instance;
		Bukkit.getServer().getScheduler()
				.scheduleAsyncRepeatingTask(plugin, new InvTime(), 2, 2);
	}

	public void invisible(Player p1, Player p2) {
		CraftPlayer hide = (CraftPlayer) p1;
		CraftPlayer hideFrom = (CraftPlayer) p2;
		hideFrom.getHandle().netServerHandler
				.sendPacket(new Packet29DestroyEntity(hide.getEntityId()));
	}

	public void uninvisible(Player p1, Player p2) {
		CraftPlayer unHide = (CraftPlayer) p1;
		CraftPlayer unHideFrom = (CraftPlayer) p2;
		unHideFrom.getHandle().netServerHandler
				.sendPacket(new Packet20NamedEntitySpawn(unHide.getHandle()));
	}

	public void remInv(Player player) {
		plugin.invisible.remove(player);
		for (Player appearTo : plugin.getServer().getOnlinePlayers()) {
			if (!plugin.noinvisible.contains(appearTo) && player != appearTo) {
				uninvisible(player, appearTo);
			}
		}
	}

	public void addInv(Player player) {
		plugin.invisible.add(player);
	}

	public void addAInv(Player player) {
		plugin.allinvisible.add(player);
	}

	public void remAInv(Player player) {
		plugin.allinvisible.remove(player);
		for (Player appearTo : plugin.noinvisible) {
			if (player != appearTo) {
				uninvisible(player, appearTo);
			}
		}
	}

	public void showAll(Player player) {
		for (Player appear : plugin.invisible) {
			if (player != appear && !plugin.allinvisible.contains(appear)) {
				uninvisible(appear, player);
			}
		}
	}

	public void addNoInv(Player player) {
		plugin.noinvisible.add(player);
		showAll(player);
	}

	public void remNoInv(Player player) {
		plugin.noinvisible.remove(player);
		for (Player disappear : plugin.invisible) {
			if (player != disappear) {
				invisible(disappear, player);
			}
		}
	}

	public class InvTime implements Runnable {
		@Override
		public void run() {
			for (Player noInv : plugin.noinvisible) {
				if (noInv == null) {
					plugin.noinvisible.remove(noInv);
				}
			}
			for (Player toHide : plugin.invisible) {
				if (toHide == null) {
					plugin.invisible.remove(toHide);
					continue;
				}
				for (Player hideFrom : plugin.getServer().getOnlinePlayers()) {
					if ((!plugin.noinvisible.contains(hideFrom) && toHide != hideFrom)
							|| plugin.allinvisible.contains(toHide)) {
						invisible(toHide, hideFrom);
					}
				}
			}
		}
	}
}
