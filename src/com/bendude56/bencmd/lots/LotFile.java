package com.bendude56.bencmd.lots;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.BenCmdFile;
import com.bendude56.bencmd.User;
import com.bendude56.bencmd.permissions.PermissionUser;

public class LotFile extends BenCmdFile {
	// TODO Use lot instances instead of Strings!

	protected HashMap<String, Lot>	lots	= new HashMap<String, Lot>();

	public LotFile() {
		super("lots.db", "--BenCmd Lot File--", true);
		loadFile();
		loadAll(); // Load the values into memory.
	}

	public void loadAll() {
		lots.clear();
		for (Object key : getFile().keySet()) {
			String LotIDString = (String) key;
			try {
				lots.put(LotIDString, new Lot(LotIDString, (String) getFile().get(LotIDString)));
			} catch (NumberFormatException e) {}
		}
	}

	/**
	 * This method saves all of the lots.
	 */
	public void saveAll() {
		for (Lot lotVal : lots.values()) {
			saveLot(lotVal, false);
		}
		saveFile();
	}

	public void saveLot(Lot lot, boolean saveFile) {
		Location corner1, corner2;
		corner1 = lot.getCorner1();
		corner2 = lot.getCorner2();

		String LotID = lot.getFullID();
		String value = "";
		value += corner1.getBlockX() + ",";
		value += corner1.getBlockY() + ",";
		value += corner1.getBlockZ() + ",";
		value += corner1.getWorld().getName() + ",";
		value += corner2.getBlockX() + ",";
		value += corner2.getBlockY() + ",";
		value += corner2.getBlockZ() + ",";
		value += corner2.getWorld().getName();
		if (lot.getSubID().equalsIgnoreCase("0")) {
			String owner = lot.getOwner();
			String group = lot.getLotGroup();
			List<String> guests = lot.guests;
			value += "," + owner + ",";
			value += group;
			int i = 0;
			while (i < guests.size()) {
				value += "," + guests.get(i);
				i++;
			}
		}
		getFile().put(LotID, value);
		if (saveFile)
			saveFile();
	}

	/**
	 * This method adds a new lot to the database.
	 * 
	 * @param player
	 *            The name of the lot owner to add to the database
	 * @param corner1
	 *            The first corner
	 * @param corner2
	 *            The opposite corner
	 * 
	 */
	public boolean addLot(String LotID, Location corner1, Location corner2, String owner, String group) {
		try {
			// TODO Make a Lot constructor that doesn't require this!
			String value = "";
			value += corner1.getBlockX() + ",";
			value += corner1.getBlockY() + ",";
			value += corner1.getBlockZ() + ",";
			value += corner1.getWorld().getName() + ",";
			value += corner2.getBlockX() + ",";
			value += corner2.getBlockY() + ",";
			value += corner2.getBlockZ() + ",";
			value += corner2.getWorld().getName();
			if (LotID.split(",")[1].equalsIgnoreCase("0")) {
				value += "," + owner + ",";
				value += group;
			}
			Lot l;
			lots.put(LotID, l = new Lot(LotID, value));
			saveLot(l, true);
			return true;
		} catch (Exception e) {
			return false; // An unknown error was encountered!
		}
	}

	/**
	 * This method removes a lot from the database.
	 * 
	 * @param lotID
	 *            The ID of the lot to remove from the database
	 */

	public boolean deleteLot(String LotID) {
		if (!this.lotExists(LotID))
			return false; // The lot doesn't exist and cannot be deleted
		String SubID;
		if (LotID.split(",").length == 1)
			SubID = "-1";
		else
			SubID = LotID.split(",")[1];
		LotID = LotID.split(",")[0];
		if (SubID.equalsIgnoreCase("-1")) {
			try {
				for (Lot l : getLot(LotID).getSubs())
					deleteLot(l.getFullID());
			} catch (Exception e) {
				return false; // An unknown error was encountered!
			}
		} else if (this.lotExists(LotID + "," + SubID)) {
			// TODO Clean this up!
			if (SubID.equalsIgnoreCase("0") && this.lotExists(LotID + ",1")) {
				Lot oldLot = this.getLot(LotID + ",0");
				Lot newLot = this.getLot(LotID + ",1");
				Location corner1 = newLot.getCorner1();
				Location corner2 = newLot.getCorner2();
				String owner = oldLot.getOwner();
				String group = oldLot.getGroup();
				List<String> guests = oldLot.guests;
				try {
					lots.remove((LotID + ",0"));
					this.addLot(LotID + ",0", corner1, corner2, owner, group);
					this.getLot(LotID + ",0").guests = guests;
					lots.remove(LotID + ",1");
					saveLot(this.getLot(LotID + ",0"), false);
				} catch (Exception e) {
					return false;
				}
				this.sortSubs(LotID);
				saveFile();
			} else {
				try {
					lots.remove(LotID + "," + SubID);
					this.sortSubs(LotID);
					saveFile();
				} catch (Exception e) {
					return false;
				}
			}
		} else {
			return false;
		}
		return true;
	}

	/**
	 * This method is used to check whether a lot exists in the lot database.
	 * 
	 * @param lotID
	 *            The ID of the lot to check for.
	 * @return This method returns whether the user exists.
	 */

	public boolean lotExists(String LotID) {
		if (LotID.split(",").length == 1) {
			LotID = LotID + ",0";
		} else {
			LotID = LotID.split(",")[0] + "," + LotID.split(",")[1];
		}
		if (lots.containsKey(LotID))
			return true;
		else
			return false;
	}

