package com.bendude56.bencmd.invisible;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.Commands;
import com.bendude56.bencmd.User;

public class InvisibleCommands implements Commands {

	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		User user = User.getUser(sender);
		if (commandLabel.equalsIgnoreCase("poof") && user.hasPerm("bencmd.poof.poof.self")) {
			poof(user, args);
			return true;
		} else if (commandLabel.equalsIgnoreCase("nopoof") && user.hasPerm("bencmd.poof.nopoof.self")) {
			noPoof(user, args);
			return true;
		} else if (commandLabel.equalsIgnoreCase("allpoof") && user.hasPerm("bencmd.poof.allpoof.self")) {
			allPoof(user, args);
			return true;
		} else if (commandLabel.equalsIgnoreCase("offline") && user.hasPerm("bencmd.poof.offline.self")) {
			offline(user, args);
			return true;
		} else if (commandLabel.equalsIgnoreCase("online") && user.hasPerm("bencmd.poof.offline.self")) {
			online(user, args);
			return true;
		} else if (commandLabel.equalsIgnoreCase("monitor") && user.hasPerm("bencmd.poof.monitor")) {
			monitor(user, args);
			return true;
		}
		return false;
	}

	public void poof(User user, String[] args) {
		if (args.length == 0) {
			if (user.isServer()) {
				BenCmd.getLocale().sendMessage(user, "basic.noServerUse");
				return;
			}
			if (!user.isPoofed()) {
				user.poof();
				BenCmd.getLocale().sendMessage(user, "command.poof.poof");
			} else {
				if (user.isOffline()) {
					BenCmd.getLocale().sendMessage(user, "command.poof.selfOffline");
				} else if (BenCmd.getMonitorController().isMonitoring((Player) user.getHandle())) {
					BenCmd.getLocale().sendMessage(user, "command.poof.selfMonitor");
				} else if (user.isAllPoofed()) {
					user.unAllPoof();
					user.unPoof();
					BenCmd.getLocale().sendMessage(user, "command.poof.unpoof");
				} else {
					user.unPoof();
					BenCmd.getLocale().sendMessage(user, "command.poof.unpoof");
				}
			}
		} else if (args.length == 1) {
			if (!user.hasPerm("bencmd.poof.poof.other")) {
				BenCmd.getPlugin().logPermFail(user, "poof", args, true);
				return;
			}
			User user2 = User.matchUserAllowPartial(args[0]);
			if (user2 == null) {
				BenCmd.getLocale().sendMessage(user, "basic.userNotFound", args[0]);
				return;
			}
			if (!user2.isPoofed()) {
				user2.poof();
				BenCmd.getLocale().sendMessage(user2, "command.poof.poof");
			} else {
				if (user2.isOffline()) {
					BenCmd.getLocale().sendMessage(user, "command.poof.otherOffline");
				} else if (BenCmd.getMonitorController().isMonitoring((Player) user2.getHandle())) {
					BenCmd.getLocale().sendMessage(user, "command.poof.otherMonitor");
				} else if (user2.isAllPoofed()) {
					user2.unAllPoof();
					user2.unPoof();
					BenCmd.getLocale().sendMessage(user2, "command.poof.unpoof");
					BenCmd.getLocale().sendMessage(user, "command.poof.unpoofOther", user2.getName());
				} else {
					user2.unPoof();
					BenCmd.getLocale().sendMessage(user2, "command.poof.unpoof");
					BenCmd.getLocale().sendMessage(user, "command.poof.unpoofOther", user2.getName());
				}
			}
		} else {
			BenCmd.showUse(user, "poof");
		}
	}

	public void allPoof(User user, String[] args) {
		if (args.length == 0) {
			if (user.isServer()) {
				BenCmd.getLocale().sendMessage(user, "basic.noServerUse");
				return;
			}
			if (!user.isPoofed()) {
				user.poof();
				user.allPoof();
				BenCmd.getLocale().sendMessage(user, "command.allpoof.poof");
			} else if (!user.isAllPoofed()) {
				user.allPoof();
				BenCmd.getLocale().sendMessage(user, "command.allpoof.poof");
			} else {
				user.unAllPoof();
				BenCmd.getLocale().sendMessage(user, "command.allpoof.unpoof");
			}
		} else if (args.length == 1) {
			if (!user.hasPerm("bencmd.poof.allpoof.other")) {
				BenCmd.getPlugin().logPermFail(user, "allpoof", args, true);
				return;
			}
			User user2 = User.matchUserAllowPartial(args[0]);
			if (user2 == null) {
				BenCmd.getLocale().sendMessage(user, "basic.userNotFound", args[0]);
				return;
			}
			if (!user2.isPoofed()) {
				user2.poof();
				user2.allPoof();
				BenCmd.getLocale().sendMessage(user2, "command.allpoof.poof");
				BenCmd.getLocale().sendMessage(user, "command.allpoof.poofOther", user2.getName());
			} else if (!user.isAllPoofed()) {
				user2.allPoof();
				BenCmd.getLocale().sendMessage(user2, "command.allpoof.poof");
				BenCmd.getLocale().sendMessage(user, "command.allpoof.poofOther", user2.getName());
			} else {
				user2.unAllPoof();
				BenCmd.getLocale().sendMessage(user2, "command.allpoof.unpoof");
				BenCmd.getLocale().sendMessage(user, "command.allpoof.unpoofOther", user2.getName());
			}
		} else {
			BenCmd.showUse(user, "allpoof");
		}
	}

	public void noPoof(User user, String args[]) {
		if (args.length == 0) {
			if (user.isServer()) {
				BenCmd.getLocale().sendMessage(user, "basic.noServerUse");
				return;
			}
			if (!user.isNoPoofed()) {
				user.noPoof();
				BenCmd.getLocale().sendMessage(user, "command.nopoof.poof");
			} else {
				user.unNoPoof();
				BenCmd.getLocale().sendMessage(user, "command.nopoof.unpoof");
			}
		} else if (args.length == 1) {
			if (!user.hasPerm("bencmd.poof.nopoof.other")) {
				BenCmd.getPlugin().logPermFail(user, "nopoof", args, true);
				return;
			}
			User user2 = User.matchUserAllowPartial(args[0]);
			if (user2 == null) {
				BenCmd.getLocale().sendMessage(user, "basic.userNotFound", args[0]);
				return;
			}
			if (!user2.isNoPoofed()) {
				user2.noPoof();
				BenCmd.getLocale().sendMessage(user2, "command.nopoof.poof");
				BenCmd.getLocale().sendMessage(user, "command.nopoof.poofOther", user2.getName());
			} else {
				user2.unNoPoof();
				BenCmd.getLocale().sendMessage(user2, "command.nopoof.unpoof");
				BenCmd.getLocale().sendMessage(user, "command.allpoof.unpoofOther", user2.getName());
			}
		} else {
			BenCmd.showUse(user, "nopoof");
		}
	}

	public void offline(User user, String[] args) {
		if (args.length == 0) {
			if (user.isServer()) {
				BenCmd.getLocale().sendMessage(user, "basic.noServerUse");
				return;
			}
			if (user.isOffline()) {
				BenCmd.getLocale().sendMessage(user, "command.offline.selfAlready");
			} else {
				if (!user.isPoofed()) {
					user.poof();
				}
				user.goOffline();
				BenCmd.getLocale().sendMessage(user, "command.offline.self");
			}
		} else if (args.length == 1) {
			if (!user.hasPerm("bencmd.poof.offline.other")) {
				BenCmd.getPlugin().logPermFail(user, "offline", args, true);
				return;
			}
			User user2 = User.matchUserAllowPartial(args[0]);
			if (user2 == null) {
				BenCmd.getLocale().sendMessage(user, "basic.userNotFound", args[0]);
				return;
			}
			if (user2.isOffline()) {
				BenCmd.getLocale().sendMessage(user, "command.offline.otherAlready", user2.getName());
			} else {
				if (!user2.isPoofed()) {
					user2.poof();
				}
				user2.goOffline();
				BenCmd.getLocale().sendMessage(user2, "command.offline.self");
				BenCmd.getLocale().sendMessage(user, "command.offline.other", user2.getName());
			}
		} else {
			BenCmd.showUse(user, "offline");
		}
	}

	public void online(User user, String[] args) {
		if (args.length == 0) {
			if (user.isServer()) {
				BenCmd.getLocale().sendMessage(user, "basic.noServerUse");
				return;
			}
			if (!user.isOffline()) {
				BenCmd.getLocale().sendMessage(user, "command.online.selfAlready");
			} else {
				user.goOnline();
				BenCmd.getLocale().sendMessage(user, "command.online.self");
			}
		} else if (args.length == 1) {
			if (!user.hasPerm("bencmd.poof.offline.other")) {
				BenCmd.getPlugin().logPermFail(user, "online", args, true);
				return;
			}
			User user2 = User.matchUserAllowPartial(args[0]);
			if (user2 == null) {
				BenCmd.getLocale().sendMessage(user, "basic.userNotFound", args[0]);
				return;
			}
			if (!user2.isOffline()) {
				BenCmd.getLocale().sendMessage(user, "command.online.otherAlready", user2.getName());
			} else {
				user2.goOnline();
				BenCmd.getLocale().sendMessage(user2, "command.online.self");
				BenCmd.getLocale().sendMessage(user, "command.online.other", user2.getName());
			}
		} else {
			BenCmd.showUse(user, "online");
		}
	}
	
	public void monitor(User user, String[] args) {
		if (args.length == 0) {
			BenCmd.showUse(user, "monitor");
		} else if (args[0].equalsIgnoreCase("none")) {
			BenCmd.getMonitorController().cancelMonitor(user.getPlayerHandle());
		} else {
			User user2 = User.matchUserAllowPartial(args[0]);
			if (user2 == null) {
				BenCmd.getLocale().sendMessage(user, "basic.userNotFound", args[0]);
				return;
			}
			BenCmd.getMonitorController().setMonitor(user.getPlayerHandle(), user2.getPlayerHandle());
			BenCmd.getLocale().sendMessage(user, "command.monitor.success", user2.getName());
		}
	}
}
