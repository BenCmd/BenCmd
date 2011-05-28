package ben_dude56.plugins.bencmd.chat.channels;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ben_dude56.plugins.bencmd.BenCmd;
import ben_dude56.plugins.bencmd.Commands;
import ben_dude56.plugins.bencmd.User;
import ben_dude56.plugins.bencmd.permissions.PermissionUser;

public class ChatChannelCommands implements Commands {
	private BenCmd plugin;
	
	public ChatChannelCommands(BenCmd instance) {
		plugin = instance;
	}
	
	public boolean onCommand(CommandSender sender, Command command,
			String commandLabel, String[] args) {
		User user;
		try {
			user = new User(plugin, (Player) sender);
		} catch (ClassCastException e) {
			user = new User(plugin);
		}
		if (!plugin.mainProperties.getBoolean("channelsEnabled", false)) {
			return false;
		}
		if(commandLabel.equalsIgnoreCase("channel")) {
			Channel(args, user);
			return true;
		}
		return false;
	}
	
	public void Channel(String[] args, User user) {
		if(args.length == 0) {
			if(user.inChannel()) {
				if(user.getActiveChannel().isOwner(user)) {
					user.sendMessage(ChatColor.YELLOW
							+ "Proper use is /channel {spy|unspy|list|leave|remove|mute|kick|ban|guest|slow|pause|motd|giveto|mod}");
				} else if (user.getActiveChannel().isMod(user)) {
					user.sendMessage(ChatColor.YELLOW
							+ "Proper use is /channel {spy|unspy|list|leave|mute|kick|ban|guest|slow|pause|motd}");
				} else {
					user.sendMessage(ChatColor.YELLOW
							+ "Proper use is /channel {spy|unspy|list|leave}");
				}
			} else {
				user.sendMessage(ChatColor.YELLOW
						+ "Proper use is /channel {join|spy|list|add|remove}");
			}
		} else {
			if(args[0].equalsIgnoreCase("leave")) {
				if(!user.inChannel()) {
					user.sendMessage(ChatColor.RED + "You aren't in a channel!");
				} else {
					user.leaveChannel();
				}
			} else if(args[0].equalsIgnoreCase("list")) {
				if(!user.inChannel()) {
					if(args.length == 1) {
						plugin.channels.listChannels(user);
					} else {
						ChatChannel channel = plugin.channels.getChannel(args[1]);
						if(channel == null) {
							user.sendMessage(ChatColor.RED + "That chat channel doesn't exist!");
						} else {
							if(channel.isMod(user) || channel.isOwner(user)) {
								channel.listUsers(user);
							} else {
								user.sendMessage(ChatColor.RED + "You must be a mod in that channel to do that!");
							}
						}
					}
				} else {
					if(args.length == 1) {
						user.getActiveChannel().listUsers(user);
					} else {
						ChatChannel channel = plugin.channels.getChannel(args[1]);
						if(channel == null) {
							user.sendMessage(ChatColor.RED + "That chat channel doesn't exist!");
						} else {
							if(channel.isMod(user) || channel.isOwner(user) || (channel.isOnline(user) != null)) {
								channel.listUsers(user);
							} else {
								user.sendMessage(ChatColor.RED + "You must be a mod in that channel to do that!");
							}
						}
					}
				}
			} else if(args[0].equalsIgnoreCase("mute")) {
				if(args.length != 2) {
					user.sendMessage(ChatColor.YELLOW
							+ "Proper use is /channel mute <player>");
					return;
				}
				if(user.inChannel()) {
					if(user.getActiveChannel().isMod(user) || user.getActiveChannel().isOwner(user)) {
						PermissionUser mutee = PermissionUser.matchUserIgnoreCase(args[1], plugin);
						if(mutee != null) {
							if(user.getActiveChannel().isMod(user) && user.getActiveChannel().isMod(mutee)) {
								user.sendMessage(ChatColor.RED + "You can't mute another mod!");
								return;
							}
							user.getActiveChannel().Mute(mutee);
						} else {
							user.sendMessage(ChatColor.RED + "That user couldn't be found!");
						}
					} else {
						user.sendMessage(ChatColor.RED + "You must be a mod to do that!");
					}
				} else {
					user.sendMessage(ChatColor.RED + "You're not in a channel!");
				}
			} else if(args[0].equalsIgnoreCase("kick")) {
				if(args.length != 2) {
					user.sendMessage(ChatColor.YELLOW
							+ "Proper use is /channel kick <player>");
					return;
				}
				if(user.inChannel()) {
					if(user.getActiveChannel().isMod(user) || user.getActiveChannel().isOwner(user)) {
						User kickee = user.getActiveChannel().isOnline(PermissionUser.matchUserIgnoreCase(args[1], plugin));
						if(kickee != null) {
							if(user.getActiveChannel().isMod(user) && user.getActiveChannel().isMod(kickee)) {
								user.sendMessage(ChatColor.RED + "You can't kick another mod!");
								return;
							}
							user.getActiveChannel().Kick(kickee);
						} else {
							user.sendMessage(ChatColor.RED + "That user couldn't be found!");
						}
					} else {
						user.sendMessage(ChatColor.RED + "You must be a mod to do that!");
					}
				} else {
					user.sendMessage(ChatColor.RED + "You're not in a channel!");
				}
			} else if(args[0].equalsIgnoreCase("remove")) {
				//TODO For version 1.1.1: Allow dynamic adding/removing of chat channels.
			} else if(args[0].equalsIgnoreCase("ban")) {
				if(args.length != 2) {
					user.sendMessage(ChatColor.YELLOW
							+ "Proper use is /channel ban <player>");
					return;
				}
				if(user.inChannel()) {
					if(user.getActiveChannel().isMod(user) || user.getActiveChannel().isOwner(user)) {
						PermissionUser banee = PermissionUser.matchUserIgnoreCase(args[1], plugin);
						if(banee != null) {
							if(user.getActiveChannel().isMod(user) && user.getActiveChannel().isMod(banee)) {
								user.sendMessage(ChatColor.RED + "You can't ban another mod!");
								return;
							}
							user.getActiveChannel().Ban(banee);
						} else {
							user.sendMessage(ChatColor.RED + "That user couldn't be found!");
						}
					} else {
						user.sendMessage(ChatColor.RED + "You must be a mod to do that!");
					}
				} else {
					user.sendMessage(ChatColor.RED + "You're not in a channel!");
				}
			} else if(args[0].equalsIgnoreCase("guest")) {
				if(args.length != 2) {
					user.sendMessage(ChatColor.YELLOW
							+ "Proper use is /channel guest <player>");
					return;
				}
				if(user.inChannel()) {
					if(user.getActiveChannel().isMod(user) || user.getActiveChannel().isOwner(user)) {
						PermissionUser guestee = PermissionUser.matchUserIgnoreCase(args[1], plugin);
						if(guestee != null) {
							if(user.getActiveChannel().isMod(user) && user.getActiveChannel().isMod(guestee)) {
								user.sendMessage(ChatColor.RED + "You can't guest another mod!");
								return;
							}
							user.getActiveChannel().Guest(guestee);
						} else {
							user.sendMessage(ChatColor.RED + "That user couldn't be found!");
						}
					} else {
						user.sendMessage(ChatColor.RED + "You must be a mod to do that!");
					}
				} else {
					user.sendMessage(ChatColor.RED + "You're not in a channel!");
				}
			} else if(args[0].equalsIgnoreCase("slow")) {
				if(args.length > 2) {
					user.sendMessage(ChatColor.YELLOW
							+ "Proper use is /channel slow [delay]");
					return;
				}
				if(user.inChannel()) {
					if(user.getActiveChannel().isMod(user) || user.getActiveChannel().isOwner(user)) {
						if(args.length == 1) {
							if(user.getActiveChannel().isSlow()) {
								user.getActiveChannel().disableSlow();
							} else {
								user.getActiveChannel().enableSlow();
							}
						} else {
							int millis;
							try {
								millis = Integer.parseInt(args[1]);
							} catch (NumberFormatException e) {
								user.sendMessage(ChatColor.RED + "Invalid delay!");
								return;
							}
							user.getActiveChannel().enableSlow(millis);
						}
					} else {
						user.sendMessage(ChatColor.RED + "You must be a mod to do that!");
					}
				} else {
					user.sendMessage(ChatColor.RED + "You're not in a channel!");
				}
			} else if(args[0].equalsIgnoreCase("pause")) {
				//TODO For version 1.1.1: Add pause capability to channels to allow only mods to talk
			} else if(args[0].equalsIgnoreCase("motd")) {
				if(!user.inChannel()) {
					user.sendMessage(ChatColor.RED + "You're not in a channel!");
					return;
				}
				if(args.length == 1) {
					user.sendMessage(ChatColor.YELLOW + user.getActiveChannel().getMotd());
				} else {
					if(user.getActiveChannel().isMod(user) || user.getActiveChannel().isOwner(user)) {
						for(int i = 1; i < args.length; i++) {
							
						}
					}
				}
			} else if(args[0].equalsIgnoreCase("giveto")) {
				if(args.length != 2) {
					user.sendMessage(ChatColor.YELLOW
							+ "Proper use is /channel giveto <player>");
					return;
				}
				if(user.inChannel()) {
					if(user.getActiveChannel().isOwner(user)) {
						PermissionUser owner = PermissionUser.matchUserIgnoreCase(args[1], plugin);
						if(owner != null) {
							user.getActiveChannel().changeOwner(owner);
						} else {
							user.sendMessage(ChatColor.RED + "That user couldn't be found!");
						}
					} else {
						user.sendMessage(ChatColor.RED + "You must be the channel owner to do that!");
					}
				} else {
					user.sendMessage(ChatColor.RED + "You're not in a channel!");
				}
			} else if(args[0].equalsIgnoreCase("mod")) {
				if(args.length != 2) {
					user.sendMessage(ChatColor.YELLOW
							+ "Proper use is /channel mod <player>");
					return;
				}
				if(user.inChannel()) {
					if(user.getActiveChannel().isOwner(user)) {
						PermissionUser mod = PermissionUser.matchUserIgnoreCase(args[1], plugin);
						if(mod != null) {
							user.getActiveChannel().Mod(mod);
						} else {
							user.sendMessage(ChatColor.RED + "That user couldn't be found!");
						}
					} else {
						user.sendMessage(ChatColor.RED + "You must be the channel owner to do that!");
					}
				} else {
					user.sendMessage(ChatColor.RED + "You're not in a channel!");
				}
			} else if(args[0].equalsIgnoreCase("join")) {
				if(args.length != 2) {
					user.sendMessage(ChatColor.YELLOW
							+ "Proper use is /channel join <channel>");
					return;
				}
				ChatChannel channel = plugin.channels.getChannel(args[1]);
				if(channel != null) {
					user.joinChannel(channel);
				} else {
					user.sendMessage(ChatColor.RED + "That channel couldn't be found!");
				}
			} else if(args[0].equalsIgnoreCase("spy")) {
				if(args.length != 2) {
					user.sendMessage(ChatColor.YELLOW
							+ "Proper use is /channel spy <channel>");
					return;
				}
				ChatChannel channel = plugin.channels.getChannel(args[1]);
				if(channel != null) {
					user.spyChannel(channel);
				} else {
					user.sendMessage(ChatColor.RED + "That channel couldn't be found!");
				}
			} else if(args[0].equalsIgnoreCase("unspy")) {
				if(args.length != 2) {
					user.sendMessage(ChatColor.YELLOW
							+ "Proper use is /channel unspy <channel>");
					return;
				}
				ChatChannel channel = plugin.channels.getChannel(args[1]);
				if(channel != null) {
					user.unspyChannel(channel);
				} else {
					user.sendMessage(ChatColor.RED + "That channel couldn't be found!");
				}
			} else if(args[0].equalsIgnoreCase("add")) {
				//TODO For version 1.1.1: Allow dynamic adding/removing of chat channels.
			}
		}
	}

}
