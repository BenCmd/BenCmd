package ben_dude56.plugins.bencmd.lots;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import ben_dude56.plugins.bencmd.BenCmd;
import ben_dude56.plugins.bencmd.permissions.PermissionUser;

public class LotFile extends Properties {

	private static final long serialVersionUID = 1L;
	File file;
	BenCmd plugin;
	static final Logger log = Logger.getLogger("minecraft");
	HashMap<String, Lot> lot = new HashMap<String, Lot>();

	public LotFile(BenCmd instance) {
		plugin = instance;
		this.reload(); // Load the values into memory.
	}

	/**
	 * This method reloads the lots database from the hard drive.
	 */
	public void reload() {
		file = new File("plugins/BenCmd/lots.db"); // Prepare the file
		if (!file.exists()) {
			try {
				file.createNewFile(); // If the file doesn't exist, create it!
			} catch (IOException ex) {
				// If you can't, produce an error.
				log.severe("BenCmd had a problem:");
				ex.printStackTrace();
				return;
			}
		}
		try {
			load(new FileInputStream(file)); // Load the values
		} catch (IOException ex) {
			// If you can't, produce an error.
			log.severe("BenCmd had a problem:");
			ex.printStackTrace();
		}
		lot.clear();
		for (Object key : this.keySet()) {
			String LotIDString = (String) key;
			try {
				lot.put(LotIDString,
						new Lot(plugin, LotIDString, (String) this
								.get(LotIDString)));
			} catch (NumberFormatException e) {
			}
		}
	}

