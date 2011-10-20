package com.bendude56.bencmd.invtools;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.block.CraftDispenser;
import org.bukkit.inventory.ItemStack;

import com.bendude56.bencmd.BenCmd;


public class InventoryBackend {
	static final int FULL_STACK = 64;
	static final int THROWABLE_STACK = 16;
	
	// Singleton instancing
	
	private static InventoryBackend instance = null;
	
	public static InventoryBackend getInstance() {
		if (instance == null) {
			return instance = new InventoryBackend();
		} else {
			return instance;
		}
	}
	
	public static void destroyInstance() {
		instance = null;
	}
	
	private InventoryBackend() {
	}

	public int getStackNumber(int id) {
		if (id >= 256 && id <= 261)
			return 1;
		if (id >= 267 && id <= 279)
			return 1;
		if (id >= 282 && id <= 286)
			return 1;
		if (id >= 290 && id <= 294)
			return 1;
		if (id >= 297 && id <= 317)
			return 1;
		if (id >= 322 && id <= 330)
			return 1;
		if (id == 319 || id == 320 || id == 349 || id == 350)
			return 1;
		if (id == 333 || id == 335 || id == 343 || id == 342)
			return 1;
		if (id == 354 || id == 2256 || id == 2257)
			return 1;
		if (id == 358)
			return 1;
		if (id == 332 || id == 344)
			return THROWABLE_STACK;
		return FULL_STACK;
	}

	public boolean canBind(Material mat) {
		int id = mat.getId();
		if (id >= 256 && id <= 289) {
			return true;
		} else if (id == 295 || id == 296) {
			return true;
		} else if (id >= 298 && id <= 318) {
			return true;
		} else if (id >= 336 && id <= 341) {
			return true;
		} else if (id >= 345 && id <= 348) {
			return true;
		} else if (id >= 351 && id <= 353) {
			return true;
		} else if (id == 2256 || id == 2257) {
			return true;
		} else {
			return false;
		}
	}

	public BCItem checkAlias(String arg) {
		int ItemID = -1;
		int Damage = 0;
		try {
			if (arg.split(":").length == 1) {
				ItemID = Integer.parseInt(arg);
			} else {
				ItemID = Integer.parseInt(arg.split(":")[0]);
				Damage = Integer.parseInt(arg.split(":")[1]);
			}
		} catch (NumberFormatException e) {
			try {
				if (BenCmd.getItemAliases().containsKey(arg.split(":")[0])
						&& arg.split(":").length == 2) {
					ItemID = Integer.parseInt(BenCmd.getItemAliases().getString(
							arg.split(":")[0], ""));
					Damage = Integer.parseInt(arg.split(":")[1]);
				} else if (BenCmd.getItemAliases().containsKey(arg)) {
					if (BenCmd.getItemAliases().getString(arg, "").split(":").length == 1) {
						ItemID = Integer.parseInt(BenCmd.getItemAliases().getString(
								arg, ""));
					} else {
						ItemID = Integer.parseInt(BenCmd.getItemAliases().getString(
								arg, "").split(":")[0]);
						Damage = Integer.parseInt(BenCmd.getItemAliases().getString(
								arg, "").split(":")[1]);
					}
				}

			} catch (NumberFormatException e2) {
				return null;
			}
		}
		if (ItemID == 0 || ItemID == -1) {
			return null;
		} else {
			return new BCItem(Material.getMaterial(ItemID), Damage);
		}
	}

	public boolean TryDispense(Block block) {
		if (BenCmd.getDispensers().isUnlimitedDispenser(block.getLocation())
				&& block.getType() == Material.DISPENSER) {
			BCItem mat = BenCmd.getDispensers()
					.getDispensedItem(block.getLocation());
			int amt = this.getStackNumber(mat.getMaterial().getId());
			CraftDispenser disp = new CraftDispenser(block);
			disp.getInventory().clear();
			if (amt == InventoryBackend.THROWABLE_STACK) {
				block.getWorld().dropItem(
						block.getLocation(),
						new ItemStack(mat.getMaterial(), amt, (short) mat
								.getDamage()));
				return true;
			}
			int i = 0;
			while (i < amt) {
				disp.getInventory().addItem(
						new ItemStack(mat.getMaterial(), amt, (short) mat
								.getDamage()));
				disp.dispense();
				i++;
			}
			return true;
		} else {
			return false;
		}
	}
}
