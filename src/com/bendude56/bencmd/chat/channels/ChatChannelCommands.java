package com.bendude56.bencmd.chat.channels;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.Commands;
import com.bendude56.bencmd.User;
import com.bendude56.bencmd.permissions.PermissionUser;

public class ChatChannelCommands implements Commands {
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		User user = User.getUser(sender);
		if (!BenCmd.getMainProperties().getBoolean("channelsEnabled", true)) {
			return false;
		}
		if (commandLabel.equalsIgnoreCase("channel")) {
			if (args[0].equalsIgnoreCase("unmute")) {
				String arguments = "";
				for (int i = 1; i < args.length; i++) {
					if (i == 0) {
						arguments += args[0];
					} else {
						arguments += " " + args[i];
					}
				}
				Bukkit.dispatchCommand(sender, "channel guest " + arguments);
				return true;
			}
			Channel(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("pause")) {
			Bukkit.dispatchCommand(sender, "channel pause");
		} else if (commandLabel.equalsIgnoreCase("me")) {
			String message = "";
			for (int i = 0; i < args.length; i++) {
				if (i == 0) {
					message += args[i];
				} else {
					message += " " + args[i];
				}
			}
			if (!user.inChannel()) {
				user.sendMessage(ChatColor.RED + "You're not in a channel!");
			} else {
				user.getActiveChannel().Me(user, message);
			}
			return true;
		}
		return false;
	}

