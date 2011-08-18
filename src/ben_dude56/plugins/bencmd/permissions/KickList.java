package ben_dude56.plugins.bencmd.permissions;

import java.util.Date;
import java.util.HashMap;

import org.bukkit.Bukkit;

import ben_dude56.plugins.bencmd.BenCmd;

public class KickList {
	private HashMap<PermissionUser, Long> users;
	private BenCmd plugin;

	public KickList(BenCmd instance) {
		users = new HashMap<PermissionUser, Long>();
		Bukkit.getServer().getScheduler()
				.scheduleAsyncRepeatingTask(instance, new KickTimer(), 2, 2);
		plugin = instance;
	}

	@SuppressWarnings("unchecked")
	public HashMap<PermissionUser, Long> getUserList() {
		return (HashMap<PermissionUser, Long>) users.clone();
	}

	public void checkUser(PermissionUser user) {
		if (new Date().getTime() >= users.get(user)) {
			users.remove(user);
		}
	}

	public void addUser(PermissionUser user) {
		if (user.hasPerm("bencmd.action.kick.nodelay")) {
			return;
		}
		users.put(
				user,
				new Date().getTime()
						+ plugin.mainProperties.getInteger("kickDelay", 120000));
	}

	public long isBlocked(String name) {
		for (int i = 0; i < users.size(); i++) {
			PermissionUser user;
			if ((user = (PermissionUser) users.keySet().toArray()[i]).getName()
					.equalsIgnoreCase(name)) {
				return users.get(user) - new Date().getTime();
			}
		}
		return 0;
	}

	public void clearList() {
		users.clear();
	}

	public class KickTimer implements Runnable {
		public void run() {
			HashMap<PermissionUser, Long> users = getUserList();
			for (PermissionUser user : users.keySet()) {
				checkUser(user);
			}
		}

	}

}
