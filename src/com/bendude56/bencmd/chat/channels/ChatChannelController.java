package com.bendude56.bencmd.chat.channels;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import com.bendude56.bencmd.BenCmdFile;
import com.bendude56.bencmd.User;
import com.bendude56.bencmd.chat.channels.ChatChannel.ChatLevel;
import com.bendude56.bencmd.permissions.PermissionUser;


public class ChatChannelController extends BenCmdFile {
	private List<ChatChannel> channels = new ArrayList<ChatChannel>();

	public ChatChannelController() {
		super("channels.db", "--BenCmd Channel File--", true);
		loadFile();
		loadAll();
	}

	public void loadAll() {
		channels.clear();
		for (int i = 0; i < getFile().size(); i++) {
			ChatChannel channel = ChatChannel
					.getChannel(this, (String) getFile().keySet().toArray()[i],
							(String) getFile().values().toArray()[i]);
			if (channel != null) {
				channels.add(channel);
			}
		}
	}

	protected void saveChannel(ChatChannel channel) {
		getFile().put(channel.getName(), channel.getValue());
		saveFile();
	}
	
	public void saveAll() {
		for (ChatChannel channel : channels) {
			getFile().put(channel.getName(), channel.getValue());
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
					if (channel.hasDisplayName()) {
						value += channel.getName() + " ("
								+ channel.getDisplayName() + ")";
					} else {
						value += channel.getName();
					}
				} else {
					if (channel.hasDisplayName()) {
						value += ", " + channel.getName() + " ("
								+ channel.getDisplayName() + ")";
					} else {
						value += ", " + channel.getName();
					}
				}
			}
		}
		if (value.isEmpty()) {
			user.sendMessage(ChatColor.GRAY
					+ "There are no chat channels that you can join...");
		} else {
			user.sendMessage(ChatColor.GRAY
					+ "The following chat channels are open to you:");
			user.sendMessage(ChatColor.GRAY + value);
		}
	}

	protected void addChannel(String name, User owner) {
		ChatChannel channel;
		channels.add(channel = new ChatChannel(this, name, owner,
				new ArrayList<PermissionUser>(),
				new ArrayList<PermissionUser>(),
				new ArrayList<PermissionUser>(),
				new ArrayList<PermissionUser>(), ChatLevel.DEFAULT,
				"Change this using /channel motd <message>", name));
		saveChannel(channel);
		owner.joinChannel(channel);
	}

	protected void removeChannel(ChatChannel channel) {
		channel.prepDelete();
		channels.remove(channel);
		getFile().remove(channel.getName());
		saveFile();
	}
}