	public String isInLot(Location loc) {
		for (Lot lotVal : lots.values()) {
			if (lotVal.withinLot(loc)) {
				return lotVal.getFullID();
			}
		}
		return "-1";
	}

	public Lot getLot(String LotID) {
		if (LotID.split(",").length == 1)
			LotID = LotID + ",0";
		LotID = LotID.split(",")[0] + "," + LotID.split(",")[1];
		return lots.get(LotID);
	}

	public boolean isBetween(int top, int middle, int bottom) {
		if (top >= bottom && middle <= top && middle >= bottom)
			return true;
		if (top <= bottom && middle >= top && middle <= bottom)
			return true;
		return false;
	}

	public String getNextID() {
		int i = 0;
		while (true) {
			if (!getFile().containsKey(String.valueOf(i) + ",0")) {
				return (String.valueOf(i) + ",0");
			}
			i++;
		}

	}

	public String getNextSubID(String LotID) {
		int i = 0;
		while (true) {
			if (!getFile().containsKey(LotID + "," + i)) {
				return String.valueOf(i);
			}
			i++;
		}
	}

	public void selectionSort(int[] numbers) {
		int min, temp;

		for (int index = 0; index < numbers.length - 1; index++) {
			min = index;
			for (int scan = index + 1; scan < numbers.length; scan++)
				if (numbers[scan] < numbers[min])
					min = scan;

			temp = numbers[min];
			numbers[min] = numbers[index];
			numbers[index] = temp;
		}
	}

	public void insertionSort(int[] numbers) {
		for (int index = 0; index < numbers.length; index++) {
			int key = numbers[index];
			int position = index;

			while (position > 0 && numbers[position - 1] > key) {
				numbers[position] = numbers[position - 1];
				position--;
			}
		}
	}

	public void sortSubs(String LotID) {
		int sort = 0;
		int i;
		int max = 0;
		int sub;
		if (!this.lotExists(LotID))
			return;
		for (Lot l : this.getLot(LotID).getSubs()) {
			try {
				sub = Integer.parseInt(l.FullID.split(",")[1]);
			} catch (NumberFormatException e) {
				BenCmd.log(Level.SEVERE, "A lot's sub-id is formatted wrong!");
				return;
			}
			if (sub > max)
				max = sub;
		}
		for (i = 0; i <= max; i++) {
			if (lotExists((LotID + "," + i))) {
				if (sort > 0) {
					Lot l = this.getLot(LotID + "," + i);
					this.addLot((LotID + "," + (i - sort)), l.getCorner1(), l.getCorner2(), l.getOwner(), l.getGroup());
					this.lots.remove(LotID + "," + i);
				}
			} else {
				sort++;
			}
		}
		saveFile();
	}

	public boolean canBuildHere(Player player, Location location) {
		boolean inLot = false;
		for (String LotID : lots.keySet()) {
			if (getLot(LotID).withinLot(location)) {
				inLot = true;
				if (getLot(LotID).canBuild(player)) {
					return true;
				}
			}
		}
		if (inLot) {
			return false;
		} else {

			User user = User.getUser(player);

			if (BenCmd.getMainProperties().getBoolean("useGlobalLot", false) && !user.hasPerm("bencmd.lot.globalguest")) {
				return false;
			} else {
				return true;
			}
		}
	}

	// TODO Return Enum instead of String!
	public String ownsHere(Player player, Location location) {
		boolean inLot = false;
		for (String LotID : lots.keySet()) {
			if (getLot(LotID).withinLot(location)) {
				inLot = true;
				PermissionUser user = PermissionUser.matchUser(player.getName());
				if (user == null) {
					return "noUser";
				}
				if (getLot(LotID).isOwner(player) || user.hasPerm("bencmd.lot.buildall")) {
					return getLot(LotID).getLotID();
				}
			}
		}
		if (inLot) {
			return "false";
		} else {
			return "noLot";
		}
	}

	public List<Lot> getLots(boolean subs) {
		List<Lot> list = new ArrayList<Lot>();
		for (String l : lots.keySet()) {
			if (getLot(l).getSubID().equalsIgnoreCase("0") || subs) {
				list.add(getLot(l));
			}
		}
		return list;
	}

	public List<Lot> getLots(Location loc, boolean subs) {
		List<Lot> list = new ArrayList<Lot>();
		for (String l : lots.keySet()) {
			if ((getLot(l).getSubID().equalsIgnoreCase("0") || subs) && getLot(l).withinLot(loc)) {
				list.add(getLot(l));
			}
		}
		return list;
	}

	public List<Lot> getLotsByOwner(String player) {
		List<Lot> list = new ArrayList<Lot>();
		for (String l : lots.keySet()) {
			if (getLot(l).getSubID().equalsIgnoreCase("0") && getLot(l).getOwner().equalsIgnoreCase(player)) {
				list.add(getLot(l));
			}
		}
		return list;
	}

	public List<Lot> getLotsByGuest(String player) {
		List<Lot> list = new ArrayList<Lot>();
		for (String l : lots.keySet()) {
			if (getLot(l).getSubID().equalsIgnoreCase("0") && getLot(l).isGuest(player)) {
				list.add(getLot(l));
			}
		}
		return list;
	}

	public List<Lot> getLotsByPermission(String player) {
		List<Lot> list = new ArrayList<Lot>();
		for (String l : lots.keySet()) {
			if (getLot(l).getSubID().equalsIgnoreCase("0") && getLot(l).canBuild(player)) {
				list.add(getLot(l));
			}
		}
		return list;
	}
}
