package ben_dude56.plugins.bencmd;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import ben_dude56.plugins.bencmd.chat.channels.ChatChannel;
import ben_dude56.plugins.bencmd.chat.channels.ChatChannel.ChatLevel;

public class User extends ActionableUser {
	BenCmd plugin;
	private ChatChannel activeChannel;
	private List<ChatChannel> spying;

	public static User matchUser(String name, BenCmd instance) {
		for (Player online : instance.getServer().getOnlinePlayers()) {
			if (online.getName().equalsIgnoreCase(name)) {
				return new User(instance, online);
			}
		}
		return null;
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
	public User(BenCmd instance, Player entity) throws NullPointerException {
		super(instance, entity);
		plugin = instance;
		activeChannel = null;
		spying = new ArrayList<ChatChannel>();
	}

	/**
	 * Creates an ActionableUser corresponding to the console.
	 * 
	 * @param instance
	 *            The BenCmd Plugin reference to point to
	 */
	public User(BenCmd instance) {
		super(instance);
		plugin = instance;
	}
	
	public boolean inChannel() {
		return (activeChannel == null);
	}
	
	public boolean joinChannel(ChatChannel channel) {
		if(inChannel()) {
			getActiveChannel().leaveChannel(this);
		}
		if(channel.joinChannel(this) != ChatLevel.BANNED) {
			activeChannel = channel;
			return true;
		} else {
			return false;
		}
	}
	
	public void leaveChannel() {
		getActiveChannel().leaveChannel(this);
	}
	
	public ChatChannel getActiveChannel() {
		return activeChannel;
	}
	
	public boolean spyChannel(ChatChannel channel) {
		if(channel.Spy(this)) {
			spying.add(channel);
			return true;
		} else {
			return false;
		}
	}
	
	public boolean unspyChannel(ChatChannel channel) {
		if(channel.Unspy(this)) {
			spying.remove(channel);
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
