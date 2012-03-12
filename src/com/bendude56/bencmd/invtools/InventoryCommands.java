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
		} else if (commandLabel.equalsIgnoreCase("kit") && user.hasPerm("bencmd.inv.kit.self")) {
			Kit(args, user);
			return true;
		}
		return false;
	}

	public void Unl(String[] args, User user) {
		if (user.isServer()) {
			BenCmd.getLocale().sendMessage(user, "basic.noServerUse");
			return;
		}
		if (args.length != 1) {
			BenCmd.showUse(user, "unl");
			return;
		}
		BCItem Item;
		Item = InventoryBackend.getInstance().checkAlias(args[0]);
		Block blockToAdd = ((Player) user.getHandle()).getTargetBlock(null, 30);
		if (blockToAdd.getType() != Material.DISPENSER) {
			BenCmd.getLocale().sendMessage(user, "command.unl.notDispenser");
			return;
		}
		if (Item == null) {
			BenCmd.getLocale().sendMessage(user, "command.unl.invalid");
			return;
		}
		if (!BenCmd.getPermissionManager().getItemLists().canSpawn(Item.getMaterial(), user.highestLevelGroup().getName())) {
			BenCmd.getLocale().sendMessage(user, "basic.noSpawn");
			return;
		}
		BenCmd.getDispensers().addDispenser(blockToAdd.getLocation(), String.valueOf(Item.getMaterial().getId()) + ":" + String.valueOf(Item.getDamage()));
		BenCmd.getLocale().sendMessage(user, "command.unl.success");
	}

	public void Disp(String[] args, User user) {
		if (user.isServer()) {
			BenCmd.getLocale().sendMessage(user, "basic.noServerUse");
			return;
		}
		if (args.length != 0) {
			BenCmd.showUse(user, "disp");
			return;
		}
		Block blockToAdd = ((Player) user.getHandle()).getTargetBlock(null, 30);
		if (blockToAdd.getType() != Material.CHEST) {
			BenCmd.getLocale().sendMessage(user, "command.disp.notChest");
			return;
		}
		BenCmd.getDisposals().addChest(blockToAdd.getLocation());
		BenCmd.getLocale().sendMessage(user, "command.disp.success");
	}

	public void Item(String[] args, User user) {
		if (args.length == 0 || args.length > 3) {
			BenCmd.showUse(user, "item");
			return;
		}
		BCItem item;
		if ((item = InventoryBackend.getInstance().checkAlias(args[0])) == null) {
			BenCmd.getLocale().sendMessage(user, "command.item.invalidId");
			return;
		}
		int amount = 1;
		
		if (args.length >= 2) {
			try {
				amount = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				BenCmd.getLocale().sendMessage(user, "command.item.invalidAmount");
				return;
			}
		}
		int fullAmount = amount;
		List<Integer> splitAmount = new ArrayList<Integer>();
		while (amount > 0) {
			Integer maxAmount = item.getMaterial().getMaxStackSize();
			if (amount > maxAmount) {
				splitAmount.add(maxAmount);
				amount -= maxAmount;
			} else {
				splitAmount.add(amount);
				amount = 0;
			}
		}
		Material mat = item.getMaterial();
		if (!BenCmd.getPermissionManager().getItemLists().canSpawn(mat, user.highestLevelGroup().getName())) {
			BenCmd.getLocale().sendMessage(user, "basic.noSpawn");
			return;
		}
		int damage = item.getDamage();
		if (args.length == 3) {
			User user2;
			if ((user2 = User.matchUserAllowPartial(args[2])) == null) {
				BenCmd.getLocale().sendMessage(user, "basic.userNotFound", args[2]);
				return;
			}
			for (int amtAdd : splitAmount) {
				if (((Player) user2.getHandle()).getInventory().firstEmpty() >= 0) {
					((Player) user2.getHandle()).getInventory().addItem(new ItemStack(mat, amtAdd, (short) damage));
				} else {
					((Player) user2.getHandle()).getWorld().dropItem(((Player) user2.getHandle()).getLocation(), new ItemStack(mat, amtAdd, (short) damage));
				}
			}
			BenCmd.getLocale().sendMessage(user2, "command.item.giftReceive", user.getName());
			BenCmd.getLocale().sendMessage(user, "command.item.giftSend", user2.getName());
			BenCmd.log(BenCmd.getLocale().getString("log.item.other", user.getName(), user2.getName(), item.getMaterial().getId() + "", damage + "", fullAmount + ""));
		} else {
			if (user.isServer()) {
				BenCmd.getLocale().sendMessage(user, "basic.noServerUse");
				return;
			}
			for (Integer amtAdd : splitAmount) {
				if (((Player) user.getHandle()).getInventory().firstEmpty() >= 0) {
					((Player) user.getHandle()).getInventory().addItem(new ItemStack(mat, amtAdd, (short) damage));
				} else {
					((Player) user.getHandle()).getWorld().dropItem(((Player) user.getHandle()).getLocation(), new ItemStack(mat, amount, (short) damage));
				}
			}
			BenCmd.getLocale().sendMessage(user, "command.item.success", user.getName());
			BenCmd.log(BenCmd.getLocale().getString("log.item.other", user.getName(), item.getMaterial().getId() + "", damage + "", fullAmount + ""));
		}
	}

	public void ClearInventory(String[] args, User user) {
		if (args.length == 0) {
			if (user.isServer()) {
				BenCmd.getLocale().sendMessage(user, "basic.noServerUse");
				return;
			}
			((Player) user.getHandle()).getInventory().clear();
			BenCmd.log(BenCmd.getLocale().getString("log.clrinv.self", user.getName()));
		} else if (args.length == 1) {
			if (!user.hasPerm("bencmd.inv.clr.other")) {
				BenCmd.getPlugin().logPermFail(user, "clrinv", true);
				return;
			}
			// Clear the other player's inventory
			User user2;
			if ((user2 = User.matchUserAllowPartial(args[0])) != null) {
				if (user2.hasPerm("bencmd.inv.clr.protect") && !user.hasPerm("bencmd.inv.clr.all")) {
					BenCmd.getLocale().sendMessage(user, "command.clrinv.protected", user2.getName());
					return;
				}
				user2.getPlayerHandle().getInventory().clear();
				BenCmd.log(BenCmd.getLocale().getString("log.clrinv.other", user.getName(), user2.getName()));
			} else {
				BenCmd.getLocale().sendMessage(user, "basic.userNotFound", args[0]);
			}
		} else {
			BenCmd.showUse(user, "clrinv");
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
					BenCmd.getLocale().sendMessage(user, "command.kit.listNone");
				} else {
					BenCmd.getLocale().sendMessage(user, "command.kit.list");
					user.sendMessage(ChatColor.GRAY + kits);
				}
				break;
			case 1:
				if (user.isServer()) {
					BenCmd.getLocale().sendMessage(user, "basic.noServerUse");
					return;
				}
				if (BenCmd.getKitList().kitExists(args[0])) {
					if (BenCmd.getKitList().canUseKit(user, args[0])) {
						BenCmd.getKitList().giveKit(user, args[0]);
						BenCmd.getLocale().sendMessage(user, "command.kit.success", user.getName());
						BenCmd.log(BenCmd.getLocale().getString("log.item.self", user.getName(), args[0]));
					} else {
						BenCmd.getLocale().sendMessage(user, "command.kit.invalid");
						BenCmd.getPlugin().logPermFail(user, "kit", args, false);
					}
				} else {
					BenCmd.getLocale().sendMessage(user, "command.kit.invalid");
				}
				break;
			case 2:
				if (!user.hasPerm("bencmd.inv.kit.other")) {
					BenCmd.getPlugin().logPermFail(user, "kit", args, true);
					return;
				}
				if (BenCmd.getKitList().kitExists(args[0])) {
					if (BenCmd.getKitList().canUseKit(user, args[0])) {
						User user2;
						if ((user2 = User.matchUser(args[1])) == null) {
							BenCmd.getLocale().sendMessage(user, "basic.userNotFound", args[1]);
							return;
						}
						BenCmd.getKitList().giveKit(user2, args[0]);
						BenCmd.getLocale().sendMessage(user, "command.kit.giftSend", user2.getName());
						BenCmd.getLocale().sendMessage(user2, "command.kit.giftReceive", user.getName());
						BenCmd.log(BenCmd.getLocale().getString("log.item.other", user.getName(), user2.getName(), args[0]));
					} else {
						BenCmd.getLocale().sendMessage(user, "command.kit.invalid");
						BenCmd.getPlugin().logPermFail(user, "kit", args, false);
					}
				} else {
					BenCmd.getLocale().sendMessage(user, "command.kit.invalid");
				}
				break;
			default:
				BenCmd.showUse(user, "kit");
				break;
		}
	}
}
