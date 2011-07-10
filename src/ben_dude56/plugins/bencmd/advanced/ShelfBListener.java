package ben_dude56.plugins.bencmd.advanced;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;

import ben_dude56.plugins.bencmd.BenCmd;

public class ShelfBListener extends BlockListener {
	private BenCmd plugin;

	public ShelfBListener(BenCmd instance) {
		plugin = instance;
	}

	public void onBlockBreak(BlockBreakEvent event) {
		for (int i = 0; i < plugin.graves.size(); i++) {
			if (plugin.graves.get(i).getBlock().getLocation()
					.equals(event.getBlock().getLocation())) {
				event.setCancelled(true);
				plugin.graves.get(i).destroyBy(event.getPlayer());
				return;
			}
		}
		if (event.isCancelled()) {
			return;
		}
		Shelf shelf;
		if ((shelf = plugin.shelff.getShelf(event.getBlock().getLocation())) != null) {
			plugin.shelff.remShelf(shelf.getLocation());
		}
	}
}
