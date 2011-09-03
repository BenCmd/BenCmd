package com.bendude56.bencmd.money;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.Commands;
import com.bendude56.bencmd.User;
import com.bendude56.bencmd.invtools.BCItem;
import com.bendude56.bencmd.invtools.InventoryBackend;
import com.bendude56.bencmd.money.BuyableItem.BuyResult;


public class MoneyCommands implements Commands {

	BenCmd plugin;

	public MoneyCommands(BenCmd instance) {
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
		if (commandLabel.equalsIgnoreCase("buy")) {
			Buy(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("sell")) {
			Sell(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("market")
				&& user.hasPerm("bencmd.market.command")) {
			Market(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("price")) {
			Price(args, user);
			return true;
		}
		return false;
	}

	public void Buy(String[] args, User user) {
		if (!plugin.mainProperties.getBoolean("marketOpen", true)) {
			user.sendMessage(ChatColor.RED + "The market is currently closed!");
			return;
		}
		if (args.length == 0 || args.length > 2) {
			user.sendMessage(ChatColor.YELLOW
					+ "Proper use is /buy <ID>[:Damage] [Amount]");
			return;
		}
		BCItem item = new InventoryBackend(plugin).checkAlias(args[0]);
		if (item == null) {
			user.sendMessage(ChatColor.RED + "Invalid item ID or damage!");
			return;
		}
		int Amount = 1;
		if (args.length == 2) {
			try {
				Amount = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				user.sendMessage(ChatColor.RED + "Invalid amount");
				return;
			}
		}
		BuyableItem bitem = plugin.prices.getItem(item);
		if (bitem == null) {
			user.sendMessage(ChatColor.RED + "That item isn't tradeable!");
			return;
		}
		BuyResult result = bitem.buyItem(user, Amount);
		switch (result) {
		case INS_FUNDS:
			user.sendMessage(ChatColor.RED
					+ "You don't have enough money to buy that! (Requires: "
					+ (bitem.getPrice() * Amount) + ")");
			break;
		case INS_SUPPLY:
			user.sendMessage(ChatColor.RED
					+ "There isn't enough supply! (Current supply: "
					+ bitem.getSupply() + ")");
			break;
		case SUCCESS:
			user.sendMessage(ChatColor.GREEN + "Enjoy, "
					+ user.getDisplayName() + "!");
			plugin.log.info(user.getDisplayName() + " bought an item. (id: "
					+ item.getMaterial().getId() + ", amount: " + Amount
					+ ", damage: " + item.getDamage() + ")");
			plugin.bLog.warning("ITEM BOUGHT: (User: " + user.getDisplayName()
					+ ", ID: " + bitem.getMaterial().getId() + ", Damage: "
					+ bitem.getDurability() + ", Amount: " + Amount
					+ ") at price: " + bitem.getPrice());
			break;
		}
	}

	public void Sell(String[] args, User user) {
		if (!plugin.mainProperties.getBoolean("marketOpen", true)) {
			user.sendMessage(ChatColor.RED + "The market is currently closed!");
			return;
		}
		if (args.length == 0 || args.length > 2) {
			user.sendMessage(ChatColor.YELLOW
					+ "Proper use is /sell <ID>[:Damage] [Amount]");
			return;
		}
		BCItem item = new InventoryBackend(plugin).checkAlias(args[0]);
		if (item == null) {
			user.sendMessage(ChatColor.RED + "Invalid item ID or damage!");
			return;
		}
		int Amount = 1;
		if (args.length == 2) {
			try {
				Amount = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				user.sendMessage(ChatColor.RED + "Invalid amount");
				return;
			}
		}
		BuyableItem bitem = plugin.prices.getItem(item);
		if (bitem == null) {
			user.sendMessage(ChatColor.RED + "That item isn't tradeable!");
			return;
		}
		if (bitem.sellItem(user, Amount)) {
			user.sendMessage(ChatColor.GREEN + "Enjoy, "
					+ user.getDisplayName() + "!");
			plugin.log.info(user.getDisplayName() + " has sold " + Amount
					+ " of item. (ID: " + bitem.getMaterial().getId() + ")");
			plugin.bLog.warning("ITEM SOLD: (User: " + user.getDisplayName()
					+ ", ID: " + bitem.getMaterial().getId() + ", Damage: "
					+ bitem.getDurability() + ", Amount: " + Amount
					+ ") at price: " + bitem.getPrice());
		} else {
			user.sendMessage(ChatColor.RED
					+ "Stop trying to sell stuff you don't have.");
		}
	}

	public void Market(String[] args, User user) {
		if (args.length == 0) {
			user.sendMessage(ChatColor.YELLOW
					+ "Proper use is /market {currency|item|supply|update}");
			return;
		}
		if (args[0].equalsIgnoreCase("currency")) {
			if (!user.hasPerm("bencmd.market.price")) {
				user.sendMessage(ChatColor.RED
						+ "You don't have permission to do that!");
				return;
			}
			MarketCurrency(args, user);
		} else if (args[0].equalsIgnoreCase("item")) {
			if (!user.hasPerm("bencmd.market.price")) {
				user.sendMessage(ChatColor.RED
						+ "You don't have permission to do that!");
				return;
			}
			MarketItem(args, user);
		} else if (args[0].equalsIgnoreCase("supply")) {
			if (!user.hasPerm("bencmd.market.supply")) {
				user.sendMessage(ChatColor.RED
						+ "You don't have permission to do that!");
				return;
			}
			MarketSupply(args, user);
		} else if (args[0].equalsIgnoreCase("update")) {
			if (!user.hasPerm("bencmd.market.update")) {
				user.sendMessage(ChatColor.RED
						+ "You don't have permission to do that!");
				return;
			}
			MarketUpdate(args, user);
		} else if (args[0].equalsIgnoreCase("noupdate")) {
			if (!user.hasPerm("bencmd.market.update")) {
				user.sendMessage(ChatColor.RED
						+ "You don't have permission to do that!");
				return;
			}
			if (!plugin.prices.isTimerEnabled()) {
				user.sendMessage(ChatColor.RED
						+ "Updating is already disabled!");
				return;
			}
			plugin.mainProperties.setProperty("updateTime", "-1");
			plugin.mainProperties.saveFile("-BenCmd Main Config-");
			user.sendMessage(ChatColor.GREEN + "Updating disabled!");
			plugin.prices.unloadTimer();
		} else if (args[0].equalsIgnoreCase("close")) {
			if (!user.hasPerm("bencmd.market.close")) {
				user.sendMessage(ChatColor.RED
						+ "You don't have permission to do that!");
				return;
			}
			if (!plugin.mainProperties.getBoolean("marketOpen", true)) {
				user.sendMessage(ChatColor.RED
						+ "The market is already closed!");
				return;
			}
			plugin.mainProperties.setProperty("marketOpen", "false");
			plugin.mainProperties.saveFile("-BenCmd Main Config-");
			plugin.getServer().broadcastMessage(
					ChatColor.RED + "The market is now closed!");
		} else if (args[0].equalsIgnoreCase("open")) {
			if (!user.hasPerm("bencmd.market.open")) {
				user.sendMessage(ChatColor.RED
						+ "You don't have permission to do that!");
				return;
			}
			if (plugin.mainProperties.getBoolean("marketOpen", true)) {
				user.sendMessage(ChatColor.RED + "The market is already open!");
				return;
			}
			plugin.mainProperties.setProperty("marketOpen", "true");
			plugin.mainProperties.saveFile("-BenCmd Main Config-");
			plugin.getServer().broadcastMessage(
					ChatColor.GREEN + "The market is now open!");
		} else if (args[0].equalsIgnoreCase("multiple")) {
			if (!user.hasPerm("bencmd.market.properties")) {
				user.sendMessage(ChatColor.RED
						+ "You don't have permission to do that!");
				return;
			}
			if (args.length != 2) {
				user.sendMessage(ChatColor.YELLOW
						+ "Proper use is /market multiple <multiple>");
				return;
			}
			Double multiple;
			try {
				multiple = Double.parseDouble(args[1]);
			} catch (NumberFormatException e) {
				user.sendMessage(ChatColor.YELLOW
						+ "Proper use is /market multiple <multiple>");
				return;
			}
			plugin.mainProperties.setProperty("marketMultiple",
					multiple.toString());
			plugin.mainProperties.saveFile("-BenCmd Main Config-");
		} else if (args[0].equalsIgnoreCase("max")) {
			if (!user.hasPerm("bencmd.market.properties")) {
				user.sendMessage(ChatColor.RED
						+ "You don't have permission to do that!");
				return;
			}
			if (args.length != 2) {
				user.sendMessage(ChatColor.YELLOW
						+ "Proper use is /market max <max>");
				return;
			}
			Double max;
			try {
				max = Double.parseDouble(args[1]);
			} catch (NumberFormatException e) {
				user.sendMessage(ChatColor.YELLOW
						+ "Proper use is /market max <max>");
				return;
			}
			plugin.mainProperties
					.setProperty("marketMaxChange", max.toString());
			plugin.mainProperties.saveFile("-BenCmd Main Config-");
		} else if (args[0].equalsIgnoreCase("min")) {
			if (!user.hasPerm("bencmd.market.properties")) {
				user.sendMessage(ChatColor.RED
						+ "You don't have permission to do that!");
				return;
			}
			if (args.length != 2) {
				user.sendMessage(ChatColor.YELLOW
						+ "Proper use is /market min <min>");
				return;
			}
			Double min;
			try {
				min = Double.parseDouble(args[1]);
			} catch (NumberFormatException e) {
				user.sendMessage(ChatColor.YELLOW
						+ "Proper use is /market min <min>");
				return;
			}
			plugin.mainProperties.setProperty("marketMin", min.toString());
			plugin.mainProperties.saveFile("-BenCmd Main Config-");
		}
	}

	public void MarketUpdate(String[] args, User user) {
		if (args.length == 1) {
			plugin.prices.ForceUpdate();
			user.sendMessage(ChatColor.GREEN
					+ "All prices have been recalculated.");
		} else if (args.length == 2) {
			Integer updateTime;
			try {
				updateTime = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				user.sendMessage(ChatColor.YELLOW
						+ "Proper use is /market update [delay]");
				return;
			}
			plugin.mainProperties.setProperty("updateTime",
					updateTime.toString());
			plugin.mainProperties.saveFile("-BenCmd Main Config-");
			plugin.prices.ForceUpdate();
			if (!plugin.prices.isTimerEnabled()) {
				plugin.prices.loadTimer();
				user.sendMessage(ChatColor.GREEN + "Updating enabled!");
			} else {
				user.sendMessage(ChatColor.GREEN + "Updating rate changed!");
			}
		} else {
			user.sendMessage(ChatColor.YELLOW
					+ "Proper use is /market update [delay]");
		}
	}

	public void MarketCurrency(String[] args, User user) {
		if (args.length != 3) {
			user.sendMessage(ChatColor.YELLOW
					+ "Proper use is /market currency <ID>[:Damage] <Value>");
			return;
		}
		BCItem item = new InventoryBackend(plugin).checkAlias(args[1]);
		if (item == null) {
			user.sendMessage(ChatColor.RED + "Invalid item ID or damage!");
			return;
		}
		BuyableItem bitem;
		Double price;
		try {
			price = Double.parseDouble(args[2]);
		} catch (NumberFormatException e) {
			user.sendMessage(ChatColor.RED + "Invalid price");
			return;
		}
		if ((bitem = plugin.prices.getItem(item)) != null) {
			if (bitem instanceof Currency) {
				if (price == 0) {
					plugin.prices.remPrice(bitem);
					plugin.log.info(user.getDisplayName()
							+ " has deleted a currency. (ID: "
							+ bitem.getItemId() + "," + bitem.getDurability()
							+ ")");
					plugin.bLog.info("CURRENCY DELETED: " + bitem.getItemId()
							+ ":" + bitem.getDurability() + " by user "
							+ user.getDisplayName());
					user.sendMessage(ChatColor.GREEN
							+ "That currency was successfully removed!");
				} else {
					bitem.setPrice(price);
					plugin.prices.savePrice(bitem);
					plugin.log.info(user.getDisplayName()
							+ " has updated the price of a currency (ID: "
							+ bitem.getItemId() + "," + bitem.getDurability()
							+ ") to " + price);
					plugin.bLog.info("CURRENCY CHANGED: " + bitem.getItemId()
							+ ":" + bitem.getDurability() + " by user "
							+ user.getDisplayName() + " to price " + price);
					user.sendMessage(ChatColor.GREEN
							+ "That currency was successfully updated!");
				}
			} else {
				if (price == 0) {
					user.sendMessage(ChatColor.RED
							+ "That currency doesn't exist!");
				} else {
					Currency currency = new Currency(bitem.getItemId(),
							bitem.getDurability(), price, -1, 0, plugin.prices);
					plugin.prices.remPrice(bitem);
					plugin.prices.savePrice(currency);
					plugin.log.info(user.getDisplayName()
							+ " has converted an item into a currency (ID: "
							+ bitem.getItemId() + "," + bitem.getDurability()
							+ ") with a price of " + price);
					plugin.bLog.info("ITEM TO CURRENCY: " + bitem.getItemId()
							+ ":" + bitem.getDurability() + " by user "
							+ user.getDisplayName() + " to price " + price);
					user.sendMessage(ChatColor.GREEN
							+ "That item is now a currency!");
				}
			}
		} else {
			Currency currency = new Currency(item.getMaterial().getId(),
					item.getDamage(), price, -1, 0, plugin.prices);
			plugin.prices.savePrice(currency);
			plugin.log.info(user.getDisplayName()
					+ " has created a currency (ID: " + currency.getItemId()
					+ "," + currency.getDurability() + ") with a price of "
					+ price);
			plugin.bLog.info("CURRENCY CREATED: " + currency.getItemId() + ":"
					+ currency.getDurability() + " by user "
					+ user.getDisplayName() + ", with price " + price);
			user.sendMessage(ChatColor.GREEN
					+ "That currency was successfully created!");
		}
	}

	public void MarketItem(String[] args, User user) {
		if (args.length != 3) {
			user.sendMessage(ChatColor.YELLOW
					+ "Proper use is /market item <ID>[:Damage] <Value>");
			return;
		}
		BCItem item = new InventoryBackend(plugin).checkAlias(args[1]);
		if (item == null) {
			user.sendMessage(ChatColor.RED + "Invalid item ID or damage!");
			return;
		}
		BuyableItem bitem;
		Double price;
		try {
			price = Double.parseDouble(args[2]);
		} catch (NumberFormatException e) {
			user.sendMessage(ChatColor.RED + "Invalid price");
			return;
		}
		if ((bitem = plugin.prices.getItem(item)) != null) {
			if (bitem instanceof Currency) {
				if (price == 0) {
					user.sendMessage(ChatColor.RED + "That item doesn't exist!");
				} else {
					BuyableItem newitem = new BuyableItem(bitem.getItemId(),
							bitem.getDurability(), price, 0, 0, plugin.prices);
					plugin.prices.remPrice(bitem);
					plugin.prices.savePrice(newitem);
					plugin.log.info(user.getDisplayName()
							+ " has converted an currency into an item (ID: "
							+ bitem.getItemId() + "," + bitem.getDurability()
							+ ") with a price of " + price);
					plugin.bLog.info("CURRENCY TO ITEM: " + bitem.getItemId()
							+ ":" + bitem.getDurability() + " by user "
							+ user.getDisplayName() + " to price " + price);
					user.sendMessage(ChatColor.GREEN
							+ "That currency is now an item!");
				}
			} else {
				if (price == 0) {
					plugin.prices.remPrice(bitem);
					plugin.log.info(user.getDisplayName()
							+ " has deleted an item. (ID: " + bitem.getItemId()
							+ "," + bitem.getDurability() + ")");
					plugin.bLog.info("ITEM DELETED: " + bitem.getItemId() + ":"
							+ bitem.getDurability() + " by user "
							+ user.getDisplayName());
					user.sendMessage(ChatColor.GREEN
							+ "That item was successfully removed!");
				} else {
					bitem.setPrice(price);
					plugin.prices.savePrice(bitem);
					plugin.log.info(user.getDisplayName()
							+ " has updated the price of an item (ID: "
							+ bitem.getItemId() + "," + bitem.getDurability()
							+ ") to " + price);
					plugin.bLog.info("ITEM CHANGED: " + bitem.getItemId() + ":"
							+ bitem.getDurability() + " by user "
							+ user.getDisplayName() + " to price " + price);
					user.sendMessage(ChatColor.GREEN
							+ "That item was successfully updated!");
				}
			}
		} else {
			BuyableItem newItem = new BuyableItem(item.getMaterial().getId(),
					item.getDamage(), price, 0, 0, plugin.prices);
			plugin.prices.savePrice(newItem);
			plugin.log.info(user.getDisplayName()
					+ " has created a currency (ID: " + newItem.getItemId()
					+ "," + newItem.getDurability() + ") with a price of "
					+ price);
			plugin.bLog.info("ITEM CREATED: " + item.getMaterial().getId()
					+ ":" + item.getDamage() + " by user "
					+ user.getDisplayName() + ", with price " + price);
			user.sendMessage(ChatColor.GREEN
					+ "That item was successfully created!");
		}
	}

	public void MarketSupply(String[] args, User user) {
		if (args.length != 3) {
			user.sendMessage(ChatColor.YELLOW
					+ "Proper use is /market stock <ID>[:Damage] <Stock>");
			return;
		}
		BCItem item = new InventoryBackend(plugin).checkAlias(args[1]);
		if (item == null) {
			user.sendMessage(ChatColor.RED + "Invalid item ID or damage!");
			return;
		}
		BuyableItem bitem;
		Integer supply;
		try {
			supply = Integer.parseInt(args[2]);
		} catch (NumberFormatException e) {
			user.sendMessage(ChatColor.RED + "Invalid price");
			return;
		}
		if ((bitem = plugin.prices.getItem(item)) != null) {
			if (bitem instanceof Currency) {
				user.sendMessage(ChatColor.RED
						+ "Currencies have unlimited supply!");
			} else {
				bitem.setSupply(supply);
				plugin.prices.savePrice(bitem);
				plugin.log.info(user.getDisplayName()
						+ " has changed the supply of an item (ID: "
						+ bitem.getItemId() + "," + bitem.getDurability()
						+ ") with a supply of " + supply);
				plugin.bLog.info("ITEM SUPPLY: " + bitem.getItemId() + ":"
						+ bitem.getDurability() + " by user "
						+ user.getDisplayName() + " to supply " + supply);
				user.sendMessage(ChatColor.GREEN
						+ "That supply count has been updated!");
			}
		} else {
			user.sendMessage(ChatColor.RED
					+ "That item must be created first! Use /market item <ID>[:Damage] <Value> to create it.");
		}
	}

	public void Price(String[] args, User user) {
		if (args.length != 1) {
			user.sendMessage(ChatColor.YELLOW
					+ "Proper use is /price <ID>[:Damage]");
			return;
		}
		BCItem item = new InventoryBackend(plugin).checkAlias(args[0]);
		if (item == null) {
			user.sendMessage(ChatColor.RED + "Invalid item ID or damage!");
			return;
		}
		BuyableItem bitem = plugin.prices.getItem(item);
		if (bitem == null) {
			user.sendMessage(ChatColor.RED + "That item isn't tradeable!");
			return;
		}
		if (bitem instanceof Currency) {
			user.sendMessage(ChatColor.GREEN + "One " + args[0] + " is worth "
					+ bitem.getPrice() + "!");
		} else {
			if (bitem.getSupply() == -1) {
				user.sendMessage(ChatColor.GREEN + "One " + args[0] + " costs "
						+ bitem.getPrice() + "!");
			} else {
				user.sendMessage(ChatColor.GREEN + "One " + args[0] + " costs "
						+ bitem.getPrice() + "! (There are currently "
						+ bitem.getSupply() + " in stock)");
			}
		}
	}
}