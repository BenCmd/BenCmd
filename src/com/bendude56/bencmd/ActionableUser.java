package com.bendude56.bencmd;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.bendude56.bencmd.warps.WarpableUser;


public class ActionableUser extends WarpableUser {
	private boolean god;

	public ActionableUser(Player entity)
			throws NullPointerException {
		super(entity);
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
		BenCmd.getPoofController().addInv(getHandle());
	}

	public void unPoof() {
		if (isServer()) {
			throw new UnsupportedOperationException();
		}
		BenCmd.getPoofController().remInv(getHandle());
	}

	public void noPoof() {
		if (isServer()) {
			throw new UnsupportedOperationException();
		}
		BenCmd.getPoofController().addNoInv(getHandle());
	}

	public void unNoPoof() {
		if (isServer()) {
			throw new UnsupportedOperationException();
		}
		BenCmd.getPoofController().remNoInv(getHandle());
	}

	public void allPoof() {
		if (isServer()) {
			throw new UnsupportedOperationException();
		}
		BenCmd.getPoofController().addAInv(getHandle());
	}

	public void unAllPoof() {
		if (isServer()) {
			throw new UnsupportedOperationException();
		}
		BenCmd.getPoofController().remAInv(getHandle());
	}

	public boolean isPoofed() {
		if (isServer()) {
			return false;
		} else {
			return BenCmd.getPoofController().isInv(getHandle());
		}
	}

	public boolean isNoPoofed() {
		if (isServer()) {
			return false;
		} else {
			return BenCmd.getPoofController().isNoInv(getHandle());
		}
	}

	public boolean isAllPoofed() {
		if (isServer()) {
			return false;
		} else {
			return BenCmd.getPoofController().isAInv(getHandle());
		}
	}

	public void kick(String reason, User sender) {
		if (isServer()) {
			throw new UnsupportedOperationException();
		}
		BenCmd.getPermissionManager().getKickTracker().addUser(this);
		getHandle().kickPlayer("You have been kicked by user: "
				+ sender.getDisplayName() + ". Reason: " + reason + ".");
	}

	public void kick(String reason) {
		if (isServer()) {
			throw new UnsupportedOperationException();
		}
		BenCmd.getPermissionManager().getKickTracker().addUser(this);
		getHandle().kickPlayer("You have been kicked. Reason: " + reason + ".");
	}

	public void kick(User sender) {
		if (isServer()) {
			throw new UnsupportedOperationException();
		}
		BenCmd.getPermissionManager().getKickTracker().addUser(this);
		getHandle().kickPlayer("You have been kicked by user: "
				+ sender.getDisplayName() + ".");
	}

	public void kick() {
		if (isServer()) {
			throw new UnsupportedOperationException();
		}
		BenCmd.getPermissionManager().getKickTracker().addUser(this);
		getHandle().kickPlayer("You have been kicked.");
	}

	public boolean kill() {
		if (isServer()) {
			throw new UnsupportedOperationException();
		}
		if (god) {
			return false;
		} else {
			getHandle().setHealth(1);
			getHandle().damage(1);
			return true;
		}
	}

	public void heal() {
		if (isServer()) {
			throw new UnsupportedOperationException();
		}
		getHandle().setHealth(20);
	}
	
	public void feed() {
		if (isServer()) {
			throw new UnsupportedOperationException();
		}
		getHandle().setFoodLevel(20);
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
		Bukkit.broadcastMessage(
				this.getColor() + this.getDisplayName() + ChatColor.WHITE
						+ " has left the game...");
		BenCmd.getPoofController().goOffline(this);
	}

	public void goOnline() {
		if (isServer()) {
			throw new UnsupportedOperationException();
		}
		Bukkit.broadcastMessage(
				this.getColor() + this.getDisplayName() + ChatColor.WHITE
						+ " has joined the game...");
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
			return "Server";
		}
		return getHandle().getDisplayName();
	}
}
