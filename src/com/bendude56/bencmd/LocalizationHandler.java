package com.bendude56.bencmd;

import java.io.*;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;

import org.bukkit.ChatColor;

public class LocalizationHandler {
	public Properties currentFile;
	public String language;
	public HashMap<String, Properties> secondaryLanguages;
	
	public LocalizationHandler(String language) {
		File dir = new File(BenCmd.propDir + "lang");
		if (!dir.exists()) {
			if (!dir.mkdir()) {
				BenCmd.log(Level.SEVERE, "Failed to create language folder!");
			}
		} else if (!dir.isDirectory()) {
			BenCmd.log(Level.SEVERE, "File exists with name 'lang' in BenCmd folder! Delete before continuing...");
		}
		loadDefaults();
		this.language = language;
		this.currentFile = loadLanguage(language);
	}
	
	public Properties loadLanguage(String language) {
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(new File(BenCmd.propDir + "lang/" + language + ".lang")));
		} catch (Exception e) {
			BenCmd.log(Level.WARNING, "Failed to load language '" + language + "'! Reverting to en-US...");
			language = "en-US";
			try {
				prop.load(new FileInputStream(new File(BenCmd.propDir + "lang/en-US.lang")));
			} catch (Exception e2) {
				BenCmd.log(Level.WARNING, "Failed to load language 'en-US'! Reverting to default values...");
				prop = loadDefaults();
			}
		}
		return prop;
	}
	
	public String getString(String identifier) {
		if (currentFile.containsKey(identifier)) {
			return currentFile.getProperty(identifier);
		} else {
			BenCmd.log(Level.WARNING, "Missing string '" + identifier + "' in language '" + language + "'!");
			return ChatColor.RED + "[NOMSG: " + identifier + "]";
		}
	}
	
	public String getString(String identifier, String... args) {
		if (currentFile.containsKey(identifier)) {
			return format(currentFile.getProperty(identifier), args);
		} else {
			BenCmd.log(Level.WARNING, "Missing string '" + identifier + "' in language '" + language + "'!");
			return ChatColor.RED + "[NOMSG: " + identifier + "]";
		}
	}
	
	public void sendMessage(User user, String identifier, String... args) {
		user.sendMessage(getString(identifier, args));
	}
	
	public void sendMessage(User user, String identifier) {
		user.sendMessage(getString(identifier));
	}
	
	public void sendMultilineMessage(User user, String identifier) {
		for (int i = 0; currentFile.containsKey(identifier + "." + i); i++) {
			user.sendMessage(getString(identifier + "." + i));
		}
	}
	
	public void sendMultilineMessage(User user, String identifier, String... args) {
		for (int i = 0; currentFile.containsKey(identifier + "." + i); i++) {
			user.sendMessage(getString(identifier + "." + i, args));
		}
	}
	
	public static String format(String original, String... args) {
		for (int i = 0; i < args.length; i++) {
			original = original.replace("%" + (i + 1), args[i]);
		}
		return original;
	}
	
	public String getStringEx(String identifier, String language) {
		if (this.language.equalsIgnoreCase(language))
			return getString(identifier);
		if (secondaryLanguages.containsKey(language)) {
			if (currentFile.containsKey(identifier)) {
				return currentFile.getProperty(identifier);
			} else {
				BenCmd.log(Level.WARNING, "Missing string '" + identifier + "' in language '" + language + "'!");
				return ChatColor.RED + "[NOMSG: " + identifier + "]";
			}
		} else {
			BenCmd.log(Level.SEVERE, "Missing language '" + language + "'!");
			return ChatColor.RED + "[NOMSG: " + identifier + "]";
		}
	}
	
	public String getStringEx(String identifier, String language, String... args) {
		if (this.language.equalsIgnoreCase(language))
			return getString(identifier);
		if (secondaryLanguages.containsKey(language)) {
			if (currentFile.containsKey(identifier)) {
				return format(currentFile.getProperty(identifier), args);
			} else {
				BenCmd.log(Level.WARNING, "Missing string '" + identifier + "' in language '" + language + "'!");
				return ChatColor.RED + "[NOMSG: " + identifier + "]";
			}
		} else {
			BenCmd.log(Level.SEVERE, "Missing language '" + language + "'!");
			return ChatColor.RED + "[NOMSG: " + identifier + "]";
		}
	}
	
	public static Properties loadDefaults() {
		Properties p = new Properties();
		
		// Basic messages
		p.setProperty("basic.devOnly", ChatColor.RED + "That command is for BenCmd developers only!");
		p.setProperty("basic.groupNotFound", ChatColor.RED + "Couldn't find group '%1'!");
		p.setProperty("basic.insufficientMoney", ChatColor.RED + "You don't have enough money to do that! (Required funds: %1)");
		p.setProperty("basic.join", "%1" + ChatColor.YELLOW + " has joined the game");
		p.setProperty("basic.joinDev", ChatColor.DARK_GREEN + "A BenCmd developer has joined the game!");
		p.setProperty("basic.no", "No");
		p.setProperty("basic.noBuild", ChatColor.RED + "You cannot build here!");
		p.setProperty("basic.noPermission", ChatColor.RED + "You don't have permission to do that!");
		p.setProperty("basic.noServerUse", ChatColor.RED + "The server can't do that!");
		p.setProperty("basic.noSpawn", ChatColor.RED + "You aren't allowed to spawn that item!");
		p.setProperty("basic.quit", "%1" + ChatColor.YELLOW + " has left the game");
		p.setProperty("basic.unreadTickets", ChatColor.RED + "You have unread tickets! Use /tick list to see them...");
		p.setProperty("basic.updateFail", ChatColor.RED + "A BenCmd update failed to download properly! Some features may cease to function...");
		p.setProperty("basic.updateInProgress", ChatColor.RED + "BenCmd is updating... Some features may reset after it is updated...");
		p.setProperty("basic.updateNag", ChatColor.RED + "A new BenCmd update was detected! Use \"/bencmd update\" to update your server...");
		p.setProperty("basic.use", ChatColor.YELLOW + "Proper usage is:");
		p.setProperty("basic.userNotFound", ChatColor.RED + "Couldn't find user '%1'!");
		p.setProperty("basic.worldNotFound", ChatColor.RED + "Couldn't find world '%1'!");
		p.setProperty("basic.yes", "Yes");
		
		// Command specific messages
		
		// ALLPOOF
		p.setProperty("command.allpoof.poof", ChatColor.GREEN + "ALLPOOF!");
		p.setProperty("command.allpoof.poofOther", ChatColor.GREEN + "%1 is now allpoofed!");
		p.setProperty("command.allpoof.unpoof", ChatColor.GREEN + "REVERSE ALLPOOF! (STILL POOFED!)");
		p.setProperty("command.allpoof.unpoofOther", ChatColor.GREEN + "%1 is no longer allpoofed!");
		p.setProperty("command.allpoof.use", ChatColor.YELLOW + "/allpoof [user]");
		
		// AREA
		p.setProperty("command.area.areaNotFound", ChatColor.RED + "There is no area with that ID!");
		p.setProperty("command.area.notInArea", ChatColor.RED + "You aren't standing inside an area!");
		p.setProperty("command.area.use", ChatColor.YELLOW + "/area {new|info|table|delete|emsg|lmsg|addi|remi|die|mtime}");
		
		p.setProperty("command.area.use.info", ChatColor.YELLOW + "/area info [id]");
		
		// BANK
		p.setProperty("command.bank.otherAlreadyDowngraded", ChatColor.RED + "%1's bank is not currently upgraded!");
		p.setProperty("command.bank.otherAlreadyUpgraded", ChatColor.RED + "%1's bank is already upgraded!");
		p.setProperty("command.bank.otherCannotDowngrade", ChatColor.RED + "The bottom half of %1's bank must be empty to proceed!");
		p.setProperty("command.bank.otherDowngrade", ChatColor.GREEN + "%1's bank has been successfully downgraded!");
		p.setProperty("command.bank.otherNoBank", ChatColor.RED + "That user doesn't exist or doesn't have a bank account!");
		p.setProperty("command.bank.otherUpgrade", ChatColor.GREEN + "%1's bank has been successfully upgraded!");
		p.setProperty("command.bank.protected", ChatColor.RED + "%1's bank is protected!");
		p.setProperty("command.bank.selfAlreadyDowngraded", ChatColor.RED + "Your bank is not currently upgraded!");
		p.setProperty("command.bank.selfAlreadyUpgraded", ChatColor.RED + "Your bank is already upgraded!");
		p.setProperty("command.bank.selfCannotDowngrade", ChatColor.RED + "The bottom half of your bank must be empty to proceed!");
		p.setProperty("command.bank.selfDowngrade", ChatColor.GREEN + "Your bank has been successfully downgraded!");
		p.setProperty("command.bank.selfUpgrade", ChatColor.GREEN + "Your bank has been successfully upgraded!");
		
		p.setProperty("command.bank.use.admin", ChatColor.YELLOW + "/bank [{downgrade|upgrade|<name> [{downgrade|upgrade}]}]");
		p.setProperty("command.bank.use.normal", ChatColor.YELLOW + "/bank [{downgrade|upgrade}]");
		
		// BENCMD
		p.setProperty("command.bencmd.noUpdates", ChatColor.RED + "There are no BenCmd updates available... Use /bencmd fupdate to force an update.");
		p.setProperty("command.bencmd.reloadSuccess", ChatColor.GREEN + "BenCmd config reloaded!");
		p.setProperty("command.bencmd.version", ChatColor.YELLOW + "This server is running BenCmd v" + ChatColor.GREEN + "%1" + ChatColor.YELLOW + "!");
		
		// CHANNEL
		p.setProperty("command.channel.channelNotFound", ChatColor.RED + "Couldn't find channel '%1'!");
		p.setProperty("command.channel.higherRole", ChatColor.RED + "You can't do that because '%1' is higher than or equal to your rank.");
		p.setProperty("command.channel.notInChannel", ChatColor.RED + "You must be inside of a channel to do that!");
		
		p.setProperty("command.channel.info.1", ChatColor.GRAY + "Information for channel '" + ChatColor.GREEN + "%1" + ChatColor.GRAY + "':");
		p.setProperty("command.channel.info.2", ChatColor.GRAY + "Default level: " + ChatColor.GREEN + "%2");
		p.setProperty("command.channel.info.3", ChatColor.GRAY + "Always slow: " + ChatColor.GREEN + "%3");
		p.setProperty("command.channel.info.4", ChatColor.GRAY + "Slow delay: " + ChatColor.GREEN + "%4");
		p.setProperty("command.channel.info.5", ChatColor.GRAY + "MOTD: " + ChatColor.YELLOW + "%5");
		p.setProperty("command.channel.info.banned", "Banned");
		p.setProperty("command.channel.info.muted", "Muted");
		p.setProperty("command.channel.info.normal", "Normal");
		
		p.setProperty("command.channel.list.list", ChatColor.GREEN + "You can join the following chat channels:");
		p.setProperty("command.channel.list.none", ChatColor.RED + "You cannot join any chat channels");
		
		p.setProperty("command.channel.remove.danger.1", ChatColor.RED + "WARNING: You are about to permanently delete '" + ChatColor.GREEN + "%1" + ChatColor.RED + "'!");
		p.setProperty("command.channel.remove.danger.2", ChatColor.RED + "All current users will be kicked from the channel! Repeat this");
		p.setProperty("command.channel.remove.danger.3", ChatColor.RED + "command within 20 seconds to verify your intention!");
		p.setProperty("command.channel.remove.success", ChatColor.GREEN + "Channel '%1' has been removed!");
		
		p.setProperty("command.channel.use.add", ChatColor.YELLOW + "/channel add <channel>");
		p.setProperty("command.channel.use.alwaysSlow", ChatColor.YELLOW + "/channel alwaysslow {true|false}");
		p.setProperty("command.channel.use.ban", ChatColor.YELLOW + "/channel ban <user>");
		p.setProperty("command.channel.use.coown", ChatColor.YELLOW + "/channel coown <user>");
		p.setProperty("command.channel.use.default", ChatColor.YELLOW + "/channel default {ban|mute|normal}");
		p.setProperty("command.channel.use.info", ChatColor.YELLOW + "/channel info");
		p.setProperty("command.channel.use.join", ChatColor.YELLOW + "/channel join <channel>");
		p.setProperty("command.channel.use.mod", ChatColor.YELLOW + "/channel mod <user>");
		p.setProperty("command.channel.use.motd", ChatColor.YELLOW + "/channel motd <message>");
		p.setProperty("command.channel.use.mute", ChatColor.YELLOW + "/channel mute <user>");
		p.setProperty("command.channel.use.normal", ChatColor.YELLOW + "/channel normal <user>");
		p.setProperty("command.channel.use.own", ChatColor.YELLOW + "/channel own <user>");
		p.setProperty("command.channel.use.pause", ChatColor.YELLOW + "/channel pause");
		p.setProperty("command.channel.use.rename", ChatColor.YELLOW + "/channel rename <name>");
		p.setProperty("command.channel.use.slow", ChatColor.YELLOW + "/channel slow [millis]");
		p.setProperty("command.channel.use.slowdelay", ChatColor.YELLOW + "/channel slowdelay <millis>");
		p.setProperty("command.channel.use.spy", ChatColor.YELLOW + "/channel spy <channel>");
		p.setProperty("command.channel.use.unspy", ChatColor.YELLOW + "/channel unspy <channel>");
		p.setProperty("command.channel.use.vip", ChatColor.YELLOW + "/channel vip <user>");
		
		p.setProperty("command.channel.use.nCoowner", ChatColor.YELLOW + "/channel {join|spy|unspy|list|info|leave|kick|ban|mute|normal|vip|mod|slow|pause|motd|default|alwaysslow|slowdelay}");
		p.setProperty("command.channel.use.nMod", ChatColor.YELLOW + "/channel {join|spy|unspy|list|info|leave|kick|ban|mute|normal|slow|pause}");
		p.setProperty("command.channel.use.nNormal", ChatColor.YELLOW + "/channel {join|spy|unspy|list|leave}");
		p.setProperty("command.channel.use.nOutside", ChatColor.YELLOW + "/channel {join|spy|unspy|list|add}");
		p.setProperty("command.channel.use.nOwner", ChatColor.YELLOW + "/channel {join|spy|unspy|list|info|leave|remove|kick|ban|mute|normal|vip|mod|coown|own|slow|pause|motd|default|rename|alwaysslow|slowdelay}");
		
		// CLRINV
		p.setProperty("command.clrinv.protected", ChatColor.RED + "%1's inventory is protected from being cleared!");
		p.setProperty("command.clrinv.use", ChatColor.YELLOW + "/clrinv [user]");
		
		// CR
		p.setProperty("command.cr.otherOff", ChatColor.GREEN + "%1 is now in survival mode!");
		p.setProperty("command.cr.otherOn", ChatColor.GREEN + "%1 is now in creative mode!");
		p.setProperty("command.cr.selfOff", ChatColor.GREEN + "You are now in survival mode!");
		p.setProperty("command.cr.selfOn", ChatColor.GREEN + "You are now in creative mode!");
		
		// DISP
		p.setProperty("command.disp.notChest", ChatColor.RED + "You aren't pointing at a chest!");
		p.setProperty("command.disp.success", ChatColor.GREEN + "That chest is now a disposal chest!");
		p.setProperty("command.disp.use", ChatColor.YELLOW + "/disp");
		
		// DISPLAY
		p.setProperty("command.display.error", ChatColor.RED + "Error logging in to new account: %1");
		p.setProperty("command.display.success", ChatColor.GREEN + "You are now imitating %1!");
		
		// FEED
		p.setProperty("command.feed.self", ChatColor.GREEN + "You have been fed.");
		p.setProperty("command.feed.other", ChatColor.GREEN + "You have fed %1.");
		p.setProperty("command.feed.use", ChatColor.YELLOW + "/feed [user]");
		
		// GOD
		p.setProperty("command.god.otherOff", ChatColor.GREEN + "%1 is no longer in god mode.");
		p.setProperty("command.god.otherOn", ChatColor.GREEN + "%1 is now in god mode.");
		p.setProperty("command.god.protected", ChatColor.RED + "%1 is protected from being godded/ungodded!");
		p.setProperty("command.god.selfOff", ChatColor.GREEN + "You are no longer in god mode.");
		p.setProperty("command.god.selfOn", ChatColor.GREEN + "You are now in god mode.");
		p.setProperty("command.god.use", ChatColor.YELLOW + "/god [user]");
		
		// HEAL
		p.setProperty("command.heal.other", ChatColor.GREEN + "You have healed %1!");
		p.setProperty("command.heal.self", ChatColor.GREEN + "You have been healed!");
		p.setProperty("command.heal.use", ChatColor.YELLOW + "/heal [user]");
		
		// IGNORE
		p.setProperty("command.ignore.alreadyIgnoring", ChatColor.RED + "You're already ignoring that user!");
		p.setProperty("command.ignore.cannotIgnore", ChatColor.RED + "You can't ignore an administrator!");
		p.setProperty("command.ignore.list", ChatColor.GREEN + "You are ignoring the following users:");
		p.setProperty("command.ignore.listNone", ChatColor.RED + "You're not ignoring anybody!");
		p.setProperty("command.ignore.success", ChatColor.GREEN + "You are now ignoring %1!");
		p.setProperty("command.ignore.use", ChatColor.YELLOW + "/ignore [user]");
		
		// INV
		p.setProperty("command.inv.protected", ChatColor.RED + "%1's inventory is protected!");
		p.setProperty("command.inv.use", ChatColor.YELLOW + "/inv <user>");
		
		// ITEM
		p.setProperty("command.item.giftReceive", ChatColor.GREEN + "You have received a gift from %1.");
		p.setProperty("command.item.giftSend", ChatColor.GREEN + "Your gift has been sent to %1.");
		p.setProperty("command.item.invalidAmount", ChatColor.RED + "Invalid amount inputted!");
		p.setProperty("command.item.invalidId", ChatColor.RED + "Invalid item ID or damage inputted!");
		p.setProperty("command.item.success", ChatColor.GREEN + "Enjoy, %1!");
		p.setProperty("command.item.use", ChatColor.YELLOW + "/item <id>[:<damage>] [amount] [player]");
		
		// KILL
		p.setProperty("command.kill.otherGod", ChatColor.RED + "You can't kill %1 while they're godded!");
		p.setProperty("command.kill.protected", ChatColor.RED + "%1 is protected from being killed!");
		p.setProperty("command.kill.selfGod", ChatColor.RED + "You can't kill yourself while you're godded!");
		p.setProperty("command.kill.use", ChatColor.YELLOW + "/kill [user]");
		
		// KILLMOBS
		p.setProperty("command.killmobs.noMobs" , ChatColor.RED + "No mobs killed!");
		p.setProperty("command.killmobs.success", ChatColor.GREEN + "%1 mobs killed!");
		p.setProperty("command.killmobs.unrecognizedMob", ChatColor.RED + "Unrecognized mob type: %1");
		p.setProperty("command.killmobs.use", ChatColor.YELLOW + "/killmobs <mob> ... [radius]");
		
		// KIT
		p.setProperty("command.kit.giftReceive", ChatColor.GREEN + "You have received a gift from %1.");
		p.setProperty("command.kit.giftSend", ChatColor.GREEN + "Your gift has been sent to %1.");
		p.setProperty("command.kit.invalid", ChatColor.RED + "That kit doesn't exist or you don't have permission to spawn it!");
		p.setProperty("command.kit.list", ChatColor.GRAY + "You can spawn the following kits:");
		p.setProperty("command.kit.listNone", ChatColor.RED + "You cannot access any kits!");
		p.setProperty("command.kit.success", ChatColor.GREEN + "Enjoy, %1!");
		p.setProperty("command.kit.use", ChatColor.YELLOW + "/kit [kit] [user]");
		
		// LEVEL
		p.setProperty("command.level.invalidExp", ChatColor.RED + "Invalid experience inputted!");
		p.setProperty("command.level.other", ChatColor.GREEN + "You have set %1's total experience to %2.");
		p.setProperty("command.level.self", ChatColor.GREEN + "Your total experience has been set to %1.");
		p.setProperty("command.level.use", ChatColor.YELLOW + "/level <exp> [user]");
		
		// LEVER
		p.setProperty("command.lever.notALever", ChatColor.RED + "The block you are pointing at is not a lever!");
		p.setProperty("command.lever.notTimed", ChatColor.RED + "That lever is not time-controlled!");
		p.setProperty("command.lever.success.day", ChatColor.GREEN + "That lever will now activate during the day!");
		p.setProperty("command.lever.success.night", ChatColor.GREEN + "That lever will now activate during the night!");
		p.setProperty("command.lever.success.none", ChatColor.GREEN + "That lever is no longer time controlled!");
		p.setProperty("command.lever.use", ChatColor.YELLOW + "/lever {day|night|none}");
		
		// LIST
		p.setProperty("command.list.empty", ChatColor.GREEN + "The server is empty :(");
		p.setProperty("command.list.list", ChatColor.GREEN + "The following players are online:");
		
		// MONITOR
		p.setProperty("command.monitor.success", ChatColor.GREEN + "You are now seeing through the eyes of %1!");
		p.setProperty("command.monitor.use", ChatColor.YELLOW + "/monitor {[user]|none}");
		
		// NOPOOF
		p.setProperty("command.nopoof.poof", ChatColor.GREEN + "NOPOOF!");
		p.setProperty("command.nopoof.poofOther", ChatColor.GREEN + "%1 is now nopoofed!");
		p.setProperty("command.nopoof.unpoof", ChatColor.GREEN + "REVERSE NOPOOF!");
		p.setProperty("command.nopoof.unpoofOther", ChatColor.GREEN + "%1 is no longer nopoofed!");
		p.setProperty("command.nopoof.use", ChatColor.YELLOW + "/nopoof [user]");
		
		// NPC
		p.setProperty("command.npc.banker", "Banker");
		p.setProperty("command.npc.bankManager", "Bank Manager");
		p.setProperty("command.npc.blacksmith", "Blacksmith");
		p.setProperty("command.npc.created", ChatColor.GREEN + "NPC of type '%1' successfully created!");
		p.setProperty("command.npc.despawnall", ChatColor.GREEN + "All NPCs have been despawned!");
		p.setProperty("command.npc.npcNotFound", ChatColor.RED + "No NPC with that ID exists!");
		p.setProperty("command.npc.static", "Static");
		p.setProperty("command.npc.useTip", ChatColor.YELLOW + "TIP: Right-click an NPC with a stick to get info about that NPC");
		
		p.setProperty("command.npc.info.1", ChatColor.GRAY + "NPC Item: %1");
		p.setProperty("command.npc.info.2", ChatColor.GRAY + "NPC Type: %2");
		p.setProperty("command.npc.info.3", ChatColor.GRAY + "NPC Name: %3");
		p.setProperty("command.npc.info.4", ChatColor.GRAY + "Skin URL: %4");
		
		p.setProperty("command.npc.item.noItem", ChatColor.RED + "You must be holding an item to do that!");
		p.setProperty("command.npc.item.notSupported", ChatColor.RED + "That NPC cannot have a custom item!");
		p.setProperty("command.npc.item.success", ChatColor.GREEN + "That NPC's item has been changed successfully!");
		
		
		p.setProperty("command.npc.name.notSupported", ChatColor.RED + "That NPC cannot have a custom name!");
		p.setProperty("command.npc.name.success", ChatColor.GREEN + "That NPC's name has been changed to '%1'!");
		
		p.setProperty("command.npc.remove.success", ChatColor.GREEN + "That NPC has been deleted!");
		
		p.setProperty("command.npc.rep.invalidPrice", ChatColor.RED + "Invalid price entered!");
		p.setProperty("command.npc.rep.noItem", ChatColor.RED + "You must be holding an item to do that!");
		p.setProperty("command.npc.rep.notSupported", ChatColor.RED + "That NPC is not a blacksmith!");
		p.setProperty("command.npc.rep.success", ChatColor.GREEN + "That item now costs %1 to repair!");
		
		p.setProperty("command.npc.skin.noSpout", ChatColor.RED + "NOTE: To see this change, you must first install SpoutCraft");
		p.setProperty("command.npc.skin.notSupported", ChatColor.RED + "That NPC cannot be given a custom skin!");
		p.setProperty("command.npc.skin.success", ChatColor.GREEN + "That NPC's skin has been changed successfully!");
		
		p.setProperty("command.npc.use", ChatColor.YELLOW + "/npc {bank|bupgrade|blacksmith|static|remove|despawnall|skin|item|name|rep}");
		p.setProperty("command.npc.use.item", ChatColor.YELLOW + "/npc item <id>");
		p.setProperty("command.npc.use.name", ChatColor.YELLOW + "/npc name <id> <name>");
		p.setProperty("command.npc.use.remove", ChatColor.YELLOW + "/npc remove <id>");
		p.setProperty("command.npc.use.rep", ChatColor.YELLOW + "/npc rep <id> <cost>");
		p.setProperty("command.npc.use.skin", ChatColor.YELLOW + "/npc skin <id> <skin>");
		p.setProperty("command.npc.use.static", ChatColor.YELLOW + "/npc static <name> [skin]");
		
		// OFFLINE
		p.setProperty("command.offline.other", ChatColor.GREEN + "%1 is now in \"offline\" mode.");
		p.setProperty("command.offline.otherAlready", ChatColor.RED + "%1 is already in \"offline\" mode!");
		p.setProperty("command.offline.self", ChatColor.GREEN + "You are now in \"offline\" mode.");
		p.setProperty("command.offline.selfAlready", ChatColor.RED + "You are already in \"offline\" mode!");
		p.setProperty("command.offline.use", ChatColor.YELLOW + "/offline [user]");
		
		// ONLINE
		p.setProperty("command.online.other", ChatColor.GREEN + "%1 is no longer in \"offline\" mode.");
		p.setProperty("command.online.otherAlready", ChatColor.RED + "%1 is not in \"offline\" mode!");
		p.setProperty("command.online.self", ChatColor.GREEN + "You are no longer in \"offline\" mode.");
		p.setProperty("command.online.selfAlready", ChatColor.RED + "You are not in \"offline\" mode!");
		p.setProperty("command.online.use", ChatColor.YELLOW + "/online [user]");
		
		// POOF
		p.setProperty("command.poof.otherMonitor", ChatColor.RED + "You can't unpoof another user while they're monitoring a user!");
		p.setProperty("command.poof.otherOffline", ChatColor.RED + "You can't unpoof another user while they're offline!");
		p.setProperty("command.poof.poof", ChatColor.GREEN + "POOF!");
		p.setProperty("command.poof.poofOther", ChatColor.GREEN + "%1 is now poofed!");
		p.setProperty("command.poof.selfMonitor", ChatColor.RED + "You can't unpoof while you're monitoring a user!");
		p.setProperty("command.poof.selfOffline", ChatColor.RED + "You can't unpoof while you're offline!");
		p.setProperty("command.poof.unpoof", ChatColor.GREEN + "REVERSE POOF!");
		p.setProperty("command.poof.unpoofOther", ChatColor.GREEN + "%1 is no longer poofed!");
		p.setProperty("command.poof.use", ChatColor.YELLOW + "/poof [user]");
		
		// SETSPAWN
		p.setProperty("command.setspawn.setSuccess", ChatColor.GREEN + "The spawn location for the world '%1' has been set here.");
		
		// SPAWNER
		p.setProperty("command.spawner.invalidMob", ChatColor.RED + "That mob type is not recognized!");
		p.setProperty("command.spawner.invalidSpawner", ChatColor.RED + "That is not a spawner! Make sure nothing is in the way!");
		p.setProperty("command.spawner.setSuccess", ChatColor.GREEN + "This spawner now spawns %1s.");
		
		// SPAWNMOB
		p.setProperty("command.spawnmob.invalidAmount", ChatColor.RED + "Invalid amount inputted!");
		p.setProperty("command.spawnmob.spawnMsg", ChatColor.GREEN + "%1 mobs spawned!");
		p.setProperty("command.spawnmob.use", ChatColor.YELLOW + "/spawnmob <mob> [amount]");
		
		// TELL
		p.setProperty("command.tell.message", ChatColor.GRAY + "(%1" + ChatColor.GRAY + " => %2" + ChatColor.GRAY + ") %3");
		p.setProperty("command.tell.muted", ChatColor.RED + "You cannot use /tell while you are muted!");
		p.setProperty("command.tell.tellSelf", ChatColor.RED + "Are you trying to talk to yourself? Weirdo...");
		p.setProperty("command.tell.use", ChatColor.YELLOW + "/tell <player> <message>");
		p.setProperty("command.tell.you", ChatColor.BLUE + "You");
		
		p.setProperty("command.tell.ignore.other", ChatColor.RED + "That user is ignoring you...");
		p.setProperty("command.tell.ignore.self", ChatColor.RED + "You are ignoring that user...");
		
		// TIME
		p.setProperty("command.time.invalidTime", ChatColor.RED + "Invalid time inputted!");
		p.setProperty("command.time.lock", ChatColor.GREEN + "Time in world '%1' has been locked!");
		p.setProperty("command.time.lockAll", ChatColor.GREEN + "Time in all worlds has been locked!");
		p.setProperty("command.time.setAllSuccess", ChatColor.GREEN + "Time in all worlds has been set to %1.");
		p.setProperty("command.time.setSuccess", ChatColor.GREEN + "Time in world '%1' has been set to %2.");
		p.setProperty("command.time.unlock", ChatColor.GREEN + "Time in world '%1' has been unlocked!");
		p.setProperty("command.time.unlockAll", ChatColor.GREEN + "Time in all worlds has been unlocked!");
		p.setProperty("command.time.use", ChatColor.YELLOW + "/time {sunrise|dawn|day|noon|sunset|dusk|night|midnight|set <time>|lock|unlock");
		
		// UNIGNORE
		p.setProperty("command.unignore.notIgnoring", ChatColor.RED + "You aren't ignoring that user!");
		p.setProperty("command.unignore.success", ChatColor.GREEN + "You are no longer ignoring %1!");
		
		// UNL
		p.setProperty("command.unl.invalid", ChatColor.RED + "Invalid item ID or damage inputted!");
		p.setProperty("command.unl.notDispenser", ChatColor.RED + "You aren't pointing at a dispenser!");
		p.setProperty("command.unl.success", ChatColor.GREEN + "That dispenser is now an unlimited dispenser!");
		p.setProperty("command.unl.use", ChatColor.YELLOW + "/unl <id>[:<damage>]");
		
		// WRITE
		p.setProperty("command.write.notAllowed", ChatColor.RED + "You're not allowed to do that here!");
		p.setProperty("command.write.notAShelf", ChatColor.RED + "You're not pointing at a bookshelf!");
		p.setProperty("command.write.success", ChatColor.GREEN + "That bookshelf has been written to successfully!");
		
		// Kick messages
		p.setProperty("kick.banPerm", "You are banned from this server... FOREVER!");
		p.setProperty("kick.banTemp", "You are still banned from this server for %1");
		p.setProperty("kick.full", "The server is full!");
		p.setProperty("kick.kickTimeout", "You cannot connect for %1 more minutes");
		p.setProperty("kick.reloadFull", "The server ran out of user slots when reloading... :(");
		p.setProperty("kick.whitelist", "You aren't whitelisted on this server!");
		
		// Logging messages
		p.setProperty("log.bank.downgradeOther", "%1 has downgraded %2's bank");
		p.setProperty("log.bank.downgradeSelf", "%1 has downgraded their bank");
		p.setProperty("log.bank.openOther", "%1 has opened %2's bank");
		p.setProperty("log.bank.openSelf", "%1 has opened their bank");
		p.setProperty("log.bank.upgradeOther", "%1 has upgraded %2's bank");
		p.setProperty("log.bank.upgradeSelf", "%1 has upgraded their bank");
		
		p.setProperty("log.basic.permissionFail", "%1 attempted to use command: %2");
		p.setProperty("log.basic.permissionFailLocation", "at (%1, %2, %3) in world '%4'");
		
		p.setProperty("log.bencmd.fupdate", "%1 is forcing the server to update");
		p.setProperty("log.bencmd.reload", "%1 has reloaded the BenCmd configuration");
		p.setProperty("log.bencmd.update", "%1 is attempting to update the server");
		
		p.setProperty("log.clrinv.self", "%1 has cleared their inventory");
		p.setProperty("log.clrinv.other", "%1 has cleared %2's inventory");
		
		p.setProperty("log.cr.otherOff", "%1 has put %2 into survival mode");
		p.setProperty("log.cr.otherOn", "%1 has put %2 into creative mode");
		p.setProperty("log.cr.selfOff", "%1 has put themself into survival mode");
		p.setProperty("log.cr.selfOn", "%1 has put themself into creative mode");
		
		p.setProperty("log.display.success", "%1 is now imitating %2");
		
		p.setProperty("log.feed.self", "%1 has fed themself");
		p.setProperty("log.feed.other", "%1 has fed %2");
		
		p.setProperty("log.god.selfOff", "%1 has turned off god mode");
		p.setProperty("log.god.selfOn", "%1 has turned on god mode");
		p.setProperty("log.god.otherOff", "%1 has turned off %2's god mode");
		p.setProperty("log.god.otherOn", "%1 has turned on %2's god mode");
		
		p.setProperty("log.heal.self", "%1 has healed themself");
		p.setProperty("log.heal.other", "%1 has healed %2");
		
		p.setProperty("log.inv.look", "%1 is looking at %2's inventory");
		
		p.setProperty("log.item.other", "%1 sent %2 an item (Item: %3:%4, Amount: %5)");
		p.setProperty("log.item.self", "%1 spawned an item (Item: %2:%3, Amount: %4)");
		
		p.setProperty("log.kill.self", "%1 has killed themself");
		p.setProperty("log.kill.other", "%1 has killed %2");
		
		p.setProperty("log.killmobs.success", "%1 has killed %2 mobs in world '%3'");
		
		p.setProperty("log.kit.other", "%1 sent %2 a kit (%3)");
		p.setProperty("log.kit.self", "%1 spawned a kit (%2)");
		
		p.setProperty("log.level.self", "%1 has set their total experience to %2");
		p.setProperty("log.level.other", "%1 has set %2's total experience to %3");
		
		p.setProperty("log.lever.day", "%1 has set a lever at (%2, %3, %4) in world '%5' to activate during the day");
		p.setProperty("log.lever.night", "%1 has set a lever at (%2, %3, %4) in world '%5' to activate during the night");
		p.setProperty("log.lever.none", "%1 has set a lever at (%2, %3, %4) in world '%5' to stop activating based on time");
		
		p.setProperty("log.npc.create", "%1 has created an NPC of type '%2' (ID: %3) at (%4, %5, %6) in world '%7'");
		p.setProperty("log.npc.create.static", "%1 has created an NPC of type '%2' (ID: %3, Name: '%4', Skin: '%5') at (%6, %7, %8) in world '%9'");
		p.setProperty("log.npc.delete", "%1 has deleted an NPC (ID: %2)");
		p.setProperty("log.npc.despawnall", "%1 has despawned all NPCs");
		p.setProperty("log.npc.item", "%1 has set an NPC (ID: %2) to hold %3");
		p.setProperty("log.npc.name", "%1 has set the name of an NPC (ID: %2) to '%3'");
		p.setProperty("log.npc.rep", "%1 has set the price of an NPC repair (ID: %2, Item: %3) to %4");
		p.setProperty("log.npc.skin", "%1 has set the skin of an NPC (ID: %2) to '%3'");
		
		p.setProperty("log.setspawn.set", "%1 has set the spawn of world '%2' to (%3, %4, %5)");
		
		p.setProperty("log.spawner.set", "%1 has set a spawner at (%2, %3, %4) in world '%5' to spawn %6s");
		
		p.setProperty("log.spawnmob.spawnMsg", "%1 has spawned %2 mobs of type %3 at (%4, %5, %6) in world '%7'");
		
		p.setProperty("log.tell.message", "(%1 => %2) %3");
		
		p.setProperty("log.time.lock", "%1 has locked time in world '%2'");
		p.setProperty("log.time.lockAll", "Time in all worlds has been locked");
		p.setProperty("log.time.set", "%1 has set time in world '%2' to %3");
		p.setProperty("log.time.setAll", "Time in all worlds has been set to %1");
		p.setProperty("log.time.unlock", "%1 has unlocked time in world '%2'");
		p.setProperty("log.time.unlockAll", "Time in all worlds has been unlocked");
		
		p.setProperty("log.update.badUrl", "Encountered bad URL while checking for updates! Are you running a bootleg copy?");
		p.setProperty("log.update.check", "Checking for BenCmd updates...");
		p.setProperty("log.update.checkFail", "Failed to check for BenCmd updates:");
		p.setProperty("log.update.failLine1", "BenCmd failed to update:");
		p.setProperty("log.update.failLine2", "BenCmd may be in an unstable state! You are advised to try downloading BenCmd manually...");
		p.setProperty("log.update.nag", "A new BenCmd update is available! Use \"bencmd update\" to update your server...");
		p.setProperty("log.update.openConnection", "Opening connection to update URL...");
		p.setProperty("log.update.start", "BenCmd update in progress...");
		p.setProperty("log.update.upToDate", "BenCmd is up to date...");
		
		p.setProperty("log.write.success", "%1 has written on a shelf at (%2, %3, %4) in world '%5'. Message: %6");
		
		// Miscellaneous messages
		p.setProperty("misc.area.noEnterGroup", ChatColor.RED + "You do not have permission to enter this area!");
		p.setProperty("misc.area.noEnterTime", ChatColor.RED + "You must wait before you can re-enter this area!");
		
		p.setProperty("misc.channel.defaultMotd", "Change this message using /channel motd <message>");
		p.setProperty("misc.channel.join", "%1" + ChatColor.YELLOW + " has joined the chat");
		p.setProperty("misc.channel.kicked", ChatColor.RED + "You have been kicked from '%1'!");
		p.setProperty("misc.channel.leave", "%2" + ChatColor.YELLOW + " has left the chat");
		p.setProperty("misc.channel.list", ChatColor.YELLOW + "The following users are chatting in '%1':");
		p.setProperty("misc.channel.motd", ChatColor.YELLOW + "MOTD: %1");
		p.setProperty("misc.channel.noConnect", ChatColor.RED + "You do not have permission to enter '%1'!");
		
		p.setProperty("misc.channel.connect", ChatColor.GREEN + "You are now in '%1'");
		p.setProperty("misc.channel.connect.spy", ChatColor.GREEN + "You are now spying on '%1'!");
		
		p.setProperty("misc.channel.disconnect", ChatColor.GREEN + "You are no longer in '%1'");
		p.setProperty("misc.channel.disconnect.spy", ChatColor.GREEN + "You are no longer spying on '%1'!");
		
		p.setProperty("misc.channel.noConnect.ban", ChatColor.RED + "You are currently banned from '%1'!");
		p.setProperty("misc.channel.noConnect.spy", ChatColor.RED + "You aren't allowed to spy on '%1'!");
		
		p.setProperty("misc.channel.note.muted", ChatColor.GRAY + "NOTE: You cannot speak in this channel");
		p.setProperty("misc.channel.note.paused", ChatColor.GRAY + "NOTE: Pause mode is on in this channel");
		p.setProperty("misc.channel.note.slow", ChatColor.GRAY + "NOTE: Slow mode is on in this channel");
		
		p.setProperty("misc.channel.noTalk.block", ChatColor.RED + "You used a blocked word!");
		p.setProperty("misc.channel.noTalk.caps", ChatColor.RED + "You cannot send messages in all-caps!");
		p.setProperty("misc.channel.noTalk.muted", ChatColor.RED + "You cannot speak in this channel!");
		p.setProperty("misc.channel.noTalk.paused", ChatColor.RED + "You cannot speak while this channel is in slow mode!");
		p.setProperty("misc.channel.noTalk.slow", ChatColor.RED + "You must wait %1 seconds before you can send another message!");
		
		p.setProperty("misc.channel.pause.off", ChatColor.YELLOW + "Pause mode has been disabled");
		p.setProperty("misc.channel.pause.on", ChatColor.YELLOW + "Pause mode has been enabled");
		
		p.setProperty("misc.channel.role.banned", ChatColor.RED + "You are now banned from '%1'");
		p.setProperty("misc.channel.role.coowner", ChatColor.GREEN + "You are now a co-owner of '%1'");
		p.setProperty("misc.channel.role.mod", ChatColor.GREEN + "You are now a moderator in '%1'");
		p.setProperty("misc.channel.role.muted", ChatColor.RED + "You are now muted in '%1'");
		p.setProperty("misc.channel.role.owner", ChatColor.GREEN + "You are now the owner of '%1'");
		p.setProperty("misc.channel.role.vip", ChatColor.GREEN + "You are now a VIP in '%1'");
		
		p.setProperty("misc.channel.slow.off", ChatColor.YELLOW + "Slow mode has been disabled");
		p.setProperty("misc.channel.slow.on", ChatColor.YELLOW + "Slow mode has been enabled (%1s)");
		
		p.setProperty("misc.disp.alert", ChatColor.RED + "ALERT: The chest you have opened is a disposal chest! Anything you put inside will be lost FOREVER!");
		p.setProperty("misc.disp.cannotDestroy", ChatColor.RED + "You're not allowed to destory disposal chests!");
		p.setProperty("misc.disp.destroy", ChatColor.GREEN + "You destroyed a disposal chest!");
		
		p.setProperty("misc.grave.adminSmash", ChatColor.RED + "Your grave has been crushed by an admin, taking your items along with it...");
		p.setProperty("misc.grave.cannotDestroy", ChatColor.RED + "You cannot destroy another user's grave!");
		p.setProperty("misc.grave.crumble", ChatColor.RED + "Your grave has crumbled into dust, taking your items along with it...");
		p.setProperty("misc.grave.crumbleWarning", ChatColor.RED + "Your grave will crumble in %1");
		p.setProperty("misc.grave.death", ChatColor.RED + "You have died... You can retrieve your items by breaking your gravestone.");
		p.setProperty("misc.grave.finalSeconds", ChatColor.RED + "%1...");
		p.setProperty("misc.grave.minute", "one minute");
		p.setProperty("misc.grave.minutes", "%1 minutes");
		p.setProperty("misc.grave.seconds", "%1 seconds");
		p.setProperty("misc.grave.success", ChatColor.GREEN + "You've reached your grave in time and your items are safe!");
		
		p.setProperty("misc.lot.corner1", ChatColor.LIGHT_PURPLE + "Corner 1 set at (%1, %2, %3)");
		p.setProperty("misc.lot.corner2", ChatColor.LIGHT_PURPLE + "Corner 2 set at (%1, %2, %3)");
		
		p.setProperty("misc.portal.noEnd", ChatColor.RED + "That portal doesn't lead anywhere!");
		p.setProperty("misc.portal.noHome", ChatColor.RED + "This portal leads to your home #%1, but you haven't set it!");
		p.setProperty("misc.portal.noPermission", ChatColor.RED + "You don't have permission to use this portal!");
		
		p.setProperty("misc.protect.destroy.log", "%1 destroyed %2's protected block at (%3, %4, %5) in world '%6'");
		p.setProperty("misc.protect.destroy.msg", ChatColor.GREEN + "The protection on that block has been removed!");
		p.setProperty("misc.protect.logUse", "%1 has used %2's protected block. (ID: %3)");
		p.setProperty("misc.protect.noDestroy.1", ChatColor.RED + "That block is protected from being destroyed!");
		p.setProperty("misc.protect.noDestroy.2", ChatColor.RED + "For more information, use /protect info!");
		p.setProperty("misc.protect.noUse.1", ChatColor.RED + "That block is protected from being used!");
		p.setProperty("misc.protect.noUse.2", ChatColor.RED + "For more information, use /protect info!");
		
		p.setProperty("misc.shelf.read", ChatColor.YELLOW + "The books on this shelf read:");
		
		p.setProperty("misc.sign.head", "%1 has placed a sign at (%2, %3, %4) in world '%5'");
		p.setProperty("misc.sign.line", "Line %1: %2");
		
		p.setProperty("misc.sign.spy.normal", ChatColor.GRAY + "%1 has placed a sign: %2");
		p.setProperty("misc.sign.spy.spTitle", "Sign by %1");
		
		p.setProperty("misc.talk.muted", ChatColor.RED + "You cannot talk because you are globally muted!");
		p.setProperty("misc.talk.noChannel", ChatColor.RED + "You must be in a channel to talk!");
		
		p.setProperty("misc.unl.cannotDestroy", ChatColor.RED + "You're not allowed to destory unlimited dispensers!");
		p.setProperty("misc.unl.destroy", ChatColor.GREEN + "You destroyed an unlimited dispenser!");
		
		p.setProperty("misc.warp.dieBack", ChatColor.RED + "Use /back to return to your death point...");
		
		// NPC Messages
		p.setProperty("npc.bank.adminNoUse", ChatColor.RED + "Admins cannot use this NPC to upgrade banks, use /bank upgrade instead!");
		p.setProperty("npc.bank.managerName", "Bank Manager");
		p.setProperty("npc.bank.name", "Banker");
		
		p.setProperty("npc.blacksmith.addToolRepair", ChatColor.GREEN + "That tool can now be repaired!");
		p.setProperty("npc.blacksmith.addArmorRepair", ChatColor.GREEN + "That armor can now be repaired!");
		p.setProperty("npc.blacksmith.cannotRepair", ChatColor.RED + "This blacksmith can't repair that item!");
		p.setProperty("npc.blacksmith.fullRepair", ChatColor.RED + "That item cannot be repaired further!");
		p.setProperty("npc.blacksmith.help", ChatColor.YELLOW + "Right-click with a tool or armor in your hand to have it repaired.");
		p.setProperty("npc.blacksmith.invalidItem", ChatColor.RED + "That item cannot be repaired!");
		p.setProperty("npc.blacksmith.name", "Blacksmith");
		p.setProperty("npc.blacksmith.noToolRepair", ChatColor.RED + "That tool is not set as repairable...");
		p.setProperty("npc.blacksmith.noArmorRepair", ChatColor.RED + "That armor is not set as repairable...");
		p.setProperty("npc.blacksmith.remToolRepair", ChatColor.GREEN + "That tool can no longer be repaired!");
		p.setProperty("npc.blacksmith.remToolRepair", ChatColor.GREEN + "That armor can no longer be repaired!");
		p.setProperty("npc.blacksmith.repairSuccess", ChatColor.GREEN + "That item has been repaired!");
		p.setProperty("npc.blackmsith.updateToolRepair", ChatColor.GREEN + "That tool's price has been updated!");
		p.setProperty("npc.blackmsith.updateToolRepair", ChatColor.GREEN + "That armor's price has been updated!");
		
		p.setProperty("npc.unnamed.name", "Unnamed");
		
		try {
			p.store(new FileOutputStream(new File(BenCmd.propDir + "lang/en-US.lang")), "");
		} catch (Exception e) {
			BenCmd.log(Level.SEVERE, "Failed to save default strings to language 'en-US'!");
			BenCmd.log(e);
		}
		return p;
	}
}
