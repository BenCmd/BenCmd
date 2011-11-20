package com.bendude56.bencmd.event.report;

import org.bukkit.event.Cancellable;

import com.bendude56.bencmd.User;
import com.bendude56.bencmd.reporting.Report;

public final class ReportCloseEvent extends ReportEvent implements Cancellable {
	private static final long	serialVersionUID	= 0L;
	private boolean				cancelled			= false;

	public ReportCloseEvent(Report report, User user) {
		super("ReportCloseEvent", report, user);
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
}
