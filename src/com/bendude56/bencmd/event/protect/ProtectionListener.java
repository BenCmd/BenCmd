package com.bendude56.bencmd.event.protect;

import org.bukkit.event.CustomEventListener;
import org.bukkit.event.Event;

public abstract class ProtectionListener extends CustomEventListener {
	
	public void onProtectionAdd(ProtectionAddEvent event) {
	}
	
	public void onProtectionRemove(ProtectionRemoveEvent event) {
	}
	
	public void onProtectionEdit(ProtectionEditEvent event) {
	}

	public void onCustomEvent(Event event) {
		if (event instanceof ProtectionAddEvent) {
			onProtectionAdd((ProtectionAddEvent) event);
		} else if (event instanceof ProtectionRemoveEvent) {
			onProtectionRemove((ProtectionRemoveEvent) event);
		} else if (event instanceof ProtectionEditEvent) {
			onProtectionEdit((ProtectionEditEvent) event);
		}
	}

}
