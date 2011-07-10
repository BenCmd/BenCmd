package ben_dude56.plugins.bencmd.lots.sparea;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import ben_dude56.plugins.bencmd.BenCmd;

public class SPAreaFile extends Properties {
	private static final long serialVersionUID = 0L;

	private String proFile;
	private BenCmd plugin;

	private HashMap<Integer, SPArea> areas = new HashMap<Integer, SPArea>();

	public SPAreaFile(BenCmd instance, String file) {
		plugin = instance;
		proFile = file;
		loadFile();
		loadAreas();
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
				} else {
					plugin.log.warning("SPArea " + key
							+ " is invalid and was ignored.");
					continue;
				}
			} catch (Exception e) {
				plugin.log.warning("SPArea " + key
						+ " is invalid and was ignored.");
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
		saveFile("--SPECIAL PURPOSE AREAS--");
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
		saveFile("--SPECIAL PURPOSE AREAS--");
	}

}
