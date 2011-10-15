package com.bendude56.bencmd.protect;

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
	BenCmd plugin;

	public ProtectedCommands(BenCmd instance) {
		plugin = instance;
	}

	public boolean onCommand(CommandSender sender, Command command,
			String commandLabel, String[] args) {
		User user;
		try {
			user = User.getUser(plugin, (Player) sender);
		} catch (ClassCastException e) {
			user = User.getUser(plugin);
		}
		if (commandLabel.equalsIgnoreCase("protect")) {
			Protect(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("lock")) {
			String owner = "";
			if (args.length >= 1) {
				owner = " " + args[0];
			}
			plugin.getServer().dispatchCommand(sender, "protect add" + owner);
			return true;
		} else if (commandLabel.equalsIgnoreCase("public")) {
			String owner = "";
			if (args.length >= 1) {
				owner = " " + args[0];
			}
			plugin.getServer()
					.dispatchCommand(sender, "protect public" + owner);
			return true;
		} else if (commandLabel.equalsIgnoreCase("unlock")) {
			plugin.getServer().dispatchCommand(sender, "protect remove");
			return true;
		} else if (commandLabel.equalsIgnoreCase("share")) {
			String guest = "";
			if (args.length >= 1) {
				guest = " " + args[0];
			}
			plugin.getServer().dispatchCommand(sender,
					"protect addguest" + guest);
			return true;
		} else if (commandLabel.equalsIgnoreCase("unshare")) {
			String guest = "";
			if (args.length >= 1) {
				guest = " " + args[0];
			}
			plugin.getServer().dispatchCommand(sender,
					"protect remguest" + guest);
			return true;
		}
		return false;
	}

	public void Protect(String[] args, User user) {
		if (user.isServer()) {
			user.sendMessage(ChatColor.RED + "The server cannot do that!");
			return;
		}
		if (args.length == 0) {
			user.sendMessage(ChatColor.YELLOW
					+ "Proper use is /protect {add|public|remove|info|setowner|addguest|remguest}");
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
			user.sendMessage(ChatColor.YELLOW
					+ "Proper use is /protect {add|remove|info|setowner|addguest|remguest}");
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
		if (plugin.protectFile.getProtection(l.getLocation()) != -1) {
			u.sendMessage(ChatColor.RED + "That block is already protected!");
			return false;
		}
		int id = plugin.protectFile.addProtection(o, l.getLocation(), t);
		ProtectionAddEvent event = new ProtectionAddEvent(plugin.protectFile.getProtection(id), u);
		plugin.getServer().getPluginManager().callEvent(event);
		if (event.isCancelled()) {
			plugin.protectFile.removeProtection(id);
			return false;
		}
		u.sendMessage(ChatColor.GREEN
				+ "Protected block created with owner "
				+ o.getName() + ".");
		String w = l.getWorld().getName();
		String x = String.valueOf(l.getX());
		String y = String.valueOf(l.getY());
		String z = String.valueOf(l.getX());
		plugin.log.info(u.getDisplayName()
				+ " created protected block (id: " + id
				+ ") with owner " + o.getName()
				+ " at position (" + w + "," + x + "," + y + "," + z
				+ ")");
		plugin.bLog.info("PROTECTION ADDED: " + String.valueOf(id)
				+ ((o.getName().equals(u.getName())) ? "" : " (" + o.getName() + ")")+ " by " + u.getDisplayName());
		for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
			if (User.getUser(plugin, onlinePlayer)
					.hasPerm("bencmd.lock.hearall") && plugin.spoutcraft) {
				plugin.spoutconnect.sendNotification(onlinePlayer, ((p) ? "Public: " : "Lock: ") + u.getName(), "Protection ID: " + id, m);
			}
		}
		return true;
	}
	
	public boolean Unlock(Block l, User u) {
		int id;
		if ((id = plugin.protectFile.getProtection(l
				.getLocation())) != -1) {
			ProtectedBlock block = plugin.protectFile.getProtection(id);
			if (!block.canChange(u) && !u.hasPerm("bencmd.lock.remove")) {
				u.sendMessage(ChatColor.RED
						+ "You don't have permission to remove the protection on that block!");
				return false;
			} else {
				ProtectionRemoveEvent event = new ProtectionRemoveEvent(block, u);
				plugin.getServer().getPluginManager().callEvent(event);
				if (event.isCancelled()) {
					return false;
				}
				plugin.protectFile.removeProtection(block.GetId());
				String w = l.getWorld().getName();
				String x = String.valueOf(l.getX());
				String y = String.valueOf(l.getY());
				String z = String.valueOf(l.getZ());
				plugin.log.info(u.getDisplayName() + " removed "
						+ block.getOwner().getName()
						+ "'s protected chest (id: "
						+ String.valueOf(block.GetId())
						+ ") at position (" + w + "," + x + "," + y
						+ "," + z + ")");
				plugin.bLog.info("PROTECTION REMOVED: "
						+ String.valueOf(block.GetId()) + " ("
						+ block.getOwner().getName() + ") by "
						+ u.getDisplayName());
				u.sendMessage(ChatColor.GREEN
						+ "The protection on that block was removed.");
				for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
					if (User.getUser(plugin, onlinePlayer)
							.hasPerm("bencmd.lock.hearall") && plugin.spoutcraft) {
						Material m = l.getType();
						if (m == Material.WOODEN_DOOR){
							m = Material.WOOD_DOOR;
						}
						plugin.spoutconnect.sendNotification(onlinePlayer, "Unlock: " + u.getName(), "Protection ID: " + id, m);
					}
				}
				return true;
			}
		} else {
			u.sendMessage(ChatColor.RED
					+ "You aren't pointing at a protected block!");
			return false;
		}
	}
	
	public void Info(Block b, User u) {
		int id;
		if ((id = plugin.protectFile.getProtection(b
				.getLocation())) != -1) {
			ProtectedBlock block = plugin.protectFile.getProtection(id);
			Info(block, u);
		} else {
			u.sendMessage(ChatColor.RED
					+ "You aren't pointing at a protected block!");
			return;
		}
	}
	
	public void Info(ProtectedBlock b, User u) {
		String owner = b.getOwner().getName();
		String id = String.valueOf(b.GetId());
		String guests = "";
		boolean init = false;
		for (PermissionUser guest : b.getGuests()) {
			if (init) {
				guests += ",";
			} else {
				init = true;
			}
			guests += guest.getName();
		}
		u.sendMessage(ChatColor.DARK_GRAY + "Protection ID: " + id);
		u.sendMessage(ChatColor.DARK_GRAY + "Owner: " + owner);
		u.sendMessage(ChatColor.DARK_GRAY + "Guests: " + guests);
		u.sendMessage(ChatColor.DARK_GRAY + "Access: " + ((b instanceof PublicBlock) ? "Public" : "Private"));
	}

	public void AddProtect(String[] args, User user) {
		if (!user.hasPerm("bencmd.lock.create")) {
			user.sendMessage(ChatColor.RED
					+ "You don't have permission to do that!");
			plugin.logPermFail();
			return;
		}
		Block pointedAt = user.getHandle().getTargetBlock(null, 4);
		if (args.length == 1) {
			this.Lock(pointedAt, user, false);
		} else if (args.length == 2) {
			PermissionUser p = PermissionUser.matchUser(args[1], plugin);
			if (p != null) {
				this.Lock(pointedAt, user, p, false);
			} else {
				user.sendMessage(ChatColor.RED + "That user doesn't exist!");
			}
		} else {
			user.sendMessage(ChatColor.YELLOW
					+ "Proper use is /protect add [owner]");
		}
	}

	public void PublicProtect(String[] args, User user) {
		if (!user.hasPerm("bencmd.lock.public")) {
			user.sendMessage(ChatColor.RED
					+ "You don't have permission to do that!");
			plugin.logPermFail();
			return;
		}
		Block pointedAt = user.getHandle().getTargetBlock(null, 4);
		if (args.length == 1) {
			this.Lock(pointedAt, user, true);
		} else if (args.length == 2) {
			PermissionUser p = PermissionUser.matchUser(args[1], plugin);
			if (p != null) {
				this.Lock(pointedAt, user, p, true);
			} else {
				user.sendMessage(ChatColor.RED + "That user doesn't exist!");
			}
		} else {
			user.sendMessage(ChatColor.YELLOW
					+ "Proper use is /protect protect [owner]");
		}
	}

	public void RemoveProtect(String[] args, User user) {
		Block pointedAt = user.getHandle().getTargetBlock(null, 4);
		if (args.length == 1) {
			Unlock(pointedAt, user);
		} else if (args.length == 2) {
			int id;
			try {
				id = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				user.sendMessage(ChatColor.RED + "'" + args[1]
						+ "' is not a number...");
				return;
			}
			ProtectedBlock block = plugin.protectFile.getProtection(id);
			if (block == null) {
				user.sendMessage(ChatColor.RED + "That block isn't protected!");
				return;
			}
			Unlock(block.getLocation().getWorld().getBlockAt(block.getLocation()), user);
		} else {
			user.sendMessage(ChatColor.YELLOW
					+ "Proper use is /protect remove [ID]");
		}
	}

	public void InfoProtect(String[] args, User user) {
		if (!user.hasPerm("bencmd.lock.info")) {
			user.sendMessage(ChatColor.RED
					+ "You don't have permission to do that!");
			plugin.logPermFail();
			return;
		}
		Block pointedAt = user.getHandle().getTargetBlock(null, 4);
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
			if (plugin.protectFile.protectionExists(id)) {
				Info(plugin.protectFile.getProtection(id), user);
			} else {
				user.sendMessage(ChatColor.RED + "No protection exists with id " + id + "!");
			}
		} else {
			
		}
	}

	public void OwnerProtect(String[] args, User user) {
		Block pointedAt = user.getHandle().getTargetBlock(null, 4);
		ProtectedBlock block;
		if (args.length == 2) {
			if ((block = plugin.protectFile.getProtection(plugin.protectFile
					.getProtection(pointedAt.getLocation()))) != null) {
				if (!block.canChange(user) && !user.hasPerm("bencmd.lock.edit")) {
					user.sendMessage(ChatColor.RED
							+ "You don't have permission to edit the protection on that block!");
					return;
				}
				PermissionUser newOwner;
				if ((newOwner = PermissionUser.matchUser(args[1], plugin)) == null) {
					user.sendMessage(ChatColor.RED
							+ "That player doesn't exist!");
					return;
				}
				ProtectionEditEvent event = new ProtectionEditEvent(block, user, ChangeType.SET_OWNER, newOwner);
				plugin.getServer().getPluginManager().callEvent(event);
				if (event.isCancelled()) {
					return;
				}
				plugin.log.info(user.getDisplayName()
						+ " has changed the owner of "
						+ block.getOwner().getName()
						+ "'s protected block (id: " + block.GetId() + ") to "
						+ args[1]);
				plugin.bLog.info("PROTECTION EDITED: "
						+ String.valueOf(block.GetId()) + " ("
						+ block.getOwner().getName() + ") by "
						+ user.getDisplayName());
				plugin.bLog.info("Owner changed to " + newOwner.getName());
				user.sendMessage(ChatColor.GREEN
						+ "That protected block now belongs to "
						+ newOwner.getName());
				plugin.protectFile.changeOwner(block.GetId(), newOwner);
			} else {
				user.sendMessage(ChatColor.RED
						+ "You aren't pointing at a protected block!");
			}
		} else if (args.length == 3) {
			int id;
			try {
				id = Integer.parseInt(args[2]);
			} catch (NumberFormatException e) {
				user.sendMessage(ChatColor.RED + args[2]
						+ "Cannot be converted into a number...");
				return;
			}
			block = plugin.protectFile.getProtection(id);
			if (!block.canChange(user) && !user.hasPerm("bencmd.lock.edit")) {
				user.sendMessage(ChatColor.RED
						+ "You don't have permission to edit the protection on that block!");
				return;
			}
			PermissionUser newOwner;
			if ((newOwner = PermissionUser.matchUser(args[0], plugin)) == null) {
				user.sendMessage(ChatColor.RED + "That player doesn't exist!");
				return;
			}
			ProtectionEditEvent event = new ProtectionEditEvent(block, user, ChangeType.SET_OWNER, newOwner);
			plugin.getServer().getPluginManager().callEvent(event);
			if (event.isCancelled()) {
				return;
			}
			plugin.log.info(user.getDisplayName()
					+ " has changed the owner of " + block.getOwner().getName()
					+ "'s protected block (id: " + block.GetId() + ") to "
					+ args[1]);
			plugin.bLog.info("PROTECTION EDITED: "
					+ String.valueOf(block.GetId()) + " ("
					+ block.getOwner().getName() + ") by "
					+ user.getDisplayName());
			plugin.bLog.info("Owner changed to " + newOwner.getName());
			user.sendMessage(ChatColor.GREEN
					+ "That protected block now belongs to "
					+ newOwner.getName());
			plugin.protectFile.changeOwner(block.GetId(), newOwner);
		} else {
			user.sendMessage(ChatColor.YELLOW
					+ "Proper use is /protect setowner <Owner> [ID]");
		}
	}

	public void AddGuest(String[] args, User user) {
		Block pointedAt = user.getHandle().getTargetBlock(null, 4);
		ProtectedBlock block;
		if (args.length == 2) {
			if ((block = plugin.protectFile.getProtection(plugin.protectFile
					.getProtection(pointedAt.getLocation()))) != null) {
				if (!block.canChange(user) && !user.hasPerm("bencmd.lock.edit")) {
					user.sendMessage(ChatColor.RED
							+ "You don't have permission to edit the protection on that block!");
					return;
				}
				PermissionUser newOwner;
				if ((newOwner = PermissionUser.matchUser(args[1], plugin)) == null) {
					user.sendMessage(ChatColor.RED
							+ "That player doesn't exist!");
					return;
				}
				ProtectionEditEvent event = new ProtectionEditEvent(block, user, ChangeType.ADD_GUEST, newOwner);
				plugin.getServer().getPluginManager().callEvent(event);
				if (event.isCancelled()) {
					return;
				}
				plugin.log.info(user.getDisplayName() + " has added "
						+ newOwner.getName() + " to the guest list of "
						+ block.getOwner().getName()
						+ "'s protected block (id: " + block.GetId() + ")");
				plugin.bLog.info("PROTECTION EDITED: "
						+ String.valueOf(block.GetId()) + " ("
						+ block.getOwner().getName() + ") by "
						+ user.getDisplayName());
				plugin.bLog.info(newOwner.getName() + " added as guest!");
				user.sendMessage(ChatColor.GREEN + newOwner.getName()
						+ " now has guest access to that block.");
				plugin.protectFile.addGuest(block.GetId(), newOwner);
			} else {
				user.sendMessage(ChatColor.RED
						+ "You aren't pointing at a protected block!");
			}
		} else if (args.length == 3) {
			
			int id;
			try {
				id = Integer.parseInt(args[2]);
			} catch (NumberFormatException e) {
				user.sendMessage(ChatColor.RED + args[2]
						+ "Cannot be converted into a number...");
				return;
			}
			if ((block = plugin.protectFile.getProtection(id)) != null) {
				if (!block.canChange(user) && !user.hasPerm("bencmd.lock.edit")) {
					user.sendMessage(ChatColor.RED
							+ "You don't have permission to edit the protection on that block!");
					return;
				}
				block = plugin.protectFile.getProtection(id);
				PermissionUser newOwner;
				if ((newOwner = PermissionUser.matchUser(args[0], plugin)) == null) {
					user.sendMessage(ChatColor.RED + "That player doesn't exist!");
					return;
				}
				ProtectionEditEvent event = new ProtectionEditEvent(block, user, ChangeType.ADD_GUEST, newOwner);
				plugin.getServer().getPluginManager().callEvent(event);
				if (event.isCancelled()) {
					return;
				}
				plugin.log.info(user.getDisplayName() + " has added "
						+ newOwner.getName() + " to the guest list of "
						+ block.getOwner().getName() + "'s protected block (id: "
						+ block.GetId() + ")");
				plugin.bLog.info("PROTECTION EDITED: "
						+ String.valueOf(block.GetId()) + " ("
						+ block.getOwner().getName() + ") by "
						+ user.getDisplayName());
				plugin.bLog.info(newOwner.getName() + " added as guest!");
				user.sendMessage(ChatColor.GREEN + newOwner.getName()
						+ " now has guest access to that block.");
				plugin.protectFile.addGuest(block.GetId(), newOwner);
			}
		} else {
			user.sendMessage(ChatColor.YELLOW
					+ "Proper use is /protect addguest <Guest> [ID]");
		}
	}

	public void RemGuest(String[] args, User user) {
		Block pointedAt = user.getHandle().getTargetBlock(null, 4);
		ProtectedBlock block;
		if (args.length == 2) {
			if ((block = plugin.protectFile.getProtection(plugin.protectFile
					.getProtection(pointedAt.getLocation()))) != null) {
				if (!block.canChange(user) && !user.hasPerm("bencmd.lock.edit")) {
					user.sendMessage(ChatColor.RED
							+ "You don't have permission to edit the protection on that block!");
					return;
				}
				PermissionUser newOwner;
				if ((newOwner = PermissionUser.matchUser(args[1], plugin)) == null) {
					user.sendMessage(ChatColor.RED
							+ "That player doesn't exist!");
					return;
				}
				ProtectionEditEvent event = new ProtectionEditEvent(block, user, ChangeType.REMOVE_GUEST, newOwner);
				plugin.getServer().getPluginManager().callEvent(event);
				if (event.isCancelled()) {
					return;
				}
				plugin.log.info(user.getDisplayName() + " has removed "
						+ newOwner.getName() + " from the guest list of "
						+ block.getOwner().getName()
						+ "'s protected block (id: " + block.GetId() + ")");
				plugin.bLog.info("PROTECTION EDITED: "
						+ String.valueOf(block.GetId()) + " ("
						+ block.getOwner().getName() + ") by "
						+ user.getDisplayName());
				plugin.bLog.info(newOwner.getName()
						+ " removed from guest list!");
				user.sendMessage(ChatColor.GREEN + newOwner.getName()
						+ " has now lost guest access to that block.");
				plugin.protectFile.removeGuest(block.GetId(), newOwner);
			} else {
				user.sendMessage(ChatColor.RED
						+ "You aren't pointing at a protected block!");
			}
		} else if (args.length == 3) {
			int id;
			try {
				id = Integer.parseInt(args[2]);
			} catch (NumberFormatException e) {
				user.sendMessage(ChatColor.RED + args[2]
						+ "Cannot be converted into a number...");
				return;
			}
			if ((block = plugin.protectFile.getProtection(id)) != null) {
				if (!block.canChange(user) && !user.hasPerm("bencmd.lock.edit")) {
					user.sendMessage(ChatColor.RED
							+ "You don't have permission to edit the protection on that block!");
					return;
				}
				PermissionUser newOwner;
				if ((newOwner = PermissionUser.matchUser(args[0], plugin)) == null) {
					user.sendMessage(ChatColor.RED + "That player doesn't exist!");
					return;
				}
				ProtectionEditEvent event = new ProtectionEditEvent(block, user, ChangeType.REMOVE_GUEST, newOwner);
				plugin.getServer().getPluginManager().callEvent(event);
				if (event.isCancelled()) {
					return;
				}
				plugin.log.info(user.getDisplayName() + " has removed "
						+ newOwner.getName() + " from the guest list of "
						+ block.getOwner().getName() + "'s protected block (id: "
						+ block.GetId() + ")");
				plugin.bLog.info("PROTECTION EDITED: "
						+ String.valueOf(block.GetId()) + " ("
						+ block.getOwner().getName() + ") by "
						+ user.getDisplayName());
				plugin.bLog.info(newOwner.getName() + " removed from guest list!");
				user.sendMessage(ChatColor.GREEN + newOwner.getName()
						+ " has now lost guest access to that block.");
				plugin.protectFile.removeGuest(block.GetId(), newOwner);
			}
		} else {
			user.sendMessage(ChatColor.YELLOW
					+ "Proper use is /protect remguest <Guest> [ID]");
		}
	}
}
