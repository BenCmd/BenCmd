package com.bendude56.bencmd.protect;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.BenCmdFile;

public class ProtectFile extends BenCmdFile {
	private List<ProtectedBlock>	protectedBlocks;

	public ProtectFile() {
		super("protection.db", "--BenCmd Protection File--", true);
		loadFile();
		loadAll();
	}

	public void loadAll() {
		protectedBlocks = new ArrayList<ProtectedBlock>();
		for (int i = 0; i < getFile().values().size(); i++) {
			// TODO Add ability to add groups as guests to protections
			String value = (String) getFile().values().toArray()[i];
			String key = (String) getFile().keySet().toArray()[i];
			if (key.startsWith(".")) {
				continue;
			}
			String[] slashsplit = value.split("/");
			if (slashsplit.length != 4) {
				BenCmd.log(Level.WARNING, "Entry " + key + " in protection.db is invalid and was ignored!");
				continue;
			}
			int id;
			try {
				id = Integer.parseInt(key);
			} catch (NumberFormatException e) {
				BenCmd.log(Level.WARNING, "Entry " + key + " in protection.db is invalid and was ignored!");
				continue;
			}
			String owner;
			owner = slashsplit[2];
			List<String> guests = new ArrayList<String>();
			try {
				if (!slashsplit[1].isEmpty()) {
					for (String guest : slashsplit[1].split(",")) {
						guests.add(guest);
					}
				}
			} catch (NullPointerException e) {
				BenCmd.log(Level.WARNING, "Entry " + key + " in protection.db is invalid and was ignored!");
				continue;
			}
			String type = slashsplit[0];
			Location loc;
			try {
				World world = Bukkit.getWorld(slashsplit[3].split(",")[0]);
				int x = Integer.parseInt(slashsplit[3].split(",")[1]);
				int y = Integer.parseInt(slashsplit[3].split(",")[2]);
				int z = Integer.parseInt(slashsplit[3].split(",")[3]);
				loc = new Location(world, x, y, z);
			} catch (NumberFormatException e) {
				BenCmd.log(Level.WARNING, "Entry " + key + " in protection.db is invalid and was ignored!");
				continue;
			} catch (NullPointerException e) {
				BenCmd.log(Level.WARNING, "Entry " + key + " in protection.db is invalid and was ignored!");
				continue;
			}
			if (type.equalsIgnoreCase("c")) {
				protectedBlocks.add(new ProtectedChest(id, owner, guests, loc));
			} else if (type.equalsIgnoreCase("d")) {
				protectedBlocks.add(new ProtectedDoor(id, owner, guests, loc));
			} else if (type.equalsIgnoreCase("f")) {
				protectedBlocks.add(new ProtectedFurnace(id, owner, guests, loc));
			} else if (type.equalsIgnoreCase("di")) {
				protectedBlocks.add(new ProtectedDispenser(id, owner, guests, loc));
			} else if (type.equalsIgnoreCase("g")) {
				protectedBlocks.add(new ProtectedGate(id, owner, guests, loc));
			} else if (type.equalsIgnoreCase("pc")) {
				protectedBlocks.add(new PublicChest(id, owner, guests, loc));
			} else if (type.equalsIgnoreCase("pd")) {
				protectedBlocks.add(new PublicDoor(id, owner, guests, loc));
			} else if (type.equalsIgnoreCase("pf")) {
				protectedBlocks.add(new PublicFurnace(id, owner, guests, loc));
			} else if (type.equalsIgnoreCase("pdi")) {
				protectedBlocks.add(new PublicDispenser(id, owner, guests, loc));
			} else if (type.equalsIgnoreCase("pg")) {
				protectedBlocks.add(new PublicGate(id, owner, guests, loc));
			} else {
				BenCmd.log(Level.WARNING, "Entry " + key + " in protection.db is invalid and was ignored!");
			}
		}
	}

