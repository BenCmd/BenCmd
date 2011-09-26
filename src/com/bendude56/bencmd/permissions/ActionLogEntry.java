package com.bendude56.bencmd.permissions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ActionLogEntry implements Comparable<ActionLogEntry> {
	private String username;
	private String sender;
	private Date time;
	private ActionLogType type;
	private String note;
	private long dur;
	
	public ActionLogEntry(String entryData) {
		time = new Date(Long.parseLong(entryData.split(":")[0]));
		username = entryData.split(":")[1];
		sender = entryData.split(":")[2];
		String t = entryData.split(":")[3];
		if (t.equals("mt")) {
			type = ActionLogType.MUTE_TEMP;
		} else if (t.equals("mp")) {
			type = ActionLogType.MUTE_PERM;
		} else if (t.equals("umm")) {
			type = ActionLogType.UNMUTE_MAN;
		} else if (t.equals("uma")) {
			type = ActionLogType.UNMUTE_AUTO;
		} else if (t.equals("jt")) {
			type = ActionLogType.JAIL_TEMP;
		} else if (t.equals("jp")) {
			type = ActionLogType.JAIL_PERM;
		} else if (t.equals("ujm")) {
			type = ActionLogType.UNJAIL_MAN;
		} else if (t.equals("uja")) {
			type = ActionLogType.UNJAIL_AUTO;
		} else if (t.equals("bt")) {
			type = ActionLogType.BAN_TEMP;
		} else if (t.equals("bp")) {
			type = ActionLogType.BAN_PERM;
		} else if (t.equals("ubm")) {
			type = ActionLogType.UNBAN_MAN;
		} else if (t.equals("uba")) {
			type = ActionLogType.UNBAN_AUTO;
		} else if (t.equals("k")) {
			type = ActionLogType.KICK;
		} else {
			type = ActionLogType.NOTE;
		}
		if (isTempAction()) {
			dur = Long.parseLong(entryData.split(":")[4]);
		} else if (type == ActionLogType.NOTE || type == ActionLogType.KICK) {
			note = entryData.split(":")[4];
		}
	}
	
	public ActionLogEntry(ActionLogType t, String u, String s) {
		type = t;
		username = u;
		sender = s;
		time = new Date();
	}
	
	public ActionLogEntry(ActionLogType t, String u, String s, long d) {
		type = t;
		username = u;
		sender = s;
		dur = d;
		time = new Date();
	}
	
	public ActionLogEntry(ActionLogType t, String u, String s, String n) {
		type = t;
		username = u;
		sender = s;
		note = n;
		time = new Date();
	}
	
	public String getUser() {
		return username;
	}
	
	public String getSender() {
		return sender;
	}
	
	public boolean isTempAction() {
		return (type == ActionLogType.BAN_TEMP || type == ActionLogType.JAIL_TEMP
				|| type == ActionLogType.MUTE_TEMP);
	}
	
	public ActionLogType getActionType() {
		return type;
	}
	
	public Date getTime() {
		return time;
	}
	
	public String formatTime() {
		DateFormat dfm = new SimpleDateFormat("MM-dd-yyyy HH:mm");
		return dfm.format(time);
	}
	
	public long getDuration() {
		if (!isTempAction()) {
			throw new UnsupportedOperationException("This type of log entry has no duration!");
		}
		return dur;
	}
	
	public String getNote() {
		if (type != ActionLogType.NOTE && type != ActionLogType.KICK) {
			throw new UnsupportedOperationException("This type of log entry cannot store notes!");
		}
		return note;
	}
	
	public String getEntry() {
		String v = "";
		v += time.getTime() + ":";
		v += username + ":";
		v += sender + ":";
		switch (type) {
		case MUTE_TEMP:
			v += "mt";
			break;
		case MUTE_PERM:
			v += "mp";
			break;
		case UNMUTE_MAN:
			v += "umm";
			break;
		case UNMUTE_AUTO:
			v += "uma";
			break;
		case JAIL_TEMP:
			v += "jt";
			break;
		case JAIL_PERM:
			v += "jp";
			break;
		case UNJAIL_MAN:
			v += "ujm";
			break;
		case UNJAIL_AUTO:
			v += "uja";
			break;
		case BAN_TEMP:
			v += "bt";
			break;
		case BAN_PERM:
			v += "bp";
			break;
		case UNBAN_MAN:
			v += "ubm";
			break;
		case UNBAN_AUTO:
			v += "uba";
			break;
		case KICK:
			v += "k";
			break;
		default:
			v += "n";
			break;
		}
		if (isTempAction()) {
			v += ":" + dur;
		} else if (type == ActionLogType.NOTE || type == ActionLogType.KICK){
			v += ":" + note;
		}
		return v;
	}
	
	public String readShort() {
		String r = "";
		r += "[" + this.formatTime() + "] ";
		r += type.toString() + ": ";
		r += username + " (" + sender + ")";
		if (type == ActionLogType.KICK || type == ActionLogType.NOTE) {
			r += ": " + note;
		} else if (this.isTempAction()) {
			r += ": " + ((int) Math.floor(((double)dur) / 3600000D)) + "h " + ((int) Math.floor((((double) dur) % 3600000D) / 60000D)) + "m";
		}
		return r;
	}
	
	public enum ActionLogType {
		MUTE_TEMP, MUTE_PERM, UNMUTE_MAN, UNMUTE_AUTO, // Muting
		JAIL_TEMP, JAIL_PERM, UNJAIL_MAN, UNJAIL_AUTO, // Jailing
		BAN_TEMP, BAN_PERM, UNBAN_MAN, UNBAN_AUTO,     // Banning
		KICK, NOTE                                     // Other
	}

	@Override
	public int compareTo(ActionLogEntry comp) {
		return time.compareTo(comp.time);
	}
}
