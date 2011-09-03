package com.bendude56.bencmd.event.protect;

import org.bukkit.event.Cancellable;

import com.bendude56.bencmd.User;
import com.bendude56.bencmd.protect.ProtectedBlock;

public class ProtectionAddEvent extends ProtectionEvent implements Cancellable {
	private static final long serialVersionUID = 0L;
	private boolean cancelled = false;

	public ProtectionAddEvent(ProtectedBlock protection, User user) {
		super("ProtectionAddEvent", protection, user);
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
