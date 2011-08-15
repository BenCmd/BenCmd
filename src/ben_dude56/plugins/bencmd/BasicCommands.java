package ben_dude56.plugins.bencmd;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

import ben_dude56.plugins.bencmd.warps.Jail;

public class BasicCommands implements Commands {
	BenCmd plugin;

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
				&& (user.hasPerm("bencmd.time.set") || user.hasPerm("bencmd.time.lock"))) {
			Time(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("day")) {
			plugin.getServer().dispatchCommand(sender, "time day");
			return true;
		} else if (commandLabel.equalsIgnoreCase("dawn")) {
			plugin.getServer().dispatchCommand(sender, "time dawn");
			return true;
		} else if (commandLabel.equalsIgnoreCase("noon")) {
			plugin.getServer().dispatchCommand(sender, "time noon");
			return true;
		} else if (commandLabel.equalsIgnoreCase("dusk")) {
			plugin.getServer().dispatchCommand(sender, "time dusk");
			return true;
		} else if (commandLabel.equalsIgnoreCase("sunrise")) {
			plugin.getServer().dispatchCommand(sender, "time sunrise");
			return true;
		} else if (commandLabel.equalsIgnoreCase("sunset")) {
			plugin.getServer().dispatchCommand(sender, "time sunset");
			return true;
		} else if (commandLabel.equalsIgnoreCase("night")) {
			plugin.getServer().dispatchCommand(sender, "time night");
			return true;
		} else if (commandLabel.equalsIgnoreCase("midnight")) {
			plugin.getServer().dispatchCommand(sender, "time midnight");
			return true;
		} else if (commandLabel.equalsIgnoreCase("svping")) {
			if (user.isServer()) {
				plugin.log.info("pong");
			} else {
				user.sendMessage(ChatColor.RED + "Nice try, asshole!");
			}
			return true;
		} else if (commandLabel.equalsIgnoreCase("spawn")
				&& user.hasPerm("bencmd.spawn.normal")) {
			Spawn(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("god")
				&& user.hasPerm("bencmd.god.self")) {
			God(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("heal")
				&& user.hasPerm("bencmd.heal.self")) {
			Heal(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("bencmd")) {
			BenCmd(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("setspawn")
				&& user.hasPerm("bencmd.spawn.set")) {
			SetSpawn(user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("help")) {
			Help(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("kill")
				&& user.hasPerm("bencmd.kill.self")) {
			Kill(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("spawnmob")
				&& user.hasPerm("bencmd.spawnmob")) {
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
				&& user.hasPerm("bencmd.fire.spread")) {
			Location loc = user.getHandle().getTargetBlock(null, 4)
					.getLocation();
			user.sendMessage(ChatColor.GREEN
					+ "Fire next to that block can now spread...");
			plugin.canSpread.add(loc);
			return true;
		} else if (commandLabel.equalsIgnoreCase("nofire")
				&& user.hasPerm("bencmd.fire.spread")) {
			plugin.canSpread.clear();
			user.sendMessage(ChatColor.GREEN
					+ "All area-specific fire-spread is now disabled.");
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
						+ "You can't kill yourself while you're godded!");
			}
		} else if (args.length == 1) {
			if (!user.hasPerm("bencmd.kill.other")) {
				user.sendMessage(ChatColor.RED
						+ "You don't have permission to do that!");
				return;
			}
			User user2;
			if ((user2 = User.matchUser(args[0], plugin)) != null) {
				if (user2.hasPerm("bencmd.kill.protect") && !user.hasPerm("bencmd.kill.all")) {
					user.sendMessage(ChatColor.RED + "That player is protected from being killed!");
					return;
				}
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
				if (!user.hasPerm("bencmd.time.set")) {
					user.sendMessage(ChatColor.RED
							+ "You don't have permission to do that!");
					return;
				}
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
				if (!user.hasPerm("bencmd.time.set")) {
					user.sendMessage(ChatColor.RED
							+ "You don't have permission to do that!");
					return;
				}
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
				if (!user.hasPerm("bencmd.time.set")) {
					user.sendMessage(ChatColor.RED
							+ "You don't have permission to do that!");
					return;
				}
				int time;
				try {
					time = Integer.parseInt(args[1]);
				} catch (NumberFormatException e) {
					user.sendMessage(ChatColor.RED + "Invaled time.");
					return;
				}
				plugin.log.info("BenCmd: " + user.getDisplayName()
						+ " has set time to " + time);
				plugin.bLog.info("BenCmd: " + user.getDisplayName()
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
				if (!user.hasPerm("bencmd.time.set")) {
					user.sendMessage(ChatColor.RED
							+ "You don't have permission to do that!");
					return;
				}
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
				if (!user.hasPerm("bencmd.time.set")) {
					user.sendMessage(ChatColor.RED
							+ "You don't have permission to do that!");
					return;
				}
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
				if (!user.hasPerm("bencmd.time.set")) {
					user.sendMessage(ChatColor.RED
							+ "You don't have permission to do that!");
					return;
				}
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
				if (!user.hasPerm("bencmd.time.set")) {
					user.sendMessage(ChatColor.RED
							+ "You don't have permission to do that!");
					return;
				}
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
				if (!user.hasPerm("bencmd.time.set")) {
					user.sendMessage(ChatColor.RED
							+ "You don't have permission to do that!");
					return;
				}
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
				if (!user.hasPerm("bencmd.time.set")) {
					user.sendMessage(ChatColor.RED
							+ "You don't have permission to do that!");
					return;
				}
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
				if (!user.hasPerm("bencmd.time.lock")) {
					user.sendMessage(ChatColor.RED
							+ "You don't have permission to do that!");
					return;
				}
				if (plugin.timeRunning) {
					plugin.log.info("BenCmd: " + user.getDisplayName()
							+ " has frozen time!");
					plugin.bLog.info("BenCmd: " + user.getDisplayName()
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
					plugin.log.info("BenCmd: " + user.getDisplayName()
							+ " has unfrozen time!");
					plugin.bLog.info("BenCmd: " + user.getDisplayName()
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
		String spawnworld;
		if (args.length >= 1 && user.hasPerm("bencmd.spawn.all")) {
			spawnworld = args[0];
			user.Spawn(spawnworld);
		} else {
			user.Spawn();
		}
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
				plugin.log.info("BenCmd: " + user.getDisplayName()
						+ " has been made a non-god by "
						+ user.getDisplayName() + "!");
				plugin.bLog.info("BenCmd: " + user.getDisplayName()
						+ " has been made a non-god by "
						+ user.getDisplayName() + "!");
			} else { // If they're not a god
				user.makeGod(); // Add them to the list
				user.sendMessage(ChatColor.GOLD + "You are now in god mode!");
				plugin.log.info("BenCmd: " + user.getDisplayName()
						+ " has been made a god by " + user.getDisplayName()
						+ "!");
				plugin.bLog.info("BenCmd: " + user.getDisplayName()
						+ " has been made a god by " + user.getDisplayName()
						+ "!");
			}
		} else if (args.length == 1) {
			if (!user.hasPerm("bencmd.god.other")) {
				user.sendMessage(ChatColor.RED
						+ "You don't have permission to do that!");
				return;
			}
			User user2;
			if ((user2 = User.matchUser(args[0], plugin)) != null) { // If
																		// they
																		// exist
				if (user2.hasPerm("bencmd.god.protect") && !user.hasPerm("bencmd.god.all")) {
					user.sendMessage(ChatColor.RED + "That player is protected from being godded/ungodded by others!");
					return;
				}
				if (user2.isGod()) { // If they're a god
					user2.makeNonGod(); // Delete them from the
										// list
					user2.sendMessage(ChatColor.GOLD
							+ "You are no longer in god mode!");
					plugin.log.info("BenCmd: " + user2.getDisplayName()
							+ " has been made a non-god by "
							+ user.getDisplayName() + "!");
					plugin.bLog.info("BenCmd: " + user2.getDisplayName()
							+ " has been made a non-god by "
							+ user.getDisplayName() + "!");
				} else { // If they're not a god
					user2.makeGod(); // Add them to the list
					user2.sendMessage(ChatColor.GOLD
							+ "You are now in god mode!");
					plugin.log.info("BenCmd: " + user2.getDisplayName()
							+ " has been made a god by "
							+ user.getDisplayName() + "!");
					plugin.bLog.info("BenCmd: " + user2.getDisplayName()
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
			plugin.log.info("BenCmd: " + user.getDisplayName() + " has healed "
					+ user.getDisplayName());
			plugin.bLog.info("BenCmd: " + user.getDisplayName()
					+ " has healed " + user.getDisplayName());
		} else {
			if (!user.hasPerm("bencmd.heal.other")) {
				user.sendMessage(ChatColor.RED
						+ "You don't have permission to do that!");
				return;
			}
			// Heal the other player
			User user2;
			if ((user2 = User.matchUser(args[0], plugin)) != null) {
				user2 = User.matchUser(args[0], plugin);
				user2.Heal();
				user2.sendMessage(ChatColor.GREEN + "You have been healed.");
				plugin.log.info("BenCmd: " + user.getDisplayName()
						+ " has healed " + user2.getDisplayName());
				plugin.bLog.info("BenCmd: " + user.getDisplayName()
						+ " has healed " + user2.getDisplayName());
			} else {
				user.sendMessage(ChatColor.RED + args[0]
						+ " doesn't exist or is not online.");
			}
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
				.equalsIgnoreCase("rel")) && user.hasPerm("bencmd.reload")) {
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
			plugin.log.warning(user.getDisplayName()
					+ " has reloaded the BenCmd configuration.");
			plugin.bLog.warning(user.getDisplayName()
					+ " has reloaded the BenCmd configuration.");
		} else if (args[0].equalsIgnoreCase("update")
				&& user.hasPerm("bencmd.update")) {
			if (plugin.checkForUpdates(true)) {
				plugin.update(false);
			} else {
				user.sendMessage(ChatColor.RED
						+ "BenCmd is up to date... Use /bencmd fupdate to force an update...");
			}
		} else if (args[0].equalsIgnoreCase("fupdate")
				&& user.hasPerm("bencmd.update")) {
			plugin.update(true);
		} else if (args[0].equalsIgnoreCase("disable")
				&& user.hasPerm("bencmd.disable")) {
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
		plugin.log.info(user.getDisplayName()
				+ " has set the spawn location to (" + newSpawn.getBlockX()
				+ ", " + newSpawn.getBlockY() + ", " + newSpawn.getBlockZ()
				+ ")");
		plugin.bLog.info(user.getDisplayName()
				+ " has set the spawn location to (" + newSpawn.getBlockX()
				+ ", " + newSpawn.getBlockY() + ", " + newSpawn.getBlockZ()
				+ ")");
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
