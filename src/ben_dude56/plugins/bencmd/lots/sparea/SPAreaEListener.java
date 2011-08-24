package ben_dude56.plugins.bencmd.lots.sparea;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.block.CraftSign;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageByProjectileEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.craftbukkit.entity.CraftArrow;
import org.bukkit.craftbukkit.entity.CraftPlayer;

import ben_dude56.plugins.bencmd.BenCmd;
import ben_dude56.plugins.bencmd.User;
import ben_dude56.plugins.bencmd.advanced.Grave;
import ben_dude56.plugins.bencmd.advanced.npc.EntityNPC;
import ben_dude56.plugins.bencmd.advanced.npc.NPC;
import ben_dude56.plugins.bencmd.advanced.npc.Damageable;

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
				if (((CraftPlayer)event.getDamager()).getHandle() instanceof EntityNPC)
				{
					if (plugin.isGod((Player)event.getEntity())) 
						event.setCancelled(true);
					return;
				}
				NPC npc = plugin.npcs.getNPC((EntityNPC) ((CraftPlayer) event.getEntity()).getHandle());
				if(npc instanceof Damageable)
				{
					((Damageable) npc).onDamage(event.getDamager(), event.getDamage());
					return;
				}
				event.setCancelled(!(inPVP((Player) event.getDamager()) != null && inPVP((Player) event
						.getEntity()) != null));
			}
		} else if (e instanceof EntityDamageByProjectileEvent) {
			if (e.getEntity().equals(((CraftArrow)((EntityDamageByProjectileEvent) e).getDamager()).getShooter())) {
				e.setCancelled(true);
				return;
			}
		}
	}

	public void onEntityDeath(EntityDeathEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		User user = User.getUser(plugin, (Player) event.getEntity());
		PVPArea a;
		if ((a = inPVP(user.getHandle())) != null) {
			HashMap<ItemStack, PVPArea.DropMode> result = a.getDrops(event
					.getDrops());
			List<ItemStack> toReturn = new ArrayList<ItemStack>();
			event.getDrops().clear();
			for (int i = 0; i < result.size(); i++) {
				ItemStack item = (ItemStack) result.keySet().toArray()[i];
				PVPArea.DropMode mode = result.get(item);
				if (mode == PVPArea.DropMode.DROP) {
					event.getDrops().add(item);
				} else if (mode == PVPArea.DropMode.KEEP) {
					toReturn.add(item);
				}
			}
			plugin.returns.put(user.getHandle(), toReturn);
			return;
		}
		if (plugin.mainProperties.getBoolean("gravesEnabled", true)) {
			for (int i = 0; i < plugin.graves.size(); i++) {
				Grave g = plugin.graves.get(i);
				if (g.getPlayer().equals((Player) event.getEntity())) {
					if (plugin.mainProperties.getBoolean(
							"newerGraveOverwrites", false)) {
						g.delete();
						plugin.graves.remove(i);
					} else {
						return;
					}
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

	public PVPArea inPVP(Player p) {
		for (SPArea a : plugin.spafile.listAreas()) {
			if (a instanceof PVPArea) {
				if (a.insideArea(p.getLocation())) {
					return (PVPArea) a;
				}
			}
		}
		return null;
	}
}
