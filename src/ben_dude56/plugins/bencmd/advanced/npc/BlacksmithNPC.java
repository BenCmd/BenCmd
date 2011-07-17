package ben_dude56.plugins.bencmd.advanced.npc;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import ben_dude56.plugins.bencmd.BenCmd;
import ben_dude56.plugins.bencmd.User;
import ben_dude56.plugins.bencmd.money.BuyableItem;

public class BlacksmithNPC extends NPC implements Clickable {
	private HashMap<ToolMaterial, HashMap<ToolType, Double>> toolPrices;
	private HashMap<ArmorMaterial, HashMap<ArmorType, Double>> armorPrices;
	
	public static HashMap<ToolMaterial, HashMap<ToolType, Double>> readTools(String s) {
		HashMap<ToolMaterial, HashMap<ToolType, Double>> t = new HashMap<ToolMaterial, HashMap<ToolType, Double>>();
		String[] s1 = s.split(";");
		for(String s2 : s1) {
			if ((s2 = s2.trim()).isEmpty() || !s2.contains(":")) {
				continue;
			}
			ToolMaterial m;
			if(s2.split(":")[0].equals("w")) {
				m = ToolMaterial.WOOD;
			} else if (s2.split(":")[0].equals("s")) {
				m = ToolMaterial.STONE;
			} else if (s2.split(":")[0].equals("i")) {
				m = ToolMaterial.IRON;
			} else if (s2.split(":")[0].equals("g")) {
				m = ToolMaterial.GOLD;
			} else if (s2.split(":")[0].equals("d")) {
				m = ToolMaterial.DIAMOND;
			} else {
				continue;
			}
			if (t.containsKey(m)) {
				continue;
			}
			String[] s3 = s2.split(":")[1].split(",");
			HashMap<ToolType, Double> tt = new HashMap<ToolType, Double>();
			for(String s4 : s3) {
				if ((s4 = s4.trim()).isEmpty() || !s2.contains("=")) {
					continue;
				}
				ToolType tp;
				if (s4.split("=")[0].equals("a")) {
					tp = ToolType.AXE;
				} else if (s4.split("=")[0].equals("p")) {
					tp = ToolType.PICK;
				} else if(s4.split("=")[0].equals("sh")) {
					tp = ToolType.SHOVEL;
				} else if(s4.split("=")[0].equals("sw")) {
					tp = ToolType.SWORD;
				} else if(s4.split("=")[0].equals("h")) {
					tp = ToolType.HOE;
				} else {
					continue;
				}
				if (tt.containsKey(tp)) {
					continue;
				}
				Double pr;
				try {
					pr = Double.parseDouble(s4.split("=")[1]);
				} catch (NumberFormatException e) {
					continue;
				}
				tt.put(tp, pr);
			}
			t.put(m, tt);
		}
		return t;
	}
	
	public static HashMap<ArmorMaterial, HashMap<ArmorType, Double>> readArmor(String s) {
		HashMap<ArmorMaterial, HashMap<ArmorType, Double>> a = new HashMap<ArmorMaterial, HashMap<ArmorType, Double>>();
		String[] s1 = s.split(";");
		for(String s2 : s1) {
			if ((s2 = s2.trim()).isEmpty() || !s2.contains(":")) {
				continue;
			}
			ArmorMaterial m;
			if (s2.split(":")[0].equals("l")) {
				m = ArmorMaterial.LEATHER;
			} else if (s2.split(":")[0].equals("c")) {
				m = ArmorMaterial.CHAINMAIL;
			} else if (s2.split(":")[0].equals("i")) {
				m = ArmorMaterial.IRON;
			} else if (s2.split(":")[0].equals("g")) {
				m = ArmorMaterial.GOLD;
			} else if (s2.split(":")[0].equals("d")) {
				m = ArmorMaterial.DIAMOND;
			} else {
				continue;
			}
			if (a.containsKey(m)) {
				continue;
			}
			String[] s3 = s2.split(":")[1].split(",");
			HashMap<ArmorType, Double> at = new HashMap<ArmorType, Double>();
			for(String s4 : s3) {
				if((s4 = s4.trim()).isEmpty() || !s4.contains("=")) {
					continue;
				}
				ArmorType t;
				if (s4.split("=")[0].equals("h")) {
					t = ArmorType.HELMET;
				} else if (s4.split("=")[0].equals("c")) {
					t = ArmorType.CHESTPLATE;
				} else if (s4.split("=")[0].equals("l")) {
					t = ArmorType.LEGGINGS;
				} else if (s4.split("=")[0].equals("b")) {
					t = ArmorType.BOOTS;
				} else {
					continue;
				}
				if (at.containsKey(t)) {
					continue;
				}
				Double pr;
				try {
					pr = Double.parseDouble(s4.split("=")[1]);
				} catch (NumberFormatException e) {
					continue;
				}
				at.put(t, pr);
			}
			a.put(m, at);
		}
		return a;
	}
	
