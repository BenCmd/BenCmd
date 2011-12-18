package com.bendude56.bencmd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;

public class Help {
	
	public static List<BCommand> Commands() {
		return Arrays.asList(
				new BCommand("time",
						"Changes or locks the time of the current world",
						"/time set <time>,/time <day|night|noon|midnight|etc>;/time lock",
						"bencmd.time.set;bencmd.time.lock",
						"Just like the vanilla /time command. These commands only effect the current world you're in. You can use common names to set the world time, such as Day, Night, Midnight, etc.;This will both lock and unlock time."),
				new BCommand("spawn",
						"Teleports a player to spawn",
						"/spawn;/spawn <world>",
						"bencmd.spawn.normal;bencmd.spawn.all",
						"This will teleport you to your current world's spawn or, depending on BenCmd's settings, the default world.;This allows you to teleport to a specific world. Replace <world> with the exact name of the world you want to teleport to. This name is usually the same as the world's folder name."),
				new BCommand("god",
						"Allows you to activate/deactivate invincibility",
						"/god;/god <player>",
						"bencmd.god.self;bencmd.god.other",
						"This will toggle god (invincibility) mode for the person who uses this.;Using this allows you to activate/deactivate god (invincibility) mode on another player. Replace <player> with the player's exact name."),
				new BCommand("heal",
						"Restores a player to full health.",
						"/heal;/heal <player>",
						"bencmd.heal.self;bencmd.heal.other",
						"This will completely refill your heart meter.;To completely heal another player, simply add their name. Example: \"/heal Notch\" will heal Notch."),
				new BCommand("bencmd",
						"Accessess operational commands and info on BenCmd.",
						"/bencmd reload;/bencmd disable;/bencmd update,/bencmd fupdate",
						"bencmd.reload;bencmd.disable;bencmd.update",
						"This will refresh BenCmd's settings and files. Better than using the /reload command, as this only effects BenCmd. You can also replace \"reload\" with \"rel\";This will completely disable BenCmd, so USE WITH CAUTION;These commands can be used to automatically update BenCmd without having to touch a web browser!"),
				new BCommand("setspawn",
						"Sets the current world's spawn point to your location.",
						"/setspawn",
						"bencmd.spawn.set",
						"It's pretty simple. Just stand where you'd like to set teh world's spawn, and type /setspawn.;NOTE: This does not save the exact position you're standing, nor the direction you are looking. This is a limitation of Minecraft :("),
				new BCommand("help",
						"Displays the BenCmd command help ;)",
						"/help;/help <command>;/help <command> more",
						"",
						"This will give a simple list of available commands.;This will give you more info on a specific command. Replace <command> with the command you need to know more about.;Add the word \"more\" to read instructions. Like these!"),
				new BCommand("kill",
						"Kills a player. Mercilessly.",
						"/kill;/kill <player>",
						"bencmd.kill.self;bencmd.kill.other",
						"This will kill yourself.;This will kill someone else who is not in god mode and who is not protected from this command (usually admins)."),
				new BCommand("mob",
						"Spawns mobs at your current location.",
						"/mob <mob> <amount>,/mob <mob,mob,mob,...> <amount>",
						"bencmd.spawnmob",
						"This will spawn a mob. Example: \"/mob creeper 6\" will spawn 6 creepers at your location. You can also pile mobs on top of eachother. Example: \"/mob spider,skeleton 3\" will spawn 3 skeletons riding spiders. Not specifying an amount will spawn only one mob."),
				new BCommand("killall",
						"Kills all mobs of specified kind(s) around your location.",
						"/killall <mob>,/killall <mob> <range>,/killall <mob> <mob> <mob> ... <amount>",
						"bencmd.killmob",
						"This will kill all mobs of the specified type(s) that are within the range you specified. Example: \"/killall creepers 8\" will kill all creepers that are 8 blocks of you. \"/killall pigmen\" will kill all pigmen in the world. \"/killall creepers skeletons and spiders\" will kill all creepers, skeletons, and spiders in the world."),
				new BCommand("rechunk",
						"Usually fixes the chunk error you're in.",
						"/rechunk",
						"",
						"This will resend the chunk you are in. This usually fixes chunk errors."),
				new BCommand("fire",
						"Temporarily allows fire spread around the block you're pointing at.",
						"/fire",
						"bencmd.fire.spread",
						"This will allow fire to spread around the block you're pointing at. You can use /nofire to do the opposite."),
				new BCommand("nofire",
						"Revokes all current fire-spread areas.",
						"/nofire",
						"bencmd.fire.spread",
						"This will remove all fire-spread areas. Use /fire to manualy put each one back in."),
				new BCommand("mainprop",
						"Changes a value in the main.properties file.",
						"/mainprop <property> <value>",
						"bencmd.mainedit",
						"Use this to manully change a setting in the Main.properties file of BenCmd. For super-advanced users only!"),
				new BCommand("tell",
						"Sends a private message to another player.",
						"/tell <player> <message ... ... ...>",
						"",
						"Use this to send a private chat message to another player. Example: \"/tell Notch I really like your game!\""),
				new BCommand("list",
						"Lists all online players.",
						"/tell",
						"bencmd.chat.list",
						"Lists all online players in no particular order, including their name colors."),
				new BCommand("slow",
						"Sets a delay between messages to prevent spamming.",
						"/slow <milliseconds>",
						"bencmd.chat.slow",
						"Adds a millisecond delay between chat messages (per user). Example: \"/slow 3000\" will make each player wait 3 seconds between sending messages.;PRO TIP: The permission \"bencmd.chat.noslow\" will make a user exempt from slowmode."),
				new BCommand("channel",
						"Controls channel-related functions.",
						"/channel join <channel>,/channel leave,/channel list;/channel add <channel>;/channel delete <channel>",
						";bencmd.channel.newchannel;",
						"These are general commands that anyone can use. The first joins the specified channel, the second leave the current channel, and the last one lists all players in the current channel.;This command creates a new channel with the name you choose. (Replace <channe> with the name);This one deletes the channel you are currently in. You must be a channel admin, or the owner of the channel."),
				new BCommand("poof",
						"Makes you invisible to other players.",
						"/poof;/allpoof",
						"bencmd.poof.poof;bencmd.poof.allpoof",
						"This will toggle invisability for yourself.;This will make you invisable from everyone, including those with \"/nopoof\" on."),
				new BCommand("allpoof",
						"Makes you invisible to all other players.",
						"/allpoof;/poof",
						"bencmd.poof.allpoof;bencmd.poof.poof",
						"This will make you invisable from everyone, including those with \"/nopoof\" on.;This will toggle invisability for yourself."),
				new BCommand("nopoof",
						"Allows you to see invisible players.",
						"/nopoof",
						"bencmd.poof.nopoof",
						"This will allow you to see other invisable players."),
				new BCommand("offline",
						"Allows you to appear as being offline.",
						"/offline",
						"bencmd.poof.offline",
						"Toggles offline for you so you don't appear in chat or in the \"/list\" command."),
				new BCommand("online",
						"Allows you to reappear online.",
						"/online",
						"bencmd.poof.offline",
						"This will undo the effects of \"/offline\"."),
				new BCommand("item",
						"Spawns an item to your inventory.",
						"/item <item> <amount>,/i <item> <amount>",
						"bencmd.inv.spawn",
						"This will spawn items into your inventory. You can use the item id, or an alias set up in the items.txt file."),
				new BCommand("clrinv",
						"Wipes a player's inventory clean.",
						"/clrinv;/clrinv <player",
						"bencmd.inv.clr.self;bencmd.inv.clr.other",
						"Completely clears your inventory;Clears the inventory of whomever you choose. Replace <player> with the player's name."),
				new BCommand("unl",
						"Creates an unlimited dispenser.",
						"/unl <item>",
						"bencmd.inv.unlimited.create",
						"The dispenser you're pointing at will become an unlimited disenser and put out infinite amounts of <item>."),
				new BCommand("disp",
						"Creates a disposal chest.",
						"/disp",
						"bencmd.inv.disposal.create",
						"The chest you're pointing at will become a disposal chest so that anything put into it will be deleted."),
				new BCommand("kit",
						"Spawns a pre-defined kit into your inventory.",
						"/kit <kit>",
						"bencmd.inv.kit",
						"This feature is incomplete. Allows you to spawn a kit that was coded into the kits.db file."),
				new BCommand("lot",
						"Creates, deletes, and manages lots.",
						"/lot info,/lot list,/lot list <here|owner|guest|permission> [player];/lot set [player],/lot advset <up> <down> [player];/lot delete [id];/lot owner <player>;/lot group <group> [id];/lot guest [id] +/-<player>;/lot extend [id];/lot advextend <up> <down> [id]",
						"bencmd.lot.info;bencmd.lot.create;bencmd.lot.remove;bencmd.lot.owner;bencmd.lot.group;bencmd.lot.guest;bencmd.lot.extend",
						"Get information on the lot you are in or list the registered lots.;Set a new lot using the corners set with the wooden shovel. Up and Down are how many blocks up and down to extend your selection.;Delete the lot, including all of it's extensions.;Change the lot's owner.;Change the lot's group.;Add or remove a lot's guests.;Create an extension of an existing lot.;"),
				new BCommand("area",
						"Creates, deletes and manages special areas.",
						"/area info;/area new <pvp|msg|heal|dmg> [options];/area delete <id>",
						"bencmd.area.info;bencmd.area.create.*;bencmd.area.remove",
						"Gets info on a Special Area.;Creates a new area of the specified type.;Deletes the area with the specied ID."),
				new BCommand("map",
						"Edits the properties of a map.",
						"/map center;/map <zoomin|zoomout>",
						"bencmd.map.center;bencmd.map.zoom",
						"Centers the map you are holding to the position you are standing.;Changes the zoom level of the map you are holding."),
				new BCommand("buy",
						"Buys an item available on the market.",
						"/but <item> [amount]",
						"",
						"Let's you buy an item from the market."),
				new BCommand("sell",
						"Sells an item to the market.",
						"/sell <itm> [amount]",
						"",
						"Lets you sell an item from your inventory to the market."),
				new BCommand("price",
						"Gets the price of an item on the market.",
						"/price <item>",
						"",
						"Gets the current price of an item."),
				new BCommand("market",
						"Controls the BenCmd Market.",
						"/market open;/market close;/market update <time>,/market noupdate;/market item <item> <price>,/market currency <item> <value>;/market multiple <multiple>,/market min <min>,/market max <max>",
						"bencmd.market.open;bencmd.market.close;bencmd.market.update;bencmd.market.price;bencmd.market.properties",
						"Opens the BenCmd market for business.;Closes the BenCmd Market.;Changes the update delay / turns of market price updating. The update system handles automatic supply-demand calculating.;Changes market prices and supplies for items.;Changes market properties like the min and max prices and the incraments the update system uses."),
				new BCommand("user", "Controls user-specific permissions", "bencmd.editpermissions"),
				new BCommand("group", "Controls group-specific permissions", "bencmd.editpermissions"),
				new BCommand("status", "Gets general info on your standing", "."),
				new BCommand("kick", "Kicks a user, preventing them from connecting for 2 minutes", "bencmd.action.kick.normal"),
				new BCommand("mute", "Mutes a user, preventing them from talking or using /tell", "bencmd.action.mute"),
				new BCommand("unmute", "Unmutes a user", "bencmd.action.unmute"),
				new BCommand("jail", "Sends a user to the pre-defined jail, and revokes their permissions", "bencmd.action.jail"),
				new BCommand("unjail", "Sends a jailed user back to spawn and reinstates their permissions", "bencmd.action.unjail"),
				new BCommand("setjail", "Sets the jail location", "bencmd.action.setjail"),
				new BCommand("ban", "Kicks a user and prevents them from rejoining", "bencmd.action.ban"),
				new BCommand("unban", "Unbans a user, allowing them to join once more", "bencmd.action.unban"),
				new BCommand("protect", "Allows fine grained control over protections", "."),
				new BCommand("lock", "Locks a chest/door", "bencmd.lock.create"),
				new BCommand("public", "Locks a chest/door publicly", "bencmd.lock.public"),
				new BCommand("unlock", "Unlocks a chest/door", "."),
				new BCommand("share", "Shares a locked chest/door with another player", "."),
				new BCommand("unshare", "Stops sharing a locked chest/door with another player", "."),
				new BCommand("report", "Reports a player to the server admins", "bencmd.ticket.send"),
				new BCommand("ticket", "Performs other ticket functions", "."),
				new BCommand("warp", "Warps you to a pre-defined point", "bencmd.warp.self"),
				new BCommand("setwarp", "Sets a warp point at your current location", "bencmd.warp.set"),
				new BCommand("delwarp", "Deletes an existing warp point", "bencmd.warp.remove"),
				new BCommand("home", "Sends you to one of your home warps", "bencmd.home.self"),
				new BCommand("sethome", "Sets one of your home warps", "bencmd.home.set"),
				new BCommand("delhome", "Deletes one of your home warps", "bencmd.home.remove"),
				new BCommand("back", "Send you back to before your last warp", "bencmd.warp.back"),
				new BCommand("tp", "Warps you to another player", "bencmd.tp.self"),
				new BCommand("tphere", "Warps another player to you", "bencmd.tp.other"),
				new BCommand("setportal", "Links a portal to a warp", "bencmd.portal.set"),
				new BCommand("remportal", "Unlinks a portal from its warp", "bencmd.portal.remove"),
				new BCommand("storm", "Controls the current game weather", "bencmd.storm.control"),
				new BCommand("strike", "Strikes lightning where you're pointing", "bencmd.strike.location")
				);
	}
	
