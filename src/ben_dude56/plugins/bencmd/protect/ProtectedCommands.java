package ben_dude56.plugins.bencmd.protect;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ben_dude56.plugins.bencmd.BenCmd;
import ben_dude56.plugins.bencmd.Commands;
import ben_dude56.plugins.bencmd.User;
import ben_dude56.plugins.bencmd.permissions.PermissionUser;
import ben_dude56.plugins.bencmd.protect.ProtectFile.ProtectionType;

public class ProtectedCommands implements Commands {
	BenCmd plugin;
	Logger log = Logger.getLogger("minecraft");

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

	public void AddProtect(String[] args, User user) {
		if (!user.hasPerm("canProtect")) {
			user.sendMessage(ChatColor.RED
					+ "You don't have permission to do that!");
			return;
		}
		Block pointedAt = user.getHandle().getTargetBlock(null, 4);
		if (pointedAt.getType() != Material.CHEST
				&& pointedAt.getType() != Material.WOODEN_DOOR) {
			user.sendMessage(ChatColor.RED
					+ "You are not pointing at a protectable block!");
			return;
		}
		if (plugin.protectFile.getProtection(pointedAt.getLocation()) != -1) {
			user.sendMessage(ChatColor.RED + "That block is already protected!");
			return;
		}
		if (!plugin.lots
				.canBuildHere(user.getHandle(), pointedAt.getLocation())) {
			user.sendMessage(ChatColor.RED
					+ "You're not allowed to protect blocks in other peoples' lots.");
			return;
		}
		if (args.length == 1) {
			if (pointedAt.getType() == Material.CHEST) {
				int id = plugin.protectFile.addProtection(user,
						pointedAt.getLocation(), ProtectionType.Chest);
				user.sendMessage(ChatColor.GREEN
						+ "Protected chest created with owner "
						+ user.getName() + ".");
				String w = pointedAt.getWorld().getName();
				String x = String.valueOf(pointedAt.getX());
				String y = String.valueOf(pointedAt.getY());
				String z = String.valueOf(pointedAt.getX());
				log.info(user.getName() + " created protected chest (id: " + id
						+ ") with owner " + user.getName() + " at position ("
						+ w + "," + x + "," + y + "," + z + ")");
			} else if (pointedAt.getType() == Material.WOODEN_DOOR) {
				int id = plugin.protectFile.addProtection(user,
						pointedAt.getLocation(), ProtectionType.Door);
				user.sendMessage(ChatColor.GREEN
						+ "Protected door created with owner " + user.getName()
						+ ".");
				String w = pointedAt.getWorld().getName();
				String x = String.valueOf(pointedAt.getX());
				String y = String.valueOf(pointedAt.getY());
				String z = String.valueOf(pointedAt.getX());
				log.info(user.getName() + " created protected door (id: " + id
						+ ") with owner " + user.getName() + " at position ("
						+ w + "," + x + "," + y + "," + z + ")");
			}
		} else if (args.length == 2) {
			PermissionUser user2;
			if ((user2 = PermissionUser.matchUser(args[1], plugin)) == null) {
				user.sendMessage(ChatColor.RED + "That player doesn't exist!");
				return;
			}
			if (pointedAt.getType() == Material.CHEST) {
				int id = plugin.protectFile.addProtection(user2,
						pointedAt.getLocation(), ProtectionType.Chest);
				user.sendMessage(ChatColor.GREEN
						+ "Protected chest created with owner "
						+ user2.getName() + ".");
				String w = pointedAt.getWorld().getName();
				String x = String.valueOf(pointedAt.getX());
				String y = String.valueOf(pointedAt.getY());
				String z = String.valueOf(pointedAt.getX());
				log.info(user.getName() + " created protected chest (id: " + id
						+ ") with owner " + user2.getName() + " at position ("
						+ w + "," + x + "," + y + "," + z + ")");
			} else if (pointedAt.getType() == Material.WOODEN_DOOR) {
				int id = plugin.protectFile.addProtection(user2,
						pointedAt.getLocation(), ProtectionType.Door);
				user.sendMessage(ChatColor.GREEN
						+ "Protected door created with owner "
						+ user2.getName() + ".");
				String w = pointedAt.getWorld().getName();
				String x = String.valueOf(pointedAt.getX());
				String y = String.valueOf(pointedAt.getY());
				String z = String.valueOf(pointedAt.getX());
				log.info(user.getName() + " created protected door (id: " + id
						+ ") with owner " + user2.getName() + " at position ("
						+ w + "," + x + "," + y + "," + z + ")");
			}
		} else {
			user.sendMessage(ChatColor.YELLOW
					+ "Proper use is /protect add [owner]");
		}
	}

