package com.bendude56.bencmd.advanced.bank;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.ItemStack;

import net.minecraft.server.TileEntityChest;

public class TileEntityBankChest extends TileEntityChest {

	protected String		name	= "null";
	protected List<Integer>	empty;

	protected TileEntityBankChest() {
		super();
		initEmpty();
	}

	public void initEmpty() {
		empty = new ArrayList<Integer>();
		for (int i = 0; i < this.getSize(); i++) {
			empty.add(i);
		}
	}

	@Override
	public void g() {
		this.h -= 1;
	}

	@Override
	public void f() {
		this.h += 1;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isFull() {
		return empty.isEmpty();
	}

	public boolean isEmpty() {
		return empty.size() == getSize();
	}

	public boolean addItem(ItemStack items) {
		if (isFull()) {
			return false;
		} else {
			super.setItem(empty.get(0), items);
			empty.remove(0);
			return true;
		}
	}

	@Override
	public ItemStack splitStack(int i, int j) {
		ItemStack toReturn = super.splitStack(i, j);
		if (toReturn != null) {
			ItemStack afterSuper[] = this.getContents();
			if (afterSuper[i] == null) {
				empty.add(i);
			}
		}
		return toReturn;
	}

	@Override
	public void setItem(int i, ItemStack items) {
		if (items != null && empty.contains(i)) {
			empty.remove(new Integer(i));
		} else if (items == null && !empty.contains(i)) {
			empty.add(new Integer(i));
		}
		super.setItem(i, items);
	}

	public void removeItem(int i) {
		if (i >= 0 && i <= getSize()) {
			super.setItem(i, null);
			empty.add(i);
		}
	}

	@Override
	public boolean a(EntityHuman entityhuman) {
		return true;
	}

	public void clear() {
		empty.clear();
		for (int i = 0; i < getSize(); i++) {
			super.setItem(i, null);
			empty.add(i);
		}
	}

	public String getValue() {
		String s = "";
		boolean init = false;
		for (int i = 0; i < getSize(); i++) {
			if (empty.contains(i)) {
				if (init) {
					s += ",";
				} else {
					init = true;
				}
			} else {
				ItemStack is = this.getContents()[i];
				if (init) {
					s += ",";
				} else {
					init = true;
				}
				s += is.id + ":" + is.g() + " " + is.count;
			}
		}
		return s;
	}
}
