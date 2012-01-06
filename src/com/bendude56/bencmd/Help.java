package com.bendude56.bencmd;

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
				new BCommand("user",
						"Controls user-specific permissions.",
						"/user <name> add,/user <name> delete,/user <name> +/-<permission>",
						"bencmd.editpermissions",
						"Controls user commands. First one adds a new user to the database, the second deletes a user from the database, and the last one adds or removes permissions to a user."),
				new BCommand("group",
						"Controls group-specific permissions.",
						"/group <name> <add/delete>,/group <name> +/-<permission>,/group <name> <adduser/remuser> <player>,/group <name> c:<color>,/group <name> p:<prefix>,/group <name> <addgroup/remgroup> <group>",
						"bencmd.editpermissions",
						"These commands handle groups. The first adds or removes a group, the second adds or removes a permission to a group, the third adds or removes a user to/from the group. The fourth will change a group's color, and the fifth changes the group's prefix. The last one adds or removes groups to the group so that the second group inherits the first group's permissions."),
				new BCommand("status",
						"Gets general info on a player.",
						"/status;/status <player>",
						";bencmd.action.status.other",
						"This command can be performed by anyone to see things, like if they've been reported or if they are in god mode.;This command will show you the status of another player. the permission \"bencmd.action.status.advanced\" will allow you to see more detailed statuses on other players."),
				new BCommand("kick",
						"Kicks a user from the server.",
						"/kick <player>",
						"bencmd.action.kick.normal",
						"This will kick a player from the server. There is an automatic timer set in the BenCmd properties that will keep them from rejoining for a set amount of time."),
				new BCommand("mute",
						"Mutes a user, preventing them from talking or using /tell.",
						"/mute <player>;/mute <player> <duration>",
						"bencmd.action.permamute;bencmd.action.mute",
						"This will prevent a player from chatting or using /tell.;You can add a timer to automatically unmute the player. Example: \"/mute steve 3h\" will mute player \"Steve\" for 3 hours."),
				new BCommand("unmute",
						"Unmutes a player.",
						"/unmute <player>",
						"bencmd.action.unmute",
						"This will unmote a player so the can chat and use /tell again."),
				new BCommand("jail",
						"Sends a player to the pre-defined jail, and revokes their permissions.",
						"/jail <player>;/jail <player> <duration>",
						"bencmd.action.permajail;bencmd.action.jail",
						"This will send a player to the pre-defined jail and will revoke their permissions.;Add a time to auto-unjail them when the time is up. Example: \"/jail steve 3h\" will jail \"Steve\" for 3 hours."),
				new BCommand("unjail",
						"Sends a jailed player back to spawn and restores their permissions.",
						"/unjail <player>",
						"bencmd.action.unjail",
						"Sends the jailed player back to spawn and restores their permissions."),
				new BCommand("setjail",
						"Sets the jail location.",
						"/setjail",
						"bencmd.action.setjail",
						"Sets the jail location to where you're standing."),
				new BCommand("ban",
						"Kicks a user and prevents them from rejoining.",
						"/ban <player>;/ban <player> <duration>",
						"bencmd.action.permaban;bencmd.action.ban",
						"Bans a user permanently unless the /unban command is used.;You can add a time limit to automatically unban someone. Example: \"/ban steve 4d\"to ban \"Steve\" for 4 days."),
				new BCommand("unban",
						"Unbans a player.",
						"/unban <player>",
						"bencmd.action.unban",
						"This will unban a player."),
				new BCommand("protect",
						"Allows fine grained control over protections.",
						"/protect add,/lock;/protect remove,/protect remove <id>,/unlock;/protect public,/public;/protect info,/protect info <id>",
						"bencmd.lock.create;bencmd.lock.remove;bencmd.lock.public;bencmd.lock.info",
						"This command will create a new protection on the block you are pointing at.;This will remove the protection on the block you're pointing at. Add a protection ID to unlock a block you are not pointing at.;This will protect blocks from being locked by someone else, but anyone can use the block.;This will give info on a protected block, like it's ID and it's owner.;The permission \"bencmd.lock.edit\" will give access to all /protect commands."),
				new BCommand("lock",
						"Protects a chest, furnace, door, etc.",
						"/lock;/lock <player>",
						"bencmd.lock.create;bencmd.lock.edit",
						"Locks a block so nobody else can touch it.;Locks the block for the specified user."),
				new BCommand("public",
						"Locks a block publicly.",
						"/public;/public <player>",
						"bencmd.lock.public;bencmd.lock.edit",
						"Locks a block so anyone can use it, but no one else can lock it."),
				new BCommand("unlock",
						"Unlocks a block.",
						"/unlock",
						"",
						"Removes the protection from the block you are pointing at."),
				new BCommand("share",
						"Shares a locked block with another player.",
						"/share <name>",
						"",
						"Grants another player access to the block."),
				new BCommand("unshare",
						"Stops sharing the block from the player.",
						"/unshare",
						"",
						"Revokes another player's access to the block."),
				new BCommand("report",
						"Reports a player to the server admins.",
						"/report <player> <reason... ...>",
						"bencmd.ticket.send",
						"Submits a ticket to the admins about another player. The \"reaons\" can be as long as necessary and CAN include spaces."),
				new BCommand("ticket",
						"Performs other ticket functions.",
						"/ticket <list>,/ticket <list> [page];/ticket <number>;/ticket <number> [close|reopen|addinfo];/ticket <number> lock;/ticket search <player>;/ticket asearch <player>;/ticket <number> inv",
						"bencmd.ticket.list;bencmd.ticket.readall;bencmd.ticket.editall;bencmd.ticket.lock;bencmd.ticket.search;bencmd.ticket.asearch;bencmd.ticket.investigate",
						"Lists the first page of open tickets, with unread ones shown in red. The list can grow to be very long, so append a page number to view different pages.;View details on a specific ticket, including who submitted it, who was reported, and what they were reported for.;Type \"close\" to close an open ticket. \"reopen\" will open a closed ticket. \"addinfo\" allows you add info to an existing ticket.;Locks a ticket so that it cannot be edited. CANNOT BE UNLOCKED!;Searches unlocked tickets, closed or open, for the player you've specified.;Searches ALL tickets, locked or unlocked, for the player you've specified.;Marks your name as the investigator so that other admins can know that the ticket is being handled."),
				new BCommand("warp",
						"Warps you to a pre-defined point.",
						"/warp <warp>;/warp <warp> <player>",
						"bencmd.warp.self;bencmd.warp.other",
						"Teleports the player to a pre-set warp. Replace \"<warp>\" with the actual name of the warp.;Teleports another player to a preset warp. Replace \"<player\" with the actual name of the player ot be teleported."),
				new BCommand("setwarp",
						"Sets a warp point at your current location.",
						"/setwarp <warp>",
						"bencmd.warp.set",
						"Sets a new warp with the name you specify at the location you are standing and facing."),
				new BCommand("delwarp",
						"Deletes an existing warp point.",
						"/delwarp <warp>",
						"bencmd.warp.remove",
						"Deletes the specified warp point completely."),
				new BCommand("home",
						"Sends you to one of your home warps.",
						"/home,/home <number>",
						"bencmd.home.self",
						"Teleports you to a preset home you or an admin set up. Add a home number to teleport to mulitple homes."),
				new BCommand("sethome",
						"Sets one of your home warps.",
						"/sethome,/sethome <number>",
						"bencmd.home.set",
						"Sets yourself a home warp that saves your exact location and direction you are facing. Add a number to set different homes."),
				new BCommand("delhome",
						"Deletes one of your home warps.",
						"/delhome,/delhome <number>",
						"bencmd.home.remove",
						"Deletes your first home warp. Add a number to delete other home warps you have set."),
				new BCommand("back",
						"Sends you back to the place you were at before teleporting.",
						"/back",
						"bencmd.warp.back",
						"Teleports you back to the place you were before you were teleported."),
				new BCommand("tp",
						"Warps you to another player.",
						"/tp <player>;/tp <player1> <player2>",
						"bencmd.tp.self;bencmd.tp.other",
						"Teleports you to the other player.;Teleports player1 to player2, similar to the vanilla Minecraft command."),
				new BCommand("tphere",
						"Warps another player to you.",
						"/tphere <player>",
						"bencmd.tp.other",
						"Teleports another player to your location."),
				new BCommand("setportal",
						"Links a portal to a pre-set warp.",
						"/setportal <warp>",
						"bencmd.portal.set",
						"Redirects a Nether portals to other locations."),
				new BCommand("remportal",
						"Unlinks a portal from its warp.",
						"/remportal",
						"bencmd.portal.remove",
						"Disconnects a portal from it's warp."),
				new BCommand("storm",
						"Controls the current game weather.",
						"/storm <off|rain|thunder>",
						"bencmd.storm.control",
						"Changes the current world's weather to sunny, rainy (snowy), or stormy (respectively)."),
				new BCommand("strike",
						"Strikes lightning where you're pointing.",
						"/strike;/strike bind",
						"bencmd.storm.strike.location;bencmd.storm.strike.player;bencmd.storm.strike.bind",
						"Strikes the ground where you are pointing.;Strikes a specific player, wherever they may be.;Binds the /strike command to the current tool so that you can right-click to cast lightning!"),
				new BCommand("cr",
						"Toggles creative and survival modes for a player.",
						"/cr;/cr <player>",
						"bencmd.creative.self;bencmd.creative.other",
						"Changes your own gamemode to creative mode, or survival mode.;Changes the gamemode of another player to creative mode, or survival mode."),
				new BCommand("monitor",
						"Allows you to see what another player sees.",
						"/monitor <player|none>",
						"bencmd.monitor",
						"Allows you to monitor another player, showing you the point of view that they have. It works by teleporting you to their location every time they move and making you two invisable to eachother so that you do not keep pushing eachother. You must be nearby for this to work, or you may experience a little bit of glitching. Type \"/monitor none\" to stop monitoring the player."),
				new BCommand("record",
						"Returns the records of a particular player.",
						"/record <player>,/record <player> <page>",
						"",
						"Gives you the background check on a player. This lists all kicks, mutes, jails, and bans, including when it happened, and how long it lasted (for temporary jails and mutes, etc.). These records are PERMANENT, so even messing around with friends will go on their permanent record.")
				);
	}
	
	public static List<BCommand> Commands(User u) {
		List<BCommand> cmds = Commands();
		for (BCommand cmd : cmds) {
			if (!cmd.canUse(u)) {
				cmds.remove(cmd);
			}
		}
		return cmds;
	}
	
	public static void ShowHelp(String c, User u) {
		for (BCommand cmd : Commands()) {
			if (cmd.getLabel().equalsIgnoreCase(c)) {
				u.sendMessage(pre + ChatColor.GREEN + "/" + cmd.getLabel() + " - " + ChatColor.GRAY + cmd.getGist());
				String uses="",perms="";
				
				for (String perm : cmd.getPermssions()) {
					if (perms != "") perms += ", ";
					perms += perm;
				}
				
				for (String s : cmd.getAllUses()) {
					if (uses != "") uses += ", ";
					uses += s;
				}
				
				if (uses == "") uses = "None.";
				if (perms == "") perms = "None.";
				u.sendMessage(pre + ChatColor.GRAY + "Uses: " + uses);
				u.sendMessage(pre + ChatColor.GRAY + "Permissions: " + perms);
				return;
			}
		}
		u.sendMessage(ChatColor.RED + "That command does not exist or does not have any documentation.");
	}
	
	public static void ShowDetails(String c, User u) {
		for (BCommand cmd : Commands()) {
			if (cmd.getLabel().equalsIgnoreCase(c)) {
				
				Boolean noPerms = true;
				for (int i=0 ; i < cmd.maxIndex() ; i++) {
					if (i < cmd.getPermssions().length) {
						
						if (u.hasPerm(cmd.getPermssions()[i]) || cmd.getPermssions()[i] == "") {
							noPerms = false;
							
							if (cmd.getUses(i) != null) {
								String uses = "";
								for (String s : cmd.getUses(i)) {
									if (uses != "") uses += ", ";
									uses += s;
								}
								u.sendMessage(pre + ChatColor.GREEN + uses);
							}
							
							if (cmd.getPermssions()[i] == "") {
								u.sendMessage(pre + ChatColor.DARK_GREEN + "No permissions required.");
							} else {
								u.sendMessage(pre + ChatColor.DARK_GREEN + cmd.getPermssions()[i]);
							}
							
							if (i < cmd.getInstructions().length) {
								if (cmd.getInstructions()[i] == "") {
									u.sendMessage(pre + ChatColor.GRAY + "There are no instructions associated with this command.");
								} else {
									u.sendMessage(pre + ChatColor.GRAY + cmd.getInstructions()[i]);
								}
							} else {
								u.sendMessage(pre + ChatColor.GRAY + "There are no instructions associated with this command.");
							}
						}
					} else {
						noPerms = false;
						if (cmd.getUses(i) != null) {
							String uses = "";
							for (String s : cmd.getUses(i)) {
								if (uses != "") uses += ", ";
								uses += s;
							}
							u.sendMessage(pre + ChatColor.GREEN + uses);
						}
						
						if (i < cmd.getInstructions().length) {
							if (cmd.getInstructions()[i] == "") {
								u.sendMessage(pre + ChatColor.GRAY + "There are no instructions associated with this command.");
							} else {
								u.sendMessage(pre + ChatColor.GRAY + cmd.getInstructions()[i]);
							}
						} else {
							u.sendMessage(pre + ChatColor.GRAY + "There are no instructions associated with this command.");
						}
					}
				}
				if (noPerms) {
					u.sendMessage(ChatColor.RED + "You do not have any permissions for this command!");
				}
				return;
			}
		}
		u.sendMessage(ChatColor.RED + "That command does not exist or does not have any documentation.");
	}
	
	private static String pre = ChatColor.YELLOW + "|  " + ChatColor.WHITE;
	
	public static class BCommand {
		private String label;
		private String gist;
		private String[] uses;
		private String[] perms;
		private String[] instructions;

		public BCommand(String label, String gist,
				String uses, String perms, String instructions) {
			this.label = label;
			this.gist = gist;
			this.uses = uses.split(";");
			if (perms == "") {
				this.perms = null;
			} else {
				this.perms = perms.split(";");
			}
			this.instructions = instructions.split(";");
		}

		public String getLabel() {
			return label;
		}

		public String getGist() {
			return gist;
		}

		public String[] getUses(int index) {
			if (index < uses.length) {
				return (uses[index].split(","));
			}
			return null;
		}
		
		public String[] getAllUses() {
			int length = 0, index = 0, subindex = 0;
			for (String use : uses) {
				length += use.split(",").length;
			}
			String[] list = new String[length];
			for (int i = 0 ; i < length ; i++) {
				if (subindex < uses[index].split(",").length) {
					list[i] = uses[index].split(",")[subindex];
				} else {
					subindex = 0;
					index++;
				}
			}
			return list;
		}
		
		public String[] getPermssions() {
			return perms;
		}
		
		public String[] getInstructions() { 
			return instructions;
		}
		
		public boolean canUse(User u) {
			for (String p : perms) {
				if (u.hasPerm(p)) {
					return true;
				}
			}
			return false;
		}
		
		public int maxIndex() {
			if (uses.length > perms.length && uses.length > instructions.length) {
				return uses.length;
			} else if (perms.length > instructions.length) {
				return perms.length;
			} else {
				return instructions.length;
			}
		}
	}

}
