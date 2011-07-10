package ben_dude56.plugins.bencmd.advanced;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import ben_dude56.plugins.bencmd.BenCmd;
import ben_dude56.plugins.bencmd.User;

public class Grave {
	private Block g;
	private Player p;
	private List<ItemStack> i;
	private BenCmd plugin;
	private int t;
	private Timer d;

	public Grave(BenCmd instance, Block grave, Player player,
			List<ItemStack> items, int secondsToPickUp) {
		plugin = instance;
		g = grave;
		p = player;
		i = new ArrayList<ItemStack>();
		i.addAll(items);
		t = secondsToPickUp;
		d = new Timer();
		d.schedule(new TimerTask() {
			@Override
			public void run() {
				tick();
			}
		}, 0, 1000);
	}

	public Block getBlock() {
		return g;
	}

	public Player getPlayer() {
		return p;
	}

	public void tick() {
		t--;
		if (t == 0) {
			delete();
			p.sendMessage(ChatColor.RED
					+ "Your grave has crumbled into dust, taking your items along with it...");
			plugin.graves.remove(this);
		} else if (t % 60 == 0) {
			if (t == 60) {
				p.sendMessage(ChatColor.RED
						+ "Your grave will crumble in 1 minute...");
			} else {
				p.sendMessage(ChatColor.RED + "Your grave will crumble in " + t
						/ 60 + " minutes...");
			}
		} else if (t % 15 == 0 && t < 60) {
			p.sendMessage(ChatColor.RED + "Your grave will crumble in " + t
					+ " seconds...");
		} else if (t == 10) {
			p.sendMessage(ChatColor.RED
					+ "Your grave will crumble in 10 seconds...");
		} else if (t == 5) {
			p.sendMessage(ChatColor.RED + "Your grave will crumble in 5...");
		} else if (t < 5) {
			p.sendMessage(ChatColor.RED + "" + t + "...");
		}
	}

	public boolean destroyBy(Player player) {
		if (p.equals(player)) {
			for (ItemStack i : this.i) {
				if (plugin.mainProperties.getBoolean("destroyCurrencyOnDeath",
						true) && plugin.prices.isCurrency(i)) {
					continue;
				}
				p.getInventory().addItem(i);
			}
			delete();
			p.sendMessage(ChatColor.GREEN
					+ "You've reached your grave in time and you items are safe!");
			return true;
		} else {
			User user = User.getUser(plugin, player);
			if (user.hasPerm("canDestroyGraves")) {
				plugin.graves.remove(this);
				p.sendMessage(ChatColor.RED
						+ "Your grave has been crushed by an admin, taking your items along with it...");
				return true;
			} else {
				player.sendMessage(ChatColor.RED
						+ "You cannot destroy someone else's grave!");
				return false;
			}
		}
	}

	public void delete() {
		d.cancel();
		g.setType(Material.AIR);
	}
}