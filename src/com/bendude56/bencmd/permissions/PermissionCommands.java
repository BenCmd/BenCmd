package com.bendude56.bencmd.permissions;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.Commands;
import com.bendude56.bencmd.User;
import com.bendude56.bencmd.permissions.Action.ActionType;
import com.bendude56.bencmd.permissions.ActionLogEntry.ActionLogType;
import com.bendude56.bencmd.reporting.Report;
import com.bendude56.bencmd.reporting.Report.ReportStatus;


public class PermissionCommands implements Commands {
	BenCmd plugin;

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
				&& user.hasPerm("bencmd.editpermissions")) {
			User(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("group")
				&& user.hasPerm("bencmd.editpermissions")) {
			Group(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("status")) {
			Status(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("kick")
				&& user.hasPerm("bencmd.action.kick.normal")) {
			Kick(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("mute")
				&& user.hasPerm("bencmd.action.mute")) {
			Mute(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("unmute")
				&& user.hasPerm("bencmd.action.unmute")) {
			Unmute(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("jail")
				&& user.hasPerm("bencmd.action.jail")) {
			Jail(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("unjail")
				&& user.hasPerm("bencmd.action.unjail")) {
			Unjail(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("ban")
				&& user.hasPerm("bencmd.action.ban")) {
			Ban(args, user);
			return true;
		} else if ((commandLabel.equalsIgnoreCase("pardon") || commandLabel
				.equalsIgnoreCase("pardon")) && user.hasPerm("bencmd.action.unban")) {
			Unban(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("note")
				&& user.hasPerm("bencmd.action.note")) {
			Note(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("record")
				&& user.hasPerm("bencmd.action.record")) {
			Record(args, user);
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
				/*for (InternalGroup g : plugin.perm.groupFile.groups.values()) {
					
				}*/
				plugin.perm.userFile.removeUser(user2);
				user.sendMessage(ChatColor.GREEN + "User " + args[0]
						+ " was successfully removed!");
				plugin.log.info("User " + args[0] + " has been removed!");
				plugin.bLog.info("User " + args[0]
						+ " has been deleted from users.db!");
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
				plugin.bLog.info("User " + args[0]
						+ " has been added to users.db!");
			}
		} else if (args[1].equalsIgnoreCase("info")) {
			if (user2 == null) {
				user.sendMessage(ChatColor.RED + "That user doesn't exist!");
			} else {
				user.sendMessage(ChatColor.GRAY + "Information for user \""
						+ user2.getName() + "\":");
				user.sendMessage(ChatColor.GRAY + "Permissions: "
						+ user2.listPermissions());
				user.sendMessage(ChatColor.GRAY + "Groups: "
						+ user2.listGroups());
				if (user2.getPrefix().isEmpty()) {
					user.sendMessage(ChatColor.GRAY + "Prefix: "
							+ user2.getColor() + "(None)");
				} else {
					user.sendMessage(ChatColor.GRAY + "Prefix: "
							+ user2.getColor() + user2.getPrefix());
				}
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
					plugin.bLog.info("User " + args[0] + " now has permission "
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
					plugin.bLog.info("User " + args[0]
							+ " has lost permission " + args[1]);
				}
			}
		}
	}

	public void Group(String[] args, User user) {
		if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
			user.sendMessage(ChatColor.YELLOW
					+ "Proper use is: /group <name> {add|remove|c:<color>|p:<prefix>|<permissions>|plist}");
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
				plugin.bLog.info("Group " + args[0]
						+ " has been deleted from groups.db!");
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
				plugin.bLog.info("Group " + args[0]
						+ " has been added to groups.db!");
			}
		} else if (args[1].equalsIgnoreCase("info")) {
			if (group == null) {
				user.sendMessage(ChatColor.RED + "That group doesn't exist!");
			} else {
				user.sendMessage(ChatColor.GRAY + "Information for group \""
						+ group.getName() + "\":");
				user.sendMessage(ChatColor.GRAY + "Permissions: "
						+ group.listPermissions());
				user.sendMessage(ChatColor.GRAY + "Users: " + group.listUsers());
				user.sendMessage(ChatColor.GRAY + "Groups: "
						+ group.listGroups());
				if (group.getPrefix().isEmpty()) {
					user.sendMessage(ChatColor.GRAY + "Prefix: "
							+ group.getColor() + "(None)");
				} else {
					user.sendMessage(ChatColor.GRAY + "Prefix: "
							+ group.getColor() + group.getPrefix());
				}
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
							plugin.bLog.info(user2.getName()
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
							plugin.bLog.info(user2.getName()
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
							plugin.bLog.info(group2.getName()
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
							plugin.bLog.info(group2.getName()
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
				plugin.bLog.info("The prefix of group " + args[0]
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
					plugin.bLog.info("The color of group " + args[0]
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
					plugin.bLog.info("The level of group " + args[0]
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
					plugin.bLog.info("Group " + args[0]
							+ " now has permission " + args[1]);
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
					plugin.bLog.info("Group " + args[0]
							+ " has lost permission " + args[1]);
				}
			}
		} else if (args[1].equalsIgnoreCase("plist")) {
			if (group == null) {
				user.sendMessage(ChatColor.RED + "That group doesn't exist!");
			} else {
				user.sendMessage(group.listPermissions());
				return;
			}
		} else {
			user.sendMessage(ChatColor.RED +
				"Proper use is: /group <name> {add|remove|c:<color>|p:<prefix>|<permissions>|plist}");
			return;
		}
	}

	public void Status(String[] args, User user) {
		PermissionUser puser2 = null;
		User user2 = null;
		if (args.length == 1 && !user.hasPerm("bencmd.action.status.other")) {
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
		if (user.hasPerm("bencmd.action.status.advanced") && user2 != null) {
			boolean godded = user2.isGod();
			boolean allpoofed = user2.isNoPoofed();
			boolean poofed = user2.isPoofed();
			boolean nopoofed = user2.isNoPoofed();
			int health = user2.getHandle().getHealth();
			if (user.getActiveChannel() != null) {
				user.sendMessage(ChatColor.GRAY + "   -Chat channel: "
						+ user.getActiveChannel().getDisplayName());
			}
			if (godded) {
				user.sendMessage(ChatColor.GREEN + "   -Godded: YES");
			} else {
				user.sendMessage(ChatColor.GRAY + "   -Godded: NO");
			}
			if (allpoofed) {
				user.sendMessage(ChatColor.GREEN + "   -Invisible: ALL");
			} else if (poofed) {
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
		if (toKick.hasPerm("bencmd.kick.protect") && !user.hasPerm("bencmd.kick.all")) {
			user.sendMessage(ChatColor.RED + "That player is protected from being godded/ungodded by others!");
			return;
		}
		plugin.alog.log(new ActionLogEntry(ActionLogType.KICK, toKick.getName(), user.getName(), (reason.isEmpty()) ? "Reason not provided" : reason));
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
		if (duration == -1 && !user.hasPerm("bencmd.action.permamute")) {
			user.sendMessage(ChatColor.RED + "You cannot permanently mute somebody! Specify a time limit!");
			return;
		}
		if (duration == -1) {
			plugin.alog.log(new ActionLogEntry(ActionLogType.MUTE_PERM, puser2.getName(), user.getName()));
		} else {
			plugin.alog.log(new ActionLogEntry(ActionLogType.MUTE_TEMP, puser2.getName(), user.getName(), duration));
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
		plugin.alog.log(new ActionLogEntry(ActionLogType.UNMUTE_MAN, puser2.getName(), user.getName()));
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
		if (duration == -1 && !user.hasPerm("bencmd.action.permajail")) {
			user.sendMessage(ChatColor.RED + "You cannot permanently jail somebody! Specify a time limit!");
			return;
		}
		if (duration == -1) {
			plugin.alog.log(new ActionLogEntry(ActionLogType.JAIL_PERM, puser2.getName(), user.getName()));
		} else {
			plugin.alog.log(new ActionLogEntry(ActionLogType.JAIL_TEMP, puser2.getName(), user.getName(), duration));
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
		plugin.alog.log(new ActionLogEntry(ActionLogType.UNJAIL_MAN, puser2.getName(), user.getName()));
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
		if (duration == -1 && !user.hasPerm("bencmd.action.permaban")) {
			user.sendMessage(ChatColor.RED + "You cannot permanently ban somebody! Specify a time limit!");
			return;
		}
		if (duration == -1) {
			plugin.alog.log(new ActionLogEntry(ActionLogType.BAN_PERM, puser2.getName(), user.getName()));
		} else {
			plugin.alog.log(new ActionLogEntry(ActionLogType.BAN_TEMP, puser2.getName(), user.getName(), duration));
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
		plugin.alog.log(new ActionLogEntry(ActionLogType.UNBAN_MAN, puser2.getName(), user.getName()));
		plugin.actions.removeAction(puser2.isBanned());
		user.sendMessage(ChatColor.GREEN + "That user has been unbanned!");
	}
	
	public void Note(String[] args, User user) {
		if (args.length < 2) {
			user.sendMessage(ChatColor.YELLOW + "Proper use is: /note <player> <note>");
			return;
		}
		PermissionUser puser2;
		if ((puser2 = PermissionUser.matchUser(args[0], plugin)) == null) {
			user.sendMessage(ChatColor.RED + "That user could not be found!");
			return;
		}
		String m = "";
		for (int i = 1; i < args.length; i++) {
			if (m.isEmpty()) {
				m += args[i];
			} else {
				m += " " + args[i];
			}
		}
		plugin.alog.log(new ActionLogEntry(ActionLogType.NOTE, puser2.getName(), user.getName(), m));
		user.sendMessage(ChatColor.GREEN + "That note was successfully appended to " + puser2.getName() + "'s record");
	}
	
	public void Record(String[] args, User user) {
		if (args.length == 0 || args.length > 3) {
			user.sendMessage(ChatColor.YELLOW + "Proper use is: /record <player> [-f] <page>");
			return;
		}
		String u = args[0];
		int p;
		boolean from = false;
		if (args.length == 1) {
			p = 1;
		} else if (args[1].equals("-f")) {
			from = true;
			if (args.length == 2) {
				p = 1;
			} else {
				try {
					p = Integer.parseInt(args[2]);
				} catch (NumberFormatException e) {
					user.sendMessage(ChatColor.RED + "'" + args[2] + "' is not a number!");
					return;
				}
			}
		} else if (args.length == 3) {
			user.sendMessage(ChatColor.YELLOW + "Proper use is: /record <player> [-f] <page>");
			return;
		} else {
			try {
				p = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				user.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a number!");
				return;
			}
		}
		if (from) {
			plugin.alog.searchEntriesFrom(user, u, p);
		} else if (u.equals("*")) {
			plugin.alog.searchEntries(user, p);
		} else {
			plugin.alog.searchEntries(user, u, p);
		}
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
