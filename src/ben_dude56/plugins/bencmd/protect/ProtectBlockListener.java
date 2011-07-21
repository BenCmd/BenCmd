package ben_dude56.plugins.bencmd.protect;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;

import ben_dude56.plugins.bencmd.BenCmd;
import ben_dude56.plugins.bencmd.User;

public class ProtectBlockListener extends BlockListener {
	BenCmd plugin;

	public ProtectBlockListener(BenCmd instance) {
		plugin = instance;
	}

	public void onBlockBreak(BlockBreakEvent event) {
		if (event.isCancelled()) {
			return;
		}
		int id;
		ProtectedBlock block;
		if ((id = plugin.protectFile.getProtection(event.getBlock()
				.getLocation())) != -1) {
			block = plugin.protectFile.getProtection(id);
			User user = User.getUser(plugin, event.getPlayer());
			if (!block.canChange(user)) {
				event.setCancelled(true);
				user.sendMessage(ChatColor.RED
						+ "That block is protected from use!  Use /protect info for more information...");
			} else {
				plugin.protectFile.removeProtection(block.getLocation());
				Location loc = block.getLocation();
				String w = loc.getWorld().getName();
				String x = String.valueOf(loc.getX());
				String y = String.valueOf(loc.getY());
				String z = String.valueOf(loc.getZ());
				plugin.log.info(user.getDisplayName() + " removed "
						+ block.getOwner().getName()
						+ "'s protected chest (id: "
						+ String.valueOf(block.GetId()) + ") at position (" + w
						+ "," + x + "," + y + "," + z + ")");
				plugin.bLog.info("PROTECTION REMOVED: " + String.valueOf(block.GetId()) + " (" + block.getOwner().getName() + ") by " + user.getDisplayName());
				user.sendMessage(ChatColor.GREEN
						+ "The protection on that block was removed.");
			}
		}
	}
}
