package ben_dude56.plugins.bencmd.advanced.bank;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ben_dude56.plugins.bencmd.BenCmd;
import ben_dude56.plugins.bencmd.Commands;
import ben_dude56.plugins.bencmd.User;

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
				&& user.hasPerm("isBankAdmin")) {
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
			if (!plugin.banks.hasBank(args[0])) {
				user.sendMessage(ChatColor.RED + "That player doesn't have a bank account!");
				return;
			}
			plugin.banks.openInventory(args[0], user.getHandle());
		}
	}

}
