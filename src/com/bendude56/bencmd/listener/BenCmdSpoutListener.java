package com.bendude56.bencmd.listener;

import org.bukkit.event.*;
import org.bukkit.plugin.*;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.event.spout.SpoutCraftEnableEvent;
import org.getspout.spoutapi.packet.PacketSkinURL;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.advanced.npc.NPC;

public class BenCmdSpoutListener implements Listener, EventExecutor {

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
		SpoutCraftEnableEvent.getHandlerList().register(new RegisteredListener(this, this, EventPriority.NORMAL, BenCmd.getPlugin(), false));
	}

	public void sendSkins(SpoutCraftEnableEvent event) {
		SpoutPlayer p = SpoutManager.getPlayer(event.getPlayer());
		if (p.isSpoutCraftEnabled()) {
			for (NPC n : BenCmd.getNPCFile().allNPCs()) {
				if (n.isSpawned()) {
					p.sendPacket(new PacketSkinURL(n.getEntityId(), n.getSkinURL()));
				}
			}
		}
	}
	
	// Split-off events
	
	public void execute(Listener listener, Event event) throws EventException {
		if (event instanceof SpoutCraftEnableEvent) {
			SpoutCraftEnableEvent e = (SpoutCraftEnableEvent) event;
			sendSkins(e);
		}
	}
}
