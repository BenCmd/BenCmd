package ben_dude56.plugins.bencmd.nofly;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import ben_dude56.plugins.bencmd.BenCmd;
import ben_dude56.plugins.bencmd.User;
import ben_dude56.plugins.bencmd.permissions.Action.ActionType;

public class FlyDetect {
	public BenCmd plugin;
	public Timer flyTime;
	public long lastUpdate;
	private HashMap<Player, Long> timed;
	private HashMap<Player, Double> rise;
	private HashMap<String, Integer> offenders;
	private List<FlyResponse> responses;
	private FlyResponse jailResponse;
	protected HashMap<Player, Location> lastL;

	public FlyDetect(BenCmd instance) {
		plugin = instance;
		flyTime = new Timer();
		flyTime.schedule(new FlyTimer(this), 0, 100);
		lastUpdate = new Date().getTime();
		timed = new HashMap<Player, Long>();
		rise = new HashMap<Player, Double>();
		offenders = new HashMap<String, Integer>();
		responses = new ArrayList<FlyResponse>();
		lastL = new HashMap<Player, Location>();
		for(String s : plugin.mainProperties.getString("flyResponses", "p").split(",")) {
			if (s.equalsIgnoreCase("p")) {
				responses.add(FlyResponse.PULLDOWN);
			} else if (s.equalsIgnoreCase("r")) {
				responses.add(FlyResponse.RESPAWN);
			} else if (s.equalsIgnoreCase("k")) {
				responses.add(FlyResponse.KICK);
			} else if (s.equalsIgnoreCase("j")) {
				responses.add(FlyResponse.JAIL);
			} else if (s.equalsIgnoreCase("b")) {
				responses.add(FlyResponse.BAN);
			} else if (s.equalsIgnoreCase("w")) {
				responses.add(FlyResponse.WARN);
			}
		}
		String s = plugin.mainProperties.getString("jailFlyResponse", "p");
		if (s.equalsIgnoreCase("p")) {
			jailResponse = FlyResponse.PULLDOWN;
		} else if (s.equalsIgnoreCase("r")) {
			jailResponse = FlyResponse.RESPAWN;
		} else if (s.equalsIgnoreCase("k")) {
			jailResponse = FlyResponse.KICK;
		} else if (s.equalsIgnoreCase("j")) {
			jailResponse = FlyResponse.JAIL;
		} else if (s.equalsIgnoreCase("b")) {
			jailResponse = FlyResponse.BAN;
		} else if (s.equalsIgnoreCase("w")) {
			jailResponse = FlyResponse.WARN;
		}
		if (jailResponse == FlyResponse.RESPAWN) {
			plugin.log.warning("A jailFlyResponse of 'r' may allow people to escape jail by flying and being respawned!");
		} else if (jailResponse == FlyResponse.JAIL) {
			plugin.log.warning("A jailFlyResponse of 'j' will not produce any action when a person in jail flies, as you cannot double-jail someone!");
		}
	}

	public boolean isTimeDetected(Player player) {
		return timed.containsKey(player);
	}
	
	public boolean isRiseDetected(Player player) {
		return rise.containsKey(player);
	}

	public void addToTimed(Player player) {
		timed.put(player, 0L);
	}

	public int getOffences(Player player) {
		if (offenders.containsKey(player.getName())) {
			return offenders.get(player.getName());
		} else {
			return 0;
		}
	}
	
