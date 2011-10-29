package com.bendude56.bencmd.event.protect;

import org.bukkit.event.Cancellable;

import com.bendude56.bencmd.User;
import com.bendude56.bencmd.permissions.PermissionUser;
import com.bendude56.bencmd.protect.ProtectedBlock;

public final class ProtectionEditEvent extends ProtectionEvent implements Cancellable {
	private static final long	serialVersionUID	= 0L;
	private boolean				cancelled			= false;
	private PermissionUser		changee;
	private ChangeType			cType;

	public ProtectionEditEvent(ProtectedBlock protection, User user, ChangeType type, PermissionUser changeTo) {
		super("ProtectionEditEvent", protection, user);
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

	public enum ChangeType {
		REMOVE_GUEST, ADD_GUEST, SET_OWNER
	}

}
