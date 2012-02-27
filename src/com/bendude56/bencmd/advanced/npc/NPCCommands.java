package com.bendude56.bencmd.advanced.npc;

import org.bukkit.*;
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
		if (args.length == 0) {
			BenCmd.showUse(user, "npc");
			user.sendMessage(BenCmd.getLocale().getString("command.npc.useTip"));
			return;
		}
		if (args[0].equalsIgnoreCase("bank")) {
			int id = BenCmd.getNPCFile().nextId();
			Location l = ((Player) user.getHandle()).getLocation();
			BenCmd.getNPCFile().addNPC(new BankerNPC(id, l));
			user.sendMessage(BenCmd.getLocale().getString("command.npc.created", BenCmd.getLocale().getString("command.npc.banker")));
			BenCmd.log(BenCmd.getLocale().getString("log.npc.create", user.getName(), BenCmd.getLocale().getString("command.npc.banker"), id + "", l.getX() + "", l.getY() + "", l.getZ() + "", l.getWorld().getName()));
		} else if (args[0].equalsIgnoreCase("bupgrade")) {
			int id = BenCmd.getNPCFile().nextId();
			Location l = ((Player) user.getHandle()).getLocation();
			BenCmd.getNPCFile().addNPC(new BankManagerNPC(id, l));
			user.sendMessage(BenCmd.getLocale().getString("command.npc.created", BenCmd.getLocale().getString("command.npc.bankManager")));
			BenCmd.log(BenCmd.getLocale().getString("log.npc.create", user.getName(), BenCmd.getLocale().getString("command.npc.bankManager"), id + "", l.getX() + "", l.getY() + "", l.getZ() + "", l.getWorld().getName()));
		} else if (args[0].equalsIgnoreCase("blacksmith")) {
			int id = BenCmd.getNPCFile().nextId();
			Location l = ((Player) user.getHandle()).getLocation();
			BenCmd.getNPCFile().addNPC(new BlacksmithNPC(id, l, null, null));
			user.sendMessage(BenCmd.getLocale().getString("command.npc.created", BenCmd.getLocale().getString("command.npc.blacksmith")));
			BenCmd.log(BenCmd.getLocale().getString("log.npc.create", user.getName(), BenCmd.getLocale().getString("command.npc.blacksmith"), id + "", l.getX() + "", l.getY() + "", l.getZ() + "", l.getWorld().getName()));
		} else if (args[0].equalsIgnoreCase("static")) {
			if (args.length == 1) {
				int id = BenCmd.getNPCFile().nextId();
				Location l = ((Player) user.getHandle()).getLocation();
				BenCmd.getNPCFile().addNPC(new StaticNPC(BenCmd.getLocale().getString("npc.unnamed.name"), BenCmd.getLocale().getString("npc.unnamed.name"), id, l, new ItemStack(Material.AIR), true));
				user.sendMessage(BenCmd.getLocale().getString("command.npc.created", BenCmd.getLocale().getString("command.npc.static")));
			} else if (args.length == 2) {
				int id = BenCmd.getNPCFile().nextId();
				Location l = ((Player) user.getHandle()).getLocation();
				BenCmd.getNPCFile().addNPC(new StaticNPC(args[1].replace('-', ' '), args[1].replace('-', ' '), id, l, new ItemStack(Material.AIR), true));
				user.sendMessage(BenCmd.getLocale().getString("command.npc.created", BenCmd.getLocale().getString("command.npc.static")));
			} else if (args.length == 3) {
				int id = BenCmd.getNPCFile().nextId();
				Location l = ((Player) user.getHandle()).getLocation();
				BenCmd.getNPCFile().addNPC(new StaticNPC(args[1].replace('-', ' '), args[2], id, l, new ItemStack(Material.AIR), true));
				user.sendMessage(BenCmd.getLocale().getString("command.npc.created", BenCmd.getLocale().getString("command.npc.static")));
			} else {
				BenCmd.showUse(user, "npc.static");
			}
		} else if (args[0].equalsIgnoreCase("skin")) {
			if (args.length == 3) {
				NPC npc = null;
				try {
					npc = BenCmd.getNPCFile().getNPC(Integer.parseInt(args[1]));
				} catch (NumberFormatException e) { }
				if (npc == null) {
					user.sendMessage(BenCmd.getLocale().getString("command.npc.npcNotFound"));
					return;
				} else if (!(npc instanceof StaticNPC)) {
					user.sendMessage(BenCmd.getLocale().getString("command.npc.skin.notSupported"));
					return;
				}
				((StaticNPC) npc).setSkin(args[2]);
				user.sendMessage(BenCmd.getLocale().getString("command.npc.skin.success"));
				if (!BenCmd.isSpoutConnected() || !BenCmd.getSpoutConnector().enabled((Player) user.getHandle())) {
					user.sendMessage(BenCmd.getLocale().getString("command.npc.skin.noSpout"));
				}
			} else {
				BenCmd.showUse(user, "npc.skin");
			}
		} else if (args[0].equalsIgnoreCase("item")) {
			if (args.length == 2) {
				NPC npc = null;
				try {
					npc = BenCmd.getNPCFile().getNPC(Integer.parseInt(args[1]));
				} catch (NumberFormatException e) { }
				if (npc == null) {
					user.sendMessage(BenCmd.getLocale().getString("command.npc.npcNotFound"));
					return;
				} else if (!(npc instanceof StaticNPC)) {
					user.sendMessage(BenCmd.getLocale().getString("command.npc.item.notSupported"));
					return;
				}
				if (((Player) user.getHandle()).getInventory().getItemInHand() == null) {
					user.sendMessage(ChatColor.RED + "You must hold an item to do that!");
					return;
				}
				npc.setHeldItem(((Player) user.getHandle()).getInventory().getItemInHand());
				user.sendMessage(ChatColor.GREEN + "That NPCs item was changed successfully!");
			} else {
				BenCmd.showUse(user, "npc.item");
			}
		} else if (args[0].equalsIgnoreCase("name")) {
			if (args.length == 3) {
				NPC npc = null;
				try {
					npc = BenCmd.getNPCFile().getNPC(Integer.parseInt(args[1]));
				} catch (NumberFormatException e) { }
				if (npc == null) {
					user.sendMessage(BenCmd.getLocale().getString("command.npc.npcNotFound"));
					return;
				} else if (!(npc instanceof StaticNPC)) {
					user.sendMessage(BenCmd.getLocale().getString("command.npc.name.notSupported"));
					return;
				}
				((StaticNPC) npc).setName(args[2].replace('-', ' '));
				user.sendMessage(BenCmd.getLocale().getString("command.npc.name.success", args[2].replace('-', ' ')));
			} else {
				BenCmd.showUse(user, "npc.name");
			}
		} else if (args[0].equalsIgnoreCase("rep")) {
			if (args.length != 2) {
				BenCmd.showUse(user, "npc.rep");
				return;
			}
			NPC npc = null;
			try {
				npc = BenCmd.getNPCFile().getNPC(Integer.parseInt(args[1]));
			} catch (NumberFormatException e) { }
			if (npc == null) {
				user.sendMessage(BenCmd.getLocale().getString("command.npc.npcNotFound"));
				return;
			}
			if (!(npc instanceof BlacksmithNPC)) {
				user.sendMessage(BenCmd.getLocale().getString("command.npc.rep.notSupported"));
				return;
			}
			if (((Player) user.getHandle()).getItemInHand() == null) {
				user.sendMessage(BenCmd.getLocale().getString("command.npc.rep.noItem"));
				return;
			}
			try {
				((BlacksmithNPC) npc).addItem(((Player) user.getHandle()).getItemInHand(), Double.parseDouble(args[2]), ((Player) user.getHandle()));
				user.sendMessage(BenCmd.getLocale().getString("command.npc.rep.success", Double.parseDouble(args[2]) + ""));
			} catch (NumberFormatException e) {
				user.sendMessage(BenCmd.getLocale().getString("command.npc.rep.invalidPrice"));
			}
		} else if (args[0].equalsIgnoreCase("remove")) {
			if (!user.hasPerm("bencmd.npc.remove")) {
				BenCmd.getPlugin().logPermFail(user, "npc", args, true);
				return;
			}
			if (args.length == 1) {
				BenCmd.showUse(user, "npc.remove");
				return;
			}
			NPC npc = null;
			try {
				npc = BenCmd.getNPCFile().getNPC(Integer.parseInt(args[1]));
			} catch (NumberFormatException e) { }
			if (npc == null) {
				user.sendMessage(BenCmd.getLocale().getString("command.npc.npcNotFound"));
				return;
			}
			BenCmd.getNPCFile().remNPC(npc);
			user.sendMessage(BenCmd.getLocale().getString("command.npc.remove.success"));
		} else if (args[0].equalsIgnoreCase("despawnall")) {
			if (!user.hasPerm("bencmd.npc.despawnall")) {
				BenCmd.getPlugin().logPermFail(user, "npc", args, true);
				return;
			}
			for (NPC npc : BenCmd.getNPCFile().allNPCs()) {
				if (npc.isSpawned())
					npc.despawn();
			}
			user.sendMessage(BenCmd.getLocale().getString("command.npc.despawnall"));
		}
	}

}
