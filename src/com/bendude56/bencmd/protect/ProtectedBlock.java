package com.bendude56.bencmd.protect;

import java.util.List;

import org.bukkit.Location;

import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.permissions.PermissionUser;

public class ProtectedBlock {
	private int						idNumber;
	private PermissionUser			blockOwner;
	private List<PermissionUser>	blockGuests;
	private Location				blockLocation;

	public ProtectedBlock(int id, PermissionUser owner, List<PermissionUser> guests, Location loc) {
		idNumber = id;
		blockGuests = guests;
		blockOwner = owner;
		blockLocation = loc;
	}

	public int GetId() {
		return idNumber;
	}

	public PermissionUser getOwner() {
		return blockOwner;
	}

	public List<PermissionUser> getGuests() {
		return blockGuests;
	}

	public Location getLocation() {
		return blockLocation;
	}

	public boolean canUse(PermissionUser user) {
		return (blockOwner.getName().equalsIgnoreCase(user.getName()) || isGuest(user) != -1);
	}

	public boolean canChange(PermissionUser user) {
		if (blockOwner.getName().equalsIgnoreCase(user.getName())) {
			return true;
		} else {
			return false;
		}
	}

	public boolean setOwner(PermissionUser user) {
		blockOwner = user;
		BenCmd.getProtections().updateValue(this, false, true);
		return true;
	}

	public boolean addGuest(PermissionUser guest) {
		if (isGuest(guest) == -1) {
			blockGuests.add(guest);
			BenCmd.getProtections().updateValue(this, false, true);
			return true;
		} else {
			return false;
		}
	}

	public int isGuest(PermissionUser user) {
		for (int i = 0; i < blockGuests.size(); i++) {
			PermissionUser guest = blockGuests.get(i);
			if (guest.getName().equalsIgnoreCase(user.getName())) {
				return i;
			}
		}
		return -1;
	}

	public boolean removeGuest(PermissionUser guest) {
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
