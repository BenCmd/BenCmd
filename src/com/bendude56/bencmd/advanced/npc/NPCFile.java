package com.bendude56.bencmd.advanced.npc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.BenCmdFile;
import com.bendude56.bencmd.advanced.npc.BlacksmithNPC.*;


public class NPCFile extends BenCmdFile {
	private HashMap<Integer, NPC> npcs;

	public NPCFile() {
		super("npc.db", "--BenCmd NPC File--", true);
		npcs = new HashMap<Integer, NPC>();
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(BenCmd.getPlugin(), new Runnable() {
			public void run() {
				tickAll();
			}
		}, 1, 1);
		loadFile();
		loadAll();
	}
	
	public void reloadNPCs() {
		for(NPC n : allNPCs()) {
			n.despawn();
		}
		npcs.clear();
		loadAll();
	}

	public void loadAll() {
		BenCmd plugin = BenCmd.getPlugin();
		for (int i = 0; i < getFile().size(); i++) {
			Integer key = Integer.parseInt((String) getFile().keySet().toArray()[i]);
			String value = getFile().getProperty(key.toString());
			Location l = null;
			switch (value.split("\\|")[0].charAt(0)) {
			case 'b':
				l = toLocation(value.split("\\|")[1]);
				npcs.put(key, new BankerNPC(plugin, key, l));
				break;
			case 'm':
				l = toLocation(value.split("\\|")[1]);
				npcs.put(key, new BankManagerNPC(plugin, key, l));
				break;
			case 's':
				l = toLocation(value.split("\\|")[1]);
				HashMap<ToolMaterial, HashMap<ToolType, Double>> t = null;
				HashMap<ArmorMaterial, HashMap<ArmorType, Double>> a = null;
				if (value.split("\\|").length == 4) {
					t = BlacksmithNPC.readTools(value.split("\\|")[2]);
					a = BlacksmithNPC.readArmor(value.split("\\|")[3]);
				} else if (value.split("\\|").length == 3) {
					t = BlacksmithNPC.readTools(value.split("\\|")[2]);
				}
				npcs.put(key, new BlacksmithNPC(plugin, key, l, t, a));
				break;
			case 'n':
				l = toLocation(value.split("\\|")[1]);
				String s = value.split("\\|")[2];
				String n = value.split("\\|")[3];
				ItemStack item = new ItemStack(Integer.parseInt(value.split("\\|")[4].split(":")[0]), Integer.parseInt(value.split("\\|")[4].split(":")[1]));
				npcs.put(key, new StaticNPC(plugin, n, s, key, l, item, true));
				break;
			case 'p':
				/*l = toLocation(value.split("\\|")[1]);
				String s1 = value.split("\\|")[2];
				String n1 = value.split("\\|")[3];
				ItemStack item1 = new ItemStack(Integer.parseInt(value.split("\\|")[4].split(":")[0]), Integer.parseInt(value.split("\\|")[4].split(":")[1]));
				String[] slocs = value.split("\\|")[5].split("/");
				List<Location> locs = new ArrayList<Location>();
				for (String loc : slocs) {
					if (!loc.isEmpty()) {
						locs.add(toLocation(loc));
					}
				}
				if (locs.isEmpty()) {
					npcs.put(key, new PathableNPC())
				} else {
					
				}
				break;*/
			default:
				plugin.bLog
						.warning("NPC ERROR: An invalid NPC type was detected in NPC with UID "
								+ key);
				plugin.log.warning("INVALID NPC TYPE! (ID " + key + ")");
				break;
			}
		}
	}

	public List<NPC> inChunk(Chunk c) {
		List<NPC> list = new ArrayList<NPC>();
		for (NPC n : npcs.values()) {
			if (c.getX() * 16 < n.getLocation().getX() && n.getLocation().getX() < (c.getX() * 16) + 16
					&& c.getZ() * 16 < n.getLocation().getZ() && n.getLocation().getZ() < (c.getZ() * 16) + 16)
			{
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
		World w = Bukkit.getWorld(splt[0]);
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
		for (NPC npc : npcs.values()) {
			if (npc.enpc == enpc) {
				return npc;
			}
		}
		return null;
	}

	public int nextId() {
		int i = 0;
		for (i = 0; npcs.containsKey(i); i++) {
		}
		return i;
	}

	public void addNPC(NPC npc) {
		npcs.put(npc.getID(), npc);
		saveNPC(npc);
	}

	public void remNPC(NPC npc) {
		npc.despawn();
		npcs.remove(npc.getID());
		getFile().remove(String.valueOf(npc.getID()));
		saveFile();
	}
	
	public void saveNPC(NPC npc) {
		saveNPC(npc, true);
	}

	public void saveNPC(NPC npc, boolean saveFile) {
		getFile().put(String.valueOf(npc.getID()), npc.getValue());
		if (saveFile) {
			saveFile();
		}
	}

	public void saveAll() {
		for (NPC npc : npcs.values()) {
			saveNPC(npc, false);
		}
		saveFile();
	}
	
	public void tickAll() {
		for(NPC n : allNPCs()) {
			n.tick();
		}
	}
}
