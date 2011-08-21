package ben_dude56.plugins.bencmd.warps;

import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ben_dude56.plugins.bencmd.BenCmd;
import ben_dude56.plugins.bencmd.Commands;
import ben_dude56.plugins.bencmd.User;
import ben_dude56.plugins.bencmd.permissions.PermissionUser;

public class WarpCommands implements Commands {
	BenCmd plugin;

	public WarpCommands(BenCmd instance) {
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
		if (commandLabel.equalsIgnoreCase("warp") && user.hasPerm("bencmd.warp.self")) {
			Warp(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("setwarp")
				&& user.hasPerm("bencmd.warp.set")) {
			SetWarp(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("delwarp")
				&& user.hasPerm("bencmd.warp.remove")) {
			DelWarp(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("home")
				&& user.hasPerm("bencmd.home.self")) {
			Home(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("sethome")
				&& user.hasPerm("bencmd.home.set")) {
			SetHome(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("delhome")
				&& user.hasPerm("bencmd.home.remove")) {
			DelHome(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("back")
				&& user.hasPerm("bencmd.warp.back")) {
			Back(user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("setjail")
				&& user.hasPerm("bencmd.action.setjail")) {
			SetJail(user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("tp")) {
			Tp(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("tphere")
				&& user.hasPerm("bencmd.tp.other")) {
			if (args.length != 1) {
				user.sendMessage(ChatColor.YELLOW
						+ "Proper use is: /tphere <player>");
			} else if (user.isServer()) {
				user.sendMessage(ChatColor.RED + "The server cannot do that!");
			} else {
				plugin.getServer().dispatchCommand(user.getHandle(),
						"tp " + args[0] + " " + user.getName());
			}
			return true;
		}
		return false;
	}

	public void Warp(String[] args, User user) {
		if (args.length == 0 || args[0].equalsIgnoreCase("list")) {
			String str = "";
			for (Warp warp : user.ListWarps()) {
				str += warp.warpName + ", ";
			}
			if (str.length() != 0) {
				str = str.substring(0, str.length() - 2);
				user.sendMessage(ChatColor.GREEN + "Available warps: " + str);
				return;
			} else {
				user.sendMessage(ChatColor.RED + "You have no available warps.");
				return;
			}
		}
		if (args.length == 1) {
			if (user.isServer()) {
				user.sendMessage(ChatColor.RED + "The server cannot do that!");
				return;
			}
			Warp warp = plugin.warps.getWarp(args[0]);
			if (warp == null) {
				user.sendMessage(ChatColor.RED + "That warp doesn't exist!");
				return;
			}
			user.WarpTo(warp);
			return;
		}
		if (args.length == 2 && user.hasPerm("bencmd.warp.other")) {
			Warp warp = plugin.warps.getWarp(args[0]);
			if (warp == null) {
				user.sendMessage(ChatColor.RED + "That warp doesn't exist!");
				return;
			}
			User warper;
			try {
				warper = User.getUser(plugin,
						plugin.getServer().matchPlayer(args[1]).get(0));
			} catch (NullPointerException e) {
				user.sendMessage(ChatColor.RED + "That player doesn't exist!");
				return;
			} catch (IndexOutOfBoundsException e) {
				user.sendMessage(ChatColor.RED + "That player doesn't exist!");
				return;
			}
			warper.WarpTo(warp, user);
			return;
		}
		if (user.hasPerm("bencmd.warp.other")) {
			user.sendMessage(ChatColor.YELLOW
					+ "Proper use is: /warp <name> [player]");
		}
	}

	public void SetWarp(String[] args, User user) {
		if (user.isServer()) {
			user.sendMessage(ChatColor.RED + "The server cannot do that!");
			return;
		}
		if (args.length == 0 || args.length > 2) {
			user.sendMessage(ChatColor.YELLOW
					+ "Proper use is: /setwarp <name> [group]");
			return;
		}
		if (plugin.warps.getWarp(args[0]) != null) {
			user.sendMessage(ChatColor.RED + "That warp already exists!");
			return;
		}
		try {
			Player player = user.getHandle();
			double x = player.getLocation().getX();
			double y = player.getLocation().getY();
			double z = player.getLocation().getZ();
			double yaw = (int) player.getLocation().getYaw();
			double pitch = (int) player.getLocation().getPitch();
			String world = player.getWorld().getName();
			String group = "";
			if (args.length == 2) {
				group = args[1];
				if (!plugin.perm.groupFile.groupExists(group)) {
					player.sendMessage(ChatColor.RED
							+ "That group doesn't exist!");
				}
			}
			if (!plugin.warps.addWarp(x, y, z, yaw, pitch, world, args[0],
					group)) {
				user.sendMessage(ChatColor.RED
						+ "There was a problem creating the warp!");
				return;
			}
		} catch (Exception e) {
			user.sendMessage(ChatColor.RED
					+ "There was a problem creating the warp!");
			plugin.bLog.log(Level.SEVERE, "Couldn't create new warp:", e);
			plugin.log.severe("Couldn't create new warp:");
			e.printStackTrace();
			return;
		}
		user.sendMessage(ChatColor.GREEN + "Warp successfully created!");
	}

	public void DelWarp(String[] args, User user) {
		if (args.length != 1) {
			user.sendMessage(ChatColor.YELLOW
					+ "Proper use is: /delwarp <name>");
			return;
		}
		if (!plugin.warps.removeWarp(args[0])) {
			user.sendMessage(ChatColor.RED
					+ "There was a problem deleting the warp!");
		} else {
			user.sendMessage(ChatColor.GREEN + "Warp successfully deleted!");
		}
	}

	public void Back(User user) {
		if (user.isServer()) {
			user.sendMessage(ChatColor.RED + "The server cannot do that!");
			return;
		}
		if (!user.LastCheck()) {
			user.sendMessage(ChatColor.RED
					+ "There are no known pre-warp checkpoints for you!");
		}
	}

	public void Home(String[] args, User user) {
		if (user.isServer()) {
			user.sendMessage(ChatColor.RED + "The server cannot do that!");
			return;
		}
		if (args.length > 2) {
			if (user.hasPerm("bencmd.home.warpall")) {
				user.sendMessage(ChatColor.YELLOW
						+ "Proper use is: /home <number> [player]");
			} else {
				user.sendMessage(ChatColor.YELLOW
						+ "Proper use is: /home <number>");
			}
		} else if (args.length <= 1) {
			int homenum;
			if (args.length == 0) {
				user.HomeWarp(1);
			} else {
				try {
					homenum = Integer.parseInt(args[0]);
					user.HomeWarp(homenum);
				} catch (NumberFormatException e) {
					user.sendMessage(ChatColor.RED + "Invalid home number!");
					return;
				}
			}
		} else if (args.length == 2) {
			if (user.hasPerm("bencmd.home.warpall")) {
				int homenum;
				try {
					homenum = Integer.parseInt(args[0]);
					user.HomeWarp(homenum,
							PermissionUser.matchUserIgnoreCase(args[1], plugin));
				} catch (NumberFormatException e) {
					user.sendMessage(ChatColor.RED + "Invalid home number!");
				}
			} else {
				user.sendMessage(ChatColor.YELLOW
						+ "Proper use is: /home <number>");
			}
		}
	}

	public void SetHome(String[] args, User user) {
		if (user.isServer()) {
			user.sendMessage(ChatColor.RED + "The server cannot do that!");
			return;
		}
		if (args.length > 2) {
			if (user.hasPerm("bencmd.home.setall")) {
				user.sendMessage(ChatColor.YELLOW
						+ "Proper use is: /sethome <number> [player]");
			} else {
				user.sendMessage(ChatColor.YELLOW
						+ "Proper use is: /sethome <number>");
			}
		} else if (args.length <= 1) {
			int homenum;
			if (args.length == 0) {
				user.SetHome(1);
			} else {
				try {
					homenum = Integer.parseInt(args[0]);
					user.SetHome(homenum);
				} catch (NumberFormatException e) {
					user.sendMessage(ChatColor.RED + "Invalid home number!");
				}
			}
		} else if (args.length == 2) {
			if (user.hasPerm("bencmd.home.setall")) {
				int homenum;
				try {
					homenum = Integer.parseInt(args[0]);
					user.SetHome(homenum,
							PermissionUser.matchUserIgnoreCase(args[1], plugin));
				} catch (NumberFormatException e) {
					user.sendMessage(ChatColor.RED + "Invalid home number!");
				}
			} else {
				user.sendMessage(ChatColor.YELLOW
						+ "Proper use is: /sethome <number>");
			}
		}
	}

	public void DelHome(String[] args, User user) {
		if (args.length > 2) {
			if (user.hasPerm("bencmd.home.removeall")) {
				user.sendMessage(ChatColor.YELLOW
						+ "Proper use is: /delhome <number> [player]");
			} else {
				user.sendMessage(ChatColor.YELLOW
						+ "Proper use is: /delhome <number>");
			}
		} else if (args.length <= 1) {
			if (user.isServer()) {
				user.sendMessage(ChatColor.YELLOW
						+ "Proper use is: /delhome <number> [player]");
			}
			int homenum;
			if (args.length == 0) {
				if (plugin.homes.DeleteHome(user.getName(), 1)) {
					user.sendMessage(ChatColor.GREEN + "Your home #"
							+ ((Integer) 1).toString()
							+ " has been successfully deleted!");
				} else {
					user.sendMessage(ChatColor.RED
							+ "You must set that home first!");
				}
			} else {
				try {
					homenum = Integer.parseInt(args[0]);
					if (plugin.homes.DeleteHome(user.getName(), homenum)) {
						user.sendMessage(ChatColor.GREEN + "Your home #"
								+ ((Integer) homenum).toString()
								+ " has been successfully deleted!");
						return;
					} else {
						user.sendMessage(ChatColor.RED
								+ "You must set that home first!");
						return;
					}
				} catch (NumberFormatException e) {
					user.sendMessage(ChatColor.RED + "Invalid home number!");
				}
			}
		} else if (args.length == 2) {
			if (user.hasPerm("bencmd.home.removeall")) {
				int homenum;
				try {
					homenum = Integer.parseInt(args[0]);
					if (plugin.homes.DeleteHome(args[1], homenum)) {
						user.sendMessage(ChatColor.GREEN + args[1]
								+ "'s home #" + ((Integer) homenum).toString()
								+ " has been successfully deleted!");
					} else {
						user.sendMessage(ChatColor.RED + args[1]
								+ " doesn't have a home #"
								+ ((Integer) homenum).toString() + "!");
					}
				} catch (NumberFormatException e) {
					user.sendMessage(ChatColor.RED + "Invalid home number!");
				}
			} else {
				user.sendMessage(ChatColor.YELLOW
						+ "Proper use is: /delhome <number>");
			}
		}
	}

	public void SetJail(User user) {
		if (user.isServer()) {
			user.sendMessage(ChatColor.RED + "The server cannot do that!");
			return;
		}
		plugin.jail.setJail(user.getHandle().getLocation());
	}

	public void Tp(String[] args, User user) {
		if (args.length == 1) {
			if (!user.hasPerm("bencmd.tp.self")) {
				user.sendMessage(ChatColor.RED
						+ "You don't have permission to do that!");
				return;
			}
			User user2 = User.matchUser(args[0], plugin);
			if (user2 == null) {
				user.sendMessage(ChatColor.RED + args[0] + " isn't online!");
				return;
			}
			if (user2.isAllPoofed()) {
				user.sendMessage(ChatColor.RED + args[0]
						+ " cannot be teleported to!");
				return;
			}
			plugin.checkpoints.SetPreWarp(user.getHandle());
			user.getHandle().teleport(user2.getHandle());
			plugin.log.info(user.getName() + " has teleported to "
					+ user2.getName());
			plugin.bLog.info(user.getName() + " has teleported to "
					+ user2.getName());
			user.sendMessage(ChatColor.YELLOW + "Woosh!");
		} else if (args.length == 2) {
			if (args[0].equalsIgnoreCase(user.getName())) {
				plugin.getServer().dispatchCommand(user.getHandle(),
						"tp " + args[1]);
				return;
			}
			if (!user.hasPerm("bencmd.tp.other")) {
				user.sendMessage(ChatColor.RED
						+ "You don't have permission to do that!");
				return;
			}
			User user1 = User.matchUser(args[0], plugin);
			User user2 = User.matchUser(args[1], plugin);
			if (user1 == null) {
				user.sendMessage(ChatColor.RED + args[0] + " isn't online!");
				return;
			} else if (user2 == null) {
				user.sendMessage(ChatColor.RED + args[1] + " isn't online!");
				return;
			}
			if (user1.isAllPoofed()
					&& !user1.getName().equalsIgnoreCase(user.getName())) {
				user.sendMessage(ChatColor.RED + args[0]
						+ " cannot be teleported!");
			}
			if (user2.isAllPoofed()
					&& !user2.getName().equalsIgnoreCase(user.getName())) {
				user.sendMessage(ChatColor.RED + args[0]
						+ " cannot be teleported to!");
				return;
			}
			plugin.checkpoints.SetPreWarp(user1.getHandle());
			user1.getHandle().teleport(user2.getHandle());
			plugin.log.info(user1.getName() + " has been teleported to "
					+ user2.getName() + " by " + user.getName());
			plugin.bLog.info(user1.getName() + " has been teleported to "
					+ user2.getName() + " by " + user.getName());
			user1.sendMessage(ChatColor.YELLOW + "Woosh!");
		} else {
			if (user.hasPerm("bencmd.tp.self") || user.hasPerm("bencmd.tp.other")) {
				user.sendMessage(ChatColor.YELLOW
						+ "Proper use is: /tp <player> [player]");
			} else {
				user.sendMessage(ChatColor.RED
						+ "You don't have permission to do that!");
			}
		}
	}
}
