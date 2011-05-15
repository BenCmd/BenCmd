package ben_dude56.plugins.bencmd.nofly;

import java.util.TimerTask;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class FlyTimer extends TimerTask {
	
	FlyDetect flyDetect;
	
	public FlyTimer(FlyDetect detect) {
		flyDetect = detect;
	}
	
	public boolean onBlock(Player player) {
		Location loc = player.getLocation();
		loc.setY(loc.getY() - 1);
		loc.setX(loc.getX() - 1);
		loc.setZ(loc.getZ() - 1);
		if(loc.getBlock().getType() != Material.AIR) {
			return true;
		}
		loc.setZ(loc.getZ() + 1);
		if(loc.getBlock().getType() != Material.AIR) {
			return true;
		}
		loc.setZ(loc.getZ() + 1);
		if(loc.getBlock().getType() != Material.AIR) {
			return true;
		}
		loc.setX(loc.getX() + 1);
		if(loc.getBlock().getType() != Material.AIR) {
			return true;
		}
		loc.setZ(loc.getZ() - 1);
		if(loc.getBlock().getType() != Material.AIR) {
			return true;
		}
		loc.setZ(loc.getZ() - 1);
		if(loc.getBlock().getType() != Material.AIR) {
			return true;
		}
		loc.setX(loc.getX() + 1);
		if(loc.getBlock().getType() != Material.AIR) {
			return true;
		}
		loc.setZ(loc.getZ() + 1);
		if(loc.getBlock().getType() != Material.AIR) {
			return true;
		}
		loc.setZ(loc.getZ() + 1);
		if(loc.getBlock().getType() != Material.AIR) {
			return true;
		}
		return false;
	}

	@Override
	public void run() {
		for(Player player : flyDetect.plugin.getServer().getOnlinePlayers()) {
			if(!onBlock(player)) {
				flyDetect.detect(player);
			} else {
				flyDetect.undetect(player);
			}
		}
	}

}
