package ben_dude56.plugins.bencmd.chat.channels;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ben_dude56.plugins.bencmd.BenCmd;
import ben_dude56.plugins.bencmd.Commands;
import ben_dude56.plugins.bencmd.User;
import ben_dude56.plugins.bencmd.chat.channels.ChatChannel.ChatterType;
import ben_dude56.plugins.bencmd.permissions.PermissionUser;

//TODO For version 1.1.0: Completely rewrite code. Current code is too messy.
public class ChatChannelCommands implements Commands {

	BenCmd plugin;
	Logger log = Logger.getLogger("minecraft");

	public ChatChannelCommands(BenCmd instance) {
		plugin = instance;
	}

	public boolean channelsEnabled() {
		return plugin.mainProperties.getBoolean("channelsEnabled", false);
	}

	public boolean onCommand(CommandSender sender, Command command,
			String commandLabel, String[] args) {
		User user;
		try {
			user = new User(plugin, (Player) sender);
		} catch (ClassCastException e) {
			user = new User(plugin);
		}
		if (commandLabel.equalsIgnoreCase("mod")) {
			Mod(args, user);
			return true;
		} else if (commandLabel.equalsIgnoreCase("demod")) {
			Demod(args, user);
		} else if (commandLabel.equalsIgnoreCase("unmute")) {
			Unmute(args, user);
		} else if (commandLabel.equalsIgnoreCase("mute")) {
			Mute(args, user);
		} else if (commandLabel.equalsIgnoreCase("undeafen")) {
			Undeafen(args, user);
		} else if (commandLabel.equalsIgnoreCase("deafen")) {
			Deafen(args, user);
		} else if (commandLabel.equalsIgnoreCase("memod")
				&& user.hasPerm("canModSelf")) {
			MeMod(user);
		}
		return false;
	}

	public void Mod(String[] args, User user) {
		if (!user.inChannel()) {
			user.sendMessage(ChatColor.RED
					+ "You must be in a chat channel to do that!");
			return;
		}
		if (!user.getActiveChannel().isMod(user)) {
			user.sendMessage(ChatColor.RED
					+ "You must be a mod in your active chat channel to do that!");
			return;
		}
		User user2;
		if ((user2 = User.matchUser(args[0], plugin)) == null
				|| user.getActiveChannel() != user2.getActiveChannel()) {
			user.sendMessage(ChatColor.RED + "That user isn't in this channel!");
			return;
		} else if (user2.getActiveChannel().isMod(user2)) {
			user.sendMessage(ChatColor.RED + "That user is already a mod!");
			return;
		}
		user.getActiveChannel().addMod(user2);
		user.sendMessage(ChatColor.GREEN + "That user has been modded!");
	}

	public void Demod(String[] args, User user) {
		if (!user.inChannel()) {
			user.sendMessage(ChatColor.RED
					+ "You must be in a chat channel to do that!");
			return;
		}
		if (!user.getActiveChannel().isMod(user)) {
			user.sendMessage(ChatColor.RED
					+ "You must be a mod in your active chat channel to do that!");
			return;
		}
		User user2;
		if ((user2 = User.matchUser(args[0], plugin)) == null
				|| user.getActiveChannel() != user2.getActiveChannel()) {
			user.sendMessage(ChatColor.RED + "That user isn't in this channel!");
			return;
		} else if (!user2.getActiveChannel().isMod(user2)) {
			user.sendMessage(ChatColor.RED + "That user is not a mod!");
			return;
		}
		user.getActiveChannel().removeMod(user2);
		user.sendMessage(ChatColor.GREEN + "That user has been demodded!");
	}

	public void Mute(String[] args, User user) {
		if (!user.inChannel()) {
			user.sendMessage(ChatColor.RED
					+ "You must be in a chat channel to do that!");
			return;
		}
		if (!user.getActiveChannel().isMod(user)) {
			user.sendMessage(ChatColor.RED
					+ "You must be a mod in your active chat channel to do that!");
			return;
		}
		PermissionUser user2;
		if ((user2 = PermissionUser.matchUserIgnoreCase(args[0], plugin)) == null) {
			user.sendMessage(ChatColor.RED + "That user doesn't exist!");
			return;
		} else if (!user.getActiveChannel().canTalk(user)) {
			user.sendMessage(ChatColor.RED + "That user is already muted!");
			return;
		}
		if (user.getActiveChannel().getAutoLevel() == ChatterType.DISALLOW
				|| user.getActiveChannel().getAutoLevel() == ChatterType.LISTEN) {
			user.getActiveChannel().addTalk(user2);
		} else {
			user.getActiveChannel().removeTalk(user2);
		}
		user.sendMessage(ChatColor.GREEN + "That user has been muted!");
	}

