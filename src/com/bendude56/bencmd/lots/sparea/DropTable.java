package com.bendude56.bencmd.lots.sparea;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.inventory.ItemStack;

import com.bendude56.bencmd.invtools.BCItem;
import com.bendude56.bencmd.invtools.InventoryBackend;

public class DropTable {
	private HashMap<BCItem, DropInfo>	drops;

	public DropTable(String value) {
		drops = new HashMap<BCItem, DropInfo>();
		String[] splt = value.split(",");
		for (String s : splt) {
			if (s.isEmpty()) {
				continue;
			}
			String[] info = s.split(" ");
			BCItem i = InventoryBackend.getInstance().checkAlias(info[0]);
			int c = Integer.parseInt(info[1]), min = Integer.parseInt(info[2]), max = Integer.parseInt(info[3]);
			drops.put(i, new DropInfo(c, min, max));
		}
	}

	public HashMap<BCItem, DropInfo> getAllDrops() {
		return drops;
	}

	public void removeItem(BCItem item) {
		for (int i = 0; i < drops.size(); i++) {
			BCItem item2 = (BCItem) drops.keySet().toArray()[i];
			if (item2.getMaterial() == item.getMaterial() && item2.getDamage() == item.getDamage()) {
				drops.remove(item2);
			}
		}
	}

	public DropInfo getInfo(BCItem item) {
		for (int i = 0; i < drops.size(); i++) {
			BCItem item2 = (BCItem) drops.keySet().toArray()[i];
			if (item2.getMaterial() == item.getMaterial() && item2.getDamage() == item.getDamage()) {
				return drops.get(item2);
			}
		}
		return null;
	}

	public void addItem(BCItem item, DropInfo di) {
		removeItem(item);
		drops.put(item, di);
	}

	public List<ItemStack> getRandomDrops(Random r) {
		List<ItemStack> ri = new ArrayList<ItemStack>();
		for (int i = 0; i < drops.size(); i++) {
			BCItem bci = (BCItem) drops.keySet().toArray()[i];
			DropInfo di = drops.get(bci);
			int a = di.getAmount(r);
			if (a != 0) {
				ri.add(new ItemStack(bci.getMaterial(), a, (short) bci.getDamage()));
			}
		}
		return ri;
	}

	public String getValue() {
		String r = "";
		for (int i = 0; i < drops.size(); i++) {
			BCItem item = (BCItem) drops.keySet().toArray()[i];
			DropInfo info = drops.get(item);
			r += item.getMaterial().getId() + ":" + item.getDamage() + " ";
			r += info.getChance() + " ";
			r += info.getMin() + " ";
			r += info.getMax() + ",";
		}
		if (r != "") {
			r = r.substring(0, r.length() - 1);
		}
		return r;
	}
}
