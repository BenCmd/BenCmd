package ben_dude56.plugins.bencmd.advanced.npc;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
		if (commandLabel.equalsIgnoreCase("npc") && user.hasPerm("bencmd.npc.create")) {
			Npc(args, user);
			return true;
		}
		return false;
	}

	public void Npc(String[] args, User user) {
		if (args.length == 0) {
			user.sendMessage(ChatColor.YELLOW
					+ "Proper usage: /npc {bank|bupgrade|blacksmith|static [name] [skin]|remove [id]|despawnall}");
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
		} else if (args[0].equalsIgnoreCase("static")) {
			if (args.length == 1) {
				plugin.npcs.addNPC(new SkinnableNPC(plugin, "Unnamed NPC", "", plugin.npcs.nextId(),
						user.getHandle().getLocation(), new ItemStack(Material.AIR)));
			} else if (args.length == 2) {
				plugin.npcs.addNPC(new SkinnableNPC(plugin, args[1].replace('-', ' '), args[1].replace('-', ' '), plugin.npcs.nextId(),
						user.getHandle().getLocation(), new ItemStack(Material.AIR)));
			} else if (args.length == 3) {
				plugin.npcs.addNPC(new SkinnableNPC(plugin, args[1].replace('-', ' '), args[2], plugin.npcs.nextId(),
						user.getHandle().getLocation(), new ItemStack(Material.AIR)));
			} else {
				user.sendMessage(ChatColor.YELLOW
						+ "Proper usage: /npc static [name] [skin]");
			}
		} else if (args[0].equalsIgnoreCase("skin")) {
			if (args.length == 3) {
				NPC npc = plugin.npcs.getNPC(Integer.parseInt(args[1]));
				if (npc == null) {
					user.sendMessage(ChatColor.RED + "No NPC with that ID exists!");
					return;
				} else if (!(npc instanceof SkinnableNPC)) {
					user.sendMessage(ChatColor.RED + "The NPC with that ID cannot have a custom skin!");
					return;
				}
				((SkinnableNPC)npc).setSkin(args[2]);
				user.sendMessage(ChatColor.GREEN + "That NPCs skin was changed successfully!");
				if (!plugin.spoutcraft || !plugin.spoutconnect.enabled(user.getHandle())) {
					user.sendMessage(ChatColor.YELLOW + "Please note: To see this change, you must first install SpoutCraft!");
				}
			} else {
				user.sendMessage(ChatColor.YELLOW
						+ "Proper usage: /npc skin <id> <skin>");
			}
		} else if (args[0].equalsIgnoreCase("item")) {
			NPC npc = plugin.npcs.getNPC(Integer.parseInt(args[1]));
			if (npc == null) {
				user.sendMessage(ChatColor.RED + "No NPC with that ID exists!");
				return;
			} else if (!(npc instanceof SkinnableNPC)) {
				user.sendMessage(ChatColor.RED + "The NPC with that ID cannot have a custom item!");
				return;
			}
			if (user.getHandle().getInventory().getItemInHand() == null) {
				user.sendMessage(ChatColor.RED + "You must hold an item to do that!");
				return;
			}
			npc.setHeldItem(user.getHandle().getInventory().getItemInHand());
			user.sendMessage(ChatColor.GREEN + "That NPCs item was changed successfully!");
		} else if (args[0].equalsIgnoreCase("name")) {
			if (args.length == 3) {
				NPC npc = plugin.npcs.getNPC(Integer.parseInt(args[1]));
				if (npc == null) {
					user.sendMessage(ChatColor.RED + "No NPC with that ID exists!");
					return;
				} else if (!(npc instanceof SkinnableNPC)) {
					user.sendMessage(ChatColor.RED + "The NPC with that ID cannot have a custom name!");
					return;
				}
				((SkinnableNPC)npc).setName(args[2].replace('-', ' '));
				user.sendMessage(ChatColor.GREEN + "That NPCs name was changed successfully!");
			} else {
				user.sendMessage(ChatColor.YELLOW
						+ "Proper usage: /npc name <id> <name>");
			}
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
			if (!user.hasPerm("bencmd.npc.remove")) {
				user.sendMessage(ChatColor.RED
						+ "You don't have permission to do that!");
				return;
			}
			if (args.length == 1) {
				user.sendMessage(ChatColor.YELLOW
						+ "Proper usage: /npc remove [id]");
				return;
			}
			plugin.npcs.remNPC(plugin.npcs.getNPC(Integer.parseInt(args[1])));
		} else if (args[0].equalsIgnoreCase("despawnall")) {
			if (!user.hasPerm("bencmd.npc.despawnall")) {
				user.sendMessage(ChatColor.RED
						+ "You don't have permission to do that!");
				return;
			}
			for (NPC npc : plugin.npcs.allNPCs()) {
				npc.despawn();
			}
		}
	}

}
