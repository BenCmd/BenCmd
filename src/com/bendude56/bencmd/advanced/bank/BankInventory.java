package com.bendude56.bencmd.advanced.bank;

import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.bendude56.bencmd.BenCmd;


public class BankInventory {
	protected TileEntityBankChest chest;
	protected String p;
	protected BenCmd plugin;

	public BankInventory(String player, BenCmd instance) {
		plugin = instance;
		p = player;
		chest = new TileEntityBankChest();
		chest.setName("Bank");
	}

	public BankInventory(BankInventory i) {
		chest = i.chest;
		p = i.p;
		plugin = i.plugin;
	}

	public void open(Player p) {
		((CraftPlayer) p).getHandle().a(chest);
	}

	public void setItem(int index, net.minecraft.server.ItemStack items) {
		chest.setItem(index, items);
	}

	public ItemStack getItem(int index) {
		return new CraftItemStack(chest.getItem(index));
	}

	public boolean isFull() {
		return chest.isFull();
	}

	public boolean isEmpty() {
		return chest.isEmpty();
	}

	public void fromValue(String value) throws NumberFormatException {
		chest.clear();
		for (int i = 0; i < value.split(",").length && i < chest.getSize(); i++) {
			String s = value.split(",")[i];
			if (s.isEmpty()) {
				continue;
			}
			int id = Integer.parseInt(s.split(" ")[0].split(":")[0]);
			short dmg = Short.parseShort(s.split(" ")[0].split(":")[1]);
			int amt = Integer.parseInt(s.split(" ")[1]);
			chest.setItem(i, new net.minecraft.server.ItemStack(id, amt, dmg));
		}
	}

	public String getValue() {
		return chest.getValue();
	}

	public boolean isUpgraded() {
		return false;
	}
}
