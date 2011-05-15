package ben_dude56.plugins.bencmd.weather;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
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
		if(args.length != 1 && args.length != 2) {
			user.sendMessage(ChatColor.YELLOW + "Proper use is /storm {off|rain|thunder} [world]");
			return;
		}
		World world;
		if(args.length == 2) {
			if((world = plugin.getServer().getWorld(args[1])) == null) {
				user.sendMessage(ChatColor.RED + "World " + args[1] + " wasn't found!");
				return;
			}
		} else {
			if(user.isServer()) {
				world = plugin.getServer().getWorlds().get(0);
			} else {
				world = user.getHandle().getWorld();
			}
		}
		if(args[0].equalsIgnoreCase("off")) {
			world.setStorm(false);
			world.setThundering(false);
		} else if (args[0].equalsIgnoreCase("rain")) {
			world.setStorm(true);
			world.setThundering(false);
		} else if (args[0].equalsIgnoreCase("thunder")) {
			world.setStorm(true);
			world.setThundering(true);
		}
	}
	
	public void Strike(String[] args, User user) {
		if(user.isServer()) {
			user.sendMessage(ChatColor.RED + "The server cannot do that!");
			return;
		}
		Block targetBlock = user.getHandle().getTargetBlock(null, 100);
		Location loc = targetBlock.getLocation();
		user.getHandle().getWorld().strikeLightning(loc);
	}
}