	public void saveAll() {
		for (ProtectedBlock b : protectedBlocks) {
			updateValue(b, false, false);
		}
		saveFile();
	}

	public void updateValue(ProtectedBlock block, boolean comment, boolean saveFile) {
		if (block instanceof ProtectedChest) {
			String value;
			String key;
			key = ((comment) ? "." : "") + String.valueOf(block.getId());
			value = "";
			value += "c/";
			boolean init = false;
			for (String guest : block.getGuests()) {
				if (init) {
					value += ",";
				} else {
					init = true;
				}
				value += guest;
			}
			value += "/" + block.getOwner();
			Location blockLoc = block.getLocation();
			value += "/" + blockLoc.getWorld().getName() + "," + String.valueOf(blockLoc.getBlockX()) + "," + String.valueOf(blockLoc.getBlockY()) + "," + String.valueOf(blockLoc.getBlockZ());
			getFile().put(key, value);
		} else if (block instanceof ProtectedDoor) {
			String value;
			String key;
			key = ((comment) ? "." : "") + String.valueOf(block.getId());
			value = "";
			value += "d/";
			boolean init = false;
			for (String guest : block.getGuests()) {
				if (init) {
					value += ",";
				} else {
					init = true;
				}
				value += guest;
			}
			value += "/" + block.getOwner();
			Location blockLoc = block.getLocation();
			value += "/" + blockLoc.getWorld().getName() + "," + String.valueOf(blockLoc.getBlockX()) + "," + String.valueOf(blockLoc.getBlockY()) + "," + String.valueOf(blockLoc.getBlockZ());
			getFile().put(key, value);
		} else if (block instanceof ProtectedFurnace) {
			String value;
			String key;
			key = ((comment) ? "." : "") + String.valueOf(block.getId());
			value = "";
			value += "f/";
			boolean init = false;
			for (String guest : block.getGuests()) {
				if (init) {
					value += ",";
				} else {
					init = true;
				}
				value += guest;
			}
			value += "/" + block.getOwner();
			Location blockLoc = block.getLocation();
			value += "/" + blockLoc.getWorld().getName() + "," + String.valueOf(blockLoc.getBlockX()) + "," + String.valueOf(blockLoc.getBlockY()) + "," + String.valueOf(blockLoc.getBlockZ());
			getFile().put(key, value);
		} else if (block instanceof ProtectedDispenser) {
			String value;
			String key;
			key = ((comment) ? "." : "") + String.valueOf(block.getId());
			value = "";
			value += "di/";
			boolean init = false;
			for (String guest : block.getGuests()) {
				if (init) {
					value += ",";
				} else {
					init = true;
				}
				value += guest;
			}
			value += "/" + block.getOwner();
			Location blockLoc = block.getLocation();
			value += "/" + blockLoc.getWorld().getName() + "," + String.valueOf(blockLoc.getBlockX()) + "," + String.valueOf(blockLoc.getBlockY()) + "," + String.valueOf(blockLoc.getBlockZ());
			getFile().put(key, value);
		} else if (block instanceof ProtectedGate) {
			String value;
			String key;
			key = ((comment) ? "." : "") + String.valueOf(block.getId());
			value = "";
			value += "g/";
			boolean init = false;
			for (String guest : block.getGuests()) {
				if (init) {
					value += ",";
				} else {
					init = true;
				}
				value += guest;
			}
			value += "/" + block.getOwner();
			Location blockLoc = block.getLocation();
			value += "/" + blockLoc.getWorld().getName() + "," + String.valueOf(blockLoc.getBlockX()) + "," + String.valueOf(blockLoc.getBlockY()) + "," + String.valueOf(blockLoc.getBlockZ());
			getFile().put(key, value);
		} else if (block instanceof PublicChest) {
			String value;
			String key;
			key = ((comment) ? "." : "") + String.valueOf(block.getId());
			value = "";
			value += "pc/";
			boolean init = false;
			for (String guest : block.getGuests()) {
				if (init) {
					value += ",";
				} else {
					init = true;
				}
				value += guest;
			}
			value += "/" + block.getOwner();
			Location blockLoc = block.getLocation();
			value += "/" + blockLoc.getWorld().getName() + "," + String.valueOf(blockLoc.getBlockX()) + "," + String.valueOf(blockLoc.getBlockY()) + "," + String.valueOf(blockLoc.getBlockZ());
			getFile().put(key, value);
		} else if (block instanceof PublicDoor) {
			String value;
			String key;
			key = ((comment) ? "." : "") + String.valueOf(block.getId());
			value = "";
			value += "pd/";
			boolean init = false;
			for (String guest : block.getGuests()) {
				if (init) {
					value += ",";
				} else {
					init = true;
				}
				value += guest;
			}
			value += "/" + block.getOwner();
			Location blockLoc = block.getLocation();
			value += "/" + blockLoc.getWorld().getName() + "," + String.valueOf(blockLoc.getBlockX()) + "," + String.valueOf(blockLoc.getBlockY()) + "," + String.valueOf(blockLoc.getBlockZ());
			getFile().put(key, value);
		} else if (block instanceof PublicFurnace) {
			String value;
			String key;
			key = ((comment) ? "." : "") + String.valueOf(block.getId());
			value = "";
			value += "pf/";
			boolean init = false;
			for (String guest : block.getGuests()) {
				if (init) {
					value += ",";
				} else {
					init = true;
				}
				value += guest;
			}
			value += "/" + block.getOwner();
			Location blockLoc = block.getLocation();
			value += "/" + blockLoc.getWorld().getName() + "," + String.valueOf(blockLoc.getBlockX()) + "," + String.valueOf(blockLoc.getBlockY()) + "," + String.valueOf(blockLoc.getBlockZ());
			getFile().put(key, value);
		} else if (block instanceof PublicDispenser) {
			String value;
			String key;
			key = ((comment) ? "." : "") + String.valueOf(block.getId());
			value = "";
			value += "pdi/";
			boolean init = false;
			for (String guest : block.getGuests()) {
				if (init) {
					value += ",";
				} else {
					init = true;
				}
				value += guest;
			}
			value += "/" + block.getOwner();
			Location blockLoc = block.getLocation();
			value += "/" + blockLoc.getWorld().getName() + "," + String.valueOf(blockLoc.getBlockX()) + "," + String.valueOf(blockLoc.getBlockY()) + "," + String.valueOf(blockLoc.getBlockZ());
			getFile().put(key, value);
		} else if (block instanceof PublicGate) {
			String value;
			String key;
			key = ((comment) ? "." : "") + String.valueOf(block.getId());
			value = "";
			value += "pg/";
			boolean init = false;
			for (String guest : block.getGuests()) {
				if (init) {
					value += ",";
				} else {
					init = true;
				}
				value += guest;
			}
			value += "/" + block.getOwner();
			Location blockLoc = block.getLocation();
			value += "/" + blockLoc.getWorld().getName() + "," + String.valueOf(blockLoc.getBlockX()) + "," + String.valueOf(blockLoc.getBlockY()) + "," + String.valueOf(blockLoc.getBlockZ());
			getFile().put(key, value);
		}
		if (saveFile)
			saveFile();
	}

