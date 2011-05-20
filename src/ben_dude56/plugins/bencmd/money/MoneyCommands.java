package ben_dude56.plugins.bencmd.money;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ben_dude56.plugins.bencmd.BenCmd;
import ben_dude56.plugins.bencmd.Commands;
import ben_dude56.plugins.bencmd.User;
import ben_dude56.plugins.bencmd.invtools.BCItem;
import ben_dude56.plugins.bencmd.invtools.InventoryBackend;
import ben_dude56.plugins.bencmd.money.BuyableItem.BuyResult;

public class MoneyCommands implements Commands {

	BenCmd plugin;
	Logger log = Logger.getLogger("minecraft");

	public MoneyCommands(BenCmd instance) {
		plugin = instance;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String commandLabel, String[] args) {
		User user;
		try {
			user = new User(plugin, (Player) sender);
		} catch (ClassCastException e) {
			user = new User(plugin);
		}
		if (commandLabel.equalsIgnoreCase("buy")) {
			Buy(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("sell")) {
			Sell(args, user);
			return true;
		}
		return false;
	}
	
	public void Buy(String[] args, User user) {
		if(args.length == 0 || args.length > 2) {
			user.sendMessage(ChatColor.YELLOW
					+ "Proper use is /buy <ID>[:Damage] [Amount]");
			return;
		}
		BCItem item = new InventoryBackend(plugin).checkAlias(args[0]);
		if(item == null) {
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
		if(bitem == null) {
			user.sendMessage(ChatColor.RED + "That item isn't tradeable!");
			return;
		}
		BuyResult result = bitem.buyItem(user, Amount);
		switch(result) {
		case INS_FUNDS:
			user.sendMessage(ChatColor.RED + "You don't have enough money to buy that! (Requires: " + (bitem.getPrice() * Amount) + ")");
			break;
		case INS_SUPPLY:
			user.sendMessage(ChatColor.RED + "There isn't enough supply! (Current supply: " + bitem.getSupply() + ")");
			break;
		case SUCCESS:
			user.sendMessage(ChatColor.GREEN + "Enjoy, " + user.getName() + "!");
			log.info(user.getName()
					+ " bought an item. (id: " + item.getMaterial().getId()
					+ ", amount: " + Amount + ", damage: "
					+ item.getDamage() + ")");
			break;
		}
	}

	public void Sell(String[] args, User user) {
		if(args.length == 0 || args.length > 2) {
			user.sendMessage(ChatColor.YELLOW
					+ "Proper use is /sell <ID>[:Damage] [Amount]");
			return;
		}
		BCItem item = new InventoryBackend(plugin).checkAlias(args[0]);
		if(item == null) {
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
		if(bitem == null) {
			user.sendMessage(ChatColor.RED + "That item isn't tradeable!");
			return;
		}
		if(bitem.sellItem(user, Amount)) {
			user.sendMessage(ChatColor.GREEN + "Enjoy, " + user.getName() + "!");
			plugin.log.info(user.getName() + " has sold " + Amount + " of item. (ID: " + bitem.getMaterial().getId() + ")");
		} else {
			user.sendMessage(ChatColor.RED + "Stop trying to sell stuff you don't have.");
		}
	}
}