	private static String valueOfT(HashMap<ToolMaterial, HashMap<ToolType, Double>> tools) {
		String s = "";
		for (int i = 0; i < tools.size(); i++) {
			ToolMaterial m = (ToolMaterial) tools.keySet().toArray()[i];
			if (!s.isEmpty()) {
				s += ";";
			}
			if (m == ToolMaterial.WOOD) {
				s += "w:";
			} else if (m == ToolMaterial.STONE) {
				s += "s:";
			} else if (m == ToolMaterial.IRON) {
				s += "i:";
			} else if (m == ToolMaterial.GOLD) {
				s += "g:";
			} else if (m == ToolMaterial.DIAMOND) {
				s += "d:";
			} else {
				continue;
			}
			for (int j = 0; j < tools.get(m).size(); j++) {
				ToolType t = (ToolType) tools.get(m).keySet().toArray()[j];
				Double c = tools.get(m).get(t);
				if(!s.endsWith(":")) {
					s += ",";
				}
				if (t == ToolType.AXE) {
					s += "a=";
				} else if (t == ToolType.PICK) {
					s += "p=";
				} else if (t == ToolType.SHOVEL) {
					s += "sh=";
				} else if (t == ToolType.SWORD) {
					s += "sw=";
				} else if (t == ToolType.HOE) {
					s += "h=";
				} else {
					continue;
				}
				s += c.toString();
			}
		}
		return s;
	}
	
	private static String valueOfA(HashMap<ArmorMaterial, HashMap<ArmorType, Double>> armor) {
		String s = "";
		for (int i = 0; i < armor.size(); i++) {
			ArmorMaterial m = (ArmorMaterial) armor.keySet().toArray()[i];
			if (!s.isEmpty()) {
				s += ";";
			}
			if (m == ArmorMaterial.LEATHER) {
				s += "l:";
			} else if (m == ArmorMaterial.CHAINMAIL) {
				s += "c:";
			} else if (m == ArmorMaterial.IRON) {
				s += "i:";
			} else if (m == ArmorMaterial.GOLD) {
				s += "g:";
			} else if (m == ArmorMaterial.DIAMOND) {
				s += "d:";
			} else {
				continue;
			}
			for (int j = 0; j < armor.get(m).size(); j++) {
				ArmorType t = (ArmorType) armor.get(m).keySet().toArray()[j];
				Double c = armor.get(m).get(t);
				if(!s.endsWith(":")) {
					s += ",";
				}
				if (t == ArmorType.HELMET) {
					s += "h=";
				} else if (t == ArmorType.CHESTPLATE) {
					s += "c=";
				} else if (t == ArmorType.LEGGINGS) {
					s += "l=";
				} else if (t == ArmorType.BOOTS) {
					s += "b=";
				} else {
					continue;
				}
				s += c.toString();
			}
		}
		return s;
	}

	public BlacksmithNPC(BenCmd instance, int id, Location l, HashMap<ToolMaterial, HashMap<ToolType, Double>> tools, HashMap<ArmorMaterial, HashMap<ArmorType, Double>> armor) {
		super(instance, "Blacksmith", id, l);
		if (tools == null) {
			toolPrices = new HashMap<ToolMaterial, HashMap<ToolType, Double>>();
		} else {
			toolPrices = tools;
		}
		if (armor == null) {
			armorPrices = new HashMap<ArmorMaterial, HashMap<ArmorType, Double>>();
		}
	}
	
	public Double getRepairPrice(int id) {
		ToolMaterial tm = ToolMaterial.getMaterial(id);
		if (tm != ToolMaterial.NOTATOOL) {
			ToolType tt = ToolType.getType(id);
			assert tt != ToolType.NOTATOOL : "An item was determined to be a tool, but also to NOT be a tool...";
			return getRepairPrice(tm, tt);
		}
		ArmorMaterial am = ArmorMaterial.getMaterial(id);
		if (am != ArmorMaterial.NOTARMOR) {
			ArmorType at = ArmorType.getType(id);
			assert at != ArmorType.NOTARMOR : "An item was determined to be armor, but also to NOT be armor...";
			return getRepairPrice(am, at);
		}
		return -1.0;
	}
	
	private Double getRepairPrice(ToolMaterial m, ToolType t) {
		if(toolPrices.containsKey(m) && toolPrices.get(m).containsKey(t)) {
			return toolPrices.get(m).get(t);
		} else {
			return -1.0;
		}
	}
	
	private Double getRepairPrice(ArmorMaterial m, ArmorType t) {
		if(armorPrices.containsKey(m) && armorPrices.get(m).containsKey(t)) {
			return armorPrices.get(m).get(t);
		} else {
			return -1.0;
		}
	}
	
