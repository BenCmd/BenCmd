package com.bendude56.bencmd.lots.sparea;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.BenCmdFile;


public class SPAreaFile extends BenCmdFile {
	private HashMap<Integer, SPArea> areas = new HashMap<Integer, SPArea>();

	public SPAreaFile() {
		super("sparea.db", "--BenCmd SPArea File--", true);
		loadFile();
		loadAll();
		Bukkit.getServer().getScheduler()
				.scheduleAsyncRepeatingTask(BenCmd.getPlugin(), new TimeCheck(), 20, 20);
	}

	public void loadAll() {
		BenCmd plugin = BenCmd.getPlugin();
		areas.clear();
		for (int i = 0; i < getFile().size(); i++) {
			String key = (String) getFile().keySet().toArray()[i];
			String value = (String) getFile().values().toArray()[i];
			int id;
			try {
				id = Integer.parseInt(key);
			} catch (NumberFormatException e) {
				plugin.log.warning("SPArea " + key
						+ " is invalid and was ignored.");
				plugin.bLog.warning("SPArea " + key + " failed to load!");
				continue;
			}
			String type = value.split("/")[0];
			try {
				// TODO Remove need for plugin references for these constructors
				if (type.equals("pvp")) {
					areas.put(id, new PVPArea(plugin, key, value));
				} else if (type.equals("msg")) {
					areas.put(id, new MsgArea(plugin, key, value));
				} else if (type.equals("heal")) {
					areas.put(id, new HealArea(plugin, key, value));
				} else if (type.equals("dmg")) {
					areas.put(id, new DamageArea(plugin, key, value));
				} else if (type.equals("tr")) {
					areas.put(id, new TRArea(plugin, key, value));
				} else {
					plugin.log.warning("SPArea " + key
							+ " is invalid and was ignored.");
					plugin.bLog.warning("SPArea " + key + " failed to load!");
					continue;
				}
			} catch (Exception e) {
				plugin.log.warning("SPArea " + key
						+ " is invalid and was ignored.");
				plugin.bLog.warning("SPArea " + key + " failed to load!");
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
