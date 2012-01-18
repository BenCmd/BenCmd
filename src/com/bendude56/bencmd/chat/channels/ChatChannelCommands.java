package com.bendude56.bencmd.chat.channels;

import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.Commands;
import com.bendude56.bencmd.User;
import com.bendude56.bencmd.chat.channels.ChatChannel.ChatLevel;
import com.bendude56.bencmd.permissions.PermissionUser;

public class ChatChannelCommands implements Commands {
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		User user = User.getUser(sender);
		if (!BenCmd.getMainProperties().getBoolean("channelsEnabled", true)) {
			return false;
		}
		if (commandLabel.equalsIgnoreCase("channel")) {
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
				user.getActiveChannel().sendMe(user, message);
			}
			return true;
		}
		return false;
	}

	public void Channel(String[] args, User user) {
		if (args.length == 0) {
			if (user.inChannel()) {
				if (user.getActiveChannel().canExecuteAllCommands(user)) {
					user.sendMessage(ChatColor.YELLOW + "Proper use is /channel {join|spy|unspy|list|info|leave|remove|kick|ban|mute|normal|vip|mod|coown|own|slow|pause|motd|default|rename|alwaysslow|slowdelay}");
				} else if (user.getActiveChannel().canExecuteAdvancedCommands(user)) {
					user.sendMessage(ChatColor.YELLOW + "Proper use is /channel {join|spy|unspy|list|info|leave|kick|ban|mute|normal|vip|mod|slow|pause|motd|default|alwaysslow|slowdelay}");
				} else if (user.getActiveChannel().canExecuteBasicCommands(user)) {
					user.sendMessage(ChatColor.YELLOW + "Proper use is /channel {join|spy|unspy|list|info|leave|kick|ban|mute|normal|slow|pause}");
				} else {
					user.sendMessage(ChatColor.YELLOW + "Proper use is /channel {join|spy|unspy|list|leave}");
				}
			} else {
				user.sendMessage(ChatColor.YELLOW + "Proper use is /channel {join|spy|unspy|list|add}");
			}
		} else {
			if (args[0].equalsIgnoreCase("join")) {
				if (args.length != 2) {
					user.sendMessage(ChatColor.YELLOW + "Proper use is /channel join <channel>");
					return;
				}
				if (BenCmd.getChatChannels().channelExists(args[1])) {
					user.joinChannel(BenCmd.getChatChannels().getChannel(args[1]), true);
				} else {
					user.sendMessage(ChatColor.RED + "That channel doesn't exist!");
				}
			} else if (args[0].equalsIgnoreCase("spy")) {
				if (args.length != 2) {
					user.sendMessage(ChatColor.YELLOW + "Proper use is /channel spy <channel>");
					return;
				}
				if (BenCmd.getChatChannels().channelExists(args[1])) {
					if (BenCmd.getChatChannels().getChannel(args[1]).attemptSpy(user)) {
						user.spyChannel(BenCmd.getChatChannels().getChannel(args[1]));
					}
				} else {
					user.sendMessage(ChatColor.RED + "That channel doesn't exist!");
				}
			} else if (args[0].equalsIgnoreCase("unspy")) {
				if (args.length != 2) {
					user.sendMessage(ChatColor.YELLOW + "Proper use is /channel unspy <channel>");
					return;
				}
				if (BenCmd.getChatChannels().channelExists(args[1])) {
					BenCmd.getChatChannels().getChannel(args[1]).kickSpy(user);
				} else {
					user.sendMessage(ChatColor.RED + "That channel doesn't exist!");
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
							if (channel.canExecuteBasicCommands(user)) {
								channel.listUsers(user);
							} else {
								user.sendMessage(ChatColor.RED + "You must be a channel moderator to do that!");
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
							if (channel.canExecuteBasicCommands(user)) {
								channel.listUsers(user);
							} else {
								user.sendMessage(ChatColor.RED + "You must be a channel moderator to do that!");
							}
						}
					}
				}
			} else if (args[0].equalsIgnoreCase("add")) {
				if (args.length != 2) {
					user.sendMessage(ChatColor.YELLOW + "Proper use is /channel add <channel>");
					return;
				}
				if (!user.hasPerm("bencmd.chat.newchannel")) {
					user.sendMessage(ChatColor.RED + "You don't have permission to do that!");
					BenCmd.getPlugin().logPermFail();
					return;
				}
				BenCmd.getChatChannels().addChannel(args[1], user);
			} else if (args[0].equalsIgnoreCase("info")) {
				if (args.length != 1) {
					user.sendMessage(ChatColor.YELLOW + "Proper use is /channel info");
					return;
				}
				if (user.inChannel()) {
					if (user.getActiveChannel().canExecuteBasicCommands(user)) {
						user.sendMessage(ChatColor.GRAY + "Information for channel " + ChatColor.GREEN + user.getActiveChannel().getName() + ChatColor.GRAY + ":");
						switch (user.getActiveChannel().getDefaultLevel()) {
							case BANNED:
								user.sendMessage(ChatColor.GRAY + "Default level: " + ChatColor.GREEN + "Banned");
								break;
							case MUTED:
								user.sendMessage(ChatColor.GRAY + "Default level: " + ChatColor.GREEN + "Muted");
								break;
							case NORMAL:
								user.sendMessage(ChatColor.GRAY + "Default level: " + ChatColor.GREEN + "Normal");
								break;
							default:
								user.sendMessage(ChatColor.GRAY + "Default level: " + ChatColor.RED + "UNKNOWN");
						}
						user.sendMessage(ChatColor.GRAY + "Always slow: " + ChatColor.GREEN + ((user.getActiveChannel().isDefaultSlowEnabled()) ? "Yes" : "No"));
						user.sendMessage(ChatColor.GRAY + "Slow delay: " + ChatColor.GREEN + (user.getActiveChannel().getDefaultSlowDelay() / 1000) + " seconds");
						user.sendMessage(ChatColor.GRAY + "MOTD: " + ChatColor.GREEN + user.getActiveChannel().getMotd());
					} else {
						user.sendMessage(ChatColor.RED + "You must be a channel moderator to do that!");
					}
				} else {
					user.sendMessage(ChatColor.RED + "You're not in a channel!");
				}
			} else if (args[0].equalsIgnoreCase("leave")) {
				if (!user.inChannel()) {
					user.sendMessage(ChatColor.RED + "You aren't in a channel!");
				} else {
					user.leaveChannel(true);
				}
			} else if (args[0].equalsIgnoreCase("remove")) {
				if (user.inChannel()) {
					if (user.getActiveChannel().canExecuteAllCommands(user)) {
						if (new Date().getTime() < user.getActiveChannel().getDelDanger()) {
							BenCmd.getChatChannels().removeChannel(user.getActiveChannel(), true);
							user.sendMessage(ChatColor.GREEN + "Channel was successfully removed!");
						} else {
							user.getActiveChannel().setDelDanger(new Date().getTime() + 20000);
							user.sendMessage(ChatColor.RED + "WARNING: You are about to permanently delete this channel!");
							user.sendMessage(ChatColor.RED + "All current users will be kicked from the channel! Repeat this");
							user.sendMessage(ChatColor.RED + "command within 20 seconds to verify your intention!");
						}
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
					if (user.getActiveChannel().canExecuteBasicCommands(user)) {
						PermissionUser toBan = PermissionUser.matchUserAllowPartial(args[1]);
						if (toBan == null) {
							user.sendMessage(ChatColor.RED + "That user couldn't be found!");
							return;
						}
						if (!user.getActiveChannel().canExecuteAllCommands(user) && user.getActiveChannel().getLevel(user).getLevel() <= user.getActiveChannel().getLevel(toBan).getLevel()) {
							user.sendMessage(ChatColor.RED + "You can't do that to somebody with a higher rank than you!");
							return;
						}
						user.getActiveChannel().setRole(toBan.getName(), ChatLevel.BANNED);
					} else {
						user.sendMessage(ChatColor.RED + "You must be a channel moderator to do that!");
					}
				} else {
					user.sendMessage(ChatColor.RED + "You're not in a channel!");
				}
			} else if (args[0].equalsIgnoreCase("mute")) {
				if (args.length != 2) {
					user.sendMessage(ChatColor.YELLOW + "Proper use is /channel mute <player>");
					return;
				}
				if (user.inChannel()) {
					if (user.getActiveChannel().canExecuteBasicCommands(user)) {
						PermissionUser toMute = PermissionUser.matchUserAllowPartial(args[1]);
						if (toMute == null) {
							user.sendMessage(ChatColor.RED + "That user couldn't be found!");
							return;
						}
						if (!user.getActiveChannel().canExecuteAllCommands(user) && user.getActiveChannel().getLevel(user).getLevel() <= user.getActiveChannel().getLevel(toMute).getLevel()) {
							user.sendMessage(ChatColor.RED + "You can't do that to somebody with a higher rank than you!");
							return;
						}
						user.getActiveChannel().setRole(toMute.getName(), ChatLevel.MUTED);
					} else {
						user.sendMessage(ChatColor.RED + "You must be a channel moderator to do that!");
					}
				} else {
					user.sendMessage(ChatColor.RED + "You're not in a channel!");
				}
			} else if (args[0].equalsIgnoreCase("normal")) {
				if (args.length != 2) {
					user.sendMessage(ChatColor.YELLOW + "Proper use is /channel normal <player>");
					return;
				}
				if (user.inChannel()) {
					if (user.getActiveChannel().canExecuteBasicCommands(user)) {
						PermissionUser toNormal = PermissionUser.matchUserAllowPartial(args[1]);
						if (toNormal == null) {
							user.sendMessage(ChatColor.RED + "That user couldn't be found!");
							return;
						}
						if (!user.getActiveChannel().canExecuteAllCommands(user) && user.getActiveChannel().getLevel(user).getLevel() <= user.getActiveChannel().getLevel(toNormal).getLevel()) {
							user.sendMessage(ChatColor.RED + "You can't do that to somebody with a higher rank than you!");
							return;
						}
						user.getActiveChannel().setRole(toNormal.getName(), ChatLevel.NORMAL);
					} else {
						user.sendMessage(ChatColor.RED + "You must be a channel moderator to do that!");
					}
				} else {
					user.sendMessage(ChatColor.RED + "You're not in a channel!");
				}
			} else if (args[0].equalsIgnoreCase("vip")) {
				if (args.length != 2) {
					user.sendMessage(ChatColor.YELLOW + "Proper use is /channel vip <player>");
					return;
				}
				if (user.inChannel()) {
					if (user.getActiveChannel().canExecuteAdvancedCommands(user)) {
						PermissionUser toVip = PermissionUser.matchUserAllowPartial(args[1]);
						if (toVip == null) {
							user.sendMessage(ChatColor.RED + "That user couldn't be found!");
							return;
						}
						if (!user.getActiveChannel().canExecuteAllCommands(user) && user.getActiveChannel().getLevel(user).getLevel() <= user.getActiveChannel().getLevel(toVip).getLevel()) {
							user.sendMessage(ChatColor.RED + "You can't do that to somebody with a higher rank than you!");
							return;
						}
						user.getActiveChannel().setRole(toVip.getName(), ChatLevel.VIP);
					} else {
						user.sendMessage(ChatColor.RED + "You must be a channel owner/co-owner to do that!");
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
					if (user.getActiveChannel().canExecuteAdvancedCommands(user)) {
						PermissionUser toMod = PermissionUser.matchUserAllowPartial(args[1]);
						if (toMod == null) {
							user.sendMessage(ChatColor.RED + "That user couldn't be found!");
							return;
						}
						if (!user.getActiveChannel().canExecuteAllCommands(user) && user.getActiveChannel().getLevel(user).getLevel() <= user.getActiveChannel().getLevel(toMod).getLevel()) {
							user.sendMessage(ChatColor.RED + "You can't do that to somebody with a higher rank than you!");
							return;
						}
						user.getActiveChannel().setRole(toMod.getName(), ChatLevel.MOD);
					} else {
						user.sendMessage(ChatColor.RED + "You must be a channel owner/co-owner to do that!");
					}
				} else {
					user.sendMessage(ChatColor.RED + "You're not in a channel!");
				}
			} else if (args[0].equalsIgnoreCase("coown")) {
				if (args.length != 2) {
					user.sendMessage(ChatColor.YELLOW + "Proper use is /channel coown <player>");
					return;
				}
				if (user.inChannel()) {
					if (user.getActiveChannel().canExecuteAllCommands(user)) {
						PermissionUser toOwn = PermissionUser.matchUserAllowPartial(args[1]);
						if (toOwn == null) {
							user.sendMessage(ChatColor.RED + "That user couldn't be found!");
							return;
						}
						user.getActiveChannel().setRole(toOwn.getName(), ChatLevel.COOWNER);
					} else {
						user.sendMessage(ChatColor.RED + "You must be the channel owner to do that!");
					}
				} else {
					user.sendMessage(ChatColor.RED + "You're not in a channel!");
				}
			} else if (args[0].equalsIgnoreCase("own")) {
				if (args.length != 2) {
					user.sendMessage(ChatColor.YELLOW + "Proper use is /channel own <player>");
					return;
				}
				if (user.inChannel()) {
					if (user.getActiveChannel().canExecuteAllCommands(user)) {
						PermissionUser toOwn = PermissionUser.matchUserAllowPartial(args[1]);
						if (toOwn == null) {
							user.sendMessage(ChatColor.RED + "That user couldn't be found!");
							return;
						}
						user.getActiveChannel().setRole(toOwn.getName(), ChatLevel.OWNER);
					} else {
						user.sendMessage(ChatColor.RED + "You must be the channel owner to do that!");
					}
				} else {
					user.sendMessage(ChatColor.RED + "You're not in a channel!");
				}
			} else if (args[0].equalsIgnoreCase("slow")) {
				if (args.length < 1 || args.length > 2) {
					user.sendMessage(ChatColor.YELLOW + "Proper use is /channel slow [millis]");
					return;
				}
				if (user.inChannel()) {
					if (user.getActiveChannel().canExecuteBasicCommands(user)) {
						if (args.length == 1) {
							user.getActiveChannel().toggleSlow();
						} else {
							int millis;
							try {
								millis = Integer.parseInt(args[1]);
							} catch (NumberFormatException e) {
								user.sendMessage(ChatColor.RED + "'" + args[1] + "' isn't a number!");
								return;
							}
							user.getActiveChannel().enableSlow(millis);
						}
					} else {
						user.sendMessage(ChatColor.RED + "You must be a channel moderator to do that!");
					}
				} else {
					user.sendMessage(ChatColor.RED + "You're not in a channel!");
				}
			} else if (args[0].equalsIgnoreCase("pause")) {
				if (args.length != 1) {
					user.sendMessage(ChatColor.YELLOW + "Proper use is /channel pause");
					return;
				}
				if (user.inChannel()) {
					if (user.getActiveChannel().canExecuteBasicCommands(user)) {
						user.getActiveChannel().togglePaused();
					} else {
						user.sendMessage(ChatColor.RED + "You must be a channel moderator to do that!");
					}
				} else {
					user.sendMessage(ChatColor.RED + "You're not in a channel!");
				}
			} else if (args[0].equalsIgnoreCase("motd")) {
				if (args.length < 2) {
					user.sendMessage(ChatColor.YELLOW + "Proper use is /channel motd <message>");
					return;
				}
				if (user.inChannel()) {
					if (user.getActiveChannel().canExecuteAdvancedCommands(user)) {
						String motd = "";
						for (int i = 1; i < args.length; i++) {
							if (motd.isEmpty()) {
								motd += args[i];
							} else {
								motd += " " + args[i];
							}
						}
						user.getActiveChannel().setMotd(motd);
					} else {
						user.sendMessage(ChatColor.RED + "You must be a channel owner/co-owner to do that!");
					}
				} else {
					user.sendMessage(ChatColor.RED + "You're not in a channel!");
				}
			} else if (args[0].equalsIgnoreCase("default")) {
				if (args.length != 2) {
					user.sendMessage(ChatColor.YELLOW + "Proper use is /channel default {ban|mute|normal}");
					return;
				}
				if (user.inChannel()) {
					if (user.getActiveChannel().canExecuteAdvancedCommands(user)) {
						ChatLevel def;
						if (args[1].equalsIgnoreCase("ban")) {
							def = ChatLevel.BANNED;
						} else if (args[1].equalsIgnoreCase("mute")) {
							def = ChatLevel.MUTED;
						} else if (args[1].equalsIgnoreCase("normal")) {
							def = ChatLevel.NORMAL;
						} else {
							user.sendMessage(ChatColor.YELLOW + "Proper use is /channel default {ban|mute|normal}");
							return;
						}
						user.getActiveChannel().setDefaultLevel(def);
					} else {
						user.sendMessage(ChatColor.RED + "You must be a channel owner/co-owner to do that!");
					}
				} else {
					user.sendMessage(ChatColor.RED + "You're not in a channel!");
				}
			} else if (args[0].equalsIgnoreCase("rename")) {
				if (args.length != 2) {
					user.sendMessage(ChatColor.YELLOW + "Proper use is /channel rename <name>");
					return;
				}
				if (user.inChannel()) {
					if (user.getActiveChannel().canExecuteAllCommands(user)) {
						user.getActiveChannel().setName(args[1]);
					} else {
						user.sendMessage(ChatColor.RED + "You must be the channel owner to do that!");
					}
				} else {
					user.sendMessage(ChatColor.RED + "You're not in a channel!");
				}
			} else if (args[0].equalsIgnoreCase("alwaysslow")) {
				if (args.length != 2) {
					user.sendMessage(ChatColor.YELLOW + "Proper use is /channel alwaysslow {true|false}");
					return;
				}
				if (user.inChannel()) {
					if (user.getActiveChannel().canExecuteAdvancedCommands(user)) {
						boolean alwaysSlow;
						if (args[1].equalsIgnoreCase("true")) {
							alwaysSlow = true;
						} else if (args[1].equalsIgnoreCase("false")) {
							alwaysSlow = false;
						} else {
							user.sendMessage(ChatColor.YELLOW + "Proper use is /channel alwaysslow {true|false}");
							return;
						}
						user.getActiveChannel().setDefaultSlowEnabled(alwaysSlow);
					} else {
						user.sendMessage(ChatColor.RED + "You must be a channel owner/co-owner to do that!");
					}
				} else {
					user.sendMessage(ChatColor.RED + "You're not in a channel!");
				}
			} else if (args[0].equalsIgnoreCase("slowdelay")) {
				if (args.length != 2) {
					user.sendMessage(ChatColor.YELLOW + "Proper use is /channel slowdelay <millis>");
					return;
				}
				if (user.inChannel()) {
					if (user.getActiveChannel().canExecuteAdvancedCommands(user)) {
						int delay;
						try {
							delay = Integer.parseInt(args[1]);
						} catch (NumberFormatException e) {
							user.sendMessage(ChatColor.YELLOW + "Proper use is /channel slowdelay <millis>");
							return;
						}
						user.getActiveChannel().setDefaultSlowDelay(delay);
					} else {
						user.sendMessage(ChatColor.RED + "You must be a channel owner/co-owner to do that!");
					}
				} else {
					user.sendMessage(ChatColor.RED + "You're not in a channel!");
				}
			}
		}
	}

}