	public static void ShowHelp(String c, User u) {
		if (c.equalsIgnoreCase("time")) {
			Time(u);
		} else if (c.equalsIgnoreCase("spawn")) {
			Spawn(u);
		} else if (c.equalsIgnoreCase("god")) {
			God(u);
		} else if (c.equalsIgnoreCase("heal")) {
			Heal(u);
		} else if (c.equalsIgnoreCase("bencmd")) {
			BenCmd(u);
		} else if (c.equalsIgnoreCase("setspawn")) {
			SetSpawn(u);
		} else if (c.equalsIgnoreCase("help")) {
			Help(u);
		} else {
			u.sendMessage(ChatColor.RED + "There is no '" + c + "' command!");
		}
	}
	
	private static String pre = ChatColor.YELLOW + "|  " + ChatColor.WHITE;
	
	private static void Time(User u) {
		if (!u.hasPerm("bencmd.time.set") && !u.hasPerm("bencmd.time.lock")) {
			u.sendMessage(ChatColor.RED + "You're not allowed to use this command!");
			return;
		}
		u.sendMessage(ChatColor.GREEN + "/time : " + ChatColor.WHITE + "Change or freeze time of the current world.");
		u.sendMessage(ChatColor.GREEN + "Correct Usages:");
		if (u.hasPerm("bencmd.time.set")) {
			u.sendMessage(pre + ChatColor.WHITE + "/time set <time>");
			u.sendMessage(pre + ChatColor.WHITE + "/time <day|night|noon|midnight|etc>");
		}
		if (u.hasPerm("bencmd.time.lock")) {
			u.sendMessage(pre + ChatColor.WHITE + "/time lock");
		}
		if (u.hasPerm("bencmd.editpermissions")) {
			u.sendMessage(ChatColor.GREEN + "Required Permissions:");
			u.sendMessage(pre + ChatColor.WHITE + "bencmd.time.set");
			u.sendMessage(pre + ChatColor.WHITE + "bencmd.time.lock");
		}
	}
	
