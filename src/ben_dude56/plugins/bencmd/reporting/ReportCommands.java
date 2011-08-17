package ben_dude56.plugins.bencmd.reporting;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ben_dude56.plugins.bencmd.BenCmd;
import ben_dude56.plugins.bencmd.Commands;
import ben_dude56.plugins.bencmd.User;
import ben_dude56.plugins.bencmd.permissions.PermissionUser;

public class ReportCommands implements Commands {
	BenCmd plugin;

	public ReportCommands(BenCmd instance) {
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
		if (commandLabel.equalsIgnoreCase("report")
				&& user.hasPerm("bencmd.ticket.send")) {
			Report(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("ticket")) {
			Ticket(args, user);
			return true;
		}
		return false;
	}

	public void Report(String[] args, User user) {
		if (user.isServer()) {
			user.sendMessage(ChatColor.RED + "The server cannot do that!");
			return;
		}
		if (args.length < 2) {
			user.sendMessage(ChatColor.YELLOW
					+ "Proper use is /report <player> <reason>");
			return;
		}
		PermissionUser reported = PermissionUser.matchUserIgnoreCase(args[0],
				plugin);
		if (reported == null) {
			user.sendMessage(ChatColor.RED + "That user doesn't exist!");
			return;
		}
		String reason = "";
		for (int i = 1; i < args.length; i++) {
			if (reason.equalsIgnoreCase("")) {
				reason += args[i];
			} else {
				reason += " " + args[i];
			}
		}
		Integer id = plugin.reports.nextId();
		plugin.reports.addTicket(new Report(plugin, id, user, reported,
				Report.ReportStatus.UNREAD, reason, "", 0,
				new ArrayList<String>()));
		plugin.log.info(user.getDisplayName() + " opened ticket #"
				+ id.toString() + "!");
		plugin.bLog.info(user.getDisplayName() + " opened ticket #"
				+ id.toString() + "!");
		user.sendMessage(ChatColor.GREEN + "Thank you for your report");
		user.sendMessage(ChatColor.GREEN
				+ "You can check the status of your report using /ticket " + id
				+ ".");
		user.sendMessage(ChatColor.GREEN
				+ "You can also list your currently open tickets using /ticket list.");
		for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
			User onlineUser;
			if ((onlineUser = User.getUser(plugin, onlinePlayer))
					.hasPerm("bencmd.ticket.readall")) {
				onlineUser.sendMessage(ChatColor.RED
						+ "A new report has been filed! Use /ticket " + id
						+ " to see details!");
			}
		}
	}

