package ben_dude56.plugins.bencmd.money;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import ben_dude56.plugins.bencmd.User;
import ben_dude56.plugins.bencmd.invtools.InventoryBackend;

public class BuyableItem {
	private Integer id;
	private Integer durability;
	private Double cost;
	private Integer supply;
	private Integer supdem;
	private PriceFile priceFile;

	public BuyableItem(Integer ID, Integer Damage, Double Cost,
			Integer Supply, Integer SupplyDemand, PriceFile instance) {
		id = ID;
		durability = Damage;
		cost = Cost;
		supply = Supply;
		supdem = SupplyDemand;
		priceFile = instance;
	}

	public Integer getDurability() {
		return durability;
	}

	public Integer getItemId() {
		return id;
	}

	public Double getPrice() {
		return cost;
	}

	protected Integer getSupplyDemand() {
		return supdem;
	}

	protected void setPrice(Double price) {
		cost = price;
	}

	protected void setSupply(Integer Supply) {
		supply = Supply;
	}

	public boolean inStock() {
		return (supply != 0);
	}

	public Material getMaterial() {
		return Material.getMaterial(id);
	}

	public Integer getSupply() {
		return supply;
	}

	public BuyResult buyItem(User user, Integer amount) {
		if (amount > supply && supply != -1) {
			return BuyResult.INS_SUPPLY;
		}
		Double amountHas = 0.0;
		Double amountTaken = 0.0;
		Double amountNeeded = amount * cost;
		Integer fullAmt = amount;
		HashMap<Double, Currency> sortedCurrencies = new HashMap<Double, Currency>();
		for (Currency currencyType : priceFile.getCurrencies()) {
			sortedCurrencies.put(currencyType.getPrice(), currencyType);
			HashMap<Integer, ? extends ItemStack> matches = user.getHandle()
					.getInventory().all(currencyType.getMaterial());
			for (ItemStack match : matches.values()) {
				if (match.getTypeId() == this.getItemId()
						&& match.getDurability() == this.getDurability()) {
					continue;
				}
				amountHas += currencyType.getPrice() * match.getAmount();
			}
		}
		if (amountHas < amountNeeded) {
			return BuyResult.INS_FUNDS;
		}
		Object[] reversedCurrencies = sortedCurrencies.values().toArray();
		for (int i = 0; i < reversedCurrencies.length / 2; i++) {
			Currency temp = (Currency) reversedCurrencies[i];
			reversedCurrencies[i] = reversedCurrencies[reversedCurrencies.length
					- i - 1];
			reversedCurrencies[reversedCurrencies.length - i - 1] = temp;
		}
		for (Currency currency : sortedCurrencies.values()) {
			Double value = currency.getPrice();
			HashMap<Integer, ? extends ItemStack> matches = user.getHandle()
					.getInventory().all(currency.getMaterial());
			for (Integer pos : matches.keySet()) {
				ItemStack item = user.getHandle().getInventory().getItem(pos);
				if (item.getTypeId() == this.getItemId()
						&& item.getDurability() == this.getDurability()) {
					continue;
				}
				if (amountTaken + item.getAmount() * value <= amountNeeded) {
					amountTaken += item.getAmount() * value;
					user.getHandle().getInventory().clear(pos);
				} else if (amountTaken + value <= amountNeeded) {
					int taken;
					taken = (int) Math.ceil((amountNeeded - amountTaken) / value);
					item.setAmount(item.getAmount() - taken);
					user.getHandle().getInventory().setItem(pos, item);
					amountTaken += taken * value;
					break;
				} else {
					break;
				}
			}
		}
		if (amountTaken < amountNeeded) {
			for (Currency currency : sortedCurrencies.values()) {
				if (currency.getItemId() == this.getItemId()
						&& currency.getDurability() == this.getDurability()) {
					continue;
				}
				HashMap<Integer, ? extends ItemStack> matches = user
						.getHandle().getInventory().all(currency.getMaterial());
				if (!matches.isEmpty()) {
					ItemStack item = (ItemStack) matches.values().toArray()[0];
					Integer pos = (Integer) matches.keySet().toArray()[0];
					item.setAmount(item.getAmount() - 1);
					if (item.getAmount() != 0) {
						user.getHandle().getInventory().setItem(pos, item);
					} else {
						user.getHandle().getInventory().clear(pos);
					}
					HashMap<Currency, Integer> currencies = makeChange(
							currency.getPrice() - (amountNeeded - amountTaken),
							reversedCurrencies);
					for (int i = 0; i < currencies.size(); i++) {
						Currency changeCurrency = (Currency) currencies
								.keySet().toArray()[i];
						Integer changeAmount = (Integer) currencies.values()
								.toArray()[i];
						List<Integer> splitamount = new ArrayList<Integer>();
						while (changeAmount > 0) {
							Integer maxAmount = new InventoryBackend(
									priceFile.plugin)
									.getStackNumber(changeCurrency.getItemId());
							if (changeAmount > maxAmount) {
								splitamount.add(maxAmount);
								changeAmount -= maxAmount;
							} else {
								splitamount.add(changeAmount);
								changeAmount = 0;
							}
						}
						for (Integer amt : splitamount) {
							if (user.getHandle().getInventory().firstEmpty() >= 0) {
								user.getHandle()
										.getInventory()
										.addItem(
												new ItemStack(changeCurrency
														.getMaterial(), amt));
							} else {
								user.getHandle()
										.getWorld()
										.dropItem(
												user.getHandle().getLocation(),
												new ItemStack(changeCurrency
														.getMaterial(), amt));
							}
						}
					}
					break;
				}
			}
		}
		List<Integer> splitamount = new ArrayList<Integer>();
		while (amount > 0) {
			Integer maxAmount = new InventoryBackend(priceFile.plugin)
					.getStackNumber(this.getItemId());
			if (amount > maxAmount) {
				splitamount.add(maxAmount);
				amount -= maxAmount;
			} else {
				splitamount.add(amount);
				amount = 0;
			}
		}
		for (Integer amt : splitamount) {
			if (user.getHandle().getInventory().firstEmpty() >= 0) {
				user.getHandle()
						.getInventory()
						.addItem(
								new ItemStack(this.getMaterial(), amt,
										(short) (int) this.getDurability()));
			} else {
				user.getHandle()
						.getWorld()
						.dropItem(
								user.getHandle().getLocation(),
								new ItemStack(this.getMaterial(), amt,
										(short) (int) this.getDurability()));
			}
		}
		supdem += fullAmt;
		if (supply != -1) {
			supply -= fullAmt;
		}
		return BuyResult.SUCCESS;
	}

