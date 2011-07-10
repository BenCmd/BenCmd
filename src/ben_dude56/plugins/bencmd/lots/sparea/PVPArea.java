package ben_dude56.plugins.bencmd.lots.sparea;

import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import ben_dude56.plugins.bencmd.BenCmd;
import ben_dude56.plugins.bencmd.invtools.BCItem;

public class PVPArea extends MsgArea {
	private int mm;
	private DropMode dmn;
	private DropMode dmc;
	private DropTable t;

	public PVPArea(BenCmd instance, String key, String value)
			throws NumberFormatException, NullPointerException,
			IndexOutOfBoundsException {
		super(instance, key, value);
		mm = Integer.parseInt(value.split("/")[5]);
		String dms = value.split("/")[6];
		switch (dms.charAt(0)) {
		case 'd':
			dmn = DropMode.DROP;
			break;
		case 'l':
			dmn = DropMode.LOSE;
			break;
		case 'k':
			dmn = DropMode.KEEP;
			break;
		default:
			throw new NoSuchElementException("Drop mode (normal) invalid!");
		}
		switch (dms.charAt(1)) {
		case 'd':
			dmc = DropMode.DROP;
			break;
		case 'l':
			dmc = DropMode.LOSE;
			break;
		case 'k':
			dmc = DropMode.KEEP;
			break;
		default:
			throw new NoSuchElementException("Drop mode (normal) invalid!");
		}
		try {
			t = new DropTable(instance, value.split("/")[7]);
		} catch (IndexOutOfBoundsException e) {
			t = new DropTable(instance, "");
		}
	}

	public PVPArea(BenCmd instance, Integer id, Location corner1,
			Location corner2, String enter, String leave, int minMoney) {
		super(instance, id, corner1, corner2, ChatColor.RED + enter,
				ChatColor.RED + leave);
		mm = minMoney;
		dmn = DropMode.DROP;
		dmc = DropMode.DROP;
		t = new DropTable(instance, "");
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
		dmn = DropMode.DROP;
		dmc = DropMode.DROP;
		t = new DropTable(instance, "");
	}

	public HashMap<ItemStack, DropMode> getDrops(List<ItemStack> inv) {
		HashMap<ItemStack, DropMode> re = new HashMap<ItemStack, DropMode>();
		for (ItemStack i : inv) {
			if (plugin.prices.isCurrency(i)) {
				re.put(i, dmc);
			} else {
				re.put(i, dmn);
			}
		}
		for (ItemStack i : t.getRandomDrops(new Random())) {
			re.put(i, DropMode.DROP);
		}
		return re;
	}

	public void addDrop(BCItem item, DropInfo info) {
		t.addItem(item, info);
		super.plugin.spafile.updateArea(this);
	}

	public void remDrop(BCItem item) {
		t.removeItem(item);
		super.plugin.spafile.updateArea(this);
	}

	public DropInfo getDrop(BCItem item) {
		return t.getInfo(item);
	}

	public int getMinimumCurrency() {
		return mm;
	}

	public String getValue() {
		String r = "pvp" + super.getInternalValue() + "/" + mm + "/";
		switch (dmn) {
		case DROP:
			r += "d";
			break;
		case LOSE:
			r += "l";
			break;
		case KEEP:
			r += "k";
			break;
		}
		switch (dmc) {
		case DROP:
			r += "d";
			break;
		case LOSE:
			r += "l";
			break;
		case KEEP:
			r += "k";
			break;
		}
		r += "/" + t.getValue();
		return r;
	}
	
	public DropMode getCDrop() {
		return dmc;
	}
	
	public DropMode getNDrop() {
		return dmn;
	}
	
	public void setCDrop(DropMode value) {
		dmc = value;
		super.plugin.spafile.updateArea(this);
	}
	
	public void setNDrop(DropMode value) {
		dmn = value;
		super.plugin.spafile.updateArea(this);
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

	public enum DropMode {
		DROP, LOSE, KEEP
	}
}
