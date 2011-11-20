package com.bendude56.bencmd.event.report;

import org.bukkit.event.CustomEventListener;
import org.bukkit.event.Event;

public abstract class ReportListener extends CustomEventListener {

	public void onReportClose(ReportCloseEvent event) {}

	public void onReportCreate(ReportCreateEvent event) {}

	public void onReportLock(ReportLockEvent event) {}

	public void onCustomEvent(Event event) {
		if (event instanceof ReportCloseEvent) {
			onReportClose((ReportCloseEvent) event);
		} else if (event instanceof ReportCreateEvent) {
			onReportCreate((ReportCreateEvent) event);
		} else if (event instanceof ReportLockEvent) {
			onReportLock((ReportLockEvent) event);
		}
	}

}
