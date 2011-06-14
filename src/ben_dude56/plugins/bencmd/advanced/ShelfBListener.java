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
		if(event.isCancelled()) {
			return;
		}
		Shelf shelf;
		if((shelf = plugin.shelff.getShelf(event.getBlock().getLocation())) != null) {
			plugin.shelff.remShelf(shelf.getLocation());
		}
	}
}
