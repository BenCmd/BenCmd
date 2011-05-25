package ben_dude56.plugins.bencmd.protect;

import java.util.List;

import org.bukkit.Location;

import ben_dude56.plugins.bencmd.BenCmd;
import ben_dude56.plugins.bencmd.permissions.PermissionUser;

public class PublicBlock extends ProtectedBlock {

	public PublicBlock(BenCmd instance, int id, PermissionUser owner,
			List<PermissionUser> guests, Location loc) {
		super(instance, id, owner, guests, loc);
	}

	public boolean canUse(PermissionUser user) {
		return true;
	}
}
