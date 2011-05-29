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
			user = User.getUser(plugin, (Player) sender);
		} catch (ClassCastException e) {
			user = User.getUser(plugin);
		}
		if (!plugin.mainProperties.getBoolean("channelsEnabled", false)) {
			return false;
		}
		if (commandLabel.equalsIgnoreCase("channel")) {
			Channel(args, user);
			return true;
		}
		return false;
	}

	public void Channel(String[] args, User user) {
		if (args.length == 0) {
			if (user.inChannel()) {
				if (user.getActiveChannel().isOwner(user)) {
					user.sendMessage(ChatColor.YELLOW
							+ "Proper use is /channel {spy|unspy|list|leave|remove|mute|kick|ban|guest|slow|pause|motd|giveto|mod|name}");
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
			if (args[0].equalsIgnoreCase("leave")) {
				if (!user.inChannel()) {
					user.sendMessage(ChatColor.RED + "You aren't in a channel!");
				} else {
					user.leaveChannel();
				}
			} else if (args[0].equalsIgnoreCase("list")) {
				if (!user.inChannel()) {
					if (args.length == 1) {
						plugin.channels.listChannels(user);
					} else {
						ChatChannel channel = plugin.channels
								.getChannel(args[1]);
						if (channel == null) {
							user.sendMessage(ChatColor.RED
									+ "That chat channel doesn't exist!");
						} else {
							if (channel.isMod(user) || channel.isOwner(user)) {
								channel.listUsers(user);
							} else {
								user.sendMessage(ChatColor.RED
										+ "You must be a mod in that channel to do that!");
							}
						}
					}
				} else {
					if (args.length == 1) {
						user.getActiveChannel().listUsers(user);
					} else {
						ChatChannel channel = plugin.channels
								.getChannel(args[1]);
						if (channel == null) {
							user.sendMessage(ChatColor.RED
									+ "That chat channel doesn't exist!");
						} else {
							if (channel.isMod(user) || channel.isOwner(user)
									|| (channel.isOnline(user) != null)) {
								channel.listUsers(user);
							} else {
								user.sendMessage(ChatColor.RED
										+ "You must be a mod in that channel to do that!");
							}
						}
					}
				}
			} else if (args[0].equalsIgnoreCase("mute")) {
				if (args.length != 2) {
					user.sendMessage(ChatColor.YELLOW
							+ "Proper use is /channel mute <player>");
					return;
				}
				if (user.inChannel()) {
					if (user.getActiveChannel().isMod(user)
							|| user.getActiveChannel().isOwner(user)) {
						PermissionUser mutee = PermissionUser
								.matchUserIgnoreCase(args[1], plugin);
						if (mutee != null) {
							if (user.getActiveChannel().isMod(user)
									&& user.getActiveChannel().isMod(mutee)) {
								user.sendMessage(ChatColor.RED
										+ "You can't mute another mod!");
								return;
							}
							user.getActiveChannel().Mute(mutee);
						} else {
							user.sendMessage(ChatColor.RED
									+ "That user couldn't be found!");
						}
					} else {
						user.sendMessage(ChatColor.RED
								+ "You must be a mod to do that!");
					}
				} else {
					user.sendMessage(ChatColor.RED + "You're not in a channel!");
				}
			} else if (args[0].equalsIgnoreCase("kick")) {
				if (args.length != 2) {
					user.sendMessage(ChatColor.YELLOW
							+ "Proper use is /channel kick <player>");
					return;
				}
				if (user.inChannel()) {
					if (user.getActiveChannel().isMod(user)
							|| user.getActiveChannel().isOwner(user)) {
						User kickee = user.getActiveChannel().isOnline(
								PermissionUser.matchUserIgnoreCase(args[1],
										plugin));
						if (kickee != null) {
							if (user.getActiveChannel().isMod(user)
									&& user.getActiveChannel().isMod(kickee)) {
								user.sendMessage(ChatColor.RED
										+ "You can't kick another mod!");
								return;
							}
							user.getActiveChannel().Kick(kickee);
						} else {
							user.sendMessage(ChatColor.RED
									+ "That user couldn't be found!");
						}
					} else {
						user.sendMessage(ChatColor.RED
								+ "You must be a mod to do that!");
					}
				} else {
					user.sendMessage(ChatColor.RED + "You're not in a channel!");
				}
			} else if (args[0].equalsIgnoreCase("remove")) {
				if(user.inChannel()) {
					if(user.getActiveChannel().isOwner(user)) {
						plugin.channels.removeChannel(user.getActiveChannel());
					} else {
						user.sendMessage(ChatColor.RED
								+ "You must be the channel owner to do that!");
					}
				} else {
					user.sendMessage(ChatColor.RED + "You're not in a channel!");
				}
			} else if (args[0].equalsIgnoreCase("ban")) {
				if (args.length != 2) {
					user.sendMessage(ChatColor.YELLOW
							+ "Proper use is /channel ban <player>");
					return;
				}
				if (user.inChannel()) {
					if (user.getActiveChannel().isMod(user)
							|| user.getActiveChannel().isOwner(user)) {
						PermissionUser banee = PermissionUser
								.matchUserIgnoreCase(args[1], plugin);
						if (banee != null) {
							if (user.getActiveChannel().isMod(user)
									&& user.getActiveChannel().isMod(banee)) {
								user.sendMessage(ChatColor.RED
										+ "You can't ban another mod!");
								return;
							}
							user.getActiveChannel().Ban(banee);
						} else {
							user.sendMessage(ChatColor.RED
									+ "That user couldn't be found!");
						}
					} else {
						user.sendMessage(ChatColor.RED
								+ "You must be a mod to do that!");
					}
				} else {
					user.sendMessage(ChatColor.RED + "You're not in a channel!");
				}
			} else if (args[0].equalsIgnoreCase("guest")) {
				if (args.length != 2) {
					user.sendMessage(ChatColor.YELLOW
							+ "Proper use is /channel guest <player>");
					return;
				}
				if (user.inChannel()) {
					if (user.getActiveChannel().isMod(user)
							|| user.getActiveChannel().isOwner(user)) {
						PermissionUser guestee = PermissionUser
								.matchUserIgnoreCase(args[1], plugin);
						if (guestee != null) {
							if (user.getActiveChannel().isMod(user)
									&& user.getActiveChannel().isMod(guestee)) {
								user.sendMessage(ChatColor.RED
										+ "You can't guest another mod!");
								return;
							}
							user.getActiveChannel().Guest(guestee);
						} else {
							user.sendMessage(ChatColor.RED
									+ "That user couldn't be found!");
						}
					} else {
						user.sendMessage(ChatColor.RED
								+ "You must be a mod to do that!");
					}
				} else {
					user.sendMessage(ChatColor.RED + "You're not in a channel!");
				}
			} else if (args[0].equalsIgnoreCase("slow")) {
				if (args.length > 2) {
					user.sendMessage(ChatColor.YELLOW
							+ "Proper use is /channel slow [delay]");
					return;
				}
				if (user.inChannel()) {
					if (user.getActiveChannel().isMod(user)
							|| user.getActiveChannel().isOwner(user)) {
						if (args.length == 1) {
							if (user.getActiveChannel().isSlow()) {
								user.getActiveChannel().disableSlow();
							} else {
								user.getActiveChannel().enableSlow();
							}
						} else {
							int millis;
							try {
								millis = Integer.parseInt(args[1]);
							} catch (NumberFormatException e) {
								user.sendMessage(ChatColor.RED
										+ "Invalid delay!");
								return;
							}
							user.getActiveChannel().enableSlow(millis);
						}
					} else {
						user.sendMessage(ChatColor.RED
								+ "You must be a mod to do that!");
					}
				} else {
					user.sendMessage(ChatColor.RED + "You're not in a channel!");
				}
			} else if (args[0].equalsIgnoreCase("pause")) {
				if (!user.inChannel()) {
					user.sendMessage(ChatColor.RED + "You're not in a channel!");
					return;
				}
				if (!user.getActiveChannel().isMod(user) && !user.getActiveChannel().isOwner(user)) {
					user.sendMessage(ChatColor.RED
							+ "You must be a mod to do that!");
					return;
				}
				if(user.getActiveChannel().isPaused()) {
					user.getActiveChannel().disablePause();
				} else {
					user.getActiveChannel().enablePause();
				}
			} else if (args[0].equalsIgnoreCase("motd")) {
				if (!user.inChannel()) {
					user.sendMessage(ChatColor.RED + "You're not in a channel!");
					return;
				}
				if (args.length == 1) {
					user.sendMessage(ChatColor.YELLOW
							+ user.getActiveChannel().getMotd());
				} else {
					if (user.getActiveChannel().isMod(user)
							|| user.getActiveChannel().isOwner(user)) {
						String newMotd = "";
						for (int i = 1; i < args.length; i++) {
							if(newMotd.isEmpty()) {
								newMotd += args[i];
							} else {
								newMotd += " " + args[i];
							}
						}
						if(newMotd.contains("`")) {
							user.sendMessage(ChatColor.RED + "MOTDs cannot contain the special character: `");
							return;
						}
						user.getActiveChannel().setMotd(newMotd);
						user.sendMessage(ChatColor.YELLOW
								+ user.getActiveChannel().getMotd());
					} else {
						user.sendMessage(ChatColor.RED
								+ "You must be a mod to do that!");
					}
				}
			} else if (args[0].equalsIgnoreCase("giveto")) {
				if (args.length != 2) {
					user.sendMessage(ChatColor.YELLOW
							+ "Proper use is /channel giveto <player>");
					return;
				}
				if (user.inChannel()) {
					if (user.getActiveChannel().isOwner(user)) {
						PermissionUser owner = PermissionUser
								.matchUserIgnoreCase(args[1], plugin);
						if (owner != null) {
							user.getActiveChannel().changeOwner(owner);
						} else {
							user.sendMessage(ChatColor.RED
									+ "That user couldn't be found!");
						}
					} else {
						user.sendMessage(ChatColor.RED
								+ "You must be the channel owner to do that!");
					}
				} else {
					user.sendMessage(ChatColor.RED + "You're not in a channel!");
				}
			} else if (args[0].equalsIgnoreCase("mod")) {
				if (args.length != 2) {
					user.sendMessage(ChatColor.YELLOW
							+ "Proper use is /channel mod <player>");
					return;
				}
				if (user.inChannel()) {
					if (user.getActiveChannel().isOwner(user)) {
						PermissionUser mod = PermissionUser
								.matchUserIgnoreCase(args[1], plugin);
						if (mod != null) {
							user.getActiveChannel().Mod(mod);
						} else {
							user.sendMessage(ChatColor.RED
									+ "That user couldn't be found!");
						}
					} else {
						user.sendMessage(ChatColor.RED
								+ "You must be the channel owner to do that!");
					}
				} else {
					user.sendMessage(ChatColor.RED + "You're not in a channel!");
				}
			} else if (args[0].equalsIgnoreCase("join")) {
				if (args.length != 2) {
					user.sendMessage(ChatColor.YELLOW
							+ "Proper use is /channel join <channel>");
					return;
				}
				ChatChannel channel = plugin.channels.getChannel(args[1]);
				if (channel != null) {
					user.joinChannel(channel);
				} else {
					user.sendMessage(ChatColor.RED
							+ "That channel couldn't be found!");
				}
			} else if (args[0].equalsIgnoreCase("spy")) {
				if (args.length != 2) {
					user.sendMessage(ChatColor.YELLOW
							+ "Proper use is /channel spy <channel>");
					return;
				}
				ChatChannel channel = plugin.channels.getChannel(args[1]);
				if (channel != null) {
					user.spyChannel(channel);
				} else {
					user.sendMessage(ChatColor.RED
							+ "That channel couldn't be found!");
				}
			} else if (args[0].equalsIgnoreCase("unspy")) {
				if (args.length != 2) {
					user.sendMessage(ChatColor.YELLOW
							+ "Proper use is /channel unspy <channel>");
					return;
				}
				ChatChannel channel = plugin.channels.getChannel(args[1]);
				if (channel != null) {
					user.unspyChannel(channel);
				} else {
					user.sendMessage(ChatColor.RED
							+ "That channel couldn't be found!");
				}
			} else if (args[0].equalsIgnoreCase("add")) {
				if(!user.hasPerm("canAddChannels")) {
					user.sendMessage(ChatColor.RED
							+ "You don't have permission to do that!");
					return;
				}
				if (args.length == 2) {
					plugin.channels.addChannel(args[1], user);
				} else {
					user.sendMessage(ChatColor.YELLOW
							+ "Proper use is /channel add <name>");
				}
			} else if (args[0].equalsIgnoreCase("name")) {
				if (!user.inChannel()) {
					user.sendMessage(ChatColor.RED + "You're not in a channel!");
					return;
				}
				if (args.length == 1) {
					user.sendMessage(ChatColor.YELLOW
							+ "Proper use is /channel name <name>");
				} else {
					if (user.getActiveChannel().isOwner(user)) {
						String newName = "";
						for (int i = 1; i < args.length; i++) {
							if(newName.isEmpty()) {
								newName += args[i];
							} else {
								newName += " " + args[i];
							}
						}
						if(newName.contains("`")) {
							user.sendMessage(ChatColor.RED + "Display namess cannot contain the special character: `");
							return;
						}
						user.getActiveChannel().setDisplayName(newName);
						user.sendMessage(ChatColor.GREEN + "The display name was successfully set.");
					} else {
						user.sendMessage(ChatColor.RED
								+ "You must be the channel owner to do that!");
					}
				}
			}
		}
	}

}
