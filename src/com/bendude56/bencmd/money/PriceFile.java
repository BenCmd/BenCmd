package com.bendude56.bencmd.money;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.BenCmdFile;
import com.bendude56.bencmd.invtools.BCItem;
import com.bendude56.bencmd.invtools.InventoryBackend;

public class PriceFile extends BenCmdFile {
	private HashMap<String, BuyableItem>	items	= new HashMap<String, BuyableItem>();
	private long							nextUpdate;
	private int								update;
	private boolean							timerenabled;

	public PriceFile() {
		super("prices.db", "--BenCmd Price File--", true);
		loadFile();
		loadAll();
		if (BenCmd.getMainProperties().getInteger("updateTime", 1800000) == -1) {
			timerenabled = false;
		} else {
			timerenabled = false;
			loadTimer();
		}
	}

	public boolean isTimerEnabled() {
		return timerenabled;
	}

	public void loadTimer() {
		if (timerenabled) {
			return;
		}
		timerenabled = true;
		update = Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask(BenCmd.getPlugin(), new Runnable() {
			public void run() {
				pollUpdate();
			}
		}, 20, 20);
	}

	public void unloadTimer() {
		if (!timerenabled) {
			return;
		}
		timerenabled = false;
		Bukkit.getServer().getScheduler().cancelTask(update);
	}

	public void loadAll() {
		nextUpdate = 0;
		items.clear();
		for (int i = 0; i < getFile().values().size(); i++) {
			int itemid;
			int damage;
			double price;
			int supply;
			int supplydemand;
			boolean isCurrency;
			if (((String) getFile().keySet().toArray()[i]).equalsIgnoreCase("nextUpdate")) {
				try {
					nextUpdate = Long.parseLong(getFile().getProperty("nextUpdate"));
				} catch (NumberFormatException e) {
					BenCmd.log(Level.SEVERE, "nextUpdate (value: " + getFile().getProperty("nextUpdate") + ") couldn't be converted to a number!");
				}
				continue;
			}
			try {
				itemid = Integer.parseInt(((String) getFile().keySet().toArray()[i]).split(",")[0]);
			} catch (NumberFormatException e) {
				BenCmd.log(Level.SEVERE, "A value in the price file couldn't be loaded (" + getFile().keySet().toArray()[i] + "): ID is NaN");
				continue;
			} catch (IndexOutOfBoundsException e) {
				BenCmd.log(Level.SEVERE, "A value in the price file couldn't be loaded (" + getFile().keySet().toArray()[i] + "): ID is missing");
				continue;
			}
			try {
				damage = Integer.parseInt(((String) getFile().keySet().toArray()[i]).split(",")[1]);
			} catch (NumberFormatException e) {
				BenCmd.log(Level.SEVERE, "A value in the price file couldn't be loaded (" + getFile().keySet().toArray()[i] + "): Damage is NaN");
				continue;
			} catch (IndexOutOfBoundsException e) {
				damage = 0;
			}
			String[] slashsplit = ((String) getFile().values().toArray()[i]).split("/");
			try {
				price = Double.parseDouble(slashsplit[0]);
			} catch (NumberFormatException e) {
				BenCmd.log(Level.SEVERE, "A value in the price file couldn't be loaded (" + getFile().keySet().toArray()[i] + "): Cost is NaN");
				continue;
			} catch (IndexOutOfBoundsException e) {
				BenCmd.log(Level.SEVERE, "A value in the price file couldn't be loaded (" + getFile().keySet().toArray()[i] + "): Cost is missing");
				continue;
			}
			try {
				supply = Integer.parseInt(slashsplit[1]);
			} catch (NumberFormatException e) {
				BenCmd.log(Level.SEVERE, "A value in the price file couldn't be loaded (" + getFile().keySet().toArray()[i] + "): Supply is NaN");
				continue;
			} catch (IndexOutOfBoundsException e) {
				BenCmd.log(Level.SEVERE, "A value in the price file couldn't be loaded (" + getFile().keySet().toArray()[i] + "): Supply is missing");
				continue;
			}
			try {
				supplydemand = Integer.parseInt(slashsplit[2]);
			} catch (NumberFormatException e) {
				BenCmd.log(Level.SEVERE, "A value in the price file couldn't be loaded (" + getFile().keySet().toArray()[i] + "): Supply/Demand is NaN");
				continue;
			} catch (IndexOutOfBoundsException e) {
				BenCmd.log(Level.SEVERE, "A value in the price file couldn't be loaded (" + getFile().keySet().toArray()[i] + "): Supply/Demand is missing");
				continue;
			}
			try {
				isCurrency = (slashsplit[3].equalsIgnoreCase("true"));
			} catch (IndexOutOfBoundsException e) {
				BenCmd.log(Level.SEVERE, "A value in the price file couldn't be loaded (" + getFile().keySet().toArray()[i] + "): isCurrency is missing");
				continue;
			}
			if (isCurrency) {
				items.put(itemid + "," + damage, new Currency(itemid, damage, price, supply, supplydemand, this));
			} else {
				items.put(itemid + "," + damage, new BuyableItem(itemid, damage, price, supply, supplydemand, this));
			}
		}
		if (nextUpdate == 0) {
			nextUpdate = new Date().getTime() + BenCmd.getMainProperties().getInteger("updateTime", 1800000);
		}
	}

