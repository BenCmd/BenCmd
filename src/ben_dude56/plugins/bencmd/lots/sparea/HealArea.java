package ben_dude56.plugins.bencmd.lots.sparea;

import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import ben_dude56.plugins.bencmd.BenCmd;

public class HealArea extends SPArea {

	private Timer t;

	public HealArea(BenCmd instance, String key, String value)
			throws NumberFormatException, NullPointerException,
			IndexOutOfBoundsException {
		super(instance, key, value);
		t = new Timer();
		t.schedule(new TimerTask() {
			public void run() {
				heal();
			}
		}, 0, 1000);
	}

	public HealArea(BenCmd instance, Integer id, Location corner1,
			Location corner2) {
		super(instance, id, corner1, corner2);
		t = new Timer();
		t.schedule(new TimerTask() {
			public void run() {
				heal();
			}
		}, 0, 1000);
	}

	private void heal() {
		for (Player p : plugin.getServer().getOnlinePlayers()) {
			if (super.insideArea(p.getLocation()) && p.getHealth() != 20
					&& !p.isDead()) {
				p.setHealth(p.getHealth() + 1);
			}
		}
	}

	public String getValue() {
		String internal = super.getInternalValue();
		return "heal" + internal.substring(0, internal.length() - 1);
	}

	public void delete() {
		t.cancel();
	}

}
