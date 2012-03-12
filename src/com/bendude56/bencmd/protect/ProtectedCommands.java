package com.bendude56.bencmd.protect;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.Commands;
import com.bendude56.bencmd.User;
import com.bendude56.bencmd.event.protect.ProtectionAddEvent;
import com.bendude56.bencmd.event.protect.ProtectionEditEvent;
import com.bendude56.bencmd.event.protect.ProtectionEditEvent.ChangeType;
import com.bendude56.bencmd.event.protect.ProtectionRemoveEvent;
import com.bendude56.bencmd.permissions.PermissionUser;
import com.bendude56.bencmd.protect.ProtectFile.ProtectionType;

public class ProtectedCommands implements Commands {

	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		User user = User.getUser(sender);
		if (commandLabel.equalsIgnoreCase("protect")) {
			Protect(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("lock")) {
			String owner = "";
			if (args.length >= 1) {
				owner = " " + args[0];
			}
			Bukkit.dispatchCommand(sender, "protect add" + owner);
			return true;
		} else if (commandLabel.equalsIgnoreCase("public")) {
			String owner = "";
			if (args.length >= 1) {
				owner = " " + args[0];
			}
			Bukkit.dispatchCommand(sender, "protect public" + owner);
			return true;
		} else if (commandLabel.equalsIgnoreCase("unlock")) {
			Bukkit.dispatchCommand(sender, "protect remove");
			return true;
		} else if (commandLabel.equalsIgnoreCase("share")) {
			String guest = "";
			if (args.length >= 1) {
				guest = " " + args[0];
			}
			Bukkit.dispatchCommand(sender, "protect addguest" + guest);
			return true;
		} else if (commandLabel.equalsIgnoreCase("unshare")) {
			String guest = "";
			if (args.length >= 1) {
				guest = " " + args[0];
			}
			Bukkit.dispatchCommand(sender, "protect remguest" + guest);
			return true;
		}
		return false;
	}

	public void Protect(String[] args, User user) {
		if (user.isServer()) {
			BenCmd.getLocale().sendMessage(user, "basic.noServerUse");
			return;
		}
		if (args.length == 0) {
			user.sendMessage(ChatColor.YELLOW + "Proper use is /protect {add|public|remove|info|setowner|addguest|remguest}");
			return;
		}
		if (args[0].equalsIgnoreCase("add")) {
			AddProtect(args, user);
		} else if (args[0].equalsIgnoreCase("public")) {
			PublicProtect(args, user);
		} else if (args[0].equalsIgnoreCase("remove")) {
			RemoveProtect(args, user);
		} else if (args[0].equalsIgnoreCase("info")) {
			InfoProtect(args, user);
		} else if (args[0].equalsIgnoreCase("setowner")) {
			OwnerProtect(args, user);
		} else if (args[0].equalsIgnoreCase("addguest")) {
			AddGuest(args, user);
		} else if (args[0].equalsIgnoreCase("remguest")) {
			RemGuest(args, user);
		} else {
			user.sendMessage(ChatColor.YELLOW + "Proper use is /protect {add|remove|info|setowner|addguest|remguest}");
		}
	}

	public boolean Lock(Block l, User u, boolean p) {
		return Lock(l, u, u, p);
	}