	public void PublicProtect(String[] args, User user) {
		if (!user.hasPerm("canProtect")) {
			user.sendMessage(ChatColor.RED
					+ "You don't have permission to do that!");
			return;
		}
		Block pointedAt = user.getHandle().getTargetBlock(null, 4);
		if (pointedAt.getType() != Material.CHEST
				&& pointedAt.getType() != Material.WOODEN_DOOR) {
			user.sendMessage(ChatColor.RED
					+ "You are not pointing at a protectable block!");
			return;
		}
		if (plugin.protectFile.getProtection(pointedAt.getLocation()) != -1) {
			user.sendMessage(ChatColor.RED + "That block is already protected!");
			return;
		}
		if (!plugin.lots
				.canBuildHere(user.getHandle(), pointedAt.getLocation())) {
			user.sendMessage(ChatColor.RED
					+ "You're not allowed to protect blocks in other peoples' lots.");
			return;
		}
		if (args.length == 1) {
			if (pointedAt.getType() == Material.CHEST) {
				int id = plugin.protectFile.addProtection(user,
						pointedAt.getLocation(), ProtectionType.PChest);
				user.sendMessage(ChatColor.GREEN
						+ "Public chest created with owner " + user.getName()
						+ ".");
				String w = pointedAt.getWorld().getName();
				String x = String.valueOf(pointedAt.getX());
				String y = String.valueOf(pointedAt.getY());
				String z = String.valueOf(pointedAt.getX());
				log.info(user.getName() + " created public chest (id: " + id
						+ ") with owner " + user.getName() + " at position ("
						+ w + "," + x + "," + y + "," + z + ")");
			} else if (pointedAt.getType() == Material.WOODEN_DOOR) {
				int id = plugin.protectFile.addProtection(user,
						pointedAt.getLocation(), ProtectionType.PDoor);
				user.sendMessage(ChatColor.GREEN
						+ "Public door created with owner " + user.getName()
						+ ".");
				String w = pointedAt.getWorld().getName();
				String x = String.valueOf(pointedAt.getX());
				String y = String.valueOf(pointedAt.getY());
				String z = String.valueOf(pointedAt.getX());
				log.info(user.getName() + " created public door (id: " + id
						+ ") with owner " + user.getName() + " at position ("
						+ w + "," + x + "," + y + "," + z + ")");
			}
		} else if (args.length == 2) {
			PermissionUser user2;
			if ((user2 = PermissionUser.matchUser(args[1], plugin)) == null) {
				user.sendMessage(ChatColor.RED + "That player doesn't exist!");
				return;
			}
			if (pointedAt.getType() == Material.CHEST) {
				int id = plugin.protectFile.addProtection(user2,
						pointedAt.getLocation(), ProtectionType.PChest);
				user.sendMessage(ChatColor.GREEN
						+ "Public chest created with owner " + user2.getName()
						+ ".");
				String w = pointedAt.getWorld().getName();
				String x = String.valueOf(pointedAt.getX());
				String y = String.valueOf(pointedAt.getY());
				String z = String.valueOf(pointedAt.getX());
				log.info(user.getName() + " created public chest (id: " + id
						+ ") with owner " + user2.getName() + " at position ("
						+ w + "," + x + "," + y + "," + z + ")");
			} else if (pointedAt.getType() == Material.WOODEN_DOOR) {
				int id = plugin.protectFile.addProtection(user2,
						pointedAt.getLocation(), ProtectionType.PDoor);
				user.sendMessage(ChatColor.GREEN
						+ "Public door created with owner " + user2.getName()
						+ ".");
				String w = pointedAt.getWorld().getName();
				String x = String.valueOf(pointedAt.getX());
				String y = String.valueOf(pointedAt.getY());
				String z = String.valueOf(pointedAt.getX());
				log.info(user.getName() + " created public door (id: " + id
						+ ") with owner " + user2.getName() + " at position ("
						+ w + "," + x + "," + y + "," + z + ")");
			}
		} else {
			user.sendMessage(ChatColor.YELLOW
					+ "Proper use is /protect public [owner]");
		}
	}