	public void remValue(Integer id) {
		getFile().remove(id.toString());
		saveFile();
	}

	public int getProtection(Location loc) {
		int id = -1;
		List<ProtectedBlock> q = new ArrayList<ProtectedBlock>();
		for (ProtectedBlock block : protectedBlocks) {
			if (block.getLocation().equals(loc)) {
				id = block.getId();
				break;
			}
			if (block instanceof ProtectedChest) {
				if (((ProtectedChest) block).isDoubleChest() && ((ProtectedChest) block).getSecondChest().getLocation().equals(loc)) {
					id = block.getId();
					break;
				}
			}
			if (block instanceof PublicChest) {
				if (((PublicChest) block).isDoubleChest() && ((PublicChest) block).getSecondChest().getLocation().equals(loc)) {
					id = block.getId();
					break;
				}
			}
			if (block instanceof ProtectedDoor) {
				try {
					if (((ProtectedDoor) block).getSecondBlock().getLocation().equals(loc)) {
						id = block.getId();
						break;
					}
				} catch (NullPointerException e) {
					BenCmd.log(Level.WARNING, block.getId() + " has a missing secondary block. It will be quarantined...");
					q.add(block);
				}
				try {
					if (((ProtectedDoor) block).getBelowBlock().getLocation().equals(loc)) {
						id = block.getId();
						break;
					}
				} catch (NullPointerException e) {
					BenCmd.log(Level.WARNING, block.getId() + " has a missing secondary block. It will be quarantined...");
					q.add(block);
				}
			}
			if (block instanceof PublicDoor) {
				try {
					if (((PublicDoor) block).getSecondBlock().getLocation().equals(loc)) {
						id = block.getId();
						break;
					}
				} catch (NullPointerException e) {
					BenCmd.log(Level.WARNING, block.getId() + " has a missing secondary block. It will be quarantined...");
					q.add(block);
				}
				try {
					if (((PublicDoor) block).getBelowBlock().getLocation().equals(loc)) {
						id = block.getId();
						break;
					}
				} catch (NullPointerException e) {
					BenCmd.log(Level.WARNING, block.getId() + " has a missing secondary block. It will be quarantined...");
					q.add(block);
				}
			}
		}
		for (ProtectedBlock block : q) {
			removeProtection(block.getLocation());
			updateValue(block, true, true);
		}
		return id;
	}

