package ben_dude56.plugins.bencmd.protect;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import ben_dude56.plugins.bencmd.BenCmd;
import ben_dude56.plugins.bencmd.permissions.PermissionUser;

public class ProtectedDoor extends ProtectedBlock {

	private Location blockLocation;

	public ProtectedDoor(BenCmd instance, int id, PermissionUser owner,
			List<PermissionUser> guests, Location loc) {
		super(instance, id, owner, guests, loc);
		blockLocation = loc;
	}

	public Block getSecondBlock() {
		Block block1 = new Location(blockLocation.getWorld(),
				blockLocation.getX(), blockLocation.getY() + 1,
				blockLocation.getZ()).getBlock();
		Block block2 = new Location(blockLocation.getWorld(),
				blockLocation.getX(), blockLocation.getY() - 1,
				blockLocation.getZ()).getBlock();
		if (block1.getType() == Material.WOODEN_DOOR) {
			return block1;
		} else if (block2.getType() == Material.WOODEN_DOOR) {
			return block2;
		} else {
			return null;
		}
	}

	public Block getBelowBlock() {
		Block block1 = new Location(blockLocation.getWorld(),
				blockLocation.getX(), blockLocation.getY() - 1,
				blockLocation.getZ()).getBlock();
		Block block2 = new Location(blockLocation.getWorld(),
				blockLocation.getX(), blockLocation.getY() - 2,
				blockLocation.getZ()).getBlock();
		if (block1.getType() != Material.WOODEN_DOOR) {
			return block1;
		} else if (block2.getType() != Material.WOODEN_DOOR) {
			return block2;
		} else {
			return null;
		}
	}
}
