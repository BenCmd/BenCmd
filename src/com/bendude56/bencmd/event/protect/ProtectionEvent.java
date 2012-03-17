package com.bendude56.bencmd.event.protect;

import com.bendude56.bencmd.User;
import com.bendude56.bencmd.event.BenCmdEvent;
import com.bendude56.bencmd.protect.ProtectedBlock;

public abstract class ProtectionEvent extends BenCmdEvent {
	private ProtectedBlock		block;
	private User				user;

	public ProtectionEvent(ProtectedBlock protection, User user) {
		super();
		block = protection;
		this.user = user;
	}

	public ProtectedBlock getProtection() {
		return block;
	}

	public User getUser() {
		return user;
	}

}
