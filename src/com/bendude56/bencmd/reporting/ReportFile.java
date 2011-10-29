package com.bendude56.bencmd.reporting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.BenCmdFile;
import com.bendude56.bencmd.User;
import com.bendude56.bencmd.permissions.PermissionUser;
import com.bendude56.bencmd.reporting.Report.ReportStatus;

public class ReportFile extends BenCmdFile {
	private HashMap<Integer, Report>	reports	= new HashMap<Integer, Report>();
	private List<Integer>				unread	= new ArrayList<Integer>();
	private List<Integer>				open	= new ArrayList<Integer>();

	public ReportFile() {
		super("tickets.db", "--BenCmd Ticket File--", true);
		loadFile();
		loadAll();
	}

	public void setRead(Report r) {
		if (unread.contains(r.getId())) {
			unread.remove(r.getId());
		}
	}

	public void setUnread(Report r) {
		if (!unread.contains(r.getId())) {
			unread.add(r.getId());
		}
	}

	public void setClosed(Report r) {
		if (open.contains(r.getId())) {
			open.remove(r.getId());
		}
	}

	public void setOpen(Report r) {
		if (!open.contains(r.getId())) {
			open.add(r.getId());
		}
	}

	public void loadAll() {
		reports.clear();
		// TODO Make this more efficient
		for (int i = 0; i < getFile().values().size(); i++) {
			if (((String) getFile().values().toArray()[i]).split("/").length != 7) {
				BenCmd.log(Level.WARNING, "Ticket #" + ((String) getFile().keySet().toArray()[i]) + " is outdated... It must be upgraded manually!");
				continue;
			}
			try {
				Integer id = Integer.parseInt((String) getFile().keySet().toArray()[i]);
				PermissionUser sender = PermissionUser.matchUserIgnoreCase(((String) getFile().values().toArray()[i]).split("/")[0]);
				PermissionUser accused = PermissionUser.matchUserIgnoreCase(((String) getFile().values().toArray()[i]).split("/")[1]);
				String reason = ((String) getFile().values().toArray()[i]).split("/")[2];
				Integer timesReopened = Integer.parseInt(((String) getFile().values().toArray()[i]).split("/")[3]);
				String finalRemark = ((String) getFile().values().toArray()[i]).split("/")[4];
				List<String> addedinfo = new ArrayList<String>();
				for (String addedinfostr : ((String) getFile().values().toArray()[i]).split("/")[5].split(",")) {
					addedinfo.add(addedinfostr);
				}
				String type = ((String) getFile().values().toArray()[i]).split("/")[6];
				Report.ReportStatus status;
				if (type.equalsIgnoreCase("u")) {
					status = Report.ReportStatus.UNREAD;
					unread.add(id);
					open.add(id);
				} else if (type.equalsIgnoreCase("r")) {
					status = Report.ReportStatus.READ;
					open.add(id);
				} else if (type.equalsIgnoreCase("i")) {
					status = Report.ReportStatus.INVESTIGATING;
					open.add(id);
				} else if (type.equalsIgnoreCase("c")) {
					status = Report.ReportStatus.CLOSED;
				} else if (type.equalsIgnoreCase("l")) {
					status = Report.ReportStatus.LOCKED;
				} else {
					throw new Exception();
				}
				reports.put(id, new Report(id, sender, accused, status, reason, finalRemark, timesReopened, addedinfo));
			} catch (Exception e) {
				BenCmd.log(Level.WARNING, "A ticket in the tickets list couldn't be loaded!");
			}
		}
	}

	public void saveAll() {
		for (Map.Entry<Integer, Report> e : reports.entrySet()) {
			saveTicket(e.getValue(), false);
		}
		saveFile();
	}

	public void PurgeOpen(final User user) {
		user.sendMessage(ChatColor.YELLOW + "Please wait... Ticket purge in progress...");
		new Thread() {
			public void run() {
				for (Report r : getOpen()) {
					r.closeTicket("Ticket purged by admin");
				}
				user.sendMessage(ChatColor.GREEN + "All tickets purged successfully!");
			}
		}.start();
	}

	public void PurgeFrom(final User user, final String toPurge) {
		user.sendMessage(ChatColor.YELLOW + "Please wait... Ticket purge in progress...");
		new Thread() {
			public void run() {
				for (Report r : getOpen()) {
					if (r.getSender().getName().equals(toPurge)) {
						r.closeTicket("Ticket purged by admin");
					}
				}
				user.sendMessage(ChatColor.GREEN + "All tickets from " + toPurge + " purged successfully!");
			}
		}.start();
	}

