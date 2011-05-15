package ben_dude56.plugins.bencmd.invtools;

import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.block.CraftDispenser;
import org.bukkit.inventory.ItemStack;

import ben_dude56.plugins.bencmd.BenCmd;
import ben_dude56.plugins.bencmd.permissions.PermissionGroup;

public class InventoryBackend {
	BenCmd plugin;
	static final int FULL_STACK = 64;
	static final int THROWABLE_STACK = 16;
	Logger log = Logger.getLogger("minecraft");

	public InventoryBackend(BenCmd instance) {
		plugin = instance;
	}

	/**
	 * 
	 * @deprecated Use directly from instance of PermissionGroup.
	 */
	public boolean canSpawnItem(Material mat, String group) {
		return new PermissionGroup(plugin, group).canSpawnItem(mat);
	}

	/**
	 * 
	 * @deprecated Use directly from instance of PermissionGroup.
	 */
	public boolean canSpawnItem(int id, String group) {
		return new PermissionGroup(plugin, group).canSpawnItem(Material
				.getMaterial(id));
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
		if (id == 332 || id == 344)
			return THROWABLE_STACK;
		return FULL_STACK;
	}
	
	public boolean canBind(Material mat) {
		int id = mat.getId();
		if(id >= 256 && id <= 289) {
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
					if (plugin.itemAliases.containsKey(arg.split(":")[0])
							&& arg.split(":").length == 2) {
						ItemID = Integer.parseInt(plugin.itemAliases.getString(
								arg.split(":")[0], ""));
						Damage = Integer.parseInt(arg.split(":")[1]);
					} else if (plugin.itemAliases.containsKey(arg)) {
						if (plugin.itemAliases.getString(arg, "").split(":").length == 1) {
							ItemID = Integer.parseInt(plugin.itemAliases
									.getString(arg, ""));
						} else {
							ItemID = Integer.parseInt(plugin.itemAliases
									.getString(arg, "").split(":")[0]);
							Damage = Integer.parseInt(plugin.itemAliases
									.getString(arg, "").split(":")[1]);
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
		if (plugin.dispensers.isUnlimitedDispenser(block.getLocation())
				&& block.getType() == Material.DISPENSER) {
			BCItem mat = plugin.dispensers
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