	private static void Spawn(User u) {
		if (!u.hasPerm("bencmd.spawn.self") && !u.hasPerm("bencmd.spawn.all")) {
			u.sendMessage(ChatColor.RED + "You're not allowed to use that command!");
			return;
		}
		u.sendMessage(ChatColor.GREEN + "/spawn : " + ChatColor.WHITE + "Teleports you back to spawn");
		u.sendMessage(ChatColor.GREEN + "Correct Usages: ");
		if (u.hasPerm("bencmd.spawn.self")) {
			u.sendMessage(pre + ChatColor.WHITE + "/spawn");
		}
		if (u.hasPerm("bencmd.spawn.all")) {
			u.sendMessage(pre + ChatColor.WHITE + "/spawn <world>");
		}
		if (u.hasPerm("bencmd.editpermissions")) {
			u.sendMessage(ChatColor.GREEN + "Required Permissions:");
			u.sendMessage(pre + ChatColor.WHITE + "bencmd.spawn.normal");
			u.sendMessage(pre + ChatColor.GRAY + "bencmd.spawn.all");
		}
	}
	
	private static void God(User u) {
		if (!u.hasPerm("bencmd.god.self")) {
			u.sendMessage(ChatColor.RED + "You're not allowed to use that command!");
			return;
		}
		u.sendMessage(ChatColor.GREEN + "DESCRIPTION: " + ChatColor.WHITE + "Allows a user to become invincible to all forms");
		u.sendMessage(ChatColor.WHITE + "of damage.");
		u.sendMessage(ChatColor.GREEN + "ALLOWED USAGES: ");
		u.sendMessage(ChatColor.WHITE + "/god");
		if (u.hasPerm("bencmd.god.other")) {
			u.sendMessage(ChatColor.WHITE + "/god <player>");
		}
		if (u.hasPerm("bencmd.editpermissions")) {
			u.sendMessage(ChatColor.GREEN + "PERMISSIONS:");
			u.sendMessage(ChatColor.WHITE + "bencmd.god.self");
			u.sendMessage(ChatColor.GRAY + "bencmd.god.other");
			u.sendMessage(ChatColor.DARK_GRAY + "bencmd.god.protect");
			u.sendMessage(ChatColor.DARK_GRAY + "bencmd.god.all");
		}
	}
	
