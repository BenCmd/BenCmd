package com.bendude56.bencmd.protect;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import com.bendude56.bencmd.permissions.PermissionUser;

public class PublicChest extends PublicBlock {
	private Location	blockLocation;

	public PublicChest(int id, PermissionUser owner, List<PermissionUser> guests, Location loc) {
		super(id, owner, guests, loc);
		blockLocation = loc;
	}

	public boolean isDoubleChest() {
		return (getSecondChest() != null);
	}

	public Block getSecondChest() {
		Block block1 = new Location(blockLocation.getWorld(), blockLocation.getX() + 1, blockLocation.getY(), blockLocation.getZ()).getBlock();
		Block block2 = new Location(blockLocation.getWorld(), blockLocation.getX() - 1, blockLocation.getY(), blockLocation.getZ()).getBlock();
		Block block3 = new Location(blockLocation.getWorld(), blockLocation.getX(), blockLocation.getY(), blockLocation.getZ() + 1).getBlock();
		Block block4 = new Location(blockLocation.getWorld(), blockLocation.getX(), blockLocation.getY(), blockLocation.getZ() - 1).getBlock();
		if (block1.getType() == Material.CHEST) {
			return block1;
		} else if (block2.getType() == Material.CHEST) {
			return block2;
		} else if (block3.getType() == Material.CHEST) {
			return block3;
		} else if (block4.getType() == Material.CHEST) {
			return block4;
		}
		return null;
	}
}
