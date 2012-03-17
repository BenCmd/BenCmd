package com.bendude56.bencmd.event.report;

import com.bendude56.bencmd.User;
import com.bendude56.bencmd.event.BenCmdEvent;
import com.bendude56.bencmd.reporting.Report;

public abstract class ReportEvent extends BenCmdEvent {
	private Report				report;
	private User				user;

	public ReportEvent(Report report, User user) {
		super();
		this.report = report;
		this.user = user;
	}

	public User getUser() {
		return user;
	}

	public Report getReport() {
		return report;
	}

}
