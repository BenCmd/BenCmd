package ben_dude56.plugins.bencmd.weather;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ben_dude56.plugins.bencmd.BenCmd;
import ben_dude56.plugins.bencmd.Commands;
import ben_dude56.plugins.bencmd.User;

public class WeatherCommands implements Commands {
	
	BenCmd plugin;
	
	public WeatherCommands(BenCmd instance) {
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
		if (commandLabel.equalsIgnoreCase("storm")
			&& user.hasPerm("canControlWeather")) {
			Storm(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("strike")
			&& user.hasPerm("canControlWeather")) {
			Strike(args, user);
			return true;
		}
		return false;
	}
	
	public void Storm(String[] args, User user) {
		if(args.length != 1) {
			user.sendMessage(ChatColor.YELLOW + "Proper use is /storm {off|rain|thunder}");
			return;
		}
		if(args[0].equalsIgnoreCase("off")) {
			user.getHandle().getWorld().setStorm(false);
			user.getHandle().getWorld().setThundering(false);
		} else if (args[0].equalsIgnoreCase("rain")) {
			user.getHandle().getWorld().setStorm(true);
			user.getHandle().getWorld().setThundering(false);
		} else if (args[0].equalsIgnoreCase("thunder")) {
			user.getHandle().getWorld().setStorm(true);
			user.getHandle().getWorld().setThundering(true);
		}
	}
	
	public void Strike(String[] args, User user) {
		Block targetBlock = user.getHandle().getTargetBlock(null, 100);
		Location loc = targetBlock.getLocation();
		user.getHandle().getWorld().strikeLightning(loc);
	}
}
