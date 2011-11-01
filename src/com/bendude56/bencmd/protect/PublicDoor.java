package com.bendude56.bencmd.protect;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class PublicDoor extends PublicBlock {

	private Location	blockLocation;

	public PublicDoor(int id, String owner, List<String> guests, Location loc) {
		super(id, owner, guests, loc);
		blockLocation = loc;
	}

	public Block getSecondBlock() {
		Block block1 = new Location(blockLocation.getWorld(), blockLocation.getX(), blockLocation.getY() + 1, blockLocation.getZ()).getBlock();
		Block block2 = new Location(blockLocation.getWorld(), blockLocation.getX(), blockLocation.getY() - 1, blockLocation.getZ()).getBlock();
		if (block1.getType() == Material.WOODEN_DOOR) {
			return block1;
		} else if (block2.getType() == Material.WOODEN_DOOR) {
			return block2;
		} else {
			return null;
		}
	}

	public Block getBelowBlock() {
		Block block1 = new Location(blockLocation.getWorld(), blockLocation.getX(), blockLocation.getY() - 1, blockLocation.getZ()).getBlock();
		Block block2 = new Location(blockLocation.getWorld(), blockLocation.getX(), blockLocation.getY() - 2, blockLocation.getZ()).getBlock();
		if (block1.getType() != Material.WOODEN_DOOR) {
			return block1;
		} else if (block2.getType() != Material.WOODEN_DOOR) {
			return block2;
		} else {
			return null;
		}
	}
}
