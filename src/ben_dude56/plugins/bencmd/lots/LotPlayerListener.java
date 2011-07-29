package ben_dude56.plugins.bencmd.lots;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;

import ben_dude56.plugins.bencmd.BenCmd;
import ben_dude56.plugins.bencmd.User;

public class LotPlayerListener extends PlayerListener {

	public Location loc;
	public String owner;
	BenCmd plugin;
	public HashMap<String, Corner> corner = new HashMap<String, Corner>();

	public LotPlayerListener(BenCmd instance) {
		plugin = instance;
	}

	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Player player = event.getPlayer();
			if (User.getUser(plugin, player).hasPerm("isLandlord")
					&& player.getItemInHand().getType() == Material.WOOD_SPADE) {
				checkPlayer(player.getName());
				if (this.corner.get(player.getName()).corner2 != event.getClickedBlock().getLocation()) {
					this.corner.get(player.getName()).setCorner2(
							event.getClickedBlock().getLocation());
					Location corner2 = this.corner.get(player.getName())
					.getCorner2();
					player.sendMessage(ChatColor.LIGHT_PURPLE
							+ "Corner 2 set at [X: " + corner2.getX() + ", Y: "
							+ corner2.getY() + ", Z: " + corner2.getZ() + ", W: "
							+ corner2.getWorld().getName() + "]");
				}
				return;
			}
		} else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
			Player player = event.getPlayer();
			if (User.getUser(plugin, player).hasPerm("isLandlord")
					&& player.getItemInHand().getType() == Material.WOOD_SPADE) {
				checkPlayer(player.getName());
				if (this.corner.get(player.getName()).corner1 != event.getClickedBlock().getLocation()) {
					this.corner.get(player.getName()).setCorner1(
							event.getClickedBlock().getLocation());
					Location corner1 = this.corner.get(player.getName())
					.getCorner1();
					player.sendMessage(ChatColor.LIGHT_PURPLE
							+ "Corner 1 set at [X: " + corner1.getX() + ", Y: "
							+ corner1.getY() + ", Z: " + corner1.getZ() + ", W: "
							+ corner1.getWorld().getName() + "]");
				}
				return;
			}
		}
	}

	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
		String LotID = String.valueOf(plugin.lots.isInLot(event
				.getBlockClicked().getLocation()));
		if (!LotID.equalsIgnoreCase("-1")) {
			Player player = event.getPlayer();
			String owner = plugin.lots.getLot(LotID).getOwner();
			if (!plugin.lots.canBuildHere(player, event.getBlockClicked()
					.getLocation())) {
				event.setCancelled(true);
				player.sendMessage("You cannot build here. This lot is owned by "
						+ owner);
			}
		}
	}

	public void onPlayerBucketFill(PlayerBucketFillEvent event) {
		String LotID = String.valueOf(plugin.lots.isInLot(event
				.getBlockClicked().getLocation()));
		if (!LotID.equalsIgnoreCase("-1")) {
			Player player = event.getPlayer();
			String owner = plugin.lots.getLot(LotID).getOwner();
			if (!plugin.lots.canBuildHere(player, event.getBlockClicked()
					.getLocation())) {
				event.setCancelled(true);
				player.sendMessage("You cannot build here. This lot is owned by "
						+ owner);
			}
		}
	}

	public void checkPlayer(String player) {
		if (!corner.containsKey(player)) {
			corner.put(player, new Corner());
		}
	}
}