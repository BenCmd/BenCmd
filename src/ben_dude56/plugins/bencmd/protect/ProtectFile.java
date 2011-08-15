package ben_dude56.plugins.bencmd.protect;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.bukkit.Location;
import org.bukkit.World;

import ben_dude56.plugins.bencmd.BenCmd;
import ben_dude56.plugins.bencmd.permissions.PermissionUser;

public class ProtectFile extends Properties {
	private static final long serialVersionUID = 0L;
	private List<ProtectedBlock> protectedBlocks;
	private BenCmd plugin;
	private String proFile;

	public ProtectFile(BenCmd instance, String protectList) {
		plugin = instance;
		proFile = protectList;
		loadFile();
		loadValues();
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

	public void saveFile(String header) {
		File file = new File(proFile);
		if (file.exists()) {
			try {
				store(new FileOutputStream(file), header);
			} catch (IOException e) {
				System.out.println("BenCmd had a problem:");
				e.printStackTrace();
			}
		}
	}

	public void loadValues() {
		protectedBlocks = new ArrayList<ProtectedBlock>();
		for (int i = 0; i < this.values().size(); i++) {
			String value = (String) this.values().toArray()[i];
			String key = (String) this.keySet().toArray()[i];
			if (key.startsWith(".")) {
				continue;
			}
			String[] slashsplit = value.split("/");
			if (slashsplit.length != 4) {
				plugin.log.warning("Entry " + key + " in " + proFile
						+ " is invalid and was ignored!");
				plugin.bLog.warning("Protection " + key + " is invalid!");
				continue;
			}
			int id;
			try {
				id = Integer.parseInt(key);
			} catch (NumberFormatException e) {
				plugin.log.warning("Entry " + key + " in " + proFile
						+ " is invalid and was ignored!");
				plugin.bLog.warning("Protection " + key + " is invalid!");
				continue;
			}
			PermissionUser owner;
			if ((owner = PermissionUser.matchUser(slashsplit[2], plugin)) == null) {
				plugin.log.warning("Entry " + key + " in " + proFile
						+ " is invalid and was ignored!");
				plugin.bLog.warning("Protection " + key + " is invalid!");
				continue;
			}
			List<PermissionUser> guests = new ArrayList<PermissionUser>();
			try {
				if (!slashsplit[1].isEmpty()) {
					for (String guest : slashsplit[1].split(",")) {
						PermissionUser newGuest;
						if ((newGuest = PermissionUser.matchUser(guest, plugin)) == null) {
							throw new NullPointerException();
						}
						guests.add(newGuest);
					}
				}
			} catch (NullPointerException e) {
				plugin.log.warning("Entry " + key + " in " + proFile
						+ " is invalid and was ignored!");
				plugin.bLog.warning("Protection " + key + " is invalid!");
				continue;
			}
			String type = slashsplit[0];
			Location loc;
			try {
				World world = plugin.getServer().getWorld(
						slashsplit[3].split(",")[0]);
				int x = Integer.parseInt(slashsplit[3].split(",")[1]);
				int y = Integer.parseInt(slashsplit[3].split(",")[2]);
				int z = Integer.parseInt(slashsplit[3].split(",")[3]);
				loc = new Location(world, x, y, z);
			} catch (NumberFormatException e) {
				plugin.log.warning("Entry " + key + " in " + proFile
						+ " is invalid and was ignored!");
				plugin.bLog.warning("Protection " + key + " is invalid!");
				continue;
			} catch (NullPointerException e) {
				plugin.log.warning("Entry " + key + " in " + proFile
						+ " is invalid and was ignored!");
				plugin.bLog.warning("Protection " + key + " is invalid!");
				continue;
			}
			if (type.equalsIgnoreCase("c")) {
				protectedBlocks.add(new ProtectedChest(plugin, id, owner,
						guests, loc));
			} else if (type.equalsIgnoreCase("d")) {
				protectedBlocks.add(new ProtectedDoor(plugin, id, owner,
						guests, loc));
			} else if (type.equalsIgnoreCase("pc")) {
				protectedBlocks.add(new PublicChest(plugin, id, owner, guests,
						loc));
			} else if (type.equalsIgnoreCase("pd")) {
				protectedBlocks.add(new PublicDoor(plugin, id, owner, guests,
						loc));
			} else {
				plugin.log.warning("Entry " + key + " in " + proFile
						+ " is invalid and was ignored!");
				plugin.bLog.warning("Protection " + key + " is invalid!");
			}
		}
	}

	public void updateValue(ProtectedBlock block, boolean comment) {
		if (block instanceof ProtectedChest) {
			String value;
			String key;
			key = ((comment) ? "." : "") + String.valueOf(block.GetId());
			value = "";
			value += "c/";
			boolean init = false;
			for (PermissionUser guest : block.getGuests()) {
				if (init) {
					value += ",";
				} else {
					init = true;
				}
				value += guest.getName();
			}
			value += "/" + block.getOwner().getName();
			Location blockLoc = block.getLocation();
			value += "/" + blockLoc.getWorld().getName() + ","
					+ String.valueOf(blockLoc.getBlockX()) + ","
					+ String.valueOf(blockLoc.getBlockY()) + ","
					+ String.valueOf(blockLoc.getBlockZ());
			this.put(key, value);
		} else if (block instanceof ProtectedDoor) {
			String value;
			String key;
			key = ((comment) ? "." : "") + String.valueOf(block.GetId());
			value = "";
			value += "d/";
			boolean init = false;
			for (PermissionUser guest : block.getGuests()) {
				if (init) {
					value += ",";
				} else {
					init = true;
				}
				value += guest.getName();
			}
			value += "/" + block.getOwner().getName();
			Location blockLoc = block.getLocation();
			value += "/" + blockLoc.getWorld().getName() + ","
					+ String.valueOf(blockLoc.getBlockX()) + ","
					+ String.valueOf(blockLoc.getBlockY()) + ","
					+ String.valueOf(blockLoc.getBlockZ());
			this.put(key, value);
		} else if (block instanceof PublicChest) {
			String value;
			String key;
			key = ((comment) ? "." : "") + String.valueOf(block.GetId());
			value = "";
			value += "pc/";
			boolean init = false;
			for (PermissionUser guest : block.getGuests()) {
				if (init) {
					value += ",";
				} else {
					init = true;
				}
				value += guest.getName();
			}
			value += "/" + block.getOwner().getName();
			Location blockLoc = block.getLocation();
			value += "/" + blockLoc.getWorld().getName() + ","
					+ String.valueOf(blockLoc.getBlockX()) + ","
					+ String.valueOf(blockLoc.getBlockY()) + ","
					+ String.valueOf(blockLoc.getBlockZ());
			this.put(key, value);
		} else if (block instanceof PublicDoor) {
			String value;
			String key;
			key = ((comment) ? "." : "") + String.valueOf(block.GetId());
			value = "";
			value += "pd/";
			boolean init = false;
			for (PermissionUser guest : block.getGuests()) {
				if (init) {
					value += ",";
				} else {
					init = true;
				}
				value += guest.getName();
			}
			value += "/" + block.getOwner().getName();
			Location blockLoc = block.getLocation();
			value += "/" + blockLoc.getWorld().getName() + ","
					+ String.valueOf(blockLoc.getBlockX()) + ","
					+ String.valueOf(blockLoc.getBlockY()) + ","
					+ String.valueOf(blockLoc.getBlockZ());
			this.put(key, value);
		}
		this.saveFile(proFile);
	}

	public void remValue(Integer id) {
		this.remove(id.toString());
		this.saveFile(proFile);
	}

	public int getProtection(Location loc) {
		int id = -1;
		List<ProtectedBlock> q = new ArrayList<ProtectedBlock>();
		for (ProtectedBlock block : protectedBlocks) {
			if (block.getLocation().equals(loc)) {
				id = block.GetId();
				break;
			}
			if (block instanceof ProtectedChest) {
				if (((ProtectedChest) block).isDoubleChest()
						&& ((ProtectedChest) block).getSecondChest()
								.getLocation().equals(loc)) {
					id = block.GetId();
					break;
				}
			}
			if (block instanceof PublicChest) {
				if (((PublicChest) block).isDoubleChest()
						&& ((PublicChest) block).getSecondChest().getLocation()
								.equals(loc)) {
					id = block.GetId();
					break;
				}
			}
			if (block instanceof ProtectedDoor) {
				try {
					if (((ProtectedDoor) block).getSecondBlock().getLocation()
							.equals(loc)) {
						id = block.GetId();
						break;
					}
				} catch (NullPointerException e) {
					plugin.log
							.warning(block.GetId()
									+ " has a missing secondary block. It will be quarantined...");
					plugin.bLog
							.warning("Protection "
									+ block.GetId()
									+ " is missing a secondary block! (It was quarantined)");
					q.add(block);
				}
				try {
					if (((ProtectedDoor) block).getBelowBlock().getLocation()
							.equals(loc)) {
						id = block.GetId();
						break;
					}
				} catch (NullPointerException e) {
					plugin.log
							.warning(block.GetId()
									+ " has a missing secondary block. It will be quarantined...");
					plugin.bLog
							.warning("Protection "
									+ block.GetId()
									+ " is missing a secondary block! (It was quarantined)");
					q.add(block);
				}
			}
			if (block instanceof PublicDoor) {
				try {
					if (((PublicDoor) block).getSecondBlock().getLocation()
							.equals(loc)) {
						id = block.GetId();
						break;
					}
				} catch (NullPointerException e) {
					plugin.log
							.warning(block.GetId()
									+ " has a missing secondary block. It will be quarantined...");
					plugin.bLog
							.warning("Protection "
									+ block.GetId()
									+ " is missing a secondary block! (It was quarantined)");
					q.add(block);
				}
				try {
					if (((PublicDoor) block).getBelowBlock().getLocation()
							.equals(loc)) {
						id = block.GetId();
						break;
					}
				} catch (NullPointerException e) {
					plugin.log
							.warning(block.GetId()
									+ " has a missing secondary block. It will be quarantined...");
					plugin.bLog
							.warning("Protection "
									+ block.GetId()
									+ " is missing a secondary block! (It was quarantined)");
					q.add(block);
				}
			}
		}
		for (ProtectedBlock block : q) {
			removeProtection(block.getLocation());
			updateValue(block, true);
		}
		return id;
	}

	public int getProtectionIndex(int id) {
		for (int i = 0; i < protectedBlocks.size(); i++) {
			if (protectedBlocks.get(i).GetId() == id) {
				return i;
			}
		}
		return -1;
	}

	public boolean protectionExists(int id) {
		for (ProtectedBlock block : protectedBlocks) {
			if (block.GetId() == id) {
				return true;
			}
		}
		return false;
	}

	public int getNextId() {
		for (int i = 0; true; i++) {
			if (!protectionExists(i)) {
				return i;
			}
		}
	}

	public int addProtection(PermissionUser owner, Location loc,
			ProtectionType type) {
		int id = getNextId();
		ProtectedBlock protect = null;
		switch (type) {
		case Chest:
			protectedBlocks.add(protect = new ProtectedChest(plugin, id, owner,
					new ArrayList<PermissionUser>(), loc));
			break;
		case Door:
			protectedBlocks.add(protect = new ProtectedDoor(plugin, id, owner,
					new ArrayList<PermissionUser>(), loc));
			break;
		case PDoor:
			protectedBlocks.add(protect = new PublicDoor(plugin, id, owner,
					new ArrayList<PermissionUser>(), loc));
			break;
		case PChest:
			protectedBlocks.add(protect = new PublicChest(plugin, id, owner,
					new ArrayList<PermissionUser>(), loc));
			break;

		}
		updateValue(protect, false);
		return id;
	}

	public boolean removeProtection(Location loc) {
		int id;
		if ((id = getProtection(loc)) != -1) {
			protectedBlocks.remove(getProtectionIndex(id));
			remValue(id);
			return true;
		} else {
			return false;
		}
	}

	public boolean removeProtection(int id) {
		int ind;
		try {
			ind = getProtection(id).GetId();
		} catch (NullPointerException e) {
			return false;
		}
		protectedBlocks.remove(getProtectionIndex(ind));
		remValue(id);
		return true;

	}

	public ProtectedBlock getProtection(int id) {
		try {
			return protectedBlocks.get(getProtectionIndex(id));
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	public void changeOwner(int id, PermissionUser newOwner) {
		int ind;
		ind = getProtectionIndex(id);
		if (ind == -1) {
			return;
		}
		ProtectedBlock pb = protectedBlocks.get(ind);
		// protectedBlocks.remove(ind);
		pb.setOwner(newOwner);
		// protectedBlocks.add(ind, pb);
	}

	public void addGuest(int id, PermissionUser newGuest) {
		int ind;
		ind = getProtectionIndex(id);
		if (ind == -1) {
			return;
		}
		ProtectedBlock pb = protectedBlocks.get(ind);
		// protectedBlocks.remove(ind);
		pb.addGuest(newGuest);
		// protectedBlocks.add(ind, pb);
	}

	public void removeGuest(int id, PermissionUser oldGuest) {
		int ind;
		ind = getProtectionIndex(id);
		if (ind == -1) {
			return;
		}
		ProtectedBlock pb = protectedBlocks.get(ind);
		// protectedBlocks.remove(ind);
		pb.removeGuest(oldGuest);
		// protectedBlocks.add(ind, pb);
	}

	public static enum ProtectionType {
		Chest, Door, Furnace, PDoor, PChest
	}
}