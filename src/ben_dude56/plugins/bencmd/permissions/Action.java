package ben_dude56.plugins.bencmd.permissions;

import java.util.Date;

public class Action {
	
	private PermissionUser user;
	private ActionType action;
	private long endTime;
	private int id;
	
	public Action(int id, PermissionUser user, ActionType action, long endTime) {
		this.id = id;
		this.action = action;
		this.user = user;
		this.endTime = endTime;
	}
	
	public PermissionUser getUser() {
		return user;
	}
	
	public ActionType getActionType() {
		return action;
	}
	
	public char getActionLetter() {
		switch(action) {
		case BAN:
			return 'b';
		case JAIL:
			return 'j';
		case ALLMUTE:
			return 'm';
		case LEAVEJAIL:
			return 'l';
		default:
			return '?';
		}
	}
	
	public boolean isExpired() {
		if(endTime == -1) {
			return false;
		}
		return (new Date().getTime() > endTime);
	}
	
	public long getMilliUntilExpired() {
		if(endTime == -1) {
			return -1;
		}
		return (endTime - new Date().getTime());
	}
	
	public long getExpiry() {
		return endTime;
	}
	
	public int getId() {
		return id;
	}
	
	public static enum ActionType {
		JAIL, BAN, ALLMUTE, LEAVEJAIL
	}
}