	public void actionOnce(Player player, FlyResponse action) {
		switch (action) {
		case PULLDOWN:
			Location l = player.getLocation();
			while(l.getBlock().getType() == Material.AIR) {
				if (l.getY() <= 0) {
					l.setY(l.getWorld().getHighestBlockYAt(l) - 1);
				} else {
					l.setY(l.getY() - 1);
				}
			}
			l.setY(l.getY() + 1);
			player.teleport(l);
			plugin.log.info(player.getName() + " was sent to ground level for flying!");
			break;
		case RESPAWN:
			User.getUser(plugin, player).Spawn();
			plugin.checkpoints.RemovePreWarp(player);
			player.sendMessage(ChatColor.RED + "You have been respawned for flying!");
			plugin.log.info(player.getName() + " was sent to spawn for flying!");
			lastL.put(player, player.getLocation());
			break;
		case KICK:
			User.getUser(plugin, player).Kick("You have been kicked for flying!");
			plugin.log.info(player.getName() + " was kicked for flying!");
			break;
		case JAIL:
			User user = User.getUser(plugin, player);
			if (user.isJailed() != null) {
				plugin.log.warning("Cannot jail " + user.getName() + " for flying, because they're already jailed!");
			} else {
				plugin.actions.addAction(user, ActionType.JAIL, 3600000L);
				plugin.jail.SendToJail(player);
				player.sendMessage(ChatColor.RED + "You have been jailed for 1 hour for flying!");
				lastL.put(player, player.getLocation());
				plugin.log.info(player.getName() + " was jailed (1hr) for flying!");
			}
			break;
		case BAN:
			User u = User.getUser(plugin, player);
			plugin.actions.addAction(u, ActionType.BAN, 3600000L);
			u.Kick("You have been banned for 1 hour for flying!");
			plugin.log.info(player.getName() + " was banned (1hr) for flying!");
			break;
		case WARN:
			player.sendMessage(ChatColor.RED + "WARNING: You have been detected flying! Action may be taken...");
			plugin.log.info(player.getName() + " has been warned for flying!");
			break;
		}
	}
	
	public void action(Player player) {
		if (User.getUser(plugin, player).isJailed() != null) {
			actionOnce(player, jailResponse);
		} else {
			if (!offenders.containsKey(player)) {
				offenders.put(player.getName(), 0);
				actionOnce(player, responses.get(0));
			} else if (offenders.get(player) > responses.size()) {
				actionOnce(player, responses.get(responses.size() - 1));
			} else {
				actionOnce(player, responses.get(offenders.get(player)));
				addOffence(player);
			}
		}
	}

	public void addOffence(Player player) {
		if (offenders.containsKey(player.getName())) {
			offenders.put(player.getName(), getOffences(player) + 1);
		} else {
			offenders.put(player.getName(), 1);
		}
	}
	
	public void riseChange(Player player, Double change) {
		/*if (lastUpdate + 2000 < new Date().getTime()) {
			lastUpdate = new Date().getTime();
			return; // Probably just lag...
		} else*/ if (change < 0 || onBlock(player.getLocation())) {
			lastUpdate = new Date().getTime();
			if (isRiseDetected(player)) {
				rise.remove(player);
			}
		} else {
			lastUpdate = new Date().getTime();
			if (rise.containsKey(player)) {
				rise.put(player, rise.get(player) + change);
			} else {
				rise.put(player, change);
			}
			if (rise.get(player) >= 2) {
				rise.remove(player);
				action(player);
			}
		}
	}
	
	public boolean onBlock(Location loc) {
		loc.setX(loc.getX() - 1);
		loc.setZ(loc.getZ() - 1);
		if (loc.getBlock().getType() != Material.AIR) {
			return true;
		}
		loc.setZ(loc.getZ() + 1);
		if (loc.getBlock().getType() != Material.AIR) {
			return true;
		}
		loc.setZ(loc.getZ() + 1);
		if (loc.getBlock().getType() != Material.AIR) {
			return true;
		}
		loc.setX(loc.getX() + 1);
		if (loc.getBlock().getType() != Material.AIR) {
			return true;
		}
		loc.setZ(loc.getZ() - 1);
		if (loc.getBlock().getType() != Material.AIR) {
			return true;
		}
		loc.setZ(loc.getZ() - 1);
		if (loc.getBlock().getType() != Material.AIR) {
			return true;
		}
		loc.setX(loc.getX() + 1);
		if (loc.getBlock().getType() != Material.AIR) {
			return true;
		}
		loc.setZ(loc.getZ() + 1);
		if (loc.getBlock().getType() != Material.AIR) {
			return true;
		}
		loc.setZ(loc.getZ() + 1);
		if (loc.getBlock().getType() != Material.AIR) {
			return true;
		}
		return false;
	}

	public void timeDetect(Player player) {
		if (isTimeDetected(player)) {
			if (player.getFallDistance() == 0.0) {
				timed.put(player, timed.get(player) + 1);
				if (timed.get(player) == 10) {
					action(player);
				}
			}
		} else {
			timed.put(player, 0L);
		}
	}

	public void timeUndetect(Player player) {
		if (isTimeDetected(player)) {
			timed.remove(player);
		}
	}
	
	public enum FlyResponse {
		PULLDOWN, RESPAWN, KICK, JAIL, BAN, WARN
	}
}
