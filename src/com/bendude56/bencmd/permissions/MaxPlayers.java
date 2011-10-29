package com.bendude56.bencmd.permissions;

import java.util.ArrayList;
import java.util.List;

import com.bendude56.bencmd.User;

public class MaxPlayers {
	private int						max;
	private int						reservemax;
	private boolean					reserveActive;
	private boolean					indefActive;
	private List<PermissionUser>	normalList;
	private List<PermissionUser>	reservedList;

	public MaxPlayers(int maxPlayers, int maxReserve, boolean allowReserve, boolean allowIndef) {
		max = maxPlayers;
		reserveActive = allowReserve;
		indefActive = allowIndef;
		reservemax = maxReserve;
		normalList = new ArrayList<PermissionUser>();
		reservedList = new ArrayList<PermissionUser>();
	}

	public JoinType join(PermissionUser user) {
		if (user.hasPerm("bencmd.slot.always") && indefActive) {
			return JoinType.SLOT_AVAILABLE;
		} else if (user.hasPerm("bencmd.slot.reserved") && reserveActive) {
			if (reservedList.size() < reservemax) {
				reservedList.add(user);
				return JoinType.SLOT_AVAILABLE;
			} else if (normalList.size() < max) {
				normalList.add(user);
				return JoinType.SLOT_AVAILABLE;
			} else {
				return JoinType.NO_SLOT_RESERVED;
			}
		} else {
			if (normalList.size() < max) {
				normalList.add(user);
				return JoinType.SLOT_AVAILABLE;
			} else {
				return JoinType.NO_SLOT_NORMAL;
			}
		}
	}

	public void leave(User user) {
		for (int i = 0; i < normalList.size(); i++) {
			if (user.getName().equalsIgnoreCase(normalList.get(i).getName())) {
				normalList.remove(i);
				return;
			}
		}
		for (int i = 0; i < reservedList.size(); i++) {
			if (user.getName().equalsIgnoreCase(reservedList.get(i).getName())) {
				reservedList.remove(i);
				return;
			}
		}
	}

	public enum JoinType {
		SLOT_AVAILABLE, NO_SLOT_NORMAL, NO_SLOT_RESERVED
	}
}
