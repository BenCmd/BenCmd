package ben_dude56.plugins.bencmd.lots.sparea;

import org.bukkit.ChatColor;
import org.bukkit.Location;

import ben_dude56.plugins.bencmd.BenCmd;

public class PVPArea extends MsgArea {
	private int mm;

	public PVPArea(BenCmd instance, String key, String value)
			throws NumberFormatException, NullPointerException,
			IndexOutOfBoundsException {
		super(instance, key, value);
		mm = Integer.parseInt(value.split("/")[5]);
	}

	public PVPArea(BenCmd instance, Integer id, Location corner1,
			Location corner2, String enter, String leave, int minMoney) {
		super(instance, id, corner1, corner2, ChatColor.RED + enter,
				ChatColor.GREEN + leave);
		mm = minMoney;
	}

	public PVPArea(BenCmd instance, Integer id, Location corner1,
			Location corner2, int minMoney) {
		super(
				instance,
				id,
				corner1,
				corner2,
				ChatColor.RED
						+ "You have entered a PVP Area! Other players can now attack you!",
				ChatColor.RED
						+ "You have left a PVP Area! Other players can no longer attack you...");
		mm = minMoney;
	}

	public int getMinimumCurrency() {
		return mm;
	}

	public String getValue() {
		return "pvp" + super.getInternalValue() + "/" + mm;
	}

	public void setEnterMessage(String value) {
		if (!value.startsWith("§")) {
			value = ChatColor.RED + value;
		}
		super.setEnterMessage(value);
	}

	public void setLeaveMessage(String value) {
		if (!value.startsWith("§")) {
			value = ChatColor.RED + value;
		}
		super.setLeaveMessage(value);
	}
}
