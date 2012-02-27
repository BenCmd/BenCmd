package com.bendude56.bencmd.reporting;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.Commands;
import com.bendude56.bencmd.User;
import com.bendude56.bencmd.event.report.ReportCloseEvent;
import com.bendude56.bencmd.event.report.ReportCreateEvent;
import com.bendude56.bencmd.event.report.ReportLockEvent;
import com.bendude56.bencmd.permissions.PermissionUser;
import com.bendude56.bencmd.recording.Recording;

public class ReportCommands implements Commands {

	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		User user = User.getUser(sender);
		if (commandLabel.equalsIgnoreCase("report") && user.hasPerm("bencmd.ticket.send")) {
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
			user.sendMessage(BenCmd.getLocale().getString("basic.noServerUse"));
			return;
		}
		if (args.length < 2) {
			user.sendMessage(ChatColor.YELLOW + "Proper use is /report <player> <reason>");
			return;
		}
		PermissionUser reported = PermissionUser.matchUserAllowPartial(args[0]);
		if (reported == null) {
			user.sendMessage(BenCmd.getLocale().getString("basic.userNotFound", args[0]));
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
		Integer id = BenCmd.getReports().nextId();
		Report r = new Report(id, user.getName(), reported.getName(), Report.ReportStatus.UNREAD, reason, "", 0, new ArrayList<String>());
		ReportCreateEvent e;
		Bukkit.getPluginManager().callEvent(e = new ReportCreateEvent(r, user));
		if (e.isCancelled()) {
			return;
		}
		r = e.getReport();
		BenCmd.getReports().addTicket(r);
		BenCmd.log(user.getDisplayName() + " opened ticket #" + id.toString() + "!");
		user.sendMessage(ChatColor.GREEN + "Thank you for your report");
		user.sendMessage(ChatColor.GREEN + "You can check the status of your report using /ticket " + id + ".");
		user.sendMessage(ChatColor.GREEN + "You can also list your currently open tickets using /ticket list.");
		for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			User onlineUser;
			if ((onlineUser = User.getUser(onlinePlayer)).hasPerm("bencmd.ticket.readall")) {
				if (BenCmd.isSpoutConnected() && BenCmd.getSpoutConnector().enabled(onlinePlayer)) {
					BenCmd.getSpoutConnector().sendNotification(onlinePlayer, "New report filed!", "Ticket ID: " + id, Material.PAPER);
				} else {
					onlineUser.sendMessage(ChatColor.RED + "A new report has been filed! Use /ticket " + id + " to see details!");
				}
			}
		}
		try {
			BenCmd.getRecordingFile().copy(BenCmd.getRecordingFile().getTemporaryRecording(), "ticket" + id);
			BenCmd.getRecordingFile().turnPermanent(BenCmd.getRecordingFile().getRecording("ticket" + id));
			Recording rec = BenCmd.getRecordingFile().getRecording("ticket" + id);
			List<String> l = new ArrayList<String>();
			l.add(user.getName());
			l.add(reported.getName());
			rec.trimToUsers(l);
			rec.trimToLastHour();
			rec.save();
		} catch (Exception ex) {
			user.sendMessage(ChatColor.RED + "Failed to attach recording to ticket!");
		}
	}

	public void Ticket(String[] args, User user) {
		if (args.length == 0) {
			user.sendMessage(ChatColor.YELLOW + "Proper use is /ticket {<id>|list|alist|search|asearch|purge|purgefrom|purgeto} [options]");
			return;
		}
		if (args[0].equalsIgnoreCase("list")) {
			int page = 1;
			if (args.length > 1) {
				try {
					page = Integer.parseInt(args[1]);
				} catch (NumberFormatException e) {
					user.sendMessage(ChatColor.RED + args[1] + " isn't a number!");
					return;
				}
			}
			BenCmd.getReports().listTickets(user, page);
		} else if (args[0].equalsIgnoreCase("alist")) {
			int page = 1;
			if (args.length > 1) {
				try {
					page = Integer.parseInt(args[1]);
				} catch (NumberFormatException e) {
					user.sendMessage(ChatColor.RED + args[1] + " isn't a number!");
					return;
				}
			}
			BenCmd.getReports().listAllTickets(user, page);
		} else if (args[0].equalsIgnoreCase("purge")) {
			if (!user.hasPerm("bencmd.ticket.purge")) {
				user.sendMessage(ChatColor.RED + "You must be an admin to do that!");
				return;
			}
			BenCmd.getReports().PurgeOpen(user);
		} else if (args[0].equalsIgnoreCase("purgefrom")) {
			if (!user.hasPerm("bencmd.ticket.purge")) {
				user.sendMessage(ChatColor.RED + "You must be an admin to do that!");
				return;
			}
			if (args.length != 2) {
				user.sendMessage(ChatColor.YELLOW + "Proper use is /ticket purgefrom [name]");
				return;
			}
			BenCmd.getReports().PurgeFrom(user, args[1]);
		} else if (args[0].equalsIgnoreCase("purgeto")) {
			if (!user.hasPerm("bencmd.ticket.purge")) {
				user.sendMessage(ChatColor.RED + "You must be an admin to do that!");
				return;
			}
			if (args.length != 2) {
				user.sendMessage(ChatColor.YELLOW + "Proper use is /ticket purgeto [name]");
				return;
			}
			BenCmd.getReports().PurgeTo(user, args[1]);
		} else if (args[0].equalsIgnoreCase("search")) {
			if (!user.hasPerm("bencmd.ticket.search")) {
				user.sendMessage(ChatColor.RED + "You must be an admin to do that!");
				return;
			}
			if (args.length == 1) {
				user.sendMessage(ChatColor.YELLOW + "Proper use is /ticket search <name> [page]");
				return;
			}
			int page = 1;
			if (args.length == 3) {
				try {
					page = Integer.parseInt(args[2]);
				} catch (NumberFormatException e) {
					user.sendMessage(ChatColor.RED + args[2] + " isn't a number!");
					return;
				}
			}
			BenCmd.getReports().searchTickets(user, args[1], page);
		} else if (args[0].equalsIgnoreCase("asearch")) {
			if (!user.hasPerm("bencmd.ticket.asearch")) {
				user.sendMessage(ChatColor.RED + "You must be an admin to do that!");
				return;
			}
			if (args.length == 1) {
				user.sendMessage(ChatColor.YELLOW + "Proper use is /ticket asearch <name> [page]");
				return;
			}
			int page = 1;
			if (args.length == 3) {
				try {
					page = Integer.parseInt(args[2]);
				} catch (NumberFormatException e) {
					user.sendMessage(ChatColor.RED + args[2] + " isn't a number!");
					return;
				}
			}
			BenCmd.getReports().searchAllTickets(user, args[1], page);
		} else {
			Integer id;
			try {
				id = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				user.sendMessage(ChatColor.RED + args[0] + " is not a number!");
				return;
			}
			Report report = BenCmd.getReports().getTicketById(id);
			if (report == null) {
				user.sendMessage(ChatColor.RED + "Ticket #" + args[0] + " doesn't exist!");
				return;
			}
			if (!report.canRead(user)) {
				user.sendMessage(ChatColor.RED + "You can't read that ticket!");
				return;
			}
			if (args.length == 1) {
				for (String s : report.readReport(user.hasPerm("bencmd.ticket.editall"), user.getName().equals(report.getAccused()) && !user.hasPerm("bencmd.ticket.readall")).split("\n")) {
					user.sendMessage(s);
				}
			} else if (args[1].equalsIgnoreCase("close")) {
				if (user.hasPerm("bencmd.ticket.editall")) {
					if (report.getStatus() == Report.ReportStatus.LOCKED) {
						user.sendMessage(ChatColor.RED + "That ticket is locked!");
						return;
					}
					if (report.getStatus() == Report.ReportStatus.CLOSED) {
						user.sendMessage(ChatColor.RED + "That ticket is already closed!");
						return;
					}
					ReportCloseEvent e;
					Bukkit.getPluginManager().callEvent(e = new ReportCloseEvent(report, user));
					if (e.isCancelled()) {
						return;
					}
					if (args.length == 2) {
						report.closeTicket("Ticket closed by admin");
						BenCmd.log(user.getDisplayName() + " closed ticket #" + id.toString() + "!");
						user.sendMessage(ChatColor.GREEN + "Ticket (ID: " + report.getId() + ") has been successfully closed!");
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
						BenCmd.log(user.getDisplayName() + " closed ticket #" + id.toString() + "! Reason: " + reason);
						user.sendMessage(ChatColor.GREEN + "Ticket (ID: " + report.getId() + ") has been successfully closed!");
					}
				} else {
					if (report.canBasicChange(user)) {
						if (report.getStatus() == Report.ReportStatus.LOCKED) {
							user.sendMessage(ChatColor.RED + "That ticket is locked!");
							return;
						}
						if (report.getStatus() == Report.ReportStatus.CLOSED) {
							user.sendMessage(ChatColor.RED + "That ticket is already closed!");
							return;
						}
						ReportCloseEvent e;
						Bukkit.getPluginManager().callEvent(e = new ReportCloseEvent(report, user));
						if (e.isCancelled()) {
							return;
						}
						report.closeTicket("Ticket closed by user");
						user.sendMessage(ChatColor.GREEN + "Ticket (ID: " + report.getId() + ") has been successfully closed!");
						BenCmd.log(user.getDisplayName() + " closed ticket #" + id.toString() + "!");
					} else {
						user.sendMessage(ChatColor.RED + "You cannot edit that ticket!");
					}
				}
			} else if (args[1].equalsIgnoreCase("reopen")) {
				if (user.hasPerm("bencmd.ticket.editall")) {
					if (report.getStatus() == Report.ReportStatus.LOCKED) {
						user.sendMessage(ChatColor.RED + "That ticket is locked!");
						return;
					}
					if (report.getStatus() != Report.ReportStatus.CLOSED) {
						user.sendMessage(ChatColor.RED + "That ticket is already open!");
						return;
					}
					report.reopenTicket(true);
					BenCmd.log(user.getDisplayName() + " re-opened ticket #" + id.toString() + "!");
					user.sendMessage(ChatColor.GREEN + "That ticket has been re-opened!");
				} else if (report.canBasicChange(user)) {
					if (report.getStatus() == Report.ReportStatus.LOCKED) {
						user.sendMessage(ChatColor.RED + "That ticket is locked!");
						return;
					}
					if (report.getStatus() != Report.ReportStatus.CLOSED) {
						user.sendMessage(ChatColor.RED + "That ticket is already open!");
						return;
					}
					if (report.reopenTicket(false)) {
						BenCmd.log(user.getDisplayName() + " re-opened ticket #" + id.toString() + "!");
						user.sendMessage(ChatColor.GREEN + "That ticket has been re-opened!");
					} else {
						user.sendMessage(ChatColor.RED + "That ticket has been re-opened too many times!");
						user.sendMessage(ChatColor.RED + "Talk to an admin to have it re-opened!");
					}
				} else {
					user.sendMessage(ChatColor.RED + "You cannot edit that ticket!");
				}
			} else if (args[1].equalsIgnoreCase("lock")) {
				if (user.hasPerm("bencmd.ticket.lock") && report.canBasicChange(user)) {
					if (report.getStatus() == Report.ReportStatus.LOCKED) {
						user.sendMessage(ChatColor.RED + "That ticket is locked!");
						return;
					}
					Bukkit.getPluginManager().callEvent(new ReportLockEvent(report, user));
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
					user.sendMessage(ChatColor.GREEN + "That ticket has been locked!");
					BenCmd.log(user.getDisplayName() + " locked ticket #" + id.toString() + "!");
				} else {
					user.sendMessage(ChatColor.RED + "You must be an admin to do that!");
				}
			} else if (args[1].equalsIgnoreCase("inv")) {
				if (user.hasPerm("bencmd.ticket.investigate") && report.canBasicChange(user)) {
					if (report.getStatus() == Report.ReportStatus.LOCKED) {
						user.sendMessage(ChatColor.RED + "That ticket is locked!");
						return;
					}
					report.InvestigateTicket();
					user.sendMessage(ChatColor.GREEN + "That ticket has been marked as under investigation!");
					BenCmd.log(user.getDisplayName() + " is investigating #" + id.toString() + "!");
				} else {
					user.sendMessage(ChatColor.RED + "You must be an admin to do that!");
				}
			} else if (args[1].equalsIgnoreCase("uninv")) {
				if (user.hasPerm("bencmd.ticket.investigate") && report.canBasicChange(user)) {
					if (report.getStatus() == Report.ReportStatus.LOCKED) {
						user.sendMessage(ChatColor.RED + "That ticket is locked!");
						return;
					}
					report.UninvestigateTicket();
					user.sendMessage(ChatColor.GREEN + "That ticket has been marked as read!");
					BenCmd.log(user.getDisplayName() + " is no longer investigating #" + id.toString() + "!");
				} else {
					user.sendMessage(ChatColor.RED + "You must be an admin to do that!");
				}
			} else if (args[1].equalsIgnoreCase("addinfo")) {
				if (report.canBasicChange(user)) {
					if (report.getStatus() == Report.ReportStatus.LOCKED) {
						user.sendMessage(ChatColor.RED + "That ticket is locked!");
						return;
					}
					if (report.getStatus() == Report.ReportStatus.CLOSED && !user.hasPerm("bencmd.ticket.editall")) {
						user.sendMessage(ChatColor.RED + "That ticket is closed! Re-open it using /ticket " + report.getId() + " reopen to add new information.");
						return;
					}
					if (report.getStatus() == Report.ReportStatus.INVESTIGATING && !user.hasPerm("bencmd.ticket.editall")) {
						user.sendMessage(ChatColor.RED + "That ticket is under investigation and new info cannot be added. Ask an admin for more details...");
						return;
					}
					if (args.length == 2) {
						user.sendMessage(ChatColor.YELLOW + "Proper use is /ticket <id> addinfo <additional info>");
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
					user.sendMessage(ChatColor.GREEN + "The info has been added successfully!");
					BenCmd.log(user.getDisplayName() + " has added information to ticket #" + id.toString() + "!");
					if (user.hasPerm("bencmd.ticket.editall")) {
						return;
					}
					for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
						User onlineUser;
						if ((onlineUser = User.getUser(onlinePlayer)).hasPerm("bencmd.ticket.readall")) {
							if (BenCmd.isSpoutConnected() && BenCmd.getSpoutConnector().enabled(onlinePlayer)) {
								BenCmd.getSpoutConnector().sendNotification(onlinePlayer, "Report info added!", "Ticket ID: " + id, Material.PAPER);
							} else {
								onlineUser.sendMessage(ChatColor.RED + "Info has been added to a report! Use /ticket " + id + " to see details!");
							}
						}
					}
				} else {
					user.sendMessage(ChatColor.RED + "You cannot edit that ticket!");
				}
			} else {
				user.sendMessage(ChatColor.YELLOW + "Proper use is /ticket <id> [{close|reopen|lock|inv|uninv|addinfo}]");
			}
		}
	}

}
