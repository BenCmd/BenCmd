package com.bendude56.bencmd.protect;

import java.util.List;

import org.bukkit.Location;

import com.bendude56.bencmd.permissions.PermissionUser;


public class ProtectedGate extends ProtectedBlock {
	public ProtectedGate(int id, PermissionUser owner,
			List<PermissionUser> guests, Location loc) {
		super(id, owner, guests, loc);
	}
}
