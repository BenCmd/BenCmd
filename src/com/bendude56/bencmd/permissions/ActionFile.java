package com.bendude56.bencmd.permissions;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.BenCmdFile;
import com.bendude56.bencmd.User;
import com.bendude56.bencmd.permissions.Action.ActionType;
import com.bendude56.bencmd.permissions.ActionLogEntry.ActionLogType;


public class ActionFile extends BenCmdFile {
	protected HashMap<Integer, Action> actions;

	public ActionFile() {
		super("action.db", "--BenCmd Action File--", true);
		this.loadFile();
		this.loadAll();
		Bukkit.getScheduler().scheduleSyncRepeatingTask(BenCmd.getPlugin(),
				new ActionTask(), 1, 1);
	}

	public void loadAll() {
		actions = new HashMap<Integer, Action>();
		for (int i = 0; i < getFile().size(); i++) {
			String value[] = ((String) getFile().values().toArray()[i]).split("/");
			String key = (String) getFile().keySet().toArray()[i];
			int id;
			try {
				id = Integer.parseInt(key);
			} catch (NumberFormatException e) {
				BenCmd.log(Level.WARNING, "In the actions file, " + key
						+ " is not a valid integer!");
				continue;
			}
			if (value.length != 3) {
				BenCmd.log(Level.WARNING, "In the actions file, an entry must contain exactly 2 slashes! (Entry: "
								+ key + ")");
				continue;
			}
			PermissionUser user = PermissionUser.matchUser(value[0]);
			if (user == null) {
				BenCmd.log(Level.WARNING, "In the actions file, " + value[0]
						+ " is not a valid user! (Entry: " + key + ")");
				continue;
			}
			ActionType action;
			if (value[1].equals("j")) {
				action = ActionType.JAIL;
			} else if (value[1].equals("b")) {
				action = ActionType.BAN;
			} else if (value[1].equals("m")) {
				action = ActionType.ALLMUTE;
			} else if (value[1].equals("l")) {
				action = ActionType.LEAVEJAIL;
			} else {
				BenCmd.log(Level.WARNING, "In the actions file, " + value[1]
						+ " is not a valid action type! (Entry: " + key + ")");
				continue;
			}
			long endTime;
			try {
				endTime = Long.parseLong(value[2]);
			} catch (NumberFormatException e) {
				BenCmd.log(Level.WARNING, "In the actions file, " + value[2]
						+ " cannot be converted into an integer! (Entry: "
						+ key + ")");
				continue;
			}
			actions.put(id, new Action(id, user, action, endTime));
		}
	}
	
	public void saveAll() {
		for (Map.Entry<Integer, Action> e : actions.entrySet()) {
			updateAction(e.getValue(), false);
		}
		saveFile();
	}

	public Action isJailed(PermissionUser user) {
		for (Action action : ofUser(user)) {
			if (action.getActionType() == ActionType.JAIL) {
				return action;
			}
		}
		return null;
	}

	public Action isUnjailed(PermissionUser user) {
		for (Action action : ofUser(user)) {
			if (action.getActionType() == ActionType.LEAVEJAIL) {
				return action;
			}
		}
		return null;
	}

	public Action isMuted(PermissionUser user) {
		for (Action action : ofUser(user)) {
			if (action.getActionType() == ActionType.ALLMUTE) {
				return action;
			}
		}
		return null;
	}

	public Action isBanned(PermissionUser user) {
		for (Action action : ofUser(user)) {
			if (action.getActionType() == ActionType.BAN) {
				return action;
			}
		}
		return null;
	}

	private List<Action> ofUser(PermissionUser user) {
		List<Action> actions = new ArrayList<Action>();
		for (Action action : this.actions.values()) {
			if (action.getUser().getName().equals(user.getName())) {
				actions.add(action);
			}
		}
		return actions;
	}

	public void addAction(PermissionUser user, ActionType action, long duration) {
		if (duration == -1) {
			this.updateAction(new Action(this.nextId(), user, action, -1), true);
		} else {
			this.updateAction(new Action(this.nextId(), user, action,
					new Date().getTime() + duration), true);
		}
	}

	private int nextId() {
		int i;
		for (i = 0; getFile().containsKey(String.valueOf(i)); i++) { }
		return i;
	}

	public void updateAction(Action action, boolean saveFile) {
		getFile().put(String.valueOf(action.getId()),
				action.getUser().getName() + "/" + action.getActionLetter()
						+ "/" + String.valueOf(action.getExpiry()));
		actions.put(action.getId(), action);
		if (saveFile)
			saveFile();
	}

	public void removeAction(Action action) {
		if (!getFile().containsKey(String.valueOf(action.getId()))) {
			return;
		}
		getFile().remove(String.valueOf(action.getId()));
		actions.remove(action.getId());
		saveFile();
	}
	
	public class ActionTask implements Runnable {

		public void run() {
			List<Action> ac = new ArrayList<Action>(actions.values());
			for (Action action : ac) {
				if (action.isExpired()) {
					if (action.getActionType() == ActionType.JAIL) {
						BenCmd.getPermissionManager().getActionLog().log(new ActionLogEntry(ActionLogType.UNJAIL_AUTO, action.getUser().getName(), "N/A"));
						User user2;
						if ((user2 = User.matchUser(action.getUser().getName())) == null) {
							addAction(action.getUser(),
									ActionType.LEAVEJAIL, -1);
						} else {
							user2.spawn();
							user2.sendMessage(ChatColor.GREEN
									+ "You've been unjailed!");
						}
					} else if (action.getActionType() == ActionType.ALLMUTE) {
						BenCmd.getPermissionManager().getActionLog().log(new ActionLogEntry(ActionLogType.UNMUTE_AUTO, action.getUser().getName(), "N/A"));
						User user2;
						if ((user2 = User.matchUser(action.getUser().getName())) != null) {
							user2.sendMessage(ChatColor.GREEN
									+ "You've been unmuted!");
						}
					} else if (action.getActionType() == ActionType.BAN){
						BenCmd.getPermissionManager().getActionLog().log(new ActionLogEntry(ActionLogType.UNBAN_AUTO, action.getUser().getName(), "N/A"));
					}
					removeAction(action);
				}
			}
			ac = null;
		}

	}
}
