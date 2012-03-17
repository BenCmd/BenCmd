package com.bendude56.bencmd.event.protect;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import com.bendude56.bencmd.User;
import com.bendude56.bencmd.protect.ProtectedBlock;

public final class ProtectionRemoveEvent extends ProtectionEvent implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	
	private boolean				cancelled			= false;

	public ProtectionRemoveEvent(ProtectedBlock protection, User user) {
		super(protection, user);
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
