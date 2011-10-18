package com.bendude56.bencmd.advanced.bank;

import org.bukkit.entity.Player;

import com.bendude56.bencmd.BenCmd;

public class BankController {
	private BankFile f;
	
	public BankController() {
		f = new BankFile(BenCmd.propDir + "bank.db");
	}
	
	protected BankFile getBankFile() {
		return f;
	}
	
	public boolean hasBank(String user) {
		return f.hasBank(user);
	}
	
	public BankInventory getBank(String user) {
		if (hasBank(user)) {
			return f.getBank(user);
		} else {
			return null;
		}
	}
	
	public void showBank(Player user) {
		showBank(user, user.getName());
	}
	
	public void showBank(Player user, String lookAt) {
		f.openInventory(lookAt, user);
	}
	
	public BankInventory createBank(String p, boolean upgraded) {
		if (hasBank(p)) {
			return getBank(p);
		} else {
			if (upgraded) {
				f.addBank(new LargeBankInventory(p, BenCmd.getPlugin()));
			} else {
				f.addBank(new BankInventory(p, BenCmd.getPlugin()));
			}
			return getBank(p);
		}
	}
	
	public boolean canDowngradeBank(String user) {
		return f.canDowngradeBank(user);
	}
	
	public void upgradeBank(String user) {
		if (hasBank(user)) {
			if (getBank(user).isUpgraded()) {
				throw new UnsupportedOperationException("Bank already upgraded!");
			} else {
				f.upgradeBank(user);
			}
		} else {
			throw new UnsupportedOperationException("That user doesn't have a bank!");
		}
	}
	
	public void downgradeBank(String user) {
		if (hasBank(user)) {
			if (getBank(user).isUpgraded()) {
				f.downgradeBank(user);
			} else {
				throw new UnsupportedOperationException("Bank isn't upgraded!");
			}
		} else {
			throw new UnsupportedOperationException("That user doesn't have a bank!");
		}
	}
	
	public void saveBanks() {
		f.
	}

}
