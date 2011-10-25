package com.bendude56.bencmd.weather;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.Commands;
import com.bendude56.bencmd.User;


public class WeatherCommands implements Commands {

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String commandLabel, String[] args) {
		User user;
		try {
			user = User.getUser((Player) sender);
		} catch (ClassCastException e) {
			user = User.getUser();
		}
		if (commandLabel.equalsIgnoreCase("storm")
				&& user.hasPerm("bencmd.storm.control")) {
			Storm(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("strike")
				&& user.hasPerm("bencmd.strike.location")) {
			Strike(args, user);
			return true;
		}
		return false;
	}

	public void Storm(String[] args, User user) {
		if (args.length != 1 && args.length != 2) {
			user.sendMessage(ChatColor.YELLOW
					+ "Proper use is /storm {off|rain|thunder} [world]");
			return;
		}
		World world;
		if (args.length == 2) {
			if ((world = Bukkit.getWorld(args[1])) == null) {
				user.sendMessage(ChatColor.RED + "World " + args[1]
						+ " wasn't found!");
				return;
			}
		} else {
			if (user.isServer()) {
				world = Bukkit.getWorlds().get(0);
			} else {
				world = ((Player) user.getHandle()).getWorld();
			}
		}
		if (args[0].equalsIgnoreCase("off")) {
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
		if (args.length > 1) {
			user.sendMessage(ChatColor.YELLOW
					+ "Proper use is /strike {bind|unbind|[Player]}");
		}
		Block targetBlock = ((Player) user.getHandle()).getTargetBlock(null, 100);
		Location loc = null;
		if (args.length == 0) {
			if (user.isServer()) {
				user.sendMessage(ChatColor.RED + "The server cannot do that!");
				return;
			}
			loc = targetBlock.getLocation();
		} else {
			if (args[0].equalsIgnoreCase("bind")) {
				if (!user.hasPerm("bencmd.storm.strike.bind")) {
					user.sendMessage(ChatColor.RED
							+ "You don't have permission to do that!");
					BenCmd.getPlugin().logPermFail();
					return;
				}
				if (user.isServer()) {
					user.sendMessage(ChatColor.RED
							+ "The server cannot do that!");
					return;
				}
				if (BenCmd.getStrikeBindings().tryBind(((Player) user.getHandle()))) {
					user.sendMessage(ChatColor.GREEN
							+ "That item has now been bound to strike lightning!");
				} else {
					user.sendMessage(ChatColor.RED
							+ "You can't bind that item!");
				}
				return;
			} else if (args[0].equalsIgnoreCase("unbind")) {
				if (!user.hasPerm("bencmd.storm.strike.bind")) {
					user.sendMessage(ChatColor.RED
							+ "You don't have permission to do that!");
					BenCmd.getPlugin().logPermFail();
					return;
				}
				if (user.isServer()) {
					user.sendMessage(ChatColor.RED
							+ "The server cannot do that!");
					return;
				}
				if (BenCmd.getStrikeBindings().hasBoundItem(((Player) user.getHandle()))) {
					BenCmd.getStrikeBindings().clearBinding(((Player) user.getHandle()));
					user.sendMessage(ChatColor.GREEN
							+ "You no longer have an item bound to strike lightning.");
				} else {
					user.sendMessage(ChatColor.RED
							+ "You don't have an item bound!");
				}
				return;
			} else {
				if (!user.hasPerm("bencmd.storm.strike.player")) {
					user.sendMessage(ChatColor.RED
							+ "You don't have permission to do that!");
					BenCmd.getPlugin().logPermFail();
					return;
				}
				User user2;
				if ((user2 = User.matchUser(args[0])) == null) {
					user.sendMessage(ChatColor.RED
							+ "That user couldn't be found!");
					return;
				}
				loc = ((Player) user2.getHandle()).getLocation();
			}
		}
		((Player) user.getHandle()).getWorld().strikeLightning(loc);
	}
}
