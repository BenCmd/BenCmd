package com.bendude56.bencmd.money;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.User;
import com.bendude56.bencmd.invtools.InventoryBackend;


public class BuyableItem implements Comparable<BuyableItem> {
	private Integer id;
	private Integer durability;
	private Double cost;
	private Integer supply;
	private Integer supdem;
	private PriceFile priceFile;

	public BuyableItem(Integer ID, Integer Damage, Double Cost, Integer Supply,
			Integer SupplyDemand, PriceFile instance) {
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

	public static boolean hasMoney(User user, Double amountNeeded) {
		Double amountHas = 0.0;
		HashMap<Double, Currency> sortedCurrencies = new HashMap<Double, Currency>();
		for (Currency currencyType : BenCmd.getMarketController().getCurrencies()) {
			sortedCurrencies.put(currencyType.getPrice(), currencyType);
			HashMap<Integer, ? extends ItemStack> matches = ((Player) user.getHandle())
					.getInventory().all(currencyType.getMaterial());
			for (ItemStack match : matches.values()) {
				amountHas += currencyType.getPrice() * match.getAmount();
			}
		}
		return amountHas >= amountNeeded;
	}

	public static void remMoney(User user, Double amountNeeded) {
		Double amountTaken = 0.0;
		HashMap<Double, Currency> sortedCurrencies = new HashMap<Double, Currency>();
		for (Currency currencyType : BenCmd.getMarketController().getCurrencies()) {
			sortedCurrencies.put(currencyType.getPrice(), currencyType);
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
			HashMap<Integer, ? extends ItemStack> matches = ((Player) user.getHandle())
					.getInventory().all(currency.getMaterial());
			for (Integer pos : matches.keySet()) {
				ItemStack item = ((Player) user.getHandle()).getInventory().getItem(pos);
				if (item.getDurability() != currency.getDurability()) {
					continue;
				}
				if (amountTaken + item.getAmount() * value <= amountNeeded) {
					amountTaken += item.getAmount() * value;
					((Player) user.getHandle()).getInventory().clear(pos);
				} else if (amountTaken + value <= amountNeeded) {
					int taken;
					taken = (int) Math.ceil((amountNeeded - amountTaken)
							/ value);
					item.setAmount(item.getAmount() - taken);
					((Player) user.getHandle()).getInventory().setItem(pos, item);
					amountTaken += taken * value;
					break;
				} else {
					break;
				}
			}
		}
		if (amountTaken < amountNeeded) {
			for (Currency currency : sortedCurrencies.values()) {
				HashMap<Integer, ? extends ItemStack> matches = ((Player) user.getHandle()).getInventory().all(currency.getMaterial());
				if (!matches.isEmpty()) {
					ItemStack item = (ItemStack) matches.values().toArray()[0];
					Integer pos = (Integer) matches.keySet().toArray()[0];
					item.setAmount(item.getAmount() - 1);
					if (item.getAmount() != 0) {
						((Player) user.getHandle()).getInventory().setItem(pos, item);
					} else {
						((Player) user.getHandle()).getInventory().clear(pos);
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
							Integer maxAmount = InventoryBackend.getInstance()
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
							if (((Player) user.getHandle()).getInventory().firstEmpty() >= 0) {
								((Player) user.getHandle())
										.getInventory()
										.addItem(
												new ItemStack(changeCurrency
														.getMaterial(), amt));
							} else {
								((Player) user.getHandle())
										.getWorld()
										.dropItem(
												((Player) user.getHandle()).getLocation(),
												new ItemStack(changeCurrency
														.getMaterial(), amt));
							}
						}
					}
					break;
				}
			}
		}
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
			HashMap<Integer, ? extends ItemStack> matches = ((Player) user.getHandle())
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
			HashMap<Integer, ? extends ItemStack> matches = ((Player) user.getHandle())
					.getInventory().all(currency.getMaterial());
			for (Integer pos : matches.keySet()) {
				ItemStack item = ((Player) user.getHandle()).getInventory().getItem(pos);
				if (item.getDurability() != currency.getDurability()) {
					continue;
				}
				if (item.getTypeId() == this.getItemId()
						&& item.getDurability() == this.getDurability()) {
					continue;
				}
				if (amountTaken + item.getAmount() * value <= amountNeeded) {
					amountTaken += item.getAmount() * value;
					((Player) user.getHandle()).getInventory().clear(pos);
				} else if (amountTaken + value <= amountNeeded) {
					int taken;
					taken = (int) Math.ceil((amountNeeded - amountTaken)
							/ value);
					item.setAmount(item.getAmount() - taken);
					((Player) user.getHandle()).getInventory().setItem(pos, item);
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
				HashMap<Integer, ? extends ItemStack> matches = ((Player) user.getHandle()).getInventory().all(currency.getMaterial());
				if (!matches.isEmpty()) {
					ItemStack item = (ItemStack) matches.values().toArray()[0];
					Integer pos = (Integer) matches.keySet().toArray()[0];
					item.setAmount(item.getAmount() - 1);
					if (item.getAmount() != 0) {
						((Player) user.getHandle()).getInventory().setItem(pos, item);
					} else {
						((Player) user.getHandle()).getInventory().clear(pos);
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
							Integer maxAmount = InventoryBackend.getInstance()
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
							if (((Player) user.getHandle()).getInventory().firstEmpty() >= 0) {
								((Player) user.getHandle())
										.getInventory()
										.addItem(
												new ItemStack(changeCurrency
														.getMaterial(), amt));
							} else {
								((Player) user.getHandle())
										.getWorld()
										.dropItem(
												((Player) user.getHandle()).getLocation(),
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
			Integer maxAmount = InventoryBackend.getInstance()
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
			if (((Player) user.getHandle()).getInventory().firstEmpty() >= 0) {
				((Player) user.getHandle())
						.getInventory()
						.addItem(
								new ItemStack(this.getMaterial(), amt,
										(short) (int) this.getDurability()));
			} else {
				((Player) user.getHandle())
						.getWorld()
						.dropItem(
								((Player) user.getHandle()).getLocation(),
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
		HashMap<Integer, ? extends ItemStack> matches = ((Player) user.getHandle())
				.getInventory().all(this.getMaterial());
		for (ItemStack iStack : matches.values()) {
			if (iStack.getDurability() != this.durability) {
				continue;
			}
			amountHas += iStack.getAmount();
		}
		if (amount > amountHas) {
			return false;
		}
		for (int i = 0; i < matches.size(); i++) {
			ItemStack iStack = (ItemStack) matches.values().toArray()[i];
			if (iStack.getDurability() != this.durability) {
				continue;
			}
			Integer slot = (Integer) matches.keySet().toArray()[i];
			if (amountTaken + iStack.getAmount() <= amount) {
				amountTaken += iStack.getAmount();
				((Player) user.getHandle()).getInventory().clear(slot);
			} else {
				Integer toTake = amount - amountTaken;
				amountTaken += toTake;
				iStack.setAmount(iStack.getAmount() - toTake);
				((Player) user.getHandle()).getInventory().setItem(slot, iStack);
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
				Integer maxAmount = InventoryBackend.getInstance()
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
				if (((Player) user.getHandle()).getInventory().firstEmpty() >= 0) {
					((Player) user.getHandle())
							.getInventory()
							.addItem(
									new ItemStack(changeCurrency.getMaterial(),
											amt));
				} else {
					((Player) user.getHandle())
							.getWorld()
							.dropItem(
									((Player) user.getHandle()).getLocation(),
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

	public static HashMap<Currency, Integer> makeChange(Double change,
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

	@Override
	public int compareTo(BuyableItem c) {
		if (c.getPrice() < cost) {
			return -1;
		} else if (c.getPrice() > cost) {
			return 1;
		} else {
			return 0;
		}
	}
}
