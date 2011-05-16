package ben_dude56.plugins.bencmd.invtools.kits;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import ben_dude56.plugins.bencmd.*;
import ben_dude56.plugins.bencmd.invtools.*;

public class KitList extends Properties {
	private static final long serialVersionUID = 0L;
	File file;
	BenCmd plugin;
	InventoryBackend back;
	static final Logger log = Logger.getLogger("minecraft");
	public List<Kit> kits = new ArrayList<Kit>();

	public KitList(BenCmd instance) {
		plugin = instance;
		back = new InventoryBackend(plugin);
		this.reload();
	}

	public void reload() {
		file = new File("plugins/BenCmd/kits.db");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException ex) {
				log.severe("BenCmd had a problem:");
				ex.printStackTrace();
				return;
			}
		}
		try {
			load(new FileInputStream(file));
		} catch (IOException ex) {
			log.severe("BenCmd had a problem:");
			ex.printStackTrace();
		}
		kits.clear();
		for (int i = 0; i < this.size(); i++) {
			kits.add(new Kit(plugin, i, (String) this.values().toArray()[i],
					(String) this.keySet().toArray()[i]));
		}
	}

	public boolean kitExists(String name) {
		for (Kit kit : kits) {
			if (kit.getName().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}

	public Kit getKit(String name) {
		for (Kit kit : kits) {
			if (kit.getName().equalsIgnoreCase(name)) {
				return kit;
			}
		}
		return null;
	}

	public Kit getKit(int ID) {
		for (Kit kit : kits) {
			if (kit.getId() == ID) {
				return kit;
			}
		}
		return null;
	}

	public boolean canUseKit(User user, String kitName) {
		Kit kit = this.getKit(kitName);
		if (kit != null) {
			return kit.canUseKit(user);
		} else {
			return false;
		}
	}

	public boolean giveKit(User user, String kitName) {
		Kit kit = this.getKit(kitName);
		if (kit != null) {
			return kit.giveKit(user);
		} else {
			return false;
		}
	}

	public boolean giveKit(User receiver, User sender, String kitName) {
		Kit kit = this.getKit(kitName);
		if (kit != null && kit.canUseKit(sender)) {
			kit.forceGiveKit(receiver);
			return true;
		} else {
			return false;
		}
	}
}
