package com.bendude56.bencmd.advanced.npc;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.Commands;
import com.bendude56.bencmd.User;

public class NPCCommands implements Commands {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		User user = User.getUser(sender);
		if (commandLabel.equalsIgnoreCase("npc") && user.hasPerm("bencmd.npc.create")) {
			Npc(args, user);
			return true;
		}
		return false;
	}

	public void Npc(String[] args, User user) {
		// TODO Log messages when using /npc
		if (args.length == 0) {
			user.sendMessage(ChatColor.YELLOW + "Proper usage: /npc {bank|bupgrade|blacksmith|static [name] [skin]|remove [id]|despawnall}");
			user.sendMessage(ChatColor.YELLOW + "TIP: Right-click an NPC with a stick to get info about that NPC");
			return;
		}
		if (args[0].equalsIgnoreCase("bank")) {
			BenCmd.getNPCFile().addNPC(new BankerNPC(BenCmd.getNPCFile().nextId(), ((Player) user.getHandle()).getLocation()));
			user.sendMessage(ChatColor.GREEN + "Bank NPC Created!");
		} else if (args[0].equalsIgnoreCase("bupgrade")) {
			BenCmd.getNPCFile().addNPC(new BankManagerNPC(BenCmd.getNPCFile().nextId(), ((Player) user.getHandle()).getLocation()));
			user.sendMessage(ChatColor.GREEN + "Bank Manager NPC Created!");
		} else if (args[0].equalsIgnoreCase("blacksmith")) {
			BenCmd.getNPCFile().addNPC(new BlacksmithNPC(BenCmd.getNPCFile().nextId(), ((Player) user.getHandle()).getLocation(), null, null));
			user.sendMessage(ChatColor.GREEN + "Blacksmith NPC Created!");
		} else if (args[0].equalsIgnoreCase("static")) {
			if (args.length == 1) {
				BenCmd.getNPCFile().addNPC(new StaticNPC("Unnamed NPC", "", BenCmd.getNPCFile().nextId(), ((Player) user.getHandle()).getLocation(), new ItemStack(Material.AIR), true));
				user.sendMessage(ChatColor.GREEN + "Static NPC Created!");
			} else if (args.length == 2) {
				BenCmd.getNPCFile().addNPC(new StaticNPC(args[1].replace('-', ' '), args[1].replace('-', ' '), BenCmd.getNPCFile().nextId(), ((Player) user.getHandle()).getLocation(), new ItemStack(Material.AIR), true));
				user.sendMessage(ChatColor.GREEN + "Static NPC Created!");
			} else if (args.length == 3) {
				BenCmd.getNPCFile().addNPC(new StaticNPC(args[1].replace('-', ' '), args[2], BenCmd.getNPCFile().nextId(), ((Player) user.getHandle()).getLocation(), new ItemStack(Material.AIR), true));
				user.sendMessage(ChatColor.GREEN + "Static NPC Created!");
			} else {
				user.sendMessage(ChatColor.YELLOW + "Proper usage: /npc static [name] [skin]");
			}
		} else if (args[0].equalsIgnoreCase("skin")) {
			if (args.length == 3) {
				NPC npc = BenCmd.getNPCFile().getNPC(Integer.parseInt(args[1]));
				if (npc == null) {
					user.sendMessage(ChatColor.RED + "No NPC with that ID exists!");
					return;
				} else if (!(npc instanceof StaticNPC)) {
					user.sendMessage(ChatColor.RED + "The NPC with that ID cannot have a custom skin!");
					return;
				}
				((StaticNPC) npc).setSkin(args[2]);
				user.sendMessage(ChatColor.GREEN + "That NPCs skin was changed successfully!");
				if (!BenCmd.isSpoutConnected() || !BenCmd.getSpoutConnector().enabled((Player) user.getHandle())) {
					user.sendMessage(ChatColor.YELLOW + "Please note: To see this change, you must first install SpoutCraft!");
				}
			} else {
				user.sendMessage(ChatColor.YELLOW + "Proper usage: /npc skin <id> <skin>");
			}
		} else if (args[0].equalsIgnoreCase("item")) {
			NPC npc = BenCmd.getNPCFile().getNPC(Integer.parseInt(args[1]));
			if (npc == null) {
				user.sendMessage(ChatColor.RED + "No NPC with that ID exists!");
				return;
			} else if (!(npc instanceof StaticNPC)) {
				user.sendMessage(ChatColor.RED + "The NPC with that ID cannot have a custom item!");
				return;
			}
			if (((Player) user.getHandle()).getInventory().getItemInHand() == null) {
				user.sendMessage(ChatColor.RED + "You must hold an item to do that!");
				return;
			}
			npc.setHeldItem(((Player) user.getHandle()).getInventory().getItemInHand());
			user.sendMessage(ChatColor.GREEN + "That NPCs item was changed successfully!");
		} else if (args[0].equalsIgnoreCase("name")) {
			if (args.length == 3) {
				NPC npc = BenCmd.getNPCFile().getNPC(Integer.parseInt(args[1]));
				if (npc == null) {
					user.sendMessage(ChatColor.RED + "No NPC with that ID exists!");
					return;
				} else if (!(npc instanceof StaticNPC)) {
					user.sendMessage(ChatColor.RED + "The NPC with that ID cannot have a custom name!");
					return;
				}
				((StaticNPC) npc).setName(args[2].replace('-', ' '));
				user.sendMessage(ChatColor.GREEN + "That NPCs name was changed successfully!");
			} else {
				user.sendMessage(ChatColor.YELLOW + "Proper usage: /npc name <id> <name>");
			}
		} else if (args[0].equalsIgnoreCase("rep")) {
			if (args.length <= 2) {
				user.sendMessage(ChatColor.YELLOW + "Proper usage: /npc rep [id] [cost]");
				return;
			}
			NPC npc = BenCmd.getNPCFile().getNPC(Integer.parseInt(args[1]));
			if (npc == null) {
				user.sendMessage(ChatColor.RED + "No NPC with that ID exists!");
				return;
			}
			if (!(npc instanceof BlacksmithNPC)) {
				user.sendMessage(ChatColor.RED + "The NPC with that ID isn't a blacksmith!");
				return;
			}
			if (((Player) user.getHandle()).getItemInHand() == null) {
				user.sendMessage(ChatColor.RED + "You must be holding an item to do that!");
				return;
			}
			((BlacksmithNPC) npc).addItem(((Player) user.getHandle()).getItemInHand(), Double.parseDouble(args[2]), ((Player) user.getHandle()));
		} else if (args[0].equalsIgnoreCase("remove")) {
			if (!user.hasPerm("bencmd.npc.remove")) {
				user.sendMessage(ChatColor.RED + "You don't have permission to do that!");
				BenCmd.getPlugin().logPermFail();
				return;
			}
			if (args.length == 1) {
				user.sendMessage(ChatColor.YELLOW + "Proper usage: /npc remove [id]");
				return;
			}
			BenCmd.getNPCFile().remNPC(BenCmd.getNPCFile().getNPC(Integer.parseInt(args[1])));
		} else if (args[0].equalsIgnoreCase("despawnall")) {
			if (!user.hasPerm("bencmd.npc.despawnall")) {
				user.sendMessage(ChatColor.RED + "You don't have permission to do that!");
				BenCmd.getPlugin().logPermFail();
				return;
			}
			for (NPC npc : BenCmd.getNPCFile().allNPCs()) {
				npc.despawn();
			}
		}
	}

}
