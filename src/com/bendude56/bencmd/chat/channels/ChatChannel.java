package com.bendude56.bencmd.chat.channels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;

import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.User;
import com.bendude56.bencmd.chat.ChatChecker;
import com.bendude56.bencmd.chat.SlowMode;
import com.bendude56.bencmd.permissions.PermissionUser;

public class ChatChannel {

	public static ChatChannel loadChannel(String name, String data) {
		String motd = data.split("\\|")[0];
		HashMap<String, ChatLevel> users = loadUsers(data.split("\\|")[1]);
		ChatLevel defaultLevel = ChatLevel.fromEntry(data.split("\\|")[2]);
		int slowDelay;
		try {
			slowDelay = Integer.parseInt(data.split("\\|")[3]);
		} catch (NumberFormatException e) {
			slowDelay = BenCmd.getMainProperties().getInteger("slowTime", 1000);
		}
		boolean slow = data.split("\\|")[4] == "true";
		return new ChatChannel(name, defaultLevel, motd, users, slowDelay, slow);
	}

	public static ChatChannel createChannel(String name, String owner) {
		HashMap<String, ChatLevel> users = new HashMap<String, ChatLevel>();
		users.put(owner, ChatLevel.OWNER);
		return new ChatChannel(name, ChatLevel.NORMAL, BenCmd.getLocale().getString("misc.channel.defaultMotd"), users, BenCmd.getMainProperties().getInteger("slowTime", 1000), false);
	}

	private static HashMap<String, ChatLevel> loadUsers(String users) {
		HashMap<String, ChatLevel> userLevels = new HashMap<String, ChatLevel>();
		for (String e : users.split(",")) {
			try {
				String name = e.split(":")[0];
				ChatLevel level = ChatLevel.fromEntry(e.split(":")[1]);
				userLevels.put(name, level);
			} catch (Exception ex) {}
		}
		return userLevels;
	}

	// Saved variables
	private String						name;
	private ChatLevel					defaultLevel;
	private String						motd;
	private HashMap<String, ChatLevel>	users;
	private int							defaultSlowDelay;
	private boolean						defaultSlowEnabled;

	// Temporary variables
	private List<User>					inChannel;
	private List<User>					spies;
	private SlowMode					slow;
	private boolean						paused;
	private long						delDanger;

	public ChatChannel(String name, ChatLevel defaultLevel, String motd, HashMap<String, ChatLevel> users, int defaultSlowDelay, boolean defaultSlowEnabled) {
		// Copy provided variables
		this.name = name;
		this.defaultLevel = defaultLevel;
		this.motd = motd;
		this.users = users;
		this.defaultSlowDelay = defaultSlowDelay;
		this.defaultSlowEnabled = defaultSlowEnabled;

		// Initialize temporary variables
		inChannel = new ArrayList<User>();
		spies = new ArrayList<User>();
		slow = new SlowMode(defaultSlowDelay);
		if (defaultSlowEnabled) {
			slow.enableSlow(defaultSlowDelay);
		}
		paused = false;
		delDanger = 0;
	}

	public ChatLevel getLevel(PermissionUser user) {
		if (user.hasPerm("bencmd.chat.owner")) {
			return ChatLevel.OWNER;
		}
		if (users.containsKey(user.getName())) {
			if (user.hasPerm("bencmd.chat.mod") && users.get(user.getName()).getLevel() < ChatLevel.MOD.getLevel()) {
				return ChatLevel.MOD;
			}
			return users.get(user.getName());
		} else {
			if (user.hasPerm("bencmd.chat.mod") && defaultLevel.getLevel() < ChatLevel.MOD.getLevel()) {
				return ChatLevel.MOD;
			}
			return defaultLevel;
		}
	}

	public boolean attemptJoin(User user, boolean announce) {
		if (spies.contains(user)) {
			spies.remove(user);
		}
		ChatLevel lvl = getLevel(user);
		if (lvl == ChatLevel.BANNED) {
			if (users.containsKey(user.getName())) {
				BenCmd.getLocale().sendMessage(user, "misc.channel.noConnect.ban", name);
			} else {
				BenCmd.getLocale().sendMessage(user, "misc.channel.noConnect", name);
			}
			return false;
		}
		sendJoinMsg(user);
		if (announce) {
			broadcastMessage(BenCmd.getLocale().getString("misc.channel.join", getSpecialPrefix(user) + user.getColor() + user.getName()));
		}
		inChannel.add(user);
		return true;
	}

