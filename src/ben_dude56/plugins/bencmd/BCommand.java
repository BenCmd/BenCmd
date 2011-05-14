package ben_dude56.plugins.bencmd;

public class BCommand {
	private String commandLabel;
	private String commandDescription;
	private String neededPermission;
	
	public BCommand(String commandLabel, String commandDescription, String neededPermission) {
		this.commandLabel = commandLabel;
		this.commandDescription = commandDescription;
		this.neededPermission = neededPermission;
	}
	
	public String getName() {
		return commandLabel;
	}
	
	public String getDescription() {
		return commandDescription;
	}
	
	public boolean canUse(User user) {
		if(neededPermission.equalsIgnoreCase(".")) {
			return true;
		}
		return user.hasPerm(neededPermission);
	}
}
