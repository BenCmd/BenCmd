package ben_dude56.plugins.bencmd.invtools;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import ben_dude56.plugins.bencmd.*;
import ben_dude56.plugins.bencmd.invtools.kits.*;

public class InventoryCommands implements Commands {
	BenCmd plugin;
	InventoryBackend back;

	public InventoryCommands(BenCmd instance) {
		plugin = instance;
		back = new InventoryBackend(plugin);
	}

	public boolean onCommand(CommandSender sender, Command command,
			String commandLabel, String[] args) {
		User user;
		try {
			user = User.getUser(plugin, (Player) sender);
		} catch (ClassCastException e) {
			user = User.getUser(plugin);
		}
		if ((commandLabel.equalsIgnoreCase("item") || commandLabel
				.equalsIgnoreCase("i")) && user.hasPerm("canSpawnItems")) {
			Item(args, user);
			return true;
		} else if ((commandLabel.equalsIgnoreCase("clearinventory") || commandLabel
				.equalsIgnoreCase("clrinv"))
				&& user.hasPerm("canClearInventory")) {
			ClearInventory(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("unl")
				&& user.hasPerm("canMakeUnlDisp")) {
			Unl(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("disp")
				&& user.hasPerm("canMakeDispChest")) {
			Disp(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("kit")
				&& user.hasPerm("canSpawnKit")) {
			Kit(args, user);
			return true;
		}
		return false;
	}

	public void Unl(String[] args, User user) {
		if (user.isServer()) {
			user.sendMessage(ChatColor.RED + "The server cannot do that!");
			return;
		}
		if (args.length != 1) {
			user.sendMessage(ChatColor.YELLOW
					+ "Proper use is /unl <ID>[:damage]");
			return;
		}
		BCItem Item;
		Item = back.checkAlias(args[0]);
		Block blockToAdd = user.getHandle().getTargetBlock(null, 30);
		if (blockToAdd.getType() != Material.DISPENSER) {
			user.sendMessage(ChatColor.RED
					+ "You must be pointing at a dispenser to do that!");
			return;
		}
		if (Item == null) {
			user.sendMessage(ChatColor.RED + "Invalid item ID or damage!");
			return;
		}
		if (!plugin.perm.itemList.canSpawn(Item.getMaterial(), user
				.highestLevelGroup().getName())) {
			user.sendMessage(ChatColor.RED
					+ "You're not allowed to spawn that item!");
			return;
		}
		plugin.dispensers.addDispenser(
				blockToAdd.getLocation(),
				String.valueOf(Item.getMaterial().getId()) + ":"
						+ String.valueOf(Item.getDamage()));
		user.sendMessage(ChatColor.GREEN
				+ "Unlimited dispenser successfully activated!");
	}

	public void Disp(String[] args, User user) {
		if (user.isServer()) {
			user.sendMessage(ChatColor.RED + "The server cannot do that!");
			return;
		}
		if (args.length != 0) {
			user.sendMessage(ChatColor.YELLOW + "Proper use is /disp");
			return;
		}
		Block blockToAdd = user.getHandle().getTargetBlock(null, 30);
		if (blockToAdd.getType() != Material.CHEST) {
			user.sendMessage(ChatColor.RED
					+ "You must be pointing at a chest to do that!");
			return;
		}
		plugin.chests.addChest(blockToAdd.getLocation());
		user.sendMessage(ChatColor.GREEN
				+ "Disposal chest successfully activated!");
	}

	public void Item(String[] args, User user) {
		if (args.length == 0 || args.length > 3) {
			user.sendMessage(ChatColor.YELLOW
					+ "Proper use is /item <ID>[:damage] [amount] [player]");
			return;
		}
		BCItem Item;
		if ((Item = back.checkAlias(args[0])) == null) {
			user.sendMessage(ChatColor.RED + "Invalid item ID or damage!");
			return;
		}
		int Amount = 1;
		if (args.length >= 2) {
			try {
				Amount = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				user.sendMessage(ChatColor.RED + "Invalid amount");
				return;
			}
		}
		int fullAmount = Amount;
		List<Integer> splitamount = new ArrayList<Integer>();
		while (Amount > 0) {
			Integer maxAmount = back.getStackNumber(Item.getMaterial().getId());
			if (Amount > maxAmount) {
				splitamount.add(maxAmount);
				Amount -= maxAmount;
			} else {
				splitamount.add(Amount);
				Amount = 0;
			}
		}
		Material mat = Item.getMaterial();
		if (!plugin.perm.itemList.canSpawn(mat, user.highestLevelGroup()
				.getName())) {
			user.sendMessage(ChatColor.RED
					+ "You're not allowed to spawn that item!");
			return;
		}
		int ItemDamage = Item.getDamage();
		if (args.length == 3) {
			User user2;
			if ((user2 = User.matchUser(args[2], plugin)) == null) {
				user.sendMessage(ChatColor.RED + "Cannot find the player '"
						+ args[2] + "'");
				return;
			}
			for (Integer amount : splitamount) {
				if (user2.getHandle().getInventory().firstEmpty() >= 0) {
					user2.getHandle()
							.getInventory()
							.addItem(
									new ItemStack(mat, amount,
											(short) ItemDamage));
				} else {
					user2.getHandle()
							.getWorld()
							.dropItem(
									user2.getHandle().getLocation(),
									new ItemStack(mat, amount,
											(short) ItemDamage));
				}
			}
			user2.sendMessage(ChatColor.GREEN + user.getDisplayName()
					+ " has sent you a gift.");
			user.sendMessage(ChatColor.GREEN + "Your gift has been sent!");
			plugin.log.info("BenCmd: " + user.getDisplayName() + " gave "
					+ user2.getDisplayName() + " an item. (id: "
					+ String.valueOf(mat.getId()) + ", amount: "
					+ String.valueOf(fullAmount) + ", damage: "
					+ String.valueOf(ItemDamage) + ")");
			plugin.bLog.info("BenCmd: " + user.getDisplayName() + " gave "
					+ user2.getDisplayName() + " an item. (id: "
					+ String.valueOf(mat.getId()) + ", amount: "
					+ String.valueOf(fullAmount) + ", damage: "
					+ String.valueOf(ItemDamage) + ")");
		} else {
			if (user.isServer()) {
				user.sendMessage(ChatColor.RED + "The server cannot do that!");
				return;
			}
			for (Integer amount : splitamount) {
				if (user.getHandle().getInventory().firstEmpty() >= 0) {
					user.getHandle()
							.getInventory()
							.addItem(
									new ItemStack(mat, amount,
											(short) ItemDamage));
				} else {
					user.getHandle()
							.getWorld()
							.dropItem(
									user.getHandle().getLocation(),
									new ItemStack(mat, amount,
											(short) ItemDamage));
				}
			}
			user.sendMessage(ChatColor.GREEN + "Enjoy, "
					+ user.getDisplayName() + "!");
			plugin.log.info("BenCmd: " + user.getDisplayName() + " gave "
					+ user.getDisplayName() + " an item. (id: "
					+ String.valueOf(mat.getId()) + ", amount: "
					+ String.valueOf(fullAmount) + ", damage: "
					+ String.valueOf(ItemDamage) + ")");
			plugin.bLog.info("BenCmd: " + user.getDisplayName() + " gave "
					+ user.getDisplayName() + " an item. (id: "
					+ String.valueOf(mat.getId()) + ", amount: "
					+ String.valueOf(fullAmount) + ", damage: "
					+ String.valueOf(ItemDamage) + ")");
		}
	}

	public void ClearInventory(String[] args, User user) {
		if (args.length == 0) {
			if (user.isServer()) {
				user.sendMessage(ChatColor.RED + "The server cannot do that!");
				return;
			}
			user.getHandle().getInventory().clear(); // Clear the player's
														// inventory
			plugin.log.info(user.getDisplayName()
					+ " has cleared their own inventory.");
			plugin.bLog.info(user.getDisplayName()
					+ " has cleared their own inventory.");
		} else if (args.length == 1) {
			// Clear the other player's inventory
			User user2;
			if ((user2 = User.matchUser(args[0], plugin)) != null) {
				user2.getHandle().getInventory().clear();
				plugin.log.info(user.getDisplayName() + " has cleared "
						+ args[0] + "'s inventory.");
				plugin.bLog.info(user.getDisplayName() + " has cleared "
						+ args[0] + "'s inventory.");
			} else {
				user.sendMessage(ChatColor.RED + args[0]
						+ " doesn't exist or is not online.");
			}
		} else {
			user.sendMessage(ChatColor.YELLOW
					+ "Proper use is /clearinventory [player]");
		}
	}

	public void Kit(String[] args, User user) {
		switch (args.length) {
		case 0:
			String kits = "";
			for (Kit kit : plugin.kits.kits) {
				if (kit.canUseKit(user)) {
					kits += " " + kit.getName();
				}
			}
			if (kits.isEmpty()) {
				user.sendMessage(ChatColor.RED
						+ "You cannot access any kits...");
			} else {
				user.sendMessage(ChatColor.YELLOW
						+ "The following kits are available: " + kits);
			}
			break;
		case 1:
			if (user.isServer()) {
				user.sendMessage(ChatColor.RED + "The server cannot do that!");
				return;
			}
			if (plugin.kits.kitExists(args[0])) {
				if (plugin.kits.canUseKit(user, args[0])) {
					plugin.kits.giveKit(user, args[0]);
					user.sendMessage(ChatColor.GREEN + "Enjoy, "
							+ user.getDisplayName() + "!");
					plugin.log.info("User " + user.getDisplayName()
							+ " has spawned kit " + args[0] + "!");
					plugin.bLog.info("User " + user.getDisplayName()
							+ " has spawned kit " + args[0] + "!");
				} else {
					user.sendMessage(ChatColor.RED
							+ "That kit doesn't exist or you don't have permission to use it!");
				}
			} else {
				user.sendMessage(ChatColor.RED
						+ "That kit doesn't exist or you don't have permission to use it!");
			}
			break;
		case 2:
			if (plugin.kits.kitExists(args[0])) {
				if (plugin.kits.canUseKit(user, args[0])) {
					User user2;
					if ((user2 = User.matchUser(args[1], plugin)) == null) {
						user.sendMessage(ChatColor.RED + args[1]
								+ " doesn't exist or is not online.");
						return;
					}
					plugin.kits.giveKit(user, args[1]);
					user2.sendMessage(ChatColor.GREEN + user.getDisplayName()
							+ " has sent you a gift.");
					user.sendMessage(ChatColor.GREEN
							+ "Your gift has been sent!");
					plugin.log.info("User " + user.getDisplayName()
							+ " has spawned kit " + args[1] + " for user "
							+ user2.getDisplayName() + "!");
					plugin.bLog.info("User " + user.getDisplayName()
							+ " has spawned kit " + args[1] + " for user "
							+ user2.getDisplayName() + "!");
				} else {
					user.sendMessage(ChatColor.RED
							+ "That kit doesn't exist or you don't have permission to use it!");
				}
			} else {
				user.sendMessage(ChatColor.RED
						+ "That kit doesn't exist or you don't have permission to use it!");
			}
			break;
		default:
			user.sendMessage(ChatColor.YELLOW
					+ "Proper use is /kit [kit] [player]");
			break;
		}
	}
}
