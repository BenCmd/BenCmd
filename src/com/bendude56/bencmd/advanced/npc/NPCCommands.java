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
			BenCmd.getLocale().sendMessage(user, "command.npc.useTip");
			return;
		}
		if (args[0].equalsIgnoreCase("bank")) {
			int id = BenCmd.getNPCFile().nextId();
			Location l = ((Player) user.getHandle()).getLocation();
			BenCmd.getNPCFile().addNPC(new BankerNPC(id, l));
			BenCmd.getLocale().sendMessage(user, "command.npc.created", BenCmd.getLocale().getString("command.npc.banker"));
			BenCmd.log(BenCmd.getLocale().getString("log.npc.create", user.getName(), BenCmd.getLocale().getString("command.npc.banker"), id + "", l.getX() + "", l.getY() + "", l.getZ() + "", l.getWorld().getName()));
		} else if (args[0].equalsIgnoreCase("bupgrade")) {
			int id = BenCmd.getNPCFile().nextId();
			Location l = ((Player) user.getHandle()).getLocation();
			BenCmd.getNPCFile().addNPC(new BankManagerNPC(id, l));
			BenCmd.getLocale().sendMessage(user, "command.npc.created", BenCmd.getLocale().getString("command.npc.bankManager"));
			BenCmd.log(BenCmd.getLocale().getString("log.npc.create", user.getName(), BenCmd.getLocale().getString("command.npc.bankManager"), id + "", l.getX() + "", l.getY() + "", l.getZ() + "", l.getWorld().getName()));
		} else if (args[0].equalsIgnoreCase("blacksmith")) {
			int id = BenCmd.getNPCFile().nextId();
			Location l = ((Player) user.getHandle()).getLocation();
			BenCmd.getNPCFile().addNPC(new BlacksmithNPC(id, l, null, null));
			BenCmd.getLocale().sendMessage(user, "command.npc.created", BenCmd.getLocale().getString("command.npc.blacksmith"));
			BenCmd.log(BenCmd.getLocale().getString("log.npc.create", user.getName(), BenCmd.getLocale().getString("command.npc.blacksmith"), id + "", l.getX() + "", l.getY() + "", l.getZ() + "", l.getWorld().getName()));
		} else if (args[0].equalsIgnoreCase("static")) {
			if (args.length == 1) {
				int id = BenCmd.getNPCFile().nextId();
				Location l = ((Player) user.getHandle()).getLocation();
				BenCmd.getNPCFile().addNPC(new StaticNPC(BenCmd.getLocale().getString("npc.unnamed.name"), BenCmd.getLocale().getString("npc.unnamed.name"), id, l, new ItemStack(Material.AIR), true));
				BenCmd.getLocale().sendMessage(user, "command.npc.created", BenCmd.getLocale().getString("command.npc.static"));
				BenCmd.log(BenCmd.getLocale().getString("log.npc.create.static", user.getName(), BenCmd.getLocale().getString("command.npc.static"), BenCmd.getLocale().getString("npc.unnamed.name"), BenCmd.getLocale().getString("npc.unnamed.name"), id + "", l.getX() + "", l.getY() + "", l.getZ() + "", l.getWorld().getName()));
			} else if (args.length == 2) {
				int id = BenCmd.getNPCFile().nextId();
				Location l = ((Player) user.getHandle()).getLocation();
				BenCmd.getNPCFile().addNPC(new StaticNPC(args[1].replace('-', ' '), args[1].replace('-', ' '), id, l, new ItemStack(Material.AIR), true));
				BenCmd.getLocale().sendMessage(user, "command.npc.created", BenCmd.getLocale().getString("command.npc.static"));
				BenCmd.log(BenCmd.getLocale().getString("log.npc.create.static", user.getName(), BenCmd.getLocale().getString("command.npc.static"), args[1].replace('-', ' '), args[1].replace('-', ' '), id + "", l.getX() + "", l.getY() + "", l.getZ() + "", l.getWorld().getName()));
			} else if (args.length == 3) {
				int id = BenCmd.getNPCFile().nextId();
				Location l = ((Player) user.getHandle()).getLocation();
				BenCmd.getNPCFile().addNPC(new StaticNPC(args[1].replace('-', ' '), args[2], id, l, new ItemStack(Material.AIR), true));
				BenCmd.getLocale().sendMessage(user, "command.npc.created", BenCmd.getLocale().getString("command.npc.static"));
				BenCmd.log(BenCmd.getLocale().getString("log.npc.create.static", user.getName(), BenCmd.getLocale().getString("command.npc.static"), args[1].replace('-', ' '), args[2], id + "", l.getX() + "", l.getY() + "", l.getZ() + "", l.getWorld().getName()));
			} else {
				BenCmd.showUse(user, "npc", "static");
			}
		} else if (args[0].equalsIgnoreCase("skin")) {
			if (args.length == 3) {
				NPC npc = null;
				try {
					npc = BenCmd.getNPCFile().getNPC(Integer.parseInt(args[1]));
				} catch (NumberFormatException e) { }
				if (npc == null) {
					BenCmd.getLocale().sendMessage(user, "command.npc.npcNotFound");
					return;
				} else if (!(npc instanceof StaticNPC)) {
					BenCmd.getLocale().sendMessage(user, "command.npc.skin.notSupported");
					return;
				}
				((StaticNPC) npc).setSkin(args[2]);
				BenCmd.getLocale().sendMessage(user, "command.npc.skin.success");
				if (!BenCmd.isSpoutConnected() || !BenCmd.getSpoutConnector().enabled((Player) user.getHandle())) {
					BenCmd.getLocale().sendMessage(user, "command.npc.skin.noSpout");
				}
				BenCmd.log(BenCmd.getLocale().getString("log.npc.skin", user.getName(), Integer.parseInt(args[1]) + "", args[2]));
			} else {
				BenCmd.showUse(user, "npc", "skin");
			}
		} else if (args[0].equalsIgnoreCase("item")) {
			if (args.length == 2) {
				NPC npc = null;
				try {
					npc = BenCmd.getNPCFile().getNPC(Integer.parseInt(args[1]));
				} catch (NumberFormatException e) { }
				if (npc == null) {
					BenCmd.getLocale().sendMessage(user, "command.npc.npcNotFound");
					return;
				} else if (!(npc instanceof StaticNPC)) {
					BenCmd.getLocale().sendMessage(user, "command.npc.item.notSupported");
					return;
				}
				if (((Player) user.getHandle()).getInventory().getItemInHand() == null) {
					BenCmd.getLocale().sendMessage(user, "command.npc.item.noItem");
					return;
				}
				npc.setHeldItem(((Player) user.getHandle()).getInventory().getItemInHand());
				BenCmd.getLocale().sendMessage(user, "command.npc.item.success");
				BenCmd.log(BenCmd.getLocale().getString("log.npc.skin", user.getName(), Integer.parseInt(args[1]) + "", ((Player) user.getHandle()).getInventory().getItemInHand().toString()));
			} else {
				BenCmd.showUse(user, "npc", "item");
			}
		} else if (args[0].equalsIgnoreCase("name")) {
			if (args.length == 3) {
				NPC npc = null;
				try {
					npc = BenCmd.getNPCFile().getNPC(Integer.parseInt(args[1]));
				} catch (NumberFormatException e) { }
				if (npc == null) {
					BenCmd.getLocale().sendMessage(user, "command.npc.npcNotFound");
					return;
				} else if (!(npc instanceof StaticNPC)) {
					BenCmd.getLocale().sendMessage(user, "command.npc.name.notSupported");
					return;
				}
				((StaticNPC) npc).setName(args[2].replace('-', ' '));
				BenCmd.getLocale().sendMessage(user, "command.npc.name.success", args[2].replace('-', ' '));
				BenCmd.log(BenCmd.getLocale().getString("log.npc.name", user.getName(), Integer.parseInt(args[1]) + "", args[2].replace('-', ' ')));
			} else {
				BenCmd.showUse(user, "npc", "name");
			}
		} else if (args[0].equalsIgnoreCase("rep")) {
			if (args.length != 2) {
				BenCmd.showUse(user, "npc", "rep");
				return;
			}
			NPC npc = null;
			try {
				npc = BenCmd.getNPCFile().getNPC(Integer.parseInt(args[1]));
			} catch (NumberFormatException e) { }
			if (npc == null) {
				BenCmd.getLocale().sendMessage(user, "command.npc.npcNotFound");
				return;
			}
			if (!(npc instanceof BlacksmithNPC)) {
				BenCmd.getLocale().sendMessage(user, "command.npc.rep.notSupported");
				return;
			}
			if (((Player) user.getHandle()).getItemInHand() == null) {
				BenCmd.getLocale().sendMessage(user, "command.npc.rep.noItem");
				return;
			}
			try {
				((BlacksmithNPC) npc).addItem(((Player) user.getHandle()).getItemInHand(), Double.parseDouble(args[2]), ((Player) user.getHandle()));
				BenCmd.getLocale().sendMessage(user, "command.npc.rep.success", Double.parseDouble(args[2]) + "");
				BenCmd.log(BenCmd.getLocale().getString("log.npc.rep", user.getName(), Integer.parseInt(args[1]) + "", ((Player) user.getHandle()).getItemInHand().toString(), Double.parseDouble(args[2]) + ""));
			} catch (NumberFormatException e) {
				BenCmd.getLocale().sendMessage(user, "command.npc.rep.invalidPrice");
			}
		} else if (args[0].equalsIgnoreCase("remove")) {
			if (!user.hasPerm("bencmd.npc.remove")) {
				BenCmd.getPlugin().logPermFail(user, "npc", args, true);
				return;
			}
			if (args.length == 1) {
				BenCmd.showUse(user, "npc", "remove");
				return;
			}
			NPC npc = null;
			try {
				npc = BenCmd.getNPCFile().getNPC(Integer.parseInt(args[1]));
			} catch (NumberFormatException e) { }
			if (npc == null) {
				BenCmd.getLocale().sendMessage(user, "command.npc.npcNotFound");
				return;
			}
			BenCmd.getNPCFile().remNPC(npc);
			BenCmd.getLocale().sendMessage(user, "command.npc.remove.success");
			BenCmd.log(BenCmd.getLocale().getString("log.npc.delete", user.getName(), Integer.parseInt(args[1]) + ""));
		} else if (args[0].equalsIgnoreCase("despawnall")) {
			if (!user.hasPerm("bencmd.npc.despawnall")) {
				BenCmd.getPlugin().logPermFail(user, "npc", args, true);
				return;
			}
			for (NPC npc : BenCmd.getNPCFile().allNPCs()) {
				if (npc.isSpawned())
					npc.despawn();
			}
			BenCmd.getLocale().sendMessage(user, "command.npc.despawnall");
		}
	}

}
