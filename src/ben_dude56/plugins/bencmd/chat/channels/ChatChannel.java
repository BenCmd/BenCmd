package ben_dude56.plugins.bencmd.chat.channels;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.ChatColor;

import ben_dude56.plugins.bencmd.BenCmd;
import ben_dude56.plugins.bencmd.User;
import ben_dude56.plugins.bencmd.chat.ChatChecker;
import ben_dude56.plugins.bencmd.chat.SlowMode;
import ben_dude56.plugins.bencmd.permissions.PermissionUser;

public class ChatChannel {
	// Constants
	Logger log = Logger.getLogger("minecraft");

	// General Information
	private String name;
	private ChatterType autoLevel;
	private BenCmd plugin;
	private SlowMode slow;

	// User permissions lists
	private List<PermissionUser> talkList;
	private List<PermissionUser> listenList;
	private List<PermissionUser> modList;

	// User login lists
	private List<User> listeners;
	private List<User> talkers;
	private List<User> mods;
	
	//Other
	private boolean temp;

	public ChatChannel(String key, String value, BenCmd instance) {
		plugin = instance;
		name = key;
		temp = false;
		
		listenList = new ArrayList<PermissionUser>();
		if(!value.split("/")[0].isEmpty()) {
			for(String listen : value.split("/")[0].split(",")) {
				listenList.add(PermissionUser.matchUser(listen, instance));
			}
		}
		
		talkList = new ArrayList<PermissionUser>();
		if(!value.split("/")[1].isEmpty()) {
			for(String listen : value.split("/")[1].split(",")) {
				talkList.add(PermissionUser.matchUser(listen, instance));
			}
		}
		
		modList = new ArrayList<PermissionUser>();
		if(!value.split("/")[2].isEmpty()) {
			for(String listen : value.split("/")[2].split(",")) {
				modList.add(PermissionUser.matchUser(listen, instance));
			}
		}
		if(value.split("/")[3].equalsIgnoreCase("d")) {
			autoLevel = ChatterType.DISALLOW;
		} else if(value.split("/")[3].equalsIgnoreCase("l")) {
			autoLevel = ChatterType.LISTEN;
		} else if(value.split("/")[3].equalsIgnoreCase("t")) {
			autoLevel = ChatterType.TALK;
		} else {
			throw new IllegalArgumentException();
		}
		
		slow = new SlowMode(plugin, plugin.mainProperties.getInteger(
				"slowTime", 10000));
	}
	
	public ChatChannel(String name, User starter) {
		this.name = name;
		this.autoLevel = ChatterType.DISALLOW;
		this.temp = true;
		
		listenList = new ArrayList<PermissionUser>();
		listenList.add(starter);
		talkList = new ArrayList<PermissionUser>();
		talkList.add(starter);
		modList = new ArrayList<PermissionUser>();
		
		slow = new SlowMode(plugin, plugin.mainProperties.getInteger(
				"slowTime", 10000));
		
		if(starter.inChannel()) {
			starter.LeaveActiveChannel();
		}
		starter.ActivateChannel(this);
		this.JoinAsMod(starter);
	}
	
	public boolean isTemporary() {
		return temp;
	}
	
	public void update() {
		
	}
	
	public List<User> getUsers() {
		return listeners;
	}
	
	public String getValue() {
		String value = "";
		boolean commaAdd = false;
		for(PermissionUser pUser : listenList) {
			if(commaAdd) {
				value += ",";
			} else {
				commaAdd = true;
			}
			value += pUser.getName();
		}
		value += "/";
		commaAdd = false;
		for(PermissionUser pUser : talkList) {
			if(commaAdd) {
				value += ",";
			} else {
				commaAdd = true;
			}
			value += pUser.getName();
		}
		value += "/";
		commaAdd = false;
		for(PermissionUser pUser : modList) {
			if(commaAdd) {
				value += ",";
			} else {
				commaAdd = true;
			}
			value += pUser.getName();
		}
		if(autoLevel == ChatterType.DISALLOW) {
			value += "/d";
		} else if (autoLevel == ChatterType.LISTEN) {
			value += "/l";
		} else if (autoLevel == ChatterType.TALK) {
			value += "/t";
		}
		return value;
	}
	
	/*private User getListener(PermissionUser pUser) {
		for (User listener : listeners) {
			if (pUser.getName().equalsIgnoreCase(listener.getName())) {
				return listener;
			}
		}
		return null;
	}*/

	public boolean canMod(PermissionUser user) {
		for (PermissionUser mod : modList) {
			if (user.getName().equalsIgnoreCase(mod.getName())) {
				return true;
			}
		}
		return false;
	}