	public void RemoveProtect(String[] args, User user) {
		Block pointedAt = user.getHandle().getTargetBlock(null, 4);
		if (args.length == 1) {
			if (pointedAt.getType() != Material.CHEST
					&& pointedAt.getType() != Material.WOODEN_DOOR) {
				user.sendMessage(ChatColor.RED
						+ "You are not pointing at a protectable block!");
				return;
			}
			if (pointedAt.getType() == Material.CHEST) {
				int id;
				if ((id = plugin.protectFile.getProtection(pointedAt
						.getLocation())) != -1) {
					ProtectedBlock block = plugin.protectFile.getProtection(id);
					if (!block.canChange(user)) {
						user.sendMessage(ChatColor.RED
								+ "You don't have permission to remove the protection on that block!");
					} else {
						plugin.protectFile.removeProtection(block.GetId());
						String w = pointedAt.getWorld().getName();
						String x = String.valueOf(pointedAt.getX());
						String y = String.valueOf(pointedAt.getY());
						String z = String.valueOf(pointedAt.getZ());
						log.info(user.getName() + " removed "
								+ block.getOwner().getName()
								+ "'s protected chest (id: "
								+ String.valueOf(block.GetId())
								+ ") at position (" + w + "," + x + "," + y
								+ "," + z + ")");
						user.sendMessage(ChatColor.GREEN
								+ "The protection on that block was removed.");
					}
				} else {
					user.sendMessage(ChatColor.RED
							+ "You aren't pointing at a protected block!");
				}
			} else if (pointedAt.getType() == Material.WOODEN_DOOR) {
				int id;
				if ((id = plugin.protectFile.getProtection(pointedAt
						.getLocation())) != -1) {
					ProtectedBlock block = plugin.protectFile.getProtection(id);
					if (!block.canChange(user)) {
						user.sendMessage(ChatColor.RED
								+ "You don't have permission to remove the protection on that block!");
					} else {
						plugin.protectFile.removeProtection(block.GetId());
						String w = pointedAt.getWorld().getName();
						String x = String.valueOf(pointedAt.getX());
						String y = String.valueOf(pointedAt.getY());
						String z = String.valueOf(pointedAt.getZ());
						log.info(user.getName() + " removed "
								+ block.getOwner().getName()
								+ "'s protected chest (id: "
								+ String.valueOf(block.GetId())
								+ ") at position (" + w + "," + x + "," + y
								+ "," + z + ")");
						user.sendMessage(ChatColor.GREEN
								+ "The protection on that block was removed.");
					}
				} else {
					user.sendMessage(ChatColor.RED
							+ "You aren't pointing at a protected block!");
				}
			}
		} else if (args.length == 2) {
			int id;
			try {
				id = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				user.sendMessage(ChatColor.RED + args[1]
						+ "Cannot be converted into a number...");
				return;
			}
			ProtectedBlock block = plugin.protectFile.getProtection(id);
			if (block == null) {
				user.sendMessage(ChatColor.RED + "That block isn't protected!");
				return;
			}
			if (block instanceof ProtectedChest) {
				if (!block.canChange(user)) {
					user.sendMessage(ChatColor.RED
							+ "You don't have permission to remove the protection on that block!");
					return;
				} else {
					plugin.protectFile.removeProtection(block.GetId());
					String w = pointedAt.getWorld().getName();
					String x = String.valueOf(pointedAt.getX());
					String y = String.valueOf(pointedAt.getY());
					String z = String.valueOf(pointedAt.getZ());
					log.info(user.getName() + " removed "
							+ block.getOwner().getName()
							+ "'s protected chest (id: "
							+ String.valueOf(block.GetId()) + ") at position ("
							+ w + "," + x + "," + y + "," + z + ")");
					user.sendMessage(ChatColor.GREEN
							+ "The protection on that block was removed.");
					return;
				}
			} else if (block instanceof ProtectedDoor) {
				if (!block.canChange(user)) {
					user.sendMessage(ChatColor.RED
							+ "You don't have permission to remove the protection on that block!");
					return;
				} else {
					plugin.protectFile.removeProtection(block.GetId());
					String w = pointedAt.getWorld().getName();
					String x = String.valueOf(pointedAt.getX());
					String y = String.valueOf(pointedAt.getY());
					String z = String.valueOf(pointedAt.getZ());
					log.info(user.getName() + " removed "
							+ block.getOwner().getName()
							+ "'s protected door (id: "
							+ String.valueOf(block.GetId()) + ") at position ("
							+ w + "," + x + "," + y + "," + z + ")");
					user.sendMessage(ChatColor.GREEN
							+ "The protection on that block was removed.");
					return;
				}
			}
		} else {
			user.sendMessage(ChatColor.YELLOW
					+ "Proper use is /protect remove [ID]");
		}
	}

