package com.bendude56.bencmd.chat.channels;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;

import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.BenCmdFile;
import com.bendude56.bencmd.User;
import com.bendude56.bencmd.chat.channels.ChatChannel.ChatLevel;

public class ChatChannelController extends BenCmdFile {
	private List<ChatChannel>	channels	= new ArrayList<ChatChannel>();

	public ChatChannelController() {
		super("channels.db", "--BenCmd Channel File--", true);
		loadFile();
		loadAll();
	}

	public void loadAll() {
		channels.clear();
		for (int i = 0; i < getFile().size(); i++) {
			ChatChannel channel = ChatChannel.loadChannel((String) getFile().keySet().toArray()[i], (String) getFile().values().toArray()[i]);
			if (channel != null) {
				channels.add(channel);
			}
		}
	}

	protected void saveChannel(ChatChannel channel) {
		getFile().put(channel.getName(), channel.getSaveValue());
		saveFile();
	}

	public void saveAll() {
		for (ChatChannel channel : channels) {
			getFile().put(channel.getName(), channel.getSaveValue());
		}
		saveFile();
	}

	public ChatChannel getChannel(String name) {
		for (ChatChannel channel : channels) {
			if (channel.getName().equalsIgnoreCase(name)) {
				return channel;
			}
		}
		return null;
	}

	public void listChannels(User user) {
		String value = "";
		for (ChatChannel channel : channels) {
			if (channel.getLevel(user) != ChatLevel.BANNED) {
				if (value.isEmpty()) {
					value += channel.getName();
				} else {
					value += ", " + channel.getName();
				}
			}
		}
		if (value.isEmpty()) {
			BenCmd.getLocale().sendMessage(user, "command.channel.list.none");
		} else {
			BenCmd.getLocale().sendMessage(user, "command.channel.list.list");
			user.sendMessage(ChatColor.GRAY + value);
		}
	}

	public void addChannel(String name, User owner) {
		ChatChannel channel;
		channels.add(channel = ChatChannel.createChannel(name, owner.getName()));
		saveChannel(channel);
		owner.joinChannel(channel, true);
	}
	
	public void addChannel(ChatChannel channel) {
		channels.add(channel);
		saveChannel(channel);
	}

	public void removeChannel(ChatChannel channel, boolean prepDelete) {
		if (prepDelete) {
			channel.prepDelete();
		}
		channels.remove(channel);
		getFile().remove(channel.getName());
		saveFile();
	}
	
	public boolean channelExists(String name) {
		for (ChatChannel c : channels) {
			if (c.getName().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}
}
