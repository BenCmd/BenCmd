package com.bendude56.bencmd.lots;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.User;

public class Lot {

	// TODO Privatize these and add getters/setters where needed!
	Location			corner1;
	Location			corner2;
	String				owner;
	String				group;
	List<String>		guests;
	String				LotID;
	String				SubID;
	String				FullID;
	org.bukkit.World	World;
	
	boolean				DISABLED;
	String				originalWorld = "world";
	boolean				GLOBALLOT;

	public Lot(String key, String value) throws NumberFormatException {
		World = Bukkit.getWorld(value.split(",")[3]);
		if (World == null) {
			DISABLED = true;
			originalWorld = value.split(",")[3];
			World = Bukkit.getWorlds().get(0);
		}
		LotID = key.split(",")[0];
		SubID = key.split(",")[1];
		FullID = key;
		corner1 = new Location(World, Double.parseDouble(value.split(",")[0]), Double.parseDouble(value.split(",")[1]), Double.parseDouble(value.split(",")[2]));
		corner2 = new Location(World, Double.parseDouble(value.split(",")[4]), Double.parseDouble(value.split(",")[5]), Double.parseDouble(value.split(",")[6]));
		if (corner1.getX() == 1.1) {
			GLOBALLOT = true;
		}
		if (SubID.equalsIgnoreCase("0")) {
			owner = value.split(",")[8];
			group = value.split(",")[9];
			guests = new ArrayList<String>();
			int i = 10;
			while (i < value.split(",").length) {
				guests.add(value.split(",")[i]);
				i++;
			}
		}
	}

	// TODO Constructor that takes variables directly

	public Location getCorner1() {
		return corner1;
	}

	public Location getCorner2() {
		return corner2;
	}

	public org.bukkit.World getWorld() {
		return World;
	}

	public String getOwner() {
		if (getSubID().equalsIgnoreCase("0")) {
			return owner;
		} else {
			return BenCmd.getLots().getLot(LotID).getOwner();
		}
	}

	public String getLotID() {
		return LotID;
	}

	public String getFullID() {
		return FullID;
	}

	public String getSubID() {
		return SubID;
	}

	public String getLotGroup() {
		if (getSubID().equalsIgnoreCase("0"))
			return group;
		else
			return BenCmd.getLots().getLot(LotID).getLotGroup();
	}

	public List<Lot> getSubs() {
		List<Lot> subs = new ArrayList<Lot>();
		for (Map.Entry<String, Lot> e : BenCmd.getLots().lots.entrySet()) {
			if (e.getKey().split(",")[0].equalsIgnoreCase(LotID)) {
				subs.add(e.getValue());
			}
		}
		return subs;
	}

	public boolean withinLot(Location loc) {
		if (this.DISABLED) {
			return false;
		}
		if (this.GLOBALLOT && loc.getWorld() == World) {
			return true;
		}
		if (this.getWorld() != loc.getWorld()) {
			return false;
		}
		if (BenCmd.getLots().isBetween(corner1.getBlockX(), loc.getBlockX(), corner2.getBlockX()) && BenCmd.getLots().isBetween(corner1.getBlockZ(), loc.getBlockZ(), corner2.getBlockZ()) && BenCmd.getLots().isBetween(corner1.getBlockY(), loc.getBlockY(), corner2.getBlockY())) {
			return true;
		} else
			return false;
	}

	public boolean isOwner(Player player) {
		if (SubID.equalsIgnoreCase("0")) {
			if (player.getName().equalsIgnoreCase(getOwner())) {
				return true;
			} else {
				return false;
			}
		} else {
			return BenCmd.getLots().getLot(LotID).isOwner(player);
		}
	}

	public boolean isGuest(String guest) {
		if (SubID.equalsIgnoreCase("0")) {
			if (guests.contains(guest)) {
				return true;
			} else {
				return false;
			}
		} else {
			return BenCmd.getLots().getLot(LotID).isGuest(guest);
		}
	}

	public boolean canBuild(Player player) {
		User user = User.getUser(player);
		String group;
		if (!SubID.equalsIgnoreCase("0")) {
			group = BenCmd.getLots().getLot(LotID).getGroup();
		} else {
			group = getGroup();
		}
		if (isOwner(player) || isGuest(player.getName()) || user.hasPerm("bencmd.lot.buildall") || user.inGroup(BenCmd.getPermissionManager().getGroupFile().getGroup(group))) {
			return true;
		} else
			return false;
	}

	public boolean canBuild(String p) {
		Player player = Bukkit.getPlayerExact(p);
		User user = User.getUser(player);
		String group;
		if (!SubID.equalsIgnoreCase("0")) {
			group = BenCmd.getLots().getLot(LotID).getGroup();
		} else {
			group = getGroup();
		}
		if (isOwner(player) || isGuest(player.getName()) || user.hasPerm("bencmd.lot.buildall") || user.inGroup(BenCmd.getPermissionManager().getGroupFile().getGroup(group))) {
			return true;
		} else
			return false;
	}

	public void addGuest(String guest) {
		if (SubID.equalsIgnoreCase("0")) {
			if (!isGuest(guest)) {
				guests.add(guest);
				BenCmd.getLots().saveLot(this, true);
			}
		} else
			BenCmd.getLots().getLot(LotID).addGuest(guest);
	}

	public List<String> getGuests() {
		if (SubID.equalsIgnoreCase("0"))
			return guests;
		else
			return BenCmd.getLots().getLot(LotID).getGuests();
	}

	public void deleteGuest(String guest) {
		if (SubID.equalsIgnoreCase("0")) {
			if (isGuest(guest)) {
				guests.remove(guest);
				BenCmd.getLots().saveLot(this, true);
			}
		} else
			BenCmd.getLots().getLot(LotID).deleteGuest(guest);
	}

	public void setGroup(String newGroup) {
		if (SubID.equalsIgnoreCase("0")) {
			group = newGroup;
			BenCmd.getLots().saveLot(this, true);
		} else
			BenCmd.getLots().getLot(LotID).setGroup(newGroup);
	}

	public String getGroup() {
		if (SubID.equalsIgnoreCase("0"))
			return group;
		else
			return BenCmd.getLots().getLot(LotID).getGroup();
	}

	public void setOwner(String newOwner) {
		if (SubID.equalsIgnoreCase("0")) {
			owner = newOwner;
			BenCmd.getLots().saveLot(this, true);
		} else
			BenCmd.getLots().getLot(LotID).setOwner(newOwner);
	}

	public void listGuests(User user) {
		if (SubID.equalsIgnoreCase("0")) {
			if (guests.size() == 0) {
				user.sendMessage("Lot " + LotID + " has no guests.");
				return;
			} else {
				user.sendMessage("Lot " + LotID + " has the following guests: (" + guests.size() + ")");
			}
			String list = "";
			int i = 0, r = 0;
			for (String guest : guests) {
				list += guest;
				i++;
				r++;
				if (r < guests.size())
					list += ",  ";
				else
					list += ".";
				if (i >= 3) {
					user.sendMessage(list);
					i = 0;
					list = "";
				}
			}
			if (!list.equalsIgnoreCase("")) {
				user.sendMessage(list);
			}
		} else {
			BenCmd.getLots().getLot(LotID).listGuests(user);
		}
	}

	public boolean clearGuests() {
		guests.clear();
		BenCmd.getLots().saveLot(this, true);
		return true;
	}

	public void setSubID(String id) { // BE EXTREMELY CAREFUL WITH THIS
		SubID = id;
		FullID = LotID + "," + SubID;

	}
}