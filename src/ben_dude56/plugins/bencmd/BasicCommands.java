package ben_dude56.plugins.bencmd;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.Material;

import ben_dude56.plugins.bencmd.warps.Jail;

public class BasicCommands implements Commands {
	BenCmd plugin;
	Logger log = Logger.getLogger("minecraft");

	public BasicCommands(BenCmd instance) {
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
		if (commandLabel.equalsIgnoreCase("time")
				&& user.hasPerm("canChangeTime")) {
			Time(args, user);
			return true;
		}
		if (commandLabel.equalsIgnoreCase("day")) {
			plugin.getServer().dispatchCommand(sender, "time day");
			return true;
		}
		if (commandLabel.equalsIgnoreCase("dawn")) {
			plugin.getServer().dispatchCommand(sender, "time dawn");
			return true;
		}
		if (commandLabel.equalsIgnoreCase("noon")) {
			plugin.getServer().dispatchCommand(sender, "time noon");
			return true;
		}
		if (commandLabel.equalsIgnoreCase("dusk")) {
			plugin.getServer().dispatchCommand(sender, "time dusk");
			return true;
		}
		if (commandLabel.equalsIgnoreCase("sunrise")) {
			plugin.getServer().dispatchCommand(sender, "time sunrise");
			return true;
		}
		if (commandLabel.equalsIgnoreCase("sunset")) {
			plugin.getServer().dispatchCommand(sender, "time sunset");
			return true;
		}
		if (commandLabel.equalsIgnoreCase("night")) {
			plugin.getServer().dispatchCommand(sender, "time night");
			return true;
		}
		if (commandLabel.equalsIgnoreCase("midnight")) {
			plugin.getServer().dispatchCommand(sender, "time midnight");
			return true;
		} else if (commandLabel.equalsIgnoreCase("spawn")
				&& user.hasPerm("canSpawn")) {
			Spawn(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("god")
				&& user.hasPerm("canMakeGod")) {
			God(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("heal")
				&& user.hasPerm("canHeal")) {
			Heal(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("gentree")
				&& user.hasPerm("canMakeTree")) {
			GenTree(user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("bencmd")) {
			BenCmd(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("setspawn")
				&& user.hasPerm("canSetSpawn")) {
			SetSpawn(user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("help")) {
			Help(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("lemonpledge")) {
			plugin.getServer().dispatchCommand(sender,
					"me \u00A7Edemands more lemon pledge!");
			return true;
		} else if (commandLabel.equalsIgnoreCase("kill")
				&& user.hasPerm("canKill")) {
			Kill(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("spawnmob")
				&& user.hasPerm("canSpawnMobs")) {
			SpawnMob(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("rechunk")) {
			Chunk chunk = user.getHandle().getWorld()
					.getChunkAt(user.getHandle().getLocation());
			int chunkx = chunk.getX();
			int chunkz = chunk.getZ();
			user.getHandle().getWorld().unloadChunk(chunkx, chunkz);
			user.getHandle().getWorld().loadChunk(chunkx, chunkz);
			user.getHandle().getWorld().refreshChunk(chunkx, chunkz);
			return true;
		} else if (commandLabel.equalsIgnoreCase("fire")
				&& user.hasPerm("canBurn")) {
			Location loc = user.getHandle().getTargetBlock(null, 4)
					.getLocation();
			plugin.canSpread.add(loc);
			return true;
		} else if (commandLabel.equalsIgnoreCase("nofire")
				&& user.hasPerm("canBurn")) {
			plugin.canSpread.clear();
			return true;
		} else if (commandLabel.equalsIgnoreCase("debug")) {
			return true;
		}
		return false;
	}

	public void Kill(String[] args, User user) {
		if (args.length == 0) {
			if (!user.Kill()) {
				user.sendMessage(ChatColor.RED
						+ "You can't kill someone while they're godded!");
			}
		} else if (args.length == 1) {
			User user2;
			if ((user2 = User.matchUser(args[0], plugin)) != null) {
				if (!user2.Kill()) {
					user.sendMessage(ChatColor.RED
							+ "You can't kill someone while they're godded!");
				}
			} else {
				user.sendMessage(ChatColor.RED + "That user doesn't exist!");
			}
		}
	}

	public void Time(String[] args, User user) {
		if (args.length == 0) {
			user.sendMessage(ChatColor.YELLOW
					+ "Proper use is /time {day|night|set|lock} [time]");
		} else {
			if (args[0].equalsIgnoreCase("day")) {
				if (user.isServer()) {
					for (World world : plugin.getServer().getWorlds()) {
						world.setTime(0);
						plugin.lastTime = world.getFullTime();
					}
				} else {
					user.getHandle().getWorld().setTime(0);
					plugin.lastTime = user.getHandle().getWorld().getFullTime();
				}
			} else if (args[0].equalsIgnoreCase("night")) {
				if (user.isServer()) {
					for (World world : plugin.getServer().getWorlds()) {
						world.setTime(15000);
						plugin.lastTime = world.getFullTime();
					}
				} else {
					user.getHandle().getWorld().setTime(15000);
					plugin.lastTime = user.getHandle().getWorld().getFullTime();
				}
			} else if (args[0].equalsIgnoreCase("set")) {
				int time;
				try {
					time = Integer.parseInt(args[1]);
				} catch (NumberFormatException e) {
					user.sendMessage(ChatColor.RED + "Invaled time.");
					return;
				}
				log.info("BenCmd: " + user.getDisplayName()
						+ " has set time to " + time);
				if (user.isServer()) {
					for (World world : plugin.getServer().getWorlds()) {
						world.setTime(time);
						plugin.lastTime = world.getFullTime();
					}
				} else {
					user.getHandle().getWorld().setTime(time);
					plugin.lastTime = user.getHandle().getWorld().getFullTime();
				}
			} else if (args[0].equalsIgnoreCase("dawn")) {
				if (user.isServer()) {
					for (World world : plugin.getServer().getWorlds()) {
						world.setTime(23000);
						plugin.lastTime = world.getFullTime();
					}
				} else {
					user.getHandle().getWorld().setTime(23000);
					plugin.lastTime = user.getHandle().getWorld().getFullTime();
				}
			} else if (args[0].equalsIgnoreCase("sunrise")) {
				if (user.isServer()) {
					for (World world : plugin.getServer().getWorlds()) {
						world.setTime(22500);
						plugin.lastTime = world.getFullTime();
					}
				} else {
					user.getHandle().getWorld().setTime(22500);
					plugin.lastTime = user.getHandle().getWorld().getFullTime();
				}
			} else if (args[0].equalsIgnoreCase("noon")) {
				if (user.isServer()) {
					for (World world : plugin.getServer().getWorlds()) {
						world.setTime(6000);
						plugin.lastTime = world.getFullTime();
					}
				} else {
					user.getHandle().getWorld().setTime(6000);
					plugin.lastTime = user.getHandle().getWorld().getFullTime();
				}
			} else if (args[0].equalsIgnoreCase("dusk")) {
				if (user.isServer()) {
					for (World world : plugin.getServer().getWorlds()) {
						world.setTime(13000);
						plugin.lastTime = world.getFullTime();
					}
				} else {
					user.getHandle().getWorld().setTime(13000);
					plugin.lastTime = user.getHandle().getWorld().getFullTime();
				}
			} else if (args[0].equalsIgnoreCase("sunset")) {
				if (user.isServer()) {
					for (World world : plugin.getServer().getWorlds()) {
						world.setTime(12000);
						plugin.lastTime = world.getFullTime();
					}
				} else {
					user.getHandle().getWorld().setTime(12000);
					plugin.lastTime = user.getHandle().getWorld().getFullTime();
				}
			} else if (args[0].equalsIgnoreCase("midnight")) {
				if (user.isServer()) {
					for (World world : plugin.getServer().getWorlds()) {
						world.setTime(18000);
						plugin.lastTime = world.getFullTime();
					}
				} else {
					user.getHandle().getWorld().setTime(18000);
					plugin.lastTime = user.getHandle().getWorld().getFullTime();
				}
			} else if (args[0].equalsIgnoreCase("lock")) {
				if (plugin.timeRunning) {
					log.info("BenCmd: " + user.getDisplayName()
							+ " has frozen time!");
					if (user.isServer()) {
						plugin.timeFrozenAt = plugin.getServer().getWorlds()
								.get(0).getTime();
					} else {
						plugin.timeFrozenAt = user.getHandle().getWorld()
								.getTime();
					}
					plugin.timeRunning = false;
					plugin.getServer().broadcastMessage(
							ChatColor.DARK_BLUE + user.getDisplayName()
									+ " has stopped the clock!");
				} else {
					log.info("BenCmd: " + user.getDisplayName()
							+ " has unfrozen time!");
					plugin.timeRunning = true;
					plugin.getServer().broadcastMessage(
							ChatColor.DARK_BLUE + user.getDisplayName()
									+ " has restarted time!");
				}
			}
		}
	}

	public void Spawn(String[] args, User user) {
		if (user.isServer()) {
			user.sendMessage(ChatColor.RED + "The server cannot do that!");
			return;
		}
		user.Spawn();
	}

	public void God(String[] args, User user) {
		if (args.length == 0) {
			if (user.isServer()) {
				user.sendMessage(ChatColor.YELLOW
						+ "Proper use is /god [player]");
				return;
			}
			if (user.isGod()) { // If they're a god
				user.makeNonGod(); // Delete them from the list
				user.sendMessage(ChatColor.GOLD
						+ "You are no longer in god mode!");
				log.info("BenCmd: " + user.getDisplayName()
						+ " has been made a non-god by "
						+ user.getDisplayName() + "!");
			} else { // If they're not a god
				user.makeGod(); // Add them to the list
				user.sendMessage(ChatColor.GOLD + "You are now in god mode!");
				log.info("BenCmd: " + user.getDisplayName()
						+ " has been made a god by " + user.getDisplayName()
						+ "!");
			}
		} else if (args.length == 1) {
			User user2;
			if ((user2 = User.matchUser(args[0], plugin)) != null) { // If
																		// they
																		// exist
				if (user2.isGod()) { // If they're a god
					user2.makeNonGod(); // Delete them from the
										// list
					user2.sendMessage(ChatColor.GOLD
							+ "You are no longer in god mode!");
					log.info("BenCmd: " + user2.getDisplayName()
							+ " has been made a non-god by "
							+ user.getDisplayName() + "!");
				} else { // If they're not a god
					user2.makeGod(); // Add them to the list
					user2.sendMessage(ChatColor.GOLD
							+ "You are now in god mode!");
					log.info("BenCmd: " + user2.getDisplayName()
							+ " has been made a god by "
							+ user.getDisplayName() + "!");
				}
			}
		} else {
			user.sendMessage(ChatColor.YELLOW + "Proper use is /god [player]");
		}
	}

	public void Heal(String[] args, User user) {
		if (args.length == 0) {
			if (user.isServer()) {
				user.sendMessage(ChatColor.YELLOW
						+ "Proper use is /heal [player]");
				return;
			}
			// Heal the player
			user.Heal();
			user.sendMessage(ChatColor.GREEN + "You have been healed.");
			log.info("BenCmd: " + user.getDisplayName() + " has healed "
					+ user.getDisplayName());
		} else {
			// Heal the other player
			User user2;
			if ((user2 = User.matchUser(args[0], plugin)) != null) {
				user2 = User.matchUser(args[0], plugin);
				user2.Heal();
				user2.sendMessage(ChatColor.GREEN + "You have been healed.");
				log.info("BenCmd: " + user.getDisplayName() + " has healed "
						+ user2.getDisplayName());
			} else {
				user.sendMessage(ChatColor.RED + args[0]
						+ " doesn't exist or is not online.");
			}
		}
	}

	public void GenTree(User user) {
		if (user.isServer()) {
			user.sendMessage(ChatColor.RED + "The server cannot do that!");
			return;
		}
		Block targetBlock = user.getHandle().getTargetBlock(null, 30); // Get
																		// the
		// block
		// that the
		// player is
		// pointing
		// at
		if (targetBlock.getType() == Material.AIR) {
			user.sendMessage(ChatColor.RED
					+ "You must be pointing at a block to sprout a tree!");
			return;
		}
		if (user.getHandle()
				.getWorld()
				.generateTree(
						new Location(user.getHandle().getWorld(),
								targetBlock.getX(), targetBlock.getY() + 1,
								targetBlock.getZ()), TreeType.TREE)) {
			// It sprouted properly!
			log.info("BenCmd: " + user.getDisplayName()
					+ " has sprouted a tree at (" + targetBlock.getX() + ","
					+ targetBlock.getY() + 1 + "," + targetBlock.getZ() + ")!");
			user.sendMessage(ChatColor.GREEN + "Tree created successfully!");
		} else {
			// There was a problem
			user.sendMessage(ChatColor.RED + "You can't sprout a tree there!");
		}
	}

	public void BenCmd(String[] args, User user) {
		if (args.length == 0 || args[0].equalsIgnoreCase("version")) {
			PluginDescriptionFile pdfFile = plugin.getDescription();
			user.sendMessage(ChatColor.YELLOW + "This server is running "
					+ pdfFile.getName() + " version " + pdfFile.getVersion()
					+ ".");
			return;
		}
		if ((args[0].equalsIgnoreCase("reload") || args[0]
				.equalsIgnoreCase("rel")) && user.hasPerm("canReloadConfig")) {
			plugin.perm.userFile.loadFile();
			plugin.perm.userFile.loadUsers();
			plugin.perm.groupFile.loadFile();
			plugin.perm.groupFile.loadGroups();
			plugin.itemAliases.loadFile();
			plugin.mainProperties.loadFile();
			plugin.checkpoints.ClearWarps();
			plugin.chests.loadFile();
			plugin.dispensers.loadFile();
			plugin.homes.ReloadHomes();
			plugin.warps.LoadWarps();
			plugin.jail = new Jail(plugin);
			plugin.lots.reload();
			plugin.lotListener.corner.clear();
			plugin.godmode.clear();
			plugin.heroActive = false;
			plugin.timeRunning = true;
			user.sendMessage(ChatColor.GREEN
					+ "BenCmd Config Successfully reloaded!");
			log.warning(user.getDisplayName()
					+ " has reloaded the BenCmd configuration.");
		}
		if (args[0].equalsIgnoreCase("disable") && user.hasPerm("canDisable")) {
			plugin.getServer()
					.broadcastMessage(
							ChatColor.RED
									+ "BenCmd is being temporarily disabled for maintenance...");
			plugin.getServer()
					.broadcastMessage(
							ChatColor.RED
									+ "Some commands may cease to function until it is restarted...");
			plugin.getServer().getPluginManager().disablePlugin(plugin);
		}
	}

	public void SetSpawn(User user) {
		if (user.isServer()) {
			user.sendMessage(ChatColor.RED + "The server cannot do that!");
			return;
		}
		Location newSpawn = user.getHandle().getLocation();
		user.sendMessage(ChatColor.GREEN + "The spawn location has been set!");
		log.info(user.getDisplayName() + " has set the spawn location to ("
				+ newSpawn.getBlockX() + ", " + newSpawn.getBlockY() + ", "
				+ newSpawn.getBlockZ() + ")");
		user.getHandle()
				.getWorld()
				.setSpawnLocation(newSpawn.getBlockX(), newSpawn.getBlockY(),
						newSpawn.getBlockZ());
	}

	public void Help(String[] args, User user) {
		int pageToShow;
		if (args.length == 0) {
			pageToShow = 1;
		} else {
			if (args[0].equalsIgnoreCase("consuela")) {
				user.sendMessage(ChatColor.GREEN + "/lemonpledge"
						+ ChatColor.WHITE + " - " + ChatColor.GRAY
						+ "Demand more lemon pledge...");
				return;
			}
			try {
				pageToShow = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				user.sendMessage(ChatColor.RED + args[0]
						+ " cannot be converted to a number!");
				return;
			}
		}
		List<BCommand> commands = getCommands(user);
		int max;
		if (pageToShow > (max = (int) Math.ceil((commands.size() - 1) / 6) + 1)) {
			user.sendMessage(ChatColor.RED + "There are only " + max
					+ " pages to show!");
			return;
		} else if (pageToShow <= 0) {
			user.sendMessage(ChatColor.RED
					+ "The page number must be a natural number, retard!");
			return;
		}
		int i = (pageToShow - 1) * 6;
		user.sendMessage(ChatColor.YELLOW + "Displaying help page "
				+ ChatColor.GREEN + pageToShow + ChatColor.YELLOW + " of "
				+ ChatColor.GREEN + max + ChatColor.YELLOW + ":");
		while (i < (pageToShow - 1) * 6 + 6) {
			if (i >= commands.size()) {
				break;
			}
			user.sendMessage(ChatColor.GREEN + commands.get(i).getName()
					+ ChatColor.WHITE + " - " + ChatColor.GRAY
					+ commands.get(i).getDescription());
			i++;
		}
	}

	public void SpawnMob(String[] args, User user) {
		if (user.isServer()) {
			user.sendMessage(ChatColor.RED + "The server cannot do that!");
			return;
		}
		if (args.length != 1 && args.length != 2) {
			user.sendMessage(ChatColor.YELLOW
					+ "Proper use is /spawnmob <Mob Name> [Amount]");
			return;
		}
		int amount = 1;
		if (args.length == 2) {
			try {
				amount = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				user.sendMessage(ChatColor.RED + args[1]
						+ " cannot be converted into a number!");
				return;
			}
		}
		for (int i = 0; i < amount; i++) {
			user.getHandle()
					.getWorld()
					.spawnCreature(user.getHandle().getLocation(),
							CreatureType.fromName(args[0]));
		}
	}

	public List<BCommand> getCommands(User user) {
		List<BCommand> commands = new ArrayList<BCommand>();
		commands.add(new BCommand("/help [page]",
				"Displays the xth page of the command list...", "."));
		commands.add(new BCommand("/slow [delay]",
				"Makes each user wait x seconds between chats.", "canSlowMode"));
		commands.add(new BCommand("/mute <player>", "Mutes a player.",
				"canMute"));
		commands.add(new BCommand("/unmute <player>", "Unmutes a player.",
				"canMute"));
		commands.add(new BCommand("/list", "Lists the players online.",
				"canListPlayers"));
		commands.add(new BCommand("/time {day|night|lock}",
				"Sets the time of day or locks the time.", "canChangeTime"));
		commands.add(new BCommand("/spawn", "Sends you to the spawn.",
				"canSpawn"));
		commands.add(new BCommand("/item <ID>[:damage] [amount] [player]",
				"Gives you an item.", "canSpawnItems"));
		commands.add(new BCommand("/god [player]",
				"Makes you or another player a god.", "canMakeGod"));
		commands.add(new BCommand("/heal [player]",
				"Heals you or another player", "canHeal"));
		commands.add(new BCommand("/bencmd reload",
				"Reloads the BenCmd Config", "canReloadConfig"));
		commands.add(new BCommand(
				"/user <name> {add|remove|g:<group>|<permissions>}",
				"Controls user permissions", "canChangePerm"));
		commands.add(new BCommand(
				"/group <name> {add|remove|g:<group>|<permissions>}",
				"Controls group permissions", "canChangePerm"));
		commands.add(new BCommand("/warp <warp>",
				"Warps you to a pre-defined point.", "canWarp"));
		commands.add(new BCommand("/warp <warp> <player>",
				"Warps another player to a pre-defined point.", "canWarpOthers"));
		commands.add(new BCommand("/setwarp <warp>", "Sets a new warp.",
				"canEditWarps"));
		commands.add(new BCommand("/delwarp <warp>", "Deletes a warp.",
				"canEditWarps"));
		commands.add(new BCommand("/back",
				"Warps you back to before your last warp.", "canWarp"));
		commands.add(new BCommand("/home <number>",
				"Teleports to your xth home.", "canWarpOwnHomes"));
		commands.add(new BCommand("/home <number> <player>",
				"Teleports to another player's xth home.", "canWarpOtherHomes"));
		commands.add(new BCommand("/sethome <number>", "Sets your xth home.",
				"canEditOwnHomes"));
		commands.add(new BCommand("/sethome <number> <player>",
				"Sets another player's xth home.", "canEditOtherHomes"));
		commands.add(new BCommand("/delhome <number>",
				"Deletes your xth home.", "canEditOwnHomes"));
		commands.add(new BCommand("/delhome <number> <player>",
				"Deletes another player's xth home.", "canEditOtherHomes"));
		commands.add(new BCommand("/clearinventory [player]",
				"Clears your own or another player's inventory.",
				"canClearInventory"));
		commands.add(new BCommand("/jail <player>", "Jails a player.",
				"canJail"));
		commands.add(new BCommand("/unjail <player>", "Unjails a  player.",
				"canJail"));
		commands.add(new BCommand("/setjail", "Sets the jail location.",
				"canJail"));
		commands.add(new BCommand("/unl <ID>[:Damage]",
				"Creates an unlimited dispenser", "canMakeUnlDisp"));
		commands.add(new BCommand("/disp", "Creates a disposal chest.",
				"canMakeDispChest"));
		commands.add(new BCommand("/lot <command>",
				"Edits or gets info on lots.", "."));
		commands.add(new BCommand("/kit <kitname>", "Spawns a kit.",
				"canSpawnKit"));
		commands.add(new BCommand("/poof", "Makes you invisible.", "canPoof"));
		commands.add(new BCommand("/nopoof",
				"Makes you able to see invisible players.", "canNoPoof"));
		commands.add(new BCommand(
				"/protect {add|remove|info|setowner|addguest|remguest}",
				"Deals with protection.", "."));
		commands.add(new BCommand("/lock", "Locks a chest", "canProtect"));
		commands.add(new BCommand("/public", "Publicly locks a chest",
				"canProtect"));
		commands.add(new BCommand("/unlock", "Unlocks a chest", "canProtect"));
		commands.add(new BCommand("/share", "Adds a guest to a chest",
				"canProtect"));
		commands.add(new BCommand("/unshare", "Removes a guest from a chest",
				"canProtect"));
		commands.add(new BCommand("/setspawn", "Sets the map spawn point.",
				"canSetSpawn"));
		commands.add(new BCommand("/me <message>",
				"Shows a message in the format \"*<player> <message>\".", "."));
		commands.add(new BCommand("/tell <player> <message>", "PMs a player.",
				"."));
		commands.add(new BCommand("/storm {off|rain|thunder}",
				"Changes the current map's storm status.", "canControlWeather"));
		commands.add(new BCommand("/strike",
				"Strikes lightning where you are pointing.",
				"canControlWeather"));
		commands.add(new BCommand("/offline",
				"Makes you appear to be offline.", "canOffline"));
		commands.add(new BCommand("/online",
				"Makes you re-appear to be online.", "canOffline"));
		commands.add(new BCommand("/report <player> <reason>",
				"Reports a player to the admins.", "canReport"));
		commands.add(new BCommand(
				"/ticket",
				"Lists and changes existing reports. Type /ticket for more info...",
				"."));
		commands.add(new BCommand("/kill <player>", "Kills the player listed.",
				"canKill"));
		commands.add(new BCommand("/spawnmob <Mob Name> [Amount]",
				"Spawns a specific amount of a specific mob.", "canSpawnMobs"));
		commands.add(new BCommand("/buy <Item> [Amount]", "Buys an item.", "."));
		commands.add(new BCommand("/sell <Item> [Amount]",
				"Sells an item from your inventory.", "."));
		commands.add(new BCommand("/price <Item>",
				"Lists the price of a specific item.", "."));
		commands.add(new BCommand("/market",
				"Used to administrate the economic functions of BenCmd.",
				"canControlMarket"));
		commands.add(new BCommand("/tp <player> [player]",
				"Teleports a player to another player.", "canTpSelf"));
		commands.add(new BCommand("/tphere <player>",
				"Teleports a player to you.", "canTpOther"));

		for (int i = 0; i < commands.size(); i++) {
			if (!commands.get(i).canUse(user)) {
				commands.remove(i);
			}
		}
		return commands;
	}

}