	public void PurgeTo(final User user, final String toPurge) {
		user.sendMessage(ChatColor.YELLOW + "Please wait... Ticket purge in progress...");
		new Thread() {
			public void run() {
				for (Report r : getOpen()) {
					if (r.getAccused().getName().equals(toPurge)) {
						r.closeTicket("Ticket purged by admin");
					}
				}
				user.sendMessage(ChatColor.GREEN + "All tickets against " + toPurge + " purged successfully!");
			}
		}.start();
	}

	public void listTickets(final User user, final int page) {
		user.sendMessage(ChatColor.YELLOW + "Please wait... The report databases are being queried...");
		new Thread() {
			public void run() {
				List<Report> results;
				if (user.hasPerm("bencmd.ticket.list")) {
					results = new ArrayList<Report>(getOpen());
				} else if (user.hasPerm("bencmd.ticket.readown")) {
					results = new ArrayList<Report>();
					for (Report ticket : getOpen()) {
						if (ticket.getAccused().getName().equalsIgnoreCase(user.getName()) || ticket.getSender().getName().equalsIgnoreCase(user.getName())) {
							results.add(ticket);
						}
					}
				} else {
					user.sendMessage(ChatColor.RED + "You don't have permission to do that!");
					BenCmd.getPlugin().logPermFail();
					return;
				}
				try {
					if (results.isEmpty()) {
						user.sendMessage(ChatColor.RED + "Your search returned no OPEN results!");
					} else {
						user.sendMessage(ChatColor.GRAY + "Your search returned the following OPEN results:");
						showTickets(results, page, user);
					}
				} catch (NullPointerException e) {
					// The user logged off during the search, take no action
				}
			}
		}.start();
	}

	public void listAllTickets(final User user, final int page) {
		user.sendMessage(ChatColor.YELLOW + "Please wait... The report databases are being queried...");
		new Thread() {
			public void run() {
				List<Report> results;
				if (user.hasPerm("bencmd.ticket.alist")) {
					results = new ArrayList<Report>(getReports());
				} else if (user.hasPerm("bencmd.ticket.readown")) {
					results = new ArrayList<Report>();
					for (Report ticket : getReports()) {
						if (ticket.getAccused().getName().equalsIgnoreCase(user.getName()) || ticket.getSender().getName().equalsIgnoreCase(user.getName())) {
							results.add(ticket);
						}
					}
				} else {
					user.sendMessage(ChatColor.RED + "You don't have permission to do that!");
					BenCmd.getPlugin().logPermFail();
					return;
				}
				Collections.sort(results);
				try {
					if (results.isEmpty()) {
						user.sendMessage(ChatColor.RED + "Your search returned no results!");
					} else {
						user.sendMessage(ChatColor.GRAY + "Your search returned the following results:");
						showTickets(results, page, user);
					}
				} catch (NullPointerException e) {
					// The user logged off during the search, take no action
				}
			}
		}.start();
	}

	public void showTickets(List<Report> reports, int page, User user) {
		HashMap<Integer, List<Report>> pages = new HashMap<Integer, List<Report>>();
		int cpage = 1;
		for (Report r : reports) {
			if (pages.containsKey(cpage)) {
				pages.get(cpage).add(r);
				if (pages.get(cpage).size() == 10) {
					cpage++;
				}
			} else {
				List<Report> t = new ArrayList<Report>();
				t.add(r);
				pages.put(cpage, t);
			}
		}
		if (!pages.containsKey(cpage)) {
			cpage--;
		}
		if (page <= 0) {
			user.sendMessage(ChatColor.RED + "There can be no page lower than 1.");
			return;
		}
		if (page > cpage) {
			user.sendMessage(ChatColor.RED + "There are only " + cpage + " pages...");
			return;
		}
		if (cpage != 1) {
			user.sendMessage(ChatColor.GRAY + "Page " + ChatColor.GREEN + page + ChatColor.GRAY + "/" + ChatColor.GREEN + cpage);
		}
		for (Report r : pages.get(page)) {
			switch (r.getStatus()) {
				case UNREAD:
					if (user.getName().equals(r.getAccused().getName()) && !user.hasPerm("bencmd.ticket.readall")) {
						user.sendMessage(ChatColor.RED + r.readShorthandAnon());
					} else {
						user.sendMessage(ChatColor.RED + r.readShorthand());
					}
					break;
				case READ:
					if (user.getName().equals(r.getAccused().getName()) && !user.hasPerm("bencmd.ticket.readall")) {
						user.sendMessage(ChatColor.YELLOW + r.readShorthandAnon());
					} else {
						user.sendMessage(ChatColor.YELLOW + r.readShorthand());
					}
					break;
				case INVESTIGATING:
					if (user.getName().equals(r.getAccused().getName()) && !user.hasPerm("bencmd.ticket.readall")) {
						user.sendMessage(ChatColor.GREEN + r.readShorthandAnon());
					} else {
						user.sendMessage(ChatColor.GREEN + r.readShorthand());
					}
					break;
				case CLOSED:
					if (user.getName().equals(r.getAccused().getName()) && !user.hasPerm("bencmd.ticket.readall")) {
						user.sendMessage(ChatColor.GRAY + r.readShorthandAnon());
					} else {
						user.sendMessage(ChatColor.GRAY + r.readShorthand());
					}
					break;
				case LOCKED:
					if (user.getName().equals(r.getAccused().getName()) && !user.hasPerm("bencmd.ticket.readall")) {
						user.sendMessage(ChatColor.DARK_GRAY + r.readShorthandAnon());
					} else {
						user.sendMessage(ChatColor.DARK_GRAY + r.readShorthand());
					}
					break;
			}
		}
	}

