package ben_dude56.plugins.bencmd.permissions;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ben_dude56.plugins.bencmd.BenCmd;
import ben_dude56.plugins.bencmd.Commands;
import ben_dude56.plugins.bencmd.User;
import ben_dude56.plugins.bencmd.permissions.Action.ActionType;
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
		} else if (commandLabel.equalsIgnoreCase("mute")
				&& user.hasPerm("canMute")) {
			Mute(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("unmute")
				&& user.hasPerm("canUnmute")) {
			Unmute(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("jail")
				&& user.hasPerm("canJail")) {
			Jail(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("unjail")
				&& user.hasPerm("canUnjail")) {
			Unjail(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("ban")
				&& user.hasPerm("canBan")) {
			Ban(args, user);
			return true;
		} else if ((commandLabel.equalsIgnoreCase("pardon") || commandLabel
				.equalsIgnoreCase("pardon")) && user.hasPerm("canUnban")) {
			Unban(args, user);
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
					+ "   -Otherwise, type +<permission> or -<permission> to add/remove permissions.");
			return;
		}
		PermissionUser user2 = plugin.perm.userFile.getUser(args[0]);
		if (args[1].equalsIgnoreCase("remove")) {
			if (user2 == null) {
				user.sendMessage(ChatColor.RED + "That user doesn't exist!");
			} else {
				plugin.perm.userFile.removeUser(user2);
				user.sendMessage(ChatColor.GREEN + "User " + args[0]
						+ " was successfully removed!");
				plugin.log.info("User " + args[0] + " has been removed!");
			}
		} else if (args[1].equalsIgnoreCase("add")) {
			if (user2 != null) {
				user.sendMessage(ChatColor.RED + "That user already exists!");
			} else {
				plugin.perm.userFile.addUser(new PermissionUser(plugin,
						args[0], new ArrayList<String>()));
				user.sendMessage(ChatColor.GREEN + "User " + args[0]
						+ " was successfully created!");
				plugin.log.info("User " + args[0] + " has been created!");
			}
		} else if (args[1].startsWith("+")) {
			args[1] = args[1].replaceFirst("\\+", "");
			if (user2 == null) {
				user.sendMessage(ChatColor.RED + "That user doesn't exist!");
			} else {
				if (user2.hasPerm(args[1], false, false)) {
					user.sendMessage(ChatColor.RED
							+ "That user can already do that!");
				} else {
					user2.addPermission(args[1]);
					user.sendMessage(ChatColor.GREEN
							+ "That user now has the specified permission!");
					plugin.log.info("User " + args[0] + " now has permission "
							+ args[1]);
				}
			}
		} else if (args[1].startsWith("-")) {
			args[1] = args[1].replaceFirst("-", "");
			if (user2 == null) {
				user.sendMessage(ChatColor.RED + "That user doesn't exist!");
			} else {
				if (!user2.hasPerm(args[1], false, false)) {
					user.sendMessage(ChatColor.RED + "That user can't do that!");
				} else {
					user2.removePermission(args[1]);
					user.sendMessage(ChatColor.GREEN
							+ "That user has now lost the specified permission!");
					plugin.log.info("User " + args[0] + " has lost permission "
							+ args[1]);
				}
			}
		}
	}

	public void Group(String[] args, User user) {
		if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
			user.sendMessage(ChatColor.YELLOW
					+ "Proper use is: /group <name> {add|remove|c:<color>|p:<prefix>|<permissions>}");
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
		PermissionGroup group = plugin.perm.groupFile.getGroup(args[0]);
		if (args[1].equalsIgnoreCase("remove")) {
			if (group == null) {
				user.sendMessage(ChatColor.RED + "That group doesn't exist!");
			} else {
				plugin.perm.groupFile.removeGroup(group);
				user.sendMessage(ChatColor.GREEN + "Group " + args[0]
						+ " was successfully removed!");
				plugin.log.info("Group " + args[0] + " has been removed!");
			}
		} else if (args[1].equalsIgnoreCase("add")) {
			if (group != null) {
				user.sendMessage(ChatColor.RED + "That group already exists!");
			} else {
				plugin.perm.groupFile.addGroup(new PermissionGroup(plugin,
						args[0], new ArrayList<String>(),
						new ArrayList<String>(), new ArrayList<String>(), "",
						-1, 0));
				user.sendMessage(ChatColor.GREEN + "Group " + args[0]
						+ " was successfully created!");
				plugin.log.info("Group " + args[0] + " has been created!");
			}
		} else if (args[1].equalsIgnoreCase("adduser")) {
			if (args.length != 3) {
				user.sendMessage(ChatColor.YELLOW
						+ "Proper use is: /group <name> adduser <user>");
			} else {
				if (group == null) {
					user.sendMessage(ChatColor.RED
							+ "That group doesn't exist!");
				} else {
					PermissionUser user2 = PermissionUser.matchUserIgnoreCase(
							args[2], plugin);
					if (user2 == null) {
						user.sendMessage(ChatColor.RED
								+ "That user doesn't exist!");
					} else {
						if (group.userInGroup(user2)) {
							user.sendMessage(ChatColor.RED + user2.getName()
									+ " is already part of " + group.getName()
									+ "!");
						} else {
							group.addUser(user2);
							user.sendMessage(ChatColor.GREEN + user2.getName()
									+ " is now a part of group "
									+ group.getName());
							plugin.log.info(user2.getName()
									+ " is now a part of group "
									+ group.getName());
						}
					}
				}
			}
		} else if (args[1].equalsIgnoreCase("remuser")) {
			if (args.length != 3) {
				user.sendMessage(ChatColor.YELLOW
						+ "Proper use is: /group <name> remuser <user>");
			} else {
				if (group == null) {
					user.sendMessage(ChatColor.RED
							+ "That group doesn't exist!");
				} else {
					PermissionUser user2 = PermissionUser.matchUserIgnoreCase(
							args[2], plugin);
					if (user2 == null) {
						user.sendMessage(ChatColor.RED
								+ "That user doesn't exist!");
					} else {
						if (!group.userInGroup(user2)) {
							user.sendMessage(ChatColor.RED + user2.getName()
									+ " is not part of " + group.getName()
									+ "!");
						} else {
							group.removeUser(user2);
							user.sendMessage(ChatColor.GREEN + user2.getName()
									+ " is no longer a part of group "
									+ group.getName());
							plugin.log.info(user2.getName()
									+ " is no longer a part of group "
									+ group.getName());
						}
					}
				}
			}
		} else if (args[1].equalsIgnoreCase("addgroup")) {
			if (args.length != 3) {
				user.sendMessage(ChatColor.YELLOW
						+ "Proper use is: /group <name> addgroup <group>");
			} else {
				if (group == null) {
					user.sendMessage(ChatColor.RED
							+ "That group doesn't exist!");
				} else {
					PermissionGroup group2 = plugin.perm.groupFile
							.getGroup(args[2]);
					if (group2 == null) {
						user.sendMessage(ChatColor.RED
								+ "That group doesn't exist!");
					} else {
						if (group.groupInGroup(group2)) {
							user.sendMessage(ChatColor.RED + group2.getName()
									+ " is already part of " + group.getName()
									+ "!");
						} else {
							group.addGroup(group2);
							user.sendMessage(ChatColor.GREEN + group2.getName()
									+ " is now a part of group "
									+ group.getName());
							plugin.log.info(group2.getName()
									+ " is now a part of group "
									+ group.getName());
						}
					}
				}
			}
		} else if (args[1].equalsIgnoreCase("remgroup")) {
			if (args.length != 3) {
				user.sendMessage(ChatColor.YELLOW
						+ "Proper use is: /group <name> remgroup <group>");
			} else {
				if (group == null) {
					user.sendMessage(ChatColor.RED
							+ "That group doesn't exist!");
				} else {
					PermissionGroup group2 = plugin.perm.groupFile
							.getGroup(args[2]);
					if (group2 == null) {
						user.sendMessage(ChatColor.RED
								+ "That group doesn't exist!");
					} else {
						if (!group.groupInGroup(group2)) {
							user.sendMessage(ChatColor.RED + group2.getName()
									+ " is not part of " + group.getName()
									+ "!");
						} else {
							group.removeGroup(group2);
							user.sendMessage(ChatColor.GREEN + group2.getName()
									+ " is no longer a part of group "
									+ group.getName());
							plugin.log.info(group2.getName()
									+ " is no longer a part of group "
									+ group.getName());
						}
					}
				}
			}
		} else if (args[1].startsWith("p:")) {
			if (group == null) {
				user.sendMessage(ChatColor.RED + "That group doesn't exist!");
			} else {
				group.setPrefix(args[1].replaceFirst("p:", "")
						.replace("_", " "));
				user.sendMessage(ChatColor.GREEN
						+ "The prefix was successfully updated!");
				plugin.log.info("The prefix of group " + args[0]
						+ " was changed to "
						+ args[1].replaceFirst("p:", "").replace("_", " "));
			}
		} else if (args[1].startsWith("c:")) {
			if (group == null) {
				user.sendMessage(ChatColor.RED + "That group doesn't exist!");
			} else {
				try {
					group.setColor(Integer.parseInt(
							args[1].replaceFirst("c:", ""), 16));
					user.sendMessage(ChatColor.GREEN
							+ "The color was successfully updated!");
					plugin.log.info("The color of group " + args[0]
							+ " was changed to "
							+ args[1].replaceFirst("c:", ""));
				} catch (NumberFormatException e) {
					user.sendMessage(ChatColor.RED
							+ "Are you sure that's a hex number?");
				}
			}
		} else if (args[1].startsWith("l:")) {
			if (group == null) {
				user.sendMessage(ChatColor.RED + "That group doesn't exist!");
			} else {
				try {
					group.setLevel(Integer.parseInt(args[1].replaceFirst("l:",
							"")));
					user.sendMessage(ChatColor.GREEN
							+ "The level was successfully updated!");
					plugin.log.info("The level of group " + args[0]
							+ " was changed to "
							+ args[1].replaceFirst("l:", ""));
				} catch (NumberFormatException e) {
					user.sendMessage(ChatColor.RED
							+ "Are you sure that's a number?");
				}
			}
		} else if (args[1].startsWith("+")) {
			args[1] = args[1].replaceFirst("\\+", "");
			if (group == null) {
				user.sendMessage(ChatColor.RED + "That group doesn't exist!");
			} else {
				if (group.hasPerm(args[1], false, false)) {
					user.sendMessage(ChatColor.RED
							+ "That group can already do that!");
				} else {
					group.addPermission(args[1]);
					user.sendMessage(ChatColor.GREEN
							+ "That group now has the specified permission!");
					plugin.log.info("Group " + args[0] + " now has permission "
							+ args[1]);
				}
			}
		} else if (args[1].startsWith("-")) {
			args[1] = args[1].replaceFirst("-", "");
			if (group == null) {
				user.sendMessage(ChatColor.RED + "That group doesn't exist!");
			} else {
				if (!group.hasPerm(args[1], false, false)) {
					user.sendMessage(ChatColor.RED
							+ "That group can't do that!");
				} else {
					group.removePermission(args[1]);
					user.sendMessage(ChatColor.GREEN
							+ "That group has now lost the specified permission!");
					plugin.log.info("Group " + args[0]
							+ " has lost permission " + args[1]);
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
		boolean banned = puser2.isBanned() != null;
		boolean jailed = puser2.isJailed() != null;
		boolean muted = puser2.isMuted() != null;
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
		if (banned) {
			user.sendMessage(ChatColor.RED + "   -Banned: YES");
		} else {
			user.sendMessage(ChatColor.GRAY + "   -Banned: NO");
		}
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

	public void Mute(String[] args, User user) {
		if (args.length < 1 || args.length > 2) {
			user.sendMessage(ChatColor.YELLOW
					+ "Proper use is: /mute <player> [time{s|m|h|d}]");
			return;
		}
		PermissionUser puser2;
		if ((puser2 = PermissionUser.matchUser(args[0], plugin)) == null) {
			user.sendMessage(ChatColor.RED + "That user could not be found!");
			return;
		}
		if (puser2.isMuted() != null) {
			user.sendMessage(ChatColor.RED + "That user is already muted!");
			return;
		}
		long duration = -1;
		TimeType durationType;
		if (args.length == 2) {
			if (args[1].endsWith("s")) {
				durationType = TimeType.SECOND;
			} else if (args[1].endsWith("m")) {
				durationType = TimeType.MINUTE;
			} else if (args[1].endsWith("h")) {
				durationType = TimeType.HOUR;
			} else if (args[1].endsWith("d")) {
				durationType = TimeType.DAY;
			} else {
				user.sendMessage(ChatColor.YELLOW
						+ "Proper use is: /mute <player> [time{s|m|h|d}]");
				return;
			}
			args[1] = args[1].substring(0, args[1].length() - 1);
			try {
				duration = Long.parseLong(args[1]);
			} catch (NumberFormatException e) {
				user.sendMessage(ChatColor.YELLOW
						+ "Proper use is: /mute <player> [<amount>{s|m|h|d}]");
				return;
			}
			duration *= getValue(durationType);
		}
		plugin.actions.addAction(puser2, ActionType.ALLMUTE, duration);
		User user2;
		if ((user2 = User.matchUser(args[0], plugin)) != null) {
			user2.sendMessage(ChatColor.RED + "You've been muted!");
		}
		user.sendMessage(ChatColor.RED + "That user has been muted!");
	}

	public void Unmute(String[] args, User user) {
		if (args.length != 1) {
			user.sendMessage(ChatColor.YELLOW
					+ "Proper use is: /unmute <player>");
			return;
		}
		PermissionUser puser2;
		if ((puser2 = PermissionUser.matchUser(args[0], plugin)) == null) {
			user.sendMessage(ChatColor.RED + "That user could not be found!");
			return;
		}
		if (puser2.isMuted() == null) {
			user.sendMessage(ChatColor.RED + "That user isn't muted!");
			return;
		}
		plugin.actions.removeAction(puser2.isMuted());
		User user2;
		if ((user2 = User.matchUser(args[0], plugin)) != null) {
			user2.sendMessage(ChatColor.GREEN + "You've been unmuted!");
		}
		user.sendMessage(ChatColor.GREEN + "That user has been unmuted!");
	}

	public void Jail(String[] args, User user) {
		if (args.length < 1 || args.length > 2) {
			user.sendMessage(ChatColor.YELLOW
					+ "Proper use is: /jail <player> [time{s|m|h|d}]");
			return;
		}
		PermissionUser puser2;
		if ((puser2 = PermissionUser.matchUser(args[0], plugin)) == null) {
			user.sendMessage(ChatColor.RED + "That user could not be found!");
			return;
		}
		if (puser2.isJailed() != null) {
			user.sendMessage(ChatColor.RED + "That user is already jailed!");
			return;
		}
		long duration = -1;
		TimeType durationType;
		if (args.length == 2) {
			if (args[1].endsWith("s")) {
				durationType = TimeType.SECOND;
			} else if (args[1].endsWith("m")) {
				durationType = TimeType.MINUTE;
			} else if (args[1].endsWith("h")) {
				durationType = TimeType.HOUR;
			} else if (args[1].endsWith("d")) {
				durationType = TimeType.DAY;
			} else {
				user.sendMessage(ChatColor.YELLOW
						+ "Proper use is: /jail <player> [time{s|m|h|d}]");
				return;
			}
			args[1] = args[1].substring(0, args[1].length() - 1);
			try {
				duration = Long.parseLong(args[1]);
			} catch (NumberFormatException e) {
				user.sendMessage(ChatColor.YELLOW
						+ "Proper use is: /jail <player> [<amount>{s|m|h|d}]");
				return;
			}
			duration *= getValue(durationType);
		}
		plugin.actions.addAction(puser2, ActionType.JAIL, duration);
		User user2;
		if ((user2 = User.matchUser(args[0], plugin)) != null) {
			plugin.jail.SendToJail(user2.getHandle());
			user2.sendMessage(ChatColor.RED + "You've been jailed!");
		}
		user.sendMessage(ChatColor.RED + "That user has been jailed!");
	}

	public void Unjail(String[] args, User user) {
		if (args.length != 1) {
			user.sendMessage(ChatColor.YELLOW
					+ "Proper use is: /unjail <player>");
			return;
		}
		PermissionUser puser2;
		if ((puser2 = PermissionUser.matchUser(args[0], plugin)) == null) {
			user.sendMessage(ChatColor.RED + "That user could not be found!");
			return;
		}
		if (puser2.isJailed() == null) {
			user.sendMessage(ChatColor.RED + "That user isn't jailed!");
			return;
		}
		plugin.actions.removeAction(puser2.isJailed());
		User user2;
		if ((user2 = User.matchUser(args[0], plugin)) != null) {
			user2.sendMessage(ChatColor.GREEN + "You've been unjailed!");
			user2.Spawn();
		} else {
			plugin.actions.addAction(puser2, ActionType.LEAVEJAIL, -1);
		}
		user.sendMessage(ChatColor.GREEN + "That user has been unjailed!");
	}

	public void Ban(String[] args, User user) {
		if (args.length < 1 || args.length > 2) {
			user.sendMessage(ChatColor.YELLOW
					+ "Proper use is: /ban <player> [time{s|m|h|d}]");
			return;
		}
		PermissionUser puser2;
		if ((puser2 = PermissionUser.matchUser(args[0], plugin)) == null) {
			user.sendMessage(ChatColor.RED + "That user could not be found!");
			return;
		}
		if (puser2.isBanned() != null) {
			user.sendMessage(ChatColor.RED + "That user is already banned!");
			return;
		}
		long duration = -1;
		TimeType durationType;
		if (args.length == 2) {
			if (args[1].endsWith("s")) {
				durationType = TimeType.SECOND;
			} else if (args[1].endsWith("m")) {
				durationType = TimeType.MINUTE;
			} else if (args[1].endsWith("h")) {
				durationType = TimeType.HOUR;
			} else if (args[1].endsWith("d")) {
				durationType = TimeType.DAY;
			} else {
				user.sendMessage(ChatColor.YELLOW
						+ "Proper use is: /ban <player> [time{s|m|h|d}]");
				return;
			}
			args[1] = args[1].substring(0, args[1].length() - 1);
			try {
				duration = Long.parseLong(args[1]);
			} catch (NumberFormatException e) {
				user.sendMessage(ChatColor.YELLOW
						+ "Proper use is: /ban <player> [<amount>{s|m|h|d}]");
				return;
			}
			duration *= getValue(durationType);
		}
		plugin.actions.addAction(puser2, ActionType.BAN, duration);
		User user2;
		if ((user2 = User.matchUser(args[0], plugin)) != null) {
			user2.Kick("You've been banned!");
		}
		user.sendMessage(ChatColor.RED + "That user has been banned!");
	}

	public void Unban(String[] args, User user) {
		if (args.length != 1) {
			user.sendMessage(ChatColor.YELLOW
					+ "Proper use is: /unban <player>");
			return;
		}
		PermissionUser puser2;
		if ((puser2 = PermissionUser.matchUser(args[0], plugin)) == null) {
			user.sendMessage(ChatColor.RED + "That user could not be found!");
			return;
		}
		if (puser2.isBanned() == null) {
			user.sendMessage(ChatColor.RED + "That user isn't banned!");
			return;
		}
		plugin.actions.removeAction(puser2.isBanned());
		user.sendMessage(ChatColor.GREEN + "That user has been unjailed!");
	}

	private long getValue(TimeType tt) {
		switch (tt) {
		case SECOND:
			return 1000L;
		case MINUTE:
			return 60000L;
		case HOUR:
			return 3600000L;
		case DAY:
			return 86400000L;
		default:
			return 0L;
		}
	}

	enum TimeType {
		SECOND, MINUTE, HOUR, DAY
	}
}