	public int getProtectionIndex(int id) {
		for (int i = 0; i < protectedBlocks.size(); i++) {
			if (protectedBlocks.get(i).getId() == id) {
				return i;
			}
		}
		return -1;
	}

	public boolean protectionExists(int id) {
		for (ProtectedBlock block : protectedBlocks) {
			if (block.getId() == id) {
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

	public int addProtection(String owner, Location loc, ProtectionType type) {
		int id = getNextId();
		ProtectedBlock protect = null;
		switch (type) {
			case Chest:
				protectedBlocks.add(protect = new ProtectedChest(id, owner, new ArrayList<String>(), loc));
				break;
			case Door:
				protectedBlocks.add(protect = new ProtectedDoor(id, owner, new ArrayList<String>(), loc));
				break;
			case Furnace:
				protectedBlocks.add(protect = new ProtectedFurnace(id, owner, new ArrayList<String>(), loc));
				break;
			case Dispenser:
				protectedBlocks.add(protect = new ProtectedDispenser(id, owner, new ArrayList<String>(), loc));
				break;
			case Gate:
				protectedBlocks.add(protect = new ProtectedGate(id, owner, new ArrayList<String>(), loc));
				break;
			case PDoor:
				protectedBlocks.add(protect = new PublicDoor(id, owner, new ArrayList<String>(), loc));
				break;
			case PChest:
				protectedBlocks.add(protect = new PublicChest(id, owner, new ArrayList<String>(), loc));
				break;
			case PFurnace:
				protectedBlocks.add(protect = new PublicFurnace(id, owner, new ArrayList<String>(), loc));
				break;
			case PDispenser:
				protectedBlocks.add(protect = new PublicDispenser(id, owner, new ArrayList<String>(), loc));
				break;
			case PGate:
				protectedBlocks.add(protect = new PublicGate(id, owner, new ArrayList<String>(), loc));
				break;
		}
		updateValue(protect, false, true);
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
			ind = getProtection(id).getId();
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

	public void changeOwner(int id, String newOwner) {
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

	public void addGuest(int id, String newGuest) {
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

	public void removeGuest(int id, String oldGuest) {
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
		Chest, Door, Furnace, Dispenser, Gate, PDoor, PChest, PFurnace, PDispenser, PGate
	}
}