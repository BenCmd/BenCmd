package com.bendude56.bencmd.advanced.npc;

import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldListener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import com.bendude56.bencmd.BenCmd;


public class NPCChunkListener extends WorldListener {
	private BenCmd plugin;

	public NPCChunkListener(BenCmd instance) {
		plugin = instance;
	}
	
	public void onWorldLoad(WorldLoadEvent event) {
		plugin.npcs.reloadNPCs();
	}
	
	public void onWorldUnload(WorldUnloadEvent event) {
		plugin.npcs.reloadNPCs();
	}

	public void onChunkLoad(ChunkLoadEvent event) {
		for (NPC npc : plugin.npcs.inChunk(event.getChunk())) {
			npc.spawn();
		}
	}

	public void onChunkUnload(ChunkUnloadEvent event) {
		if (event.isCancelled()) {
			return;
		}
		for (NPC npc : plugin.npcs.inChunk(event.getChunk())) {
			npc.despawn();
		}
	}
}
