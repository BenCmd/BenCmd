package com.bendude56.bencmd.event.report;

import com.bendude56.bencmd.User;
import com.bendude56.bencmd.event.BenCmdEvent;
import com.bendude56.bencmd.reporting.Report;

public abstract class ReportEvent extends BenCmdEvent {
	private static final long	serialVersionUID	= 0L;

	private Report				report;
	private User				user;

	public ReportEvent(String name, Report report, User user) {
		super(name);
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
