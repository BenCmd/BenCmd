package ben_dude56.plugins.bencmd.warps;

import java.util.logging.Logger;

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
	Logger log = Logger.getLogger("minecraft");

	public WarpCommands(BenCmd instance) {
		plugin = instance;
	}

	public boolean onCommand(CommandSender sender, Command command,
			String commandLabel, String[] args) {
		User user;
		try {
			user = new User(plugin, (Player) sender);
		} catch (ClassCastException e) {
			user = new User(plugin);
		}
		if (commandLabel.equalsIgnoreCase("warp") && user.hasPerm("canWarp")) {
			Warp(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("setwarp")
				&& user.hasPerm("canEditWarps")) {
			SetWarp(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("delwarp")
				&& user.hasPerm("canEditWarps")) {
			DelWarp(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("home")
				&& user.hasPerm("canWarpOwnHomes")) {
			Home(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("sethome")
				&& user.hasPerm("canEditOwnHomes")) {
			SetHome(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("delhome")
				&& user.hasPerm("canEditOwnHomes")) {
			DelHome(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("back")
				&& user.hasPerm("canWarp")) {
			Back(user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("jail")
				&& user.hasPerm("canJail")) {
			Jail(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("unjail")
				&& user.hasPerm("canJail")) {
			Unjail(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("setjail")
				&& user.hasPerm("canJail")) {
			SetJail(user);
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
		if (args.length == 2 && user.hasPerm("canWarpOthers")) {
			Warp warp = plugin.warps.getWarp(args[0]);
			if (warp == null) {
				user.sendMessage(ChatColor.RED + "That warp doesn't exist!");
				return;
			}
			User warper;
			try {
				warper = new User(plugin, plugin.getServer()
						.matchPlayer(args[1]).get(0));
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
		if (user.hasPerm("canWarpOthers")) {
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
			log.severe("Couldn't create new warp:");
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
		if (args.length == 0 || args.length > 2) {
			if (user.hasPerm("canWarpOtherHomes")) {
				user.sendMessage(ChatColor.YELLOW
						+ "Proper use is: /home <number> [player]");
			} else {
				user.sendMessage(ChatColor.YELLOW
						+ "Proper use is: /home <number>");
			}
		} else if (args.length == 1) {
			int homenum;
			try {
				homenum = Integer.parseInt(args[0]);
				user.HomeWarp(homenum);
			} catch (NumberFormatException e) {
				user.sendMessage(ChatColor.RED + "Invalid home number!");
			}
		} else if (args.length == 2) {
			if (user.hasPerm("canWarpOtherHomes")) {
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
		if (args.length == 0 || args.length > 2) {
			if (user.hasPerm("canEditOtherHomes")) {
				user.sendMessage(ChatColor.YELLOW
						+ "Proper use is: /sethome <number> [player]");
			} else {
				user.sendMessage(ChatColor.YELLOW
						+ "Proper use is: /sethome <number>");
			}
		} else if (args.length == 1) {
			int homenum;
			try {
				homenum = Integer.parseInt(args[0]);
				user.SetHome(homenum);
			} catch (NumberFormatException e) {
				user.sendMessage(ChatColor.RED + "Invalid home number!");
			}
		} else if (args.length == 2) {
			if (user.hasPerm("canEditOtherHomes")) {
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
		if (args.length == 0 || args.length > 2) {
			if (user.hasPerm("canEditOtherHomes")) {
				user.sendMessage(ChatColor.YELLOW
						+ "Proper use is: /delhome <number> [player]");
			} else {
				user.sendMessage(ChatColor.YELLOW
						+ "Proper use is: /delhome <number>");
			}
		} else if (args.length == 1) {
			if (user.isServer()) {
				user.sendMessage(ChatColor.YELLOW
						+ "Proper use is: /delhome <number> [player]");
				return;
			}
			int homenum;
			try {
				homenum = Integer.parseInt(args[0]);
				if (plugin.homes.DeleteHome(user.getName(), homenum)) {
					user.sendMessage(ChatColor.GREEN + "Your home #"
							+ ((Integer) homenum).toString()
							+ " has been successfully deleted!");
				} else {
					user.sendMessage(ChatColor.RED
							+ "You must set that home first!");
				}
			} catch (NumberFormatException e) {
				user.sendMessage(ChatColor.RED + "Invalid home number!");
			}
		} else if (args.length == 2) {
			if (user.hasPerm("canEditOtherHomes")) {
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

	public void Jail(String[] args, User user) {
		if (args.length == 1) {
			User user2;
			if ((user2 = User.matchUser(args[0], plugin)) == null) {
				user.sendMessage(ChatColor.RED + "That player doesn't exist!");
				return;
			}
			if (user.getName() == user2.getName()) {
				user.sendMessage(ChatColor.RED
						+ "What the hell do you think you're doing!?");
				return;
			}
			user2.toggleJail();
			plugin.getServer().broadcastMessage(
					ChatColor.RED + user2.getName() + " has been jailed!");
		} else {
			user.sendMessage(ChatColor.YELLOW + "Proper use is: /jail <player>");
		}
	}

	public void Unjail(String[] args, User user) {
		if (args.length == 1) {
			User user2;
			if ((user2 = User.matchUser(args[0], plugin)) == null) {
				user.sendMessage(ChatColor.RED + "That player doesn't exist!");
				return;
			}
			user2.toggleJail();
			plugin.getServer().broadcastMessage(
					ChatColor.RED + user2.getName() + " has been unjailed!");
		} else {
			user.sendMessage(ChatColor.YELLOW
					+ "Proper use is: /unjail <player>");
		}
	}

	public void SetJail(User user) {
		if (user.isServer()) {
			user.sendMessage(ChatColor.RED + "The server cannot do that!");
			return;
		}
		plugin.jail.setJail(user.getHandle().getLocation());
	}
}
