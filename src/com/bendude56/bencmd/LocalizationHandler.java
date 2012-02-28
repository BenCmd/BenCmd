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
		p.setProperty("basic.noPermission", ChatColor.RED + "You don't have permission to do that!");
		p.setProperty("basic.noServerUse", ChatColor.RED + "The server can't do that!");
		p.setProperty("basic.updateFail", ChatColor.RED + "A BenCmd update failed to download properly! Some features may cease to function...");
		p.setProperty("basic.updateInProgress", ChatColor.RED + "BenCmd is updating... Some features may reset after it is updated...");
		p.setProperty("basic.updateNag", ChatColor.RED + "A new BenCmd update was detected! Use \"/bencmd update\" to update your server...");
		p.setProperty("basic.use", ChatColor.YELLOW + "Proper usage is:");
		p.setProperty("basic.userNotFound", ChatColor.RED + "Couldn't find user '%1'!");
		p.setProperty("basic.worldNotFound", ChatColor.RED + "Couldn't find world '%1'!");
		
		// Command specific messages
		p.setProperty("command.bank.admin.use", ChatColor.YELLOW + "/bank [{downgrade|upgrade|<name> [{downgrade|upgrade}]}]");
		p.setProperty("command.bank.normal.use", ChatColor.YELLOW + "/bank [{downgrade|upgrade}]");
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
		
		p.setProperty("command.bencmd.noUpdates", ChatColor.RED + "There are no BenCmd updates available... Use /bencmd fupdate to force an update.");
		p.setProperty("command.bencmd.reloadSuccess", ChatColor.GREEN + "BenCmd config reloaded!");
		p.setProperty("command.bencmd.version", ChatColor.YELLOW + "This server is running BenCmd v" + ChatColor.GREEN + "%1" + ChatColor.YELLOW + "!");
		
		p.setProperty("command.channel.coowner.use", ChatColor.YELLOW + "/channel {join|spy|unspy|list|info|leave|kick|ban|mute|normal|vip|mod|slow|pause|motd|default|alwaysslow|slowdelay}");
		// p.setProperty("command.channel., value)
		p.setProperty("command.channel.mod.use", ChatColor.YELLOW + "/channel {join|spy|unspy|list|info|leave|kick|ban|mute|normal|slow|pause}");
		p.setProperty("command.channel.normal.use", ChatColor.YELLOW + "/channel {join|spy|unspy|list|leave}");
		p.setProperty("command.channel.outside.use", ChatColor.YELLOW + "/channel {join|spy|unspy|list|add}");
		p.setProperty("command.channel.owner.use", ChatColor.YELLOW + "/channel {join|spy|unspy|list|info|leave|remove|kick|ban|mute|normal|vip|mod|coown|own|slow|pause|motd|default|rename|alwaysslow|slowdelay}");
		
		p.setProperty("command.cr.otherOff", ChatColor.GREEN + "%1 is now in survival mode!");
		p.setProperty("command.cr.otherOn", ChatColor.GREEN + "%1 is now in creative mode!");
		p.setProperty("command.cr.selfOff", ChatColor.GREEN + "You are now in survival mode!");
		p.setProperty("command.cr.selfOn", ChatColor.GREEN + "You are now in creative mode!");
		
		p.setProperty("command.feed.self", ChatColor.GREEN + "You have been fed.");
		p.setProperty("command.feed.other", ChatColor.GREEN + "You have fed %1.");
		p.setProperty("command.feed.use", ChatColor.YELLOW + "/feed [player]");
		
		p.setProperty("command.god.otherOff", ChatColor.GREEN + "%1 is no longer in god mode.");
		p.setProperty("command.god.otherOn", ChatColor.GREEN + "%1 is now in god mode.");
		p.setProperty("command.god.protected", ChatColor.RED + "%1 is protected from being godded/ungodded!");
		p.setProperty("command.god.selfOff", ChatColor.GREEN + "You are no longer in god mode.");
		p.setProperty("command.god.selfOn", ChatColor.GREEN + "You are now in god mode.");
		p.setProperty("command.god.use", ChatColor.YELLOW + "/god [player]");
		
		p.setProperty("command.heal.other", ChatColor.GREEN + "You have healed %1!");
		p.setProperty("command.heal.self", ChatColor.GREEN + "You have been healed!");
		p.setProperty("command.heal.use", ChatColor.YELLOW + "/heal [player]");
		
		p.setProperty("command.kill.otherGod", ChatColor.RED + "You can't kill %1 while they're godded!");
		p.setProperty("command.kill.protected", ChatColor.RED + "%1 is protected from being killed!");
		p.setProperty("command.kill.selfGod", ChatColor.RED + "You can't kill yourself while you're godded!");
		p.setProperty("command.kill.use", ChatColor.YELLOW + "/kill [player]");
		
		p.setProperty("command.killmobs.noMobs" , ChatColor.RED + "No mobs killed!");
		p.setProperty("command.killmobs.success", ChatColor.GREEN + "%1 mobs killed!");
		p.setProperty("command.killmobs.unrecognizedMob", ChatColor.RED + "Unrecognized mob type: %1");
		p.setProperty("command.killmobs.use", ChatColor.YELLOW + "/killmobs <mob> ... [radius]");
		
		p.setProperty("command.level.invalidExp", ChatColor.RED + "Invalid experience inputted!");
		p.setProperty("command.level.other", ChatColor.GREEN + "You have set %1's total experience to %2.");
		p.setProperty("command.level.self", ChatColor.GREEN + "Your total experience has been set to %1.");
		p.setProperty("command.level.use", ChatColor.YELLOW + "/level <exp> [player]");
		
		p.setProperty("command.lever.notALever", ChatColor.RED + "The block you are pointing at is not a lever!");
		p.setProperty("command.lever.notTimed", ChatColor.RED + "That lever is not time-controlled!");
		p.setProperty("command.lever.success.day", ChatColor.GREEN + "That lever will now activate during the day!");
		p.setProperty("command.lever.success.night", ChatColor.GREEN + "That lever will now activate during the night!");
		p.setProperty("command.lever.success.none", ChatColor.GREEN + "That lever is no longer time controlled!");
		p.setProperty("command.lever.use", ChatColor.YELLOW + "/lever {day|night|none}");
		
		p.setProperty("command.npc.banker", "Banker");
		p.setProperty("command.npc.bankManager", "Bank Manager");
		p.setProperty("command.npc.blacksmith", "Blacksmith");
		p.setProperty("command.npc.created", ChatColor.GREEN + "NPC of type '%1' successfully created!");
		p.setProperty("command.npc.despawnall", ChatColor.GREEN + "All NPCs have been despawned!");
		p.setProperty("command.npc.item.noItem", ChatColor.RED + "You must be holding an item to do that!");
		p.setProperty("command.npc.item.notSupported", ChatColor.RED + "That NPC cannot have a custom item!");
		p.setProperty("command.npc.item.success", ChatColor.GREEN + "That NPC's item has been changed successfully!");
		p.setProperty("command.npc.item.use", ChatColor.YELLOW + "/npc item <id>");
		p.setProperty("command.npc.name.notSupported", ChatColor.RED + "That NPC cannot have a custom name!");
		p.setProperty("command.npc.name.success", ChatColor.GREEN + "That NPC's name has been changed to '%1'!");
		p.setProperty("command.npc.name.use", ChatColor.YELLOW + "/npc name <id> <name>");
		p.setProperty("command.npc.npcNotFound", ChatColor.RED + "No NPC with that ID exists!");
		p.setProperty("command.npc.remove.success", ChatColor.GREEN + "That NPC has been deleted!");
		p.setProperty("command.npc.remove.use", ChatColor.YELLOW + "/npc remove <id>");
		p.setProperty("command.npc.rep.invalidPrice", ChatColor.RED + "Invalid price entered!");
		p.setProperty("command.npc.rep.noItem", ChatColor.RED + "You must be holding an item to do that!");
		p.setProperty("command.npc.rep.notSupported", ChatColor.RED + "That NPC is not a blacksmith!");
		p.setProperty("command.npc.rep.success", ChatColor.GREEN + "That item now costs %1 to repair!");
		p.setProperty("command.npc.rep.use", ChatColor.YELLOW + "/npc rep <id> <cost>");
		p.setProperty("command.npc.skin.noSpout", ChatColor.RED + "NOTE: To see this change, you must first install SpoutCraft");
		p.setProperty("command.npc.skin.notSupported", ChatColor.RED + "That NPC cannot be given a custom skin!");
		p.setProperty("command.npc.skin.success", ChatColor.GREEN + "That NPC's skin has been changed successfully!");
		p.setProperty("command.npc.skin.use", ChatColor.YELLOW + "/npc skin <id> <skin>");
		p.setProperty("command.npc.static", "Static");
		p.setProperty("command.npc.static.use", ChatColor.YELLOW + "/npc static <name> [skin]");
		p.setProperty("command.npc.use", ChatColor.YELLOW + "/npc {bank|bupgrade|blacksmith|static|remove|despawnall|skin|item|name|rep}");
		p.setProperty("command.npc.useTip", ChatColor.YELLOW + "TIP: Right-click an NPC with a stick to get info about that NPC");
		
		p.setProperty("command.setspawn.setSuccess", ChatColor.GREEN + "The spawn location for the world '%1' has been set here.");
		
		p.setProperty("command.spawner.invalidMob", ChatColor.RED + "That mob type is not recognized!");
		p.setProperty("command.spawner.invalidSpawner", ChatColor.RED + "That is not a spawner! Make sure nothing is in the way!");
		p.setProperty("command.spawner.setSuccess", ChatColor.GREEN + "This spawner now spawns %1s.");
		
		p.setProperty("command.spawnmob.invalidAmount", ChatColor.RED + "Invalid amount inputted!");
		p.setProperty("command.spawnmob.spawnMsg", ChatColor.GREEN + "%1 mobs spawned!");
		p.setProperty("command.spawnmob.use", ChatColor.YELLOW + "/spawnmob <mob> [amount]");
		
		p.setProperty("command.time.invalidTime", ChatColor.RED + "Invalid time inputted!");
		p.setProperty("command.time.lock", ChatColor.GREEN + "Time in world '%1' has been locked!");
		p.setProperty("command.time.lockAll", ChatColor.GREEN + "Time in all worlds has been locked!");
		p.setProperty("command.time.setAllSuccess", ChatColor.GREEN + "Time in all worlds has been set to %1.");
		p.setProperty("command.time.setSuccess", ChatColor.GREEN + "Time in world '%1' has been set to %2.");
		p.setProperty("command.time.unlock", ChatColor.GREEN + "Time in world '%1' has been unlocked!");
		p.setProperty("command.time.unlockAll", ChatColor.GREEN + "Time in all worlds has been unlocked!");
		p.setProperty("command.time.use", ChatColor.YELLOW + "/time {sunrise|dawn|day|noon|sunset|dusk|night|midnight|set <time>|lock|unlock");
		
		// Kick messages
		p.setProperty("kick.reloadFull", "The server ran out of user slots when reloading... :(");
		
		// Logging messages
		p.setProperty("log.bank.downgradeOther", "%1 has downgraded %2's bank");
		p.setProperty("log.bank.downgradeSelf", "%1 has downgraded their bank");
		p.setProperty("log.bank.openOther", "%1 has opened %2's bank");
		p.setProperty("log.bank.openSelf", "%1 has opened their bank");
		p.setProperty("log.bank.upgradeOther", "%1 has upgraded %2's bank");
		p.setProperty("log.bank.upgradeSelf", "%1 has upgraded their bank");
		
		p.setProperty("log.basic.permissionFail", "%1 attempted to use command: %2");
		p.setProperty("log.basic.permissionFailLocation", "at (%1, %2, %3) in world '%4'");
		
		p.setProperty("log.bencmd.fupdate", "%1 is forcing the server to update...");
		p.setProperty("log.bencmd.reload", "%1 has reloaded the BenCmd configuration!");
		p.setProperty("log.bencmd.update", "%1 is attempting to update the server...");
		
		p.setProperty("log.cr.otherOff", "%1 has put %2 into survival mode!");
		p.setProperty("log.cr.otherOn", "%1 has put %2 into creative mode!");
		p.setProperty("log.cr.selfOff", "%1 has put themself into survival mode!");
		p.setProperty("log.cr.selfOn", "%1 has put themself into creative mode!");
		
		p.setProperty("log.feed.self", "%1 has fed themself");
		p.setProperty("log.feed.other", "%1 has fed %2");
		
		p.setProperty("log.god.selfOff", "%1 has turned off god mode");
		p.setProperty("log.god.selfOn", "%1 has turned on god mode");
		p.setProperty("log.god.otherOff", "%1 has turned off %2's god mode");
		p.setProperty("log.god.otherOn", "%1 has turned on %2's god mode");
		
		p.setProperty("log.heal.self", "%1 has healed themself");
		p.setProperty("log.heal.other", "%1 has healed %2");
		
		p.setProperty("log.kill.self", "%1 has killed themself");
		p.setProperty("log.kill.other", "%1 has killed %2");
		
		p.setProperty("log.killmobs.success", "%1 has killed %2 mobs in world '%3'");
		
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