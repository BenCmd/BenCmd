package com.bendude56.bencmd.advanced.bank;

import java.util.HashMap;
import org.bukkit.entity.Player;
import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.BenCmdFile;


public class BankFile extends BenCmdFile {
	private HashMap<String, BankInventory> banks;

	public BankFile() {
		super("bank.db", "--BenCmd Bank File--", true);
		BenCmd plugin = BenCmd.getPlugin();
		banks = new HashMap<String, BankInventory>();
		loadFile();
		loadAll();
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {

			@Override
			public void run() {
				saveAll();
			}
			
		}, 12000, 12000);
	}

	public boolean hasBank(String pname) {
		return (banks.containsKey(pname));
	}

	public BankInventory getBank(String pname) {
		return banks.get(pname);
	}

	public void openInventory(Player p) {
		openInventory(p.getName(), p);
	}

	public void openInventory(String p, Player p2) {
		getBank(p).open(p2);
	}

	public void loadAll() {
		BenCmd plugin = BenCmd.getPlugin();
		for (int i = 0; i < getFile().size(); i++) {
			String key = (String) getFile().keySet().toArray()[i], value = getFile()
					.getProperty(key);
			int comma = 0;
			for (char c : value.toCharArray()) {
				if (c == ',') {
					comma++;
				}
			}
			BankInventory bank;
			if (comma < 27) {
				bank = new BankInventory(key, plugin);
			} else {
				bank = new LargeBankInventory(key, plugin);
			}
			bank.fromValue(value);
			banks.put(key, bank);
		}
	}

	public void upgradeBank(String player) {
		if (getBank(player).isUpgraded()) {
			return;
		}
		banks.put(player, new LargeBankInventory(banks.get(player)));
		saveBank(banks.get(player));
	}

	public boolean canDowngradeBank(String player) {
		return ((LargeBankInventory) getBank(player)).inv2.isEmpty();
	}

	public void downgradeBank(String player) {
		if (!getBank(player).isUpgraded()) {
			return;
		}
		if (!((LargeBankInventory) getBank(player)).inv2.isEmpty()) {
			return;
		}
		banks.put(player, new BankInventory(getBank(player)));
		saveBank(banks.get(player));
	}

	public void addBank(BankInventory bank) {
		banks.put(bank.p, bank);
		saveBank(bank);
	}

	public void saveBank(BankInventory bank) {
		try {
			getFile().put(bank.p, bank.getValue());
		} catch (Exception e) {
		}
		saveFile();
	}

	public void saveAll() {
		BenCmd plugin = BenCmd.getPlugin();
		for (BankInventory bank : banks.values()) {
			saveBank(bank);
		}
		for (Player p : plugin.getServer().getOnlinePlayers()) {
			p.saveData();
		}
		saveFile();
	}
}
