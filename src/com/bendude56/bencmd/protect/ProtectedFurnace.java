package com.bendude56.bencmd.protect;

import java.util.List;

import org.bukkit.Location;

import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.permissions.PermissionUser;


public class ProtectedFurnace extends ProtectedBlock {
	public ProtectedFurnace(BenCmd instance, int id, PermissionUser owner,
			List<PermissionUser> guests, Location loc) {
		super(instance, id, owner, guests, loc);
	}
}