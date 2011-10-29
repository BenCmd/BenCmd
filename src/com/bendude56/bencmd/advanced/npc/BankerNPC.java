package com.bendude56.bencmd.advanced.npc;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.advanced.bank.BankInventory;

public class BankerNPC extends NPC implements Clickable {

	public BankerNPC(int id, Location l) {
		super("Banker", id, l, new ItemStack(Material.PAPER));
	}

	@Override
	public String getSkinURL() {
		return "http://s3.amazonaws.com/squirt/i4e2869fa774793133401218182131713162132.png";
	}

	@Override
	public void onRightClick(Player p) {
		if (!BenCmd.getBankController().hasBank(p.getName())) {
			BenCmd.getBankController().addBank(new BankInventory(p.getName()));
		}
		BenCmd.log(p.getName() + " has opened their bank!");
		BenCmd.getBankController().openInventory(p);
	}

	@Override
	public void onLeftClick(Player p) {

	}

	@Override
	public String getValue() {
		Location l = super.getLocation();
		return "b|" + l.getWorld().getName() + "," + l.getX() + "," + l.getY() + "," + l.getZ() + "," + l.getYaw() + "," + l.getPitch();
	}

}
