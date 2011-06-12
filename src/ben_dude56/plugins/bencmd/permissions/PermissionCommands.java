package ben_dude56.plugins.bencmd.permissions;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ben_dude56.plugins.bencmd.BenCmd;
import ben_dude56.plugins.bencmd.Commands;
import ben_dude56.plugins.bencmd.User;
import ben_dude56.plugins.bencmd.reporting.Report;
import ben_dude56.plugins.bencmd.reporting.Report.ReportStatus;

public class PermissionCommands implements Commands {
	BenCmd plugin;
	Logger log = Logger.getLogger("minecraft");

	public PermissionCommands(BenCmd instance) {
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
		if (commandLabel.equalsIgnoreCase("user")
				&& user.hasPerm("canChangePerm")) {
			User(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("group")
				&& user.hasPerm("canChangePerm")) {
			Group(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("status")) {
			Status(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("kick")
				&& user.hasPerm("canKick")) {
			Kick(args, user);
			return true;
		}
		return false;
	}

	public void User(String[] args, User user) {
		if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
			user.sendMessage(ChatColor.YELLOW
					+ "Proper use is: /user <name> {add|remove|g:<group>|c:<color>|<permissions>}");
			user.sendMessage(ChatColor.YELLOW
					+ "   -<name> is the name of the user.");
			user.sendMessage(ChatColor.YELLOW
					+ "   -Use add to add a user, and remove to delete one.");
			user.sendMessage(ChatColor.YELLOW
					+ "   -Use g:<group> to change the group that a user belongs to.");
			user.sendMessage(ChatColor.YELLOW
					+ "   -Use c:<color> to change the color that the player's name shows up as.");
			user.sendMessage(ChatColor.YELLOW
					+ "   -Otherwise, type +<permission> or -<permission> to add/remove permissions.");
			return;
		}
		switch (args.length) {
		case 1:
			user.sendMessage(ChatColor.YELLOW
					+ "Proper use is: /user <name> {add|remove|g:<group>|<permissions>}");
			user.sendMessage(ChatColor.YELLOW
					+ "Type /perm help for additionnal information.");
			break;
		case 2:
			if (args[1].equalsIgnoreCase("remove")) {
				if (!plugin.perm.userFile.userExists(args[0])) {
					user.sendMessage(ChatColor.RED + "User doesn't exist!");
				} else {
					for (Player p : plugin.getServer().getOnlinePlayers()) {
						if (p.getName().equalsIgnoreCase(args[0])) {
							user.sendMessage(ChatColor.RED
									+ "You cannot remove a player that is currently logged in!");
							return;
						}
					}
					if (plugin.perm.userFile.removeUser(args[0])) {
						user.sendMessage(ChatColor.GREEN
								+ "User successfully removed!");
						log.info("User " + args[0]
								+ " has been removed from the database.");
					} else {
						user.sendMessage(ChatColor.RED
								+ "An unknown error occured while removing this user!");
					}
				}
				break;
			} else if (args[1].equalsIgnoreCase("add")) {
				if (!plugin.perm.userFile.userExists(args[0])) {
					if (plugin.perm.userFile.addUser(args[0])) {
						user.sendMessage(ChatColor.GREEN
								+ "User successfully added!");
						log.info("User " + args[0]
								+ " has been added to the database.");
					} else {
						user.sendMessage(ChatColor.RED
								+ "An unknown error occured while adding this user!");
					}
				} else {
					user.sendMessage(ChatColor.RED
							+ "That user is already in the database!");
				}
				break;
			} else if (args[1].startsWith("g:")) {
				PermissionUser user2;
				if ((user2 = PermissionUser
						.matchUserIgnoreCase(args[0], plugin)) == null) {
					user.sendMessage(ChatColor.RED
							+ "That player doesn't exist!");
					return;
				}
				switch (user2.changeGroup(args[1].replaceFirst("g:", ""))) {
				case AlreadyInGroup:
					user.sendMessage(ChatColor.RED
							+ "That user is already a child of "
							+ args[1].replaceFirst("g:", "") + "!");
					break;
				case DBTargetNotExist:
					user.sendMessage(ChatColor.RED
							+ "The user you tried to change is not present in the database!");
					break;
				case DBGroupNotExist:
					user.sendMessage(ChatColor.RED
							+ "The group you tried to inherit from is not present in the database!");
					break;
				case MalformedPermissions:
					user.sendMessage(ChatColor.RED
							+ "The user permissions file is broken! Please contact your server admin!");
					log.warning("User permissions file malformed!");
					break;
				case Success:
					user.sendMessage(ChatColor.GREEN
							+ "The operation completed successfully!");
					break;
				}
				break;
			} else if (args[1].startsWith("p:")) {
				user.sendMessage(ChatColor.RED
						+ "You can only do that for groups!");
				return;
			} else if (args[1].startsWith("c:")) {
				args[1] = args[1].replaceFirst("c:", "");
				int colorid;
				try {
					colorid = Integer.parseInt(args[1]);
				} catch (NumberFormatException e) {
					try {
						colorid = Integer.parseInt(args[1], 16);
					} catch (NumberFormatException e2) {
						user.sendMessage(ChatColor.RED + "Invalid color ID!");
						return;
					}
				}
				PermissionUser user2;
				if ((user2 = PermissionUser
						.matchUserIgnoreCase(args[0], plugin)) == null) {
					user.sendMessage(ChatColor.RED
							+ "The user you tried to change is not present in the database!");
					return;
				}
				user2.setColor(ChatColor.getByCode(colorid));
				user.sendMessage(ChatColor.GREEN
						+ "The operation completed successfully!");
				return;
			} else {
				if (args[1].startsWith("+")) {
					// Fall-through
				} else if (args[1].startsWith("-")) {
					// Fall-through
				}
			}
		default:
			boolean commandsReady = false;
			for (String str : args) {
				if (!commandsReady) {
					commandsReady = true;
					continue;
				}
				if (str.startsWith("+")) {
					str = str.replaceFirst("\\+", "");
					PermissionUser user2;
					if ((user2 = PermissionUser.matchUserIgnoreCase(args[0],
							plugin)) == null) {
						user.sendMessage(ChatColor.RED
								+ "That player doesn't exist!");
						return;
					}
					switch (user2.addPermission(str)) {
					case DBTargetNotExist:
						user.sendMessage(ChatColor.RED
								+ "That user doesn't exist!");
						break;
					case DBAlreadyHas:
						user.sendMessage(ChatColor.RED
								+ "That user can already do that!");
						break;
					case MalformedPermissions:
						user.sendMessage(ChatColor.RED
								+ "The user permissions file is broken! Please contact your server admin!");
						log.warning("User permissions file malformed!");
						break;
					case Success:
						user.sendMessage(ChatColor.GREEN
								+ "The operation completed successfully!");
						log.info("User " + args[0] + " has been given the "
								+ str + " permission.");
						break;
					}
				} else if (str.startsWith("-")) {
					str = str.replaceFirst("-", "");
					PermissionUser user2;
					if ((user2 = PermissionUser.matchUserIgnoreCase(args[0],
							plugin)) == null) {
						user.sendMessage(ChatColor.RED
								+ "That player doesn't exist!");
						return;
					}
					switch (user2.deletePermission(str)) {
					case DBTargetNotExist:
						user.sendMessage(ChatColor.RED
								+ "That user doesn't exist!");
						break;
					case DBNotHave:
						user.sendMessage(ChatColor.RED
								+ "That user can't do that!");
						break;
					case MalformedPermissions:
						user.sendMessage(ChatColor.RED
								+ "The user permissions file is broken! Please contact your server admin!");
						log.warning("User permissions file malformed!");
						break;
					case Success:
						user.sendMessage(ChatColor.GREEN
								+ "The operation completed successfully!");
						log.info("User " + args[0] + " has lost the " + str
								+ " permission.");
						break;
					}
				}
			}
		}
	}

	public void Group(String[] args, User user) {
		if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
			user.sendMessage(ChatColor.YELLOW
					+ "Proper use is: /group <name> {add|remove|g:<group>|c:<color>|p:<prefix>|<permissions>}");
			user.sendMessage(ChatColor.YELLOW
					+ "   -<name> is the name of the group.");
			user.sendMessage(ChatColor.YELLOW
					+ "   -Use add to add a group, and remove to delete one.");
			user.sendMessage(ChatColor.YELLOW
					+ "   -Use g:<group> to change the group that a group belongs to.");
			user.sendMessage(ChatColor.YELLOW
					+ "   -Use c:<color> to change the color that the group's prefix shows up as.");
			user.sendMessage(ChatColor.YELLOW
					+ "   -Use p:<prefix> to change the prefix of the group.");
			user.sendMessage(ChatColor.YELLOW
					+ "   -Otherwise, type +<permission> or -<permission> to add/remove permissions.");
			return;
		}
		switch (args.length) {
		case 1:
			user.sendMessage(ChatColor.YELLOW
					+ "Proper use is: /group <name> {add|remove|g:<group>|<permissions>}");
			user.sendMessage(ChatColor.YELLOW
					+ "Type /group help for additionnal information.");
			break;
		case 2:
			if (args[1].equalsIgnoreCase("remove")) {
				if (!plugin.perm.groupFile.groupExists(args[0])) {
					user.sendMessage(ChatColor.RED + "Group doesn't exist!");
				} else {
					if (plugin.perm.groupFile.removeGroup(args[0])) {
						user.sendMessage(ChatColor.GREEN
								+ "Group successfully removed!");
						log.info("Group " + args[0]
								+ " has been removed from the database.");
					} else {
						user.sendMessage(ChatColor.RED
								+ "An unknown error occured while removing this group!");
					}
				}
				break;
			} else if (args[1].equalsIgnoreCase("add")) {
				if (!plugin.perm.groupFile.groupExists(args[0])) {
					if (plugin.perm.groupFile.addGroup(args[0])) {
						user.sendMessage(ChatColor.GREEN
								+ "Group successfully added!");
						log.info("Group " + args[0]
								+ " has been added to the database.");
					} else {
						user.sendMessage(ChatColor.RED
								+ "An unknown error occured while adding this user!");
					}
				} else {
					user.sendMessage(ChatColor.RED
							+ "That group is already in the database!");
				}
				break;
			} else if (args[1].startsWith("g:")) {
				switch (plugin.perm.groupFile.changeGroup(args[0],
						args[1].replaceFirst("g:", ""))) {
				case AlreadyInGroup:
					user.sendMessage(ChatColor.RED
							+ "That group is already a child of "
							+ args[1].replaceFirst("g:", "") + "!");
					break;
				case DBTargetNotExist:
					user.sendMessage(ChatColor.RED
							+ "The group you tried to change is not present in the database!");
					break;
				case DBGroupNotExist:
					user.sendMessage(ChatColor.RED
							+ "The group you tried to inherit from is not present in the database!");
					break;
				case MalformedPermissions:
					user.sendMessage(ChatColor.RED
							+ "The group permissions file is broken! Please contact your server admin!");
					log.warning("Group permissions file malformed!");
					break;
				case Success:
					user.sendMessage(ChatColor.GREEN
							+ "The operation completed successfully!");
					log.info("Group " + args[0] + " has been moved into group "
							+ args[1] + ".");
					break;
				}
				break;
			} else if (args[1].startsWith("p:")) {
				args[1] = args[1].replaceFirst("p:", "");
				PermissionGroup group;
				try {
					group = new PermissionGroup(plugin, args[0]);
				} catch (NullPointerException e) {
					user.sendMessage(ChatColor.RED
							+ "The group you tried to change is not present in the database!");
					return;
				}
				group.setPrefix(args[1].replace('_', ' '));
				user.sendMessage(ChatColor.GREEN
						+ "The operation completed successfully!");
			} else if (args[1].startsWith("c:")) {
				args[1] = args[1].replaceFirst("c:", "");
				PermissionGroup group;
				try {
					group = new PermissionGroup(plugin, args[0]);
				} catch (NullPointerException e) {
					user.sendMessage(ChatColor.RED
							+ "The group you tried to change is not present in the database!");
					return;
				}
				int colorid;
				try {
					colorid = Integer.parseInt(args[1]);
				} catch (NumberFormatException e) {
					try {
						colorid = Integer.parseInt(args[1], 16);
					} catch (NumberFormatException e2) {
						user.sendMessage(ChatColor.RED + "Invalid color ID!");
						return;
					}
				}
				group.setPrefixColor(ChatColor.getByCode(colorid));
				user.sendMessage(ChatColor.GREEN
						+ "The operation completed successfully!");
			} else {
				if (args[1].startsWith("+")) {
					// Fall-through
				} else if (args[1].startsWith("-")) {
					// Fall-through
				}
			}
		default:
			boolean commandsReady = false;
			for (String str : args) {
				if (!commandsReady) {
					commandsReady = true;
					continue;
				}
				if (str.startsWith("+")) {
					str = str.replaceFirst("\\+", "");
					switch (plugin.perm.groupFile.addPermission(args[0], str)) {
					case DBTargetNotExist:
						user.sendMessage(ChatColor.RED
								+ "That group doesn't exist!");
						break;
					case DBAlreadyHas:
						user.sendMessage(ChatColor.RED
								+ "That group can already do that!");
						break;
					case MalformedPermissions:
						user.sendMessage(ChatColor.RED
								+ "The group permissions file is broken! Please contact your server admin!");
						log.warning("Group permissions file malformed!");
						break;
					case Success:
						user.sendMessage(ChatColor.GREEN
								+ "The operation completed successfully!");
						log.info("Group " + args[0] + " has been given the "
								+ str + " permission.");
						break;
					}
				} else if (str.startsWith("-")) {
					str = str.replaceFirst("-", "");
					switch (plugin.perm.groupFile
							.removePermission(args[0], str)) {
					case DBTargetNotExist:
						user.sendMessage(ChatColor.RED
								+ "That group doesn't exist!");
						break;
					case DBNotHave:
						user.sendMessage(ChatColor.RED
								+ "That group can't do that!");
						break;
					case MalformedPermissions:
						user.sendMessage(ChatColor.RED
								+ "The group permissions file is broken! Please contact your server admin!");
						log.warning("Group permissions file malformed!");
						break;
					case Success:
						user.sendMessage(ChatColor.GREEN
								+ "The operation completed successfully!");
						log.info("Group " + args[0] + " has lost the " + str
								+ " permission.");
						break;
					}
				}
			}
		}
	}

	public void Status(String[] args, User user) {
		PermissionUser puser2 = null;
		User user2 = null;
		if (args.length == 1 && !user.hasPerm("canCheckOtherStatus")) {
			user.sendMessage(ChatColor.RED
					+ "You don't have enough permissions to check the status of others!");
			return;
		} else if (args.length == 1) {
			if ((puser2 = PermissionUser.matchUserIgnoreCase(args[0], plugin)) == null) {
				user.sendMessage(ChatColor.RED
						+ "That user isn't in the database!");
				return;
			}
			user2 = User.matchUser(args[0], plugin);
		} else if (args.length == 0) {
			puser2 = user;
			user2 = user;
		} else {
			user.sendMessage(ChatColor.YELLOW
					+ "Proper use is: /status [player]");
			return;
		}
		boolean jailed = puser2.hasPerm("isJailed", false);
		boolean muted = puser2.hasPerm("isMuted", false);
		boolean reported = false;
		for (Report ticket : plugin.reports.getReports()) {
			if (ticket.getAccused().getName()
					.equalsIgnoreCase(puser2.getName())
					&& ticket.getStatus() != ReportStatus.CLOSED
					&& ticket.getStatus() != ReportStatus.LOCKED) {
				reported = true;
				break;
			}
		}
		user.sendMessage(ChatColor.GRAY + "Status of " + puser2.getName() + ":");
		if (jailed) {
			user.sendMessage(ChatColor.RED + "   -Jailed: YES");
		} else {
			user.sendMessage(ChatColor.GRAY + "   -Jailed: NO");
		}
		if (muted) {
			user.sendMessage(ChatColor.RED + "   -Muted: YES");
		} else {
			user.sendMessage(ChatColor.GRAY + "   -Muted: NO");
		}
		if (reported) {
			user.sendMessage(ChatColor.RED + "   -Reported: YES");
		} else {
			user.sendMessage(ChatColor.GRAY + "   -Reported: NO");
		}
		if (user.hasPerm("canViewAdvStatus") && user2 != null) {
			boolean godded = user2.isGod();
			boolean poofed = user2.isPoofed();
			boolean nopoofed = user2.isNoPoofed();
			int health = user2.getHandle().getHealth();
			if (godded) {
				user.sendMessage(ChatColor.GREEN + "   -Godded: YES");
			} else {
				user.sendMessage(ChatColor.GRAY + "   -Godded: NO");
			}
			if (poofed) {
				user.sendMessage(ChatColor.GREEN + "   -Invisible: YES");
			} else {
				user.sendMessage(ChatColor.GRAY + "   -Invisible: NO");
			}
			if (nopoofed) {
				user.sendMessage(ChatColor.GREEN + "   -See-all: YES");
			} else {
				user.sendMessage(ChatColor.GRAY + "   -See-all: NO");
			}
			if (health >= 15) {
				user.sendMessage(ChatColor.GREEN + "   -Health: "
						+ (((double) health) / 2) + "/10");
			} else if (health <= 5) {
				user.sendMessage(ChatColor.RED + "   -Health: "
						+ (((double) health) / 2) + "/10");
			} else {
				user.sendMessage(ChatColor.YELLOW + "   -Health: "
						+ (((double) health) / 2) + "/10");
			}
		}
	}

	public void Kick(String[] args, User user) {
		boolean anon = false;
		String reason = "";
		User toKick;
		if (args.length == 0) {
			user.sendMessage(ChatColor.YELLOW
					+ "Proper use is: /kick <player> [--anon] [reason]");
			return;
		}
		toKick = User.matchUser(args[0], plugin);
		if (toKick == null) {
			user.sendMessage(ChatColor.RED + args[0] + " cannot be found!");
			return;
		}
		if (args.length > 1) {
			int i = 1;
			if (args[1].equalsIgnoreCase("--anon")) {
				i++;
				anon = true;
			}
			while (i < args.length) {
				if (reason.isEmpty()) {
					reason = args[i];
				} else {
					reason += " " + args[i];
				}
				i++;
			}
		}
		if (anon) {
			if (reason.isEmpty()) {
				toKick.Kick();
			} else {
				toKick.Kick(reason);
			}
		} else {
			if (reason.isEmpty()) {
				toKick.Kick(user);
			} else {
				toKick.Kick(reason, user);
			}
		}
	}
}
