package com.bendude56.bencmd.event.report;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import com.bendude56.bencmd.User;
import com.bendude56.bencmd.reporting.Report;

public final class ReportCreateEvent extends ReportEvent implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	
	private boolean				cancelled			= false;

	public ReportCreateEvent(Report report, User user) {
		super(report, user);
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
}
