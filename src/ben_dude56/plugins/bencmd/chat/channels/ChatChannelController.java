package ben_dude56.plugins.bencmd.chat.channels;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import ben_dude56.plugins.bencmd.BenCmd;

//TODO For version 1.1.0: Completely rewrite code. Current code is too messy.
public class ChatChannelController extends Properties {
	private static final long serialVersionUID = 0L;
	private List<ChatChannel> channels;
	private List<ChatChannel> tempchannels;
	String path;
	Logger log = Logger.getLogger("minecraft");
	BenCmd plugin;
	boolean channelsActive;

	public ChatChannelController(String pathName, BenCmd instance) {
		path = pathName;
		plugin = instance;
		this.loadFile();
		this.loadChannels();
		channelsActive = true;
		tempchannels = new ArrayList<ChatChannel>();
	}

	public ChatChannelController(String pathName, BenCmd instance,
			boolean active) {
		path = pathName;
		plugin = instance;
		channelsActive = active;
		this.loadFile();
		if (active) {
			this.loadChannels();
		}
		tempchannels = new ArrayList<ChatChannel>();
	}

	public void reloadChannels() {
		this.loadFile();
		this.loadChannels();
	}

	public void saveAll() {
		this.saveChannels();
		this.saveFile();
	}

	private void loadFile() {
		File file = new File(path); // Prepare the file
		if (!file.exists()) {
			try {
				file.createNewFile(); // If the file doesn't exist, create it!
			} catch (IOException ex) {
				// If you can't, produce an error.
				log.severe("BenCmd had a problem:");
				ex.printStackTrace();
				return;
			}
		}
		try {
			load(new FileInputStream(file)); // Load the values
		} catch (IOException ex) {
			// If you can't, produce an error.
			log.severe("BenCmd had a problem:");
			ex.printStackTrace();
		}
	}

	private void loadChannels() {
		channels = new ArrayList<ChatChannel>();
		for (int i = 0; i < this.values().size(); i++) {
			try {
				channels.add(new ChatChannel(
						(String) this.keySet().toArray()[i], (String) this
								.values().toArray()[i], plugin));
			} catch (Exception e) {
				log.warning("ChatChannel "
						+ (String) this.keySet().toArray()[i]
						+ " couldn't be created!");
			}
		}
		if (getChannel("General") == null) {
			log.severe("ChatChannel General doesn't exist! Turning off Chat Channels...");
			plugin.mainProperties.setProperty("channelsEnabled", "false");
			plugin.mainProperties.saveFile("-BenCmd Main Config-");
			return;
		}
	}

	private void saveFile() {
		File file = new File(path);
		if (file.exists()) {
			try {
				store(new FileOutputStream(file), "-BenCmd Channel Config-");
			} catch (IOException e) {
				System.out.println("BenCmd had a problem:");
				e.printStackTrace();
			}
		}
	}

	private void saveChannels() {
		this.clear();
		for (ChatChannel channel : channels) {
			this.put(channel.getName(), channel.getValue());
		}
	}

	public boolean isActive() {
		return channelsActive;
	}

	public ChatChannel getChannel(String name) {
		if (!channelsActive) {
			return null;
		}
		for (ChatChannel channel : channels) {
			if (channel.getName().equalsIgnoreCase(name)) {
				return channel;
			}
		}
		for (ChatChannel channel : tempchannels) {
			if (channel.getName().equalsIgnoreCase(name)) {
				return channel;
			}
		}
		return null;
	}
}
