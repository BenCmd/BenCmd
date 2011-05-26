package ben_dude56.plugins.bencmd.chat;

import java.util.TimerTask;

//TODO For version 1.0.4: Rewrite code using Date.getTime() rather than a changing value...
public class SlowModeTimer extends TimerTask {
	SlowMode parent;

	public SlowModeTimer(SlowMode instance) {
		parent = instance;
	}

	public void run() {
		if (parent.isEnabled()) {
			for (int i = 0; i < parent.playerList.size(); i++) {
				String playerName = (String) parent.playerList.keySet()
						.toArray()[i];
				Integer timeLeft = (Integer) parent.playerList.values()
						.toArray()[i];
				timeLeft--;
				if (timeLeft <= 0) {
					parent.playerList.remove(playerName);
				} else {
					parent.playerList.put(playerName, timeLeft);
				}
			}
		}
	}
}
