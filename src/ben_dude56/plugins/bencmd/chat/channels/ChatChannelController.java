package ben_dude56.plugins.bencmd.chat.channels;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.bukkit.ChatColor;
import org.bukkit.util.FileUtil;

import ben_dude56.plugins.bencmd.BenCmd;
import ben_dude56.plugins.bencmd.User;
import ben_dude56.plugins.bencmd.chat.channels.ChatChannel.ChatLevel;
import ben_dude56.plugins.bencmd.permissions.PermissionUser;

public class ChatChannelController extends Properties {
	private static final long serialVersionUID = 0L;
	protected BenCmd plugin;
	private String fileName;
	private List<ChatChannel> channels;

	public ChatChannelController(String fileName, BenCmd instance) {
		plugin = instance;
		this.fileName = fileName;
		channels = new ArrayList<ChatChannel>();
		if (new File("plugins/BenCmd/_channels.db").exists()) {
			plugin.log.warning("Channel backup file found... Restoring...");
			if (FileUtil.copy(new File("plugins/BenCmd/_channels.db"), new File(
					fileName))) {
				new File("plugins/BenCmd/_channels.db").delete();
				plugin.log.info("Restoration suceeded!");
			} else {
				plugin.log.warning("Failed to restore from backup!");
			}
		}
		loadFile();
		loadChannels();
	}

	private void loadChannels() {
		for (int i = 0; i < this.size(); i++) {
			ChatChannel channel = ChatChannel
					.getChannel(this, (String) this.keySet().toArray()[i],
							(String) this.values().toArray()[i]);
			if (channel != null) {
				channels.add(channel);
			}
		}
	}

	protected void saveChannel(ChatChannel channel) {
		this.put(channel.getName(), channel.getValue());
		try {
			new File("plugins/BenCmd/_channels.db").createNewFile();
			if (!FileUtil.copy(new File(fileName), new File(
					"plugins/BenCmd/_channels.db"))) {
				plugin.log.warning("Failed to back up channel database!");
			}
		} catch (IOException e) {
			plugin.log.warning("Failed to back up channel database!");
		}
		saveFile("-BenCmd Channel List-");
		try {
			new File("plugins/BenCmd/_channels.db").delete();
		} catch (Exception e) { }
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
		this.remove(channel.getName());
		this.saveFile("-BenCmd Channel List-");
	}

	public void loadFile() {
		File file = new File(fileName);
		if (file.exists()) {
			try {
				load(new FileInputStream(file));
			} catch (IOException e) {
				System.out.println("BenCmd had a problem:");
				e.printStackTrace();
			}
		}
	}

	public void saveFile(String header) {
		File file = new File(fileName);
		if (file.exists()) {
			try {
				store(new FileOutputStream(file), header);
			} catch (IOException e) {
				System.out.println("BenCmd had a problem:");
				e.printStackTrace();
			}
		}
	}
}
