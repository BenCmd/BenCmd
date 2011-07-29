package ben_dude56.plugins.bencmd.lots;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import ben_dude56.plugins.bencmd.BenCmd;
import ben_dude56.plugins.bencmd.User;

public class Lot {
	Location corner1;
	Location corner2;
	String owner;
	String group;
	List<String> guests;
	String LotID;
	String SubID;
	String FullID;
	org.bukkit.World World;
	BenCmd plugin;

	public Lot(BenCmd instance, String key, String value)
			throws NumberFormatException {
		LotID = key.split(",")[0];
		SubID = key.split(",")[1];
		FullID = key;
		plugin = instance;
		corner1 = new Location(
				plugin.getServer().getWorld(value.split(",")[3]),
				Integer.parseInt(value.split(",")[0]), Integer.parseInt(value
						.split(",")[1]), Integer.parseInt(value.split(",")[2]));
		corner2 = new Location(
				plugin.getServer().getWorld(value.split(",")[7]),
				Integer.parseInt(value.split(",")[4]), Integer.parseInt(value
						.split(",")[5]), Integer.parseInt(value.split(",")[6]));
		World = corner1.getWorld();
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
			return plugin.lots.getLot(LotID).getOwner();
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
			return plugin.lots.getLot(LotID).getLotGroup();
	}

	public List<String> getSubs() {
		List<String> subs;
		subs = new ArrayList<String>();
		for (String lot : plugin.lots.lot.keySet()) {
			if (lot.split(",")[0].equalsIgnoreCase(LotID)) {
				subs.add(lot);
			}
		}
		return subs;
	}

	public boolean withinLot(Location loc) {
		if (this.getWorld() != loc.getWorld()) {
			return false;
		}
		if (plugin.lots.isBetween(corner1.getBlockX(), loc.getBlockX(),
				corner2.getBlockX())
				&& plugin.lots.isBetween(corner1.getBlockZ(), loc.getBlockZ(),
						corner2.getBlockZ())
				&& plugin.lots.isBetween(corner1.getBlockY(), loc.getBlockY(),
						corner2.getBlockY())) {
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
			return plugin.lots.getLot(LotID).isOwner(player);
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
			return plugin.lots.getLot(LotID).isGuest(guest);
		}
	}

	public boolean canBuild(Player player) {
		User user = User.getUser(plugin, player);
		String group;
		if (!SubID.equalsIgnoreCase("0")) {
			group = plugin.lots.getLot(LotID).getGroup();
		} else {
			group = getGroup();
		}
		if (isOwner(player) || isGuest(player.getName())
				|| user.hasPerm("isLandlord")
				|| user.inGroup(plugin.perm.groupFile.getGroup(group))) {
			return true;
		} else
			return false;
	}

	public void addGuest(String guest) {
		if (SubID.equalsIgnoreCase("0")) {
			if (!isGuest(guest)) {
				guests.add(guest);
				plugin.lots.save();
			}
		} else
			plugin.lots.getLot(LotID).addGuest(guest);
	}

	public List<String> getGuests() {
		if (SubID.equalsIgnoreCase("0"))
			return guests;
		else
			return plugin.lots.getLot(LotID).getGuests();
	}

	public void deleteGuest(String guest) {
		if (SubID.equalsIgnoreCase("0")) {
			if (isGuest(guest)) {
				guests.remove(guest);
				plugin.lots.save();
			}
		} else
			plugin.lots.getLot(LotID).deleteGuest(guest);
	}

	public void setGroup(String newGroup) {
		if (SubID.equalsIgnoreCase("0")) {
			group = newGroup;
			plugin.lots.save();
		} else
			plugin.lots.getLot(LotID).setGroup(newGroup);
	}

	public String getGroup() {
		if (SubID.equalsIgnoreCase("0"))
			return group;
		else
			return plugin.lots.getLot(LotID).getGroup();
	}

	public void setOwner(String newOwner) {
		if (SubID.equalsIgnoreCase("0")) {
			owner = newOwner;
			plugin.lots.save();
		} else
			plugin.lots.getLot(LotID).setOwner(newOwner);
	}

	public void listGuests(User user) {
		if (SubID.equalsIgnoreCase("0")) {
			if (guests.size() == 0) {
				user.sendMessage("Lot " + LotID + " has no guests.");
				return;
			} else {
				user.sendMessage("Lot " + LotID
						+ " has the following guests: (" + guests.size() + ")");
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
			plugin.lots.getLot(LotID).listGuests(user);
		}
	}

	public boolean clearGuests() {
		guests.clear();
		plugin.lots.save();
		return true;
	}

	public void setSubID(String id) { // BE EXTREMELY CAREFUL WITH THIS
		SubID = id;
		FullID = LotID + "," + SubID;

	}
}