package ben_dude56.plugins.bencmd.permissions;

import java.util.ArrayList;
import java.util.List;

import ben_dude56.plugins.bencmd.BenCmd;

public class InternalUser {
	protected BenCmd plugin;
	private String name;
	private List<String> permissions;

	protected InternalUser(BenCmd instance, String name, List<String> permissions) {
		this.plugin = instance;
		this.name = name;
		this.permissions = permissions;
	}
	
	public String getName() {
		return name;
	}
	
	protected List<String> getPerms() {
		return permissions;
	}
	
	public boolean hasPerm(String perm, boolean testStar, boolean testGroup) {
		if(isServer()) {
			return testStar;
		}
		boolean isStarred = false;
		boolean isJailed = false;
		boolean isDenied = false;
		boolean isAllowed = false;
		List<String> perms = new ArrayList<String>(permissions);
		if(testGroup) {
			for(PermissionGroup group : plugin.perm.groupFile.getAllUserGroups(this)) {
				perms.addAll(group.getInternal().getPermissions(true));
			}
		}
		for(String perm2 : perms) {
			if(perm2.equals("*")) {
				isStarred = true;
			}
			if(perm2.equalsIgnoreCase("-" + perm)) {
				isDenied = true;
			}
			if(perm2.equalsIgnoreCase("isJailed")) {
				isJailed = true;
			}
			if(perm.equalsIgnoreCase(perm2)) {
				isAllowed = true;
			}
		}
		if((isDenied || isJailed) && testStar) {
			return false;
		} else if (isStarred && testStar) {
			return true;
		} else {
			return isAllowed;
		}
	}
	
	public void addPerm(String perm) {
		if(isServer()) {
			return;
		}
		permissions.add(perm);
		plugin.perm.userFile.updateUser(this);
	}
	
	public void remPerm(String perm) {
		if(isServer()) {
			return;
		}
		permissions.remove(perm);
		plugin.perm.userFile.updateUser(this);
	}
	
	public boolean inGroup(PermissionGroup group) {
		if(isServer()) {
			return false;
		}
		for(PermissionGroup group2 : plugin.perm.groupFile.getAllUserGroups(this)) {
			if(group.getName().equals(group2.getName())) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isServer() {
		return (name == "*");
	}
}
