package com.bendude56.bencmd.invtools.kits;

import java.util.ArrayList;
import java.util.List;
import com.bendude56.bencmd.*;


public class KitList extends BenCmdFile {
	public List<Kit> kits = new ArrayList<Kit>();

	public KitList() {
		super("kits.db", "--BenCmd Kit File--", false);
		loadFile();
		loadAll();
	}

	public void loadAll() {
		kits.clear();
		for (int i = 0; i < getFile().size(); i++) {
			kits.add(new Kit(i, (String) getFile().values().toArray()[i],
					(String) getFile().keySet().toArray()[i]));
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

	@Override
	public void saveAll() {
		throw new UnsupportedOperationException("Kit list cannot be saved!");
	}
}
