package com.bendude56.bencmd;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;

public class Help {
	
	public static List<BCommand> ListCommands() {
		return Arrays.asList(
				new BCommand("/time", "Changes or locks the current time of day in the game", "bencmd.time.set,bencmd.time.lock"),
				new BCommand("/spawn", (BenCmd.getMainProperties().getBoolean("perWorldSpawn", false)) ? "Sends you back to the current world's spawn point" : "Sends you back to the default world's spawn point", "bencmd.spawn.normal"),
				new BCommand("/god", "Allows the use of god mode, making the player invincible", "bencmd.god.self"),
				new BCommand("/heal", "Heals a player from all damage and starvation", "bencmd.heal.self"),
				new BCommand("/bencmd", "Provides general information on the installation of BenCmd on this server", "."),
				new BCommand("/setspawn", "Sets the current world's spawn point", "bencmd.spawn.set"),
				new BCommand("/help", "Displays the BenCmd command help", "."),
				new BCommand("/kill", "Kills a player", "bencmd.kill.self"),
				new BCommand("/spawnmob", "Spawns mobs at your current location", "bencmd.spawnmob"),
				new BCommand("/killall", "Kills mobs of specified kinds around your current location", "bencmd.killmob"),
				new BCommand("/rechunk", "Resends a chunk if you are stuck in a chunk error", "."),
				new BCommand("/fire", "Temporarily allows fire spread around the block you're pointing at", "bencmd.fire.spread"),
				new BCommand("/nofire", "Revokes all current fire-spread areas", "bencmd.fire.spread"),
				new BCommand("/mainprop", "Changes a value in the main.properties file", "bencmd.mainedit"),
				new BCommand("/tell", "Messages another player privately", "."),
				new BCommand("/list", "Lists what players are currently online", "bencmd.chat.list"),
				new BCommand("/slow", "Slows chat, preventing users from sending messages too quickly", "[!C]bencmd.chat.slow"),
				new BCommand("/channel", "Controls channel-related functions", "[C]."),
				new BCommand("/poof", "Makes you invisible to other players", "bencmd.poof.poof"),
				new BCommand("/nopoof", "Allows you to see invisible players", "bencmd.poof.nopoof"),
				new BCommand("/offline", "Allows you to appear as being offline", "bencmd.poof.offline"),
				new BCommand("/online", "Allows you to reappear online", "bencmd.poof.offline"),
				new BCommand("/item", "Spawns a specific item", "bencmd.inv.spawn"),
				new BCommand("/clrinv", "Clears your inventory", "bencmd.inv.clr.self"),
				new BCommand("/unl", "Creates an unlimited dispenser", "bencmd.inv.unlimited.create"),
				new BCommand("/disp", "Creates a disposal chest", "bencmd.inv.disposal.create"),
				new BCommand("/kit", "Spawns a pre-defined kit", "bencmd.inv.kit"),
				new BCommand("/lot", "Creates, deletes, or edits lots", "."),
				new BCommand("/area", "Creates, deletes or edits special areas", "bencmd.area.command"),
				new BCommand("/map", "Edits the properties of a map", "bencmd.map.zoom,bencmd.map.center"),
				new BCommand("/buy", "Buys an item available on the market", "[M]."),
				new BCommand("/sell", "Sells an item onto the market", "[M]."),
				new BCommand("/price", "Gets the price of an item on the market", "[M]."),
				new BCommand("/market", "Controls the BenCmd Market", "bencmd.market.command"),
				new BCommand("/user", "Controls user-specific permissions", "bencmd.editpermissions"),
				new BCommand("/group", "Controls group-specific permissions", "bencmd.editpermissions"),
				new BCommand("/status", "Gets general info on your standing", "."),
				new BCommand("/kick", "Kicks a user, preventing them from connecting for 2 minutes", "bencmd.action.kick.normal"),
				new BCommand("/mute", "Mutes a user, preventing them from talking or using /tell", "bencmd.action.mute"),
				new BCommand("/unmute", "Unmutes a user", "bencmd.action.unmute"),
				new BCommand("/jail", "Sends a user to the pre-defined jail, and revokes their permissions", "bencmd.action.jail"),
				new BCommand("/unjail", "Sends a jailed user back to spawn and reinstates their permissions", "bencmd.action.unjail"),
				new BCommand("/setjail", "Sets the jail location", "bencmd.action.setjail"),
				new BCommand("/ban", "Kicks a user and prevents them from rejoining", "bencmd.action.ban"),
				new BCommand("/unban", "Unbans a user, allowing them to join once more", "bencmd.action.unban"),
				new BCommand("/protect", "Allows fine grained control over protections", "."),
				new BCommand("/lock", "Locks a chest/door", "bencmd.lock.create"),
				new BCommand("/public", "Locks a chest/door publicly", "bencmd.lock.public"),
				new BCommand("/unlock", "Unlocks a chest/door", "."),
				new BCommand("/share", "Shares a locked chest/door with another player", "."),
				new BCommand("/unshare", "Stops sharing a locked chest/door with another player", "."),
				new BCommand("/report", "Reports a player to the server admins", "bencmd.ticket.send"),
				new BCommand("/ticket", "Performs other ticket functions", "."),
				new BCommand("/warp", "Warps you to a pre-defined point", "bencmd.warp.self"),
				new BCommand("/setwarp", "Sets a warp point at your current location", "bencmd.warp.set"),
				new BCommand("/delwarp", "Deletes an existing warp point", "bencmd.warp.remove"),
				new BCommand("/home", "Sends you to one of your home warps", "bencmd.home.self"),
				new BCommand("/sethome", "Sets one of your home warps", "bencmd.home.set"),
				new BCommand("/delhome", "Deletes one of your home warps", "bencmd.home.remove"),
				new BCommand("/back", "Send you back to before your last warp", "bencmd.warp.back"),
				new BCommand("/tp", "Warps you to another player", "bencmd.tp.self"),
				new BCommand("/tphere", "Warps another player to you", "bencmd.tp.other"),
				new BCommand("/setportal", "Links a portal to a warp", "bencmd.portal.set"),
				new BCommand("/remportal", "Unlinks a portal from its warp", "bencmd.portal.remove"),
				new BCommand("/storm", "Controls the current game weather", "bencmd.storm.control"),
				new BCommand("/strike", "Strikes lightning where you're pointing", "bencmd.strike.location")
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
	
	private static void Time(User u) {
		if (!u.hasPerm("bencmd.time.set") || !u.hasPerm("bencmd.time.lock")) {
			u.sendMessage(ChatColor.RED + "You're not allowed to use that command!");
			return;
		}
		u.sendMessage(ChatColor.GREEN + "DESCRIPTION: " + ChatColor.WHITE + "Allows the user to change and/or freeze time");
		u.sendMessage(ChatColor.WHITE + "in the current world.");
		u.sendMessage(ChatColor.GREEN + "ALLOWED USAGES: ");
		if (u.hasPerm("bencmd.time.set")) {
			u.sendMessage(ChatColor.WHITE + "/time {<alias>|set <time>}");
		}
		if (u.hasPerm("bencmd.time.lock")) {
			u.sendMessage(ChatColor.WHITE + "/time lock");
		}
		if (u.hasPerm("bencmd.editpermissions")) {
			u.sendMessage(ChatColor.GREEN + "PERMISSIONS:");
			u.sendMessage(ChatColor.WHITE + "bencmd.time.set");
			u.sendMessage(ChatColor.WHITE + "bencmd.time.lock");
		}
	}
	
	private static void Spawn(User u) {
		if (!u.hasPerm("bencmd.spawn.self")) {
			u.sendMessage(ChatColor.RED + "You're not allowed to use that command!");
			return;
		}
		u.sendMessage(ChatColor.GREEN + "DESCRIPTION: " + ChatColor.WHITE + "Teleports the user back to the spawn point");
		u.sendMessage(ChatColor.WHITE + "of the current or default world.");
		u.sendMessage(ChatColor.GREEN + "ALLOWED USAGES: ");
		u.sendMessage(ChatColor.WHITE + "/spawn");
		if (u.hasPerm("bencmd.spawn.all")) {
			u.sendMessage(ChatColor.WHITE + "/spawn <world>");
		}
		if (u.hasPerm("bencmd.editpermissions")) {
			u.sendMessage(ChatColor.GREEN + "PERMISSIONS:");
			u.sendMessage(ChatColor.WHITE + "bencmd.spawn.normal");
			u.sendMessage(ChatColor.GRAY + "bencmd.spawn.all");
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
		private String commandLabel;
		private String commandDescription;
		private String neededPermission;

		public BCommand(String commandLabel, String commandDescription,
				String neededPermission) {
			this.commandLabel = commandLabel;
			this.commandDescription = commandDescription;
			this.neededPermission = neededPermission;
		}

		public String getName() {
			return commandLabel;
		}

		public String getDescription() {
			return commandDescription;
		}

		public boolean canUse(User user) {
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
		}
	}
}
