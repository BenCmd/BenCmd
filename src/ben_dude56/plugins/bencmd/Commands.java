package ben_dude56.plugins.bencmd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public interface Commands {
	public boolean onCommand(CommandSender sender, Command command,
			String commandLabel, String[] args);
}
