package com.bendude56.bencmd.invtools.kits;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.User;
import com.bendude56.bencmd.invtools.InventoryBackend;

public class Kit {
	private List<ItemStack>	items	= new ArrayList<ItemStack>();
	private String			group;
	private String			kitName;
	private int				kitId;

	public Kit(int ID, String value, String name) {
		kitName = name;
		kitId = ID;
		if (value.split("/").length >= 2) {
			group = value.split("/")[1];
		} else {
			group = "";
		}
		for (String itemKey : value.split("/")[0].split(",")) {
			int itemId;
			short itemDamage;
			int itemAmount;
			if (itemKey.split(" ").length == 1) {
				if (itemKey.split(" ")[0].split(":").length == 2) {
					try {
						itemId = InventoryBackend.getInstance().checkAlias(itemKey.split(" ")[0]).getMaterial().getId();
					} catch (NumberFormatException e) {
						BenCmd.log(Level.SEVERE, "Error in kit (id: " + String.valueOf(ID) + "). " + itemKey.split(" ")[0] + " is NaN!");
						continue;
					}
					try {
						itemDamage = Short.parseShort(itemKey.split(" ")[0].split(":")[1]);
					} catch (NumberFormatException e) {
						BenCmd.log(Level.SEVERE, "Error in kit (id: " + String.valueOf(ID) + "). " + itemKey.split(" ")[0].split(":")[1] + " is NaN!");
						continue;
					}
					itemAmount = 1;
				} else {
					try {
						itemId = InventoryBackend.getInstance().checkAlias(itemKey.split(" ")[0]).getMaterial().getId();
					} catch (NumberFormatException e) {
						BenCmd.log(Level.SEVERE, "Error in kit (id: " + String.valueOf(ID) + "). " + itemKey.split(" ")[0].split(":")[0] + " is NaN!");
						continue;
					}
					itemAmount = 1;
					itemDamage = 0;
				}
			} else if (itemKey.split(" ").length == 2) {
				if (itemKey.split(" ")[0].split(":").length == 2) {
					try {
						itemId = InventoryBackend.getInstance().checkAlias(itemKey.split(" ")[0]).getMaterial().getId();
					} catch (NumberFormatException e) {
						BenCmd.log(Level.SEVERE, "Error in kit (id: " + String.valueOf(ID) + "). " + itemKey.split(" ")[0] + " is NaN!");
						continue;
					}
					try {
						itemDamage = Short.parseShort(itemKey.split(" ")[0].split(":")[1]);
					} catch (NumberFormatException e) {
						BenCmd.log(Level.SEVERE, "Error in kit (id: " + String.valueOf(ID) + "). " + itemKey.split(" ")[0].split(":")[1] + " is NaN!");
						continue;
					}
				} else {
					try {
						itemId = InventoryBackend.getInstance().checkAlias(itemKey.split(" ")[0]).getMaterial().getId();
					} catch (NumberFormatException e) {
						BenCmd.log(Level.SEVERE, "Error in kit (id: " + String.valueOf(ID) + "). " + itemKey.split(" ")[0].split(":")[0] + " is NaN!");
						continue;
					}
					itemDamage = 0;
				}
				try {
					itemAmount = Integer.parseInt(itemKey.split(" ")[1]);
				} catch (NumberFormatException e) {
					BenCmd.log(Level.SEVERE, "Error in kit (id: " + String.valueOf(ID) + "). " + itemKey.split(" ")[0].split(":")[0] + " is NaN!");
					continue;
				}
			} else {
				BenCmd.log(Level.SEVERE, "Error in kit (id: " + String.valueOf(ID) + "). Too many/not enough spaces.");
				continue;
			}
			items.add(new ItemStack(itemId, itemAmount, itemDamage));
		}
	}

	public boolean canUseKit(User user) {
		if (user.hasPerm("bencmd.inv.kit.all")) {
			return true;
		}
		return (user.inGroup(BenCmd.getPermissionManager().getGroupFile().getGroup(group)));
	}

	public boolean giveKit(User user) {
		if (!canUseKit(user)) {
			return false;
		}
		for (ItemStack item : items) {
			if (((Player) user.getHandle()).getInventory().firstEmpty() >= 0) {
				((Player) user.getHandle()).getInventory().addItem(item);
			} else {
				((Player) user.getHandle()).getWorld().dropItem(((Player) user.getHandle()).getLocation(), item);
			}
		}
		return true;
	}

	public void forceGiveKit(User user) {
		for (ItemStack item : items) {
			if (((Player) user.getHandle()).getInventory().firstEmpty() >= 0) {
				((Player) user.getHandle()).getInventory().addItem(item);
			} else {
				((Player) user.getHandle()).getWorld().dropItem(((Player) user.getHandle()).getLocation(), item);
			}
		}
	}

	public String getName() {
		return kitName;
	}

	public int getId() {
		return kitId;
	}

	public List<ItemStack> getItems() {
		return items;
	}
}
