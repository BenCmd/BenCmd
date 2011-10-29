package com.bendude56.bencmd.event;

import org.bukkit.event.Event;

import com.bendude56.bencmd.BenCmd;

public abstract class BenCmdEvent extends Event {
	private static final long	serialVersionUID	= 0L;

	public BenCmdEvent(String name) {
		super(name);
	}

	public BenCmd getPlugin() {
		return BenCmd.getPlugin();
	}
}
