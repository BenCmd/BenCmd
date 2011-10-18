package com.bendude56.bencmd.event.protect;

import com.bendude56.bencmd.User;
import com.bendude56.bencmd.event.BenCmdEvent;
import com.bendude56.bencmd.protect.ProtectedBlock;

public abstract class ProtectionEvent extends BenCmdEvent {
	private static final long serialVersionUID = 0L;
	
	private ProtectedBlock block;
	private User user;

	public ProtectionEvent(String name, ProtectedBlock protection, User user) {
		super(name);
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
