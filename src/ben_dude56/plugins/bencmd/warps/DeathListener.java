package ben_dude56.plugins.bencmd.warps;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.entity.CraftCreeper;
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
import ben_dude56.plugins.bencmd.lots.sparea.DropTable;

public class DeathListener extends EntityListener {
	BenCmd plugin;

	public DeathListener(BenCmd instance) {
		plugin = instance;
	}

	public void onEntityDeath(EntityDeathEvent event) {
		if (event.getEntity() instanceof CraftZombie) {
			if (plugin.mainProperties.getString("zombieDrop", "").isEmpty()) {
				return;
			}
			DropTable table = new DropTable(plugin,
					plugin.mainProperties.getString("zombieDrop", ""));
			event.getDrops().clear();
			for (ItemStack item : table.getRandomDrops(new Random())) {
				event.getDrops().add(item);
			}
		}
		if (event.getEntity() instanceof CraftSkeleton) {
			if (plugin.mainProperties.getString("skeletonDrop", "").isEmpty()) {
				return;
			}
			DropTable table = new DropTable(plugin,
					plugin.mainProperties.getString("skeletonDrop", ""));
			event.getDrops().clear();
			for (ItemStack item : table.getRandomDrops(new Random())) {
				event.getDrops().add(item);
			}
		}
		if (event.getEntity() instanceof CraftSpider) {
			if (plugin.mainProperties.getString("spiderDrop", "").isEmpty()) {
				return;
			}
			DropTable table = new DropTable(plugin,
					plugin.mainProperties.getString("spiderDrop", ""));
			event.getDrops().clear();
			for (ItemStack item : table.getRandomDrops(new Random())) {
				event.getDrops().add(item);
			}
		}
		if (event.getEntity() instanceof CraftCreeper) {
			if (plugin.mainProperties.getString("creeperDrop", "").isEmpty()) {
				return;
			}
			DropTable table = new DropTable(plugin,
					plugin.mainProperties.getString("creeperDrop", ""));
			event.getDrops().clear();
			for (ItemStack item : table.getRandomDrops(new Random())) {
				event.getDrops().add(item);
			}
		}
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		User user = User.getUser(plugin, (Player) event.getEntity());
		if (user.hasPerm("bencmd.warp.back") && user.hasPerm("bencmd.warp.deathback")) {
			plugin.checkpoints.SetPreWarp(user.getHandle());
			user.sendMessage(ChatColor.RED
					+ "Use /back to return to your death point...");
		}
	}

	public void onEntityDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		if (User.getUser(plugin, (Player) event.getEntity()).isPoofed()) {
			event.setCancelled(true);
		}
		Player player = (Player) event.getEntity();
		if (plugin.isGod(player)) {
			event.setCancelled(true);
		}
	}
}
