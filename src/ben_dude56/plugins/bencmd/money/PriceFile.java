package ben_dude56.plugins.bencmd.money;

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

import org.bukkit.ChatColor;

import ben_dude56.plugins.bencmd.BenCmd;
import ben_dude56.plugins.bencmd.invtools.BCItem;
import ben_dude56.plugins.bencmd.invtools.InventoryBackend;

public class PriceFile extends Properties {
	private static final long serialVersionUID = 0L;

	BenCmd plugin;
	private String proFile;
	private HashMap<String, BuyableItem> items = new HashMap<String, BuyableItem>();
	private long nextUpdate;
	private InventoryBackend back;
	private Timer update;
	private boolean timerenabled;

	public PriceFile(BenCmd instance, String priceLocation) {
		plugin = instance;
		proFile = priceLocation;
		back = new InventoryBackend(plugin);
		loadFile();
		loadPrices();
		update = new Timer();
		if(plugin.mainProperties.getInteger("updateTime", 1800000) == -1) {
			timerenabled = false;
		} else {
			update.schedule(new UpdateTimer(this), 0, 1000);
			timerenabled = true;
		}
	}
	
	public boolean isTimerEnabled() {
		return timerenabled;
	}
	
	public void loadTimer() {
		if(timerenabled) {
			return;
		}
		timerenabled = true;
		update.schedule(new UpdateTimer(this), 0, 1000);
	}

	public void unloadTimer() {
		if(!timerenabled) {
			return;
		}
		timerenabled = true;
		update.cancel();
	}

	public void loadFile() {
		File file = new File(proFile);
		if (file.exists()) {
			try {
				load(new FileInputStream(file));
			} catch (IOException e) {
				System.out.println("BenCmd had a problem:");
				e.printStackTrace();
			}
		}
	}

	public void saveFile() {
		File file = new File(proFile);
		if (file.exists()) {
			try {
				store(new FileOutputStream(file), "--BenCmd Price List--");
			} catch (IOException e) {
				System.out.println("BenCmd had a problem:");
				e.printStackTrace();
			}
		}
	}

	public void loadPrices() {
		nextUpdate = 0;
		items.clear();
		for (int i = 0; i < this.values().size(); i++) {
			int itemid;
			int damage;
			double price;
			int supply;
			int supplydemand;
			boolean isCurrency;
			if (((String) this.keySet().toArray()[i])
					.equalsIgnoreCase("nextUpdate")) {
				try {
					nextUpdate = Long.parseLong(this.getProperty("nextUpdate"));
				} catch (NumberFormatException e) {
					plugin.log.severe("nextUpdate (value: "
							+ this.getProperty("nextUpdate")
							+ ") couldn't be converted to a number!");
				}
				continue;
			}
			try {
				itemid = Integer.parseInt(((String) this.keySet().toArray()[i])
						.split(",")[0]);
			} catch (NumberFormatException e) {
				plugin.log
						.severe("A value in the price file couldn't be loaded ("
								+ this.keySet().toArray()[i] + "): ID is NaN");
				continue;
			} catch (IndexOutOfBoundsException e) {
				plugin.log
						.severe("A value in the price file couldn't be loaded ("
								+ this.keySet().toArray()[i]
								+ "): ID is missing");
				continue;
			}
			try {
				damage = Integer.parseInt(((String) this.keySet().toArray()[i])
						.split(",")[1]);
			} catch (NumberFormatException e) {
				plugin.log
						.severe("A value in the price file couldn't be loaded ("
								+ this.keySet().toArray()[i]
								+ "): Damage is NaN");
				continue;
			} catch (IndexOutOfBoundsException e) {
				damage = 0;
			}
			String[] slashsplit = ((String) this.values().toArray()[i])
					.split("/");
			try {
				price = Double.parseDouble(slashsplit[0]);
			} catch (NumberFormatException e) {
				plugin.log
						.severe("A value in the price file couldn't be loaded ("
								+ this.keySet().toArray()[i] + "): Cost is NaN");
				continue;
			} catch (IndexOutOfBoundsException e) {
				plugin.log
						.severe("A value in the price file couldn't be loaded ("
								+ this.keySet().toArray()[i]
								+ "): Cost is missing");
				continue;
			}
			try {
				supply = Integer.parseInt(slashsplit[1]);
			} catch (NumberFormatException e) {
				plugin.log
						.severe("A value in the price file couldn't be loaded ("
								+ this.keySet().toArray()[i]
								+ "): Supply is NaN");
				continue;
			} catch (IndexOutOfBoundsException e) {
				plugin.log
						.severe("A value in the price file couldn't be loaded ("
								+ this.keySet().toArray()[i]
								+ "): Supply is missing");
				continue;
			}
			try {
				supplydemand = Integer.parseInt(slashsplit[2]);
			} catch (NumberFormatException e) {
				plugin.log
						.severe("A value in the price file couldn't be loaded ("
								+ this.keySet().toArray()[i]
								+ "): Supply/Demand is NaN");
				continue;
			} catch (IndexOutOfBoundsException e) {
				plugin.log
						.severe("A value in the price file couldn't be loaded ("
								+ this.keySet().toArray()[i]
								+ "): Supply/Demand is missing");
				continue;
			}
			try {
				isCurrency = (slashsplit[3].equalsIgnoreCase("true"));
			} catch (IndexOutOfBoundsException e) {
				plugin.log
						.severe("A value in the price file couldn't be loaded ("
								+ this.keySet().toArray()[i]
								+ "): isCurrency is missing");
				continue;
			}
			if (isCurrency) {
				items.put(itemid + "," + damage, new Currency(itemid, damage,
						price, supply, supplydemand, this));
			} else {
				items.put(itemid + "," + damage, new BuyableItem(itemid,
						damage, price, supply, supplydemand, this));
			}
		}
		if (nextUpdate == 0) {
			nextUpdate = new Date().getTime() + plugin.mainProperties.getInteger("updateTime", 1800000);
		}
	}

