package com.bendude56.bencmd.lots.sparea;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.bukkit.Bukkit;
import org.bukkit.util.FileUtil;

import com.bendude56.bencmd.BenCmd;


public class SPAreaFile extends Properties {
	private static final long serialVersionUID = 0L;

	private String proFile;
	private BenCmd plugin;

	private HashMap<Integer, SPArea> areas = new HashMap<Integer, SPArea>();

	public SPAreaFile(BenCmd instance, String file) {
		plugin = instance;
		proFile = file;
		if (new File("plugins/BenCmd/_sparea.db").exists()) {
			plugin.log.warning("SPArea backup file found... Restoring...");
			if (FileUtil.copy(new File("plugins/BenCmd/_sparea.db"), new File(
					file))) {
				new File("plugins/BenCmd/_sparea.db").delete();
				plugin.log.info("Restoration suceeded!");
			} else {
				plugin.log.warning("Failed to restore from backup!");
			}
		}
		loadFile();
		loadAreas();
		Bukkit.getServer().getScheduler()
				.scheduleAsyncRepeatingTask(plugin, new TimeCheck(), 20, 20);
	}

	public void loadFile() {
		File file = new File(proFile);
		if (file.exists()) {
			try {
				load(new FileInputStream(file));
			} catch (IOException e) {
				System.out.println("BenCmd had a problem:");
				e.printStackTrace();
			}
		}
	}

	public void saveFile(String header) {
		File file = new File(proFile);
		if (file.exists()) {
			try {
				store(new FileOutputStream(file), header);
			} catch (IOException e) {
				System.out.println("BenCmd had a problem:");
				e.printStackTrace();
			}
		}
	}

	public void loadAreas() {
		areas.clear();
		for (int i = 0; i < this.size(); i++) {
			String key = (String) this.keySet().toArray()[i];
			String value = (String) this.values().toArray()[i];
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

	public void updateArea(SPArea area) throws UnsupportedOperationException {
		this.put(area.getAreaID().toString(), area.getValue());
		try {
			new File("plugins/BenCmd/_sparea.db").createNewFile();
			if (!FileUtil.copy(new File(proFile), new File(
					"plugins/BenCmd/_sparea.db"))) {
				plugin.log.warning("Failed to back up SPArea database!");
			}
		} catch (IOException e) {
			plugin.log.warning("Failed to back up SPArea database!");
		}
		saveFile("--SPECIAL PURPOSE AREAS--");
		try {
			new File("plugins/BenCmd/_sparea.db").delete();
		} catch (Exception e) { }
	}

	public int nextId() {
		for (int i = 0; true; i++) {
			if (!areas.containsKey(i)) {
				return i;
			}
		}
	}

	public void addArea(SPArea area) throws UnsupportedOperationException {
		updateArea(area);
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
		this.remove(area.getAreaID().toString());
		areas.remove(area.getAreaID());
		try {
			new File("plugins/BenCmd/_sparea.db").createNewFile();
			if (!FileUtil.copy(new File(proFile), new File(
					"plugins/BenCmd/_sparea.db"))) {
				plugin.log.warning("Failed to back up SPArea database!");
			}
		} catch (IOException e) {
			plugin.log.warning("Failed to back up SPArea database!");
		}
		saveFile("--SPECIAL PURPOSE AREAS--");
		try {
			new File("plugins/BenCmd/_sparea.db").delete();
		} catch (Exception e) { }
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
}
