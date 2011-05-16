package ben_dude56.plugins.bencmd.invisible;

import java.util.Timer;

import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import ben_dude56.plugins.bencmd.BenCmd;

import net.minecraft.server.Packet20NamedEntitySpawn;
import net.minecraft.server.Packet29DestroyEntity;

public class Invisibility {
	BenCmd plugin;
	public Timer timer;

	public Invisibility(BenCmd instance) {
		plugin = instance;
		timer = new Timer();
		timer.schedule(new InvisibilityTask(this), 0, 10);
	}

	public void invisible(Player p1, Player p2) {
		CraftPlayer hide = (CraftPlayer) p1;
		CraftPlayer hideFrom = (CraftPlayer) p2;
		hideFrom.getHandle().netServerHandler
				.sendPacket(new Packet29DestroyEntity(hide.getEntityId()));
	}

	public void uninvisible(Player p1, Player p2) {
		CraftPlayer unHide = (CraftPlayer) p1;
		CraftPlayer unHideFrom = (CraftPlayer) p2;
		unHideFrom.getHandle().netServerHandler
				.sendPacket(new Packet20NamedEntitySpawn(unHide.getHandle()));
	}

	public void remInv(Player player) {
		plugin.invisible.remove(player);
		for (Player appearTo : plugin.getServer().getOnlinePlayers()) {
			if (!plugin.noinvisible.contains(appearTo) && player != appearTo) {
				uninvisible(player, appearTo);
			}
		}
	}

	public void addInv(Player player) {
		plugin.invisible.add(player);
	}

	public void showAll(Player player) {
		for (Player appear : plugin.invisible) {
			if (player != appear) {
				uninvisible(appear, player);
			}
		}
	}

	public void addNoInv(Player player) {
		plugin.noinvisible.add(player);
		showAll(player);
	}

	public void remNoInv(Player player) {
		plugin.noinvisible.remove(player);
		for (Player disappear : plugin.invisible) {
			if (player != disappear) {
				invisible(disappear, player);
			}
		}
	}
}
