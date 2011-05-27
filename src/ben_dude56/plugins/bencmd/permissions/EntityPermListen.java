package ben_dude56.plugins.bencmd.permissions;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.ExplosionPrimeEvent;

import ben_dude56.plugins.bencmd.BenCmd;
import ben_dude56.plugins.bencmd.User;

public class EntityPermListen extends EntityListener {
	BenCmd plugin;

	public EntityPermListen(BenCmd instance) {
		plugin = instance;
	}

	public void onExplosionPrime(ExplosionPrimeEvent event) {
		if (event.getEntity() instanceof TNTPrimed) {
			event.setCancelled(true);
			String logMessage = "REDSTONE tried to detonate TNT at X:"
					+ event.getEntity().getLocation().getBlockX() + "  Y:"
					+ event.getEntity().getLocation().getBlockZ() + ". ";
			Player nearPlayer = nearestPlayer(event.getEntity().getLocation());
			logMessage += "Nearest detected player: " + nearPlayer.getName();
			plugin.getServer()
					.broadcastMessage(
							ChatColor.RED
									+ "Attempted redstone TNT detonation detected! Nearest player:  "
									+ nearPlayer.getName());
			Logger.getLogger("minecraft").info(logMessage);
			if (plugin.mainProperties.getBoolean("attemptRedstoneTntKick",
					false)) {
				User user = new User(plugin, nearPlayer);
				plugin.getServer().broadcastMessage(
						ChatColor.RED + user.getName()
								+ " tried to detonate TNT!");
				user.Kick(plugin.mainProperties.getString("TNTKick",
						"You can't detonate TNT!"));
			}
			return;
		}
	}

	public Player nearestPlayer(Location loc) {
		Player lastPlayer = null;
		double olddistance = 0.0;
		for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
			if (lastPlayer == null) {
				lastPlayer = onlinePlayer;
				olddistance = distanceBetween(loc, lastPlayer.getLocation());
			} else {
				double newdistance = distanceBetween(loc,
						onlinePlayer.getLocation());
				if (newdistance < olddistance) {
					lastPlayer = onlinePlayer;
					olddistance = newdistance;
				}
			}
		}
		return lastPlayer;
	}

	public Double distanceBetween(Location loc1, Location loc2) {
		Double distance;
		distance = Math.abs(loc1.getX() - loc2.getX());
		distance += Math.abs(loc1.getY() - loc2.getY());
		distance += Math.abs(loc1.getZ() - loc2.getZ());
		return distance;
	}
}
