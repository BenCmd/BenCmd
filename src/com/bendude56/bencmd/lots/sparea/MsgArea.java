package com.bendude56.bencmd.lots.sparea;

import org.bukkit.ChatColor;
import org.bukkit.Location;

import com.bendude56.bencmd.BenCmd;


public class MsgArea extends SPArea {
	private String emsg;
	private String lmsg;

	public MsgArea(String key, String value)
			throws NumberFormatException, NullPointerException,
			IndexOutOfBoundsException {
		super(key, value);
		emsg = value.split("/")[3].replace('`', '/');
		if (emsg.equals("/")) {
			emsg = "";
		}
		lmsg = value.split("/")[4].replace('`', '/');
		if (lmsg.equals("/")) {
			lmsg = "";
		}
	}

	public MsgArea(Integer id, Location corner1,
			Location corner2, String enter, String leave) {
		super(id, corner1, corner2);
		emsg = enter;
		if (!emsg.startsWith("§")) {
			emsg = ChatColor.YELLOW + emsg;
		}
		lmsg = leave;
		if (!lmsg.startsWith("§")) {
			lmsg = ChatColor.YELLOW + lmsg;
		}
	}

	public String getEnterMessage() {
		return emsg;
	}

	public String getLeaveMessage() {
		return lmsg;
	}

	public void setEnterMessage(String value) {
		if (!value.startsWith("§")) {
			value = ChatColor.YELLOW + value;
		}
		emsg = value;
		BenCmd.getAreas().updateArea(this, true);
	}

	public void setLeaveMessage(String value) {
		if (!value.startsWith("§")) {
			value = ChatColor.YELLOW + value;
		}
		lmsg = value;
		BenCmd.getAreas().updateArea(this, true);
	}

	public String getValue() {
		String value = "msg" + super.getInternalValue();
		value += emsg.replace('/', '`') + "/";
		value += lmsg.replace('/', '`');
		return value;
	}

	protected String getInternalValue() {
		String value = super.getInternalValue();
		value += (emsg.equals("") ? "`" : emsg.replace('/', '`')) + "/";
		value += lmsg.equals("") ? "`" : lmsg.replace('/', '`');
		return value;
	}
}
