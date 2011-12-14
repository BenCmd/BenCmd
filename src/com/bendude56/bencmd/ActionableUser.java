package com.bendude56.bencmd;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bendude56.bencmd.warps.WarpableUser;

public class ActionableUser extends WarpableUser {
	private boolean	god;

	public ActionableUser(CommandSender s) throws NullPointerException {
		super(s);
		god = false;
	}

	public ActionableUser() {
		super();
		god = false;
	}

	public void poof() {
		if (isServer()) {
			throw new UnsupportedOperationException();
		}
		BenCmd.getPoofController().addInv((Player) getHandle());
	}

	public void unPoof() {
		if (isServer()) {
			throw new UnsupportedOperationException();
		}
		BenCmd.getPoofController().remInv((Player) getHandle());
	}

	public void noPoof() {
		if (isServer()) {
			throw new UnsupportedOperationException();
		}
		BenCmd.getPoofController().addNoInv((Player) getHandle());
	}

	public void unNoPoof() {
		if (isServer()) {
			throw new UnsupportedOperationException();
		}
		BenCmd.getPoofController().remNoInv((Player) getHandle());
	}

	public void allPoof() {
		if (isServer()) {
			throw new UnsupportedOperationException();
		}
		BenCmd.getPoofController().addAInv((Player) getHandle());
	}

	public void unAllPoof() {
		if (isServer()) {
			throw new UnsupportedOperationException();
		}
		BenCmd.getPoofController().remAInv((Player) getHandle());
	}

	public boolean isPoofed() {
		if (isServer()) {
			return false;
		} else {
			return BenCmd.getPoofController().isInv((Player) getHandle());
		}
	}

	public boolean isNoPoofed() {
		if (isServer()) {
			return false;
		} else {
			return BenCmd.getPoofController().isNoInv((Player) getHandle());
		}
	}

	public boolean isAllPoofed() {
		if (isServer()) {
			return false;
		} else {
			return BenCmd.getPoofController().isAInv((Player) getHandle());
		}
	}

	public void kick(String reason, User sender) {
		if (isServer()) {
			throw new UnsupportedOperationException();
		}
		BenCmd.getPermissionManager().getKickTracker().addUser(this);
		((Player) getHandle()).kickPlayer("You have been kicked by user: " + sender.getDisplayName() + ". Reason: " + reason + ".");
	}

	public void kick(String reason) {
		if (isServer()) {
			throw new UnsupportedOperationException();
		}
		BenCmd.getPermissionManager().getKickTracker().addUser(this);
		((Player) getHandle()).kickPlayer("You have been kicked. Reason: " + reason + ".");
	}

	public void kick(User sender) {
		if (isServer()) {
			throw new UnsupportedOperationException();
		}
		BenCmd.getPermissionManager().getKickTracker().addUser(this);
		((Player) getHandle()).kickPlayer("You have been kicked by user: " + sender.getDisplayName() + ".");
	}

	public void kick() {
		if (isServer()) {
			throw new UnsupportedOperationException();
		}
		BenCmd.getPermissionManager().getKickTracker().addUser(this);
		((Player) getHandle()).kickPlayer("You have been kicked.");
	}

	public boolean kill() {
		if (isServer()) {
			throw new UnsupportedOperationException();
		}
		if (god) {
			return false;
		} else {
			((Player) getHandle()).setHealth(1);
			((Player) getHandle()).damage(1);
			return true;
		}
	}

	public void heal() {
		if (isServer()) {
			throw new UnsupportedOperationException();
		}
		((Player) getHandle()).setHealth(20);
	}

	public void feed() {
		if (isServer()) {
			throw new UnsupportedOperationException();
		}
		((Player) getHandle()).setFoodLevel(20);
		((Player) getHandle()).setSaturation(1.0F);
	}

	public void makeGod() {
		if (isServer()) {
			throw new UnsupportedOperationException();
		}
		god = true;
	}

	public void makeNonGod() {
		if (isServer()) {
			throw new UnsupportedOperationException();
		}
		god = false;
	}

	public boolean isGod() {
		if (isServer()) {
			return true;
		}
		return god;
	}

	public boolean isOffline() {
		return BenCmd.getPoofController().isOffline(this);
	}

	public void goOffline() {
		if (isServer()) {
			throw new UnsupportedOperationException();
		}
		Bukkit.broadcastMessage(this.getColor() + this.getDisplayName() + ChatColor.WHITE + " has left the game...");
		BenCmd.getPoofController().goOffline(this);
	}

	public void goOnline() {
		if (isServer()) {
			throw new UnsupportedOperationException();
		}
		Bukkit.broadcastMessage(this.getColor() + this.getDisplayName() + ChatColor.WHITE + " has joined the game...");
		BenCmd.getPoofController().goOnline(this);
	}

	public void goOnlineNoMsg() {
		if (isServer()) {
			throw new UnsupportedOperationException();
		}
		BenCmd.getPoofController().goOnline(this);
	}

	public String getDisplayName() {
		if (isServer()) {
			if (getPermissionUser().getName().equals("*")) {
				return "Server";
			} else {
				return getHandle().getName();
			}
		}
		return ((Player) getHandle()).getDisplayName();
	}
}
