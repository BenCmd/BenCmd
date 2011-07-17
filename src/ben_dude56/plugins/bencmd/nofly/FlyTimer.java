package ben_dude56.plugins.bencmd.nofly;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import ben_dude56.plugins.bencmd.User;

public class FlyTimer implements Runnable {
	FlyDetect flyDetect;

	public FlyTimer(FlyDetect detect) {
		flyDetect = detect;
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

	@Override
	public void run() {
		try {
			for (Player player : flyDetect.plugin.getServer()
					.getOnlinePlayers()) {
				if (User.getUser(flyDetect.plugin, player).hasPerm("canFly")) {
					return;
				}
				Location loc = player.getLocation();
				if (!flyDetect.lastL.containsKey(player)) {
					flyDetect.lastL.put(player, player.getLocation());
				} else {
					flyDetect.riseChange(player, player.getLocation().getY()
							- flyDetect.lastL.get(player).getY());
					flyDetect.lastL.put(player, player.getLocation());
				}
				loc.setY(loc.getY() - 1);
				if (!onBlock(player.getLocation()) && !onBlock(loc)) {
					flyDetect.timeDetect(player);
				} else {
					flyDetect.timeUndetect(player);
				}
			}
		} catch (Exception e) {
		}
	}

}
