package com.bendude56.bencmd.multiworld;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.World;
import org.bukkit.WorldCreator;

import com.bendude56.bencmd.BenCmdFile;

public class MultiworldController extends BenCmdFile {
	private HashMap<String, BenCmdWorld> worlds;

	public MultiworldController() {
		super("worlds.db", "--BenCmd Worlds File--", true);
		worlds = new HashMap<String, BenCmdWorld>();
		loadFile();
		loadAll();
	}

	@Override
	public void saveAll() {
		for (Entry<String, BenCmdWorld> e : worlds.entrySet()) {
			updateWorldEntry(e.getValue(), false);
		}
		saveFile();
	}

	@Override
	public void loadAll() {
		worlds.clear();
		for (Entry<Object, Object> e : getFile().entrySet()) {
			long seed;
			try {
				seed = Integer.parseInt(((String) e.getValue()).split("\\|")[0]);
			} catch (NumberFormatException ex) {
				seed = ((String) e.getValue()).split("\\|")[0].hashCode();
			}
			boolean passive, neutral, aggro;
			try {
				passive = ((String) e.getValue()).split("\\|")[1] == "true";
			} catch (IndexOutOfBoundsException ex) {
				passive = true;
			}
			try {
				neutral = ((String) e.getValue()).split("\\|")[2] == "true";
			} catch (IndexOutOfBoundsException ex) {
				neutral = true;
			}
			try {
				aggro = ((String) e.getValue()).split("\\|")[3] == "true";
			} catch (IndexOutOfBoundsException ex) {
				aggro = true;
			}
			WorldCreator w = new WorldCreator((String) e.getKey());
			boolean update = false;
			if (seed == 0) {
				w.seed();
				update = true;
			} else {
				w.seed(seed);
			}
			worlds.put(w.name(), new BenCmdWorld(w.createWorld(), passive, neutral, aggro));
			if (update) {
				updateWorldEntry(worlds.get(w.name()), true);
			}
		}
	}
	
	public void removeWorldEntry(BenCmdWorld w) {
		worlds.remove(w.getName());
		getFile().remove(w.getName());
		saveFile();
	}
	
	public void updateWorldEntry(BenCmdWorld w, boolean save) {
		if (!worlds.containsKey(w.getName())) {
			worlds.put(w.getName(), w);
		}
		getFile().put(w.getName(), w.getSeed() + "|" + ((w.getAllowSpawnPassive()) ? "true" : "false") + "|" + ((w.getAllowSpawnNeutral()) ? "true" : "false") + "|" + ((w.getAllowSpawnAggressive()) ? "true" : "false"));
		if (save) {
			saveFile();
		}
	}
	
	public BenCmdWorld getWorld(World w) {
		return getWorld(w.getName());
	}
	
	public BenCmdWorld getWorld(String w) {
		if (worlds.containsKey(w)) {
			return worlds.get(w);
		} else {
			return null;
		}
	}

	public void createWorld(String name, long seed) throws IOException {
		try {
			WorldCreator w = new WorldCreator(name);
			if (seed == 0) {
				w.seed();
			} else {
				w.seed(seed);
			}
			worlds.put(w.name(), new BenCmdWorld(w.createWorld(), true, true, true));
			updateWorldEntry(worlds.get(w.name()), true);
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

}