	public boolean canTalk(PermissionUser user) {
		for (PermissionUser talker : talkList) {
			if (user.getName().equalsIgnoreCase(talker.getName())) {
				return (autoLevel != ChatterType.TALK);
			}
		}
		return (autoLevel == ChatterType.TALK);
	}

	public boolean canListen(PermissionUser user) {
		for (PermissionUser listener : listenList) {
			if (user.getName().equalsIgnoreCase(listener.getName())) {
				return (autoLevel != ChatterType.LISTEN);
			}
		}
		return (autoLevel == ChatterType.LISTEN);
	}

	private boolean JoinAsMod(User user) {
		if (!canMod(user)) {
			return false;
		}
		listeners.add(user);
		talkers.add(user);
		mods.add(user);
		return true;
	}

	private boolean JoinAsTalker(User user) {
		if (!canListen(user) || !canTalk(user)) {
			return false;
		}
		listeners.add(user);
		talkers.add(user);
		return true;
	}

	private boolean JoinAsListener(User user) {
		if (!canListen(user)) {
			return false;
		}
		listeners.add(user);
		return true;
	}

	public boolean isMod(User user) {
		for (User mod : mods) {
			if (user.getName().equalsIgnoreCase(mod.getName())) {
				return true;
			}
		}
		return false;
	}

	public boolean isTalker(User user) {
		for (User talker : talkers) {
			if (user.getName().equalsIgnoreCase(talker.getName())) {
				return true;
			}
		}
		return false;
	}

	public boolean isListener(User user) {
		for (User listener : listeners) {
			if (user.getName().equalsIgnoreCase(listener.getName())) {
				return true;
			}
		}
		return false;
	}

	public ChatterType JoinChat(User user) {
		if (JoinAsMod(user)) {
			return ChatterType.MOD;
		} else if (JoinAsTalker(user)) {
			return ChatterType.TALK;
		} else if (JoinAsListener(user)) {
			return ChatterType.LISTEN;
		} else {
			return ChatterType.DISALLOW;
		}
	}

	public void sendMessage(String message) {
		for (User receiver : listeners) {
			receiver.sendMessage(message);
		}
	}
	
	public void ToggleSlow(User user) {
		if (slow.isEnabled()) {
			slow.DisableSlow();
			log.info(user.getName() + " has disabled slow mode. (Channel: " + name + ")");
			this.sendMessage(
					ChatColor.GRAY + "Slow mode has been disabled.");
		} else {
			slow.EnableSlow();
			log.info(user.getName() + " has enabled slow mode. (Channel: " + name + ")");
			this.sendMessage(
					ChatColor.GRAY
							+ "Slow mode has been enabled. You must wait "
							+ (slow.getDefTime() / 1000)
							+ " seconds between each chat message.");
		}
	}

	public void sendChat(String message, User sender) {
		if (!this.canTalk(sender)) {
			sender.sendMessage(ChatColor.GRAY
					+ plugin.mainProperties.getString("muteMessage",
							"You are muted..."));
			return;
		}
		boolean blocked = ChatChecker.checkBlocked(message, plugin);
		if (blocked) {
			sender.sendMessage(ChatColor.GRAY
					+ plugin.mainProperties.getString("blockMessage",
							"You used a blocked word..."));
			return;
		}
		String prefix;
		log.info("(" + name + ") " + sender.getName() + ": " + message);
		if (sender.getGroup() != null
				&& !(prefix = sender.getGroup().getPrefix()).isEmpty()) {
			message = ChatColor.GRAY + "(" + name + ") "
					+ sender.getGroup().getPrefixColor() + "[" + prefix + "] "
					+ sender.getColor() + sender.getName() + ": "
					+ ChatColor.WHITE + message;
			this.sendMessage(message);
		} else {
			message = ChatColor.GRAY + "(" + name + ") " + sender.getColor()
					+ sender.getName() + ": " + ChatColor.WHITE + message;
			this.sendMessage(message);
		}
	}

	public String getName() {
		return name;
	}

	protected void setName(String newName) {
		name = newName;
	}

	public boolean JoinChat(User user, ChatterType type) {
		switch (type) {
		case LISTEN:
			return JoinAsListener(user);
		case TALK:
			return JoinAsTalker(user);
		case MOD:
			return JoinAsMod(user);
		default:
			return false;
		}
	}
	
