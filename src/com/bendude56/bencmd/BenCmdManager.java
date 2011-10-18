package com.bendude56.bencmd;

import com.bendude56.bencmd.advanced.bank.BankController;
import com.bendude56.bencmd.permissions.MainPermissions;

public final class BenCmdManager {
	private static MainPermissions pManager;
	private static BankController bc;

	public static MainPermissions getPermissionManager() {
		return pManager;
	}
	
	public static BankController getBankController() {
		return bc;
	}
	
	protected static void loadAll() {
		pManager = new MainPermissions();
		bc = new BankController();
	}
	
	protected static void unloadAll() {
		pManager = null;
		bc = null;
	}
}
