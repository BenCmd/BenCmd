package ben_dude56.plugins.bencmd;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import ben_dude56.plugins.bencmd.permissions.PermChangeResult;
import ben_dude56.plugins.bencmd.warps.WarpableUser;

public class ActionableUser extends WarpableUser {
	private BenCmd plugin;
	private Player player;
	private boolean isConsole;
	Logger log = Logger.getLogger("minecraft");

	public ActionableUser(BenCmd instance, Player entity)
			throws NullPointerException {
		super(instance, entity);
		plugin = instance;
		player = entity;
		isConsole = false;
	}
	
	public ActionableUser(BenCmd instance)
			throws NullPointerException {
		super(instance);
		plugin = instance;
		isConsole = true;
	}
	
	public void Poof() {
		if(isConsole) {
			log.info("The server is already invisible, doofus!");
			return;
		}
		plugin.inv.addInv(player);
	}
	
	public void UnPoof() {
		if(isConsole) {
			log.info("This code should NEVER be reached...");
			return;
		}
		plugin.inv.remInv(player);
	}
	
	public void NoPoof() {
		if(isConsole) {
			log.info("The server has no eyes...");
			return;
		}
		plugin.inv.addNoInv(player);
	}
	
	public void UnNoPoof() {
		if(isConsole) {
			log.info("his code should NEVER be reached...");
			return;
		}
		plugin.inv.remNoInv(player);
	}
	
	
	public boolean isPoofed() {
		if(isConsole) {
			return false;
		} else {
			return plugin.invisible.contains(player);
		}
	}
	
	public boolean isNoPoofed() {
		if(isConsole) {
			return false;
		} else {
			return plugin.noinvisible.contains(player);
		}
	}

	public void Kick(String reason, Player sender) {
		if(isConsole) {
			log.info(sender.getName() + " attempted to kick the server!");
			return;
		}
		player.kickPlayer("You have been kicked by user: " + sender.getName()
				+ ". Reason: " + reason + ".");
	}

	public void Kick(String reason) {
		if(isConsole) {
			log.info("An attempt was made to kick the server!");
			return;
		}
		player.kickPlayer("You have been kicked. Reason: " + reason + ".");
	}

	public void Kick(Player sender) {
		if(isConsole) {
			log.info(sender.getName() + " attempted to kick the server!");
			return;
		}
		player.kickPlayer("You have been kicked by user: " + sender.getName()
				+ ".");
	}

	public void Kick() {
		if(isConsole) {
			log.info("An attempt was made to kick the server!");
			return;
		}
		player.kickPlayer("You have been kicked.");
	}

	public boolean Kill() {
		if(isConsole) {
			log.info("An attempt was made to kill the server!");
			return false;
		}
		if (plugin.isGod(player)) {
			return false;
		} else {
			player.setHealth(1);
			player.damage(1);
			return true;
		}
	}

	public void Heal() {
		if(isConsole) {
			return;
		}
		player.setHealth(20);
	}

	public void makeGod() {
		if(isConsole) {
			return;
		}
		plugin.setGod(player, true);
	}

	public void makeNonGod() {
		if(isConsole) {
			return;
		}
		plugin.setGod(player, false);
	}

	public boolean isGod() {
		if(isConsole) {
			return true;
		}
		return plugin.isGod(player);
	}

	public boolean isMuted() {
		return this.hasPerm("isMuted", false);
	}

	public PermChangeResult Mute() {
		return this.addPermission("isMuted");
	}

	public PermChangeResult Unmute() {
		return this.deletePermission("isMuted");
	}

	public ActionableUser getActionableUser() {
		return this;
	}

	public boolean canBuild() {
		return !this.hasPerm("NoBuild", false);
	}
	
	public boolean isOffline() {
		for(ActionableUser user : plugin.offline) {
			if(user.getName().equalsIgnoreCase(this.getName())) {
				return true;
			}
		}
		return false;
	}
	
	public void goOffline() {
		plugin.getServer().broadcastMessage(this.getColor() + this.getName() + ChatColor.WHITE
				+ " has left the game...");
		plugin.offline.add(this);
	}
	
	public void goOnline() {
		for(int i = 0; i < plugin.offline.size(); i++) {
			ActionableUser user = plugin.offline.get(i);
			if(user.getName().equalsIgnoreCase(this.getName())) {
				plugin.offline.remove(i);
			}
		}
		plugin.getServer().broadcastMessage(this.getColor() + this.getName() + ChatColor.WHITE
				+ " has joined the game...");
	}
	
	public void goOnlineNoMsg() {
		for(int i = 0; i < plugin.offline.size(); i++) {
			ActionableUser user = plugin.offline.get(i);
			if(user.getName().equalsIgnoreCase(this.getName())) {
				plugin.offline.remove(i);
			}
		}
	}
}
