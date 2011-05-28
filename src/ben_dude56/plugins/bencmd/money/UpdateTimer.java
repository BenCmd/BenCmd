package ben_dude56.plugins.bencmd.money;

import java.util.TimerTask;

public class UpdateTimer extends TimerTask {
	PriceFile priceFile;

	public UpdateTimer(PriceFile instance) {
		priceFile = instance;
	}

	public void run() {
		priceFile.pollUpdate();
	}

}
