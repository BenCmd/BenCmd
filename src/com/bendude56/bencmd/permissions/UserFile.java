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
	HashMap<String, InternalUser> users = new HashMap<String, InternalUser>();

	public UserFile() {
		super("users.db", "--BenCmd User File--", true);
		loadFile();
		loadAll();
	}

	public void updateUser(InternalUser user, boolean saveFile) {
		String value = "";
		for (String perm : user.getPermissions(false)) {
			if (value.isEmpty()) {
				value += perm;
			} else {
				value += "," + perm;
			}
		}
		getFile().put(user.getName(), value);
		users.put(user.getName(), user);
		if (saveFile)
			saveFile();
	}

	public void removeUser(PermissionUser user) {
		getFile().remove(user.getName());
		users.remove(user.getName());
		saveFile();
	}

	public void loadAll() {
		users.clear();
		for (int i = 0; i < getFile().size(); i++) {
			String name = (String) getFile().keySet().toArray()[i];
			List<String> permissions = new ArrayList<String>();
			permissions
					.addAll(Arrays.asList(getFile().getProperty(name).split(",")));
			users.put(name,
					new InternalUser(name, permissions));
		}
	}
	
	public void saveAll() {
		for (Map.Entry<String, InternalUser> e : users.entrySet()) {
			updateUser(e.getValue(), false);
		}
		
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
}
