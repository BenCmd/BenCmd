package ben_dude56.plugins.bencmd.warps;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.block.CraftSign;
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
import ben_dude56.plugins.bencmd.invtools.kits.Kit;

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
			Kit kit = plugin.kits.getKit(plugin.mainProperties.getString(
					"zombieDrop", ""));
			if (kit == null) {
				plugin.log
						.warning("Kit specified for zombie drops doesn't exist!");
				return;
			}
			event.getDrops().clear();
			for (ItemStack item : kit.getItems()) {
				event.getDrops().add(item);
			}
		}
		if (event.getEntity() instanceof CraftSkeleton) {
			if (plugin.mainProperties.getString("skeletonDrop", "").isEmpty()) {
				return;
			}
			Kit kit = plugin.kits.getKit(plugin.mainProperties.getString(
					"skeletonDrop", ""));
			if (kit == null) {
				plugin.log
						.warning("Kit specified for skeleton drops doesn't exist!");
				return;
			}
			event.getDrops().clear();
			for (ItemStack item : kit.getItems()) {
				event.getDrops().add(item);
			}
		}
		if (event.getEntity() instanceof CraftSpider) {
			if (plugin.mainProperties.getString("spiderDrop", "").isEmpty()) {
				return;
			}
			Kit kit = plugin.kits.getKit(plugin.mainProperties.getString(
					"spiderDrop", ""));
			if (kit == null) {
				plugin.log
						.warning("Kit specified for spider drops doesn't exist!");
				return;
			}
			event.getDrops().clear();
			for (ItemStack item : kit.getItems()) {
				event.getDrops().add(item);
			}
		}
		if (event.getEntity() instanceof CraftCreeper) {
			if (plugin.mainProperties.getString("creeperDrop", "").isEmpty()) {
				return;
			}
			Kit kit = plugin.kits.getKit(plugin.mainProperties.getString(
					"creeperDrop", ""));
			if (kit == null) {
				plugin.log
						.warning("Kit specified for creeper drops doesn't exist!");
				return;
			}
			event.getDrops().clear();
			for (ItemStack item : kit.getItems()) {
				event.getDrops().add(item);
			}
		}
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		User user = User.getUser(plugin,(Player) event.getEntity());
		if (user.hasPerm("canWarp")
				&& user.hasPerm("canBackOnDeath")) {
			plugin.checkpoints.SetPreWarp(user.getHandle());
			user.sendMessage(ChatColor.RED
					+ "Use /back to return to your death point...");
		}
		if (plugin.mainProperties.getBoolean("gravesEnabled", true)) {
			Location graveLoc = new Location(user.getHandle().getWorld(), user.getHandle()
					.getLocation().getBlockX(), user.getHandle().getLocation()
					.getBlockY(), user.getHandle().getLocation().getBlockZ());
			Block grave = user.getHandle().getWorld().getBlockAt(graveLoc);
			while (grave.getType() != Material.AIR) {
				if (graveLoc.getBlockY() == 1) {
					return;
				}
				graveLoc.setY(graveLoc.getY() + 1);
				grave = user.getHandle().getWorld().getBlockAt(graveLoc);
			}
			grave.setType(Material.SIGN_POST);
			CraftSign graveSign = new CraftSign(grave);
			graveSign.setLine(1, "R.I.P.");
			graveSign.setLine(2, user.getName());
			graveSign.update();
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
