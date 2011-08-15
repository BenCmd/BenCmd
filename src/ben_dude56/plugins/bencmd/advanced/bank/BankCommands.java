package ben_dude56.plugins.bencmd.advanced.bank;

import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import ben_dude56.plugins.bencmd.BenCmd;
import ben_dude56.plugins.bencmd.Commands;
import ben_dude56.plugins.bencmd.User;
import ben_dude56.plugins.bencmd.money.BuyableItem;
import ben_dude56.plugins.bencmd.money.Currency;

public class BankCommands implements Commands {

	private BenCmd plugin;

	public BankCommands(BenCmd instance) {
		plugin = instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String commandLabel, String[] args) {
		User user;
		try {
			user = User.getUser(plugin, (Player) sender);
		} catch (ClassCastException e) {
			user = User.getUser(plugin);
		}
		if (commandLabel.equalsIgnoreCase("bank")
				&& (user.hasPerm("isBankAdmin") || user
						.hasPerm("canBankRemote"))) {
			Bank(args, user);
			return true;
		}
		return false;
	}

	public void Bank(String[] args, User user) {
		if (args.length == 0) {
			if (!plugin.banks.hasBank(user.getName())) {
				plugin.banks.addBank(new BankInventory(user.getName(), plugin));
			}
			plugin.banks.openInventory(user.getHandle());
		} else if (args.length == 1) {
			if (args[0].equalsIgnoreCase("upgrade")) {
				if (plugin.banks.getBank(user.getName()).isUpgraded()) {
					user.sendMessage(ChatColor.RED
							+ "Your bank has already been upgraded!");
				} else {
					if (user.hasPerm("isBankAdmin")) {
						plugin.banks.upgradeBank(user.getName());
						user.sendMessage(ChatColor.GREEN
								+ "Enjoy the extra bank space!");
					} else {
						if (BuyableItem.hasMoney(user, plugin.mainProperties
								.getDouble("bankUpgradeCost", 4096), plugin)) {
							BuyableItem
									.remMoney(user,
											plugin.mainProperties.getDouble(
													"bankUpgradeCost", 4096),
											plugin);
							plugin.banks.upgradeBank(user.getName());
							user.sendMessage(ChatColor.GREEN
									+ "Enjoy the extra bank space!");
						} else {
							user.sendMessage(ChatColor.RED
									+ "You need at least "
									+ plugin.mainProperties.getDouble(
											"bankUpgradeCost", 4096)
									+ " worth of currency to upgrade your bank!");
						}
					}
				}
			} else if (args[0].equalsIgnoreCase("downgrade")) {
				if (plugin.banks.getBank(user.getName()).isUpgraded()) {
					if (!plugin.banks.canDowngradeBank(user.getName())) {
						user.sendMessage(ChatColor.RED
								+ "The bottom half of your bank must be empty in order to proceed!");
						return;
					}
					plugin.banks.downgradeBank(user.getName());
					if (!user.hasPerm("isBankAdmin")) {
						Object[] ac = plugin.prices.getCurrencies().toArray();
						Arrays.sort(ac);
						HashMap<Currency, Integer> change = BuyableItem
								.makeChange(plugin.mainProperties.getDouble(
										"bankUpgradeCost", 4096), ac);
						for (int i = 0; i < change.size(); i++) {
							Currency c = (Currency) change.keySet().toArray()[i];
							int a = change.get(c);
							if (a != 0) {
								if (user.getHandle().getInventory()
										.firstEmpty() >= 0) {
									user.getHandle()
											.getInventory()
											.addItem(
													new ItemStack(c
															.getMaterial(), a));
								} else {
									user.getHandle()
											.getWorld()
											.dropItem(
													user.getHandle()
															.getLocation(),
													new ItemStack(c
															.getMaterial(), a));
								}
							}
						}
					}
					user.sendMessage(ChatColor.GREEN
							+ "Your bank has been downgraded successfully!");
				} else {
					user.sendMessage(ChatColor.RED
							+ "Your bank isn't upgraded!");
				}
			} else {
				if (!user.hasPerm("isBankAdmin")) {
					user.sendMessage(ChatColor.RED
							+ "You need to be an admin to do that!");
					return;
				}
				if (!plugin.banks.hasBank(args[0])) {
					user.sendMessage(ChatColor.RED
							+ "That player doesn't have a bank account!");
					return;
				}
				plugin.banks.openInventory(args[0], user.getHandle());
			}
		} else if (args.length == 2) {
			if (!user.hasPerm("isBankAdmin")) {
				user.sendMessage(ChatColor.RED
						+ "You need to be an admin to do that!");
				return;
			}
			if (!plugin.banks.hasBank(args[0])) {
				user.sendMessage(ChatColor.RED
						+ "That player doesn't have a bank account!");
				return;
			}
			if (args[1].equalsIgnoreCase("upgrade")) {
				if (plugin.banks.getBank(args[0]).isUpgraded()) {
					user.sendMessage(ChatColor.RED
							+ "That player's bank is already upgraded!");
				} else {
					plugin.banks.upgradeBank(args[0]);
					user.sendMessage(ChatColor.GREEN
							+ "That player's bank has been upgraded!");
				}
			} else if (args[1].equalsIgnoreCase("downgrade")) {
				if (plugin.banks.getBank(args[0]).isUpgraded()) {
					if (!plugin.banks.canDowngradeBank(args[0])) {
						user.sendMessage(ChatColor.RED
								+ "The bottom half of that player's bank must be empty in order to proceed!");
						return;
					}
					plugin.banks.downgradeBank(args[0]);
					user.sendMessage(ChatColor.GREEN
							+ "That player's bank has been downgraded successfully!");
				} else {
					user.sendMessage(ChatColor.RED
							+ "That player's bank isn't upgraded!");
				}
			} else {
				user.sendMessage(ChatColor.YELLOW
						+ "Proper usage: /bank [{upgrade|downgrade|<name> [{upgrade|downgrade}]}]");
			}
		} else {
			if (user.hasPerm("isBankAdmin")) {
				user.sendMessage(ChatColor.YELLOW
						+ "Proper usage: /bank [{upgrade|downgrade|<name> [{upgrade|downgrade}]}]");
			} else {
				user.sendMessage(ChatColor.YELLOW
						+ "Proper usage: /bank [{upgrade|downgrade}]");
			}
		}
	}

}
