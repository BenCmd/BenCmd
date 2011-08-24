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
								"bencmd.npc.info")) {
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
		if (plugin.spoutcraft && plugin.spoutconnect.enabled(p)) {
			plugin.spoutconnect.showNPCScreen(p, n);
		} else {
			p.sendMessage(ChatColor.GRAY + "NPC ID: " + n.getID());
			if (n instanceof BankerNPC) {
				p.sendMessage(ChatColor.GRAY + "NPC Type: Banker");
			} else if (n instanceof BankManagerNPC) {
				p.sendMessage(ChatColor.GRAY + "NPC Type: Bank Manager");
			} else if (n instanceof BlacksmithNPC) {
				p.sendMessage(ChatColor.GRAY + "NPC Type: Blacksmith");
			} else if (n instanceof StaticNPC) {
				p.sendMessage(ChatColor.GRAY + "NPC Type: Static");
			} else {
				p.sendMessage(ChatColor.GRAY + "NPC Type: Unknown");
			}
			p.sendMessage(ChatColor.GRAY + "NPC Name: " + n.getName());
			p.sendMessage(ChatColor.GRAY + "Skin URL: " + ((StaticNPC)n).getSkinURL());
		}
	}
}
