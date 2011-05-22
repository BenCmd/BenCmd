package ben_dude56.plugins.bencmd.money;

public class Currency extends BuyableItem {

	public Currency(Integer ID, Integer Damage, Double Cost, Integer Supply,
			Integer SupplyDemand, PriceFile instance) {
		super(ID, Damage, Cost, Supply, SupplyDemand, instance);
	}

	// This class is currently a shell. It is used to determine whether or not
	// something is a currency.

}
