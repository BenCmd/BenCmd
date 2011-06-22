package ben_dude56.plugins.bencmd;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import ben_dude56.plugins.bencmd.warps.WarpableUser;

public class ActionableUser extends WarpableUser {
	private BenCmd plugin;
	private Player player;
	private boolean isConsole;
	Logger log = Logger.getLogger("minecraft");

	/**
	 * Creates an ActionableUser corresponding to a player entity.
	 * 
	 * @param instance
	 *            The BenCmd Plugin reference to point to
	 * @param entity
	 *            The player entity that this ActionableUser should point to.
	 * @throws NullPointerException
	 */
	public ActionableUser(BenCmd instance, Player entity)
			throws NullPointerException {
		super(instance, entity);
		plugin = instance;
		player = entity;
		isConsole = false;
	}

	/**
	 * Creates an ActionableUser corresponding to the console.
	 * 
	 * @param instance
	 *            The BenCmd Plugin reference to point to
	 */
	public ActionableUser(BenCmd instance) {
		super(instance);
		plugin = instance;
		isConsole = true;
	}

	/**
	 * Causes the ActionableUser to become invisible
	 */
	public void Poof() {
		if (isConsole) {
			log.info("The server is already invisible, doofus!");
			return;
		}
		plugin.inv.addInv(player);
	}

	/**
	 * Causes the ActionableUser to become visible
	 */
	public void UnPoof() {
		if (isConsole) {
			log.info("This code should NEVER be reached...");
			return;
		}
		plugin.inv.remInv(player);
	}

	/**
	 * Causes the ActionableUser to be able to see invisible ActionableUsers
	 */
	public void NoPoof() {
		if (isConsole) {
			log.info("The server has no eyes...");
			return;
		}
		plugin.inv.addNoInv(player);
	}

	/**
	 * Causes the ActionableUser to be unable to see invisible ActionableUsers
	 */
	public void UnNoPoof() {
		if (isConsole) {
			log.info("This code should NEVER be reached...");
			return;
		}
		plugin.inv.remNoInv(player);
	}

	/**
	 * Gets whether or not the ActionableUser is invisible
	 * 
	 * @return Returns whether the ActionableUser is invisible
	 */
	public boolean isPoofed() {
		if (isConsole) {
			return false;
		} else {
			return plugin.invisible.contains(player);
		}
	}

	/**
	 * Gets whether or not the ActionableUser can see invisible ActionableUsers
	 * 
	 * @return Returns whether or not the ActionableUser can see invisible
	 *         ActionableUsers
	 */
	public boolean isNoPoofed() {
		if (isConsole) {
			return false;
		} else {
			return plugin.noinvisible.contains(player);
		}
	}

	/**
	 * Kicks the user
	 * 
	 * @param reason
	 *            The reason that they were kicked
	 * @param sender
	 *            The user that kicked them
	 */
	public void Kick(String reason, User sender) {
		if (isConsole) {
			log.info(sender.getDisplayName() + " attempted to kick the server!");
			return;
		}
		plugin.kicked.addUser(this);
		player.kickPlayer("You have been kicked by user: " + sender.getDisplayName()
				+ ". Reason: " + reason + ".");
	}

	/**
	 * Kicks the user
	 * 
	 * @param reason
	 *            The reason that they were kicked
	 */
	public void Kick(String reason) {
		if (isConsole) {
			log.info("An attempt was made to kick the server!");
			return;
		}
		plugin.kicked.addUser(this);
		player.kickPlayer("You have been kicked. Reason: " + reason + ".");
	}

	/**
	 * Kicks the user
	 * 
	 * @param sender
	 *            The user that kicked them
	 */
	public void Kick(User sender) {
		if (isConsole) {
			log.info(sender.getDisplayName() + " attempted to kick the server!");
			return;
		}
		plugin.kicked.addUser(this);
		player.kickPlayer("You have been kicked by user: " + sender.getDisplayName()
				+ ".");
	}

	/**
	 * Kicks the user
	 */
	public void Kick() {
		if (isConsole) {
			log.info("An attempt was made to kick the server!");
			return;
		}
		plugin.kicked.addUser(this);
		player.kickPlayer("You have been kicked.");
	}

	/**
	 * Kills the user
	 * 
	 * @return Return whether or not the user could be killed
	 */
	public boolean Kill() {
		if (isConsole) {
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

	/**
	 * Heals the user to full health
	 */
	public void Heal() {
		if (isConsole) {
			return;
		}
		player.setHealth(20);
	}

	/**
	 * Makes the user enter god mode
	 */
	public void makeGod() {
		if (isConsole) {
			return;
		}
		plugin.setGod(player, true);
	}

	/**
	 * Makes the user exit god mode
	 */
	public void makeNonGod() {
		if (isConsole) {
			return;
		}
		plugin.setGod(player, false);
	}

	/**
	 * Gets whether the user is in god mode
	 * 
	 * @return Returns whether the user is in god mode
	 */
	public boolean isGod() {
		if (isConsole) {
			return true;
		}
		return plugin.isGod(player);
	}

	/**
	 * Checks if the user is muted
	 * 
	 * @return True if muted, false otherwise
	 */
	public boolean isMuted() {
		return this.hasPerm("isMuted", false);
	}

	/**
	 * @deprecated Instead use {@link PermissionUser#addPermission(String)} with permission "isMuted"
	 */
	public void Mute() {
		this.addPermission("isMuted");
	}

	/**
	 * @deprecated Instead use {@link PermissionUser#removePermission(String)} with permission "isMuted"
	 */
	public void Unmute() {
		this.removePermission("isMuted");
	}

	/**
	 * Returns the ActionableUser directly
	 * 
	 * @return Returns the ActionableUser
	 * @deprecated Unused function
	 */
	public ActionableUser getActionableUser() {
		return this;
	}

	/**
	 * Checks if the user is allowed to build
	 * 
	 * @return True if they can build, False otherwise
	 */
	public boolean canBuild() {
		return !this.hasPerm("NoBuild", false);
	}

	/**
	 * Checks if the user is using /offline
	 * 
	 * @return Whether /offline was used
	 */
	public boolean isOffline() {
		for (ActionableUser user : plugin.offline) {
			if (user.getName().equalsIgnoreCase(this.getName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Forces the user to appear to be offline
	 */
	public void goOffline() {
		plugin.getServer().broadcastMessage(
				this.getColor() + this.getDisplayName() + ChatColor.WHITE
						+ " has left the game...");
		plugin.offline.add(this);
	}

	/**
	 * Forces a user that is faking offline to reappear.
	 */
	public void goOnline() {
		for (int i = 0; i < plugin.offline.size(); i++) {
			ActionableUser user = plugin.offline.get(i);
			if (user.getName().equalsIgnoreCase(this.getName())) {
				plugin.offline.remove(i);
			}
		}
		plugin.getServer().broadcastMessage(
				this.getColor() + this.getDisplayName() + ChatColor.WHITE
						+ " has joined the game...");
	}

	/**
	 * Forces a user that is faking offline to reappear without showing a
	 * message
	 */
	public void goOnlineNoMsg() {
		for (int i = 0; i < plugin.offline.size(); i++) {
			ActionableUser user = plugin.offline.get(i);
			if (user.getName().equalsIgnoreCase(this.getName())) {
				plugin.offline.remove(i);
			}
		}
	}
	
	public String getDisplayName() {
		return player.getDisplayName();
	}
}
