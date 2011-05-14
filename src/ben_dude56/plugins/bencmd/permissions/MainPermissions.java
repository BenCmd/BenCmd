package ben_dude56.plugins.bencmd.permissions;

import org.bukkit.entity.Player;

import ben_dude56.plugins.bencmd.BenCmd;

public class MainPermissions {
	public UserFile userFile;
	public GroupFile groupFile;
	public ItemBW itemList;
	BenCmd plugin;

	public MainPermissions(BenCmd instance) {
		userFile = new UserFile(this);
		groupFile = new GroupFile(this);
		itemList = new ItemBW(this);
		plugin = instance;
	}

	/**
	 * 
	 * @deprecated This method has been deprecated in favor of
	 *             UserFile.hasPermission() and GroupFile.hasPermission().
	 */
	public boolean hasPermission(Player player, String permission) {
		return userFile.hasPermission(player.getName(), permission);
	}
}
