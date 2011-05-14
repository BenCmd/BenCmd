package ben_dude56.plugins.bencmd;

import org.bukkit.entity.Player;

import ben_dude56.plugins.bencmd.chat.channels.ChatChannel;

public class User extends ActionableUser {
	BenCmd plugin;
	private ChatChannel activeChannel;
	
	public static User matchUser(String name, BenCmd instance) {
		for(Player online : instance.getServer().getOnlinePlayers()) {
			if(online.getName().equalsIgnoreCase(name)) {
				return new User(instance, online);
			}
		}
		return null;
	}
	
	public User(BenCmd instance, Player entity) throws NullPointerException {
		super(instance, entity);
		plugin = instance;
		activeChannel = null;
	}
	
	public void ActivateChannel(ChatChannel channel) {
		activeChannel = channel;
	}
	
	public void DeactivateChannel() {
		activeChannel.LeaveChannel(this);
		activeChannel = null;
	}
	
	public User(BenCmd instance) {
		super(instance);
		plugin = instance;
	}

	public boolean inChannel() {
		return (activeChannel != null);
	}
	
	public ChatChannel getActiveChannel() {
		return activeChannel;
	}
	
	public boolean JoinActiveChannel(String name) {
		if(activeChannel != null) {
			activeChannel.LeaveChannel(this);
		}
		ChatChannel channel;
		if((channel = plugin.channels.getChannel(name)) == null) {
			return false;
		}
		channel.JoinChat(this);
		activeChannel = channel;
		return true;
	}
	
	public boolean LeaveActiveChannel() {
		if(!this.inChannel()) {
			return false;
		}
		activeChannel.LeaveChannel(this);
		activeChannel = null;
		return true;
	}
}
