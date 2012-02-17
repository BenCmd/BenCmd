package com.bendude56.bencmd.listener;

import org.bukkit.event.*;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.plugin.*;

import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.advanced.npc.NPC;

public class BenCmdWorldListener implements Listener, EventExecutor {

	// Singleton instancing

	private static BenCmdWorldListener	instance	= null;

	public static BenCmdWorldListener getInstance() {
		if (instance == null) {
			return instance = new BenCmdWorldListener();
		} else {
			return instance;
		}
	}

	public static void destroyInstance() {
		instance = null;
	}

	private BenCmdWorldListener() {
		WorldLoadEvent.getHandlerList().register(new RegisteredListener(this, this, EventPriority.MONITOR, BenCmd.getPlugin(), false));
		WorldUnloadEvent.getHandlerList().register(new RegisteredListener(this, this, EventPriority.MONITOR, BenCmd.getPlugin(), false));
		ChunkLoadEvent.getHandlerList().register(new RegisteredListener(this, this, EventPriority.MONITOR, BenCmd.getPlugin(), false));
		ChunkUnloadEvent.getHandlerList().register(new RegisteredListener(this, this, EventPriority.MONITOR, BenCmd.getPlugin(), false));
	}
	
	// Split-off events

	@Override
	public void execute(Listener listener, Event event) throws EventException {
		if (event instanceof WorldLoadEvent || event instanceof WorldUnloadEvent) {
			BenCmd.getNPCFile().reloadNPCs();
		} else if (event instanceof ChunkLoadEvent) {
			ChunkLoadEvent e = (ChunkLoadEvent) event;
			for (NPC npc : BenCmd.getNPCFile().inChunk(e.getChunk())) {
				npc.spawn();
			}
		} else if (event instanceof ChunkUnloadEvent) {
			ChunkUnloadEvent e = (ChunkUnloadEvent) event;
			for (NPC npc : BenCmd.getNPCFile().inChunk(e.getChunk())) {
				npc.despawn();
			}
		}
	}
}
