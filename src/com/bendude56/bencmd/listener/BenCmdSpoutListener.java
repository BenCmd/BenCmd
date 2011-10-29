package com.bendude56.bencmd.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.event.spout.SpoutCraftEnableEvent;
import org.getspout.spoutapi.event.spout.SpoutListener;
import org.getspout.spoutapi.packet.PacketSkinURL;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.advanced.npc.NPC;

public class BenCmdSpoutListener extends SpoutListener {

	// Singleton instancing

	private static BenCmdSpoutListener	instance	= null;

	public static BenCmdSpoutListener getInstance() {
		if (instance == null) {
			return instance = new BenCmdSpoutListener();
		} else {
			return instance;
		}
	}

	public static void destroyInstance() {
		instance = null;
	}

	private BenCmdSpoutListener() {
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvent(Event.Type.CUSTOM_EVENT, this, Event.Priority.Normal, BenCmd.getPlugin());
	}

	@Override
	public void onSpoutCraftEnable(SpoutCraftEnableEvent event) {
		SpoutPlayer p = SpoutManager.getPlayer(event.getPlayer());
		if (p.isSpoutCraftEnabled()) {
			for (NPC n : BenCmd.getNPCFile().allNPCs()) {
				if (n.isSpawned()) {
					p.sendPacket(new PacketSkinURL(n.getEntityId(), n.getSkinURL()));
				}
			}
		}
	}
}
