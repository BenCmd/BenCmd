package com.bendude56.bencmd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.bendude56.bencmd.chat.channels.ChatChannel;
import com.bendude56.bencmd.chat.channels.ChatChannel.ChatLevel;

public class User extends ActionableUser {
	private static HashMap<String, ChatChannel>			activeChannels	= new HashMap<String, ChatChannel>();
	private static HashMap<String, List<ChatChannel>>	spyingChannels	= new HashMap<String, List<ChatChannel>>();
	private static HashMap<String, User>				activeUsers		= new HashMap<String, User>();
	private ChatChannel									activeChannel;
	private List<ChatChannel>							spying;

	public static User matchUser(String name) {
		for (Player online : Bukkit.getOnlinePlayers()) {
			if (online.getName().equalsIgnoreCase(name) || online.getDisplayName().equalsIgnoreCase(name)) {
				return User.getUser(online);
			}
		}
		return null;
	}

	public static HashMap<String, User> getActiveUsers() {
		return activeUsers;
	}

	public static User matchUserIgnoreCase(String name) {
		return User.matchUser(name);
	}

	public static void finalizeAll() {
		User.activeUsers.clear();
	}

	public static void finalizeUser(User user) {
		if (User.activeUsers.containsKey(user.getName())) {
			User.activeUsers.remove(user.getName());
		}
		assert (!User.activeUsers.containsKey(user.getName()));
	}

	public static User getUser(CommandSender s) {
		if (s instanceof ConsoleCommandSender) {
			return getUser();
		} else {
			if (User.activeUsers.containsKey(s.getName())) {
				return User.activeUsers.get(s.getName());
			} else {
				return new User(s);
			}
		}
	}

	public static User getUser() {
		return new User();
	}

	/**
	 * Creates a User corresponding to a player entity.
	 * 
	 * @param instance
	 *            The BenCmd Plugin reference to point to
	 * @param entity
	 *            The player entity that this ActionableUser should point to.
	 * @throws NullPointerException
	 */
	private User(CommandSender s) throws NullPointerException {
		super(s);
		if (User.activeChannels.containsKey(s.getName())) {
			setActiveChannel(User.activeChannels.get(s.getName()));
		} else {
			setActiveChannel(null);
		}
		if (User.spyingChannels.containsKey(s.getName())) {
			spying = User.spyingChannels.get(s.getName());
		} else {
			spying = new ArrayList<ChatChannel>();
		}
		User.activeUsers.put(s.getName(), this);
	}

	/**
	 * Creates an ActionableUser corresponding to the console.
	 * 
	 * @param instance
	 *            The BenCmd Plugin reference to point to
	 */
	private User() {
		super();
	}

	public boolean inChannel() {
		return (getActiveChannel() != null);
	}

	public void pushActive() {
		User.activeChannels.put(getHandle().getName(), getActiveChannel());
	}

	private void pushSpying() {
		User.spyingChannels.put(getHandle().getName(), spying);
	}

	public boolean joinChannel(ChatChannel channel) {
		if (inChannel()) {
			getActiveChannel().leaveChannel(this);
		}
		if (channel.joinChannel(this) != ChatLevel.BANNED) {
			setActiveChannel(channel);
			pushActive();
			return true;
		} else {
			return false;
		}
	}

	public void leaveChannel() {
		getActiveChannel().leaveChannel(this);
		setActiveChannel(null);
		pushActive();
	}

	public ChatChannel getActiveChannel() {
		return activeChannel;
	}

	public boolean spyChannel(ChatChannel channel) {
		if (channel.Spy(this)) {
			spying.add(channel);
			pushSpying();
			return true;
		} else {
			return false;
		}
	}

	public boolean unspyChannel(ChatChannel channel) {
		if (channel.Unspy(this)) {
			spying.remove(channel);
			pushSpying();
			return true;
		} else {
			return false;
		}
	}

	public void unspyAll() {
		for (ChatChannel channel : spying) {
			unspyChannel(channel);
		}
	}

	public void setActiveChannel(ChatChannel activeChannel) {
		this.activeChannel = activeChannel;
	}
}