	public void Ticket(String[] args, User user) {
		if (args.length == 0) {
			user.sendMessage(ChatColor.YELLOW
					+ "Proper use is /ticket {<id>|list|alist|search|asearch|purge|purgefrom|purgeto} [options]");
			return;
		}
		if (args[0].equalsIgnoreCase("list")) {
			int page = 1;
			if (args.length > 1) {
				try {
					page = Integer.parseInt(args[1]);
				} catch (NumberFormatException e) {
					user.sendMessage(ChatColor.RED + args[1]
							+ " isn't a number!");
					return;
				}
			}
			plugin.reports.listTickets(user, page);
		} else if (args[0].equalsIgnoreCase("alist")) {
			int page = 1;
			if (args.length > 1) {
				try {
					page = Integer.parseInt(args[1]);
				} catch (NumberFormatException e) {
					user.sendMessage(ChatColor.RED + args[1]
							+ " isn't a number!");
					return;
				}
			}
			plugin.reports.listAllTickets(user, page);
		} else if (args[0].equalsIgnoreCase("purge")) {
			if (!user.hasPerm("bencmd.ticket.purge")) {
				user.sendMessage(ChatColor.RED
						+ "You must be an admin to do that!");
				return;
			}
			plugin.reports.PurgeOpen(user);
		} else if (args[0].equalsIgnoreCase("purgefrom")) {
			if (!user.hasPerm("bencmd.ticket.purge")) {
				user.sendMessage(ChatColor.RED
						+ "You must be an admin to do that!");
				return;
			}
			if (args.length != 2) {
				user.sendMessage(ChatColor.YELLOW
						+ "Proper use is /ticket purgefrom [name]");
				return;
			}
			plugin.reports.PurgeFrom(user, args[1]);
		} else if (args[0].equalsIgnoreCase("purgeto")) {
			if (!user.hasPerm("bencmd.ticket.purge")) {
				user.sendMessage(ChatColor.RED
						+ "You must be an admin to do that!");
				return;
			}
			if (args.length != 2) {
				user.sendMessage(ChatColor.YELLOW
						+ "Proper use is /ticket purgeto [name]");
				return;
			}
			plugin.reports.PurgeTo(user, args[1]);
		} else if (args[0].equalsIgnoreCase("search")) {
			if (!user.hasPerm("bencmd.ticket.search")) {
				user.sendMessage(ChatColor.RED
						+ "You must be an admin to do that!");
				return;
			}
			if (args.length == 1) {
				user.sendMessage(ChatColor.YELLOW
						+ "Proper use is /ticket search <name> [page]");
				return;
			}
			int page = 1;
			if (args.length == 3) {
				try {
					page = Integer.parseInt(args[2]);
				} catch (NumberFormatException e) {
					user.sendMessage(ChatColor.RED + args[2]
							+ " isn't a number!");
					return;
				}
			}
			plugin.reports.searchTickets(user, args[1], page);
		} else if (args[0].equalsIgnoreCase("asearch")) {
			if (!user.hasPerm("bencmd.ticket.asearch")) {
				user.sendMessage(ChatColor.RED
						+ "You must be an admin to do that!");
				return;
			}
			if (args.length == 1) {
				user.sendMessage(ChatColor.YELLOW
						+ "Proper use is /ticket asearch <name> [page]");
				return;
			}
			int page = 1;
			if (args.length == 3) {
				try {
					page = Integer.parseInt(args[2]);
				} catch (NumberFormatException e) {
					user.sendMessage(ChatColor.RED + args[2]
							+ " isn't a number!");
					return;
				}
			}
			plugin.reports.searchAllTickets(user, args[1], page);
		} else {
			Integer id;
			try {
				id = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				user.sendMessage(ChatColor.RED + args[0] + " is not a number!");
				return;
			}
			Report report = plugin.reports.getTicketById(id);
			if (report == null) {
				user.sendMessage(ChatColor.RED + "Ticket #" + args[0]
						+ " doesn't exist!");
				return;
			}
			if (!report.canRead(user)) {
				user.sendMessage(ChatColor.RED + "You can't read that ticket!");
				return;
			}
			if (args.length == 1) {
				for (String s : report
						.readReport(user.hasPerm("bencmd.ticket.editall"), user.getName().equals(report.getAccused().getName()) && !user.hasPerm("bencmd.ticket.readall")).split("\n")) {
					user.sendMessage(s);
				}
			} else if (args[1].equalsIgnoreCase("close")) {
				if (user.hasPerm("bencmd.ticket.editall")) {
					if (report.getStatus() == Report.ReportStatus.LOCKED) {
						user.sendMessage(ChatColor.RED
								+ "That ticket is locked!");
						return;
					}
					if (report.getStatus() == Report.ReportStatus.CLOSED) {
						user.sendMessage(ChatColor.RED
								+ "That ticket is already closed!");
						return;
					}
					if (args.length == 2) {
						report.closeTicket("Ticket closed by admin");
						plugin.log.info(user.getDisplayName()
								+ " closed ticket #" + id.toString() + "!");
						plugin.bLog.info(user.getDisplayName()
								+ " closed ticket #" + id.toString() + "!");
						user.sendMessage(ChatColor.GREEN + "Ticket (ID: "
								+ report.getId()
								+ ") has been successfully closed!");
					} else {
						String reason = "";
						for (int i = 2; i < args.length; i++) {
							if (reason.equalsIgnoreCase("")) {
								reason += args[i];
							} else {
								reason += " " + args[i];
							}
						}
						report.closeTicket(reason);
						plugin.log.info(user.getDisplayName()
								+ " closed ticket #" + id.toString()
								+ "! Reason: " + reason);
						plugin.bLog.info(user.getDisplayName()
								+ " closed ticket #" + id.toString()
								+ "! Reason: " + reason);
						user.sendMessage(ChatColor.GREEN + "Ticket (ID: "
								+ report.getId()
								+ ") has been successfully closed!");
					}
				} else {
					if (report.canBasicChange(user)) {
						if (report.getStatus() == Report.ReportStatus.LOCKED) {
							user.sendMessage(ChatColor.RED
									+ "That ticket is locked!");
							return;
						}
						if (report.getStatus() == Report.ReportStatus.CLOSED) {
							user.sendMessage(ChatColor.RED
									+ "That ticket is already closed!");
							return;
						}
						report.closeTicket("Ticket closed by user");
						user.sendMessage(ChatColor.GREEN + "Ticket (ID: "
								+ report.getId()
								+ ") has been successfully closed!");
						plugin.log.info(user.getDisplayName()
								+ " closed ticket #" + id.toString() + "!");
						plugin.bLog.info(user.getDisplayName()
								+ " closed ticket #" + id.toString() + "!");
					} else {
						user.sendMessage(ChatColor.RED
								+ "You cannot edit that ticket!");
					}
				}
			} else if (args[1].equalsIgnoreCase("reopen")) {
				if (user.hasPerm("bencmd.ticket.editall")) {
					if (report.getStatus() == Report.ReportStatus.LOCKED) {
						user.sendMessage(ChatColor.RED
								+ "That ticket is locked!");
						return;
					}
					if (report.getStatus() != Report.ReportStatus.CLOSED) {
						user.sendMessage(ChatColor.RED
								+ "That ticket is already open!");
						return;
					}
					report.reopenTicket(true);
					plugin.log.info(user.getDisplayName()
							+ " re-opened ticket #" + id.toString() + "!");
					plugin.bLog.info(user.getDisplayName()
							+ " re-opened ticket #" + id.toString() + "!");
					user.sendMessage(ChatColor.GREEN
							+ "That ticket has been re-opened!");
				} else if (report.canBasicChange(user)) {
					if (report.getStatus() == Report.ReportStatus.LOCKED) {
						user.sendMessage(ChatColor.RED
								+ "That ticket is locked!");
						return;
					}
					if (report.getStatus() != Report.ReportStatus.CLOSED) {
						user.sendMessage(ChatColor.RED
								+ "That ticket is already open!");
						return;
					}
					if (report.reopenTicket(false)) {
						plugin.log.info(user.getDisplayName()
								+ " re-opened ticket #" + id.toString() + "!");
						plugin.bLog.info(user.getDisplayName()
								+ " re-opened ticket #" + id.toString() + "!");
						user.sendMessage(ChatColor.GREEN
								+ "That ticket has been re-opened!");
					} else {
						user.sendMessage(ChatColor.RED
								+ "That ticket has been re-opened too many times!");
						user.sendMessage(ChatColor.RED
								+ "Talk to an admin to have it re-opened!");
					}
				} else {
					user.sendMessage(ChatColor.RED
							+ "You cannot edit that ticket!");
				}
			} else if (args[1].equalsIgnoreCase("lock")) {
				if (user.hasPerm("bencmd.ticket.lock") && report.canBasicChange(user)) {
					if (report.getStatus() == Report.ReportStatus.LOCKED) {
						user.sendMessage(ChatColor.RED
								+ "That ticket is locked!");
						return;
					}
					if (args.length > 2) {
						String reason = "";
						for (int i = 2; i < args.length; i++) {
							if (reason.equalsIgnoreCase("")) {
								reason += args[i];
							} else {
								reason += " " + args[i];
							}
						}
						report.lockTicket(reason);
					} else if (report.getRemark().equalsIgnoreCase("")) {
						report.lockTicket(report.getRemark());
					} else {
						report.lockTicket("Ticket locked by admin");
					}
					user.sendMessage(ChatColor.GREEN
							+ "That ticket has been locked!");
					plugin.log.info(user.getDisplayName() + " locked ticket #"
							+ id.toString() + "!");
					plugin.bLog.info(user.getDisplayName() + " locked ticket #"
							+ id.toString() + "!");
				} else {
					user.sendMessage(ChatColor.RED
							+ "You must be an admin to do that!");
				}
			} else if (args[1].equalsIgnoreCase("inv")) {
				if (user.hasPerm("bencmd.ticket.investigate") && report.canBasicChange(user)) {
					if (report.getStatus() == Report.ReportStatus.LOCKED) {
						user.sendMessage(ChatColor.RED
								+ "That ticket is locked!");
						return;
					}
					report.InvestigateTicket();
					user.sendMessage(ChatColor.GREEN
							+ "That ticket has been marked as under investigation!");
					plugin.log.info(user.getDisplayName()
							+ " is investigating #" + id.toString() + "!");
					plugin.bLog.info(user.getDisplayName()
							+ " is investigating #" + id.toString() + "!");
				} else {
					user.sendMessage(ChatColor.RED
							+ "You must be an admin to do that!");
				}
			} else if (args[1].equalsIgnoreCase("uninv")) {
				if (user.hasPerm("bencmd.ticket.investigate") && report.canBasicChange(user)) {
					if (report.getStatus() == Report.ReportStatus.LOCKED) {
						user.sendMessage(ChatColor.RED
								+ "That ticket is locked!");
						return;
					}
					report.UninvestigateTicket();
					user.sendMessage(ChatColor.GREEN
							+ "That ticket has been marked as read!");
					plugin.log.info(user.getDisplayName()
							+ " is no longer investigating #" + id.toString()
							+ "!");
					plugin.bLog.info(user.getDisplayName()
							+ " is no longer investigating #" + id.toString()
							+ "!");
				} else {
					user.sendMessage(ChatColor.RED
							+ "You must be an admin to do that!");
				}
			} else if (args[1].equalsIgnoreCase("addinfo")) {
				if (report.canBasicChange(user)) {
					if (report.getStatus() == Report.ReportStatus.LOCKED) {
						user.sendMessage(ChatColor.RED
								+ "That ticket is locked!");
						return;
					}
					if (report.getStatus() == Report.ReportStatus.CLOSED
							&& !user.hasPerm("bencmd.ticket.editall")) {
						user.sendMessage(ChatColor.RED
								+ "That ticket is closed! Re-open it using /ticket "
								+ report.getId()
								+ " reopen to add new information.");
						return;
					}
					if (report.getStatus() == Report.ReportStatus.INVESTIGATING
							&& !user.hasPerm("bencmd.ticket.editall")) {
						user.sendMessage(ChatColor.RED
								+ "That ticket is under investigation and new info cannot be added. Ask an admin for more details...");
						return;
					}
					if (args.length == 2) {
						user.sendMessage(ChatColor.YELLOW
								+ "Proper use is /ticket <id> addinfo <additional info>");
						return;
					}
					String newInfo = "";
					for (int i = 2; i < args.length; i++) {
						if (newInfo.equalsIgnoreCase("")) {
							newInfo += args[i];
						} else {
							newInfo += " " + args[i];
						}
					}
					report.addInfo(newInfo);
					if (!user.hasPerm("bencmd.ticket.editall")) {
						report.setStatus(Report.ReportStatus.UNREAD);
					}
					user.sendMessage(ChatColor.GREEN
							+ "The info has been added successfully!");
					plugin.log.info(user.getDisplayName()
							+ " has added information to ticket #"
							+ id.toString() + "!");
					plugin.bLog.info(user.getDisplayName()
							+ " has added information to ticket #"
							+ id.toString() + "!");
					if (user.hasPerm("bencmd.ticket.editall")) {
						return;
					}
					for (Player onlinePlayer : plugin.getServer()
							.getOnlinePlayers()) {
						User onlineUser;
						if ((onlineUser = User.getUser(plugin, onlinePlayer))
								.hasPerm("bencmd.ticket.readall")) {
							onlineUser
									.sendMessage(ChatColor.RED
											+ "Info has been added to a report! Use /ticket "
											+ id + " to see details!");
						}
					}
				} else {
					user.sendMessage(ChatColor.RED
							+ "You cannot edit that ticket!");
				}
			} else {
				user.sendMessage(ChatColor.YELLOW
						+ "Proper use is /ticket <id> [{close|reopen|lock|inv|uninv|addinfo}]");
			}
		}
	}

}