	public void savePrice(BuyableItem item) {
		String key = item.getItemId() + "," + item.getDurability();
		String value = item.getPrice().toString();
		value += "/" + item.getSupply().toString();
		value += "/" + item.getSupplyDemand().toString();
		if (item instanceof Currency) {
			value += "/true";
		} else {
			value += "/false";
		}
		this.put(key, value);
		items.put(key, item);
		saveFile();
	}

	public void remPrice(BuyableItem item) {
		String key = item.getItemId() + "," + item.getDurability();
		this.remove(key);
		items.remove(key);
		saveFile();
	}

	public void pollUpdate() {
		if(plugin.mainProperties.getInteger("updateTime", 1800000) == -1) {
			return;
		}
		long nowTime = new Date().getTime();
		if (nowTime >= nextUpdate) {
			ForceUpdate();
		}
	}

	public void ForceUpdate() {
		nextUpdate = new Date().getTime() + plugin.mainProperties.getInteger("updateTime", 1800000);
		saveUpdateTime();
		plugin.getServer().broadcastMessage(
				ChatColor.RED + "ALERT: All prices are being updated...");
		for (BuyableItem item : items.values()) {
			if (item instanceof Currency) {
				continue;
			}
			if (item.getSupplyDemand() > 0) {
				Double oldPrice = item.getPrice();
				Double newPrice = oldPrice;
				newPrice += item.getSupplyDemand()
								/ ((double) back.getStackNumber(item
										.getItemId())) * plugin.mainProperties.getDouble("marketMultiple", 0.25);
				if(newPrice - oldPrice > plugin.mainProperties.getDouble("marketMaxChange", 2)) {
					newPrice = oldPrice + plugin.mainProperties.getDouble("marketMaxChange", 2);
				}
				item.setPrice(newPrice);
				item.resetSupplyDemand();
				savePrice(item);
			} else if (item.getSupplyDemand() < 0) {
				Double oldPrice = item.getPrice();
				Double newPrice = oldPrice;
				newPrice -= -item.getSupplyDemand()
								/ ((double) back.getStackNumber(item
										.getItemId())) * plugin.mainProperties.getDouble("marketMultiple", 0.25);
				if (newPrice < plugin.mainProperties.getDouble("marketMin", 0.05)) {
					newPrice = 0.05;
				}
				if(oldPrice - newPrice > plugin.mainProperties.getDouble("marketMaxChange", 2)) {
					newPrice = oldPrice - plugin.mainProperties.getDouble("marketMaxChange", 2);
				}
				item.setPrice(newPrice);
				item.resetSupplyDemand();
				savePrice(item);
			}
		}
	}

	public void saveUpdateTime() {
		this.put("nextUpdate", String.valueOf(nextUpdate));
		saveFile();
	}

	public BuyableItem getItem(BCItem item) {
		try {
			return items.get(item.getMaterial().getId() + ","
					+ item.getDamage());
		} catch (NullPointerException e) {
			return null;
		}
	}

	public List<Currency> getCurrencies() {
		List<Currency> currencies = new ArrayList<Currency>();
		for (BuyableItem item : items.values()) {
			if (item instanceof Currency) {
				currencies.add((Currency) item);
			}
		}
		return currencies;
	}
}
