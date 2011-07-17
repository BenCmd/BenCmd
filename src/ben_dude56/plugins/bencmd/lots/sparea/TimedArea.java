package ben_dude56.plugins.bencmd.lots.sparea;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import ben_dude56.plugins.bencmd.BenCmd;

public class TimedArea extends SPArea {
	private HashMap<Player, Integer> timing;
	private int minTime;

	public TimedArea(BenCmd instance, String key, String value)
			throws NumberFormatException, NullPointerException,
			IndexOutOfBoundsException {
		super(instance, key, value);
		timing = new HashMap<Player, Integer>();
		minTime = Integer.parseInt(value.split("/")[3]);
	}

	protected TimedArea(BenCmd instance, Integer id, Location corner1,
			Location corner2, Integer minimumTime) {
		super(instance, id, corner1, corner2);
		timing = new HashMap<Player, Integer>();
		minTime = minimumTime;
	}

	public int getMinTime() {
		return minTime;
	}

	public void setMinTime(int value) {
		minTime = value;
		super.plugin.spafile.updateArea(this);
	}

	protected void preTick() {
		List<Player> inside = new ArrayList<Player>();
		for (Player p : super.plugin.getServer().getOnlinePlayers()) {
			if (super.insideArea(p.getLocation())) {
				if (minTime == 0 || minTime == 1) {
					inside.add(p);
				} else {
					if (timing.containsKey(p)) {
						if (timing.get(p) + 1 >= minTime) {
							inside.add(p);
						}
						timing.put(p, timing.get(p) + 1);
					} else {
						timing.put(p, 1);
					}
				}
			}
		}
		for (Player p : timing.keySet()) {
			if ((timing.get(p) > minTime && !inside.contains(p)) || p == null) {
				timing.remove(p);
			}
		}
		tick(inside);
	}

	protected void tick(List<Player> players) {
		throw new UnsupportedOperationException(
				"tick(List<Player> players) not overridden!");
	}

	public void delete() {
		// Do nothing
	}

	public String getInternalValue() {
		return super.getInternalValue() + minTime;
	}
}
