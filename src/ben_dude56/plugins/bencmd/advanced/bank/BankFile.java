package ben_dude56.plugins.bencmd.advanced.bank;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import org.bukkit.entity.Player;

import ben_dude56.plugins.bencmd.BenCmd;

public class BankFile extends Properties {
	private static final long serialVersionUID = 0L;
	
	private String filename;
	private HashMap<String, BankInventory> banks;
	private BenCmd plugin;

	public BankFile(BenCmd instance, String file) {
		plugin = instance;
		filename = file;
		banks = new HashMap<String, BankInventory>();
		loadFile();
		loadBanks();
	}
	
	public void loadFile() {
		File file = new File(filename);
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
		File file = new File(filename);
		if (file.exists()) {
			try {
				store(new FileOutputStream(file), "-BenCmd Shelf List-");
			} catch (IOException e) {
				System.out.println("BenCmd had a problem:");
				e.printStackTrace();
			}
		}
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
	
	public void loadBanks() {
		for(int i = 0; i < this.size(); i++) {
			String key = (String) this.keySet().toArray()[i], value = this.getProperty(key);
			int comma = 0;
			for(char c : value.toCharArray()) {
				if (c == ',') {
					comma++;
				}
			}
			BankInventory bank;
			if(comma < 27) {
				bank = new BankInventory(key, plugin);
			} else {
				bank = new LargeBankInventory(key, plugin);
			}
			bank.fromValue(value);
			banks.put(key, bank);
		}
	}
	
	public void upgradeBank(String player) {
		if(getBank(player).isUpgraded()) {
			return;
		}
		banks.put(player, new LargeBankInventory(banks.get(player)));
		saveBank(banks.get(player));
	}
	
	public void addBank(BankInventory bank) {
		banks.put(bank.p, bank);
		saveBank(bank);
	}
	
	public void saveBank(BankInventory bank) {
		try {
			this.put(bank.p, bank.getValue());
			this.saveFile();
		} catch (Exception e) { }
	}
	
	public void saveAll() {
		for (BankInventory bank : banks.values()) {
			saveBank(bank);
		}
	}
}
