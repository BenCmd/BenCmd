package ben_dude56.plugins.bencmd.money;

public class UpdateTimer implements Runnable {
	PriceFile priceFile;

	public UpdateTimer(PriceFile instance) {
		priceFile = instance;
	}

	public void run() {
		priceFile.pollUpdate();
	}

}