	public boolean Lock(Block l, User u, PermissionUser o, boolean p) {
		Material m = l.getType();
		ProtectionType t;
		if (m == Material.CHEST) {
			t = (p) ? ProtectionType.PChest : ProtectionType.Chest;
		} else if (m == Material.WOODEN_DOOR) {
			t = (p) ? ProtectionType.PDoor : ProtectionType.Door;
			m = Material.WOOD_DOOR;
		} else if (m == Material.FURNACE) {
			t = (p) ? ProtectionType.PFurnace : ProtectionType.Furnace;
		} else if (m == Material.DISPENSER) {
			t = (p) ? ProtectionType.PDispenser : ProtectionType.Dispenser;
		} else if (m == Material.FENCE_GATE) {
			t = (p) ? ProtectionType.PGate : ProtectionType.Gate;
		} else {
			u.sendMessage(ChatColor.RED + "That type of block cannot be protected!");
			return false;
		}
		if (BenCmd.getProtections().getProtection(l.getLocation()) != -1) {
			u.sendMessage(ChatColor.RED + "That block is already protected!");
			return false;
		}
		int id = BenCmd.getProtections().addProtection(o.getName(), l.getLocation(), t);
		ProtectionAddEvent event = new ProtectionAddEvent(BenCmd.getProtections().getProtection(id), u);
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled()) {
			BenCmd.getProtections().removeProtection(id);
			return false;
		}
		u.sendMessage(ChatColor.GREEN + "Protected block created with owner " + o.getName() + ".");
		String w = l.getWorld().getName();
		String x = String.valueOf(l.getX());
		String y = String.valueOf(l.getY());
		String z = String.valueOf(l.getX());
		BenCmd.log(u.getDisplayName() + " created protected block (id: " + id + ") with owner " + o.getName() + " at position (" + w + "," + x + "," + y + "," + z + ")");
		for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			if (User.getUser(onlinePlayer).hasPerm("bencmd.lock.hearall") && BenCmd.isSpoutConnected()) {
				BenCmd.getSpoutConnector().sendNotification(onlinePlayer, ((p) ? "Public: " : "Lock: ") + u.getName(), "Protection ID: " + id, m);
			}
		}
		return true;
	}

	public boolean Unlock(Block l, User u) {
		int id;
		if ((id = BenCmd.getProtections().getProtection(l.getLocation())) != -1) {
			ProtectedBlock block = BenCmd.getProtections().getProtection(id);
			if (!block.canChange(u.getName()) && !u.hasPerm("bencmd.lock.remove")) {
				BenCmd.getLocale().sendMessage(u, "basic.noPermission");
				return false;
			} else {
				ProtectionRemoveEvent event = new ProtectionRemoveEvent(block, u);
				Bukkit.getPluginManager().callEvent(event);
				if (event.isCancelled()) {
					return false;
				}
				BenCmd.getProtections().removeProtection(block.getId());
				String w = l.getWorld().getName();
				String x = String.valueOf(l.getX());
				String y = String.valueOf(l.getY());
				String z = String.valueOf(l.getZ());
				BenCmd.log(u.getDisplayName() + " removed " + block.getOwner() + "'s protected chest (id: " + String.valueOf(block.getId()) + ") at position (" + w + "," + x + "," + y + "," + z + ")");
				u.sendMessage(ChatColor.GREEN + "The protection on that block was removed.");
				for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
					if (User.getUser(onlinePlayer).hasPerm("bencmd.lock.hearall") && BenCmd.isSpoutConnected()) {
						Material m = l.getType();
						if (m == Material.WOODEN_DOOR) {
							m = Material.WOOD_DOOR;
						}
						BenCmd.getSpoutConnector().sendNotification(onlinePlayer, "Unlock: " + u.getName(), "Protection ID: " + id, m);
					}
				}
				return true;
			}
		} else {
			u.sendMessage(ChatColor.RED + "You aren't pointing at a protected block!");
			return false;
		}
	}

	public void Info(Block b, User u) {
		int id;
		if ((id = BenCmd.getProtections().getProtection(b.getLocation())) != -1) {
			ProtectedBlock block = BenCmd.getProtections().getProtection(id);
			Info(block, u);
		} else {
			u.sendMessage(ChatColor.RED + "You aren't pointing at a protected block!");
			return;
		}
	}

	public void Info(ProtectedBlock b, User u) {
		String owner = b.getOwner();
		String id = String.valueOf(b.getId());
		String guests = "";
		boolean init = false;
		for (String guest : b.getGuests()) {
			if (init) {
				guests += ",";
			} else {
				init = true;
			}
			guests += guest;
		}
		u.sendMessage(ChatColor.DARK_GRAY + "Protection ID: " + id);
		u.sendMessage(ChatColor.DARK_GRAY + "Owner: " + owner);
		u.sendMessage(ChatColor.DARK_GRAY + "Guests: " + guests);
		u.sendMessage(ChatColor.DARK_GRAY + "Access: " + ((b instanceof PublicBlock) ? "Public" : "Private"));
	}

	public void AddProtect(String[] args, User user) {
		if (!user.hasPerm("bencmd.lock.create")) {
			BenCmd.getLocale().sendMessage(user, "basic.noPermission");
			BenCmd.getPlugin().logPermFail();
			return;
		}
		Block pointedAt = ((Player) user.getHandle()).getTargetBlock(BenCmd.getPlugin().getTransparentBlocks(), 4);
		if (args.length == 1) {
			this.Lock(pointedAt, user, false);
		} else if (args.length == 2) {
			PermissionUser p = PermissionUser.matchUserAllowPartial(args[1]);
			if (p != null) {
				this.Lock(pointedAt, user, p, false);
			} else {
				BenCmd.getLocale().sendMessage(user, "basic.userNotFound", args[1]);
			}
		} else {
			user.sendMessage(ChatColor.YELLOW + "Proper use is /protect add [owner]");
		}
	}

	public void PublicProtect(String[] args, User user) {
		if (!user.hasPerm("bencmd.lock.public")) {
			BenCmd.getLocale().sendMessage(user, "basic.noPermission");
			BenCmd.getPlugin().logPermFail();
			return;
		}
		Block pointedAt = ((Player) user.getHandle()).getTargetBlock(BenCmd.getPlugin().getTransparentBlocks(), 4);
		if (args.length == 1) {
			this.Lock(pointedAt, user, true);
		} else if (args.length == 2) {
			PermissionUser p = PermissionUser.matchUserAllowPartial(args[1]);
			if (p != null) {
				this.Lock(pointedAt, user, p, true);
			} else {
				BenCmd.getLocale().sendMessage(user, "basic.userNotFound", args[1]);
			}
		} else {
			user.sendMessage(ChatColor.YELLOW + "Proper use is /protect protect [owner]");
		}
	}

	public void RemoveProtect(String[] args, User user) {
		Block pointedAt = ((Player) user.getHandle()).getTargetBlock(BenCmd.getPlugin().getTransparentBlocks(), 4);
		if (args.length == 1) {
			Unlock(pointedAt, user);
		} else if (args.length == 2) {
			int id;
			try {
				id = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				user.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a number...");
				return;
			}
			ProtectedBlock block = BenCmd.getProtections().getProtection(id);
			if (block == null) {
				user.sendMessage(ChatColor.RED + "That block isn't protected!");
				return;
			}
			Unlock(block.getLocation().getWorld().getBlockAt(block.getLocation()), user);
		} else {
			user.sendMessage(ChatColor.YELLOW + "Proper use is /protect remove [ID]");
		}
	}

	public void InfoProtect(String[] args, User user) {
		if (!user.hasPerm("bencmd.lock.info")) {
			BenCmd.getLocale().sendMessage(user, "basic.noPermission");
			BenCmd.getPlugin().logPermFail();
			return;
		}
		Block pointedAt = ((Player) user.getHandle()).getTargetBlock(BenCmd.getPlugin().getTransparentBlocks(), 4);
		if (args.length == 1) {
			Info(pointedAt, user);
		} else if (args.length == 2) {
			int id;
			try {
				id = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				user.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a valid number!");
				return;
			}
			if (BenCmd.getProtections().protectionExists(id)) {
				Info(BenCmd.getProtections().getProtection(id), user);
			} else {
				user.sendMessage(ChatColor.RED + "No protection exists with id " + id + "!");
			}
		} else {

		}
	}

	public void OwnerProtect(String[] args, User user) {
		Block pointedAt = ((Player) user.getHandle()).getTargetBlock(BenCmd.getPlugin().getTransparentBlocks(), 4);
		ProtectedBlock block;
		if (args.length == 2) {
			if ((block = BenCmd.getProtections().getProtection(BenCmd.getProtections().getProtection(pointedAt.getLocation()))) != null) {
				if (!block.canChange(user.getName()) && !user.hasPerm("bencmd.lock.edit")) {
					BenCmd.getLocale().sendMessage(user, "basic.noPermission");
					return;
				}
				PermissionUser newOwner;
				if ((newOwner = PermissionUser.matchUserAllowPartial(args[1])) == null) {
					BenCmd.getLocale().sendMessage(user, "basic.userNotFound", args[1]);
					return;
				}
				ProtectionEditEvent event = new ProtectionEditEvent(block, user, ChangeType.SET_OWNER, newOwner);
				Bukkit.getPluginManager().callEvent(event);
				if (event.isCancelled()) {
					return;
				}
				BenCmd.log(user.getDisplayName() + " has changed the owner of " + block.getOwner() + "'s protected block (id: " + block.getId() + ") to " + args[1]);
				user.sendMessage(ChatColor.GREEN + "That protected block now belongs to " + newOwner.getName());
				BenCmd.getProtections().changeOwner(block.getId(), newOwner.getName());
			} else {
				user.sendMessage(ChatColor.RED + "You aren't pointing at a protected block!");
			}
		} else if (args.length == 3) {
			int id;
			try {
				id = Integer.parseInt(args[2]);
			} catch (NumberFormatException e) {
				user.sendMessage(ChatColor.RED + args[2] + "Cannot be converted into a number...");
				return;
			}
			block = BenCmd.getProtections().getProtection(id);
			if (!block.canChange(user.getName()) && !user.hasPerm("bencmd.lock.edit")) {
				BenCmd.getLocale().sendMessage(user, "basic.noPermission");
				return;
			}
			PermissionUser newOwner;
			if ((newOwner = PermissionUser.matchUserAllowPartial(args[1])) == null) {
				BenCmd.getLocale().sendMessage(user, "basic.userNotFound", args[1]);
				return;
			}
			ProtectionEditEvent event = new ProtectionEditEvent(block, user, ChangeType.SET_OWNER, newOwner);
			Bukkit.getPluginManager().callEvent(event);
			if (event.isCancelled()) {
				return;
			}
			BenCmd.log(user.getDisplayName() + " has changed the owner of " + block.getOwner() + "'s protected block (id: " + block.getId() + ") to " + args[1]);
			user.sendMessage(ChatColor.GREEN + "That protected block now belongs to " + newOwner.getName());
			BenCmd.getProtections().changeOwner(block.getId(), newOwner.getName());
		} else {
			user.sendMessage(ChatColor.YELLOW + "Proper use is /protect setowner <Owner> [ID]");
		}
	}

	public void AddGuest(String[] args, User user) {
		Block pointedAt = ((Player) user.getHandle()).getTargetBlock(BenCmd.getPlugin().getTransparentBlocks(), 4);
		ProtectedBlock block;
		if (args.length == 2) {
			if ((block = BenCmd.getProtections().getProtection(BenCmd.getProtections().getProtection(pointedAt.getLocation()))) != null) {
				if (!block.canChange(user.getName()) && !user.hasPerm("bencmd.lock.edit")) {
					BenCmd.getLocale().sendMessage(user, "basic.noPermission");
					return;
				}
				PermissionUser newOwner;
				if ((newOwner = PermissionUser.matchUserAllowPartial(args[1])) == null) {
					BenCmd.getLocale().sendMessage(user, "basic.userNotFound", args[1]);
					return;
				}
				ProtectionEditEvent event = new ProtectionEditEvent(block, user, ChangeType.ADD_GUEST, newOwner);
				Bukkit.getPluginManager().callEvent(event);
				if (event.isCancelled()) {
					return;
				}
				BenCmd.log(user.getDisplayName() + " has added " + newOwner.getName() + " to the guest list of " + block.getOwner() + "'s protected block (id: " + block.getId() + ")");
				user.sendMessage(ChatColor.GREEN + newOwner.getName() + " now has guest access to that block.");
				BenCmd.getProtections().addGuest(block.getId(), newOwner.getName());
			} else {
				user.sendMessage(ChatColor.RED + "You aren't pointing at a protected block!");
			}
		} else if (args.length == 3) {

			int id;
			try {
				id = Integer.parseInt(args[2]);
			} catch (NumberFormatException e) {
				user.sendMessage(ChatColor.RED + args[2] + "Cannot be converted into a number...");
				return;
			}
			if ((block = BenCmd.getProtections().getProtection(id)) != null) {
				if (!block.canChange(user.getName()) && !user.hasPerm("bencmd.lock.edit")) {
					BenCmd.getLocale().sendMessage(user, "basic.noPermission");
					return;
				}
				block = BenCmd.getProtections().getProtection(id);
				PermissionUser newOwner;
				if ((newOwner = PermissionUser.matchUserAllowPartial(args[1])) == null) {
					BenCmd.getLocale().sendMessage(user, "basic.userNotFound", args[1]);
					return;
				}
				ProtectionEditEvent event = new ProtectionEditEvent(block, user, ChangeType.ADD_GUEST, newOwner);
				Bukkit.getPluginManager().callEvent(event);
				if (event.isCancelled()) {
					return;
				}
				BenCmd.log(user.getDisplayName() + " has added " + newOwner.getName() + " to the guest list of " + block.getOwner() + "'s protected block (id: " + block.getId() + ")");
				user.sendMessage(ChatColor.GREEN + newOwner.getName() + " now has guest access to that block.");
				BenCmd.getProtections().addGuest(block.getId(), newOwner.getName());
			}
		} else {
			user.sendMessage(ChatColor.YELLOW + "Proper use is /protect addguest <Guest> [ID]");
		}
	}

	public void RemGuest(String[] args, User user) {
		Block pointedAt = ((Player) user.getHandle()).getTargetBlock(BenCmd.getPlugin().getTransparentBlocks(), 4);
		ProtectedBlock block;
		if (args.length == 2) {
			if ((block = BenCmd.getProtections().getProtection(BenCmd.getProtections().getProtection(pointedAt.getLocation()))) != null) {
				if (!block.canChange(user.getName()) && !user.hasPerm("bencmd.lock.edit")) {
					BenCmd.getLocale().sendMessage(user, "basic.noPermission");
					return;
				}
				PermissionUser newOwner;
				if ((newOwner = PermissionUser.matchUserAllowPartial(args[1])) == null) {
					BenCmd.getLocale().sendMessage(user, "basic.userNotFound", args[1]);
					return;
				}
				ProtectionEditEvent event = new ProtectionEditEvent(block, user, ChangeType.REMOVE_GUEST, newOwner);
				Bukkit.getPluginManager().callEvent(event);
				if (event.isCancelled()) {
					return;
				}
				BenCmd.log(user.getDisplayName() + " has removed " + newOwner.getName() + " from the guest list of " + block.getOwner() + "'s protected block (id: " + block.getId() + ")");
				user.sendMessage(ChatColor.GREEN + newOwner.getName() + " has now lost guest access to that block.");
				BenCmd.getProtections().removeGuest(block.getId(), newOwner.getName());
			} else {
				user.sendMessage(ChatColor.RED + "You aren't pointing at a protected block!");
			}
		} else if (args.length == 3) {
			int id;
			try {
				id = Integer.parseInt(args[2]);
			} catch (NumberFormatException e) {
				user.sendMessage(ChatColor.RED + args[2] + "Cannot be converted into a number...");
				return;
			}
			if ((block = BenCmd.getProtections().getProtection(id)) != null) {
				if (!block.canChange(user.getName()) && !user.hasPerm("bencmd.lock.edit")) {
					BenCmd.getLocale().sendMessage(user, "basic.noPermission");
					return;
				}
				PermissionUser newOwner;
				if ((newOwner = PermissionUser.matchUserAllowPartial(args[1])) == null) {
					BenCmd.getLocale().sendMessage(user, "basic.userNotFound", args[1]);
					return;
				}
				ProtectionEditEvent event = new ProtectionEditEvent(block, user, ChangeType.REMOVE_GUEST, newOwner);
				Bukkit.getPluginManager().callEvent(event);
				if (event.isCancelled()) {
					return;
				}
				BenCmd.log(user.getDisplayName() + " has removed " + newOwner.getName() + " from the guest list of " + block.getOwner() + "'s protected block (id: " + block.getId() + ")");
				user.sendMessage(ChatColor.GREEN + newOwner.getName() + " has now lost guest access to that block.");
				BenCmd.getProtections().removeGuest(block.getId(), newOwner.getName());
			}
		} else {
			user.sendMessage(ChatColor.YELLOW + "Proper use is /protect remguest <Guest> [ID]");
		}
	}
}
