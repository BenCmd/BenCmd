package com.bendude56.bencmd.event.protect;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import com.bendude56.bencmd.User;
import com.bendude56.bencmd.permissions.PermissionUser;
import com.bendude56.bencmd.protect.ProtectedBlock;

public final class ProtectionEditEvent extends ProtectionEvent implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	
	private boolean				cancelled			= false;
	private PermissionUser		changee;
	private ChangeType			cType;

	public ProtectionEditEvent(ProtectedBlock protection, User user, ChangeType type, PermissionUser changeTo) {
		super(protection, user);
		changee = changeTo;
		cType = type;
	}

	public PermissionUser getChangee() {
		return changee;
	}

	public ChangeType getChangeType() {
		return cType;
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

	public enum ChangeType {
		REMOVE_GUEST, ADD_GUEST, SET_OWNER
	}

}
