package com.bendude56.bencmd.event;

import org.bukkit.event.Event;

import com.bendude56.bencmd.BenCmd;

public abstract class BenCmdEvent extends Event {
	public BenCmdEvent() {
		super();
	}

	public BenCmd getPlugin() {
		return BenCmd.getPlugin();
	}
}
