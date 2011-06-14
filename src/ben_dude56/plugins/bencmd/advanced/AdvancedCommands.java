package ben_dude56.plugins.bencmd.advanced;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ben_dude56.plugins.bencmd.BenCmd;
import ben_dude56.plugins.bencmd.Commands;
import ben_dude56.plugins.bencmd.User;

public class AdvancedCommands implements Commands {

	private BenCmd plugin;
	
	public AdvancedCommands(BenCmd instance) {
		plugin = instance;
	}
	
	public boolean onCommand(CommandSender sender, Command command,
			String commandLabel, String[] args) {
		User user;
		try {
			user = User.getUser(plugin, (Player) sender);
		} catch (ClassCastException e) {
			user = User.getUser(plugin);
		}
		if(commandLabel.equalsIgnoreCase("write")) {
			Write(args, user);
			return true;
		}
		return false;
	}

	public void Write(String[] args, User user) {
		if(user.getHandle().getTargetBlock(null, 4).getType() != Material.BOOKSHELF) {
			user.sendMessage(ChatColor.RED + "You're not pointing at a bookshelf!");
			return;
		}
		String message = "";
		for (int i = 0; i < args.length; i++) {
			String word = args[i];
			if (message == "") {
				message += word;
			} else {
				message += " " + word;
			}
		}
		plugin.shelff.addShelf(new Shelf(user.getHandle().getTargetBlock(null, 4).getLocation(), message));
		user.sendMessage(ChatColor.GREEN + "Magically, writing appears on that shelf.");
	}
}
