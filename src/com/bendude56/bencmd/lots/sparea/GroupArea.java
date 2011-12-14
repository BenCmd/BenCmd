package com.bendude56.bencmd.lots.sparea;

import org.bukkit.Location;

import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.User;

public class GroupArea extends SPArea {
	
	private String group;

	public GroupArea(Integer id, Location corner1, Location corner2, String group) {
		super(id, corner1, corner2);
		this.group = group;
	}

	public GroupArea(String key, String value) throws NumberFormatException, NullPointerException, IndexOutOfBoundsException {
		super(key, value);
		group = value.split("/")[3];
	}
	
	public String getValue() {
		return "grp" + super.getInternalValue() + group;
	}
	
	public String getGroup() {
		return group;
	}
	
	public void setGroup(String group) {
		this.group = group;
		BenCmd.getAreas().updateArea(this, true);
	}
	
	public boolean canEnter(User u) {
		return !BenCmd.getPermissionManager().getGroupFile().groupExists(group) || u.inGroup(BenCmd.getPermissionManager().getGroupFile().getGroup(group));
	}

}
