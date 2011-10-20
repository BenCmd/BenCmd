package com.bendude56.bencmd.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldListener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.plugin.PluginManager;

import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.advanced.npc.NPC;


public class BenCmdWorldListener extends WorldListener {
	
	// Singleton instancing
	
	private static BenCmdWorldListener instance = null;
	
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
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvent(Event.Type.WORLD_LOAD, this,
				Event.Priority.Monitor, BenCmd.getPlugin());
		pm.registerEvent(Event.Type.WORLD_UNLOAD, this,
				Event.Priority.Monitor, BenCmd.getPlugin());
		pm.registerEvent(Event.Type.CHUNK_LOAD, this,
				Event.Priority.Monitor, BenCmd.getPlugin());
		pm.registerEvent(Event.Type.CHUNK_UNLOAD, this,
				Event.Priority.Monitor, BenCmd.getPlugin());
	}
	
	public void onWorldLoad(WorldLoadEvent event) {
		BenCmd.getNPCFile().reloadNPCs();
	}
	
	public void onWorldUnload(WorldUnloadEvent event) {
		BenCmd.getNPCFile().reloadNPCs();
	}

	public void onChunkLoad(ChunkLoadEvent event) {
		for (NPC npc : BenCmd.getNPCFile().inChunk(event.getChunk())) {
			npc.spawn();
		}
	}

	public void onChunkUnload(ChunkUnloadEvent event) {
		if (event.isCancelled()) {
			return;
		}
		for (NPC npc : BenCmd.getNPCFile().inChunk(event.getChunk())) {
			npc.despawn();
		}
	}
}
