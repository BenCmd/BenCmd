package com.bendude56.bencmd;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

import com.bendude56.bencmd.warps.Jail;


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
		} else if (commandLabel.equalsIgnoreCase("ping")) {
			if (user.isServer()) {
				if (args.length == 0) {
					plugin.log.info("pong");
				} else {
					plugin.log.info(args[0]);
				}
			} else {
				user.sendMessage(ChatColor.RED + "No, you cannot spam the server console, smart one!");
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
		} else if (commandLabel.equalsIgnoreCase("mob")
				&& user.hasPerm("bencmd.spawnmob")) {
			SpawnMob(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("killall")
				&& user.hasPerm("bencmd.spawnmob")) {
			KillEntities(args, user);
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
		} else if (commandLabel.equalsIgnoreCase("mainprop")
				&& user.hasPerm("bencmd.mainedit")) {
			if (args.length == 0) {
				user.sendMessage(ChatColor.YELLOW + "Proper use is: /mainprop <property> [value]");
			} else if (args.length == 1) {
				if (plugin.mainProperties.containsKey(args[0])) {
					user.sendMessage(ChatColor.YELLOW + "That property is currently set to:");
					user.sendMessage(BenCmd.getPlugin().mainProperties.getProperty(args[0]));
				} else {
					user.sendMessage(ChatColor.RED + "That property doesn't exist!");
				}
			} else {
				String val = "";
				for (int i = 1; i < args.length; i++) {
					if (val.isEmpty()) {
						val += args[i];
					} else {
						val += " " + args[i];
					}
				}
				BenCmd.getPlugin().mainProperties.setProperty(args[0], val);
				BenCmd.getPlugin().mainProperties.saveFile("");
				user.sendMessage(ChatColor.GREEN + "Success!");
			}
			return true;
		} else if (commandLabel.equalsIgnoreCase("cr")
				&& user.hasPerm("bencmd.creative.self")) {
			Cr(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("debug")) {
			if (!user.isDev()) {
				user.sendMessage(ChatColor.RED + "That command is reserved for BenCmd developers only!");
				plugin.logPermFail();
				return true;
			}
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
				plugin.logPermFail();
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
					plugin.logPermFail();
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
					plugin.logPermFail();
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
					plugin.logPermFail();
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
					plugin.logPermFail();
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
					plugin.logPermFail();
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
					plugin.logPermFail();
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
					plugin.logPermFail();
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
					plugin.logPermFail();
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
					plugin.logPermFail();
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
					plugin.logPermFail();
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
				plugin.logPermFail();
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
				plugin.logPermFail();
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
			if (!plugin.update(false)) {
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
						+ " is an invalid page number!");
				return;
			}
		}
		List<BCommand> commands = getCommands(user);
		int max;
		if (pageToShow > (max = (int) Math.ceil((commands.size() - 1) / 6) + 1)) {
			user.sendMessage(ChatColor.RED + "There are only " + max
					+ " pages!");
			return;
		} else if (pageToShow <= 0) {
			user.sendMessage(ChatColor.RED
					+ "There are no negative pages.");
			return;
		}
		int i = (pageToShow - 1) * 6;
		user.sendMessage(ChatColor.YELLOW + "Displaying help page "
				+ ChatColor.RED + pageToShow + ChatColor.YELLOW + " of "
				+ ChatColor.RED + max + ChatColor.YELLOW + ":");
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
		
		int mobCounter = 0;
		
		// Prepare for passengers
		String[] passengers = args[0].split(",");
		LivingEntity vehicle = null, passenger, mob;
		String mobName;
		
		// Spawn the mob(s)
		for (int i = 0; i < amount; i++) {
			vehicle = null;
			for (int p = 0; p < passengers.length; p++) {
				mobName = getMobAlias(passengers[p]);
				if (!mobName.equalsIgnoreCase("ERROR")) {
					mob = user.getHandle().getWorld()
						.spawnCreature(user.getHandle().getLocation(),
								CreatureType.fromName(mobName));
		
					// Add up the passengers
					if (vehicle == null && mob != null) {
						vehicle = mob;
					}
					else if (vehicle != null){
						passenger = mob;
						vehicle.setPassenger(passenger);
						vehicle = mob;
					}
					if (mob != null) {
						mobCounter++;
					}
				}
			}
		}
		user.sendMessage(ChatColor.GREEN + "" + mobCounter + " mobs spawned!");
	}
	
	public void KillEntities(String args[], User user) {
		// Tally up the mobs
		String mobToKill;
		int mobCounter = 0;
		int range = -1;
		try {
			range = Integer.parseInt(args[args.length-1]);
		} catch (NumberFormatException e) {
			range = -1;
		}
		for (int i = 0 ; i < args.length ; i++) {
			mobToKill = getCraftMobAlias(args[i]);
			if (mobToKill != null) {
				for (int ii = 0 ; ii < user.getHandle().getWorld().getEntities().size() ; ii++) {
					String entity = user.getHandle().getWorld().getEntities().get(ii).toString(); {
						if (entity == mobToKill) {
							if (range == -1 ||
									getDistance(user.getHandle().getLocation(),
											user.getHandle().getWorld().getEntities().get(ii).getLocation(),
											false) <= range) {
								user.getHandle().getWorld().getEntities().get(ii).remove();
								mobCounter++;
							}
						}
					}
				}
			}
		}
		user.sendMessage(ChatColor.GREEN + "" + mobCounter + " mobs were killed!");
	}
	
	private double getDistance(Location loc1, Location loc2, boolean checkY) {
		
		double xdis = 0, ydis = 0, zdis = 0;
		double distance = 0;
		
		xdis = loc1.getX() - loc2.getX();
		ydis = loc1.getY() - loc2.getY();
		zdis = loc1.getZ() - loc2.getZ();
		
		if (checkY) {
			distance = Math.sqrt(
					Math.pow(xdis, 2) +
					Math.pow(ydis, 2) +
					Math.pow(zdis, 2) );
		} else {
			distance = Math.sqrt(
					Math.pow(xdis, 2) +
					Math.pow(zdis, 2) );
		}
		return distance;
	}
	
	private String getMobAlias(String alias) {
		if (alias.equalsIgnoreCase("creeper")
				|| alias.equalsIgnoreCase("creepers")) {
			alias = "Creeper";
			return alias;
		}
		if (alias.equalsIgnoreCase("zombie")
				|| alias.equalsIgnoreCase("zombies")) {
			alias = "Zombie";
			return alias;
		}
		if (alias.equalsIgnoreCase("skeleton") 
				|| alias.equalsIgnoreCase("skele") 
				|| alias.equalsIgnoreCase("skeletons")
				|| alias.equalsIgnoreCase("skeles")) {
			alias = "Skeleton";
			return alias;
		}
		if (alias.equalsIgnoreCase("spider") 
				|| alias.equalsIgnoreCase("spiders")) {
			alias = "Spider";
			return alias;
		}
		if (alias.equalsIgnoreCase("pig")
				|| alias.equalsIgnoreCase("piggy")
				|| alias.equalsIgnoreCase("pigs")) {
			alias = "Pig";
			return alias;
		}
		if (alias.equalsIgnoreCase("chicken") 
				|| alias.equalsIgnoreCase("chickens")) {
			alias = "Chicken";
			return alias;
		}
		if (alias.equalsIgnoreCase("cow")
				|| alias.equalsIgnoreCase("cows")) {
			alias = "Cow";
			return alias;
		}
		if (alias.equalsIgnoreCase("sheep") 
				|| alias.equalsIgnoreCase("sheeps")) {
			alias = "Sheep";
			return alias;
		}
		if (alias.equalsIgnoreCase("wolf")
				|| alias.equalsIgnoreCase("wolves")) {
			alias = "Wolf";
			return alias;
		}
		if (alias.equalsIgnoreCase("squid")
				|| alias.equalsIgnoreCase("squids")) {
			alias = "Squid";
			return alias;
		}
		if (alias.equalsIgnoreCase("slime")
				|| alias.equalsIgnoreCase("slimes")) {
			alias = "Slime";
			return alias;
		}
		if (alias.equalsIgnoreCase("ghast")
				|| alias.equalsIgnoreCase("ghasts")) {
			alias = "Ghast";
			return alias;
		}
		if (alias.equalsIgnoreCase("pigzombie") 
				|| alias.equalsIgnoreCase("zombiepigman") || alias.equalsIgnoreCase("zombiepig")) {
			alias = "PigZombie";
			return alias;
		}
		if (alias.equalsIgnoreCase("giant") 
				|| alias.equalsIgnoreCase("bigzombie") || alias.equalsIgnoreCase("giantzombie")) {
			alias = "Giant";
			return alias;
		}
		if (alias.equalsIgnoreCase("monster") 
				|| alias.equalsIgnoreCase("human") 
				|| alias.equalsIgnoreCase("steve")) {
			alias = "Monster";
			return alias;
		}
		if (alias.equalsIgnoreCase("enderman")
				|| alias.equalsIgnoreCase("eman")
				|| alias.equalsIgnoreCase("endman")
				|| alias.equalsIgnoreCase("endermen")
				|| alias.equalsIgnoreCase("emen")
				|| alias.equalsIgnoreCase("endmen")) {
			alias = "Enderman";
			return alias;
		}
		if (alias.equalsIgnoreCase("bluespider")
				|| alias.equalsIgnoreCase("cavespider")
				|| alias.equalsIgnoreCase("smallspider")
				|| alias.equalsIgnoreCase("poisonspider")
				|| alias.equalsIgnoreCase("cavespiders")
				|| alias.equalsIgnoreCase("bluespiders")) {
			alias = "CaveSpider";
			return alias;
		}
		if (alias.equalsIgnoreCase("silverfish")
				|| alias.equalsIgnoreCase("sliverfish")
				|| alias.equalsIgnoreCase("caterpillar")
				|| alias.equalsIgnoreCase("caterpillars")) {
			alias = "Silverfish";
			return alias;
		}
		alias = "ERROR";
		return alias;
	}
	
	private String getCraftMobAlias(String alias) {
		if (alias.equalsIgnoreCase("creeper") 
				|| alias.equalsIgnoreCase("creepers")
				|| alias.equalsIgnoreCase("craftcreeper")
				|| alias.equalsIgnoreCase("craftcreepers")) {
			alias = "CraftCreeper";
			return alias;
		}
		if (alias.equalsIgnoreCase("zombie")
				|| alias.equalsIgnoreCase("zombies")
				|| alias.equalsIgnoreCase("craftzombie")
				|| alias.equalsIgnoreCase("craftzombies")) {
			alias = "CraftZombie";
			return alias;
		}
		if (alias.equalsIgnoreCase("skeleton")
				|| alias.equalsIgnoreCase("skele")
				|| alias.equalsIgnoreCase("skeletons")
				|| alias.equalsIgnoreCase("skeles")
				|| alias.equalsIgnoreCase("craftskeleton")
				|| alias.equalsIgnoreCase("craftskeletons")) {
			alias = "CraftSkeleton";
			return alias;
		}
		if (alias.equalsIgnoreCase("spider")
				|| alias.equalsIgnoreCase("spiders")
				|| alias.equalsIgnoreCase("craftspider")
				|| alias.equalsIgnoreCase("craftspiders")) {
			alias = "CraftSpider";
			return alias;
		}
		if (alias.equalsIgnoreCase("pig")
				|| alias.equalsIgnoreCase("piggy")
				|| alias.equalsIgnoreCase("pigs")
				|| alias.equalsIgnoreCase("craftpig")
				|| alias.equalsIgnoreCase("craftpigs")) {
			alias = "CraftPig";
			return alias;
		}
		if (alias.equalsIgnoreCase("chicken")
				|| alias.equalsIgnoreCase("chickens")
				|| alias.equalsIgnoreCase("craftchicken")
				|| alias.equalsIgnoreCase("craftchickens")) {
			alias = "CraftChicken";
			return alias;
		}
		if (alias.equalsIgnoreCase("cow")
				|| alias.equalsIgnoreCase("cows")
				|| alias.equalsIgnoreCase("craftcow")
				|| alias.equalsIgnoreCase("craftcows")) {
			alias = "CraftCow";
			return alias;
		}
		if (alias.equalsIgnoreCase("sheep")
				|| alias.equalsIgnoreCase("sheeps")
				|| alias.equalsIgnoreCase("craftsheep")
				|| alias.equalsIgnoreCase("craftsheeps")) {
			alias = "CraftSheep";
			return alias;
		}
		if (alias.equalsIgnoreCase("wolf")
				|| alias.equalsIgnoreCase("wolves")
				|| alias.equalsIgnoreCase("craftwolf")
				|| alias.equalsIgnoreCase("craftwolves")) {
			alias = "CraftWolf";
			return alias;
		}
		if (alias.equalsIgnoreCase("squid")
				|| alias.equalsIgnoreCase("squids")
				|| alias.equalsIgnoreCase("craftsquid")
				|| alias.equalsIgnoreCase("craftsquids")) {
			alias = "CraftSquid";
			return alias;
		}
		if (alias.equalsIgnoreCase("slime")
				|| alias.equalsIgnoreCase("slimes")
				|| alias.equalsIgnoreCase("craftslime")
				|| alias.equalsIgnoreCase("craftslimes")) {
			alias = "CraftSlime";
			return alias;
		}
		if (alias.equalsIgnoreCase("ghast")
				|| alias.equalsIgnoreCase("ghasts")
				|| alias.equalsIgnoreCase("craftghast")
				|| alias.equalsIgnoreCase("craftghasts")) {
			alias = "CraftGhast";
			return alias;
		}
		if (alias.equalsIgnoreCase("pigzombie")
				|| alias.equalsIgnoreCase("zombiepigman")
				|| alias.equalsIgnoreCase("zombiepig")
				|| alias.equalsIgnoreCase("pigzombies")
				|| alias.equalsIgnoreCase("zombiepigmen")
				|| alias.equalsIgnoreCase("zombiepigs")
				|| alias.equalsIgnoreCase("craftpigzombies")
				|| alias.equalsIgnoreCase("craftpigzombie")) {
			alias = "CraftPigZombie";
			return alias;
		}
		if (alias.equalsIgnoreCase("giant")
				|| alias.equalsIgnoreCase("bigzombie")
				|| alias.equalsIgnoreCase("giantzombie")
				|| alias.equalsIgnoreCase("craftgiant")
				|| alias.equalsIgnoreCase("craftgiants")) {
			alias = "CraftGiant";
			return alias;
		}
		if (alias.equalsIgnoreCase("monster") 
				|| alias.equalsIgnoreCase("human")
				|| alias.equalsIgnoreCase("steve")
				|| alias.equalsIgnoreCase("craftmonster")
				|| alias.equalsIgnoreCase("craftmonsters")) {
			alias = "CraftMonster";
			return alias;
		}
		if (alias.equalsIgnoreCase("enderman")
				|| alias.equalsIgnoreCase("eman")
				|| alias.equalsIgnoreCase("endman")
				|| alias.equalsIgnoreCase("endermen")
				|| alias.equalsIgnoreCase("emen")
				|| alias.equalsIgnoreCase("endmen")) {
			alias = "CraftEnderman";
			return alias;
		}
		if (alias.equalsIgnoreCase("bluespider")
			|| alias.equalsIgnoreCase("cavespider")
			|| alias.equalsIgnoreCase("smallspider")
			|| alias.equalsIgnoreCase("bluespiders")
			|| alias.equalsIgnoreCase("cavespiders")
			|| alias.equalsIgnoreCase("smallspiders")) {
			alias = "CraftCaveSpider";
			return alias;
		}
		if (alias.equalsIgnoreCase("silverfish")
				|| alias.equalsIgnoreCase("sliverfish")
				|| alias.equalsIgnoreCase("caterpillar")
				|| alias.equalsIgnoreCase("caterpillars")) {
			alias = "CraftSilverfish";
			return alias;
		}
		
		alias = null;
		return alias;
	}
	
	public void Cr(String[] args, User user) {
		if (args.length == 0) {
			if (user.getHandle().getGameMode() == GameMode.CREATIVE) {
				user.getHandle().setGameMode(GameMode.SURVIVAL);
				user.sendMessage(ChatColor.GREEN + "You are now in survival mode!");
				plugin.log.info(user.getName() + " has left creative mode");
			} else {
				user.getHandle().setGameMode(GameMode.CREATIVE);
				user.sendMessage(ChatColor.GREEN + "You are now in creative mode!");
				plugin.log.info(user.getName() + " has entered creative mode!");
			}
		} else if (args.length == 1) {
			if (!user.hasPerm("bencmd.creative.other")) {
				user.sendMessage(ChatColor.RED + "You don't have permission to do that!");
				plugin.logPermFail();
				return;
			}
			User u = User.matchUser(args[0], plugin);
			if (u == null) {
				user.sendMessage(ChatColor.RED + args[0] + " isn't online right now!");
				return;
			}
			if (u.getHandle().getGameMode() == GameMode.CREATIVE) {
				u.getHandle().setGameMode(GameMode.SURVIVAL);
				u.sendMessage(ChatColor.GREEN + "You are now in survival mode!");
				user.sendMessage(ChatColor.GREEN + "That user is now in survival mode!");
				plugin.log.info(u.getName() + " has left creative mode (" + user.getName() + ")");
			} else {
				u.getHandle().setGameMode(GameMode.CREATIVE);
				u.sendMessage(ChatColor.GREEN + "You are now in creative mode!");
				user.sendMessage(ChatColor.GREEN + "That user is now in creative mode!");
				plugin.log.info(u.getName() + " has entered creative mode (" + user.getName() + ")");
			}
		} else {
			user.sendMessage(ChatColor.YELLOW + "Proper use: /cr [player]");
		}
	}
	
	public List<BCommand> getCommands(User user) {
		List<BCommand> commands = new ArrayList<BCommand>();
		commands.add(new BCommand("/help [page]",
				"Displays the xth page of the command list...", "."));
		commands.add(new BCommand("/slow [delay]",
				"Makes each user wait x seconds between chats.", "bencmd.chat.slow"));
		commands.add(new BCommand("/mute <player>", "Mutes a player.",
				"bencmd.action.mute"));
		commands.add(new BCommand("/unmute <player>", "Unmutes a player.",
				"bencmd.action.unmute"));
		commands.add(new BCommand("/list", "Lists the players online.",
				"bencmd.chat.list"));
		commands.add(new BCommand("/time {day|night|lock}",
				"Sets the time of day or locks the time.", "bencmd.time.set"));
		commands.add(new BCommand("/spawn", "Sends you to the spawn.",
				"bencmd.spawn.normal"));
		commands.add(new BCommand("/spawn <world>", "Sends you to the specified world's spawn.",
				"bencmd.spawn.all"));
		commands.add(new BCommand("/item <ID>[:damage] [amount] [player]",
				"Gives you an item.", "bencmd.inv.spawn"));
		commands.add(new BCommand("/god",
				"Makes you invincible.", "bencmd.god.self"));
		commands.add(new BCommand("/god [player]",
				"Makes another player invincible.", "bencmd.god.other"));
		commands.add(new BCommand("/heal",
				"Gives you full health.", "bencmd.heal.self"));
		commands.add(new BCommand("/heal [player]",
				"Gives another player full health.", "bencmd.heal.other"));
		commands.add(new BCommand("/bencmd reload",
				"Reloads the BenCmd Config", "bencmd.reload"));
		commands.add(new BCommand(
				"/user <name> {add|remove|+/-<permissions>}",
				"Controls user permissions", "bencmd.editpermissions"));
		commands.add(new BCommand(
				"/group <name> {add|remove|g:<group>|<permissions>}",
				"Controls group permissions", "bencmd.editpermissions"));
		commands.add(new BCommand("/warp <warp>",
				"Warps you to a pre-defined point.", "bencmd.warp.self"));
		commands.add(new BCommand("/warp <warp> <player>",
				"Warps another player to a pre-defined point.", "bencmd.warp.other"));
		commands.add(new BCommand("/setwarp <warp>", "Sets a new warp.",
				"bencmd.warp.set"));
		commands.add(new BCommand("/delwarp <warp>", "Deletes a warp.",
				"bencmd.warp.remove"));
		commands.add(new BCommand("/back",
				"Warps you back to before your last warp.", "bencmd.warp.back"));
		commands.add(new BCommand("/home <number>",
				"Teleports to your xth home.", "bencmd.home.self"));
		commands.add(new BCommand("/home <number> <player>",
				"Teleports to another player's xth home.", "bencmd.home.warpall"));
		commands.add(new BCommand("/sethome <number>", "Sets your xth home.",
				"bencmd.home.set"));
		commands.add(new BCommand("/sethome <number> <player>",
				"Sets another player's xth home.", "bencmd.home.setall"));
		commands.add(new BCommand("/delhome <number>",
				"Deletes your xth home.", "bencmd.home.remove"));
		commands.add(new BCommand("/delhome <number> <player>",
				"Deletes another player's xth home.", "bencmd.home.removeall"));
		commands.add(new BCommand("/clearinventory [player]",
				"Clears your own or another player's inventory.",
				"bencmd.inv.clr.self"));
		commands.add(new BCommand("/jail <player> <time>", "Jails a player for the set time.",
				"bencmd.action.jail"));
		commands.add(new BCommand("/unjail <player>", "Unjails a  player.",
				"bnecmd.action.unjail"));
		commands.add(new BCommand("/setjail", "Sets the jail location.",
				"bencmd.action.setjail"));
		commands.add(new BCommand("/unl <ID>[:Damage]",
				"Creates an unlimited dispenser", "bencmd.unlimited.create"));
		commands.add(new BCommand("/disp", "Creates a disposal chest.",
				"bencmd.disposal.create"));
		commands.add(new BCommand("/lot info",
				"Gets information on a lot.", "bencmd.lot.info"));
		commands.add(new BCommand("/lot set/advset",
				"Creates a new lot from your current selection.", "bencmd.lot.create"));
		commands.add(new BCommand("/lot extend/advext",
				"Expands a lot to your current selection.", "bencmd.lot.extend"));
		commands.add(new BCommand("/lot remove [id]",
				"Deletes a lot.", "bencmd.lot.remove"));
		commands.add(new BCommand("/kit <kitname>", "Spawns a kit.",
				"bencmd.inv.kit.spawn"));
		commands.add(new BCommand("/poof", "Makes you invisible.", "bencmd.poof.poof"));
		commands.add(new BCommand("/nopoof",
				"Makes you able to see invisible players.", "bencmd.poof.nopoof"));
		commands.add(new BCommand("/allpoof",
				"Makes you invisible to /nopoof players.", "bencmd.poof.allpoof"));
		commands.add(new BCommand(
				"/protect {add|remove|info|setowner|addguest|remguest}",
				"Deals with protection.", "bencmd.lock.*"));
		commands.add(new BCommand("/lock", "Locks a chest", "bencmd.lock.create"));
		commands.add(new BCommand("/public", "Publicly locks a chest",
				"bencmd.lock.public"));
		commands.add(new BCommand("/unlock", "Unlocks a chest", "bencmd.lock.create"));
		commands.add(new BCommand("/share", "Adds a guest to a chest",
				"bencmd.lock.create"));
		commands.add(new BCommand("/unshare", "Removes a guest from a chest",
				"bencmd.lock.create"));
		commands.add(new BCommand("/setspawn", "Sets the map spawn point.",
				"bencmd.spawn.set"));
		commands.add(new BCommand("/me <emote>",
				"Allows you to emote something", "."));
		commands.add(new BCommand("/tell <player> <message>", "PMs a player.",
				"."));
		commands.add(new BCommand("/storm {off|rain|thunder}",
				"Changes the current map's weather.", "bencmd.storm.change"));
		commands.add(new BCommand("/strike",
				"Strikes lightning where you are pointing.",
				"bnemcd.storm.strike.location"));
		commands.add(new BCommand("/strike <player>",
				"Strikes that player with lightning.",
				"bencmd.storm.strike.player"));
		commands.add(new BCommand("/strike bind",
				"Binds/unbinds right-click striking to the current tool.",
				"bencmd.storm.strike.bind"));
		commands.add(new BCommand("/offline",
				"Makes you appear to be offline.", "bnecmd.poof.offline"));
		commands.add(new BCommand("/online",
				"Makes you re-appear to be online.", "bencmd.poof.offline"));
		commands.add(new BCommand("/report <player> <reason>",
				"Reports a player to the admins.", "bencmd.ticket.set"));
		commands.add(new BCommand(
				"/ticket",
				"Lists and changes existing reports. Type /ticket for more info...",
				"bencmd.ticket.readown"));
		commands.add(new BCommand("/kill <player>", "Kills the player listed.",
				"bemcmd.kill.*"));
		commands.add(new BCommand("/mob <Mob Name>,<Passenger>,.. [Amount]",
				"Spawns a specific amount of a specific mob.", "bencmd.spawnmob"));
		commands.add(new BCommand("/killall <Mob Name> <Mob Name> etc <range>",
				"Kills all specified mobs within the given range.", "bencmd.spawmnmob"));
		commands.add(new BCommand("/buy <Item> [Amount]", "Buys an item.", "."));
		commands.add(new BCommand("/sell <Item> [Amount]",
				"Sells an item from your inventory.", "."));
		commands.add(new BCommand("/price <Item>",
				"Lists the price of a specific item.", "."));
		commands.add(new BCommand("/market",
				"Used to administrate the economic functions of BenCmd.",
				"bencmd.market.*"));
		commands.add(new BCommand("/tp <player>",
				"Teleports you to another player.", "bencmd.tp.self"));
		commands.add(new BCommand("/tp <plater> [player]",
				"Teleports one player to another.", "bencmd.tp.other"));
		commands.add(new BCommand("/tphere <player>",
				"Teleports a player to you.", "bnecmd.tp.other"));

		for (int i = 0; i < commands.size(); i++) {
			if (!commands.get(i).canUse(user)) {
				commands.remove(i);
			}
		}
		return commands;
	}

}
