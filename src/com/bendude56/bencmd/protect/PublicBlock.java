package com.bendude56.bencmd.protect;

import java.util.List;

import org.bukkit.Location;

public class PublicBlock extends ProtectedBlock {

	public PublicBlock(int id, String owner, List<String> guests, Location loc) {
		super(id, owner, guests, loc);
	}

	public boolean canUse(String user) {
		return true;
	}
}
