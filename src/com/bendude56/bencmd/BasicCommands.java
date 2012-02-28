package com.bendude56.bencmd;

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
import org.bukkit.block.Block;
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

import com.bendude56.bencmd.Help.BCommand;

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
		User user = User.getUser(sender);
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
		} else if (commandLabel.equalsIgnoreCase("killmobs") && user.hasPerm("bencmd.spawnmob")) {
			KillEntities(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("rechunk")) {
			if (user.isServer()) {
				user.sendMessage(BenCmd.getLocale().getString("basic.noServerUse"));
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
				user.sendMessage(BenCmd.getLocale().getString("basic.noServerUse"));
				return true;
			}
			Location loc = ((Player) user.getHandle()).getTargetBlock(null, 4).getLocation();
			user.sendMessage(ChatColor.GREEN + "Fire next to that block can now spread...");
			BenCmd.getPlugin().canSpread.add(loc);
			return true;
		} else if (commandLabel.equalsIgnoreCase("nofire") && user.hasPerm("bencmd.fire.spread")) {
			if (user.isServer()) {
				user.sendMessage(BenCmd.getLocale().getString("basic.noServerUse"));
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
				user.sendMessage(BenCmd.getLocale().getString("basic.devOnly"));
				BenCmd.getPlugin().logPermFail(user, "debug", args, false);
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
			} else if (args[0].equalsIgnoreCase("lmsg")) {
				if (args.length < 2 || args.length > 3) {
					return true;
				}
				String msg = args[1];
				String lang = (args.length == 3) ? args[2] : BenCmd.getLocale().language;
				user.sendMessage(ChatColor.GRAY + "Message handle: \"" + msg + "\"");
				user.sendMessage(ChatColor.GRAY + "Language: \"" + lang + "\"");
				user.sendMessage(ChatColor.GRAY + "Contents: " + BenCmd.getLocale().getStringEx(msg, lang));
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
				user.sendMessage(BenCmd.getLocale().getString("command.kill.selfGod"));
			}
		} else if (args.length == 1) {
			if (!user.hasPerm("bencmd.kill.other")) {
				BenCmd.getPlugin().logPermFail(user, "kill", args, true);
				return;
			}
			User user2;
			if ((user2 = User.matchUserAllowPartial(args[0])) != null) {
				if (user2.hasPerm("bencmd.kill.protect") && !user.hasPerm("bencmd.kill.all")) {
					user.sendMessage(BenCmd.getLocale().getString("command.kill.protected", user2.getName()));
					return;
				}
				if (!user2.kill()) {
					user.sendMessage(BenCmd.getLocale().getString("command.kill.otherGod", user2.getName()));
				}
			} else {
				user.sendMessage(BenCmd.getLocale().getString("basic.userNotFound", args[0]));
			}
		} else {
			BenCmd.showUse(user, "kill");
		}
	}

	public void Time(String[] args, User user) {
		if (args.length == 0) {
			BenCmd.showUse(user, "time");
		} else {
			World w = null;
			if ((args[0].equals("set") && args.length == 3) || (!args[0].equals("set") && args.length == 2)) {
				w = Bukkit.getWorld((args[0].equals("set")) ? args[2] : args[1]);
				if (w == null) {
					user.sendMessage(BenCmd.getLocale().getString("basic.worldNotFound", (args[0].equals("set")) ? args[2] : args[1]));
					return;
				}
			}
			if (args[0].equalsIgnoreCase("day")) {
				Bukkit.dispatchCommand(user.getHandle(), "time set " + TIME_DAY + ((w == null) ? "" : " " + w.getName()));
			} else if (args[0].equalsIgnoreCase("night")) {
				Bukkit.dispatchCommand(user.getHandle(), "time set " + TIME_NIGHT + ((w == null) ? "" : " " + w.getName()));
			} else if (args[0].equalsIgnoreCase("set")) {
				if (!user.hasPerm("bencmd.time.set")) {
					BenCmd.getPlugin().logPermFail(user, "time", args, true);
					return;
				}
				int time;
				try {
					time = Integer.parseInt(args[1]);
				} catch (NumberFormatException e) {
					user.sendMessage(BenCmd.getLocale().getString("command.time.invalidTime"));
					return;
				}
				if (user.isServer() && w == null) {
					for (World world : Bukkit.getWorlds()) {
						world.setTime(time);
						BenCmd.getTimeManager().syncLastTime(world);
					}
					user.sendMessage(BenCmd.getLocale().getString("command.time.setAllSuccess", time + ""));
				} else if (w == null) {
					((Player) user.getHandle()).getWorld().setTime(time);
					BenCmd.getTimeManager().syncLastTime(((Player) user.getHandle()).getWorld());
					user.sendMessage(BenCmd.getLocale().getString("command.time.setSuccess", ((Player) user.getHandle()).getWorld().getName(), time + ""));
				} else {
					w.setTime(time);
					BenCmd.getTimeManager().syncLastTime(w);
					user.sendMessage(BenCmd.getLocale().getString("command.time.setSuccess", w.getName(), time + ""));
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
					BenCmd.getPlugin().logPermFail(user, "time", args, true);
					return;
				}
				if (user.isServer() && w == null) {
					for (World world : Bukkit.getWorlds()) {
						BenCmd.getTimeManager().setFrozen(world, true);
					}
					Bukkit.broadcastMessage(BenCmd.getLocale().getString("command.time.lockAll"));
				} else if (w == null) {
					w = ((Player) user.getHandle()).getWorld();
					BenCmd.getTimeManager().setFrozen(w, true);
					for (Player p : w.getPlayers()) {
						p.sendMessage(BenCmd.getLocale().getString("command.time.lock", w.getName()));
					}
				} else {
					BenCmd.getTimeManager().setFrozen(w, true);
					for (Player p : w.getPlayers()) {
						p.sendMessage(BenCmd.getLocale().getString("command.time.lock", w.getName()));
					}
				}
			} else if (args[0].equalsIgnoreCase("unlock")) {
				if (!user.hasPerm("bencmd.time.lock")) {
					BenCmd.getPlugin().logPermFail(user, "time", args, true);
					return;
				}
				if (user.isServer() && w == null) {
					for (World world : Bukkit.getWorlds()) {
						BenCmd.getTimeManager().setFrozen(world, false);
					}
					Bukkit.broadcastMessage(BenCmd.getLocale().getString("command.time.unlockAll"));
				} else if (w == null) {
					w = ((Player) user.getHandle()).getWorld();
					BenCmd.getTimeManager().setFrozen(w, false);
					for (Player p : w.getPlayers()) {
						p.sendMessage(BenCmd.getLocale().getString("command.time.unlock", w.getName()));
					}
				} else {
					BenCmd.getTimeManager().setFrozen(w, false);
					for (Player p : w.getPlayers()) {
						p.sendMessage(BenCmd.getLocale().getString("command.time.unlock", w.getName()));
					}
				}
			} else {
				BenCmd.showUse(user, "time");
			}
		}
	}

	public void Spawn(String[] args, User user) {
		if (user.isServer()) {
			user.sendMessage(BenCmd.getLocale().getString("basic.noServerUse"));
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
				BenCmd.showUse(user, "god");
				return;
			}
			if (user.isGod()) {
				user.makeNonGod();
				user.sendMessage(BenCmd.getLocale().getString("command.god.selfOff"));
				BenCmd.log(Level.INFO, BenCmd.getLocale().getString("log.god.selfOff", user.getName()));
			} else {
				user.makeGod();
				user.sendMessage(BenCmd.getLocale().getString("command.god.selfOn"));
				BenCmd.log(Level.INFO, BenCmd.getLocale().getString("log.god.selfOn", user.getName()));
			}
		} else if (args.length == 1) {
			if (!user.hasPerm("bencmd.god.other")) {
				BenCmd.getPlugin().logPermFail(user, "god", args, true);
				return;
			}
			User user2;
			if ((user2 = User.matchUserAllowPartial(args[0])) != null) {
				if (user2.hasPerm("bencmd.god.protect") && !user.hasPerm("bencmd.god.all")) {
					user.sendMessage(BenCmd.getLocale().getString("command.god.protected"));
					BenCmd.getPlugin().logPermFail(user, "god", args, false);
					return;
				}
				if (user2.isGod()) {
					user2.makeNonGod();
					user.sendMessage(BenCmd.getLocale().getString("command.god.selfOff"));
					user.sendMessage(BenCmd.getLocale().getString("command.god.otherOff", user2.getName()));
					BenCmd.log(Level.INFO, BenCmd.getLocale().getString("log.god.otherOff", user.getName(), user2.getName()));
				} else {
					user2.makeGod();
					user.sendMessage(BenCmd.getLocale().getString("command.god.selfOn"));
					user.sendMessage(BenCmd.getLocale().getString("command.god.otherOn", user2.getName()));
					BenCmd.log(Level.INFO, BenCmd.getLocale().getString("log.god.otherOn", user.getName(), user2.getName()));
				}
			}
		} else {
			BenCmd.showUse(user, "god");
		}
	}

	public void Heal(String[] args, User user) {
		if (args.length == 0) {
			if (user.isServer()) {
				BenCmd.showUse(user, "heal");
				return;
			}
			// Heal the player
			user.heal();
			user.sendMessage(BenCmd.getLocale().getString("command.heal.self"));
			BenCmd.log(BenCmd.getLocale().getString("log.heal.self", user.getName()));
		} else {
			if (!user.hasPerm("bencmd.heal.other") && !(args[0].equalsIgnoreCase(user.getDisplayName()) && user.hasPerm("bencmd.heal.self"))) {
				BenCmd.getPlugin().logPermFail(user, "heal", args, true);
				return;
			}
			// Heal the other player
			User user2;
			if ((user2 = User.matchUserAllowPartial(args[0])) != null) {
				user2.heal();
				user2.sendMessage(BenCmd.getLocale().getString("command.heal.self"));
				user.sendMessage(BenCmd.getLocale().getString("command.heal.other", user2.getName()));
				BenCmd.log(BenCmd.getLocale().getString("log.heal.self", user.getName()));
			} else {
				user.sendMessage(BenCmd.getLocale().getString("basic.userNotFound", args[0]));
			}
		}
	}

	public void Feed(String[] args, User user) {
		if (args.length == 0) {
			if (user.isServer()) {
				BenCmd.showUse(user, "feed");
				return;
			}
			// Feed the player
			user.feed();
			user.sendMessage(BenCmd.getLocale().getString("command.feed.self"));
			BenCmd.log(BenCmd.getLocale().getString("log.feed.self", user.getName()));
		} else {
			if (!user.hasPerm("bencmd.feed.other") && !(args[0].equalsIgnoreCase(user.getDisplayName()) && user.hasPerm("bencmd.feed.self"))) {
				BenCmd.getPlugin().logPermFail(user, "feed", args, true);
				return;
			}
			// Feed the other player
			User user2;
			if ((user2 = User.matchUserAllowPartial(args[0])) != null) {
				user2.feed();
				user2.sendMessage(BenCmd.getLocale().getString("command.feed.self"));
				user.sendMessage(BenCmd.getLocale().getString("command.feed.other", user2.getName()));
				BenCmd.log(BenCmd.getLocale().getString("log.feed.other", user.getName(), user2.getName()));
			} else {
				user.sendMessage(BenCmd.getLocale().getString("basic.userNotFound", args[0]));
			}
		}
	}
	
	public void Level(String[] args, User user) {
		if (args.length == 0) {
			BenCmd.showUse(user, "level");
		} else if (args.length == 1) {
			if (user.isServer()) {
				user.sendMessage(BenCmd.getLocale().getString("basic.noServerUse"));
				return;
			}
			if (!user.hasPerm("bencmd.experience.self")) {
				BenCmd.getPlugin().logPermFail(user, "level", args, true);
				return;
			}
			try {
				((Player) user.getHandle()).setTotalExperience(Integer.parseInt(args[0]));
				user.sendMessage(BenCmd.getLocale().getString("command.level.self", args[0]));
				BenCmd.log(BenCmd.getLocale().getString("log.level.self", args[0]));
				return;
			} catch (NumberFormatException e) {
				user.sendMessage(BenCmd.getLocale().getString("command.level.invalidExp"));
				return;
			}
		} else if (args.length == 2) {
			if (!user.hasPerm("bencmd.experience.other")) {
				BenCmd.getPlugin().logPermFail(user, "level", args, true);
				return;
			}
			User user2;
			if ((user2 = User.matchUserAllowPartial(args[1])) == null) {
				user.sendMessage(BenCmd.getLocale().getString("basic.userNotFound", args[1]));
				return;
			}
			try {
				((Player) user2.getHandle()).setLevel(Integer.parseInt(args[0]));
				user2.sendMessage(BenCmd.getLocale().getString("command.level.self", args[0]));
				user.sendMessage(BenCmd.getLocale().getString("command.level.other", user2.getName(), args[0]));
				BenCmd.log(BenCmd.getLocale().getString("log.level.other", user.getName(), user2.getName(), args[0]));
				return;
			} catch (NumberFormatException e) {
				user.sendMessage(BenCmd.getLocale().getString("command.level.invalidExp"));
				return;
			}
		} else {
			BenCmd.showUse(user, "level");
		}
	}

	public void BenCmd(String[] args, User user) {
		if (args.length == 0 || args[0].equalsIgnoreCase("version")) {
			PluginDescriptionFile pdfFile = BenCmd.getPlugin().getDescription();
			user.sendMessage(BenCmd.getLocale().getString("command.bencmd.version", pdfFile.getVersion()));
			return;
		} else if ((args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rel")) && user.hasPerm("bencmd.reload")) {
			BenCmd.unloadAll(true);
			BenCmd.loadAll();
			user.sendMessage(BenCmd.getLocale().getString("command.bencmd.reloadSuccess"));
			BenCmd.log(Level.WARNING, BenCmd.getLocale().getString("log.bencmd.reload", user.getName()));
		} else if (args[0].equalsIgnoreCase("update") && user.hasPerm("bencmd.update")) {
			BenCmd.log(Level.WARNING, BenCmd.getLocale().getString("log.bencmd.update", user.getName()));
			if (!BenCmd.getPlugin().update(false)) {
				user.sendMessage(BenCmd.getLocale().getString("command.bencmd.noUpdates"));
			}
		} else if (args[0].equalsIgnoreCase("fupdate") && user.hasPerm("bencmd.update")) {
			BenCmd.log(Level.WARNING, BenCmd.getLocale().getString("log.bencmd.fupdate", user.getName()));
			BenCmd.getPlugin().update(true);
		}
	}

	public void SetSpawn(User user) {
		if (user.isServer()) {
			user.sendMessage(BenCmd.getLocale().getString("basic.noServerUse"));
			return;
		}
		Location newSpawn = ((Player) user.getHandle()).getLocation();
		user.sendMessage(BenCmd.getLocale().getString("command.setspawn.setSuccess", newSpawn.getWorld().getName()));
		BenCmd.log(BenCmd.getLocale().getString("log.setspawn.set", user.getName(), newSpawn.getWorld().getName(), newSpawn.getBlockX() + "", newSpawn.getBlockY() + "", newSpawn.getBlockZ() + ""));
		((Player) user.getHandle()).getWorld().setSpawnLocation(newSpawn.getBlockX(), newSpawn.getBlockY(), newSpawn.getBlockZ());
	}

	public void Help(String[] args, User user) {
		// TODO Add localization to /help command
		int pageToShow;
		if (args.length == 0) {
			pageToShow = 1;
		} else {
			try {
				pageToShow = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				if (args.length == 1 || (args.length == 2 && (args[1].equalsIgnoreCase("1") || args[1].equalsIgnoreCase("gist")))) {
					Help.ShowHelp(args[0], user);
				} else if (args.length == 2 && (args[1].equalsIgnoreCase("2") || args[1].equalsIgnoreCase("more") || args[1].equalsIgnoreCase("details") || args[1].equalsIgnoreCase("instructions"))) {
					Help.ShowDetails(args[0], user);
				} else {
					user.sendMessage(ChatColor.RED + "Proper use is /help <page> or /help <command> [more]");
				}
				return;
			}
		}
		List<BCommand> commands = Help.Commands(user);
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
			user.sendMessage(ChatColor.GREEN + "/" + Help.Commands().get(i).getLabel()
					+ ChatColor.WHITE + " - " + ChatColor.GRAY
					+ Help.Commands().get(i).getGist());
			i++;
		}
		user.sendMessage(ChatColor.GREEN + "Type \"/help <command> [more]\" to get help on a particular command.");
	}

	public void SpawnMob(String[] args, User user) {
		if (user.isServer()) {
			user.sendMessage(BenCmd.getLocale().getString("basic.noServerUse"));
			return;
		}
		if (args.length != 1 && args.length != 2) {
			BenCmd.showUse(user, "spawnmob");
			return;
		}
		int amount = 1;
		if (args.length == 2) {
			try {
				amount = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				user.sendMessage(BenCmd.getLocale().getString("command.spawnmob.invalidAmount"));
				return;
			}
		}

		int mobCounter = 0;

		// Prepare for passengers
		String[] passengers = args[0].split(",");
		LivingEntity vehicle = null, passenger, mob;
		CreatureType mobType = CreatureType.COW;
		
		Location l = ((Player) user.getHandle()).getLocation();

		// Spawn the mob(s)
		// TODO HERE BE DRAGONS!!
		for (int i = 0; i < amount; i++) {
			vehicle = null;
			for (int p = 0; p < passengers.length; p++) {
				mobType = getMobType(passengers[p].split(":")[0]);
				
				if (mobType != null) {
					mob = l.getWorld().spawnCreature(l, mobType);
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
		user.sendMessage(BenCmd.getLocale().getString("command.spawnmob.spawnMsg", mobCounter + ""));
		BenCmd.log(BenCmd.getLocale().getString("log.spawnmob.spawnMsg", user.getName(), amount + "", mobType.getName(), l.getX() + "", l.getY() + "", l.getX() + "", l.getWorld().getName()));
	}

	public void Spawner(String args[], User user) {
		if (user.isServer()) {
			user.sendMessage(BenCmd.getLocale().getString("basic.noServerUse"));
			return;
		}
		Player player = ((Player) user.getHandle());
		Block b = player.getTargetBlock(null, 4);
		if (b.getType().equals(Material.MOB_SPAWNER)) {
			if (args.length!= 1) {
				user.sendMessage(ChatColor.RED + "Proper use is /spawner <creature>");
				return;
			}
			CreatureType mob = getMobType(args[0]);
			if (mob == null) {
				user.sendMessage(BenCmd.getLocale().getString("command.spawner.invalidMob"));
				return;
			}
			((CreatureSpawner) b).setCreatureType(mob);
			user.sendMessage(BenCmd.getLocale().getString("command.spawner.setSuccess", mob.getName()));
			BenCmd.log(BenCmd.getLocale().getString("log.spawner.set", user.getName(), b.getX() + "", b.getY() + "", b.getZ() + "", b.getWorld().getName(), mob.getName()));
			return; 
		} else {
			user.sendMessage(BenCmd.getLocale().getString("command.spawner.invalidSpawner"));
			return;
		}
	}

	public void KillEntities(String args[], User user) {		
		if (user.isServer()) {
			user.sendMessage(BenCmd.getLocale().getString("basic.noServerUse"));
			return;
		}
		// Tally up the mobs
		int mobCounter = 0;
		int range = -1;
		boolean killmobs = false;
		boolean killHostile = false;
		CreatureType mobToKill = CreatureType.PIG;
		
		// TODO HERE BE DRAGONS!!
		if (args.length == 0) {
			BenCmd.showUse(user, "killmobs");
			return;
		} else if (args.length == 1) {
			if (getMobType(args[0]) == null) {
				if (args[0].equalsIgnoreCase("hostile")) {
					killHostile = true;
				} else if (args[0].equalsIgnoreCase("all")) {
					killmobs = true;
				} else {
					BenCmd.showUse(user, "killmobs");
					return;
				}
			}
		} else {
			try {
				range = Integer.parseInt(args[args.length - 1]);
			} catch (NumberFormatException e) {
				range = -1;
			}
		}
		for (int i = 0; i < args.length; i++) {
			if (args[i].equalsIgnoreCase("all")
					|| args[i].equalsIgnoreCase("hostile")
					|| (mobToKill = getMobType(args[i])) != null) {
				for (int ii = 0; ii < ((Player) user.getHandle()).getWorld().getLivingEntities().size(); ii++) {
						LivingEntity entity = ((Player) user.getHandle()).getWorld().getLivingEntities().get(ii);
						{
						if (	   (entity instanceof Creeper && (mobToKill == CreatureType.CREEPER || killmobs || killHostile))
								|| (entity instanceof Zombie && (mobToKill == CreatureType.ZOMBIE || killmobs || killHostile))
								|| (entity instanceof Skeleton && (mobToKill == CreatureType.SKELETON || killmobs || killHostile))
								|| (entity instanceof Spider && (mobToKill == CreatureType.SPIDER || killmobs || killHostile))
								|| (entity instanceof Slime && (mobToKill == CreatureType.SLIME || killmobs || killHostile))
								|| (entity instanceof CaveSpider && (mobToKill == CreatureType.CAVE_SPIDER || killmobs))
								|| (entity instanceof Enderman && (mobToKill == CreatureType.ENDERMAN || killmobs || killHostile))
								|| (entity instanceof Silverfish && (mobToKill == CreatureType.SILVERFISH || killmobs || killHostile))
								|| (entity instanceof PigZombie && (mobToKill == CreatureType.PIG_ZOMBIE || killmobs || killHostile))
								|| (entity instanceof Ghast && (mobToKill == CreatureType.GHAST || killmobs || killHostile))
								|| (entity instanceof MagmaCube && (mobToKill == CreatureType.MAGMA_CUBE || killmobs || killHostile))
								|| (entity instanceof Blaze && (mobToKill == CreatureType.BLAZE || killmobs || killHostile))
								|| (entity instanceof Pig && (mobToKill == CreatureType.PIG || killmobs))
								|| (entity instanceof Sheep && (mobToKill == CreatureType.SHEEP || killmobs))
								|| (entity instanceof Cow && (mobToKill == CreatureType.COW || killmobs))
								|| (entity instanceof Chicken && (mobToKill == CreatureType.CHICKEN || killmobs))
								|| (entity instanceof MushroomCow && ((mobToKill == CreatureType.MUSHROOM_COW && mobToKill != CreatureType.COW) || killmobs))
								|| (entity instanceof Squid && (mobToKill == CreatureType.SQUID || killmobs))
								|| (entity instanceof Villager && (mobToKill == CreatureType.VILLAGER || killmobs))
								|| (entity instanceof Wolf && (mobToKill == CreatureType.WOLF || killmobs))
								|| (entity instanceof Snowman && (mobToKill == CreatureType.SNOWMAN || killmobs))
								|| (entity instanceof EnderDragon && (mobToKill == CreatureType.ENDER_DRAGON || killmobs || killHostile))
								|| (entity instanceof Giant && (mobToKill == CreatureType.GIANT || killmobs || killHostile))) {
							if (range == -1 || getDistance(((Player) user.getHandle()).getLocation(), entity.getLocation(), false) <= range) {
								entity.remove();
								mobCounter++;
							}
						}
					}
				}
			}
		}
		if (mobCounter == 0) {
			user.sendMessage(BenCmd.getLocale().getString("command.killmobs.noMobs"));
		} else {
			user.sendMessage(BenCmd.getLocale().getString("command.killmobs.success", mobCounter + ""));
			BenCmd.log(BenCmd.getLocale().getString("log.killmobs.success", user.getName(), mobCounter + "", ((Player) user.getHandle()).getWorld().getName()));
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
				user.sendMessage(BenCmd.getLocale().getString("basic.noServerUse"));
				return;
			}
			if (((Player) user.getHandle()).getGameMode() == GameMode.CREATIVE) {
				((Player) user.getHandle()).setGameMode(GameMode.SURVIVAL);
				user.sendMessage(BenCmd.getLocale().getString("command.cr.selfOff"));
				BenCmd.log(BenCmd.getLocale().getString("log.cr.selfOff", user.getName()));
			} else {
				((Player) user.getHandle()).setGameMode(GameMode.CREATIVE);
				user.sendMessage(BenCmd.getLocale().getString("command.cr.selfOn"));
				BenCmd.log(BenCmd.getLocale().getString("log.cr.selfOn", user.getName()));
			}
		} else if (args.length == 1) {
			if (!user.hasPerm("bencmd.creative.other")) {
				BenCmd.getPlugin().logPermFail(user, "cr", args, true);
				return;
			}
			User u = User.matchUser(args[0]);
			if (u == null) {
				user.sendMessage(BenCmd.getLocale().getString("basic.userNotFound", args[0]));
				return;
			}
			if (((Player) u.getHandle()).getGameMode() == GameMode.CREATIVE) {
				((Player) u.getHandle()).setGameMode(GameMode.SURVIVAL);
				u.sendMessage(BenCmd.getLocale().getString("command.cr.selfOff"));
				user.sendMessage(BenCmd.getLocale().getString("command.cr.otherOff", u.getName()));
				BenCmd.log(BenCmd.getLocale().getString("log.cr.otherOff", user.getName(), u.getName()));
			} else {
				((Player) u.getHandle()).setGameMode(GameMode.CREATIVE);
				u.sendMessage(BenCmd.getLocale().getString("command.cr.selfOn"));
				user.sendMessage(BenCmd.getLocale().getString("command.cr.otherOn", u.getName()));
				BenCmd.log(BenCmd.getLocale().getString("log.cr.otherOn", user.getName(), u.getName()));
			}
		} else {
			BenCmd.showUse(user, "cr");
		}
	}

	/*public List<BCommand> getCommands(User user) {
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
		commands.add(new BCommand("/killmobs <Mob Name> <Mob Name> etc <range>", "Kills all specified mobs within the given range.", "bencmd.spawmnmob"));
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
	}*/

}
