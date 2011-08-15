package ben_dude56.plugins.bencmd.reporting;

import java.util.List;

import org.bukkit.ChatColor;

import ben_dude56.plugins.bencmd.BenCmd;
import ben_dude56.plugins.bencmd.permissions.PermissionUser;

public class Report implements Comparable<Report> {
	private BenCmd plugin;
	private Integer idNumber;
	private PermissionUser sender;
	private PermissionUser accused;
	private String finalRemark;
	private ReportStatus status;
	private String reason;
	private Integer timesReopened;
	private List<String> addedInfo;

	public Report(BenCmd instance, Integer ID, PermissionUser sender,
			PermissionUser accused, ReportStatus status, String reason,
			String finalRemark, Integer timesReopened, List<String> addedInfo) {
		this.plugin = instance;
		this.idNumber = ID;
		this.sender = sender;
		this.accused = accused;
		this.status = status;
		this.finalRemark = finalRemark;
		this.reason = reason;
		this.timesReopened = timesReopened;
		this.addedInfo = addedInfo;
	}

	public PermissionUser getAccused() {
		return this.accused;
	}

	public PermissionUser getSender() {
		return this.sender;
	}

	public ReportStatus getStatus() {
		return this.status;
	}

	public Integer getId() {
		return this.idNumber;
	}

	public String getReason() {
		return reason;
	}

	public Integer getTimesReopened() {
		return timesReopened;
	}

	public String getRemark() {
		return finalRemark;
	}

	public List<String> getAddedInfo() {
		return addedInfo;
	}

	public void addInfo(String newInfo) {
		if (addedInfo.isEmpty() || addedInfo.get(0).isEmpty()) {
			addedInfo.clear();
		}
		addedInfo.add(newInfo);
		plugin.reports.saveTicket(this);
	}

	public void setStatus(ReportStatus newStatus) {
		this.status = newStatus;
		plugin.reports.saveTicket(this);
	}

	public String readReport(Boolean isAdmin) {
		if (this.status == ReportStatus.UNREAD && isAdmin) {
			this.status = ReportStatus.READ;
			plugin.reports.saveTicket(this);
		}
		String message = "";
		message += ChatColor.GRAY + "(" + this.status.toString() + ") "
				+ this.idNumber.toString() + "\n";
		message += ChatColor.GRAY + this.sender.getName() + " reported "
				+ this.accused.getName() + "!\n";
		message += ChatColor.GRAY + "Reasoning: " + this.reason;
		if (!this.addedInfo.isEmpty() && !this.addedInfo.get(0).isEmpty()) {
			message += "\n" + ChatColor.GRAY + "Added info:";
			for (String info : addedInfo) {
				message += "\n" + ChatColor.GRAY + "  -" + info;
			}
		}
		if (this.status == ReportStatus.CLOSED
				|| this.status == ReportStatus.LOCKED) {
			message += "\n" + ChatColor.GRAY + "Closing remark: "
					+ this.finalRemark;
		}
		return message;
	}

	public String readShorthand() {
		String message = "";
		message = "(" + this.status.toString() + ") "
				+ this.idNumber.toString() + " : " + this.sender.getName()
				+ " -> " + this.accused.getName();
		return message;
	}

	public boolean reopenTicket(boolean isAdmin) {
		if (isAdmin) {
			this.status = ReportStatus.READ;
		} else {
			if (timesReopened < plugin.mainProperties.getInteger(
					"ticketMaxReopen", 1)) {
				timesReopened += 1;
				this.status = ReportStatus.UNREAD;
			} else {
				return false;
			}
		}
		plugin.reports.saveTicket(this);
		return true;
	}

	public void InvestigateTicket() {
		this.status = ReportStatus.INVESTIGATING;
		plugin.reports.saveTicket(this);
	}

	public void UninvestigateTicket() {
		this.status = ReportStatus.READ;
		plugin.reports.saveTicket(this);
	}

	public void closeTicket(String finalRemark) {
		this.finalRemark = finalRemark;
		this.status = ReportStatus.CLOSED;
		plugin.reports.saveTicket(this);
	}

	public void lockTicket(String finalRemark) {
		this.finalRemark = finalRemark;
		this.status = ReportStatus.LOCKED;
		plugin.reports.saveTicket(this);
	}

	public boolean canRead(PermissionUser user) {
		return (user.getName().equalsIgnoreCase(accused.getName()) && user.hasPerm("bencmd.ticket.readown"))
				|| (user.getName().equalsIgnoreCase(sender.getName()) && user.hasPerm("bencmd.ticket.readown"))
				|| user.hasPerm("bencmd.ticket.readall");
	}

	public boolean canBasicChange(PermissionUser user) {
		return (user.getName().equalsIgnoreCase(sender.getName()) && user.hasPerm("bencmd.ticket.editown"))
				|| user.hasPerm("bencmd.ticket.editall");
	}

	public enum ReportStatus {
		UNREAD, READ, INVESTIGATING, CLOSED, LOCKED
	}

	@Override
	public int compareTo(Report r) {
		if (r.getId() > idNumber) {
			return -1;
		} else if (r.getId() < idNumber) {
			return 1;
		} else {
			return 0;
		}
	}
}
