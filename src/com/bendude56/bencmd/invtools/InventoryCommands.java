package com.bendude56.bencmd.invtools;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.bendude56.bencmd.*;
import com.bendude56.bencmd.invtools.kits.*;

public class InventoryCommands implements Commands {
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		User user = User.getUser(sender);
		if ((commandLabel.equalsIgnoreCase("item") || commandLabel.equalsIgnoreCase("i")) && user.hasPerm("bencmd.inv.spawn")) {
			Item(args, user);
			return true;
		} else if ((commandLabel.equalsIgnoreCase("clearinventory") || commandLabel.equalsIgnoreCase("clrinv")) && user.hasPerm("bencmd.inv.clr.self")) {
			ClearInventory(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("unl") && user.hasPerm("bencmd.inv.unlimited.create")) {
			Unl(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("disp") && user.hasPerm("bencmd.inv.disposal.create")) {
			Disp(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("kit") && user.hasPerm("bencmd.inv.kit")) {
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
			user.sendMessage(ChatColor.YELLOW + "Proper use is /unl <ID>[:damage]");
			return;
		}
		BCItem Item;
		Item = InventoryBackend.getInstance().checkAlias(args[0]);
		Block blockToAdd = ((Player) user.getHandle()).getTargetBlock(null, 30);
		if (blockToAdd.getType() != Material.DISPENSER) {
			user.sendMessage(ChatColor.RED + "You must be pointing at a dispenser to do that!");
			return;
		}
		if (Item == null) {
			user.sendMessage(ChatColor.RED + "Invalid item ID or damage!");
			return;
		}
		if (!BenCmd.getPermissionManager().getItemLists().canSpawn(Item.getMaterial(), user.highestLevelGroup().getName())) {
			user.sendMessage(ChatColor.RED + "You're not allowed to spawn that item!");
			return;
		}
		BenCmd.getDispensers().addDispenser(blockToAdd.getLocation(), String.valueOf(Item.getMaterial().getId()) + ":" + String.valueOf(Item.getDamage()));
		user.sendMessage(ChatColor.GREEN + "Unlimited dispenser successfully activated!");
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
		Block blockToAdd = ((Player) user.getHandle()).getTargetBlock(null, 30);
		if (blockToAdd.getType() != Material.CHEST) {
			user.sendMessage(ChatColor.RED + "You must be pointing at a chest to do that!");
			return;
		}
		BenCmd.getDisposals().addChest(blockToAdd.getLocation());
		user.sendMessage(ChatColor.GREEN + "Disposal chest successfully activated!");
	}

	public void Item(String[] args, User user) {
		if (args.length == 0 || args.length > 3) {
			user.sendMessage(ChatColor.YELLOW + "Proper use is /item <ID>[:damage] [amount] [player]");
			return;
		}
		BCItem Item;
		if ((Item = InventoryBackend.getInstance().checkAlias(args[0])) == null) {
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
			Integer maxAmount = InventoryBackend.getInstance().getStackNumber(Item.getMaterial().getId());
			if (Amount > maxAmount) {
				splitamount.add(maxAmount);
				Amount -= maxAmount;
			} else {
				splitamount.add(Amount);
				Amount = 0;
			}
		}
		Material mat = Item.getMaterial();
		if (!BenCmd.getPermissionManager().getItemLists().canSpawn(mat, user.highestLevelGroup().getName())) {
			user.sendMessage(ChatColor.RED + "You're not allowed to spawn that item!");
			return;
		}
		int ItemDamage = Item.getDamage();
		if (args.length == 3) {
			User user2;
			if ((user2 = User.matchUser(args[2])) == null) {
				user.sendMessage(ChatColor.RED + "Cannot find the player '" + args[2] + "'");
				return;
			}
			for (Integer amount : splitamount) {
				if (((Player) user.getHandle()).getInventory().firstEmpty() >= 0) {
					((Player) user.getHandle()).getInventory().addItem(new ItemStack(mat, amount, (short) ItemDamage));
				} else {
					((Player) user.getHandle()).getWorld().dropItem(((Player) user.getHandle()).getLocation(), new ItemStack(mat, amount, (short) ItemDamage));
				}
			}
			user2.sendMessage(ChatColor.GREEN + user.getDisplayName() + " has sent you a gift.");
			user.sendMessage(ChatColor.GREEN + "Your gift has been sent!");
			BenCmd.log("BenCmd: " + user.getDisplayName() + " gave " + user2.getDisplayName() + " an item. (id: " + String.valueOf(mat.getId()) + ", amount: " + String.valueOf(fullAmount) + ", damage: " + String.valueOf(ItemDamage) + ")");
		} else {
			if (user.isServer()) {
				user.sendMessage(ChatColor.RED + "The server cannot do that!");
				return;
			}
			for (Integer amount : splitamount) {
				if (((Player) user.getHandle()).getInventory().firstEmpty() >= 0) {
					((Player) user.getHandle()).getInventory().addItem(new ItemStack(mat, amount, (short) ItemDamage));
				} else {
					((Player) user.getHandle()).getWorld().dropItem(((Player) user.getHandle()).getLocation(), new ItemStack(mat, amount, (short) ItemDamage));
				}
			}
			user.sendMessage(ChatColor.GREEN + "Enjoy, " + user.getDisplayName() + "!");
			BenCmd.log("BenCmd: " + user.getDisplayName() + " gave " + user.getDisplayName() + " an item. (id: " + String.valueOf(mat.getId()) + ", amount: " + String.valueOf(fullAmount) + ", damage: " + String.valueOf(ItemDamage) + ")");
		}
	}

	public void ClearInventory(String[] args, User user) {
		if (args.length == 0) {
			if (user.isServer()) {
				user.sendMessage(ChatColor.RED + "The server cannot do that!");
				return;
			}
			((Player) user.getHandle()).getInventory().clear(); // Clear the
																// player's
			// inventory
			BenCmd.log(user.getDisplayName() + " has cleared their own inventory.");
		} else if (args.length == 1) {
			if (!user.hasPerm("bencmd.inv.clr.other")) {
				user.sendMessage(ChatColor.RED + "You don't have permission to do that!");
				BenCmd.getPlugin().logPermFail();
				return;
			}
			// Clear the other player's inventory
			User user2;
			if ((user2 = User.matchUser(args[0])) != null) {
				if (user2.hasPerm("bencmd.inv.clr.protect") && !user.hasPerm("bencmd.inv.clr.all")) {
					user.sendMessage(ChatColor.RED + "That player is protected from being godded/ungodded by others!");
					return;
				}
				((Player) user2.getHandle()).getInventory().clear();
				BenCmd.log(user.getDisplayName() + " has cleared " + args[0] + "'s inventory.");
			} else {
				user.sendMessage(ChatColor.RED + args[0] + " doesn't exist or is not online.");
			}
		} else {
			user.sendMessage(ChatColor.YELLOW + "Proper use is /clearinventory [player]");
		}
	}

	public void Kit(String[] args, User user) {
		switch (args.length) {
			case 0:
				String kits = "";
				for (Kit kit : BenCmd.getKitList().kits) {
					if (kit.canUseKit(user)) {
						kits += " " + kit.getName();
					}
				}
				if (kits.isEmpty()) {
					user.sendMessage(ChatColor.RED + "You cannot access any kits...");
				} else {
					user.sendMessage(ChatColor.YELLOW + "The following kits are available: " + kits);
				}
				break;
			case 1:
				if (user.isServer()) {
					user.sendMessage(ChatColor.RED + "The server cannot do that!");
					return;
				}
				if (BenCmd.getKitList().kitExists(args[0])) {
					if (BenCmd.getKitList().canUseKit(user, args[0])) {
						BenCmd.getKitList().giveKit(user, args[0]);
						user.sendMessage(ChatColor.GREEN + "Enjoy, " + user.getDisplayName() + "!");
						BenCmd.log("User " + user.getDisplayName() + " has spawned kit " + args[0] + "!");
					} else {
						user.sendMessage(ChatColor.RED + "That kit doesn't exist or you don't have permission to use it!");
					}
				} else {
					user.sendMessage(ChatColor.RED + "That kit doesn't exist or you don't have permission to use it!");
				}
				break;
			case 2:
				if (BenCmd.getKitList().kitExists(args[0])) {
					if (BenCmd.getKitList().canUseKit(user, args[0])) {
						User user2;
						if ((user2 = User.matchUser(args[1])) == null) {
							user.sendMessage(ChatColor.RED + args[1] + " doesn't exist or is not online.");
							return;
						}
						BenCmd.getKitList().giveKit(user, args[1]);
						user2.sendMessage(ChatColor.GREEN + user.getDisplayName() + " has sent you a gift.");
						user.sendMessage(ChatColor.GREEN + "Your gift has been sent!");
						BenCmd.log("User " + user.getDisplayName() + " has spawned kit " + args[1] + " for user " + user2.getDisplayName() + "!");
					} else {
						user.sendMessage(ChatColor.RED + "That kit doesn't exist or you don't have permission to use it!");
					}
				} else {
					user.sendMessage(ChatColor.RED + "That kit doesn't exist or you don't have permission to use it!");
				}
				break;
			default:
				user.sendMessage(ChatColor.YELLOW + "Proper use is /kit [kit] [player]");
				break;
		}
	}
}
