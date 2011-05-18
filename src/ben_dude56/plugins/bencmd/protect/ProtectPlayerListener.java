package ben_dude56.plugins.bencmd.protect;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;

import ben_dude56.plugins.bencmd.BenCmd;
import ben_dude56.plugins.bencmd.User;

public class ProtectPlayerListener extends PlayerListener {
	BenCmd plugin;
	Logger log = Logger.getLogger("minecraft");

	public ProtectPlayerListener(BenCmd instance) {
		plugin = instance;
	}

	public void onPlayerInteract(PlayerInteractEvent event) {
		if ((event.getAction() != Action.RIGHT_CLICK_BLOCK && !(event.getAction() == Action.LEFT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.WOODEN_DOOR))
				|| event.isCancelled()) {
			return;
		}
		int id;
		ProtectedBlock block;
		if ((id = plugin.protectFile.getProtection(event.getClickedBlock()
				.getLocation())) != -1) {
			block = plugin.protectFile.getProtection(id);
			User user = new User(plugin, event.getPlayer());
			if (!block.canUse(user)) {
				event.setCancelled(true);
				user.sendMessage(ChatColor.RED
						+ "That block is protected from use!  Use /protect info for more information...");
			} else {
				if (!user.getName()
						.equalsIgnoreCase(block.getOwner().getName())) {
					log.info(user.getName() + " has accessed "
							+ block.getOwner().getName()
							+ "'s protected block. (" + block.GetId() + ")");
				}
			}
		}
	}
}