	private static void Heal(User u) {
		if (!u.hasPerm("bencmd.heal.self")) {
			u.sendMessage(ChatColor.RED + "You're not allowed to use that command!");
			return;
		}
		u.sendMessage(ChatColor.GREEN + "DESCRIPTION: " + ChatColor.WHITE + "Allows a user to heal themselves or others,");
		u.sendMessage(ChatColor.WHITE + "restoring health and food to their maximum level");
		u.sendMessage(ChatColor.GREEN + "ALLOWED USAGES: ");
		u.sendMessage(ChatColor.WHITE + "/heal");
		if (u.hasPerm("bencmd.heal.other")) {
			u.sendMessage(ChatColor.WHITE + "/heal <player>");
		}
		if (u.hasPerm("bencmd.editpermissions")) {
			u.sendMessage(ChatColor.GREEN + "PERMISSIONS:");
			u.sendMessage(ChatColor.WHITE + "bencmd.heal.self");
			u.sendMessage(ChatColor.GRAY + "bencmd.heal.other");
		}
	}
	
	private static void BenCmd(User u) {
		u.sendMessage(ChatColor.GREEN + "DESCRIPTION: " + ChatColor.WHITE + "Provides information on, or performs other");
		u.sendMessage(ChatColor.WHITE + "functions related to BenCmd administration");
		u.sendMessage(ChatColor.GREEN + "ALLOWED USAGES: ");
		u.sendMessage(ChatColor.WHITE + "/bencmd version");
		if (u.hasPerm("bencmd.reload")) {
			u.sendMessage(ChatColor.WHITE + "/bencmd rel");
		}
		if (u.hasPerm("bencmd.update")) {
			u.sendMessage(ChatColor.WHITE + "/bencmd update");
			u.sendMessage(ChatColor.WHITE + "/bencmd fupdate");
		}
		if (u.hasPerm("bencmd.disable")) {
			u.sendMessage(ChatColor.WHITE + "/bencmd disable");
		}
		if (u.hasPerm("bencmd.editpermissions")) {
			u.sendMessage(ChatColor.GREEN + "PERMISSIONS:");
			u.sendMessage(ChatColor.WHITE + "(None)");
			u.sendMessage(ChatColor.GRAY + "bencmd.reload");
			u.sendMessage(ChatColor.GRAY + "bencmd.update");
			u.sendMessage(ChatColor.GRAY + "bencmd.disable");
		}
	}
	
