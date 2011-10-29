package com.bendude56.bencmd.advanced.bank;

import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.Commands;
import com.bendude56.bencmd.User;
import com.bendude56.bencmd.money.BuyableItem;
import com.bendude56.bencmd.money.Currency;
import com.bendude56.bencmd.permissions.PermissionUser;

public class BankCommands implements Commands {

	public BankCommands() {}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		User user;
		try {
			user = User.getUser((Player) sender);
		} catch (ClassCastException e) {
			user = User.getUser();
		}
		if (commandLabel.equalsIgnoreCase("bank") && (user.hasPerm("bencmd.bank.admin") || user.hasPerm("bencmd.bank.remote"))) {
			Bank(args, user);
			return true;
		}
		return false;
	}

	public void Bank(String[] args, User user) {
		if (args.length == 0) {
			if (!BenCmd.getBankController().hasBank(user.getName())) {
				BenCmd.getBankController().addBank(new BankInventory(user.getName()));
			}
			BenCmd.log(user.getName() + " has opened their bank!");
			BenCmd.getBankController().openInventory(((Player) user.getHandle()));
		} else if (args.length == 1) {
			if (args[0].equalsIgnoreCase("upgrade")) {
				if (BenCmd.getBankController().getBank(user.getName()).isUpgraded()) {
					user.sendMessage(ChatColor.RED + "Your bank has already been upgraded!");
				} else {
					if (user.hasPerm("bencmd.bank.admin")) {
						BenCmd.log(user.getName() + " has upgraded their bank!");
						BenCmd.getBankController().upgradeBank(user.getName());
						user.sendMessage(ChatColor.GREEN + "Enjoy the extra bank space!");
					} else {
						if (BuyableItem.hasMoney(user, BenCmd.getMainProperties().getDouble("bankUpgradeCost", 4096))) {
							BuyableItem.remMoney(user, BenCmd.getMainProperties().getDouble("bankUpgradeCost", 4096));
							BenCmd.getBankController().upgradeBank(user.getName());
							BenCmd.log(user.getName() + " has upgraded their bank!");
							user.sendMessage(ChatColor.GREEN + "Enjoy the extra bank space!");
						} else {
							user.sendMessage(ChatColor.RED + "You need at least " + BenCmd.getMainProperties().getDouble("bankUpgradeCost", 4096) + " worth of currency to upgrade your bank!");
						}
					}
				}
			} else if (args[0].equalsIgnoreCase("downgrade")) {
				if (BenCmd.getBankController().getBank(user.getName()).isUpgraded()) {
					if (!BenCmd.getBankController().canDowngradeBank(user.getName())) {
						user.sendMessage(ChatColor.RED + "The bottom half of your bank must be empty in order to proceed!");
						return;
					}
					BenCmd.getBankController().downgradeBank(user.getName());
					if (!user.hasPerm("bencmd.bank.admin")) {
						Object[] ac = BenCmd.getMarketController().getCurrencies().toArray();
						Arrays.sort(ac);
						HashMap<Currency, Integer> change = BuyableItem.makeChange(BenCmd.getMainProperties().getDouble("bankUpgradeCost", 4096), ac);
						for (int i = 0; i < change.size(); i++) {
							Currency c = (Currency) change.keySet().toArray()[i];
							int a = change.get(c);
							if (a != 0) {
								if (((Player) user.getHandle()).getInventory().firstEmpty() >= 0) {
									((Player) user.getHandle()).getInventory().addItem(new ItemStack(c.getMaterial(), a));
								} else {
									((Player) user.getHandle()).getWorld().dropItem(((Player) user.getHandle()).getLocation(), new ItemStack(c.getMaterial(), a));
								}
							}
						}
					}
					BenCmd.log(user.getName() + " has downgraded their bank!");
					user.sendMessage(ChatColor.GREEN + "Your bank has been downgraded successfully!");
				} else {
					user.sendMessage(ChatColor.RED + "Your bank isn't upgraded!");
				}
			} else {
				if (!user.hasPerm("bencmd.bank.admin")) {
					user.sendMessage(ChatColor.RED + "You need to be an admin to do that!");
					BenCmd.getPlugin().logPermFail();
					return;
				}
				if (!BenCmd.getBankController().hasBank(args[0])) {
					user.sendMessage(ChatColor.RED + "That player doesn't have a bank account!");
					return;
				}
				if (PermissionUser.matchUserIgnoreCase(args[0]).hasPerm("bencmd.bank.protect") && !user.hasPerm("bencmd.bank.all")) {
					user.sendMessage(ChatColor.RED + "That player's bank is protected!");
					return;
				}
				BenCmd.log(user.getName() + " has opened " + args[0] + "'s bank!");
				BenCmd.getBankController().openInventory(args[0], ((Player) user.getHandle()));
			}
		} else if (args.length == 2) {
			if (!user.hasPerm("bencmd.bank.admin")) {
				user.sendMessage(ChatColor.RED + "You need to be an admin to do that!");
				BenCmd.getPlugin().logPermFail();
				return;
			}
			if (!BenCmd.getBankController().hasBank(args[0])) {
				user.sendMessage(ChatColor.RED + "That player doesn't have a bank account!");
				return;
			}
			if (PermissionUser.matchUserIgnoreCase(args[0]).hasPerm("bencmd.bank.protect") && !user.hasPerm("bencmd.bank.all")) {
				user.sendMessage(ChatColor.RED + "That player's bank is protected!");
				return;
			}
			if (args[1].equalsIgnoreCase("upgrade")) {
				if (BenCmd.getBankController().getBank(args[0]).isUpgraded()) {
					user.sendMessage(ChatColor.RED + "That player's bank is already upgraded!");
				} else {
					BenCmd.getBankController().upgradeBank(args[0]);
					BenCmd.log(user.getName() + " has upgraded " + args[0] + "'s bank!");
					user.sendMessage(ChatColor.GREEN + "That player's bank has been upgraded!");
				}
			} else if (args[1].equalsIgnoreCase("downgrade")) {
				if (BenCmd.getBankController().getBank(args[0]).isUpgraded()) {
					if (!BenCmd.getBankController().canDowngradeBank(args[0])) {
						user.sendMessage(ChatColor.RED + "The bottom half of that player's bank must be empty in order to proceed!");
						return;
					}
					BenCmd.log(user.getName() + " has downgraded " + args[0] + "'s bank!");
					BenCmd.getBankController().downgradeBank(args[0]);
					user.sendMessage(ChatColor.GREEN + "That player's bank has been downgraded successfully!");
				} else {
					user.sendMessage(ChatColor.RED + "That player's bank isn't upgraded!");
				}
			} else {
				user.sendMessage(ChatColor.YELLOW + "Proper usage: /bank [{upgrade|downgrade|<name> [{upgrade|downgrade}]}]");
			}
		} else {
			if (user.hasPerm("bencmd.bank.admin")) {
				user.sendMessage(ChatColor.YELLOW + "Proper usage: /bank [{upgrade|downgrade|<name> [{upgrade|downgrade}]}]");
			} else {
				user.sendMessage(ChatColor.YELLOW + "Proper usage: /bank [{upgrade|downgrade}]");
			}
		}
	}

}
