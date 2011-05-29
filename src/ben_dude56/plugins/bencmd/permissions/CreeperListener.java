package ben_dude56.plugins.bencmd.permissions;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityTargetEvent;

import ben_dude56.plugins.bencmd.BenCmd;
import ben_dude56.plugins.bencmd.User;

public class CreeperListener extends EntityListener {
	BenCmd plugin;

	public CreeperListener(BenCmd instance) {
		plugin = instance;
	}

	public void onEntityTarget(EntityTargetEvent event) {
		// TODO For version 1.1.2: Provide option to turn passive Creepers off.
		if (event.getEntity().toString().equalsIgnoreCase("CraftCreeper"))
			event.setCancelled(true);
		if (event.getTarget() instanceof Player
				&& User.getUser(plugin, (Player) event.getTarget()).isPoofed())
			event.setCancelled(true);
	}
}