	protected void addMod(PermissionUser newMod) {
		switch(autoLevel) {
		case LISTEN:
			addTalk(newMod);
			removeListen(newMod);
			break;
		case TALK:
			removeTalk(newMod);
			removeListen(newMod);
			break;
		default:
			addListen(newMod);
			addTalk(newMod);
			break;
		}
		modList.add(newMod);
		for(User listener : listeners) {
			if(newMod.getName().equalsIgnoreCase(listener.getName())) {
				UpdateAsMod(listener);
				listener.sendMessage(ChatColor.GREEN + "You are now a mod in your current channel!");
				return;
			}
		}
	}
	
	private void UpdateAsMod(User newMod) {
		this.mods.add(newMod);
	}
	
	private void UpdateAsNonMod(User oldMod) {
		for(int i = 0; i < mods.size(); i++) {
			if(mods.get(i).getName().equalsIgnoreCase(oldMod.getName())) {
				mods.remove(i);
				break;
			}
		}
	}
	
	private void UpdateAsTalker(User newTalker) {
		this.talkers.add(newTalker);
	}
	
	private void UpdateAsNonTalker(User oldTalker) {
		for(int i = 0; i < talkers.size(); i++) {
			if(talkers.get(i).getName().equalsIgnoreCase(oldTalker.getName())) {
				talkers.remove(i);
				oldTalker.sendMessage(ChatColor.RED + "You are no longer allowed to talk in your current channel!");
				break;
			}
		}
	}
	
	protected void ListenOnly(User listening) {
		UpdateAsNonMod(listening);
		UpdateAsNonTalker(listening);
	}
	
	public void LeaveChannel(User leaving) {
		UpdateAsNonMod(leaving);
		UpdateAsNonTalker(leaving);
		for(int i = 0; i < listeners.size(); i++) {
			if(listeners.get(i).getName().equalsIgnoreCase(leaving.getName())) {
				listeners.remove(i);
				break;
			}
		}
		this.sendMessage(leaving.getColor() + leaving.getName() + ChatColor.WHITE + " has left the channel");
	}
	
	protected void removeMod(PermissionUser oldMod) {
		for(int i = 0; i < modList.size(); i++) {
			if(modList.get(i).getName().equalsIgnoreCase(oldMod.getName())) {
				modList.remove(i);
				break;
			}
		}
		for(User listener : listeners) {
			if(oldMod.getName().equalsIgnoreCase(listener.getName())) {
				UpdateAsNonMod(listener);
				listener.sendMessage(ChatColor.RED + "You are no longer a mod in your current channel!");
				return;
			}
		}
	}
	
	protected boolean voiceUser(PermissionUser toVoice) {
		if(this.canTalk(toVoice)) {
			return false;
		} else {
			return true;
		}
	}
	
	protected void addListen(PermissionUser newListen) {
		listenList.add(newListen);
		if(autoLevel != ChatterType.DISALLOW) {
			return;
		}
		for(User listener : listeners) {
			if(newListen.getName().equalsIgnoreCase(listener.getName())) {
				this.LeaveChannel(listener);
				return;
			}
		}
	}
	
	protected void removeListen(PermissionUser oldListen) {
		for(int i = 0; i < talkList.size(); i++) {
			if(talkList.get(i).getName().equalsIgnoreCase(oldListen.getName())) {
				talkList.remove(i);
				break;
			}
		}
		if(autoLevel == ChatterType.DISALLOW) {
			return;
		}
		for(User listener : listeners) {
			if(oldListen.getName().equalsIgnoreCase(listener.getName())) {
				this.LeaveChannel(listener);
				return;
			}
		}
	}
	
	protected void addTalk(PermissionUser newTalker) {
		switch(autoLevel) {
		case LISTEN:
			removeListen(newTalker);
			break;
		case TALK:
			removeListen(newTalker);
			break;
		default:
			addListen(newTalker);
			break;
		}
		talkList.add(newTalker);
		for(User listener : listeners) {
			if(newTalker.getName().equalsIgnoreCase(listener.getName())) {
				UpdateAsTalker(listener);
				listener.sendMessage(ChatColor.GREEN + "You can now talk in your current channel!");
				return;
			}
		}
	}
	
	protected void removeTalk(PermissionUser oldTalker) {
		for(int i = 0; i < talkList.size(); i++) {
			if(talkList.get(i).getName().equalsIgnoreCase(oldTalker.getName())) {
				talkList.remove(i);
				break;
			}
		}
		for(User listener : listeners) {
			if(oldTalker.getName().equalsIgnoreCase(listener.getName())) {
				UpdateAsNonTalker(listener);
				listener.sendMessage(ChatColor.RED + "You can no longer talk in your current channel!");
				return;
			}
		}
	}
	
	protected ChatterType getAutoLevel() {
		return autoLevel;
	}

	enum ChatterType {
		LISTEN, TALK, MOD, DISALLOW
	}
}