	public boolean attemptSpy(User user) {
		if (inChannel.contains(user)) {
			leaveChannel(user, true);
		}
		if (getLevel(user).getLevel() < ChatLevel.MOD.getLevel()) {
			BenCmd.getLocale().sendMessage(user, "misc.channel.noConnect.spy", name);
			return false;
		}
		BenCmd.getLocale().sendMessage(user, "misc.channel.connect.spy", name);
		BenCmd.getLocale().sendMessage(user, "misc.channel.motd", motd);
		spies.add(user);
		return true;
	}

	private void sendJoinMsg(User user) {
		ChatLevel lvl = getLevel(user);
		BenCmd.getLocale().sendMessage(user, "misc.channel.connect", name);
		BenCmd.getLocale().sendMessage(user, "misc.channel.motd", motd);
		if (lvl == ChatLevel.MUTED) {
			BenCmd.getLocale().sendMessage(user, "misc.channel.note.muted");
		} else if (paused && lvl.getLevel() < ChatLevel.VIP.getLevel()) {
			BenCmd.getLocale().sendMessage(user, "misc.channel.note.paused");
		} else if (slow.isEnabled() && lvl.getLevel() < ChatLevel.VIP.getLevel()) {
			BenCmd.getLocale().sendMessage(user, "misc.channel.note.slow");
		}
	}

	public void leaveChannel(User user, boolean announce) {
		if (!inChannel.contains(user)) {
			throw new UnsupportedOperationException("Player not in channel!");
		}
		BenCmd.getLocale().sendMessage(user, "misc.channel.disconnect", name);
		inChannel.remove(user);
		if (announce) {
			broadcastMessage(BenCmd.getLocale().getString("misc.channel.leave", getSpecialPrefix(user) + user.getColor() + user.getName()));
		}
	}

	private String getSpecialPrefix(User user) {
		if (user.isDev()) {
			return ChatColor.DARK_GREEN + "*";
		}
		switch (getLevel(user)) {
			case OWNER:
			case COOWNER:
				return ChatColor.GOLD + "*";
			case MOD:
				return ChatColor.GRAY + "*";
			case VIP:
				return ChatColor.DARK_RED + "*";
			default:
				return "";
		}
	}

	public void broadcastMessage(String message, User sender) {
		for (User u : inChannel) {
			if (sender.hasPerm("bencmd.chat.noignore") || !u.isIgnoring(sender)) {
				u.sendMessage(message);
			}
		}
		for (User u : spies) {
			if (sender.hasPerm("bencmd.chat.noignore") || !u.isIgnoring(sender)) {
				u.sendMessage(ChatColor.GRAY + name + ": " + message);
			}
		}
		BenCmd.log(message);
	}
	
	public void broadcastMessage(String message) {
		for (User u : inChannel) {
			u.sendMessage(message);
		}
		for (User u : spies) {
			u.sendMessage(ChatColor.GRAY + name + ": " + message);
		}
		BenCmd.log(message);
	}

	public void sendChat(User u, String msg) {
		ChatLevel lvl = getLevel(u);

		// Pre-send checks
		if (lvl == ChatLevel.MUTED) {
			BenCmd.getLocale().sendMessage(u, "misc.channel.noTalk.muted");
			return;
		} else if (paused && lvl.getLevel() < ChatLevel.VIP.getLevel()) {
			BenCmd.getLocale().sendMessage(u, "misc.channel.noTalk.paused");
			return;
		} else if (slow.isEnabled() && slow.playerBlocked(u.getName()) != 0) {
			BenCmd.getLocale().sendMessage(u, "misc.channel.noTalk.slow", (slow.playerBlocked(u.getName()) / 1000) + "");
			return;
		} else if (ChatChecker.checkBlocked(msg)) {
			BenCmd.getLocale().sendMessage(u, "misc.channel.noTalk.blocked");
			return;
		} else if (ChatChecker.isAllCaps(msg) && lvl.getLevel() < ChatLevel.VIP.getLevel()) {
			BenCmd.getLocale().sendMessage(u, "misc.channel.noTalk.caps");
			return;
		}

		// If slow is enabled, add the user
		if (slow.isEnabled() && lvl.getLevel() < ChatLevel.VIP.getLevel()) {
			slow.playerAdd(u.getName());
		}

		// Format + send the message
		String prefix = (u.getPrefix().isEmpty()) ? (getSpecialPrefix(u) + u.getColor()) : (u.getColor() + "[" + u.getPrefix() + "] " + getSpecialPrefix(u) + u.getColor());
		broadcastMessage(prefix + u.getName() + ": " + ChatColor.WHITE + msg, u);
	}

