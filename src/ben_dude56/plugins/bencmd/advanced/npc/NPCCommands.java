package ben_dude56.plugins.bencmd.advanced.npc;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ben_dude56.plugins.bencmd.BenCmd;
import ben_dude56.plugins.bencmd.Commands;
import ben_dude56.plugins.bencmd.User;

public class NPCCommands implements Commands {

	private BenCmd plugin;

	public NPCCommands(BenCmd instance) {
		plugin = instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String commandLabel, String[] args) {
		User user;
		try {
			user = User.getUser(plugin, (Player) sender);
		} catch (ClassCastException e) {
			user = User.getUser(plugin);
		}
		if (commandLabel.equalsIgnoreCase("npc") && user.hasPerm("canEditNpcs")) {
			Npc(args, user);
			return true;
		}
		return false;
	}

	public void Npc(String[] args, User user) {
		if (args.length == 0) {
			user.sendMessage(ChatColor.YELLOW
					+ "Proper usage: /npc {bank|bupgrade|blacksmith|remove [id]|despawnall}");
			user.sendMessage(ChatColor.YELLOW
					+ "TIP: Right-click an NPC with a stick to get info about that NPC");
			return;
		}
		if (args[0].equalsIgnoreCase("bank")) {
			plugin.npcs.addNPC(new BankerNPC(plugin, plugin.npcs.nextId(), user
					.getHandle().getLocation()));
		} else if (args[0].equalsIgnoreCase("bupgrade")) {
			plugin.npcs.addNPC(new BankManagerNPC(plugin, plugin.npcs.nextId(),
					user.getHandle().getLocation()));
		} else if (args[0].equalsIgnoreCase("blacksmith")) {
			plugin.npcs.addNPC(new BlacksmithNPC(plugin, plugin.npcs.nextId(),
					user.getHandle().getLocation(), null, null));
		} else if (args[0].equalsIgnoreCase("rep")) {
			if (args.length <= 2) {
				user.sendMessage(ChatColor.YELLOW
						+ "Proper usage: /npc rep [id] [cost]");
				return;
			}
			NPC npc = plugin.npcs.getNPC(Integer.parseInt(args[1]));
			if (npc == null) {
				user.sendMessage(ChatColor.RED + "No NPC with that ID exists!");
				return;
			}
			if (!(npc instanceof BlacksmithNPC)) {
				user.sendMessage(ChatColor.RED
						+ "The NPC with that ID isn't a blacksmith!");
				return;
			}
			if (user.getHandle().getItemInHand() == null) {
				user.sendMessage(ChatColor.RED
						+ "You must be holding an item to do that!");
				return;
			}
			((BlacksmithNPC) npc).addItem(user.getHandle().getItemInHand(),
					Double.parseDouble(args[2]), user.getHandle());
		} else if (args[0].equalsIgnoreCase("remove")) {
			if (args.length == 1) {
				user.sendMessage(ChatColor.YELLOW
						+ "Proper usage: /npc remove [id]");
				return;
			}
			plugin.npcs.remNPC(plugin.npcs.getNPC(Integer.parseInt(args[1])));
		} else if (args[0].equalsIgnoreCase("despawnall")) {
			for (NPC npc : plugin.npcs.allNPCs()) {
				npc.despawn();
			}
		}
	}

}