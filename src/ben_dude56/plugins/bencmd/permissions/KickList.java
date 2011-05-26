package ben_dude56.plugins.bencmd.permissions;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import ben_dude56.plugins.bencmd.BenCmd;

//TODO For version 1.0.4: Rewrite code using Date.getTime() rather than a changing value...
public class KickList {
	private Timer kickTimer;
	private HashMap<PermissionUser, Integer> users;
	
	public KickList(BenCmd instance) {
		users = new HashMap<PermissionUser, Integer>();
		kickTimer = new Timer();
		kickTimer.schedule(new KickTimer(this), 0, 1);
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<PermissionUser, Integer> getUserList() {
		return (HashMap<PermissionUser, Integer>) users.clone();
	}
	
	public void updateUser(PermissionUser user, Integer time) {
		if(time == 0) {
			users.remove(user);
		} else {
			users.put(user, time);
		}
	}
	
	public void addUser(PermissionUser user) {
		if(user.hasPerm("noKickDelay")) {
			return;
		}
		users.put(user, 72000);
	}
	
	public boolean isBlocked(String name) {
		for(int i = 0; i < users.size(); i++) {
			if(((PermissionUser)users.keySet().toArray()[i]).getName().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}
	
	public void clearList() {
		users.clear();
	}
	
	public void killTimer() {
		kickTimer.cancel();
		kickTimer = null;
	}

	public class KickTimer extends TimerTask {
		private KickList list;
		
		public KickTimer(KickList instance) {
			list = instance;
		}

		public void run() {
			HashMap<PermissionUser, Integer> users = list.getUserList();
			for(PermissionUser user : users.keySet()) {
				list.updateUser(user, users.get(user) - 1);
			}
		}

	}

}
