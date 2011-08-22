package ben_dude56.plugins.bencmd.advanced.npc;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import ben_dude56.plugins.bencmd.BenCmd;
import ben_dude56.plugins.bencmd.advanced.bank.BankInventory;

public class BankerNPC extends NPC implements Clickable {

	public BankerNPC(BenCmd instance, int id, Location l) {
		super(instance, "Banker", id, l);
	}
	
	@Override
	public String getSkinURL() {
		return "http://s3.amazonaws.com/squirt/i4e2869fa774793133401218182131713162132.png";
	}

	@Override
	public void onRightClick(Player p) {
		if (!plugin.banks.hasBank(p.getName())) {
			plugin.banks.addBank(new BankInventory(p.getName(), plugin));
		}
		plugin.banks.openInventory(p);
	}

	@Override
	public void onLeftClick(Player p) {

	}

	@Override
	public String getValue() {
		Location l = super.getLocation();
		return "b|" + l.getWorld().getName() + "," + l.getX() + "," + l.getY()
				+ "," + l.getZ() + "," + l.getYaw() + "," + l.getPitch();
	}

}
