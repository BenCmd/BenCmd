package com.bendude56.bencmd.invisible;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.bendude56.bencmd.ActionableUser;
import com.bendude56.bencmd.BenCmd;


import net.minecraft.server.Packet20NamedEntitySpawn;
import net.minecraft.server.Packet29DestroyEntity;

public class Invisibility {
	
	private final List<Player> invisible = new ArrayList<Player>();
	private final List<Player> noinvisible = new ArrayList<Player>();
	private final List<Player> allinvisible = new ArrayList<Player>();
	private final List<ActionableUser> offline = new ArrayList<ActionableUser>();

	public Invisibility() {
		Bukkit.getServer().getScheduler()
				.scheduleAsyncRepeatingTask(BenCmd.getPlugin(), new InvTime(), 2, 2);
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
		invisible.remove(player);
		for (Player appearTo : Bukkit.getOnlinePlayers()) {
			if (!noinvisible.contains(appearTo) && player != appearTo) {
				uninvisible(player, appearTo);
			}
		}
	}

	public void addInv(Player player) {
		invisible.add(player);
	}

	public void addAInv(Player player) {
		allinvisible.add(player);
	}

	public void remAInv(Player player) {
		allinvisible.remove(player);
		for (Player appearTo : noinvisible) {
			if (player != appearTo) {
				uninvisible(player, appearTo);
			}
		}
	}

	public void showAll(Player player) {
		for (Player appear : invisible) {
			if (player != appear && !allinvisible.contains(appear)) {
				uninvisible(appear, player);
			}
		}
	}

	public void addNoInv(Player player) {
		noinvisible.add(player);
		showAll(player);
	}

	public void remNoInv(Player player) {
		noinvisible.remove(player);
		for (Player disappear : invisible) {
			if (player != disappear) {
				invisible(disappear, player);
			}
		}
	}
	
	public boolean isInv(Player player) {
		return invisible.contains(player);
	}
	
	public boolean isNoInv(Player player) {
		return noinvisible.contains(player);
	}
	
	public boolean isAInv(Player player) {
		return allinvisible.contains(player);
	}
	
	public boolean isOffline(ActionableUser user) {
		return offline.contains(user);
	}
	
	public void goOffline(ActionableUser user) {
		offline.add(user);
	}
	
	public void goOnline(ActionableUser user) {
		offline.remove(user);
	}

	public class InvTime implements Runnable {
		@Override
		public void run() {
			for (Player noInv : noinvisible) {
				if (noInv == null) {
					noinvisible.remove(noInv);
				}
			}
			for (Player toHide : invisible) {
				if (toHide == null) {
					invisible.remove(toHide);
					continue;
				}
				for (Player hideFrom : Bukkit.getOnlinePlayers()) {
					if ((!noinvisible.contains(hideFrom) && toHide != hideFrom)
							|| allinvisible.contains(toHide)) {
						invisible(toHide, hideFrom);
					}
				}
			}
		}
	}
}
