package ben_dude56.plugins.bencmd.permissions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.util.FileUtil;

import ben_dude56.plugins.bencmd.User;

@SuppressWarnings("unused")
public class UserFile extends Properties {
	private static final long serialVersionUID = 0L;
	MainPermissions mainPerm;
	HashMap<String, InternalUser> users = new HashMap<String, InternalUser>();

	public UserFile(MainPermissions mainPermissions) {
		mainPerm = mainPermissions; // Initialize the value of the parent
		if (new File("plugins/BenCmd/_users.db").exists()) {
			mainPerm.plugin.log.warning("User backup file found... Restoring...");
			if (FileUtil.copy(new File("plugins/BenCmd/_users.db"), new File(
					"plugins/BenCmd/users.db"))) {
				new File("plugins/BenCmd/_users.db").delete();
				mainPerm.plugin.log.info("Restoration suceeded!");
			} else {
				mainPerm.plugin.log.warning("Failed to restore from backup!");
			}
		}
		loadFile();
		loadUsers();
	}

	public void loadFile() {
		File file = new File("plugins/BenCmd/users.db"); // Prepare the file
		if (!file.exists()) {
			try {
				file.createNewFile(); // If the file doesn't exist, create it!
			} catch (IOException ex) {
				// If you can't, produce an error.
				mainPerm.plugin.log.severe("BenCmd had a problem:");
				ex.printStackTrace();
				return;
			}
		}
		try {
			load(new FileInputStream(file)); // Load the values
		} catch (IOException ex) {
			// If you can't, produce an error.
			mainPerm.plugin.log.severe("BenCmd had a problem:");
			ex.printStackTrace();
		}
	}

	public void updateUser(InternalUser user) {
		String value = "";
		for (String perm : user.getPermissions(false)) {
			if (value.isEmpty()) {
				value += perm;
			} else {
				value += "," + perm;
			}
		}
		this.put(user.getName(), value);
		users.put(user.getName(), user);
		try {
			new File("plugins/BenCmd/_users.db").createNewFile();
			if (!FileUtil.copy(new File("plugins/BenCmd/users.db"), new File(
					"plugins/BenCmd/_users.db"))) {
				mainPerm.plugin.log.warning("Failed to back up user database!");
			}
		} catch (IOException e) {
			mainPerm.plugin.log.warning("Failed to back up user database!");
		}
		saveFile();
		try {
			new File("plugins/BenCmd/_users.db").delete();
		} catch (Exception e) { }
	}

	public void removeUser(PermissionUser user) {
		this.remove(user.getName());
		users.remove(user.getName());
		try {
			new File("plugins/BenCmd/_users.db").createNewFile();
			if (!FileUtil.copy(new File("plugins/BenCmd/users.db"), new File(
					"plugins/BenCmd/_users.db"))) {
				mainPerm.plugin.log.warning("Failed to back up user database!");
			}
		} catch (IOException e) {
			mainPerm.plugin.log.warning("Failed to back up user database!");
		}
		saveFile();
		try {
			new File("plugins/BenCmd/_users.db").delete();
		} catch (Exception e) { }
	}

	public void loadUsers() {
		users.clear();
		for (int i = 0; i < this.size(); i++) {
			String name = (String) this.keySet().toArray()[i];
			List<String> permissions = new ArrayList<String>();
			permissions
					.addAll(Arrays.asList(this.getProperty(name).split(",")));
			users.put(name,
					new InternalUser(mainPerm.plugin, name, permissions));
		}
	}

	public HashMap<String, InternalUser> listUsers() {
		return users;
	}

	public void saveFile() {
		File file = new File("plugins/BenCmd/users.db"); // Prepare the file
		if (!file.exists()) {
			try {
				file.createNewFile(); // If the file doesn't exist, create it!
			} catch (IOException ex) {
				// If you can't, produce an error.
				mainPerm.plugin.log.severe("BenCmd had a problem:");
				ex.printStackTrace();
				return;
			}
		}
		try {
			// Save the values
			store(new FileOutputStream(file), "BenCmd User Permissions File");
		} catch (IOException ex) {
			// If you can't, produce an error.
			mainPerm.plugin.log.severe("BenCmd had a problem:");
			ex.printStackTrace();
		}
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
		updateUser(user.getInternal());
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
