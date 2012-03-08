package com.bendude56.bencmd.chat.channels;

import java.util.Date;

import org.bukkit.Bukkit;
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
				BenCmd.getLocale().sendMessage(user, "command.channel.notInChannel");
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
					BenCmd.showUse(user, "channel", "nOwner");
				} else if (user.getActiveChannel().canExecuteAdvancedCommands(user)) {
					BenCmd.showUse(user, "channel", "nCoowner");
				} else if (user.getActiveChannel().canExecuteBasicCommands(user)) {
					BenCmd.showUse(user, "channel", "nMod");
				} else {
					BenCmd.showUse(user, "channel", "nNormal");
				}
			} else {
				BenCmd.showUse(user, "channel", "nOutside");
			}
		} else {
			if (args[0].equalsIgnoreCase("join")) {
				if (args.length != 2) {
					BenCmd.showUse(user, "channel", "join");
					return;
				}
				if (BenCmd.getChatChannels().channelExists(args[1])) {
					user.joinChannel(BenCmd.getChatChannels().getChannel(args[1]), true);
				} else {
					BenCmd.getLocale().sendMessage(user, "command.channel.channelNotFound", args[1]);
				}
			} else if (args[0].equalsIgnoreCase("spy")) {
				if (args.length != 2) {
					BenCmd.showUse(user, "channel", "spy");
					return;
				}
				if (BenCmd.getChatChannels().channelExists(args[1])) {
					if (BenCmd.getChatChannels().getChannel(args[1]).attemptSpy(user)) {
						user.spyChannel(BenCmd.getChatChannels().getChannel(args[1]));
					}
				} else {
					BenCmd.getLocale().sendMessage(user, "command.channel.channelNotFound", args[1]);
				}
			} else if (args[0].equalsIgnoreCase("unspy")) {
				if (args.length != 2) {
					BenCmd.showUse(user, "channel", "unspy");
					return;
				}
				if (BenCmd.getChatChannels().channelExists(args[1])) {
					BenCmd.getChatChannels().getChannel(args[1]).kickSpy(user);
				} else {
					BenCmd.getLocale().sendMessage(user, "command.channel.channelNotFound", args[1]);
				}
			} else if (args[0].equalsIgnoreCase("list")) {
				if (!user.inChannel()) {
					if (args.length == 1) {
						BenCmd.getChatChannels().listChannels(user);
					} else {
						ChatChannel channel = BenCmd.getChatChannels().getChannel(args[1]);
						if (channel == null) {
							BenCmd.getLocale().sendMessage(user, "command.channel.channelNotFound", args[1]);
						} else {
							if (channel.canExecuteBasicCommands(user)) {
								channel.listUsers(user);
							} else {
								BenCmd.getPlugin().logPermFail(user, "channel", args, true);
							}
						}
					}
				} else {
					if (args.length == 1) {
						user.getActiveChannel().listUsers(user);
					} else {
						ChatChannel channel = BenCmd.getChatChannels().getChannel(args[1]);
						if (channel == null) {
							BenCmd.getLocale().sendMessage(user, "command.channel.channelNotFound", args[1]);
						} else {
							if (channel.canExecuteBasicCommands(user)) {
								channel.listUsers(user);
							} else {
								BenCmd.getPlugin().logPermFail(user, "channel", args, true);
							}
						}
					}
				}
			} else if (args[0].equalsIgnoreCase("add")) {
				if (!user.hasPerm("bencmd.chat.newchannel")) {
					BenCmd.getPlugin().logPermFail(user, "channel", args, true);
					return;
				}
				if (args.length != 2) {
					BenCmd.showUse(user, "channel", "add");
					return;
				}
				BenCmd.getChatChannels().addChannel(args[1], user);
			} else if (args[0].equalsIgnoreCase("info")) {
				if (args.length != 1) {
					BenCmd.showUse(user, "channel", "info");
					return;
				}
				if (user.inChannel()) {
					if (user.getActiveChannel().canExecuteBasicCommands(user)) {
						String name = user.getActiveChannel().getName();
						String level;
						switch (user.getActiveChannel().getDefaultLevel()) {
							case BANNED:
								level = BenCmd.getLocale().getString("command.channel.info.banned");
								break;
							case MUTED:
								level = BenCmd.getLocale().getString("command.channel.info.muted");
								break;
							default:
								level = BenCmd.getLocale().getString("command.channel.info.normal");
								break;
						}
						String alwaysSlow = ((user.getActiveChannel().isDefaultSlowEnabled()) ? BenCmd.getLocale().getString("basic.yes") : BenCmd.getLocale().getString("basic.no"));
						String defaultDelay = (user.getActiveChannel().getDefaultSlowDelay() / 1000) + " seconds";
						String motd = user.getActiveChannel().getMotd();
						BenCmd.getLocale().sendMultilineMessage(user, "command.channel.info", name, level, alwaysSlow, defaultDelay, motd);
					} else {
						BenCmd.getPlugin().logPermFail(user, "channel", args, true);
					}
				} else {
					BenCmd.getLocale().sendMessage(user, "command.channel.notInChannel");
				}
			} else if (args[0].equalsIgnoreCase("leave")) {
				if (!user.inChannel()) {
					BenCmd.getLocale().sendMessage(user, "command.channel.notInChannel");
				} else {
					user.leaveChannel(true);
				}
			} else if (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("delete")) {
				if (user.inChannel()) {
					if (user.getActiveChannel().canExecuteAllCommands(user)) {
						if (new Date().getTime() < user.getActiveChannel().getDelDanger()) {
							BenCmd.getLocale().sendMessage(user, "command.channel.remove.success", user.getActiveChannel().getName());
							BenCmd.getChatChannels().removeChannel(user.getActiveChannel(), true);
						} else {
							user.getActiveChannel().setDelDanger(new Date().getTime() + 20000);
							BenCmd.getLocale().sendMultilineMessage(user, "command.channel.remove.danger", user.getActiveChannel().getName());
						}
					} else {
						BenCmd.getPlugin().logPermFail(user, "channel", args, true);
					}
				} else {
					BenCmd.getLocale().sendMessage(user, "command.channel.notInChannel");
				}
			} else if (args[0].equalsIgnoreCase("ban")) {
				if (args.length != 2) {
					BenCmd.showUse(user, "channel", "ban");
					return;
				}
				if (user.inChannel()) {
					if (user.getActiveChannel().canExecuteBasicCommands(user)) {
						PermissionUser toBan = PermissionUser.matchUserAllowPartial(args[1]);
						if (toBan == null) {
							BenCmd.getLocale().sendMessage(user, "basic.userNotFound", args[1]);
							return;
						}
						if (!user.getActiveChannel().canExecuteAllCommands(user) && user.getActiveChannel().getLevel(user).getLevel() <= user.getActiveChannel().getLevel(toBan).getLevel()) {
							BenCmd.getLocale().sendMessage(user, "command.channel.higherRank", toBan.getName());
							return;
						}
						user.getActiveChannel().setRole(toBan.getName(), ChatLevel.BANNED);
					} else {
						BenCmd.getPlugin().logPermFail(user, "channel", args, true);
					}
				} else {
					BenCmd.getLocale().sendMessage(user, "command.channel.notInChannel");
				}
			} else if (args[0].equalsIgnoreCase("mute")) {
				if (args.length != 2) {
					BenCmd.showUse(user, "channel", "mute");
					return;
				}
				if (user.inChannel()) {
					if (user.getActiveChannel().canExecuteBasicCommands(user)) {
						PermissionUser toMute = PermissionUser.matchUserAllowPartial(args[1]);
						if (toMute == null) {
							BenCmd.getLocale().sendMessage(user, "basic.userNotFound", args[1]);
							return;
						}
						if (!user.getActiveChannel().canExecuteAllCommands(user) && user.getActiveChannel().getLevel(user).getLevel() <= user.getActiveChannel().getLevel(toMute).getLevel()) {
							BenCmd.getLocale().sendMessage(user, "command.channel.higherRank", toMute.getName());
							return;
						}
						user.getActiveChannel().setRole(toMute.getName(), ChatLevel.MUTED);
					} else {
						BenCmd.getPlugin().logPermFail(user, "channel", args, true);
					}
				} else {
					BenCmd.getLocale().sendMessage(user, "command.channel.notInChannel");
				}
			} else if (args[0].equalsIgnoreCase("normal")) {
				if (args.length != 2) {
					BenCmd.showUse(user, "channel", "normal");
					return;
				}
				if (user.inChannel()) {
					if (user.getActiveChannel().canExecuteBasicCommands(user)) {
						PermissionUser toNormal = PermissionUser.matchUserAllowPartial(args[1]);
						if (toNormal == null) {
							BenCmd.getLocale().sendMessage(user, "basic.userNotFound", args[1]);
							return;
						}
						if (!user.getActiveChannel().canExecuteAllCommands(user) && user.getActiveChannel().getLevel(user).getLevel() <= user.getActiveChannel().getLevel(toNormal).getLevel()) {
							BenCmd.getLocale().sendMessage(user, "command.channel.higherRank", toNormal.getName());
							return;
						}
						user.getActiveChannel().setRole(toNormal.getName(), ChatLevel.NORMAL);
					} else {
						BenCmd.getPlugin().logPermFail(user, "channel", args, true);
					}
				} else {
					BenCmd.getLocale().sendMessage(user, "command.channel.notInChannel");
				}
			} else if (args[0].equalsIgnoreCase("vip")) {
				if (args.length != 2) {
					BenCmd.showUse(user, "channel", "vip");
					return;
				}
				if (user.inChannel()) {
					if (user.getActiveChannel().canExecuteAdvancedCommands(user)) {
						PermissionUser toVip = PermissionUser.matchUserAllowPartial(args[1]);
						if (toVip == null) {
							BenCmd.getLocale().sendMessage(user, "basic.userNotFound", args[1]);
							return;
						}
						if (!user.getActiveChannel().canExecuteAllCommands(user) && user.getActiveChannel().getLevel(user).getLevel() <= user.getActiveChannel().getLevel(toVip).getLevel()) {
							BenCmd.getLocale().sendMessage(user, "command.channel.higherRank", toVip.getName());
							return;
						}
						user.getActiveChannel().setRole(toVip.getName(), ChatLevel.VIP);
					} else {
						BenCmd.getPlugin().logPermFail(user, "channel", args, true);
					}
				} else {
					BenCmd.getLocale().sendMessage(user, "command.channel.notInChannel");
				}
			} else if (args[0].equalsIgnoreCase("mod")) {
				if (args.length != 2) {
					BenCmd.showUse(user, "channel", "mod");
					return;
				}
				if (user.inChannel()) {
					if (user.getActiveChannel().canExecuteAdvancedCommands(user)) {
						PermissionUser toMod = PermissionUser.matchUserAllowPartial(args[1]);
						if (toMod == null) {
							BenCmd.getLocale().sendMessage(user, "basic.userNotFound", args[1]);
							return;
						}
						if (!user.getActiveChannel().canExecuteAllCommands(user) && user.getActiveChannel().getLevel(user).getLevel() <= user.getActiveChannel().getLevel(toMod).getLevel()) {
							BenCmd.getLocale().sendMessage(user, "command.channel.higherRank", toMod.getName());
							return;
						}
						user.getActiveChannel().setRole(toMod.getName(), ChatLevel.MOD);
					} else {
						BenCmd.getPlugin().logPermFail(user, "channel", args, true);
					}
				} else {
					BenCmd.getLocale().sendMessage(user, "command.channel.notInChannel");
				}
			} else if (args[0].equalsIgnoreCase("coown")) {
				if (args.length != 2) {
					BenCmd.showUse(user, "channel", "coown");
					return;
				}
				if (user.inChannel()) {
					if (user.getActiveChannel().canExecuteAllCommands(user)) {
						PermissionUser toOwn = PermissionUser.matchUserAllowPartial(args[1]);
						if (toOwn == null) {
							BenCmd.getLocale().sendMessage(user, "basic.userNotFound", args[1]);
							return;
						}
						user.getActiveChannel().setRole(toOwn.getName(), ChatLevel.COOWNER);
					} else {
						BenCmd.getPlugin().logPermFail(user, "channel", args, true);
					}
				} else {
					BenCmd.getLocale().sendMessage(user, "command.channel.notInChannel");
				}
			} else if (args[0].equalsIgnoreCase("own")) {
				if (args.length != 2) {
					BenCmd.showUse(user, "channel", "own");
					return;
				}
				if (user.inChannel()) {
					if (user.getActiveChannel().canExecuteAllCommands(user)) {
						PermissionUser toOwn = PermissionUser.matchUserAllowPartial(args[1]);
						if (toOwn == null) {
							BenCmd.getLocale().sendMessage(user, "basic.userNotFound", args[1]);
							return;
						}
						user.getActiveChannel().setRole(toOwn.getName(), ChatLevel.OWNER);
					} else {
						BenCmd.getPlugin().logPermFail(user, "channel", args, true);
					}
				} else {
					BenCmd.getLocale().sendMessage(user, "command.channel.notInChannel");
				}
			} else if (args[0].equalsIgnoreCase("slow")) {
				if (args.length < 1 || args.length > 2) {
					BenCmd.showUse(user, "channel", "slow");
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
								BenCmd.showUse(user, "channel", "slow");
								return;
							}
							user.getActiveChannel().enableSlow(millis);
						}
					} else {
						BenCmd.getPlugin().logPermFail(user, "channel", args, true);
					}
				} else {
					BenCmd.getLocale().sendMessage(user, "command.channel.notInChannel");
				}
			} else if (args[0].equalsIgnoreCase("pause")) {
				if (args.length != 1) {
					BenCmd.showUse(user, "channel", "pause");
					return;
				}
				if (user.inChannel()) {
					if (user.getActiveChannel().canExecuteBasicCommands(user)) {
						user.getActiveChannel().togglePaused();
					} else {
						BenCmd.getPlugin().logPermFail(user, "channel", args, true);
					}
				} else {
					BenCmd.getLocale().sendMessage(user, "command.channel.notInChannel");
				}
			} else if (args[0].equalsIgnoreCase("motd")) {
				if (args.length < 2) {
					BenCmd.showUse(user, "channel", "motd");
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
						BenCmd.getPlugin().logPermFail(user, "channel", args, true);
					}
				} else {
					BenCmd.getLocale().sendMessage(user, "command.channel.notInChannel");
				}
			} else if (args[0].equalsIgnoreCase("default")) {
				if (args.length != 2) {
					BenCmd.showUse(user, "channel", "default");
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
							BenCmd.showUse(user, "channel", "default");
							return;
						}
						user.getActiveChannel().setDefaultLevel(def);
					} else {
						BenCmd.getPlugin().logPermFail(user, "channel", args, true);
					}
				} else {
					BenCmd.getLocale().sendMessage(user, "command.channel.notInChannel");
				}
			} else if (args[0].equalsIgnoreCase("rename")) {
				if (args.length != 2) {
					BenCmd.showUse(user, "channel", "rename");
					return;
				}
				if (user.inChannel()) {
					if (user.getActiveChannel().canExecuteAllCommands(user)) {
						user.getActiveChannel().setName(args[1]);
					} else {
						BenCmd.getPlugin().logPermFail(user, "channel", args, true);
					}
				} else {
					BenCmd.getLocale().sendMessage(user, "command.channel.notInChannel");
				}
			} else if (args[0].equalsIgnoreCase("alwaysslow")) {
				if (args.length != 2) {
					BenCmd.showUse(user, "channel", "alwaysslow");
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
							BenCmd.showUse(user, "channel", "alwaysslow");
							return;
						}
						user.getActiveChannel().setDefaultSlowEnabled(alwaysSlow);
					} else {
						BenCmd.getPlugin().logPermFail(user, "channel", args, true);
					}
				} else {
					BenCmd.getLocale().sendMessage(user, "command.channel.notInChannel");
				}
			} else if (args[0].equalsIgnoreCase("slowdelay")) {
				if (args.length != 2) {
					BenCmd.showUse(user, "channel", "slowdelay");
					return;
				}
				if (user.inChannel()) {
					if (user.getActiveChannel().canExecuteAdvancedCommands(user)) {
						int delay;
						try {
							delay = Integer.parseInt(args[1]);
						} catch (NumberFormatException e) {
							BenCmd.showUse(user, "channel", "slowdelay");
							return;
						}
						user.getActiveChannel().setDefaultSlowDelay(delay);
					} else {
						BenCmd.getPlugin().logPermFail(user, "channel", args, true);
					}
				} else {
					BenCmd.getLocale().sendMessage(user, "command.channel.notInChannel");
				}
			}
		}
	}

}
