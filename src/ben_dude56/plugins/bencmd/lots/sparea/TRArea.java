package ben_dude56.plugins.bencmd.lots.sparea;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import ben_dude56.plugins.bencmd.BenCmd;

public class TRArea extends TimedArea {
	
	private HashMap<Player, Integer> locked;
	private List<Player> inside;
	private int waitTime;

	public TRArea(BenCmd instance, String key, String value)
			throws NumberFormatException, NullPointerException,
			IndexOutOfBoundsException {
		super(instance, key, value.substring(0, value.lastIndexOf('/')) + "/0");
		locked = new HashMap<Player, Integer>();
		inside = new ArrayList<Player>();
		waitTime = Integer.parseInt(value.split("/")[3]);
	}
	
	public TRArea(BenCmd instance, Integer id, Location corner1,
			Location corner2, Integer minimumTime) {
		super(instance, id, corner1, corner2, 0);
		locked = new HashMap<Player, Integer>();
		inside = new ArrayList<Player>();
		waitTime = minimumTime;
	}
	
	public void tick(List<Player> players) {
		if (inside == null) {
			return;
		}
		for(Player p : inside) {
			if(!players.contains(p)) {
				locked.put(p, 0);
			}
		}
		inside = new ArrayList<Player>(players);
		for(int i = 0; i < locked.size(); i++) {
			Player p = (Player) locked.keySet().toArray()[i];
			Integer time = locked.get(p);
			time++;
			if(time == waitTime) {
				locked.remove(p);
				i--;
			} else {
				locked.put(p, time);
			}
		}
	}
	
	public boolean isLocked(Player player) {
		return locked.containsKey(player);
	}

	public int getMinTime() {
		return waitTime;
	}
	
	public void setMinTime(int value) {
		waitTime = value;
		super.plugin.spafile.updateArea(this);
	}
	
	public String getValue() {
		String r = super.getInternalValue();
		return "tr" + r.substring(0, r.lastIndexOf('/')) + "/" + waitTime;
	}
	
}
