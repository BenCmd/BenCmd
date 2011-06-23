package ben_dude56.plugins.bencmd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Player;

import ben_dude56.plugins.bencmd.chat.channels.ChatChannel;
import ben_dude56.plugins.bencmd.chat.channels.ChatChannel.ChatLevel;

public class User extends ActionableUser {
	BenCmd plugin;
	private static HashMap<String, ChatChannel> activeChannels = new HashMap<String, ChatChannel>();
	private static HashMap<String, List<ChatChannel>> spyingChannels = new HashMap<String, List<ChatChannel>>();
	private static HashMap<String, User> activeUsers = new HashMap<String, User>();
	private ChatChannel activeChannel;
	private List<ChatChannel> spying;
	private Player player;

	public static User matchUser(String name, BenCmd instance) {
		for (Player online : instance.getServer().getOnlinePlayers()) {
			if (online.getName().equalsIgnoreCase(name) || online.getDisplayName().equalsIgnoreCase(name)) {
				return User.getUser(instance, online);
			}
		}
		return null;
	}
	
	public static HashMap<String, User> getActiveUsers() {
		return activeUsers;
	}

	public static User matchUserIgnoreCase(String name, BenCmd instance) {
		return User.matchUser(name, instance);
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

	public static User getUser(BenCmd instance, Player entity) {
		if (User.activeUsers.containsKey(entity.getName())) {
			return User.activeUsers.get(entity.getName());
		} else {
			return new User(instance, entity);
		}
	}

	public static User getUser(BenCmd instance) {
		return new User(instance);
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
	private User(BenCmd instance, Player entity) throws NullPointerException {
		super(instance, entity);
		plugin = instance;
		player = entity;
		if (User.activeChannels.containsKey(entity.getName())) {
			activeChannel = User.activeChannels.get(entity.getName());
		} else {
			activeChannel = null;
		}
		if (User.spyingChannels.containsKey(entity.getName())) {
			spying = User.spyingChannels.get(entity.getName());
		} else {
			spying = new ArrayList<ChatChannel>();
		}
		User.activeUsers.put(entity.getName(), this);
	}

	/**
	 * Creates an ActionableUser corresponding to the console.
	 * 
	 * @param instance
	 *            The BenCmd Plugin reference to point to
	 */
	private User(BenCmd instance) {
		super(instance);
		plugin = instance;
	}

	public boolean inChannel() {
		return (activeChannel != null);
	}

	private void pushActive() {
		User.activeChannels.put(player.getName(), activeChannel);
	}

	private void pushSpying() {
		User.spyingChannels.put(player.getName(), spying);
	}

	public boolean joinChannel(ChatChannel channel) {
		if (inChannel()) {
			getActiveChannel().leaveChannel(this);
		}
		if (channel.joinChannel(this) != ChatLevel.BANNED) {
			activeChannel = channel;
			pushActive();
			return true;
		} else {
			return false;
		}
	}

	public void leaveChannel() {
		getActiveChannel().leaveChannel(this);
		activeChannel = null;
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
}
