package ben_dude56.plugins.bencmd.advanced.npc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftChunk;

import ben_dude56.plugins.bencmd.BenCmd;
import ben_dude56.plugins.bencmd.advanced.npc.BlacksmithNPC.*;

public class NPCFile extends Properties {
	private static final long serialVersionUID = 0L;
	
	private String filename;
	private HashMap<Integer, NPC> npcs;
	private BenCmd plugin;

	public NPCFile(BenCmd instance, String file) {
		plugin = instance;
		filename = file;
		npcs = new HashMap<Integer, NPC>();
		loadFile();
		loadNPCs();
	}
	
	public void loadFile() {
		File file = new File(filename);
		if (file.exists()) {
			try {
				load(new FileInputStream(file));
			} catch (IOException e) {
				System.out.println("BenCmd had a problem:");
				e.printStackTrace();
			}
		}
	}

	public void saveFile() {
		File file = new File(filename);
		if (file.exists()) {
			try {
				store(new FileOutputStream(file), "-BenCmd NPC List-");
			} catch (IOException e) {
				System.out.println("BenCmd had a problem:");
				e.printStackTrace();
			}
		}
	}
	
	public void loadNPCs() {
		for(int i = 0; i < this.size(); i++) {
			Integer key = Integer.parseInt((String) this.keySet().toArray()[i]);
			String value = this.getProperty(key.toString());
			Location l = null;
			switch(value.split("/")[0].charAt(0)) {
			case 'b':
				l = toLocation(value.split("/")[1]);
				npcs.put(key, new BankerNPC(plugin, key, l));
				break;
			case 'm':
				l = toLocation(value.split("/")[1]);
				npcs.put(key, new BankManagerNPC(plugin, key, l));
				break;
			case 's':
				l = toLocation(value.split("/")[1]);
				HashMap<ToolMaterial, HashMap<ToolType, Double>> t = null;
				HashMap<ArmorMaterial, HashMap<ArmorType, Double>> a = null;
				if (value.split("/").length == 4) {
					t = BlacksmithNPC.readTools(value.split("/")[2]);
					a = BlacksmithNPC.readArmor(value.split("/")[3]);
				} else if (value.split("/").length == 3) {
					t = BlacksmithNPC.readTools(value.split("/")[2]);
				}
				npcs.put(key, new BlacksmithNPC(plugin, key, l, t, a));
				break;
			default:
				plugin.log.warning("INVALID NPC TYPE! (ID " + key + ")");
				break;
			}
		}
	}
	
	protected List<NPC> inChunk(Chunk c) {
		List<NPC> list = new ArrayList<NPC>();
		for(NPC n : npcs.values()) {
			CraftChunk c2 = (CraftChunk)n.getLocation().getBlock().getChunk();
			if (c2.getX() == c.getX() && c2.getZ() == c.getZ()) {
				list.add(n);
			}
		}
		return list;
	}
	
	public List<NPC> allNPCs() {
		return new ArrayList<NPC>(npcs.values());
	}
	
	private Location toLocation(String s) {
		String[] splt = s.split(",");
		World w = plugin.getServer().getWorld(splt[0]);
		Double x = Double.parseDouble(splt[1]);
		Double y = Double.parseDouble(splt[2]);
		Double z = Double.parseDouble(splt[3]);
		Float yaw = Float.parseFloat(splt[4]);
		Float pitch = Float.parseFloat(splt[5]);
		return new Location(w, x, y, z, yaw, pitch);
	}
	
	public NPC getNPC(int id) {
		return npcs.get(id);
	}
	
	public NPC getNPC(EntityNPC enpc) {
		for(NPC npc : npcs.values()) {
			if(npc.enpc == enpc) {
				return npc;
			}
		}
		return null;
	}
	
	public int nextId() {
		int i = 0;
		for(i = 0; npcs.containsKey(i); i++) { }
		return i;
	}
	
	public void addNPC(NPC npc) {
		npcs.put(npc.getID(), npc);
		saveNPC(npc);
	}
	
	public void remNPC(NPC npc) {
		npc.despawn();
		npcs.remove(npc.getID());
		this.remove(String.valueOf(npc.getID()));
		saveFile();
	}
	
	public void saveNPC(NPC npc) {
		this.put(String.valueOf(npc.getID()), npc.getValue());
		saveFile();
	}
	
	public void saveAll() {
		for (NPC npc : npcs.values()) {
			saveNPC(npc);
		}
	}
}
