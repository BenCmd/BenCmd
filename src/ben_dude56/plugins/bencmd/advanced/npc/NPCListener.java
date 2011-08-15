package ben_dude56.plugins.bencmd.advanced.npc;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerListener;

import ben_dude56.plugins.bencmd.BenCmd;
import ben_dude56.plugins.bencmd.User;

public class NPCListener extends PlayerListener {
	private BenCmd plugin;

	public NPCListener(BenCmd instance) {
		plugin = instance;
	}

	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		if (event.getRightClicked() instanceof CraftPlayer) {
			if (((CraftPlayer) event.getRightClicked()).getHandle() instanceof EntityNPC) {
				NPC npc = plugin.npcs.getNPC((EntityNPC) ((CraftPlayer) event
						.getRightClicked()).getHandle());
				if (npc == null) {
					plugin.log
							.warning("Ghost NPC detected... Try restarting the server...");
					plugin.bLog.warning("NPC ERROR: A Ghost NPC was detected!");
					return;
				}
				if (event.getPlayer().getItemInHand().getType() == Material.STICK
						&& User.getUser(plugin, event.getPlayer()).hasPerm(
								"canEditNpcs")) {
					info(event.getPlayer(), npc);
					return;
				}
				if (npc instanceof Clickable) {
					((Clickable) npc).onRightClick(event.getPlayer());
				}
			}
		}
	}

	public void info(Player p, NPC n) {
		p.sendMessage(ChatColor.GRAY + "NPC ID: " + n.getID());
		if (n instanceof BankerNPC) {
			p.sendMessage(ChatColor.GRAY + "NPC Type: Banker");
		} else if (n instanceof BankManagerNPC) {
			p.sendMessage(ChatColor.GRAY + "NPC Type: Bank Manager");
		} else if (n instanceof BlacksmithNPC) {
			p.sendMessage(ChatColor.GRAY + "NPC Type: Blacksmith");
		}
	}
}