	public boolean canRepair(int id) {
		ToolMaterial tm = ToolMaterial.getMaterial(id);
		if (tm != ToolMaterial.NOTATOOL) {
			ToolType tt = ToolType.getType(id);
			assert tt != ToolType.NOTATOOL : "An item was determined to be a tool, but also to NOT be a tool...";
			return canRepair(tm, tt);
		}
		ArmorMaterial am = ArmorMaterial.getMaterial(id);
		if (am != ArmorMaterial.NOTARMOR) {
			ArmorType at = ArmorType.getType(id);
			assert at != ArmorType.NOTARMOR : "An item was determined to be armor, but also to NOT be armor...";
			return canRepair(am, at);
		}
		return false;
	}
	
	private boolean canRepair(ToolMaterial m, ToolType t) {
		return (toolPrices.containsKey(m) && toolPrices.get(m).containsKey(t));
	}
	
	private boolean canRepair(ArmorMaterial m, ArmorType t) {
		return (armorPrices.containsKey(m) && armorPrices.get(m).containsKey(t));
	}
	
	public void addItem(ItemStack i, Double c, Player p) {
		ToolMaterial tm = ToolMaterial.getMaterial(i.getTypeId());
		if (tm != ToolMaterial.NOTATOOL) {
			ToolType t = ToolType.getType(i.getTypeId());
			assert t != ToolType.NOTATOOL : "An item was determined to be a tool, but also to NOT be a tool...";
			if(toolPrices.containsKey(tm)) {
				if (toolPrices.get(tm).containsKey(t)) {
					if (c == -1.0) {
						toolPrices.get(tm).remove(t);
						plugin.npcs.saveNPC(this);
						p.sendMessage(ChatColor.GREEN + "That tool can no longer be repaired!");
					} else {
						toolPrices.get(tm).put(t, c);
						plugin.npcs.saveNPC(this);
						p.sendMessage(ChatColor.GREEN + "That tool's price has been updated!");
					}
				} else {
					if (c == -1.0) {
						p.sendMessage(ChatColor.RED + "That tool is not set as repairable...");
					} else {
						toolPrices.get(tm).put(t, c);
						plugin.npcs.saveNPC(this);
						p.sendMessage(ChatColor.GREEN + "That tool can now be repaired!");
					}
				}
			} else {
				if (c == -1.0) {
					p.sendMessage(ChatColor.RED + "That tool is not set as repairable...");
				} else {
					HashMap<ToolType, Double> toPut = new HashMap<ToolType, Double>();
					toPut.put(t, c);
					toolPrices.put(tm, toPut);
					plugin.npcs.saveNPC(this);
					p.sendMessage(ChatColor.GREEN + "That tool can now be repaired!");
				}
			}
			return;
		}
		ArmorMaterial am = ArmorMaterial.getMaterial(i.getTypeId());
		if (am != ArmorMaterial.NOTARMOR) {
			ArmorType t = ArmorType.getType(i.getTypeId());
			assert t != ArmorType.NOTARMOR : "An item was determined to be armor, but also to NOT be armor...";
			if(armorPrices.containsKey(am)) {
				if (armorPrices.get(am).containsKey(t)) {
					if (c == -1.0) {
						armorPrices.get(am).remove(t);
						plugin.npcs.saveNPC(this);
						p.sendMessage(ChatColor.GREEN + "That armor can no longer be repaired!");
					} else {
						armorPrices.get(am).put(t, c);
						plugin.npcs.saveNPC(this);
						p.sendMessage(ChatColor.GREEN + "That armor's price has been updated!");
					}
				} else {
					if (c == -1.0) {
						p.sendMessage(ChatColor.RED + "That armor is not set as repairable...");
					} else {
						armorPrices.get(am).put(t, c);
						plugin.npcs.saveNPC(this);
						p.sendMessage(ChatColor.GREEN + "That armor can now be repaired!");
					}
				}
			} else {
				if (c == -1.0) {
					p.sendMessage(ChatColor.RED + "That armor is not set as repairable...");
				} else {
					HashMap<ArmorType, Double> toPut = new HashMap<ArmorType, Double>();
					toPut.put(t, c);
					armorPrices.put(am, toPut);
					plugin.npcs.saveNPC(this);
					p.sendMessage(ChatColor.GREEN + "That armor can now be repaired!");
				}
			}
			return;
		}
		p.sendMessage(ChatColor.RED + "That item cannot be repaired!");
	}

