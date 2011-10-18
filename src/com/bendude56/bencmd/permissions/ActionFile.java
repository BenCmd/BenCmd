package com.bendude56.bencmd.permissions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Timer;

import org.bukkit.util.FileUtil;

import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.permissions.Action.ActionType;


public class ActionFile extends Properties {

	private static final long serialVersionUID = 0L;
	protected HashMap<Integer, Action> actions;
	private Timer time = new Timer();

	public ActionFile() {
		BenCmd plugin = BenCmd.getPlugin();
		if (new File("plugins/BenCmd/_action.db").exists()) {
			plugin.log.warning("Action backup file found... Restoring...");
			if (FileUtil.copy(new File("plugins/BenCmd/_action.db"), new File(
					"plugins/BenCmd/action.db"))) {
				new File("plugins/BenCmd/_action.db").delete();
				plugin.log.info("Restoration suceeded!");
			} else {
				plugin.log.warning("Failed to restore from backup!");
			}
		}
		this.loadFile();
		this.loadActions();
		time.schedule(new ActionTask(this), 0, 1000);
	}

	public void loadFile() {
		BenCmd plugin = BenCmd.getPlugin();
		File file = new File("plugins/BenCmd/action.db"); // Prepare the file
		if (!file.exists()) {
			try {
				file.createNewFile(); // If the file doesn't exist, create it!
			} catch (IOException ex) {
				// If you can't, produce an error.
				plugin.log.severe("BenCmd had a problem:");
				ex.printStackTrace();
				return;
			}
		}
		try {
			load(new FileInputStream(file)); // Load the values
		} catch (IOException ex) {
			// If you can't, produce an error.
			plugin.log.severe("BenCmd had a problem:");
			ex.printStackTrace();
		}
	}

	public void loadActions() {
		BenCmd plugin = BenCmd.getPlugin();
		actions = new HashMap<Integer, Action>();
		for (int i = 0; i < this.size(); i++) {
			String value[] = ((String) this.values().toArray()[i]).split("/");
			String key = (String) this.keySet().toArray()[i];
			int id;
			try {
				id = Integer.parseInt(key);
			} catch (NumberFormatException e) {
				plugin.log.warning("In the actions file, " + key
						+ " is not a valid integer!");
				plugin.bLog.info("Action " + key + " is invalid!");
				continue;
			}
			if (value.length != 3) {
				plugin.log
						.warning("In the actions file, an entry must contain exactly 2 slashes! (Entry: "
								+ key + ")");
				plugin.bLog.info("Action " + key + " is invalid!");
				continue;
			}
			PermissionUser user = PermissionUser.matchUser(value[0], plugin);
			if (user == null) {
				plugin.log.warning("In the actions file, " + value[0]
						+ " is not a valid user! (Entry: " + key + ")");
				plugin.bLog.info("Action " + key + " is invalid!");
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
				plugin.log.warning("In the actions file, " + value[1]
						+ " is not a valid action type! (Entry: " + key + ")");
				plugin.bLog.info("Action " + key + " is invalid!");
				continue;
			}
			long endTime;
			try {
				endTime = Long.parseLong(value[2]);
			} catch (NumberFormatException e) {
				plugin.log.warning("In the actions file, " + value[2]
						+ " cannot be converted into an integer! (Entry: "
						+ key + ")");
				plugin.bLog.info("Action " + key + " is invalid!");
				continue;
			}
			actions.put(id, new Action(id, user, action, endTime));
		}
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
			this.updateAction(new Action(this.nextId(), user, action, -1));
		} else {
			this.updateAction(new Action(this.nextId(), user, action,
					new Date().getTime() + duration));
		}
	}

	private int nextId() {
		int i;
		for (i = 0; this.containsKey(String.valueOf(i)); i++) {
		}
		return i;
	}

	public void updateAction(Action action) {
		BenCmd plugin = BenCmd.getPlugin();
		this.put(String.valueOf(action.getId()),
				action.getUser().getName() + "/" + action.getActionLetter()
						+ "/" + String.valueOf(action.getExpiry()));
		actions.put(action.getId(), action);
		try {
			new File("plugins/BenCmd/_action.db").createNewFile();
			if (!FileUtil.copy(new File("plugins/BenCmd/action.db"), new File(
					"plugins/BenCmd/_action.db"))) {
				plugin.log.warning("Failed to back up action database!");
			}
		} catch (IOException e) {
			plugin.log.warning("Failed to back up action database!");
		}
		saveFile();
		try {
			new File("plugins/BenCmd/_action.db").delete();
		} catch (Exception e) { }
	}

	public void removeAction(Action action) {
		BenCmd plugin = BenCmd.getPlugin();
		if (!this.containsKey(String.valueOf(action.getId()))) {
			return;
		}
		this.remove(String.valueOf(action.getId()));
		actions.remove(action.getId());
		try {
			new File("plugins/BenCmd/_action.db").createNewFile();
			if (!FileUtil.copy(new File("plugins/BenCmd/action.db"), new File(
					"plugins/BenCmd/_action.db"))) {
				plugin.log.warning("Failed to back up action database!");
			}
		} catch (IOException e) {
			plugin.log.warning("Failed to back up action database!");
		}
		saveFile();
		try {
			new File("plugins/BenCmd/_action.db").delete();
		} catch (Exception e) { }
	}

	public void saveFile() {
		BenCmd plugin = BenCmd.getPlugin();
		File file = new File("plugins/BenCmd/action.db"); // Prepare the file
		if (!file.exists()) {
			try {
				file.createNewFile(); // If the file doesn't exist, create it!
			} catch (IOException ex) {
				// If you can't, produce an error.
				plugin.log.severe("BenCmd had a problem:");
				ex.printStackTrace();
				return;
			}
		}
		try {
			// Save the values
			store(new FileOutputStream(file), "BenCmd User Actions File");
		} catch (IOException ex) {
			// If you can't, produce an error.
			plugin.log.severe("BenCmd had a problem:");
			ex.printStackTrace();
		}
	}
}
