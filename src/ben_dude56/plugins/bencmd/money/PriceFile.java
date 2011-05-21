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

	public PriceFile(BenCmd instance, String priceLocation) {
		plugin = instance;
		proFile = priceLocation;
		back = new InventoryBackend(plugin);
		loadFile();
		loadPrices();
		update = new Timer();
		update.schedule(new UpdateTimer(this), 1000);
	}
	
	public void unloadTimer() {
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
			int price;
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
				price = Integer.parseInt(slashsplit[0]);
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
			nextUpdate = new Date().getTime() + 86400;
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
		if (new Date().getTime() >= nextUpdate) {
			ForceUpdate();
		}
	}

	public void ForceUpdate() {
		nextUpdate = new Date().getTime() + 1800;
		plugin.getServer().broadcastMessage(ChatColor.RED + "ALERT: All prices are being updated...");
		for (BuyableItem item : items.values()) {
			if (item instanceof Currency) {
				continue;
			}
			if (item.getSupplyDemand() >= ((double) back.getStackNumber(item
					.getItemId())) * 1.5) {
				Integer newPrice = item.getPrice();
				newPrice += (int) Math
						.ceil((item.getSupplyDemand() / back
								.getStackNumber(item.getItemId()))
								* (((double) back.getStackNumber(item
										.getItemId())) * 0.5));
				item.setPrice(newPrice);
				item.resetSupplyDemand();
				savePrice(item);
			} else if (item.getSupplyDemand() <= ((double) back
					.getStackNumber(item.getItemId())) * -1.5) {
				Integer newPrice = item.getPrice();
				newPrice -= (int) Math
						.ceil((item.getSupplyDemand() / back
								.getStackNumber(item.getItemId()))
								* -(((double) back.getStackNumber(item
										.getItemId())) * 0.5));
				if (newPrice < 1) {
					newPrice = 1;
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
