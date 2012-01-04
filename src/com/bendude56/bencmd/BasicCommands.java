package com.bendude56.bencmd;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Giant;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowman;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Squid;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.plugin.PluginDescriptionFile;

public class BasicCommands implements Commands {

	private static final long	TIME_SUNRISE	= 22500;
	private static final long	TIME_DAWN		= 23000;
	private static final long	TIME_DAY		= 0;
	private static final long	TIME_NOON		= 6000;
	private static final long	TIME_SUNSET		= 12000;
	private static final long	TIME_DUSK		= 13000;
	private static final long	TIME_NIGHT		= 15000;
	private static final long	TIME_MIDNIGHT	= 18000;

	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		User user;
		try {
			user = User.getUser((Player) sender);
		} catch (ClassCastException e) {
			user = User.getUser();
		}
		if (commandLabel.equalsIgnoreCase("time") && (user.hasPerm("bencmd.time.set") || user.hasPerm("bencmd.time.lock"))) {
			Time(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("day")) {
			Bukkit.dispatchCommand(sender, "time day");
			return true;
		} else if (commandLabel.equalsIgnoreCase("dawn")) {
			Bukkit.dispatchCommand(sender, "time dawn");
			return true;
		} else if (commandLabel.equalsIgnoreCase("noon")) {
			Bukkit.dispatchCommand(sender, "time noon");
			return true;
		} else if (commandLabel.equalsIgnoreCase("dusk")) {
			Bukkit.dispatchCommand(sender, "time dusk");
			return true;
		} else if (commandLabel.equalsIgnoreCase("sunrise")) {
			Bukkit.dispatchCommand(sender, "time sunrise");
			return true;
		} else if (commandLabel.equalsIgnoreCase("sunset")) {
			Bukkit.dispatchCommand(sender, "time sunset");
			return true;
		} else if (commandLabel.equalsIgnoreCase("night")) {
			Bukkit.dispatchCommand(sender, "time night");
			return true;
		} else if (commandLabel.equalsIgnoreCase("midnight")) {
			Bukkit.dispatchCommand(sender, "time midnight");
			return true;
		} else if (commandLabel.equalsIgnoreCase("ping")) {
			if (user.isServer()) {
				if (args.length == 0) {
					BenCmd.log("pong");
				} else {
					BenCmd.log(args[0]);
				}
			} else {
				user.sendMessage(ChatColor.RED + "No, you cannot spam the server console, smart one!");
			}
			return true;
		} else if (commandLabel.equalsIgnoreCase("spawn") && user.hasPerm("bencmd.spawn.normal")) {
			Spawn(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("god") && user.hasPerm("bencmd.god.self")) {
			God(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("heal") && user.hasPerm("bencmd.heal.self")) {
			Heal(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("feed") && user.hasPerm("bencmd.feed.self")) {
			Feed(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("level") && user.hasPerm("bencmd.level.self")) {
			Level(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("bencmd")) {
			BenCmd(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("setspawn") && user.hasPerm("bencmd.spawn.set")) {
			SetSpawn(user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("help")) {
			Help(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("kill") && user.hasPerm("bencmd.kill.self")) {
			Kill(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("spawnmob") && user.hasPerm("bencmd.spawnmob")) {
			SpawnMob(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("mob") && user.hasPerm("bencmd.spawnmob")) {
			SpawnMob(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("spawner") && user.hasPerm("bencmd.spawnmob")) {
			Spawner(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("killall") && user.hasPerm("bencmd.spawnmob")) {
			KillEntities(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("rechunk")) {
			if (user.isServer()) {
				user.sendMessage(ChatColor.RED + "The server cannot do that!");
				return true;
			}
			Chunk chunk = ((Player) user.getHandle()).getWorld().getChunkAt(((Player) user.getHandle()).getLocation());
			int chunkx = chunk.getX();
			int chunkz = chunk.getZ();
			((Player) user.getHandle()).getWorld().unloadChunk(chunkx, chunkz);
			((Player) user.getHandle()).getWorld().loadChunk(chunkx, chunkz);
			((Player) user.getHandle()).getWorld().refreshChunk(chunkx, chunkz);
			return true;
		} else if (commandLabel.equalsIgnoreCase("fire") && user.hasPerm("bencmd.fire.spread")) {
			if (user.isServer()) {
				user.sendMessage(ChatColor.RED + "The server cannot do that!");
				return true;
			}
			Location loc = ((Player) user.getHandle()).getTargetBlock(null, 4).getLocation();
			user.sendMessage(ChatColor.GREEN + "Fire next to that block can now spread...");
			BenCmd.getPlugin().canSpread.add(loc);
			return true;
		} else if (commandLabel.equalsIgnoreCase("nofire") && user.hasPerm("bencmd.fire.spread")) {
			if (user.isServer()) {
				user.sendMessage(ChatColor.RED + "The server cannot do that!");
				return true;
			}
			BenCmd.getPlugin().canSpread.clear();
			user.sendMessage(ChatColor.GREEN + "All area-specific fire-spread is now disabled.");
			return true;
		} else if (commandLabel.equalsIgnoreCase("mainprop") && user.hasPerm("bencmd.mainedit")) {
			if (args.length == 0) {
				user.sendMessage(ChatColor.YELLOW + "Proper use is: /mainprop <property> [value]");
			} else if (args.length == 1) {
				if (BenCmd.getMainProperties().containsKey(args[0])) {
					user.sendMessage(ChatColor.YELLOW + "That property is currently set to:");
					user.sendMessage(BenCmd.getMainProperties().getProperty(args[0]));
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
				BenCmd.getMainProperties().setProperty(args[0], val);
				BenCmd.getMainProperties().saveFile("");
				user.sendMessage(ChatColor.GREEN + "Success!");
			}
			return true;
		} else if (commandLabel.equalsIgnoreCase("cr") && user.hasPerm("bencmd.creative.self")) {
			Cr(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("debug")) {
			if (!user.isDev() && !user.isServer()) {
				user.sendMessage(ChatColor.RED + "That command is reserved for BenCmd developers only!");
				BenCmd.getPlugin().logPermFail();
				return true;
			}
			if (args.length == 0 || args[0].equalsIgnoreCase("ver")) {
				user.sendMessage(ChatColor.GRAY + "This server is running BenCmd Build " + BenCmd.buildId + ((BenCmd.debug) ? " (DEBUG)" : ""));
				user.sendMessage(ChatColor.GRAY + "Supported CraftBukkit version: " + BenCmd.cbbuild);
				int cb;
				try {
					cb = Integer.parseInt(Bukkit.getVersion().split("-")[4].split(" ")[0].replace("b", "").replace("jnks", ""));
				} catch (NumberFormatException e) {
					cb = -1;
				}
				user.sendMessage(ChatColor.GRAY + "Running CraftBukkit version: " + ((cb == -1) ? "UNKNOWN" : cb));
			} else if (args[0].equalsIgnoreCase("pfail")) {
				user.sendMessage(ChatColor.GRAY + "Permission failures: " + BenCmd.getUsageFile().getInteger("permFail", 0));
			} else if (args[0].equalsIgnoreCase("chkconfig")) {
				user.sendMessage(ChatColor.GRAY + "Anonymous usage logging: " + ((BenCmd.getMainProperties().getBoolean("anonUsageStats", true)) ? (ChatColor.GREEN + "ON") : (ChatColor.YELLOW + "OFF")));
				user.sendMessage(ChatColor.GRAY + "Update mode: " + ((BenCmd.getMainProperties().getBoolean("downloadDevUpdates", true)) ? (ChatColor.YELLOW + "ALL") : (ChatColor.GREEN + "STABLE ONLY")));
				String dGroup = BenCmd.getMainProperties().getString("defaultGroup", "default");
				user.sendMessage(ChatColor.GRAY + "Default group: " + ((BenCmd.getPermissionManager().getGroupFile().groupExists(dGroup)) ? ChatColor.GREEN : ChatColor.RED) + dGroup);
				user.sendMessage(ChatColor.GRAY + "Spout connected: " + ((BenCmd.isSpoutConnected()) ? (ChatColor.GREEN + "YES") : (ChatColor.YELLOW + "NO")));
				if (BenCmd.getMainProperties().getBoolean("channelsEnabled", true)) {
					user.sendMessage(ChatColor.GRAY + "General channel: " + ((BenCmd.getChatChannels().getChannel("General") != null) ? (ChatColor.GREEN + "Correctly configured") : (ChatColor.RED + "Doesn't exist")));
				} else {
					user.sendMessage(ChatColor.GRAY + "General channel: " + ChatColor.RED + "Channels disabled");
				}
				/*if (BenCmd.getMainServer() != null) {
					user.sendMessage(ChatColor.GRAY + "Main server connection: " + ChatColor.GREEN + "Connected");
				} else if (BenCmd.getMainProperties().getBoolean("connectMainServer", true)) {
					user.sendMessage(ChatColor.GRAY + "Main server connection: " + ChatColor.YELLOW + "Disabled");
				} else {
					user.sendMessage(ChatColor.GRAY + "Main server connection: " + ChatColor.RED + "Not connected");
				}*/
			} else {
				user.sendMessage(ChatColor.GRAY + "Invalid debug command");
			}
			return true;
		}
		return false;
	}

	public void Kill(String[] args, User user) {
		if (args.length == 0) {
			if (!user.kill()) {
				user.sendMessage(ChatColor.RED + "You can't kill yourself while you're godded!");
			}
		} else if (args.length == 1) {
			if (!user.hasPerm("bencmd.kill.other")) {
				user.sendMessage(ChatColor.RED + "You don't have permission to do that!");
				BenCmd.getPlugin().logPermFail();
				return;
			}
			User user2;
			if ((user2 = User.matchUser(args[0])) != null) {
				if (user2.hasPerm("bencmd.kill.protect") && !user.hasPerm("bencmd.kill.all")) {
					user.sendMessage(ChatColor.RED + "That player is protected from being killed!");
					return;
				}
				if (!user2.kill()) {
					user.sendMessage(ChatColor.RED + "You can't kill someone while they're godded!");
				}
			} else {
				user.sendMessage(ChatColor.RED + "That user doesn't exist!");
			}
		}
	}

	public void Time(String[] args, User user) {
		if (args.length == 0) {
			user.sendMessage(ChatColor.YELLOW + "Proper use is /time {day|night|set|lock} [time]");
		} else {
			World w = null;
			if ((args[0].equals("set") && args.length == 3) || (!args[0].equals("set") && args.length == 2)) {
				w = Bukkit.getWorld((args[0].equals("set")) ? args[2] : args[1]);
				if (w == null) {
					user.sendMessage(ChatColor.RED + "There is no world by the name of '" + ((args[0].equals("set")) ? args[2] : args[1]) + "'!");
					return;
				}
			}
			if (args[0].equalsIgnoreCase("day")) {
				Bukkit.dispatchCommand(user.getHandle(), "time set " + TIME_DAY + ((w == null) ? "" : " " + w.getName()));
			} else if (args[0].equalsIgnoreCase("night")) {
				Bukkit.dispatchCommand(user.getHandle(), "time set " + TIME_NIGHT + ((w == null) ? "" : " " + w.getName()));
			} else if (args[0].equalsIgnoreCase("set")) {
				if (!user.hasPerm("bencmd.time.set")) {
					user.sendMessage(ChatColor.RED + "You don't have permission to do that!");
					BenCmd.getPlugin().logPermFail();
					return;
				}
				int time;
				try {
					time = Integer.parseInt(args[1]);
				} catch (NumberFormatException e) {
					user.sendMessage(ChatColor.RED + "Invalid time.");
					return;
				}
				if (user.isServer() && w == null) {
					for (World world : Bukkit.getWorlds()) {
						world.setTime(time);
						BenCmd.getTimeManager().syncLastTime(world);
					}
				} else if (w == null) {
					((Player) user.getHandle()).getWorld().setTime(time);
					BenCmd.getTimeManager().syncLastTime(((Player) user.getHandle()).getWorld());
				} else {
					w.setTime(time);
					BenCmd.getTimeManager().syncLastTime(w);
				}
			} else if (args[0].equalsIgnoreCase("dawn")) {
				Bukkit.dispatchCommand(user.getHandle(), "time set " + TIME_DAWN + ((w == null) ? "" : " " + w.getName()));
			} else if (args[0].equalsIgnoreCase("sunrise")) {
				Bukkit.dispatchCommand(user.getHandle(), "time set " + TIME_SUNRISE + ((w == null) ? "" : " " + w.getName()));
			} else if (args[0].equalsIgnoreCase("noon")) {
				Bukkit.dispatchCommand(user.getHandle(), "time set " + TIME_NOON + ((w == null) ? "" : " " + w.getName()));
			} else if (args[0].equalsIgnoreCase("dusk")) {
				Bukkit.dispatchCommand(user.getHandle(), "time set " + TIME_DUSK + ((w == null) ? "" : " " + w.getName()));
			} else if (args[0].equalsIgnoreCase("sunset")) {
				Bukkit.dispatchCommand(user.getHandle(), "time set " + TIME_SUNSET + ((w == null) ? "" : " " + w.getName()));
			} else if (args[0].equalsIgnoreCase("midnight")) {
				Bukkit.dispatchCommand(user.getHandle(), "time set " + TIME_MIDNIGHT + ((w == null) ? "" : " " + w.getName()));
			} else if (args[0].equalsIgnoreCase("lock")) {
				if (!user.hasPerm("bencmd.time.lock")) {
					user.sendMessage(ChatColor.RED + "You don't have permission to do that!");
					BenCmd.getPlugin().logPermFail();
					return;
				}
				if (user.isServer() && w == null) {
					for (World world : Bukkit.getWorlds()) {
						BenCmd.getTimeManager().setFrozen(world, true);
					}
					Bukkit.broadcastMessage(ChatColor.BLUE + "Server has frozen time!");
				} else if (w == null) {
					w = ((Player) user.getHandle()).getWorld();
					BenCmd.getTimeManager().setFrozen(w, true);
					for (Player p : w.getPlayers()) {
						p.sendMessage(ChatColor.BLUE + user.getDisplayName() + " has frozen time!");
					}
				} else {
					BenCmd.getTimeManager().setFrozen(w, true);
					for (Player p : w.getPlayers()) {
						p.sendMessage(ChatColor.BLUE + user.getDisplayName() + " has frozen time!");
					}
				}
			} else if (args[0].equalsIgnoreCase("unlock")) {
				if (!user.hasPerm("bencmd.time.lock")) {
					user.sendMessage(ChatColor.RED + "You don't have permission to do that!");
					BenCmd.getPlugin().logPermFail();
					return;
				}
				if (user.isServer() && w == null) {
					for (World world : Bukkit.getWorlds()) {
						BenCmd.getTimeManager().setFrozen(world, false);
					}
					Bukkit.broadcastMessage(ChatColor.BLUE + "Server has unfrozen time!");
				} else if (w == null) {
					w = ((Player) user.getHandle()).getWorld();
					BenCmd.getTimeManager().setFrozen(w, false);
					for (Player p : w.getPlayers()) {
						p.sendMessage(ChatColor.BLUE + user.getDisplayName() + " has unfrozen time!");
					}
				} else {
					BenCmd.getTimeManager().setFrozen(w, false);
					for (Player p : w.getPlayers()) {
						p.sendMessage(ChatColor.BLUE + user.getDisplayName() + " has unfrozen time!");
					}
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
			user.spawn(spawnworld);
		} else {
			user.spawn();
		}
	}

	public void God(String[] args, User user) {
		if (args.length == 0) {
			if (user.isServer()) {
				user.sendMessage(ChatColor.YELLOW + "Proper use is /god [player]");
				return;
			}
			if (user.isGod()) { // If they're a god
				user.makeNonGod(); // Delete them from the list
				user.sendMessage(ChatColor.GOLD + "You are no longer in god mode!");
				BenCmd.log("BenCmd: " + user.getDisplayName() + " has been made a non-god by " + user.getDisplayName() + "!");
			} else { // If they're not a god
				user.makeGod(); // Add them to the list
				user.sendMessage(ChatColor.GOLD + "You are now in god mode!");
				BenCmd.log("BenCmd: " + user.getDisplayName() + " has been made a god by " + user.getDisplayName() + "!");
			}
		} else if (args.length == 1) {
			if (!user.hasPerm("bencmd.god.other")) {
				user.sendMessage(ChatColor.RED + "You don't have permission to do that!");
				BenCmd.getPlugin().logPermFail();
				return;
			}
			User user2;
			if ((user2 = User.matchUser(args[0])) != null) { // If
																// they
																// exist
				if (user2.hasPerm("bencmd.god.protect") && !user.hasPerm("bencmd.god.all")) {
					user.sendMessage(ChatColor.RED + "That player is protected from being godded/ungodded by others!");
					return;
				}
				if (user2.isGod()) { // If they're a god
					user2.makeNonGod(); // Delete them from the
										// list
					user2.sendMessage(ChatColor.GOLD + "You are no longer in god mode!");
					user.sendMessage(ChatColor.GOLD + "You have un-godded " + user2.getColor() + user2.getDisplayName());
					BenCmd.log("BenCmd: " + user2.getDisplayName() + " has been made a non-god by " + user.getDisplayName() + "!");
				} else { // If they're not a god
					user2.makeGod(); // Add them to the list
					user2.sendMessage(ChatColor.GOLD + "You are now in god mode!");
					user.sendMessage(ChatColor.GOLD + "You have godded " + user2.getColor() + user2.getDisplayName());
					BenCmd.log("BenCmd: " + user2.getDisplayName() + " has been made a god by " + user.getDisplayName() + "!");
				}
			}
		} else {
			user.sendMessage(ChatColor.YELLOW + "Proper use is /god [player]");
		}
	}

	public void Heal(String[] args, User user) {
		if (args.length == 0) {
			if (user.isServer()) {
				user.sendMessage(ChatColor.YELLOW + "Proper use is /heal [player]");
				return;
			}
			// Heal the player
			user.heal();
			user.sendMessage(ChatColor.GREEN + "You have been healed.");
			BenCmd.log("BenCmd: " + user.getDisplayName() + " has been healed by " + user.getDisplayName());
		} else {
			if (!user.hasPerm("bencmd.heal.other") && !(args[0].equalsIgnoreCase(user.getDisplayName()) && user.hasPerm("bencmd.heal.self"))) {
				user.sendMessage(ChatColor.RED + "You don't have permission to do that!");
				BenCmd.getPlugin().logPermFail();
				return;
			}
			// Heal the other player
			User user2;
			if ((user2 = User.matchUser(args[0])) != null) {
				user2 = User.matchUser(args[0]);
				user2.heal();
				user2.sendMessage(ChatColor.GREEN + "You have been healed.");
				user.sendMessage(ChatColor.GREEN + "You have healed " + user2.getColor() + user2.getDisplayName());
				BenCmd.log("BenCmd: " + user2.getDisplayName() + " has been healed by " + user.getDisplayName());
			} else {
				user.sendMessage(ChatColor.RED + args[0] + " doesn't exist or is not online.");
			}
		}
	}

	public void Feed(String[] args, User user) {
		if (args.length == 0) {
			if (user.isServer()) {
				user.sendMessage(ChatColor.YELLOW + "Proper use is /feed [player]");
				return;
			}
			// Feed the player
			user.feed();
			user.sendMessage(ChatColor.GREEN + "You have been fed.");
			BenCmd.log("BenCmd: " + user.getDisplayName() + " has been fed by " + user.getDisplayName());
		} else {
			if (!user.hasPerm("bencmd.feed.other") && !(args[0].equalsIgnoreCase(user.getDisplayName()) && user.hasPerm("bencmd.feed.self"))) {
				user.sendMessage(ChatColor.RED + "You don't have permission to do that!");
				BenCmd.getPlugin().logPermFail();
				return;
			}
			// Feed the other player
			User user2;
			if ((user2 = User.matchUser(args[0])) != null) {
				user2 = User.matchUserIgnoreCase(args[0]);
				user2.feed();
				user2.sendMessage(ChatColor.GREEN + "You have been fed.");
				user.sendMessage(ChatColor.GREEN + "You have fed" + user2.getColor() + user2.getDisplayName());
				BenCmd.log("BenCmd: " + user2.getDisplayName() + " has been healed by " + user.getDisplayName());
			} else {
				user.sendMessage(ChatColor.RED + args[0] + " doens't exist or is not online.");
			}
		}
	}
	
	public void Level(String[] args, User user) {
		if (args.length == 0) {
			user.sendMessage(ChatColor.YELLOW + "Proper use is /level <integer> [player]");
			return;
		}
		if (args.length == 1) {
			if (user.isServer()) {
				user.sendMessage("You cannot set the server's experience level!");
				return;
			}
			try {
				((Player) user.getHandle()).setTotalExperience(Integer.parseInt(args[0]));
				user.sendMessage(ChatColor.GREEN + "Your experience level has been set to " + args[0]);
				return;
			} catch (NumberFormatException e) {
				user.sendMessage(ChatColor.RED + args[0] + " is not an integer!");
				return;
			}
		} else if (user.hasPerm("bencmd.experience.other")) {
			if (Bukkit.getPlayer(args[1]) == null) {
				user.sendMessage(ChatColor.RED + args[1] + " does not exist!");
				return;
			}
			try {
				Bukkit.getPlayerExact(args[1]).setTotalExperience(Integer.parseInt(args[0]));
				user.sendMessage(ChatColor.GREEN + args[1] + "'s experience has been set to " + args[1]);
				return;
			} catch (NumberFormatException e) {
				user.sendMessage(ChatColor.RED + args[0] + " is not an integer!");
				return;
			}
		} else {
			user.sendMessage(ChatColor.RED + "You don't have permission to do that!");
			return;
		}
	}

	public void BenCmd(String[] args, User user) {
		if (args.length == 0 || args[0].equalsIgnoreCase("version")) {
			PluginDescriptionFile pdfFile = BenCmd.getPlugin().getDescription();
			user.sendMessage(ChatColor.YELLOW + "This server is running " + pdfFile.getName() + " version " + pdfFile.getVersion() + ".");
			return;
		}
		if ((args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rel")) && user.hasPerm("bencmd.reload")) {
			BenCmd.unloadAll(true);
			BenCmd.loadAll();
			user.sendMessage(ChatColor.GREEN + "BenCmd Config Successfully reloaded!");
			BenCmd.log(Level.WARNING, user.getDisplayName() + " has reloaded the BenCmd configuration.");
		} else if (args[0].equalsIgnoreCase("update") && user.hasPerm("bencmd.update")) {
			if (!BenCmd.getPlugin().update(false)) {
				user.sendMessage(ChatColor.RED + "BenCmd is up to date... Use /bencmd fupdate to force an update...");
			}
		} else if (args[0].equalsIgnoreCase("fupdate") && user.hasPerm("bencmd.update")) {
			BenCmd.getPlugin().update(true);
		} else if (args[0].equalsIgnoreCase("disable") && user.hasPerm("bencmd.disable")) {
			Bukkit.broadcastMessage(ChatColor.RED + "BenCmd is being temporarily disabled for maintenance...");
			Bukkit.broadcastMessage(ChatColor.RED + "Some commands may cease to function until it is restarted...");
			Bukkit.getPluginManager().disablePlugin(BenCmd.getPlugin());
		}
	}

	public void SetSpawn(User user) {
		if (user.isServer()) {
			user.sendMessage(ChatColor.RED + "The server cannot do that!");
			return;
		}
		Location newSpawn = ((Player) user.getHandle()).getLocation();
		user.sendMessage(ChatColor.GREEN + "The spawn location has been set!");
		BenCmd.log(user.getDisplayName() + " has set the spawn location to (" + newSpawn.getBlockX() + ", " + newSpawn.getBlockY() + ", " + newSpawn.getBlockZ() + ")");
		((Player) user.getHandle()).getWorld().setSpawnLocation(newSpawn.getBlockX(), newSpawn.getBlockY(), newSpawn.getBlockZ());
	}

	public void Help(String[] args, User user) {
		int pageToShow;
		if (args.length == 0) {
			pageToShow = 1;
		} else {
			try {
				pageToShow = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				user.sendMessage(ChatColor.RED + args[0] + " is an invalid page number!");
				return;
			}
		}
		List<BCommand> commands = getCommands(user);
		int max;
		if (pageToShow > (max = (int) Math.ceil((commands.size() - 1) / 6) + 1)) {
			user.sendMessage(ChatColor.RED + "There are only " + max + " pages!");
			return;
		} else if (pageToShow <= 0) {
			user.sendMessage(ChatColor.RED + "There are no negative pages.");
			return;
		}
		int i = (pageToShow - 1) * 6;
		user.sendMessage(ChatColor.YELLOW + "Displaying help page " + ChatColor.RED + pageToShow + ChatColor.YELLOW + " of " + ChatColor.RED + max + ChatColor.YELLOW + ":");
		while (i < (pageToShow - 1) * 6 + 6) {
			if (i >= commands.size()) {
				break;
			}
			user.sendMessage(ChatColor.GREEN + commands.get(i).getName() + ChatColor.WHITE + " - " + ChatColor.GRAY + commands.get(i).getDescription());
			i++;
		}
	}

	public void SpawnMob(String[] args, User user) {
		if (user.isServer()) {
			user.sendMessage(ChatColor.RED + "The server cannot do that!");
			return;
		}
		if (args.length != 1 && args.length != 2) {
			user.sendMessage(ChatColor.YELLOW + "Proper use is /spawnmob <Mob Name> [Amount]");
			return;
		}
		int amount = 1;
		if (args.length == 2) {
			try {
				amount = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				user.sendMessage(ChatColor.RED + args[1] + " cannot be converted into a number!");
				return;
			}
		}

		int mobCounter = 0;

		// Prepare for passengers
		String[] passengers = args[0].split(",");
		LivingEntity vehicle = null, passenger, mob;
		CreatureType mobType;

		// Spawn the mob(s)
		for (int i = 0; i < amount; i++) {
			vehicle = null;
			for (int p = 0; p < passengers.length; p++) {
				mobType = getMobType(passengers[p].split(":")[0]);
				
				if (mobType != null) {
					mob = ((Player) user.getHandle()).getWorld().spawnCreature(((Player) user.getHandle()).getLocation(), mobType);
					if (mobType == CreatureType.CREEPER) {
						if (passengers[p].equalsIgnoreCase("supercreeper") || passengers[p].equalsIgnoreCase("chargedcreeper")) {
							((Creeper) mob).setPowered(true);
						}
					} else if (mobType == CreatureType.SLIME) {
						if (passengers[p].split(":").length > 1) {
							try {
								((Slime) mob).setSize(Integer.parseInt(passengers[p].split(":")[1]));
							} catch (NumberFormatException e) { }
						}
					} else if (mobType == CreatureType.MAGMA_CUBE) {
						if (passengers[p].split(":").length > 1) {
							try {
								((MagmaCube) mob).setSize(Integer.parseInt(passengers[p].split(":")[1]));
							} catch (NumberFormatException e) { }
						}
					} else if (mobType == CreatureType.SHEEP) {
						if (passengers[p].split(":").length > 1) {
							if (passengers[p].split(":")[1].equalsIgnoreCase("red")) {
								((Sheep) mob).setColor(DyeColor.RED);
							} else if (passengers[p].split(":")[1].equalsIgnoreCase("pink")) {
								((Sheep) mob).setColor(DyeColor.PINK);
							} else if (passengers[p].split(":")[1].equalsIgnoreCase("orange")) {
								((Sheep) mob).setColor(DyeColor.ORANGE);
							} else if (passengers[p].split(":")[1].equalsIgnoreCase("yellow")) {
								((Sheep) mob).setColor(DyeColor.YELLOW);
							} else if (passengers[p].split(":")[1].equalsIgnoreCase("lime") || passengers[p].split(":")[1].equalsIgnoreCase("lightgreen")) {
								((Sheep) mob).setColor(DyeColor.LIME);
							} else if (passengers[p].split(":")[1].equalsIgnoreCase("green") || passengers[p].split(":")[1].equalsIgnoreCase("darkgreen")) {
								((Sheep) mob).setColor(DyeColor.GREEN);
							} else if (passengers[p].split(":")[1].equalsIgnoreCase("aqua") || passengers[p].split(":")[1].equalsIgnoreCase("cyan")) {
								((Sheep) mob).setColor(DyeColor.CYAN);
							} else if (passengers[p].split(":")[1].equalsIgnoreCase("lightblue")) {
								((Sheep) mob).setColor(DyeColor.LIGHT_BLUE);
							} else if (passengers[p].split(":")[1].equalsIgnoreCase("blue")) {
								((Sheep) mob).setColor(DyeColor.BLUE);
							} else if (passengers[p].split(":")[1].equalsIgnoreCase("yellow")) {
								((Sheep) mob).setColor(DyeColor.YELLOW);
							} else if (passengers[p].split(":")[1].equalsIgnoreCase("purple")) {
								((Sheep) mob).setColor(DyeColor.PURPLE);
							} else if (passengers[p].split(":")[1].equalsIgnoreCase("magenta")) {
								((Sheep) mob).setColor(DyeColor.MAGENTA);
							} else if (passengers[p].split(":")[1].equalsIgnoreCase("brown")) {
								((Sheep) mob).setColor(DyeColor.BROWN);
							} else if (passengers[p].split(":")[1].equalsIgnoreCase("black")) {
								((Sheep) mob).setColor(DyeColor.BLACK);
							} else if (passengers[p].split(":")[1].equalsIgnoreCase("grey") || passengers[p].split(":")[1].equalsIgnoreCase("gray")) {
								((Sheep) mob).setColor(DyeColor.GRAY);
							} else if (passengers[p].split(":")[1].equalsIgnoreCase("lightgray") || passengers[p].split(":")[1].equalsIgnoreCase("lightgrey")) {
								((Sheep) mob).setColor(DyeColor.SILVER);
							} else if (passengers[p].split(":")[1].equalsIgnoreCase("white")) {
								((Sheep) mob).setColor(DyeColor.WHITE);
							}
						}
					}
					
					
					// Add up the passengers
					if (vehicle == null && mob != null) {
						vehicle = mob;
					} else if (vehicle != null) {
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

	public void Spawner(String args[], User user) {
		if (user.isServer()) {
			user.sendMessage(ChatColor.RED + "The server cannot do that!");
			return;
		}
		Player player = ((Player) user.getHandle());
		if (player.getTargetBlock(null, 4).getType() == Material.MOB_SPAWNER) {
			if (args.length!= 1) {
				user.sendMessage(ChatColor.RED + "Proper use is /spawner <creature>");
				return;
			}
			CreatureType mob = getMobType(args[0]);
			if (mob == null) {
				user.sendMessage(ChatColor.RED + "Invalid mob type!");
				return;
			}
			((CreatureSpawner) player.getTargetBlock(null, 4)).setCreatureType(mob);
			user.sendMessage(ChatColor.GREEN + "This spawner now spawns " + mob.name() + "s.");
			return; 
		} else {
			user.sendMessage(ChatColor.RED + "That is not a spawner! Make sure nothing is in the way!");
			return;
		}
	}

	public void KillEntities(String args[], User user) {
		if (user.isServer()) {
			user.sendMessage(ChatColor.RED + "The server cannot do that!");
			return;
		}
		// Tally up the mobs
		int mobCounter = 0;
		int range = -1;
		try {
			range = Integer.parseInt(args[args.length - 1]);
		} catch (NumberFormatException e) {
			range = -1;
		}
		for (int i = 0; i < args.length; i++) {
			CreatureType mobToKill = getMobType(args[i]);
			if (mobToKill != null) {
				for (int ii = 0; ii < ((Player) user.getHandle()).getWorld().getLivingEntities().size(); ii++) {
					LivingEntity entity = ((Player) user.getHandle()).getWorld().getLivingEntities().get(ii);
					{
						if (	   (entity instanceof Creeper && mobToKill == CreatureType.CREEPER)
								|| (entity instanceof Zombie && mobToKill == CreatureType.ZOMBIE)
								|| (entity instanceof Skeleton && mobToKill == CreatureType.SKELETON)
								|| (entity instanceof Spider && mobToKill == CreatureType.SPIDER)
								|| (entity instanceof Slime && mobToKill == CreatureType.SLIME)
								|| (entity instanceof CaveSpider && mobToKill == CreatureType.CAVE_SPIDER)
								|| (entity instanceof Enderman && mobToKill == CreatureType.ENDERMAN)
								|| (entity instanceof Silverfish && mobToKill == CreatureType.SILVERFISH)
								|| (entity instanceof PigZombie && mobToKill == CreatureType.PIG_ZOMBIE)
								|| (entity instanceof Ghast && mobToKill == CreatureType.GHAST)
								|| (entity instanceof MagmaCube && mobToKill == CreatureType.MAGMA_CUBE)
								|| (entity instanceof Blaze && mobToKill == CreatureType.BLAZE)
								|| (entity instanceof Pig && mobToKill == CreatureType.PIG)
								|| (entity instanceof Cow && mobToKill == CreatureType.COW)
								|| (entity instanceof Chicken && mobToKill == CreatureType.CHICKEN)
								|| (entity instanceof MushroomCow && mobToKill == CreatureType.MUSHROOM_COW)
								|| (entity instanceof Squid && mobToKill == CreatureType.SQUID)
								|| (entity instanceof Villager && mobToKill == CreatureType.VILLAGER)
								|| (entity instanceof Wolf && mobToKill == CreatureType.WOLF)
								|| (entity instanceof Snowman && mobToKill == CreatureType.SNOWMAN)
								|| (entity instanceof EnderDragon && mobToKill == CreatureType.ENDER_DRAGON)
								|| (entity instanceof Giant && mobToKill == CreatureType.GIANT)) {
							if (range == -1 || getDistance(((Player) user.getHandle()).getLocation(), ((Player) user.getHandle()).getWorld().getLivingEntities().get(ii).getLocation(), false) <= range) {
								((Player) user.getHandle()).getWorld().getEntities().get(ii).remove();
								mobCounter++;
							}
						}
					}
				}
			}
		}
		if (mobCounter == 0) {
			user.sendMessage(ChatColor.RED + "No mobs were killed.");
		} else {
			user.sendMessage(ChatColor.GREEN + "" + mobCounter + " mobs were killed!");
		}
	}

	private double getDistance(Location loc1, Location loc2, boolean checkY) {

		double xdis = 0, ydis = 0, zdis = 0;
		double distance = 0;

		xdis = loc1.getX() - loc2.getX();
		ydis = loc1.getY() - loc2.getY();
		zdis = loc1.getZ() - loc2.getZ();

		if (checkY) {
			distance = Math.sqrt(Math.pow(xdis, 2) + Math.pow(ydis, 2) + Math.pow(zdis, 2));
		} else {
			distance = Math.sqrt(Math.pow(xdis, 2) + Math.pow(zdis, 2));
		}
		return distance;
	}

	private CreatureType getMobType(String alias) {
		if (alias.equalsIgnoreCase("creeper") || alias.equalsIgnoreCase("creepers")
				|| alias.equalsIgnoreCase("supercreeper") || alias.equalsIgnoreCase("chargedcreeper")) {
			return CreatureType.CREEPER;
		} else if (alias.equalsIgnoreCase("zombie") || alias.equalsIgnoreCase("zombies")) {
			return CreatureType.ZOMBIE;
		} else if (alias.equalsIgnoreCase("skeleton") || alias.equalsIgnoreCase("skele") || alias.equalsIgnoreCase("skeletons") || alias.equalsIgnoreCase("skeles")) {
			return CreatureType.SKELETON;
		} else if (alias.equalsIgnoreCase("spider") || alias.equalsIgnoreCase("spiders")) {
			return CreatureType.SPIDER;
		} else if (alias.equalsIgnoreCase("pig") || alias.equalsIgnoreCase("piggy") || alias.equalsIgnoreCase("pigs") || alias.equalsIgnoreCase("babypig") || alias.equalsIgnoreCase("piglette")) {
			return CreatureType.PIG;
		} else if (alias.equalsIgnoreCase("chicken") || alias.equalsIgnoreCase("chickens") || alias.equalsIgnoreCase("babychicken") || alias.equalsIgnoreCase("chick")) {
			return CreatureType.CHICKEN;
		} else if (alias.equalsIgnoreCase("cow") || alias.equalsIgnoreCase("cows") || alias.equalsIgnoreCase("calf") || alias.equalsIgnoreCase("babycow")) {
			return CreatureType.COW;
		} else if (alias.equalsIgnoreCase("sheep") || alias.equalsIgnoreCase("sheeps") || alias.equalsIgnoreCase("babysheep") || alias.equalsIgnoreCase("lamb")) {
			return CreatureType.SHEEP;
		} else if (alias.equalsIgnoreCase("wolf") || alias.equalsIgnoreCase("wolves")) {
			return CreatureType.WOLF;
		} else if (alias.equalsIgnoreCase("squid") || alias.equalsIgnoreCase("squids")) {
			return CreatureType.SQUID;
		} else if (alias.equalsIgnoreCase("slime") || alias.equalsIgnoreCase("slimes")) {
			return CreatureType.SLIME;
		} else if (alias.equalsIgnoreCase("ghast") || alias.equalsIgnoreCase("ghasts")) {
			return CreatureType.GHAST;
		} else if (alias.equalsIgnoreCase("pigzombie") || alias.equalsIgnoreCase("zombiepigman") || alias.equalsIgnoreCase("zombiepig")) {
			return CreatureType.PIG_ZOMBIE;
		} else if (alias.equalsIgnoreCase("giant") || alias.equalsIgnoreCase("bigzombie") || alias.equalsIgnoreCase("giantzombie")) {
			return CreatureType.GIANT;
		} else if (alias.equalsIgnoreCase("enderman") || alias.equalsIgnoreCase("eman") || alias.equalsIgnoreCase("endman") || alias.equalsIgnoreCase("endermen") || alias.equalsIgnoreCase("emen") || alias.equalsIgnoreCase("endmen")) {
			return CreatureType.ENDERMAN;
		} else if (alias.equalsIgnoreCase("bluespider") || alias.equalsIgnoreCase("cavespider") || alias.equalsIgnoreCase("smallspider") || alias.equalsIgnoreCase("poisonspider") || alias.equalsIgnoreCase("cavespiders") || alias.equalsIgnoreCase("bluespiders")) {
			return CreatureType.CAVE_SPIDER;
		} else if (alias.equalsIgnoreCase("silverfish") || alias.equalsIgnoreCase("sliverfish") || alias.equalsIgnoreCase("caterpillar") || alias.equalsIgnoreCase("caterpillars")) {
			return CreatureType.SILVERFISH;
		} else if (alias.equalsIgnoreCase("mooshroom") || alias.equalsIgnoreCase("redcow") || alias.equalsIgnoreCase("mooshrooms") || alias.equalsIgnoreCase("redcows")) {
			return CreatureType.MUSHROOM_COW;
		} else if (alias.equalsIgnoreCase("snowman") || alias.equalsIgnoreCase("snowgolem")) {
			return CreatureType.SNOWMAN;
		} else if (alias.equalsIgnoreCase("blaze") || alias.equalsIgnoreCase("blazes")) {
			return CreatureType.BLAZE;
		} else if (alias.equalsIgnoreCase("magmacube") || alias.equalsIgnoreCase("lavaslime") || alias.equalsIgnoreCase("magmacubes") || alias.equalsIgnoreCase("lavaslimes")) {
			return CreatureType.MAGMA_CUBE;
		} else if (alias.equalsIgnoreCase("npc") || alias.equalsIgnoreCase("testificate") || alias.equalsIgnoreCase("npcs") || alias.equalsIgnoreCase("villager") || alias.equalsIgnoreCase("villagers")) {
			return CreatureType.VILLAGER;
		} else if (alias.equalsIgnoreCase("dragon") || alias.equalsIgnoreCase("enderdragon") || alias.equalsIgnoreCase("dragons") || alias.equalsIgnoreCase("blackdragon")) {
			return CreatureType.ENDER_DRAGON;
		} else {
			return null;
		}
	}

	public void Cr(String[] args, User user) {
		if (args.length == 0) {
			if (user.isServer()) {
				user.sendMessage(ChatColor.RED + "The server cannot do that!");
				return;
			}
			if (((Player) user.getHandle()).getGameMode() == GameMode.CREATIVE) {
				((Player) user.getHandle()).setGameMode(GameMode.SURVIVAL);
				user.sendMessage(ChatColor.GREEN + "You are now in survival mode!");
				BenCmd.log(user.getName() + " has left creative mode");
			} else {
				((Player) user.getHandle()).setGameMode(GameMode.CREATIVE);
				user.sendMessage(ChatColor.GREEN + "You are now in creative mode!");
				BenCmd.log(user.getName() + " has entered creative mode!");
			}
		} else if (args.length == 1) {
			if (!user.hasPerm("bencmd.creative.other")) {
				user.sendMessage(ChatColor.RED + "You don't have permission to do that!");
				BenCmd.getPlugin().logPermFail();
				return;
			}
			User u = User.matchUser(args[0]);
			if (u == null) {
				user.sendMessage(ChatColor.RED + args[0] + " isn't online right now!");
				return;
			}
			if (((Player) user.getHandle()).getGameMode() == GameMode.CREATIVE) {
				((Player) user.getHandle()).setGameMode(GameMode.SURVIVAL);
				u.sendMessage(ChatColor.GREEN + "You are now in survival mode!");
				user.sendMessage(ChatColor.GREEN + "That user is now in survival mode!");
				BenCmd.log(u.getName() + " has left creative mode (" + user.getName() + ")");
			} else {
				((Player) user.getHandle()).setGameMode(GameMode.CREATIVE);
				u.sendMessage(ChatColor.GREEN + "You are now in creative mode!");
				user.sendMessage(ChatColor.GREEN + "That user is now in creative mode!");
				BenCmd.log(u.getName() + " has entered creative mode (" + user.getName() + ")");
			}
		} else {
			user.sendMessage(ChatColor.YELLOW + "Proper use: /cr [player]");
		}
	}

	public List<BCommand> getCommands(User user) {
		List<BCommand> commands = new ArrayList<BCommand>();
		commands.add(new BCommand("/help [page]", "Displays the xth page of the command list...", "."));
		commands.add(new BCommand("/slow [delay]", "Makes each user wait x seconds between chats.", "bencmd.chat.slow"));
		commands.add(new BCommand("/mute <player>", "Mutes a player.", "bencmd.action.mute"));
		commands.add(new BCommand("/unmute <player>", "Unmutes a player.", "bencmd.action.unmute"));
		commands.add(new BCommand("/list", "Lists the players online.", "bencmd.chat.list"));
		commands.add(new BCommand("/time {day|night|lock}", "Sets the time of day or locks the time.", "bencmd.time.set"));
		commands.add(new BCommand("/spawn", "Sends you to the spawn.", "bencmd.spawn.normal"));
		commands.add(new BCommand("/spawn <world>", "Sends you to the specified world's spawn.", "bencmd.spawn.all"));
		commands.add(new BCommand("/item <ID>[:damage] [amount] [player]", "Gives you an item.", "bencmd.inv.spawn"));
		commands.add(new BCommand("/god", "Makes you invincible.", "bencmd.god.self"));
		commands.add(new BCommand("/god [player]", "Makes another player invincible.", "bencmd.god.other"));
		commands.add(new BCommand("/heal", "Gives you full health.", "bencmd.heal.self"));
		commands.add(new BCommand("/heal [player]", "Gives another player full health.", "bencmd.heal.other"));
		commands.add(new BCommand("/bencmd reload", "Reloads the BenCmd Config", "bencmd.reload"));
		commands.add(new BCommand("/user <name> {add|remove|+/-<permissions>}", "Controls user permissions", "bencmd.editpermissions"));
		commands.add(new BCommand("/group <name> {add|remove|g:<group>|<permissions>}", "Controls group permissions", "bencmd.editpermissions"));
		commands.add(new BCommand("/warp <warp>", "Warps you to a pre-defined point.", "bencmd.warp.self"));
		commands.add(new BCommand("/warp <warp> <player>", "Warps another player to a pre-defined point.", "bencmd.warp.other"));
		commands.add(new BCommand("/setwarp <warp>", "Sets a new warp.", "bencmd.warp.set"));
		commands.add(new BCommand("/delwarp <warp>", "Deletes a warp.", "bencmd.warp.remove"));
		commands.add(new BCommand("/back", "Warps you back to before your last warp.", "bencmd.warp.back"));
		commands.add(new BCommand("/home <number>", "Teleports to your xth home.", "bencmd.home.self"));
		commands.add(new BCommand("/home <number> <player>", "Teleports to another player's xth home.", "bencmd.home.warpall"));
		commands.add(new BCommand("/sethome <number>", "Sets your xth home.", "bencmd.home.set"));
		commands.add(new BCommand("/sethome <number> <player>", "Sets another player's xth home.", "bencmd.home.setall"));
		commands.add(new BCommand("/delhome <number>", "Deletes your xth home.", "bencmd.home.remove"));
		commands.add(new BCommand("/delhome <number> <player>", "Deletes another player's xth home.", "bencmd.home.removeall"));
		commands.add(new BCommand("/clearinventory [player]", "Clears your own or another player's inventory.", "bencmd.inv.clr.self"));
		commands.add(new BCommand("/jail <player> <time>", "Jails a player for the set time.", "bencmd.action.jail"));
		commands.add(new BCommand("/unjail <player>", "Unjails a  player.", "bnecmd.action.unjail"));
		commands.add(new BCommand("/setjail", "Sets the jail location.", "bencmd.action.setjail"));
		commands.add(new BCommand("/unl <ID>[:Damage]", "Creates an unlimited dispenser", "bencmd.unlimited.create"));
		commands.add(new BCommand("/disp", "Creates a disposal chest.", "bencmd.disposal.create"));
		commands.add(new BCommand("/lot info", "Gets information on a lot.", "bencmd.lot.info"));
		commands.add(new BCommand("/lot set/advset", "Creates a new lot from your current selection.", "bencmd.lot.create"));
		commands.add(new BCommand("/lot extend/advext", "Expands a lot to your current selection.", "bencmd.lot.extend"));
		commands.add(new BCommand("/lot remove [id]", "Deletes a lot.", "bencmd.lot.remove"));
		commands.add(new BCommand("/kit <kitname>", "Spawns a kit.", "bencmd.inv.kit.spawn"));
		commands.add(new BCommand("/poof", "Makes you invisible.", "bencmd.poof.poof"));
		commands.add(new BCommand("/nopoof", "Makes you able to see invisible players.", "bencmd.poof.nopoof"));
		commands.add(new BCommand("/allpoof", "Makes you invisible to /nopoof players.", "bencmd.poof.allpoof"));
		commands.add(new BCommand("/protect {add|remove|info|setowner|addguest|remguest}", "Deals with protection.", "bencmd.lock.*"));
		commands.add(new BCommand("/lock", "Locks a chest", "bencmd.lock.create"));
		commands.add(new BCommand("/public", "Publicly locks a chest", "bencmd.lock.public"));
		commands.add(new BCommand("/unlock", "Unlocks a chest", "bencmd.lock.create"));
		commands.add(new BCommand("/share", "Adds a guest to a chest", "bencmd.lock.create"));
		commands.add(new BCommand("/unshare", "Removes a guest from a chest", "bencmd.lock.create"));
		commands.add(new BCommand("/setspawn", "Sets the map spawn point.", "bencmd.spawn.set"));
		commands.add(new BCommand("/me <emote>", "Allows you to emote something", "."));
		commands.add(new BCommand("/tell <player> <message>", "PMs a player.", "."));
		commands.add(new BCommand("/storm {off|rain|thunder}", "Changes the current map's weather.", "bencmd.storm.change"));
		commands.add(new BCommand("/strike", "Strikes lightning where you are pointing.", "bnemcd.storm.strike.location"));
		commands.add(new BCommand("/strike <player>", "Strikes that player with lightning.", "bencmd.storm.strike.player"));
		commands.add(new BCommand("/strike bind", "Binds/unbinds right-click striking to the current tool.", "bencmd.storm.strike.bind"));
		commands.add(new BCommand("/offline", "Makes you appear to be offline.", "bnecmd.poof.offline"));
		commands.add(new BCommand("/online", "Makes you re-appear to be online.", "bencmd.poof.offline"));
		commands.add(new BCommand("/report <player> <reason>", "Reports a player to the admins.", "bencmd.ticket.set"));
		commands.add(new BCommand("/ticket", "Lists and changes existing reports. Type /ticket for more info...", "bencmd.ticket.readown"));
		commands.add(new BCommand("/kill <player>", "Kills the player listed.", "bemcmd.kill.*"));
		commands.add(new BCommand("/mob <Mob Name>,<Passenger>,.. [Amount]", "Spawns a specific amount of a specific mob.", "bencmd.spawnmob"));
		commands.add(new BCommand("/killall <Mob Name> <Mob Name> etc <range>", "Kills all specified mobs within the given range.", "bencmd.spawmnmob"));
		commands.add(new BCommand("/buy <Item> [Amount]", "Buys an item.", "."));
		commands.add(new BCommand("/sell <Item> [Amount]", "Sells an item from your inventory.", "."));
		commands.add(new BCommand("/price <Item>", "Lists the price of a specific item.", "."));
		commands.add(new BCommand("/market", "Used to administrate the economic functions of BenCmd.", "bencmd.market.*"));
		commands.add(new BCommand("/tp <player>", "Teleports you to another player.", "bencmd.tp.self"));
		commands.add(new BCommand("/tp <plater> [player]", "Teleports one player to another.", "bencmd.tp.other"));
		commands.add(new BCommand("/tphere <player>", "Teleports a player to you.", "bnecmd.tp.other"));

		for (int i = 0; i < commands.size(); i++) {
			if (!commands.get(i).canUse(user)) {
				commands.remove(i);
			}
		}
		return commands;
	}

}
