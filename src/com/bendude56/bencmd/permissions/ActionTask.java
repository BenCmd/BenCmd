package com.bendude56.bencmd.permissions;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import org.bukkit.ChatColor;

import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.User;
import com.bendude56.bencmd.permissions.Action.ActionType;
import com.bendude56.bencmd.permissions.ActionLogEntry.ActionLogType;


public class ActionTask extends TimerTask {

	private ActionFile actions;

	public ActionTask(ActionFile actions) {
		this.actions = actions;
	}

	public void run() {
		List<Action> ac = new ArrayList<Action>(actions.actions.values());
		for (Action action : ac) {
			if (action.isExpired()) {
				if (action.getActionType() == ActionType.JAIL) {
					BenCmd.getPlugin().alog.log(new ActionLogEntry(ActionLogType.UNJAIL_AUTO, action.getUser().getName(), "N/A"));
					User user2;
					if ((user2 = User.matchUser(action.getUser().getName(),
							actions.plugin)) == null) {
						actions.addAction(action.getUser(),
								ActionType.LEAVEJAIL, -1);
					} else {
						user2.Spawn();
						user2.sendMessage(ChatColor.GREEN
								+ "You've been unjailed!");
					}
				} else if (action.getActionType() == ActionType.ALLMUTE) {
					BenCmd.getPlugin().alog.log(new ActionLogEntry(ActionLogType.UNMUTE_AUTO, action.getUser().getName(), "N/A"));
					User user2;
					if ((user2 = User.matchUser(action.getUser().getName(),
							actions.plugin)) != null) {
						user2.sendMessage(ChatColor.GREEN
								+ "You've been unmuted!");
					}
				} else if (action.getActionType() == ActionType.BAN){
					BenCmd.getPlugin().alog.log(new ActionLogEntry(ActionLogType.UNBAN_AUTO, action.getUser().getName(), "N/A"));
				}
				actions.removeAction(action);
			}
		}
		ac = null;
	}

}
