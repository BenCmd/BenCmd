package ben_dude56.plugins.bencmd.reporting;

import java.util.ArrayList;
import java.util.List;

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
				&& user.hasPerm("canReport")) {
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
		plugin.log.info(user.getDisplayName() + " opened ticket #" + id.toString()
				+ "!");
		plugin.bLog.info(user.getDisplayName() + " opened ticket #" + id.toString()
				+ "!");
		user.sendMessage(ChatColor.GREEN + "Thank you for your report");
		user.sendMessage(ChatColor.GREEN
				+ "You can check the status of your report using /ticket " + id
				+ ".");
		user.sendMessage(ChatColor.GREEN
				+ "You can also list your currently open tickets using /ticket list.");
		for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
			User onlineUser;
			if ((onlineUser = User.getUser(plugin, onlinePlayer))
					.hasPerm("isTicketAdmin")) {
				onlineUser.sendMessage(ChatColor.RED
						+ "A new report has been filed! Use /ticket " + id
						+ " to see details!");
			}
		}
	}

	public void Ticket(String[] args, User user) {
		if (args.length == 0) {
			user.sendMessage(ChatColor.YELLOW
					+ "Proper use is /ticket {<id>|list|search} [options]");
			return;
		}
		if (args[0].equalsIgnoreCase("list")) {
			boolean tickets = false;
			user.sendMessage(ChatColor.GRAY + "The following tickets are OPEN:");
			for (Report ticket : plugin.reports.getReports()) {
				if (ticket.canRead(user)) {
					switch (ticket.getStatus()) {
					case UNREAD:
						tickets = true;
						user.sendMessage(ChatColor.RED + ticket.readShorthand());
						break;
					case READ:
						tickets = true;
						user.sendMessage(ChatColor.YELLOW
								+ ticket.readShorthand());
						break;
					case INVESTIGATING:
						tickets = true;
						user.sendMessage(ChatColor.GREEN
								+ ticket.readShorthand());
						break;
					}
				}
			}
			if (!tickets) {
				user.sendMessage(ChatColor.RED
						+ "You don't have any open tickets!");
			}
		} else if (args[0].equalsIgnoreCase("search")) {
			if (!user.hasPerm("isTicketAdmin")) {
				user.sendMessage(ChatColor.RED
						+ "You must be an admin to do that!");
				return;
			}
			if (args.length == 1) {
				user.sendMessage(ChatColor.YELLOW
						+ "Proper use is /ticket search <name>");
				return;
			}
			List<Report> results = new ArrayList<Report>();
			for (Report ticket : plugin.reports.getReports()) {
				if (ticket.getAccused().getName().equalsIgnoreCase(args[1])
						|| ticket.getSender().getName()
								.equalsIgnoreCase(args[1])) {
					results.add(ticket);
				}
			}
			if (results.isEmpty()) {
				user.sendMessage(ChatColor.RED
						+ "Your search returned no results!");
			} else {
				user.sendMessage(ChatColor.GRAY
						+ "Your search returned the following results:");
				for (Report ticket : results) {
					switch (ticket.getStatus()) {
					case UNREAD:
						user.sendMessage(ChatColor.RED + ticket.readShorthand());
						break;
					case READ:
						user.sendMessage(ChatColor.YELLOW
								+ ticket.readShorthand());
						break;
					case INVESTIGATING:
						user.sendMessage(ChatColor.GREEN
								+ ticket.readShorthand());
						break;
					case CLOSED:
						user.sendMessage(ChatColor.GRAY
								+ ticket.readShorthand());
						break;
					case LOCKED:
						user.sendMessage(ChatColor.DARK_GRAY
								+ ticket.readShorthand());
						break;
					}
				}
			}
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
			if (args.length == 1) {
				for(String s : plugin.reports.getTicketById(id).readReport(
						user.hasPerm("isTicketAdmin")).split("\n")) {
					user.sendMessage(s);
				}
			} else if (args[1].equalsIgnoreCase("close")) {
				if (user.hasPerm("isTicketAdmin")) {
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
						plugin.log.info(user.getDisplayName() + " closed ticket #"
								+ id.toString() + "!");
						plugin.bLog.info(user.getDisplayName() + " closed ticket #"
								+ id.toString() + "!");
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
						plugin.log.info(user.getDisplayName() + " closed ticket #"
								+ id.toString() + "! Reason: " + reason);
						plugin.bLog.info(user.getDisplayName() + " closed ticket #"
								+ id.toString() + "! Reason: " + reason);
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
						plugin.log.info(user.getDisplayName() + " closed ticket #"
								+ id.toString() + "!");
						plugin.bLog.info(user.getDisplayName() + " closed ticket #"
								+ id.toString() + "!");
					} else {
						user.sendMessage(ChatColor.RED
								+ "You cannot edit that ticket!");
					}
				}
			} else if (args[1].equalsIgnoreCase("reopen")) {
				if (user.hasPerm("isTicketAdmin")) {
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
					plugin.log.info(user.getDisplayName() + " re-opened ticket #"
							+ id.toString() + "!");
					plugin.bLog.info(user.getDisplayName() + " re-opened ticket #"
							+ id.toString() + "!");
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
						plugin.log.info(user.getDisplayName() + " re-opened ticket #"
								+ id.toString() + "!");
						plugin.bLog.info(user.getDisplayName() + " re-opened ticket #"
								+ id.toString() + "!");
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
				if (user.hasPerm("isTicketAdmin")) {
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
				if (user.hasPerm("isTicketAdmin")) {
					if (report.getStatus() == Report.ReportStatus.LOCKED) {
						user.sendMessage(ChatColor.RED
								+ "That ticket is locked!");
						return;
					}
					report.InvestigateTicket();
					user.sendMessage(ChatColor.GREEN
							+ "That ticket has been marked as under investigation!");
					plugin.log.info(user.getDisplayName() + " is investigating #"
							+ id.toString() + "!");
					plugin.bLog.info(user.getDisplayName() + " is investigating #"
							+ id.toString() + "!");
				} else {
					user.sendMessage(ChatColor.RED
							+ "You must be an admin to do that!");
				}
			} else if (args[1].equalsIgnoreCase("uninv")) {
				if (user.hasPerm("isTicketAdmin")) {
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
							&& !user.hasPerm("isTicketAdmin")) {
						user.sendMessage(ChatColor.RED
								+ "That ticket is closed! Re-open it using /ticket "
								+ report.getId()
								+ " reopen to add new information.");
						return;
					}
					if (report.getStatus() == Report.ReportStatus.INVESTIGATING
							&& !user.hasPerm("isTicketAdmin")) {
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
					if (!user.hasPerm("isTicketAdmin")) {
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
					if (user.hasPerm("isTicketAdmin")) {
						return;
					}
					for (Player onlinePlayer : plugin.getServer()
							.getOnlinePlayers()) {
						User onlineUser;
						if ((onlineUser = User.getUser(plugin, onlinePlayer))
								.hasPerm("isTicketAdmin")) {
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
