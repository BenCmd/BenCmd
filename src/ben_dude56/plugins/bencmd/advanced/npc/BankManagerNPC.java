package ben_dude56.plugins.bencmd.advanced.npc;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import ben_dude56.plugins.bencmd.BenCmd;
import ben_dude56.plugins.bencmd.User;
import ben_dude56.plugins.bencmd.advanced.bank.BankInventory;
import ben_dude56.plugins.bencmd.money.BuyableItem;

public class BankManagerNPC extends NPC implements Clickable {

	public BankManagerNPC(BenCmd instance, int id, Location l) {
		super(instance, "Bank Manager", id, l);
	}
	
	@Override
	public String getSkinURL() {
		return "http://s3.amazonaws.com/squirt/i4e4d4fbbb8f288300604535471306130503032.png";
	}

	@Override
	public void onRightClick(Player p) {
		if (User.getUser(super.plugin, p).hasPerm("bencmd.bank.admin")) {
			p.sendMessage(ChatColor.RED
					+ "Admins cannot use this NPC to upgrade banks, use /bank upgrade instead!");
			return;
		}
		if (!plugin.banks.hasBank(p.getName())) {
			plugin.banks.addBank(new BankInventory(p.getName(), plugin));
		}
		if (plugin.banks.getBank(p.getName()).isUpgraded()) {
			p.sendMessage(ChatColor.RED
					+ "Your bank has already been upgraded!");
		} else {
			if (BuyableItem.hasMoney(User.getUser(plugin, p),
					plugin.mainProperties.getDouble("bankUpgradeCost", 4096),
					plugin)) {
				BuyableItem.remMoney(User.getUser(plugin, p),
						plugin.mainProperties
								.getDouble("bankUpgradeCost", 4096), plugin);
				plugin.banks.upgradeBank(p.getName());
				p.sendMessage(ChatColor.GREEN + "Enjoy the extra bank space!");
			} else {
				p.sendMessage(ChatColor.RED
						+ "You need at least "
						+ plugin.mainProperties.getDouble("bankUpgradeCost",
								4096)
						+ " worth of currency to upgrade your bank!");
			}
		}
	}

	@Override
	public void onLeftClick(Player p) {

	}

	@Override
	public String getValue() {
		Location l = super.getLocation();
		return "m/" + l.getWorld().getName() + "," + l.getX() + "," + l.getY()
				+ "," + l.getZ() + "," + l.getYaw() + "," + l.getPitch();
	}

}
