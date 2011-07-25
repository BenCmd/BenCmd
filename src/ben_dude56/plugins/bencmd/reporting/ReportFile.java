package ben_dude56.plugins.bencmd.reporting;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.bukkit.ChatColor;

import ben_dude56.plugins.bencmd.BenCmd;
import ben_dude56.plugins.bencmd.User;
import ben_dude56.plugins.bencmd.permissions.PermissionUser;
import ben_dude56.plugins.bencmd.reporting.Report.ReportStatus;

public class ReportFile extends Properties {

	private static final long serialVersionUID = 0L;
	private BenCmd plugin;
	private String proFile = "plugins/BenCmd/tickets.db";
	private HashMap<Integer, Report> reports = new HashMap<Integer, Report>();
	private List<Integer> unread = new ArrayList<Integer>();
	private List<Integer> open = new ArrayList<Integer>();

	public ReportFile(BenCmd instance) {
		plugin = instance;
		this.loadFile();
		this.loadTickets();
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

	public void loadFile() {
		File file = new File(proFile);
		if (file.exists()) {
			try {
				load(new FileInputStream(file));
			} catch (IOException e) {
				System.out.println("BenCmd had a problem:");
				e.printStackTrace();
			}
		}
	}

	public void copyToOld() throws IOException, FileNotFoundException {
		File current = new File(proFile);
		File old = new File("plugins/BenCmd/tickets.db");
		if (!old.exists()) {
			old.createNewFile();
		}
		InputStream in = new FileInputStream(current);
		OutputStream out = new FileOutputStream(old);
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}

	public void loadTickets() {
		reports.clear();
		for (int i = 0; i < this.values().size(); i++) {
			if (((String) this.values().toArray()[i]).split("/").length != 7) {
				try {
					Integer id = Integer.parseInt((String) this.keySet()
							.toArray()[i]);
					plugin.log.info("Ticket #" + this.keySet().toArray()[i]
							+ " is outdated! Attempting to upgrade...");
					plugin.bLog.info("Ticket #" + this.keySet().toArray()[i]
							+ " is outdated! Attempting to upgrade...");
					reports.put(id, this.TicketUpgrade(id));
				} catch (Exception e) {
					try {
						copyToOld();
						plugin.log
								.warning("Ticket couldn't be updated! You can upgrade it manually in oldtickets.db...");
						plugin.bLog
								.warning("Ticket couldn't be updated! You can upgrade it manually in oldtickets.db...");
					} catch (Exception e1) {
						plugin.log
								.warning("Ticket couldn't be updated! You can upgrade it manually in tickets.db...");
						plugin.bLog
								.warning("Ticket couldn't be updated! You can upgrade it manually in tickets.db...");
					}
				}
				continue;
			}
			try {
				Integer id = Integer
						.parseInt((String) this.keySet().toArray()[i]);
				PermissionUser sender = PermissionUser.matchUserIgnoreCase(
						((String) this.values().toArray()[i]).split("/")[0],
						plugin);
				PermissionUser accused = PermissionUser.matchUserIgnoreCase(
						((String) this.values().toArray()[i]).split("/")[1],
						plugin);
				String reason = ((String) this.values().toArray()[i])
						.split("/")[2];
				Integer timesReopened = Integer.parseInt(((String) this
						.values().toArray()[i]).split("/")[3]);
				String finalRemark = ((String) this.values().toArray()[i])
						.split("/")[4];
				List<String> addedinfo = new ArrayList<String>();
				for (String addedinfostr : ((String) this.values().toArray()[i])
						.split("/")[5].split(",")) {
					addedinfo.add(addedinfostr);
				}
				String type = ((String) this.values().toArray()[i]).split("/")[6];
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
				reports.put(id, new Report(plugin, id, sender, accused, status,
						reason, finalRemark, timesReopened, addedinfo));
			} catch (Exception e) {
				plugin.log
						.warning("A ticket in the tickets list couldn't be loaded!");
				plugin.bLog
						.warning("A ticket in the tickets list couldn't be loaded!");
			}
		}
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

	public Report TicketUpgrade(Integer id) throws Exception {
		Integer i = getIndexById(id);
		if (i == -1) {
			return null;
		}
		PermissionUser sender = PermissionUser.matchUserIgnoreCase(
				((String) this.values().toArray()[i]).split("/")[0], plugin);
		PermissionUser accused = PermissionUser.matchUserIgnoreCase(
				((String) this.values().toArray()[i]).split("/")[1], plugin);
		String reason = ((String) this.values().toArray()[i]).split("/")[2];
		Integer timesReopened = Integer.parseInt(((String) this.values()
				.toArray()[i]).split("/")[3]);
		String finalRemark = ((String) this.values().toArray()[i]).split("/")[4];
		String type = ((String) this.values().toArray()[i]).split("/")[5];
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
		return new Report(plugin, id, sender, accused, status, reason,
				finalRemark, timesReopened, new ArrayList<String>());
	}

	/**
	 * @deprecated Causes excessive lag
	 */
	public void saveAll() {
		saveTickets();
		saveFile("== BenCmd Tickets ==");
	}

	public void saveFile(String header) {
		File file = new File(proFile);
		if (file.exists()) {
			try {
				store(new FileOutputStream(file), header);
			} catch (IOException e) {
				System.out.println("BenCmd had a problem:");
				e.printStackTrace();
			}
		}
	}

	/**
	 * @deprecated Causes excessive lag
	 */
	public void saveTickets() {
		this.clear();
		for (Report ticket : reports.values()) {
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
			this.put(key, value);
		}
	}
	
	public void listTickets(final User user, final int page) {
		user.sendMessage(ChatColor.YELLOW
				+ "Please wait... The report databases are being queried...");
		new Thread() {
			public void run() {
				List<Report> results;
				if (user.hasPerm("isTicketAdmin")) {
					results = new ArrayList<Report>(getOpen());
				} else {
					results = new ArrayList<Report>();
					for (Report ticket : getOpen()) {
						if (ticket.getAccused().getName().equalsIgnoreCase(user.getName())
								|| ticket.getSender().getName()
									.equalsIgnoreCase(user.getName())) {
							results.add(ticket);
						}
					}
				}
				try {
					if (results.isEmpty()) {
						user.sendMessage(ChatColor.RED
								+ "Your search returned no OPEN results!");
					} else {
						user.sendMessage(ChatColor.GRAY
								+ "Your search returned the following OPEN results:");
						showTickets(results, page, user);
					}
				} catch (NullPointerException e) {
					// The user logged off during the search, take no action
				}
			}
		}.start();
	}
	
	public void listAllTickets(final User user, final int page) {
		user.sendMessage(ChatColor.YELLOW
				+ "Please wait... The report databases are being queried...");
		new Thread() {
			public void run() {
				List<Report> results;
				if (user.hasPerm("isTicketAdmin")) {
					results = new ArrayList<Report>(getReports());
				} else {
					results = new ArrayList<Report>();
					for (Report ticket : getReports()) {
						if (ticket.getAccused().getName().equalsIgnoreCase(user.getName())
								|| ticket.getSender().getName()
									.equalsIgnoreCase(user.getName())) {
							results.add(ticket);
						}
					}
				}
				try {
					if (results.isEmpty()) {
						user.sendMessage(ChatColor.RED
								+ "Your search returned no results!");
					} else {
						user.sendMessage(ChatColor.GRAY
								+ "Your search returned the following results:");
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
				user.sendMessage(ChatColor.RED + r.readShorthand());
				break;
			case READ:
				user.sendMessage(ChatColor.YELLOW
						+ r.readShorthand());
				break;
			case INVESTIGATING:
				user.sendMessage(ChatColor.GREEN
						+ r.readShorthand());
				break;
			case CLOSED:
				user.sendMessage(ChatColor.GRAY
						+ r.readShorthand());
				break;
			case LOCKED:
				user.sendMessage(ChatColor.DARK_GRAY
						+ r.readShorthand());
				break;
			}
		}
	}

	public void saveTicket(Report ticket) {
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
		this.put(key, value);
		saveFile("== BenCmd Tickets ==");
		if (ticket.getStatus() == ReportStatus.UNREAD) {
			setUnread(ticket);
		} else {
			setRead(ticket);
		}
		if (ticket.getStatus() == ReportStatus.CLOSED || ticket.getStatus() == ReportStatus.LOCKED){
			setClosed(ticket);
		} else {
			setOpen(ticket);
		}
	}
	
	public void searchTickets(final User user, final String search, final int page) {
		user.sendMessage(ChatColor.YELLOW
				+ "Please wait... The report databases are being queried...");
		new Thread() {
			public void run() {
				List<Report> results = new ArrayList<Report>();
				for (Report ticket : getOpen()) {
					if (ticket.getAccused().getName().equalsIgnoreCase(search)
							|| ticket.getSender().getName()
									.equalsIgnoreCase(search)) {
						results.add(ticket);
					}
				}
				try {
					if (results.isEmpty()) {
						user.sendMessage(ChatColor.RED
								+ "Your search returned no OPEN results!");
					} else {
						user.sendMessage(ChatColor.GRAY
								+ "Your search returned the following OPEN results:");
						showTickets(results, page, user);
					}
				} catch (NullPointerException e) {
					// The user logged off during the search, take no action
				}
			}
		}.start();
	}

	public void searchAllTickets(final User user, final String search, final int page) {
		user.sendMessage(ChatColor.YELLOW
				+ "Please wait... The report databases are being queried...");
		new Thread() {
			public void run() {
				for (long i = 0; i < 10000000000L; i++) { }
				List<Report> results = new ArrayList<Report>();
				for (Report ticket : getReports()) {
					if (ticket.getAccused().getName().equalsIgnoreCase(search)
							|| ticket.getSender().getName()
									.equalsIgnoreCase(search)) {
						results.add(ticket);
					}
				}
				try {
					if (results.isEmpty()) {
						user.sendMessage(ChatColor.RED
								+ "Your search returned no results!");
					} else {
						user.sendMessage(ChatColor.GRAY
								+ "Your search returned the following results:");
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
		this.saveTicket(report);
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
		for (int i = 0; i < this.keySet().size(); i++) {
			if (this.keySet().toArray()[i].toString().equalsIgnoreCase(
					id.toString())) {
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
