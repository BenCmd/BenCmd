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

	@Override
	public void run() {
		for(Player player : flyDetect.plugin.getServer().getOnlinePlayers()) {
			Location playerLocation = player.getLocation();
			playerLocation.setY(playerLocation.getY() - 1);
			if(playerLocation.getBlock().getType() == Material.AIR) {
				flyDetect.detect(player);
			} else {
				flyDetect.undetect(player);
			}
		}
	}

}
