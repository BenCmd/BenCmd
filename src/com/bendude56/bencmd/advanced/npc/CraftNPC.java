package com.bendude56.bencmd.advanced.npc;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftPlayer;

public class CraftNPC extends CraftPlayer {

	public CraftNPC(CraftServer server, EntityNPC entity) {
		super(server, entity);
	}

}
