package com.bendude56.bencmd.protect;

import java.util.List;

import org.bukkit.Location;

import com.bendude56.bencmd.BenCmd;

public class ProtectedBlock {
	private int						idNumber;
	private String					blockOwner;
	private List<String>			blockGuests;
	private Location				blockLocation;

	public ProtectedBlock(int id, String owner, List<String> guests, Location loc) {
		idNumber = id;
		blockGuests = guests;
		blockOwner = owner;
		blockLocation = loc;
	}

	public int GetId() {
		return idNumber;
	}

	public String getOwner() {
		return blockOwner;
	}

	public List<String> getGuests() {
		return blockGuests;
	}

	public Location getLocation() {
		return blockLocation;
	}

	public boolean canUse(String user) {
		return (blockOwner.equalsIgnoreCase(user) || isGuest(user) != -1);
	}

	public boolean canChange(String user) {
		if (blockOwner.equalsIgnoreCase(user)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean setOwner(String user) {
		blockOwner = user;
		BenCmd.getProtections().updateValue(this, false, true);
		return true;
	}

	public boolean addGuest(String guest) {
		if (isGuest(guest) == -1) {
			blockGuests.add(guest);
			BenCmd.getProtections().updateValue(this, false, true);
			return true;
		} else {
			return false;
		}
	}

	public int isGuest(String user) {
		for (int i = 0; i < blockGuests.size(); i++) {
			String guest = blockGuests.get(i);
			if (guest.equalsIgnoreCase(user)) {
				return i;
			}
		}
		return -1;
	}

	public boolean removeGuest(String guest) {
		int id;
		if ((id = isGuest(guest)) != -1) {
			blockGuests.remove(id);
			BenCmd.getProtections().updateValue(this, false, true);
			return true;
		} else {
			return false;
		}
	}
}