	public void Unmute(String[] args, User user) {
		if (!user.inChannel()) {
			user.sendMessage(ChatColor.RED
					+ "You must be in a chat channel to do that!");
			return;
		}
		if (!user.getActiveChannel().isMod(user)) {
			user.sendMessage(ChatColor.RED
					+ "You must be a mod in your active chat channel to do that!");
			return;
		}
		PermissionUser user2;
		if ((user2 = PermissionUser.matchUserIgnoreCase(args[0], plugin)) == null) {
			user.sendMessage(ChatColor.RED + "That user doesn't exist!");
			return;
		} else if (user.getActiveChannel().canTalk(user)) {
			user.sendMessage(ChatColor.RED + "That user is not muted!");
			return;
		}
		if (user.getActiveChannel().getAutoLevel() == ChatterType.DISALLOW
				|| user.getActiveChannel().getAutoLevel() == ChatterType.LISTEN) {
			user.getActiveChannel().removeTalk(user2);
		} else {
			user.getActiveChannel().addTalk(user2);
		}
		user.sendMessage(ChatColor.GREEN + "That user has been unmuted!");
	}

	public void Deafen(String[] args, User user) {
		if (!user.inChannel()) {
			user.sendMessage(ChatColor.RED
					+ "You must be in a chat channel to do that!");
			return;
		}
		if (!user.getActiveChannel().isMod(user)) {
			user.sendMessage(ChatColor.RED
					+ "You must be a mod in your active chat channel to do that!");
			return;
		}
		PermissionUser user2;
		if ((user2 = PermissionUser.matchUserIgnoreCase(args[0], plugin)) == null) {
			user.sendMessage(ChatColor.RED + "That user doesn't exist!");
			return;
		} else if (!user.getActiveChannel().canListen(user)) {
			user.sendMessage(ChatColor.RED + "That user is already deaf!");
			return;
		}
		if (user.getActiveChannel().getAutoLevel() != ChatterType.DISALLOW) {
			user.getActiveChannel().addListen(user2);
		} else {
			user.getActiveChannel().removeListen(user2);
		}
		user.sendMessage(ChatColor.GREEN + "That user has been deafened!");
	}

	public void Undeafen(String[] args, User user) {
		if (!user.inChannel()) {
			user.sendMessage(ChatColor.RED
					+ "You must be in a chat channel to do that!");
			return;
		}
		if (!user.getActiveChannel().isMod(user)) {
			user.sendMessage(ChatColor.RED
					+ "You must be a mod in your active chat channel to do that!");
			return;
		}
		PermissionUser user2;
		if ((user2 = PermissionUser.matchUserIgnoreCase(args[0], plugin)) == null) {
			user.sendMessage(ChatColor.RED + "That user doesn't exist!");
			return;
		} else if (user.getActiveChannel().canListen(user)) {
			user.sendMessage(ChatColor.RED + "That user is not deaf!");
			return;
		}
		if (user.getActiveChannel().getAutoLevel() != ChatterType.DISALLOW) {
			user.getActiveChannel().removeListen(user2);
		} else {
			user.getActiveChannel().addListen(user2);
		}
		user.sendMessage(ChatColor.GREEN + "That user has been undeafened!");
	}

	public void MeMod(User user) {
		if (!user.inChannel()) {
			user.sendMessage(ChatColor.RED
					+ "You must be in a chat channel to do that!");
			return;
		}
		if (user.getActiveChannel().isMod(user)) {
			user.sendMessage(ChatColor.RED + "You are already a mod!");
			return;
		}
		user.getActiveChannel().addMod(user);
		user.sendMessage(ChatColor.GREEN + "You are now a mod!");
	}
}