	private static void SetSpawn(User u) {
		if (!u.hasPerm("bencmd.spawn.set")) {
			u.sendMessage(ChatColor.RED + "You're not allowed to use that command!");
			return;
		}
		u.sendMessage(ChatColor.GREEN + "DESCRIPTION: " + ChatColor.WHITE + "Sets the current world's spawn point to the");
		u.sendMessage(ChatColor.WHITE + "user's location");
		u.sendMessage(ChatColor.GREEN + "ALLOWED USAGES: ");
		u.sendMessage(ChatColor.WHITE + "/setspawn");
		if (u.hasPerm("bencmd.editpermissions")) {
			u.sendMessage(ChatColor.GREEN + "PERMISSIONS:");
			u.sendMessage(ChatColor.WHITE + "bencmd.spawn.set");
		}
	}
	
	private static void Help(User u) {
		u.sendMessage(ChatColor.GREEN + "DESCRIPTION: " + ChatColor.WHITE + "Pretty self-explanatory");
		u.sendMessage(ChatColor.GREEN + "ALLOWED USAGES: ");
		u.sendMessage(ChatColor.WHITE + "/help [page]");
		u.sendMessage(ChatColor.WHITE + "/help <command>");
		if (u.hasPerm("bencmd.editpermissions")) {
			u.sendMessage(ChatColor.GREEN + "PERMISSIONS:");
			u.sendMessage(ChatColor.WHITE + "(None)");
		}
	}
	