	public void savePrice(BuyableItem item, boolean saveFile) {
		String key = item.getItemId() + "," + item.getDurability();
		String value = item.getPrice().toString();
		value += "/" + item.getSupply().toString();
		value += "/" + item.getSupplyDemand().toString();
		if (item instanceof Currency) {
			value += "/true";
		} else {
			value += "/false";
		}
		getFile().put(key, value);
		items.put(key, item);
		if (saveFile)
			saveFile();
	}

	public void remPrice(BuyableItem item) {
		String key = item.getItemId() + "," + item.getDurability();
		getFile().remove(key);
		items.remove(key);
		saveFile();
	}

	public void pollUpdate() {
		if (BenCmd.getMainProperties().getInteger("updateTime", 1800000) == -1) {
			return;
		}
		long nowTime = new Date().getTime();
		if (nowTime >= nextUpdate) {
			ForceUpdate();
		}
	}

	public boolean isCurrency(ItemStack i) {
		BuyableItem bi = getItem(new BCItem(i.getType(), i.getDurability()));
		if (bi == null) {
			return false;
		} else {
			return (bi instanceof Currency);
		}
	}

	public void ForceUpdate() {
		nextUpdate = new Date().getTime() + BenCmd.getMainProperties().getInteger("updateTime", 1800000);
		saveUpdateTime();
		Bukkit.broadcastMessage(ChatColor.RED + "ALERT: All prices are being updated...");
		for (BuyableItem item : items.values()) {
			if (item instanceof Currency) {
				continue;
			}
			if (item.getSupplyDemand() > 0) {
				Double oldPrice = item.getPrice();
				Double newPrice = oldPrice;
				newPrice += item.getSupplyDemand() / ((double) InventoryBackend.getInstance().getStackNumber(item.getItemId())) * BenCmd.getMainProperties().getDouble("marketMultiple", 0.25);
				if (newPrice - oldPrice > BenCmd.getMainProperties().getDouble("marketMaxChange", 2)) {
					newPrice = oldPrice + BenCmd.getMainProperties().getDouble("marketMaxChange", 2);
				}
				newPrice *= 100;
				newPrice = Math.ceil(newPrice) / 100;
				item.setPrice(newPrice);
				item.resetSupplyDemand();
				savePrice(item, false);
			} else if (item.getSupplyDemand() < 0) {
				Double oldPrice = item.getPrice();
				Double newPrice = oldPrice;
				newPrice -= -item.getSupplyDemand() / ((double) InventoryBackend.getInstance().getStackNumber(item.getItemId())) * BenCmd.getMainProperties().getDouble("marketMultiple", 0.25);
				if (newPrice < BenCmd.getMainProperties().getDouble("marketMin", 0.05)) {
					newPrice = 0.05;
				}
				if (oldPrice - newPrice > BenCmd.getMainProperties().getDouble("marketMaxChange", 2)) {
					newPrice = oldPrice - BenCmd.getMainProperties().getDouble("marketMaxChange", 2);
				}
				item.setPrice(newPrice);
				item.resetSupplyDemand();
				savePrice(item, false);
			}
		}
		saveFile();
	}

	public void saveUpdateTime() {
		getFile().put("nextUpdate", String.valueOf(nextUpdate));
		saveFile();
	}

	public BuyableItem getItem(BCItem item) {
		try {
			return items.get(item.getMaterial().getId() + "," + item.getDamage());
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

	@Override
	public void saveAll() {
		for (BuyableItem i : items.values()) {
			savePrice(i, false);
		}
		saveFile();
	}
}