	@Override
	public void onRightClick(Player p) {
		if (p.getItemInHand() == null) {
			p.sendMessage(ChatColor.RED + "Right-click with a tool or armor in your hand to have it repaired.");
			return;
		}
		int id = p.getItemInHand().getTypeId();
		if (ToolMaterial.getMaterial(id) == ToolMaterial.NOTATOOL && ArmorMaterial.getMaterial(id) == ArmorMaterial.NOTARMOR) {
			p.sendMessage(ChatColor.RED + "Right-click with a tool or armor in your hand to have it repaired.");
			return;
		}
		if (canRepair(id)) {
			if (getRepairPrice(id) == -1.0) {
				p.sendMessage(ChatColor.RED + "This blacksmith can't repair that item!");
			} else if (p.getItemInHand().getDurability() == 0) {
				p.sendMessage(ChatColor.RED + "That item cannot be repaired further!");
			} else if (BuyableItem.hasMoney(User.getUser(plugin, p), getRepairPrice(id), plugin)) {
				BuyableItem.remMoney(User.getUser(plugin, p), getRepairPrice(id), plugin);
				p.getItemInHand().setDurability((short) 0);
				p.sendMessage(ChatColor.GREEN + "That item has been repaired!");
			} else {
				p.sendMessage(ChatColor.RED + "You must have at least " + getRepairPrice(id) + " worth of currency to repair that item!");
			}
		} else {
			p.sendMessage(ChatColor.RED + "This blacksmith can't repair that item!");
		}
	}

	@Override
	public void onLeftClick(Player p) {
	}
	
	@Override
	public String getValue() {
		Location l = super.getLocation();
		return "s/" + l.getWorld().getName() + "," + l.getX() + "," + l.getY() + "," + l.getZ() + "," + l.getYaw() + "," + l.getPitch() + "/" + valueOfT(toolPrices) + "/" + valueOfA(armorPrices);
	}
	
	public enum ToolMaterial {
		WOOD, STONE, IRON, GOLD, DIAMOND, NOTATOOL;
		
		public static ToolMaterial getMaterial(int id) {
			if ((id >= 268 && id <= 271) || id == 290) {
				return ToolMaterial.WOOD;
			}
			if ((id >= 272 && id <= 275) || id == 291) {
				return ToolMaterial.STONE;
			}
			if ((id >= 256 && id <= 258) || id == 267 || id == 292) {
				return ToolMaterial.IRON;
			}
			if ((id >= 283 && id <= 286) || id == 294) {
				return ToolMaterial.GOLD;
			}
			if ((id >= 276 && id <= 279) || id == 293) {
				return ToolMaterial.DIAMOND;
			}
			return ToolMaterial.NOTATOOL;
		}
	}
	
	public enum ToolType {
		AXE, PICK, SHOVEL, SWORD, HOE, NOTATOOL;
		
		public static ToolType getType(int id) {
			if (id == 258 || id == 271 || id == 275 || id == 279 || id == 286) {
				return ToolType.AXE;
			}
			if (id == 257 || id == 270 || id == 274 || id == 278 || id == 285) {
				return ToolType.PICK;
			}
			if (id == 256 || id == 269 || id == 273 || id == 277 || id == 284) {
				return ToolType.SHOVEL;
			}
			if (id == 267 || id == 268 || id == 272 || id == 276 || id == 283) {
				return ToolType.SWORD;
			}
			if (id >= 290 && id <= 294) {
				return ToolType.HOE;
			}
			return ToolType.NOTATOOL;
		}
	}
	
	public enum ArmorMaterial {
		LEATHER, CHAINMAIL, IRON, GOLD, DIAMOND, NOTARMOR;
		
		public static ArmorMaterial getMaterial(int id) {
			if (id >= 298 && id <= 301) {
				return ArmorMaterial.LEATHER;
			}
			if (id >= 302 && id <= 305) {
				return ArmorMaterial.CHAINMAIL;
			}
			if (id >= 306 && id <= 309) {
				return ArmorMaterial.IRON;
			}
			if (id >= 314 && id <= 317) {
				return ArmorMaterial.GOLD;
			}
			if (id >= 310 && id <= 313) {
				return ArmorMaterial.DIAMOND;
			}
			return ArmorMaterial.NOTARMOR;
		}
	}
	
	public enum ArmorType {
		HELMET, CHESTPLATE, LEGGINGS, BOOTS, NOTARMOR;
		
		public static ArmorType getType(int id) {
			if (id < 298 || id > 317) {
				return ArmorType.NOTARMOR;
			}
			if (id % 4 == 0) {
				return ArmorType.HELMET;
			} else if (id % 4 == 1) {
				return ArmorType.CHESTPLATE;
			} else if (id % 4 == 2) {
				return ArmorType.LEGGINGS;
			} else if (id % 4 == 3) {
				return ArmorType.BOOTS;
			} else {
				throw new AssertionError("(id % 4) is out of range!"); //Should never be reached
			}
		}
	}
}