	private static void Kill(User u) {
		if (!u.hasPerm("bencmd.kill.self")) {
			u.sendMessage(ChatColor.RED + "You're not allowed to use that command!");
			return;
		}
		u.sendMessage(ChatColor.GREEN + "DESCRIPTION: " + ChatColor.WHITE + "Allows a user to kill themselves or");
		u.sendMessage(ChatColor.WHITE + "another user.");
		u.sendMessage(ChatColor.GREEN + "ALLOWED USAGES: ");
		u.sendMessage(ChatColor.WHITE + "/kill");
		if (u.hasPerm("bencmd.kill.other")) {
			u.sendMessage(ChatColor.WHITE + "/kill <player>");
		}
		if (u.hasPerm("bencmd.editpermissions")) {
			u.sendMessage(ChatColor.GREEN + "PERMISSIONS:");
			u.sendMessage(ChatColor.WHITE + "bencmd.kill.self");
			u.sendMessage(ChatColor.GRAY + "bencmd.kill.other");
			u.sendMessage(ChatColor.DARK_GRAY + "bencmd.kill.protect");
			u.sendMessage(ChatColor.DARK_GRAY + "bencmd.kill.all");
		}
	}
	
	public static class BCommand {
		private String label;
		private String gist;
		private String[] uses;
		private String[] perms;
		private String instructions;

		public BCommand(String label, String gist,
				String uses, String perms, String instructions) {
			this.label = label;
			this.gist = gist;
			this.uses = uses.split(";");
			this.perms = perms.split(";");
			this.instructions = instructions;
		}

		public String getName() {
			return label;
		}

		public String getGist() {
			return gist;
		}

		public void sendUses(User u) {
			/*for (int i = 0 ; i < uses.length ; i ++) {
				if (perms.length > i) {
					if (u.hasPerm(perms[i])) {
						for (String use : uses[i].split(",")) { 
							u.sendMessage(pre + use);
						}
					}
				} else {
					for (String use : uses[i].split(",")) { 
						u.sendMessage(pre + use);
					}
				}
			}*/
			for (String use : uses) {
				for (String subuse : use.split(",")) {
					u.sendMessage(pre + subuse);
				}
			}
		}
		
		public void sendPerms(User u) {
			for (String perm : perms) {
				u.sendMessage(pre + perm);
			}
		}
		
		public void sendInstructions(User u) { 
			/*for (int i = 0 ; i < instructions.split(";").length ; i ++) {
				if (perms.length > i) {
					if (u.hasPerm(perms[i])) {
						u.sendMessage(pre + instructions.split(";")[i]);
					}
				} else {
					u.sendMessage(pre + instructions.split(";")[i]);
				}
			}*/
			for (String inst : instructions.split(";")) {
				u.sendMessage(pre + inst);
			}
		}
		
		/*public boolean canUse(User user) {
			if (neededPermission.equalsIgnoreCase(".")) {
				return true;
			} else if (neededPermission.startsWith("[C]")) {
				if (BenCmd.getMainProperties().getBoolean("channelsEnabled", true)) {
					neededPermission.replaceFirst("\\[C]", "");
				} else {
					return false;
				}
			} else if (neededPermission.startsWith("[!C]")) {
				if (!BenCmd.getMainProperties().getBoolean("channelsEnabled", true)) {
					neededPermission.replaceFirst("\\[!C]", "");
				} else {
					return false;
				}
			} else if (neededPermission.startsWith("[M]")) {
				if (BenCmd.getMainProperties().getBoolean("marketOpen", false)) {
					neededPermission.replaceFirst("\\[M]", "");
				} else {
					return false;
				}
			}
			for (String p : neededPermission.split(",")) {
				if (user.hasPerm(p)) {
					return true;
				}
			}
			return false;
		}*/
	}
}
