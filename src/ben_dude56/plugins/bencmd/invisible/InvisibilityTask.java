package ben_dude56.plugins.bencmd.invisible;

import java.util.TimerTask;

import org.bukkit.entity.Player;

public class InvisibilityTask extends TimerTask {
	// TODO For v1.2.6: Change to Bukkit Scheduler
	Invisibility parent;

	public InvisibilityTask(Invisibility parentInv) {
		parent = parentInv;
	}

	@Override
	public void run() {
		for (Player noInv : parent.plugin.noinvisible) {
			if (noInv == null) {
				parent.plugin.noinvisible.remove(noInv);
			}
		}
		for (Player toHide : parent.plugin.invisible) {
			if (toHide == null) {
				parent.plugin.invisible.remove(toHide);
				continue;
			}
			for (Player hideFrom : parent.plugin.getServer().getOnlinePlayers()) {
				if ((!parent.plugin.noinvisible.contains(hideFrom) && toHide != hideFrom)
						|| parent.plugin.allinvisible.contains(toHide)) {
					parent.invisible(toHide, hideFrom);
				}
			}
		}
	}
}
