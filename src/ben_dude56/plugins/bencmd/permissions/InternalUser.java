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
		List<String> perms = new ArrayList<String>(permissions);
		if(testGroup) {
			for(PermissionGroup group : plugin.perm.groupFile.getAllUserGroups(this)) {
				perms.addAll(group.getInternal().getPermissions(true));
			}
		}
		if(testStar && perms.contains("*")) {
			return true;
		}
		for(String perm2 : perms) {
			if(perm.equalsIgnoreCase(perm2)) {
				return true;
			}
		}
		return false;
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
