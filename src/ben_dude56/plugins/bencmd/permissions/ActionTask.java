package ben_dude56.plugins.bencmd.permissions;

import java.util.TimerTask;

import org.bukkit.ChatColor;

import ben_dude56.plugins.bencmd.User;
import ben_dude56.plugins.bencmd.permissions.Action.ActionType;

public class ActionTask extends TimerTask {
	
	private ActionFile actions;
	
	public ActionTask(ActionFile actions) {
		this.actions = actions;
	}

	public void run() {
		for(Action action : actions.actions.values()) {
			if(action.isExpired()) {
				if(action.getActionType() == ActionType.JAIL) {
					User user2;
					if((user2 = User.matchUser(action.getUser().getName(),actions.plugin)) == null) {
						actions.addAction(action.getUser(), ActionType.LEAVEJAIL, -1);
					} else {
						user2.Spawn();
						user2.sendMessage(ChatColor.GREEN + "You've been unjailed!");
					}
				} else if (action.getActionType() == ActionType.ALLMUTE) {
					User user2;
					if((user2 = User.matchUser(action.getUser().getName(), actions.plugin)) != null) {
						user2.sendMessage(ChatColor.GREEN + "You've been unmuted!");
					}
				}
				actions.removeAction(action);
			}
		}
	}

}
