package ben_dude56.plugins.bencmd.reporting;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import ben_dude56.plugins.bencmd.BenCmd;
import ben_dude56.plugins.bencmd.permissions.PermissionUser;

public class ReportFile extends Properties {

	private static final long serialVersionUID = 0L;
	private BenCmd plugin;
	private String proFile = "plugins/BenCmd/tickets.db";
	private List<Report> reports = new ArrayList<Report>();
	Logger log = Logger.getLogger("minecraft");
	
	public ReportFile(BenCmd instance) {
		plugin = instance;
		this.loadFile();
		this.loadTickets();
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
		if(!old.exists()) {
			old.createNewFile();
		}
		InputStream in = new FileInputStream(current);
	    OutputStream out = new FileOutputStream(old);
	    byte[] buf = new byte[1024];
	    int len;
	    while ((len = in.read(buf)) > 0){
	      out.write(buf, 0, len);
	    }
	    in.close();
	    out.close();
	}
	
	public void loadTickets() {
		reports.clear();
		for(int i = 0; i < this.values().size(); i++) {
			if(((String)this.values().toArray()[i]).split("/").length != 7) {
				try {
					Integer id = Integer.parseInt((String) this.keySet().toArray()[i]);
					log.info("Ticket #" + this.keySet().toArray()[i] + " is outdated! Attempting to upgrade...");
					reports.add(this.TicketUpgrade(id));
				} catch (Exception e) {
					try {
						copyToOld();
						log.warning("Ticket couldn't be updated! You can upgrade it manually in oldtickets.db...");
					} catch (Exception e1) {
						log.warning("Ticket couldn't be updated! You can upgrade it manually in tickets.db...");
					}
				}
				continue;
			}
			try {
				Integer id = Integer.parseInt((String) this.keySet().toArray()[i]);
				PermissionUser sender = PermissionUser.matchUserIgnoreCase(((String)this.values().toArray()[i]).split("/")[0], plugin);
				PermissionUser accused = PermissionUser.matchUserIgnoreCase(((String)this.values().toArray()[i]).split("/")[1], plugin);
				String reason = ((String)this.values().toArray()[i]).split("/")[2];
				Integer timesReopened = Integer.parseInt(((String)this.values().toArray()[i]).split("/")[3]);
				String finalRemark = ((String)this.values().toArray()[i]).split("/")[4];
				List<String> addedinfo = new ArrayList<String>();
				for(String addedinfostr : ((String)this.values().toArray()[i]).split("/")[5].split(",")) {
					addedinfo.add(addedinfostr);
				}
				String type = ((String)this.values().toArray()[i]).split("/")[6];
				Report.ReportStatus status;
				if (type.equalsIgnoreCase("u")) {
					status = Report.ReportStatus.UNREAD;
				} else if (type.equalsIgnoreCase("r")) {
					status = Report.ReportStatus.READ;
				} else if (type.equalsIgnoreCase("i")) {
					status = Report.ReportStatus.INVESTIGATING;
				} else if (type.equalsIgnoreCase("c")) {
					status = Report.ReportStatus.CLOSED;
				} else if (type.equalsIgnoreCase("l")) {
					status = Report.ReportStatus.LOCKED;
				} else {
					throw new Exception();
				}
				reports.add(new Report(plugin, id, sender, accused, status, reason, finalRemark, timesReopened, addedinfo));
			} catch (Exception e) {
				log.warning("A ticket in the tickets list couldn't be loaded!");
			}
		}
	}
	
	public Report TicketUpgrade(Integer id) throws Exception {
		Integer i = getIndexById(id);
		if(i == -1) {
			return null;
		}
		PermissionUser sender = PermissionUser.matchUserIgnoreCase(((String)this.values().toArray()[i]).split("/")[0], plugin);
		PermissionUser accused = PermissionUser.matchUserIgnoreCase(((String)this.values().toArray()[i]).split("/")[1], plugin);
		String reason = ((String)this.values().toArray()[i]).split("/")[2];
		Integer timesReopened = Integer.parseInt(((String)this.values().toArray()[i]).split("/")[3]);
		String finalRemark = ((String)this.values().toArray()[i]).split("/")[4];
		String type = ((String)this.values().toArray()[i]).split("/")[5];
		Report.ReportStatus status;
		if (type.equalsIgnoreCase("u")) {
			status = Report.ReportStatus.UNREAD;
		} else if (type.equalsIgnoreCase("r")) {
			status = Report.ReportStatus.READ;
		} else if (type.equalsIgnoreCase("i")) {
			status = Report.ReportStatus.INVESTIGATING;
		} else if (type.equalsIgnoreCase("c")) {
			status = Report.ReportStatus.CLOSED;
		} else if (type.equalsIgnoreCase("l")) {
			status = Report.ReportStatus.LOCKED;
		} else {
			throw new Exception();
		}
		return new Report(plugin, id, sender, accused, status, reason, finalRemark, timesReopened, new ArrayList<String>());
	}
	
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
	
	public void saveTickets() {
		this.clear();
		for(Report ticket : reports) {
			String key = ticket.getId().toString();
			String value = ticket.getSender().getName() + "/";
			value += ticket.getAccused().getName() + "/";
			value += ticket.getReason() + "/";
			value += ticket.getTimesReopened().toString() + "/";
			value += ticket.getRemark() + "/";
			boolean first = true;
			for(String info : ticket.getAddedInfo()) {
				if(first) {
					first = false;
					value += info;
				} else {
					value += "," + info;
				}
			}
			value += "/";
			switch(ticket.getStatus()) {
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
	
	public void addTicket(Report report) {
		reports.add(report);
		this.saveAll();
	}
	
	public Integer nextId() {
		for(int i = 0; true; i++) {
			if(getTicketById(i) == null) {
				return i;
			}
		}
	}
	
	public boolean unreadTickets() {
		for(Report ticket : reports) {
			if(ticket.getStatus() == Report.ReportStatus.UNREAD) {
				return true;
			}
		}
		return false;
	}
	
	public Report getTicketById(Integer id) {
		for(Report ticket : reports) {
			if(ticket.getId() == id) {
				return ticket;
			}
		}
		return null;
	}
	
	public Integer getIndexById(Integer id) {
		for(int i = 0; i < this.keySet().size(); i++) {
			if(this.keySet().toArray()[i].toString().equalsIgnoreCase(id.toString())) {
				return i;
			}
		}
		return -1;
	}
	
	public List<Report> getReports() {
		return reports;
	}
}
