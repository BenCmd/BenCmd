package ben_dude56.plugins.bencmd.advanced.bank;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import org.bukkit.entity.Player;
import org.bukkit.util.FileUtil;

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
		if (new File("plugins/BenCmd/_bank.db").exists()) {
			plugin.log.warning("Bank backup file found... Restoring...");
			if (FileUtil.copy(new File("plugins/BenCmd/_bank.db"), new File(
					file))) {
				new File("plugins/BenCmd/_bank.db").delete();
				plugin.log.info("Restoration suceeded!");
			} else {
				plugin.log.warning("Failed to restore from backup!");
			}
		}
		loadFile();
		loadBanks();
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {

			@Override
			public void run() {
				saveAll();
			}
			
		}, 12000, 12000);
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
		for (int i = 0; i < this.size(); i++) {
			String key = (String) this.keySet().toArray()[i], value = this
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
			this.put(bank.p, bank.getValue());
		} catch (Exception e) {
		}
		try {
			new File("plugins/BenCmd/_bank.db").createNewFile();
			if (!FileUtil.copy(new File(filename), new File(
					"plugins/BenCmd/_bank.db"))) {
				plugin.log.warning("Failed to back up bank database!");
			}
		} catch (IOException e) {
			plugin.log.warning("Failed to back up bank database!");
		}
		saveFile();
		try {
			new File("plugins/BenCmd/_bank.db").delete();
		} catch (Exception e) { }
	}

	public void saveAll() {
		for (BankInventory bank : banks.values()) {
			saveBank(bank);
		}
		for (Player p : plugin.getServer().getOnlinePlayers()) {
			p.saveData();
		}
		try {
			new File("plugins/BenCmd/_bank.db").createNewFile();
			if (!FileUtil.copy(new File(filename), new File(
					"plugins/BenCmd/_bank.db"))) {
				plugin.log.warning("Failed to back up bank database!");
			}
		} catch (IOException e) {
			plugin.log.warning("Failed to back up bank database!");
		}
		saveFile();
		try {
			new File("plugins/BenCmd/_bank.db").delete();
		} catch (Exception e) { }
	}
}
