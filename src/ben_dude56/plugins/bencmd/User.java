package ben_dude56.plugins.bencmd;

import org.bukkit.entity.Player;

import ben_dude56.plugins.bencmd.chat.channels.ChatChannel;

public class User extends ActionableUser {
	BenCmd plugin;
	private ChatChannel activeChannel;

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

	/**
	 * Changes the active channel of this user to reflect a different channel
	 * 
	 * @param channel
	 *            The channel to enter
	 * @return Returns whether they joined successfully
	 */
	public boolean ActivateChannel(ChatChannel channel) {
		if (inChannel()) {
			DeactivateChannel();
		}
		if (channel.JoinChat(this) != ChatChannel.ChatterType.DISALLOW) {
			activeChannel = channel;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Forces the user to leave their active channel
	 */
	public void DeactivateChannel() {
		activeChannel.LeaveChannel(this);
		activeChannel = null;
	}

	/**
	 * Checks if the user is currently talking in a channel
	 * 
	 * @return Returns if the user is in a channel
	 */
	public boolean inChannel() {
		return (activeChannel != null);
	}

	/**
	 * Used to check what channel a user is currently in, or manipulate or
	 * otherwise interact with, a user's active channel.
	 * 
	 * @return Returns the channel the user is active in
	 */
	public ChatChannel getActiveChannel() {
		return activeChannel;
	}

	/**
	 * Causes the user to join a new channel.
	 * 
	 * @param name
	 *            The name of the channel to join
	 * @return Whether the channel activated successfully
	 */
	public boolean JoinActiveChannel(String name) {
		ChatChannel channel;
		if ((channel = plugin.channels.getChannel(name)) == null) {
			return false;
		}
		return ActivateChannel(channel);
	}

	/**
	 * Leaves the user's active channel
	 * 
	 * @return Whether the channel was left successfully
	 * @deprecated Use {@link #DeactivateChannel()}
	 */
	public boolean LeaveActiveChannel() {
		if (!this.inChannel()) {
			return false;
		}
		activeChannel.LeaveChannel(this);
		activeChannel = null;
		return true;
	}
}
