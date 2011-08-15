package ben_dude56.plugins.bencmd.invtools.kits;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;

import ben_dude56.plugins.bencmd.BenCmd;
import ben_dude56.plugins.bencmd.User;
import ben_dude56.plugins.bencmd.invtools.InventoryBackend;

public class Kit {
	private List<ItemStack> items = new ArrayList<ItemStack>();
	private String group;
	private String kitName;
	private int kitId;
	BenCmd plugin;

	public Kit(BenCmd instance, int ID, String value, String name) {
		plugin = instance;
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
						itemId = plugin.kits.back
								.checkAlias(itemKey.split(" ")[0])
								.getMaterial().getId();
					} catch (NumberFormatException e) {
						plugin.log.severe("Error in kit (id: "
								+ String.valueOf(ID) + "). "
								+ itemKey.split(" ")[0] + " is NaN!");
						plugin.bLog.severe("Error in kit (id: "
								+ String.valueOf(ID) + "). "
								+ itemKey.split(" ")[0] + " is NaN!");
						continue;
					}
					try {
						itemDamage = Short.parseShort(itemKey.split(" ")[0]
								.split(":")[1]);
					} catch (NumberFormatException e) {
						plugin.log.severe("Error in kit (id: "
								+ String.valueOf(ID) + "). "
								+ itemKey.split(" ")[0].split(":")[1]
								+ " is NaN!");
						plugin.bLog.severe("Error in kit (id: "
								+ String.valueOf(ID) + "). "
								+ itemKey.split(" ")[0].split(":")[1]
								+ " is NaN!");
						continue;
					}
					itemAmount = 1;
				} else {
					try {
						itemId = new InventoryBackend(plugin)
								.checkAlias(itemKey.split(" ")[0])
								.getMaterial().getId();
					} catch (NumberFormatException e) {
						plugin.log.severe("Error in kit (id: "
								+ String.valueOf(ID) + "). "
								+ itemKey.split(" ")[0].split(":")[0]
								+ " is NaN!");
						plugin.bLog.severe("Error in kit (id: "
								+ String.valueOf(ID) + "). "
								+ itemKey.split(" ")[0].split(":")[0]
								+ " is NaN!");
						continue;
					}
					itemAmount = 1;
					itemDamage = 0;
				}
			} else if (itemKey.split(" ").length == 2) {
				if (itemKey.split(" ")[0].split(":").length == 2) {
					try {
						itemId = new InventoryBackend(plugin)
								.checkAlias(itemKey.split(" ")[0])
								.getMaterial().getId();
					} catch (NumberFormatException e) {
						plugin.log.severe("Error in kit (id: "
								+ String.valueOf(ID) + "). "
								+ itemKey.split(" ")[0] + " is NaN!");
						plugin.bLog.severe("Error in kit (id: "
								+ String.valueOf(ID) + "). "
								+ itemKey.split(" ")[0] + " is NaN!");
						continue;
					}
					try {
						itemDamage = Short.parseShort(itemKey.split(" ")[0]
								.split(":")[1]);
					} catch (NumberFormatException e) {
						plugin.log.severe("Error in kit (id: "
								+ String.valueOf(ID) + "). "
								+ itemKey.split(" ")[0].split(":")[1]
								+ " is NaN!");
						plugin.bLog.severe("Error in kit (id: "
								+ String.valueOf(ID) + "). "
								+ itemKey.split(" ")[0].split(":")[1]
								+ " is NaN!");
						continue;
					}
				} else {
					try {
						itemId = new InventoryBackend(plugin)
								.checkAlias(itemKey.split(" ")[0])
								.getMaterial().getId();
					} catch (NumberFormatException e) {
						plugin.log.severe("Error in kit (id: "
								+ String.valueOf(ID) + "). "
								+ itemKey.split(" ")[0].split(":")[0]
								+ " is NaN!");
						plugin.bLog.severe("Error in kit (id: "
								+ String.valueOf(ID) + "). "
								+ itemKey.split(" ")[0].split(":")[0]
								+ " is NaN!");
						continue;
					}
					itemDamage = 0;
				}
				try {
					itemAmount = Integer.parseInt(itemKey.split(" ")[1]);
				} catch (NumberFormatException e) {
					plugin.log.severe("Error in kit (id: " + String.valueOf(ID)
							+ "). " + itemKey.split(" ")[0].split(":")[0]
							+ " is NaN!");
					plugin.bLog.severe("Error in kit (id: "
							+ String.valueOf(ID) + "). "
							+ itemKey.split(" ")[0].split(":")[0] + " is NaN!");
					continue;
				}
			} else {
				plugin.log.severe("Error in kit (id: " + String.valueOf(ID)
						+ "). Too many/not enough spaces.");
				plugin.bLog.severe("Error in kit (id: " + String.valueOf(ID)
						+ "). Too many/not enough spaces.");
				continue;
			}
			items.add(new ItemStack(itemId, itemAmount, itemDamage));
		}
	}

	public boolean canUseKit(User user) {
		if (user.hasPerm("allKit")) {
			return true;
		}
		return (user.inGroup(plugin.perm.groupFile.getGroup(group)));
	}

	public boolean giveKit(User user) {
		if (!canUseKit(user)) {
			return false;
		}
		for (ItemStack item : items) {
			if (user.getHandle().getInventory().firstEmpty() >= 0) {
				user.getHandle().getInventory().addItem(item);
			} else {
				user.getHandle().getWorld()
						.dropItem(user.getHandle().getLocation(), item);
			}
		}
		return true;
	}

	public void forceGiveKit(User user) {
		for (ItemStack item : items) {
			if (user.getHandle().getInventory().firstEmpty() >= 0) {
				user.getHandle().getInventory().addItem(item);
			} else {
				user.getHandle().getWorld()
						.dropItem(user.getHandle().getLocation(), item);
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