	public boolean sellItem(User user, Integer amount) {
		Integer amountHas = 0;
		Integer amountTaken = 0;
		Integer fullAmt = amount;
		HashMap<Integer, ? extends ItemStack> matches = user.getHandle()
				.getInventory().all(this.getMaterial());
		for (ItemStack iStack : matches.values()) {
			amountHas += iStack.getAmount();
		}
		if (amount > amountHas) {
			return false;
		}
		for (int i = 0; i < matches.size(); i++) {
			ItemStack iStack = (ItemStack) matches.values().toArray()[i];
			Integer slot = (Integer) matches.keySet().toArray()[i];
			if (amountTaken + iStack.getAmount() <= amount) {
				amountTaken += iStack.getAmount();
				user.getHandle().getInventory().clear(slot);
			} else {
				Integer toTake = amount - amountTaken;
				iStack.setAmount(iStack.getAmount() - toTake);
				user.getHandle().getInventory().setItem(slot, iStack);
			}
			if (amountTaken == amount) {
				break;
			}
		}
		HashMap<Double, Currency> sortedCurrencies = new HashMap<Double, Currency>();
		for (Currency currencyType : priceFile.getCurrencies()) {
			sortedCurrencies.put(currencyType.getPrice(), currencyType);
		}
		Object[] reversedCurrencies = sortedCurrencies.values().toArray();
		for (int i = 0; i < reversedCurrencies.length / 2; i++) {
			Currency temp = (Currency) reversedCurrencies[i];
			reversedCurrencies[i] = reversedCurrencies[reversedCurrencies.length
					- i - 1];
			reversedCurrencies[reversedCurrencies.length - i - 1] = temp;
		}
		HashMap<Currency, Integer> change = makeChange(amount * cost,
				reversedCurrencies);
		for (int i = 0; i < change.size(); i++) {
			Currency changeCurrency = (Currency) change.keySet().toArray()[i];
			Integer changeAmount = (Integer) change.values().toArray()[i];
			List<Integer> splitamount = new ArrayList<Integer>();
			while (changeAmount > 0) {
				Integer maxAmount = new InventoryBackend(priceFile.plugin)
						.getStackNumber(changeCurrency.getItemId());
				if (changeAmount > maxAmount) {
					splitamount.add(maxAmount);
					changeAmount -= maxAmount;
				} else {
					splitamount.add(changeAmount);
					changeAmount = 0;
				}
			}
			for (Integer amt : splitamount) {
				if (user.getHandle().getInventory().firstEmpty() >= 0) {
					user.getHandle()
							.getInventory()
							.addItem(
									new ItemStack(changeCurrency.getMaterial(),
											amt));
				} else {
					user.getHandle()
							.getWorld()
							.dropItem(
									user.getHandle().getLocation(),
									new ItemStack(changeCurrency.getMaterial(),
											amt));
				}
			}
		}
		supdem -= fullAmt;
		if (supply != -1) {
			supply += fullAmt;
		}
		return true;
	}

	public HashMap<Currency, Integer> makeChange(Double change,
			Object[] acceptedCurrencies) {
		HashMap<Currency, Integer> giveChange = new HashMap<Currency, Integer>();
		Double given = 0.0;
		for (Object currencyo : acceptedCurrencies) {
			Currency currency = (Currency) currencyo;
			double rem = change - given;
			double price = currency.getPrice();
			Integer toGive = (int) Math.floor(rem / price);
			given += toGive * currency.getPrice();
			giveChange.put(currency, toGive);
		}
		return giveChange;
	}

	public void resetSupplyDemand() {
		supdem = 0;
	}

	public enum BuyResult {
		INS_FUNDS, INS_SUPPLY, IMP_BUY, SUCCESS
	}
}
