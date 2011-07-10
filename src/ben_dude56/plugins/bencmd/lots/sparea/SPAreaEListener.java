package ben_dude56.plugins.bencmd.lots.sparea;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.block.CraftSign;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.inventory.ItemStack;

import ben_dude56.plugins.bencmd.BenCmd;
import ben_dude56.plugins.bencmd.User;
import ben_dude56.plugins.bencmd.advanced.Grave;

public class SPAreaEListener extends EntityListener {
	BenCmd plugin;

	public SPAreaEListener(BenCmd instance) {
		plugin = instance;
	}

	public void onEntityDamage(EntityDamageEvent e) {
		if (e instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
			if (event.getDamager() instanceof Player
					&& event.getEntity() instanceof Player) {
				event.setCancelled(!(inPVP((Player) event.getDamager()) && inPVP((Player) event
						.getEntity())));
			}
		}
	}

	public void onEntityDeath(EntityDeathEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		User user = User.getUser(plugin, (Player) event.getEntity());
		if (inPVP(user.getHandle())) {
			List<ItemStack> realDrops = new ArrayList<ItemStack>();
			for (ItemStack i : event.getDrops()) {
				if (plugin.prices.isCurrency(i)) {
					realDrops.add(i);
				}
			}
			event.getDrops().clear();
			event.getDrops().addAll(realDrops);
			return;
		}
		if (plugin.mainProperties.getBoolean("gravesEnabled", true)) {
			for (int i = 0; i < plugin.graves.size(); i++) {
				Grave g = plugin.graves.get(i);
				if (g.getPlayer().equals((Player) event.getEntity())) {
					g.delete();
					plugin.graves.remove(i);
				}
			}
			Location graveLoc = new Location(user.getHandle().getWorld(), user
					.getHandle().getLocation().getBlockX(), user.getHandle()
					.getLocation().getBlockY(), user.getHandle().getLocation()
					.getBlockZ());
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
			graveSign.setLine(2, user.getDisplayName());
			graveSign.update();
			plugin.graves.add(new Grave(plugin, grave, (Player) event
					.getEntity(), event.getDrops(), plugin.mainProperties
					.getInteger("graveDuration", 180)));
			((Player) event.getEntity())
					.sendMessage(ChatColor.RED
							+ "You have died... You can retrieve your items by breaking your gravestone...");
			event.getDrops().clear();
		}
	}

	public boolean inPVP(Player p) {
		for (SPArea a : plugin.spafile.listAreas()) {
			if (a instanceof PVPArea) {
				if (a.insideArea(p.getLocation())) {
					return true;
				}
			}
		}
		return false;
	}
}
