package com.bendude56.bencmd.advanced.bank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Material;
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
		User user = User.getUser(sender);
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
			BenCmd.log(BenCmd.getLocale().getString("log.bank.openSelf", user.getName()));
			BenCmd.getBankController().openInventory(((Player) user.getHandle()));
		} else if (args.length == 1) {
			if (args[0].equalsIgnoreCase("upgrade")) {
				if (BenCmd.getBankController().getBank(user.getName()).isUpgraded()) {
					BenCmd.getLocale().sendMessage(user, "command.bank.selfAlreadyUpgraded");
				} else {
					if (user.hasPerm("bencmd.bank.admin")) {
						BenCmd.log(BenCmd.getLocale().getString("log.bank.upgradeSelf", user.getName()));
						BenCmd.getBankController().upgradeBank(user.getName());
						BenCmd.getLocale().sendMessage(user, "command.bank.selfUpgrade");
					} else {
						if (BuyableItem.hasMoney(user, BenCmd.getMainProperties().getDouble("bankUpgradeCost", 4096), new ArrayList<Material>())) {
							BuyableItem.remMoney(user, BenCmd.getMainProperties().getDouble("bankUpgradeCost", 4096), new ArrayList<Material>());
							BenCmd.getBankController().upgradeBank(user.getName());
							BenCmd.log(BenCmd.getLocale().getString("log.bank.upgradeSelf", user.getName()));
							BenCmd.getLocale().sendMessage(user, "command.bank.selfUpgrade");
						} else {
							BenCmd.getLocale().sendMessage(user, "basic.insufficientMoney", BenCmd.getMainProperties().getDouble("bankUpgradeCost", 4096) + "");
						}
					}
				}
			} else if (args[0].equalsIgnoreCase("downgrade")) {
				if (BenCmd.getBankController().getBank(user.getName()).isUpgraded()) {
					if (!BenCmd.getBankController().canDowngradeBank(user.getName())) {
						BenCmd.getLocale().sendMessage(user, "command.bank.selfCannotDowngrade");
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
					BenCmd.log(BenCmd.getLocale().getString("log.bank.downgradeSelf", user.getName()));
					BenCmd.getLocale().sendMessage(user, "command.bank.selfDowngrade");
				} else {
					BenCmd.getLocale().sendMessage(user, "command.bank.selfAlreadyDowngraded");
				}
			} else {
				if (!user.hasPerm("bencmd.bank.admin")) {
					BenCmd.getPlugin().logPermFail(user, "bank", args, true);
					return;
				}
				PermissionUser u = PermissionUser.matchUserAllowPartial(args[0]);
				if (!BenCmd.getBankController().hasBank(u.getName())) {
					BenCmd.getLocale().sendMessage(user, "command.bank.otherNoBank");
					return;
				}
				if (u.hasPerm("bencmd.bank.protect") && !user.hasPerm("bencmd.bank.all")) {
					BenCmd.getLocale().sendMessage(user, "command.bank.protected", u.getName());
					return;
				}
				BenCmd.log(BenCmd.getLocale().getString("log.bank.openOther", user.getName(), u.getName()));
				BenCmd.getBankController().openInventory(u.getName(), ((Player) user.getHandle()));
			}
		} else if (args.length == 2) {
			if (!user.hasPerm("bencmd.bank.admin")) {
				BenCmd.getPlugin().logPermFail(user, "bank", args, true);
				return;
			}
			PermissionUser u = PermissionUser.matchUserAllowPartial(args[0]);
			if (!BenCmd.getBankController().hasBank(u.getName())) {
				BenCmd.getLocale().sendMessage(user, "command.bank.otherNoBank");
				return;
			}
			if (u.hasPerm("bencmd.bank.protect") && !user.hasPerm("bencmd.bank.all")) {
				BenCmd.getLocale().sendMessage(user, "command.bank.protected", u.getName());
				return;
			}
			if (args[1].equalsIgnoreCase("upgrade")) {
				if (BenCmd.getBankController().getBank(u.getName()).isUpgraded()) {
					BenCmd.getLocale().sendMessage(user, "command.bank.otherAlreadyUpgraded", u.getName());
				} else {
					BenCmd.getBankController().upgradeBank(u.getName());
					BenCmd.log(BenCmd.getLocale().getString("log.bank.upgradeOther", user.getName(), u.getName()));
					BenCmd.getLocale().sendMessage(user, "command.bank.otherUpgrade", u.getName());
				}
			} else if (args[1].equalsIgnoreCase("downgrade")) {
				if (BenCmd.getBankController().getBank(u.getName()).isUpgraded()) {
					if (!BenCmd.getBankController().canDowngradeBank(u.getName())) {
						BenCmd.getLocale().sendMessage(user, "command.bank.otherCannotDowngrade", u.getName());
						return;
					}
					BenCmd.getBankController().downgradeBank(u.getName());
					BenCmd.log(BenCmd.getLocale().getString("log.bank.downgradeOther", user.getName(), u.getName()));
					BenCmd.getLocale().sendMessage(user, "command.bank.otherDowngrade", u.getName());
				} else {
					BenCmd.getLocale().sendMessage(user, "command.bank.otherAlreadyDowngraded", u.getName());
				}
			} else {
				BenCmd.showUse(user, "bank.admin");
			}
		} else {
			if (user.hasPerm("bencmd.bank.admin")) {
				BenCmd.showUse(user, "bank", "admin");
			} else {
				BenCmd.showUse(user, "bank", "normal");
			}
		}
	}

}