	public void InfoProtect(String[] args, User user) {
		Block pointedAt = user.getHandle().getTargetBlock(null, 4);
		ProtectedBlock block;
		if ((block = plugin.protectFile.getProtection(plugin.protectFile
				.getProtection(pointedAt.getLocation()))) != null) {
			String owner = block.getOwner().getName();
			String id = String.valueOf(block.GetId());
			String guests = "";
			boolean init = false;
			for (PermissionUser guest : block.getGuests()) {
				if (init) {
					guests += ",";
				} else {
					init = true;
				}
				guests += guest.getName();
			}
			user.sendMessage(ChatColor.DARK_GRAY + "Protection ID: " + id);
			user.sendMessage(ChatColor.DARK_GRAY + "Owner: " + owner);
			user.sendMessage(ChatColor.DARK_GRAY + "Guests: " + guests);
		} else {
			user.sendMessage(ChatColor.RED
					+ "You aren't pointing at a protected block!");
		}
	}

	public void OwnerProtect(String[] args, User user) {
		Block pointedAt = user.getHandle().getTargetBlock(null, 4);
		ProtectedBlock block;
		if (args.length == 2) {
			if ((block = plugin.protectFile.getProtection(plugin.protectFile
					.getProtection(pointedAt.getLocation()))) != null) {
				if (!block.canChange(user)) {
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
				log.info(user.getName() + " has changed the owner of "
						+ block.getOwner().getName()
						+ "'s protected block (id: " + block.GetId() + ") to "
						+ args[1]);
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
			if (!block.canChange(user)) {
				user.sendMessage(ChatColor.RED
						+ "You don't have permission to edit the protection on that block!");
				return;
			}
			PermissionUser newOwner;
			if ((newOwner = PermissionUser.matchUser(args[0], plugin)) == null) {
				user.sendMessage(ChatColor.RED + "That player doesn't exist!");
				return;
			}
			log.info(user.getName() + " has changed the owner of "
					+ block.getOwner().getName() + "'s protected block (id: "
					+ block.GetId() + ") to " + args[1]);
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
				if (!block.canChange(user)) {
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
				log.info(user.getName() + " has added " + newOwner.getName()
						+ " to the guest list of " + block.getOwner().getName()
						+ "'s protected block (id: " + block.GetId() + ")");
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
			block = plugin.protectFile.getProtection(id);
			PermissionUser newOwner;
			if ((newOwner = PermissionUser.matchUser(args[0], plugin)) == null) {
				user.sendMessage(ChatColor.RED + "That player doesn't exist!");
				return;
			}
			log.info(user.getName() + " has added " + newOwner.getName()
					+ " to the guest list of " + block.getOwner().getName()
					+ "'s protected block (id: " + block.GetId() + ")");
			user.sendMessage(ChatColor.GREEN + newOwner.getName()
					+ " now has guest access to that block.");
			plugin.protectFile.addGuest(block.GetId(), newOwner);
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
				if (!block.canChange(user)) {
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
				log.info(user.getName() + " has removed " + newOwner.getName()
						+ " from the guest list of "
						+ block.getOwner().getName()
						+ "'s protected block (id: " + block.GetId() + ")");
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
			block = plugin.protectFile.getProtection(id);
			PermissionUser newOwner;
			if ((newOwner = PermissionUser.matchUser(args[0], plugin)) == null) {
				user.sendMessage(ChatColor.RED + "That player doesn't exist!");
				return;
			}
			log.info(user.getName() + " has removed " + newOwner.getName()
					+ " from the guest list of " + block.getOwner().getName()
					+ "'s protected block (id: " + block.GetId() + ")");
			user.sendMessage(ChatColor.GREEN + newOwner.getName()
					+ " has now lost guest access to that block.");
			plugin.protectFile.removeGuest(block.GetId(), newOwner);
		} else {
			user.sendMessage(ChatColor.YELLOW
					+ "Proper use is /protect remguest <Guest> [ID]");
		}
	}
}
