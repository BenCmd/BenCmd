package com.bendude56.bencmd.advanced;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.User;

public class Grave {
	public static List<Grave>						graves			= new ArrayList<Grave>();
	public static HashMap<Player, List<ItemStack>>	returns			= new HashMap<Player, List<ItemStack>>();
	
	
	private Block			g;
	private Player			p;
	private List<ItemStack>	i;
	private int				t;
	private int				d;

	public Grave(Block grave, Player player, List<ItemStack> items, int secondsToPickUp) {
		g = grave;
		p = player;
		i = new ArrayList<ItemStack>();
		i.addAll(items);
		t = secondsToPickUp;
	}

	public Block getBlock() {
		return g;
	}

	public Player getPlayer() {
		return p;
	}

	public void tick() {
		User u = User.getUser(p);
		t--;
		if (t == 0) {
			delete();
			BenCmd.getLocale().sendMessage(u, "misc.grave.crumble");
			Grave.graves.remove(this);
		} else if (t % 60 == 0) {
			if (t == 60) {
				BenCmd.getLocale().sendMessage(u, "misc.grave.crumbleWarning", BenCmd.getLocale().getString("misc.grave.minute"));
			} else {
				BenCmd.getLocale().sendMessage(u, "misc.grave.crumbleWarning", BenCmd.getLocale().getString("misc.grave.minutes", (t / 60) + ""));
			}
		} else if (t % 15 == 0 && t < 60) {
			BenCmd.getLocale().sendMessage(u, "misc.grave.crumbleWarning", BenCmd.getLocale().getString("misc.grave.seconds", (t / 60) + ""));
		} else if (t == 10) {
			BenCmd.getLocale().sendMessage(u, "misc.grave.crumbleWarning", BenCmd.getLocale().getString("misc.grave.seconds", "10"));
		} else if (t == 5) {
			BenCmd.getLocale().sendMessage(u, "misc.grave.crumbleWarning", "5");
		} else if (t < 5) {
			BenCmd.getLocale().sendMessage(u, "misc.grave.finalSeconds", t + "");
		}
	}

	public boolean destroyBy(Player player) {
		User u = User.getUser(p);
		User u2 = User.getUser(player);
		if (p.equals(player)) {
			for (ItemStack i : this.i) {
				if (BenCmd.getMainProperties().getBoolean("destroyCurrencyOnDeath", false) && BenCmd.getMarketController().isCurrency(i)) {
					continue;
				}
				p.getInventory().addItem(i);
			}
			delete();
			Grave.graves.remove(this);
			BenCmd.getLocale().sendMessage(u, "misc.grave.success");
			return true;
		} else {
			if (u2.hasPerm("bencmd.grave.destroy")) {
				delete();
				Grave.graves.remove(this);
				BenCmd.getLocale().sendMessage(u, "misc.grave.adminSmash");
				return true;
			} else {
				BenCmd.getLocale().sendMessage(u2, "misc.grave.cannotDestroy");
				return false;
			}
		}
	}

	public void delete() {
		Bukkit.getServer().getScheduler().cancelTask(d);
		g.setType(Material.AIR);
	}
}