	public void Channel(String[] args, User user) {
		if (args.length == 0) {
			if (user.inChannel()) {
				if (user.getActiveChannel().isOwner(user.getName())) {
					user.sendMessage(ChatColor.YELLOW + "Proper use is /channel {spy|unspy|list|leave|remove|mute|kick|ban|guest|slow|pause|motd|giveto|mod|name}");
				} else if (user.getActiveChannel().isMod(user.getName())) {
					user.sendMessage(ChatColor.YELLOW + "Proper use is /channel {spy|unspy|list|leave|mute|kick|ban|guest|slow|pause|motd}");
				} else {
					user.sendMessage(ChatColor.YELLOW + "Proper use is /channel {spy|unspy|list|leave}");
				}
			} else {
				user.sendMessage(ChatColor.YELLOW + "Proper use is /channel {join|spy|list|add|remove}");
			}
		} else {
			if (args[0].equalsIgnoreCase("leave")) {
				if (!user.inChannel()) {
					user.sendMessage(ChatColor.RED + "You aren't in a channel!");
				} else {
					user.leaveChannel(true);
				}
			} else if (args[0].equalsIgnoreCase("list")) {
				if (!user.inChannel()) {
					if (args.length == 1) {
						BenCmd.getChatChannels().listChannels(user);
					} else {
						ChatChannel channel = BenCmd.getChatChannels().getChannel(args[1]);
						if (channel == null) {
							user.sendMessage(ChatColor.RED + "That chat channel doesn't exist!");
						} else {
							if (channel.isMod(user.getName()) || channel.isOwner(user.getName())) {
								channel.listUsers(user);
							} else {
								user.sendMessage(ChatColor.RED + "You must be a mod in that channel to do that!");
							}
						}
					}
				} else {
					if (args.length == 1) {
						user.getActiveChannel().listUsers(user);
					} else {
						ChatChannel channel = BenCmd.getChatChannels().getChannel(args[1]);
						if (channel == null) {
							user.sendMessage(ChatColor.RED + "That chat channel doesn't exist!");
						} else {
							if (channel.isMod(user.getName()) || channel.isOwner(user.getName()) || (channel.isOnline(user.getName()) != null)) {
								channel.listUsers(user);
							} else {
								user.sendMessage(ChatColor.RED + "You must be a mod in that channel to do that!");
							}
						}
					}
				}
			} else if (args[0].equalsIgnoreCase("mute")) {
				if (args.length != 2) {
					user.sendMessage(ChatColor.YELLOW + "Proper use is /channel mute <player>");
					return;
				}
				if (user.inChannel()) {
					if (user.getActiveChannel().isMod(user.getName()) || user.getActiveChannel().isOwner(user.getName())) {
						PermissionUser mutee = PermissionUser.matchUserIgnoreCase(args[1]);
						if (mutee != null) {
							if (user.getActiveChannel().isMod(user.getName()) && user.getActiveChannel().isMod(mutee.getName())) {
								user.sendMessage(ChatColor.RED + "You can't mute another mod!");
								return;
							}
							user.getActiveChannel().Mute(mutee.getName());
							user.sendMessage(mutee.getColor() + mutee.getName() + ChatColor.GREEN + "Has been muted from the " + user.getActiveChannel().getName() + " channel!");
						} else {
							user.sendMessage(ChatColor.RED + "That user couldn't be found!");
						}
					} else {
						user.sendMessage(ChatColor.RED + "You must be a mod to do that!");
					}
				} else {
					user.sendMessage(ChatColor.RED + "You're not in a channel!");
				}
			} else if (args[0].equalsIgnoreCase("kick")) {
				if (args.length != 2) {
					user.sendMessage(ChatColor.YELLOW + "Proper use is /channel kick <player>");
					return;
				}
				if (user.inChannel()) {
					if (user.getActiveChannel().isMod(user.getName()) || user.getActiveChannel().isOwner(user.getName())) {
						User kickee = user.getActiveChannel().isOnline(args[1]);
						if (kickee != null) {
							if (user.getActiveChannel().isMod(user.getName()) && user.getActiveChannel().isMod(kickee.getName())) {
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
			} else if (args[0].equalsIgnoreCase("remove")) {
				if (user.inChannel()) {
					if (user.getActiveChannel().isOwner(user.getName())) {
						BenCmd.getChatChannels().removeChannel(user.getActiveChannel());
					} else {
						user.sendMessage(ChatColor.RED + "You must be the channel owner to do that!");
					}
				} else {
					user.sendMessage(ChatColor.RED + "You're not in a channel!");
				}
			} else if (args[0].equalsIgnoreCase("ban")) {
				if (args.length != 2) {
					user.sendMessage(ChatColor.YELLOW + "Proper use is /channel ban <player>");
					return;
				}
				if (user.inChannel()) {
					if (user.getActiveChannel().isMod(user.getName()) || user.getActiveChannel().isOwner(user.getName())) {
						PermissionUser banee = PermissionUser.matchUserIgnoreCase(args[1]);
						if (banee != null) {
							if (user.getActiveChannel().isMod(user.getName()) && user.getActiveChannel().isMod(banee.getName())) {
								user.sendMessage(ChatColor.RED + "You can't ban another mod!");
								return;
							}
							user.getActiveChannel().Ban(banee.getName());
						} else {
							user.sendMessage(ChatColor.RED + "That user couldn't be found!");
						}
					} else {
						user.sendMessage(ChatColor.RED + "You must be a mod to do that!");
					}
				} else {
					user.sendMessage(ChatColor.RED + "You're not in a channel!");
				}
			} else if (args[0].equalsIgnoreCase("guest")) {
				if (args.length != 2) {
					user.sendMessage(ChatColor.YELLOW + "Proper use is /channel guest <player>");
					return;
				}
				if (user.inChannel()) {
					if (user.getActiveChannel().isMod(user.getName()) || user.getActiveChannel().isOwner(user.getName())) {
						PermissionUser guestee = PermissionUser.matchUserIgnoreCase(args[1]);
						if (guestee != null) {
							if (user.getActiveChannel().isMod(user.getName()) && user.getActiveChannel().isMod(guestee.getName())) {
								user.sendMessage(ChatColor.RED + "You can't guest another mod!");
								return;
							}
							user.getActiveChannel().Guest(guestee.getName());
						} else {
							user.sendMessage(ChatColor.RED + "That user couldn't be found!");
						}
					} else {
						user.sendMessage(ChatColor.RED + "You must be a mod to do that!");
					}
				} else {
					user.sendMessage(ChatColor.RED + "You're not in a channel!");
				}
			} else if (args[0].equalsIgnoreCase("slow")) {
				if (args.length > 2) {
					user.sendMessage(ChatColor.YELLOW + "Proper use is /channel slow [delay]");
					return;
				}
				if (user.inChannel()) {
					if (user.getActiveChannel().isMod(user.getName()) || user.getActiveChannel().isOwner(user.getName())) {
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
			} else if (args[0].equalsIgnoreCase("pause")) {
				if (!user.inChannel()) {
					user.sendMessage(ChatColor.RED + "You're not in a channel!");
					return;
				}
				if (!user.getActiveChannel().isMod(user.getName()) && !user.getActiveChannel().isOwner(user.getName())) {
					user.sendMessage(ChatColor.RED + "You must be a mod to do that!");
					return;
				}
				if (user.getActiveChannel().isPaused()) {
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
					user.sendMessage(ChatColor.YELLOW + user.getActiveChannel().getMotd());
				} else {
					if (user.getActiveChannel().isMod(user.getName()) || user.getActiveChannel().isOwner(user.getName())) {
						String newMotd = "";
						for (int i = 1; i < args.length; i++) {
							if (newMotd.isEmpty()) {
								newMotd += args[i];
							} else {
								newMotd += " " + args[i];
							}
						}
						if (newMotd.contains("`")) {
							user.sendMessage(ChatColor.RED + "MOTDs cannot contain the special character: `");
							return;
						}
						user.getActiveChannel().setMotd(newMotd);
						user.sendMessage(ChatColor.YELLOW + user.getActiveChannel().getMotd());
					} else {
						user.sendMessage(ChatColor.RED + "You must be a mod to do that!");
					}
				}
			} else if (args[0].equalsIgnoreCase("giveto")) {
				if (args.length != 2) {
					user.sendMessage(ChatColor.YELLOW + "Proper use is /channel giveto <player>");
					return;
				}
				if (user.inChannel()) {
					if (user.getActiveChannel().isOwner(user.getName())) {
						PermissionUser owner = PermissionUser.matchUserIgnoreCase(args[1]);
						if (owner != null) {
							user.getActiveChannel().changeOwner(owner.getName());
						} else {
							user.sendMessage(ChatColor.RED + "That user couldn't be found!");
						}
					} else {
						user.sendMessage(ChatColor.RED + "You must be the channel owner to do that!");
					}
				} else {
					user.sendMessage(ChatColor.RED + "You're not in a channel!");
				}
			} else if (args[0].equalsIgnoreCase("mod")) {
				if (args.length != 2) {
					user.sendMessage(ChatColor.YELLOW + "Proper use is /channel mod <player>");
					return;
				}
				if (user.inChannel()) {
					if (user.getActiveChannel().isOwner(user.getName())) {
						PermissionUser mod = PermissionUser.matchUserIgnoreCase(args[1]);
						if (mod != null) {
							user.getActiveChannel().Mod(mod.getName());
						} else {
							user.sendMessage(ChatColor.RED + "That user couldn't be found!");
						}
					} else {
						user.sendMessage(ChatColor.RED + "You must be the channel owner to do that!");
					}
				} else {
					user.sendMessage(ChatColor.RED + "You're not in a channel!");
				}
			} else if (args[0].equalsIgnoreCase("join")) {
				if (args.length != 2) {
					user.sendMessage(ChatColor.YELLOW + "Proper use is /channel join <channel>");
					return;
				}
				ChatChannel channel = BenCmd.getChatChannels().getChannel(args[1]);
				if (channel != null) {
					user.joinChannel(channel, true);
				} else {
					user.sendMessage(ChatColor.RED + "That channel couldn't be found!");
				}
			} else if (args[0].equalsIgnoreCase("spy")) {
				if (args.length != 2) {
					user.sendMessage(ChatColor.YELLOW + "Proper use is /channel spy <channel>");
					return;
				}
				ChatChannel channel = BenCmd.getChatChannels().getChannel(args[1]);
				if (channel != null) {
					user.spyChannel(channel);
				} else {
					user.sendMessage(ChatColor.RED + "That channel couldn't be found!");
				}
			} else if (args[0].equalsIgnoreCase("unspy")) {
				if (args.length != 2) {
					user.sendMessage(ChatColor.YELLOW + "Proper use is /channel unspy <channel>");
					return;
				}
				ChatChannel channel = BenCmd.getChatChannels().getChannel(args[1]);
				if (channel != null) {
					user.unspyChannel(channel);
				} else {
					user.sendMessage(ChatColor.RED + "That channel couldn't be found!");
				}
			} else if (args[0].equalsIgnoreCase("add")) {
				if (!user.hasPerm("bencmd.chat.newchannel")) {
					user.sendMessage(ChatColor.RED + "You don't have permission to do that!");
					return;
				}
				if (args.length == 2) {
					BenCmd.getChatChannels().addChannel(args[1], user);
				} else {
					user.sendMessage(ChatColor.YELLOW + "Proper use is /channel add <name>");
				}
			} else if (args[0].equalsIgnoreCase("name")) {
				if (!user.inChannel()) {
					user.sendMessage(ChatColor.RED + "You're not in a channel!");
					return;
				}
				if (args.length == 1) {
					user.sendMessage(ChatColor.YELLOW + "Proper use is /channel name <name>");
				} else {
					if (user.getActiveChannel().isOwner(user.getName())) {
						String newName = "";
						for (int i = 1; i < args.length; i++) {
							if (newName.isEmpty()) {
								newName += args[i];
							} else {
								newName += " " + args[i];
							}
						}
						if (newName.contains("`")) {
							user.sendMessage(ChatColor.RED + "Display namess cannot contain the special character: `");
							return;
						}
						user.getActiveChannel().setDisplayName(newName);
						user.sendMessage(ChatColor.GREEN + "The display name was successfully set.");
					} else {
						user.sendMessage(ChatColor.RED + "You must be the channel owner to do that!");
					}
				}
			}
		}
	}

}