	public void sendMe(User u, String msg) {
		ChatLevel lvl = getLevel(u);

		// Pre-send checks
		if (lvl == ChatLevel.MUTED) {
			BenCmd.getLocale().sendMessage(u, "misc.channel.noTalk.muted");
			return;
		} else if (paused && lvl.getLevel() < ChatLevel.VIP.getLevel()) {
			BenCmd.getLocale().sendMessage(u, "misc.channel.noTalk.paused");
			return;
		} else if (slow.isEnabled() && slow.playerBlocked(u.getName()) != 0) {
			BenCmd.getLocale().sendMessage(u, "misc.channel.noTalk.slow", (slow.playerBlocked(u.getName()) / 1000) + "");
			return;
		} else if (ChatChecker.checkBlocked(msg)) {
			BenCmd.getLocale().sendMessage(u, "misc.channel.noTalk.blocked");
			return;
		} else if (ChatChecker.isAllCaps(msg) && lvl.getLevel() < ChatLevel.VIP.getLevel()) {
			BenCmd.getLocale().sendMessage(u, "misc.channel.noTalk.caps");
			return;
		}

		// If slow is enabled, add the user
		if (slow.isEnabled() && lvl.getLevel() < ChatLevel.VIP.getLevel()) {
			slow.playerAdd(u.getName());
		}

		// Format + send the message
		String prefix = getSpecialPrefix(u) + u.getColor();
		broadcastMessage(prefix + u.getName() + ChatColor.WHITE + " " + msg);
	}

	public String getSaveValue() {
		StringBuilder result = new StringBuilder();
		result.append(motd + "|");
		boolean prepend = false;
		for (Map.Entry<String, ChatLevel> user : users.entrySet()) {
			if (prepend) {
				result.append("," + user.getKey() + ":" + user.getValue().getEntry());
			} else {
				prepend = true;
				result.append(user.getKey() + ":" + user.getValue().getEntry());
			}
		}
		result.append("|" + defaultLevel.getEntry() + "|" + defaultSlowDelay + "|" + ((defaultSlowEnabled) ? "true" : "false"));
		return result.toString();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		BenCmd.getChatChannels().removeChannel(this, false);
		this.name = name;
		BenCmd.getChatChannels().addChannel(this);
	}

	public boolean userInside(String user) {
		User u = User.matchUser(user);
		return inChannel.contains(u);
	}

	public void kickUser(User user) {
		if (inChannel.contains(user)) {
			BenCmd.getLocale().sendMessage(user, "misc.channel.kicked", name);
			user.setActiveChannel(null);
			inChannel.remove(user);
			broadcastMessage(BenCmd.getLocale().getString("misc.channel.leave", getSpecialPrefix(user) + user.getColor() + user.getName()));
		}
		kickSpy(user);
	}

	public void kickSpy(User user) {
		if (spies.contains(user)) {
			BenCmd.getLocale().sendMessage(user, "misc.channel.disconnect.spy", name);
			user.unspyChannel(this);
			spies.remove(user);
		}
	}

	public void setRole(String user, ChatLevel role) {
		if (role == ChatLevel.DEFAULT) {
			role = defaultLevel;
			if (users.containsKey(user)) {
				users.remove(user);
			}
		} else {
			users.put(user, role);
		}
		User u = User.matchUser(user);
		if (u != null) {
			if (role == ChatLevel.BANNED) {
				kickUser(u);
				BenCmd.getLocale().sendMessage(u, "misc.channel.role.banned", name);
			} else if (role == ChatLevel.MUTED) {
				BenCmd.getLocale().sendMessage(u, "misc.channel.role.muted", name);
				kickSpy(u);
			} else if (role == ChatLevel.NORMAL) {
				kickSpy(u);
			} else if (role == ChatLevel.VIP) {
				BenCmd.getLocale().sendMessage(u, "misc.channel.role.vip", name);
				kickSpy(u);
			} else if (role == ChatLevel.MOD) {
				BenCmd.getLocale().sendMessage(u, "misc.channel.role.mod", name);
			} else if (role == ChatLevel.COOWNER) {
				BenCmd.getLocale().sendMessage(u, "misc.channel.role.coowner", name);
			} else if (role == ChatLevel.OWNER) {
				BenCmd.getLocale().sendMessage(u, "misc.channel.role.owner", name);
			}
		}
		BenCmd.getChatChannels().saveChannel(this);
	}

