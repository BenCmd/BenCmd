package ben_dude56.plugins.bencmd;

import java.util.ArrayList;
import java.util.List;

public class BCommand {
	public static List<BCommand> commands;
	
	static {
		commands = new ArrayList<BCommand>();
		commands.add(new BCommand("/time", "Change or lock the current time of day", "bencmd.time.set,bencmd.time.lock"));
		commands.add(new BCommand("/spawn", "Sends you back to the default world's spawn point", "bencmd.spawn.normal"));
		commands.add(new BCommand("/god", "Turns on/off god mode", "bencmd.god.self"));
		commands.add(new BCommand("/heal", "Heals you or another player", "bencmd.heal.self"));
		commands.add(new BCommand("/bencmd", "Gets information or performs some special bencmd commands", "bencmd.spawn.normal"));
		commands.add(new BCommand("/help", "Display this help text", "."));
	}
	private String commandLabel;
	private String commandDescription;
	private String neededPermission;

	public BCommand(String commandLabel, String commandDescription,
			String neededPermission) {
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
		if (neededPermission.equalsIgnoreCase(".")) {
			return true;
		}
		for (String p : neededPermission.split(",")) {
			if (user.hasPerm(p)) {
				return true;
			}
		}
		return false;
	}
}
