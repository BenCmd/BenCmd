package ben_dude56.plugins.bencmd.permissions;

import ben_dude56.plugins.bencmd.BenCmd;

public class MainPermissions {
	public UserFile userFile;
	public GroupFile groupFile;
	public ItemBW itemList;
	BenCmd plugin;

	public MainPermissions(BenCmd instance) {
		plugin = instance;
		userFile = new UserFile(this);
		groupFile = new GroupFile(this);
		itemList = new ItemBW(this);
	}
}