	public boolean canExecuteBasicCommands(User user) {
		return (getLevel(user).getLevel() >= ChatLevel.MOD.getLevel());
	}

	public boolean canExecuteAdvancedCommands(User user) {
		return (getLevel(user).getLevel() >= ChatLevel.COOWNER.getLevel());
	}

	public boolean canExecuteAllCommands(User user) {
		return (getLevel(user) == ChatLevel.OWNER);
	}

	public void togglePaused() {
		if (paused) {
			paused = false;
			broadcastMessage(BenCmd.getLocale().getString("misc.channel.pause.off"));
		} else {
			paused = true;
			broadcastMessage(BenCmd.getLocale().getString("misc.channel.pause.on"));
		}
	}

	public void toggleSlow() {
		if (slow.isEnabled()) {
			slow.disableSlow();
			broadcastMessage(BenCmd.getLocale().getString("misc.channel.slow.off"));
		} else {
			slow.enableSlow(defaultSlowDelay);
			broadcastMessage(BenCmd.getLocale().getString("misc.channel.slow.on", (defaultSlowDelay / 1000) + ""));
		}
	}

	public void enableSlow(int millis) {
		slow.enableSlow(millis);
		broadcastMessage(BenCmd.getLocale().getString("misc.channel.slow.on", (millis / 1000) + ""));
	}

	public ChatLevel getDefaultLevel() {
		return defaultLevel;
	}

	public void setDefaultLevel(ChatLevel defaultLevel) {
		this.defaultLevel = defaultLevel;
		BenCmd.getChatChannels().saveChannel(this);
	}

	public String getMotd() {
		return motd;
	}

	public void setMotd(String motd) {
		this.motd = motd;
		BenCmd.getChatChannels().saveChannel(this);
	}

	public int getDefaultSlowDelay() {
		return defaultSlowDelay;
	}

	public void setDefaultSlowDelay(int defaultSlowDelay) {
		this.defaultSlowDelay = defaultSlowDelay;
		BenCmd.getChatChannels().saveChannel(this);
	}

	public boolean isDefaultSlowEnabled() {
		return defaultSlowEnabled;
	}

	public void setDefaultSlowEnabled(boolean defaultSlowEnabled) {
		this.defaultSlowEnabled = defaultSlowEnabled;
		BenCmd.getChatChannels().saveChannel(this);
	}

	public void listUsers(User user) {
		String value = "";
		for (User online : inChannel) {
			if (value.isEmpty()) {
				value += getSpecialPrefix(user) + online.getColor() + online.getName();
			} else {
				value += ChatColor.WHITE + ", " + getSpecialPrefix(user) + online.getColor() + online.getName();
			}
		}
		BenCmd.getLocale().sendMessage(user, "misc.channel.list", name);
		user.sendMessage(ChatColor.GRAY + value);
	}

	public enum ChatLevel {

		OWNER("o", 6), COOWNER("c", 5), MOD("m", 4), VIP("v", 3), NORMAL("n", 2), MUTED("mu", 1), BANNED("b", 0), DEFAULT("d", -1);

		public static ChatLevel fromEntry(String entry) {
			for (ChatLevel l : ChatLevel.values()) {
				if (l.getEntry().equals(entry)) {
					return l;
				}
			}
			return ChatLevel.DEFAULT;
		}

		private String	entry;
		private int		level;

		private ChatLevel(String entry, int level) {
			this.entry = entry;
			this.level = level;
		}

		public String getEntry() {
			return entry;
		}

		public int getLevel() {
			return level;
		}
	}

	public void prepDelete() {
		while (inChannel.size() > 0) {
			inChannel.get(0).leaveChannel(false);
		}
		while (spies.size() > 0) {
			kickSpy(spies.get(0));
		}
	}

	public long getDelDanger() {
		return delDanger;
	}

	public void setDelDanger(long delDanger) {
		this.delDanger = delDanger;
	}
}