	public void saveTicket(Report ticket, boolean saveFile) {
		String key = ticket.getId().toString();
		String value = ticket.getSender().getName() + "/";
		value += ticket.getAccused().getName() + "/";
		value += ticket.getReason() + "/";
		value += ticket.getTimesReopened().toString() + "/";
		value += ticket.getRemark() + "/";
		boolean first = true;
		for (String info : ticket.getAddedInfo()) {
			if (first) {
				first = false;
				value += info;
			} else {
				value += "," + info;
			}
		}
		value += "/";
		switch (ticket.getStatus()) {
			case UNREAD:
				value += "u";
				break;
			case READ:
				value += "r";
				break;
			case INVESTIGATING:
				value += "i";
				break;
			case CLOSED:
				value += "c";
				break;
			case LOCKED:
				value += "l";
				break;
		}
		getFile().put(key, value);
		if (ticket.getStatus() == ReportStatus.UNREAD) {
			setUnread(ticket);
		} else {
			setRead(ticket);
		}
		if (ticket.getStatus() == ReportStatus.CLOSED || ticket.getStatus() == ReportStatus.LOCKED) {
			setClosed(ticket);
		} else {
			setOpen(ticket);
		}
		if (saveFile)
			saveFile();
	}

	public void searchTickets(final User user, final String search, final int page) {
		user.sendMessage(ChatColor.YELLOW + "Please wait... The report databases are being queried...");
		new Thread() {
			public void run() {
				List<Report> results = new ArrayList<Report>();
				for (Report ticket : getOpen()) {
					if (ticket.getAccused().getName().equalsIgnoreCase(search) || ticket.getSender().getName().equalsIgnoreCase(search)) {
						results.add(ticket);
					}
				}
				try {
					if (results.isEmpty()) {
						user.sendMessage(ChatColor.RED + "Your search returned no OPEN results!");
					} else {
						user.sendMessage(ChatColor.GRAY + "Your search returned the following OPEN results:");
						showTickets(results, page, user);
					}
				} catch (NullPointerException e) {
					// The user logged off during the search, take no action
				}
			}
		}.start();
	}

	public void searchAllTickets(final User user, final String search, final int page) {
		user.sendMessage(ChatColor.YELLOW + "Please wait... The report databases are being queried...");
		new Thread() {
			public void run() {
				List<Report> results = new ArrayList<Report>();
				for (Report ticket : getReports()) {
					if (ticket.getAccused().getName().equalsIgnoreCase(search) || ticket.getSender().getName().equalsIgnoreCase(search)) {
						results.add(ticket);
					}
				}
				try {
					if (results.isEmpty()) {
						user.sendMessage(ChatColor.RED + "Your search returned no results!");
					} else {
						user.sendMessage(ChatColor.GRAY + "Your search returned the following results:");
						showTickets(results, page, user);
					}
				} catch (NullPointerException e) {
					// The user logged off during the search, take no action
				}
			}
		}.start();
	}

	public void addTicket(Report report) {
		reports.put(report.getId(), report);
		saveTicket(report, true);
	}

	public Integer nextId() {
		for (int i = 0; true; i++) {
			if (getTicketById(i) == null) {
				return i;
			}
		}
	}

	public boolean unreadTickets() {
		return (unread.size() != 0);
	}

	public Report getTicketById(Integer id) {
		if (reports.containsKey(id)) {
			return reports.get(id);
		} else {
			return null;
		}
	}

	public Integer getIndexById(Integer id) {
		for (int i = 0; i < getFile().keySet().size(); i++) {
			if (getFile().keySet().toArray()[i].toString().equalsIgnoreCase(id.toString())) {
				return i;
			}
		}
		return -1;
	}

	public List<Report> getOpen() {
		List<Report> o = new ArrayList<Report>();
		for (Integer i : open) {
			o.add(getTicketById(i));
		}
		return o;
	}

	public List<Report> getReports() {
		return new ArrayList<Report>(reports.values());
	}
}
