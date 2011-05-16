package ben_dude56.plugins.bencmd.warps;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.block.CraftSign;
import org.bukkit.craftbukkit.entity.CraftSkeleton;
import org.bukkit.craftbukkit.entity.CraftSpider;
import org.bukkit.craftbukkit.entity.CraftZombie;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.inventory.ItemStack;

import ben_dude56.plugins.bencmd.BenCmd;
import ben_dude56.plugins.bencmd.User;

public class DeathListener extends EntityListener {
	BenCmd plugin;

	public DeathListener(BenCmd instance) {
		plugin = instance;
	}

	public void onEntityDeath(EntityDeathEvent event) {
		if (event.getEntity() instanceof CraftZombie) {
			event.getDrops().clear();
			event.getDrops().add(new ItemStack(Material.PAPER, 5));
		}
		if (event.getEntity() instanceof CraftSkeleton) {
			event.getDrops().add(new ItemStack(Material.PAPER, 3));
		}
		if (event.getEntity() instanceof CraftSpider) {
			event.getDrops().add(new ItemStack(Material.PAPER, 6));
		}
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		Player player = (Player) event.getEntity();
		if (plugin.perm.userFile.hasPermission(player.getName(), "canWarp",
				true, true)
				&& plugin.perm.userFile.hasPermission(player.getName(),
						"canBackOnDeath", true, true)) {
			plugin.checkpoints.SetPreWarp(player);
			player.sendMessage(ChatColor.RED
					+ "Use /back to return to your death point...");
		}
		if (plugin.mainProperties.getBoolean("gravesEnabled", true)) {
			Location graveLoc = new Location(player.getWorld(), player
					.getLocation().getBlockX(), player.getLocation()
					.getBlockY(), player.getLocation().getBlockZ());
			Block grave = player.getWorld().getBlockAt(graveLoc);
			while (grave.getType() != Material.AIR) {
				if (graveLoc.getBlockY() == 1) {
					return;
				}
				graveLoc.setY(graveLoc.getY() + 1);
				grave = player.getWorld().getBlockAt(graveLoc);
			}
			grave.setType(Material.SIGN_POST);
			CraftSign graveSign = new CraftSign(grave);
			graveSign.setLine(1, "R.I.P.");
			graveSign.setLine(2, player.getName());
			graveSign.update();
		}
	}

	public void onEntityDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		if (new User(plugin, (Player) event.getEntity()).isPoofed()) {
			event.setCancelled(true);
		}
		Player player = (Player) event.getEntity();
		if (plugin.isGod(player)) {
			event.setCancelled(true);
		}
	}
}