	/**
	 * This method saves all of the lots.
	 */
	public void save() {
		file = new File("plugins/BenCmd/lots.db"); // Prepare the file
		if (!file.exists()) {
			try {
				file.createNewFile(); // If the file doesn't exist, create it!
			} catch (IOException ex) {
				// If you can't, produce an error.
				log.severe("BenCmd had a problem:");
				ex.printStackTrace();
				return;
			}
		}
		this.clear();
		for (Lot lotVal : lot.values()) {
			Location corner1, corner2;
			corner1 = lotVal.getCorner1();
			corner2 = lotVal.getCorner2();
			
			String LotID = lotVal.getFullID();
			String value = "";
			value += corner1.getBlockX() + ",";
			value += corner1.getBlockY() + ",";
			value += corner1.getBlockZ() + ",";
			value += corner1.getWorld().getName() + ",";
			value += corner2.getBlockX() + ",";
			value += corner2.getBlockY() + ",";
			value += corner2.getBlockZ() + ",";
			value += corner2.getWorld().getName();
			if (lotVal.getSubID().equalsIgnoreCase("0")) {
				String owner = lotVal.getOwner();
				String group = lotVal.getLotGroup();
				List<String> guests = lotVal.guests;
				value += "," + owner + ",";
				value += group;
				int i = 0;
				while (i<guests.size()) {
					value += "," + guests.get(i);
					i++;
				}
			}
			this.put(LotID, value);
		}	
		try {
			store(new FileOutputStream(file), "BenCmd Lots File");	// Save
																	// the
																	// values
		} catch (IOException ex) {
			// If you can't, produce an error.
			log.severe("BenCmd had a problem:");
			ex.printStackTrace();
		}
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
	public boolean addLot(String LotID, Location corner1, Location corner2,
			String owner, String group) {
		try {
			String value = "";
			value += corner1.getBlockX() + ",";
			value += corner1.getBlockY() + ",";
			value += corner1.getBlockZ() + ",";
			value += corner1.getWorld().getName() + ",";
			value += corner2.getBlockX() + ",";
			value += corner2.getBlockY() + ",";
			value += corner2.getBlockZ() + ",";
			value += corner2.getWorld().getName();
			if (LotID.split(",")[1].equalsIgnoreCase("0")){
				value +=  "," + owner + ",";
				value += group;
			}
			lot.put(LotID, new Lot(plugin, LotID, value));
			save();
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
		if (LotID.split(",").length==1)
			SubID = "-1";
		else
			SubID = LotID.split(",")[1];
		LotID = LotID.split(",")[0];
		if (SubID.equalsIgnoreCase("-1")) {
			try { 
				for (String key : plugin.lots.getLot(LotID).getSubs())
					lot.remove(key);
					save();
			} catch (Exception e) {
				return false; // An unknown error was encountered!
			}
			return true;
		}
		else if (this.lotExists((LotID + "," + SubID))) {
			if (SubID.equalsIgnoreCase("0") && this.lotExists(LotID + ",1")) {
				Lot oldLot = this.getLot(LotID + ",0");
				Lot newLot = this.getLot(LotID + ",1");
				Location corner1 = newLot.getCorner1();
				Location corner2 = newLot.getCorner2();
				String owner = oldLot.getOwner();
				String group = oldLot.getGroup();
				List<String> guests = oldLot.guests;
				try {
					lot.remove((LotID + ",0"));
					this.addLot(LotID + ",0", corner1, corner2, owner, group);
					this.getLot(LotID + ",0").guests = guests;
					lot.remove(LotID + ",1");
					save();
				} catch (Exception e) {
					return false;
				}
				this.sortSubs(LotID);
				save();
			}
			else {
				try {
					lot.remove((LotID + "," + SubID));
					save();
				} catch (Exception e) {
					return false;
				}
				this.sortSubs(LotID);
				save();
			}
			return true;
		}
		return false;
	}

	/**
	 * This method is used to check whether a lot exists in the lot database.
	 * 
	 * @param lotID
	 *            The ID of the lot to check for.
	 * @return This method returns whether the user exists.
	 */
	
	public boolean lotExists(String LotID) {
		if (LotID.split(",").length==1) {
			LotID = LotID + ",0";
		}
		else {
			LotID = LotID.split(",")[0] + "," + LotID.split(",")[1];
		}
		if (lot.containsKey(LotID))
			return true;
		else
			return false;
	}

	public String isInLot(Location loc) {
		for (Lot lotVal : lot.values()) {
			if (lotVal.withinLot(loc)) {
				return lotVal.getFullID();
			}
		}
		return "-1";
	}

	public Lot getLot(String LotID) {
		if (LotID.split(",").length==1)
			LotID = LotID + ",0";
		LotID = LotID.split(",")[0] + "," + LotID.split(",")[1];
		return lot.get(LotID);
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
			if (!this.containsKey(String.valueOf(i) + ",0")) {
				return (String.valueOf(i) + ",0");
			}
			i++;
		}

	}
	
	public String getNextSubID(String LotID) {
		int i = 0;
		while (true) {
			if (!this.containsKey(LotID + "," + i)) {
				return String.valueOf(i);
			}
			i++;
		}
	}
	
	public void selectionSort(int[] numbers) {
		int min, temp;
		
		for (int index=0; index < numbers.length-1; index++) {
			min = index;
			for (int scan = index+1; scan < numbers.length; scan++)
				if (numbers[scan] < numbers[min])
					min = scan;
			
			temp = numbers[min];
			numbers[min] = numbers[index];
			numbers[index] = temp;
			}
		}
	public void insertionSort (int[] numbers)
	{
		for (int index = 0; index < numbers.length; index++)
		{
			int key = numbers[index];
			int position = index;
			
			while (position > 0 && numbers[position-1] > key)
			{
				numbers[position] = numbers[position-1];
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
		for (String lot : this.getLot(LotID).getSubs()) {
			try { 
				sub = Integer.parseInt(lot.split(",")[1]);
			} catch (NumberFormatException e) {
				log.severe("A lot's sub-id is formatted wrong!");
				return;
			}
			if (sub > max)
				max = sub;
		}
		for (i=0; i<=max; i++) {
			if (lotExists((LotID + "," + i))) {
				if (sort>0) {
					this.addLot((LotID + "," + (i-sort)), 
							this.getLot(LotID + "," + i).getCorner1(),
							this.getLot(LotID + "," + i).getCorner2(),
							this.getLot(LotID + "," + i).getOwner(),
							this.getLot(LotID + "," + i).getGroup());
					this.lot.remove((LotID + "," + i));
				}
			}
			else
				sort++;
		}
	}
	
	public boolean canBuildHere(Player player, Location location) {
		boolean inLot = false;
		for (String LotID : lot.keySet()) {
			if (plugin.lots.getLot(LotID).withinLot(location)) {
				inLot = true;
				if (plugin.lots.getLot(LotID).canBuild(player)) {
					return true;
				}
			}
		}
		if (inLot) {
			return false;
		}
		else {
			return true;
		}
	}
	public String ownsHere(Player player, Location location) {
		boolean inLot = false;
		for (String LotID : lot.keySet()) {
			if (plugin.lots.getLot(LotID).withinLot(location)) {
				inLot = true;
				PermissionUser user = PermissionUser.matchUser(player.getName(), plugin);
				if (user == null) {
					return "noUser";
				}
				if (plugin.lots.getLot(LotID).isOwner(player) || user.hasPerm("isLandlord")) {
					return plugin.lots.getLot(LotID).getLotID();
				}
			}
		}
		if (inLot) {
			return "false";
		}
		else
		{
			return "noLot";
		}
	}
}
