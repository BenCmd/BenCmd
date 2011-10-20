package com.bendude56.bencmd.advanced.bank;

import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.minecraft.server.InventoryLargeChest;

public class LargeBankInventory extends BankInventory {
	protected TileEntityBankChest inv2;
	protected InventoryLargeChest lc;

	public LargeBankInventory(String player) {
		super(player);
		inv2 = new TileEntityBankChest();
		inv2.setName("Bank");
		lc = new InventoryLargeChest("Bank", chest, inv2);
	}

	public LargeBankInventory(BankInventory inv1) {
		super(inv1);
		inv2 = new TileEntityBankChest();
		inv2.setName("Bank");
		lc = new InventoryLargeChest("Bank", chest, inv2);
	}

	@Override
	public boolean isFull() {
		return super.isFull() && inv2.isFull();
	}

	@Override
	public boolean isEmpty() {
		return super.isEmpty() && inv2.isEmpty();
	}

	@Override
	public void fromValue(String value) {
		chest.clear();
		inv2.clear();
		for (int i = 0; i < lc.getSize() && i < value.split(",").length; i++) {
			String s = value.split(",")[i];
			if (s.isEmpty()) {
				continue;
			}
			int id = Integer.parseInt(s.split(" ")[0].split(":")[0]);
			short dmg = Short.parseShort(s.split(" ")[0].split(":")[1]);
			int amt = Integer.parseInt(s.split(" ")[1]);
			lc.setItem(i, new net.minecraft.server.ItemStack(id, amt, dmg));
		}
	}

	@Override
	public String getValue() {
		return chest.getValue() + "," + inv2.getValue();
	}

	@Override
	public boolean isUpgraded() {
		return true;
	}

	@Override
	public void open(Player p) {
		((CraftPlayer) p).getHandle().a(lc);
	}

	@Override
	public void setItem(int index, net.minecraft.server.ItemStack items) {
		lc.setItem(index, items);
	}

	@Override
	public ItemStack getItem(int index) {
		return new CraftItemStack(lc.getItem(index));
	}
}
