package com.bendude56.bencmd.lots.sparea;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.BenCmdFile;

public class SPAreaFile extends BenCmdFile {
	private HashMap<Integer, SPArea>	areas	= new HashMap<Integer, SPArea>();
	private Integer						task;

	public SPAreaFile() {
		super("sparea.db", "--BenCmd SPArea File--", true);
		loadFile();
		loadAll();
		task = Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask(BenCmd.getPlugin(), new TimeCheck(), 20, 20);
	}

	public void forceStopTimer() {
		Bukkit.getScheduler().cancelTask(task);
	}

	public void loadAll() {
		areas.clear();
		for (int i = 0; i < getFile().size(); i++) {
			String key = (String) getFile().keySet().toArray()[i];
			String value = (String) getFile().values().toArray()[i];
			int id;
			try {
				id = Integer.parseInt(key);
			} catch (NumberFormatException e) {
				BenCmd.log(Level.WARNING, "SPArea " + key + " is invalid and was ignored.");
				continue;
			}
			String type = value.split("/")[0];
			try {
				if (type.equals("pvp")) {
					areas.put(id, new PVPArea(key, value));
				} else if (type.equals("msg")) {
					areas.put(id, new MsgArea(key, value));
				} else if (type.equals("heal")) {
					areas.put(id, new HealArea(key, value));
				} else if (type.equals("dmg")) {
					areas.put(id, new DamageArea(key, value));
				} else if (type.equals("tr")) {
					areas.put(id, new TRArea(key, value));
				} else if (type.equals("grp")) {
					areas.put(id, new GroupArea(key, value));
				} else {
					BenCmd.log(Level.WARNING, "SPArea " + key + " is invalid and was ignored.");
					continue;
				}
			} catch (Exception e) {
				BenCmd.log(Level.WARNING, "SPArea " + key + " is invalid and was ignored.");
				continue;
			}
		}
	}

	public List<SPArea> listAreas() {
		List<SPArea> areas = new ArrayList<SPArea>();
		areas.addAll(this.areas.values());
		return areas;
	}

	public void updateArea(SPArea area, boolean saveFile) throws UnsupportedOperationException {
		getFile().put(area.getAreaID().toString(), area.getValue());
		if (saveFile)
			saveFile();
	}

	public int nextId() {
		for (int i = 0; true; i++) {
			if (!areas.containsKey(i)) {
				return i;
			}
		}
	}

	public void addArea(SPArea area) throws UnsupportedOperationException {
		updateArea(area, true);
		areas.put(area.getAreaID(), area);
	}

	public SPArea byId(int id) {
		if (areas.containsKey(id)) {
			return areas.get(id);
		} else {
			return null;
		}
	}

	public void removeArea(SPArea area) {
		area.delete();
		getFile().remove(area.getAreaID().toString());
		areas.remove(area.getAreaID());
		saveFile();
	}

	public class TimeCheck implements Runnable {

		public void run() {
			for (SPArea a : new ArrayList<SPArea>(areas.values())) {
				if (a instanceof TimedArea) {
					((TimedArea) a).preTick();
				}
			}
		}
	}

	@Override
	public void saveAll() {
		for (Map.Entry<Integer, SPArea> e : areas.entrySet()) {
			updateArea(e.getValue(), false);
		}
		saveFile();
	}
}
