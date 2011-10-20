package com.bendude56.bencmd;

import java.util.logging.Logger;

import com.bendude56.bencmd.advanced.bank.BankFile;
import com.bendude56.bencmd.permissions.MainPermissions;
import com.bendude56.bencmd.warps.HomeWarps;
import com.bendude56.bencmd.warps.WarpList;

public final class BenCmdManager {
	private static MainPermissions pManager;
	private static BankFile bc;
	private static WarpList warps;
	private static HomeWarps homes;

	public static MainPermissions getPermissionManager() {
		return pManager;
	}
	
	public static BankFile getBankController() {
		return bc;
	}
	
	public static Logger getMCLogger() {
		return Logger.getLogger("Minecraft");
	}
	
	public static Logger getBCLogger() {
		return Logger.getLogger("Minecraft.BenCmd");
	}
	
	protected static void loadAll() {
		pManager = new MainPermissions();
		bc = new BankFile();
		warps = new WarpList();
	}
	
	protected static void unloadAll(boolean save) {
		if (save) {
			bc.saveAll();
		}
		pManager = null;
		bc = null;
		warps = null;
		homes = null;
	}
}
