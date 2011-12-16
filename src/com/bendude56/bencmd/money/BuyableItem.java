package com.bendude56.bencmd.money;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.User;
import com.bendude56.bencmd.invtools.InventoryBackend;

public class BuyableItem implements Comparable<BuyableItem> {
	private Integer		id;
	private Short		durability;
	private Double		cost;
	private Integer		supply;
	private Integer		supdem;

	public BuyableItem(Integer ID, Short Damage, Double Cost, Integer Supply, Integer SupplyDemand) {
		id = ID;
		durability = Damage;
		cost = Cost;
		supply = Supply;
		supdem = SupplyDemand;
	}

	public Short getDurability() {
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
	
	public static Integer amountItem(User user, Integer id, Short durability) {
		int amount = 0;
		HashMap<Integer, ? extends ItemStack> matches = ((Player) user.getHandle()).getInventory().all(id);
		for (ItemStack match : matches.values()) {
			if (match.getDurability() == durability) {
				amount += match.getAmount();
			}
		}
		return amount;
	}

	public static boolean hasMoney(User user, Double amountNeeded, List<Material> exempt) {
		Double amountHas = 0.0;
		for (Currency currencyType : BenCmd.getMarketController().getCurrencies()) {
			if (!exempt.contains(currencyType.getMaterial())) {
				amountHas += currencyType.getPrice() * amountItem(user, currencyType.getItemId(), currencyType.getDurability());
			}
		}
		return amountHas >= amountNeeded;
	}

	public static void remMoney(User user, Double amountNeeded, List<Material> exempt) {
		Double amountTaken = 0.0;
		HashMap<Double, Currency> sortedCurrencies = new HashMap<Double, Currency>();
		for (Currency currencyType : BenCmd.getMarketController().getCurrencies()) {
			if (!exempt.contains(currencyType.getMaterial())) {
				sortedCurrencies.put(currencyType.getPrice(), currencyType);
			}
		}
		for (Currency currency : sortedCurrencies.values()) {
			Double value = currency.getPrice();
			HashMap<Integer, ? extends ItemStack> matches = ((Player) user.getHandle()).getInventory().all(currency.getMaterial());
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
					taken = (int) Math.ceil((amountNeeded - amountTaken) / value);
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
					HashMap<Currency, Integer> currencies = makeChange(currency.getPrice() - (amountNeeded - amountTaken), sortedCurrencies.values().toArray());
					for (int i = 0; i < currencies.size(); i++) {
						Currency changeCurrency = (Currency) currencies.keySet().toArray()[i];
						Integer changeAmount = (Integer) currencies.values().toArray()[i];
						List<Integer> splitamount = new ArrayList<Integer>();
						while (changeAmount > 0) {
							Integer maxAmount = InventoryBackend.getInstance().getStackNumber(changeCurrency.getItemId());
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
								((Player) user.getHandle()).getInventory().addItem(new ItemStack(changeCurrency.getMaterial(), amt));
							} else {
								((Player) user.getHandle()).getWorld().dropItem(((Player) user.getHandle()).getLocation(), new ItemStack(changeCurrency.getMaterial(), amt));
							}
						}
					}
					break;
				}
			}
		}
	}
	
	public static void giveMoney(User user, Double amount) {
		HashMap<Double, Currency> sortedCurrencies = new HashMap<Double, Currency>();
		for (Currency currencyType : BenCmd.getMarketController().getCurrencies()) {
			sortedCurrencies.put(currencyType.getPrice(), currencyType);
		}
		Object[] reversedCurrencies = sortedCurrencies.values().toArray();
		for (int i = 0; i < reversedCurrencies.length / 2; i++) {
			Currency temp = (Currency) reversedCurrencies[i];
			reversedCurrencies[i] = reversedCurrencies[reversedCurrencies.length - i - 1];
			reversedCurrencies[reversedCurrencies.length - i - 1] = temp;
		}
		HashMap<Currency, Integer> change = makeChange(amount, sortedCurrencies.values().toArray());
		for (Map.Entry<Currency, Integer> e : change.entrySet()) {
			if (e.getValue() != 0) {
				((Player) user.getHandle()).getInventory().addItem(new ItemStack(e.getKey().getItemId(), e.getValue(), e.getKey().getDurability()));
			}
		}
	}
	
	public static void remItem(User user, Integer amount, BuyableItem i) {
		for (Map.Entry<Integer, ? extends ItemStack> e : ((Player) user.getHandle()).getInventory().all(i.getItemId()).entrySet()) {
			if (e.getValue().getDurability() == i.getDurability()) {
				if (e.getValue().getAmount() <= amount) {
					amount -= e.getValue().getAmount();
					((Player) user.getHandle()).getInventory().clear(e.getKey());
				} else {
					((Player) user.getHandle()).getInventory().setItem(e.getKey(), new ItemStack(e.getValue().getTypeId(), e.getValue().getAmount() - amount, e.getValue().getDurability()));
					amount = 0;
				}
			}
			if (amount == 0) {
				break;
			}
		}
	}
	
	public BuyResult buyItem(User user, Integer amount) {
		if (amount > supply && supply != -1) {
			return BuyResult.INS_SUPPLY;
		} else if (!hasMoney(user, amount * cost, new ArrayList<Material>(Arrays.asList(new Material[] { this.getMaterial() })))) {
			return BuyResult.INS_FUNDS;
		} else {
			remMoney(user, amount * cost, new ArrayList<Material>(Arrays.asList(new Material[] { this.getMaterial() })));
			((Player)user.getHandle()).getInventory().addItem(new ItemStack(id, amount, durability));
			return BuyResult.SUCCESS;
		}
	}
	
	public boolean sellItem(User user, Integer amount) {
		if (amountItem(user, id, durability) < amount) {
			return false;
		} else {
			remItem(user, amount, this);
			giveMoney(user, amount * cost);
			return true;
		}
	}
	
	public static HashMap<Currency, Integer> makeChange(Double change, Object[] acceptedCurrencies) {
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
