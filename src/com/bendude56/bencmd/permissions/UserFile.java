package com.bendude56.bencmd.permissions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.util.FileUtil;

import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.BenCmdFile;
import com.bendude56.bencmd.User;

@SuppressWarnings("unused")
public class UserFile extends BenCmdFile {
	HashMap<String, InternalUser>	users	= new HashMap<String, InternalUser>();

	public UserFile() {
		super("users.db", "--BenCmd User File--", true);
		loadFile();
		loadAll();
	}

	public void updateUser(InternalUser user, boolean saveFile) {
		String value = "";
		for (String perm : user.getPermissions(false, true)) {
			if (value.isEmpty()) {
				value += perm;
			} else {
				value += "," + perm;
			}
		}
		value += "|";
		for (String ignored : user.getIgnoring()) {
			if (value.endsWith("|")) {
				value += ignored;
			} else {
				value += "," + ignored;
			}
		}
		getFile().put(user.getName(), value);
		users.put(user.getName(), user);
		if (saveFile)
			saveFile();
	}

	public void removeUser(PermissionUser user) {
		for (PermissionGroup g : BenCmd.getPermissionManager().getGroupFile().getUserGroups(user)) {
			g.removeUser(user);
		}
		getFile().remove(user.getName());
		users.remove(user.getName());
		saveFile();
	}

	public void loadAll() {
		users.clear();
		for (int i = 0; i < getFile().size(); i++) {
			String name = (String) getFile().keySet().toArray()[i];
			if (getFile().getProperty(name).contains("|")) {
				List<String> permissions = new ArrayList<String>();
				permissions.addAll(Arrays.asList(getFile().getProperty(name).split("\\|", -1)[0].split(",")));
				List<String> ignored = new ArrayList<String>();
				ignored.addAll(Arrays.asList(getFile().getProperty(name).split("\\|", -1)[1].split(",")));
				users.put(name, new InternalUser(name, permissions, ignored));
			} else {
				List<String> permissions = new ArrayList<String>();
				permissions.addAll(Arrays.asList(getFile().getProperty(name).split(",")));
				BenCmd.log(Level.WARNING, "User " + name + " is missing user-ignore metadata! Adding...");
				InternalUser u;
				users.put(name, u = new InternalUser(name, permissions, new ArrayList<String>()));
				updateUser(u, true);
			}
		}
		saveFile();
	}

	public void saveAll() {
		for (Map.Entry<String, InternalUser> e : users.entrySet()) {
			updateUser(e.getValue(), false);
		}
		saveFile();
	}

	public HashMap<String, InternalUser> listUsers() {
		return users;
	}

	protected InternalUser getInternal(String userName) {
		for (InternalUser user : users.values()) {
			if (user.getName().equalsIgnoreCase(userName)) {
				return user;
			}
		}
		return null;
	}

	public PermissionUser getUser(String userName) {
		for (InternalUser user : users.values()) {
			if (user.getName().equalsIgnoreCase(userName)) {
				return new PermissionUser(user);
			}
		}
		return null;
	}

	public boolean userExists(String userName) {
		return getUser(userName) != null;
	}

	public void addUser(PermissionUser user) {
		updateUser(user.getInternal(), true);
	}

	public List<User> allWithPerm(String perm) {
		List<User> users = new ArrayList<User>();
		for (User user : User.getActiveUsers().values()) {
			if (user.hasPerm(perm, true, true)) {
				users.add(user);
			}
		}
		return users;
	}
	
	public void correctCase(PermissionUser user, String name) {
		String oldName = user.getName();
		if (!oldName.equals(name)) {
			InternalUser i;
			users.put(name, i = new InternalUser(name, users.get(oldName).getPermissions(false, true), users.get(oldName).getIgnoring()));
			getFile().remove(oldName);
			users.remove(oldName);
			updateUser(i, true);
		}
	}
}
