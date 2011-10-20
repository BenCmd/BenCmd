package com.bendude56.bencmd;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.bukkit.util.FileUtil;

public abstract class BenCmdFile {
	private final String l;
	private final String h;
	private Properties _prop;
	
	public BenCmdFile(String file, String header, boolean allowRestore) {
		l = file;
		h = header;
		_prop = null;
		if (allowRestore && new File(BenCmd.propDir + "_" + l).exists()) {
			BenCmdManager.getMCLogger().warning("Backup file found (_" + l + ")... Restoring...");
			if (FileUtil.copy(new File(BenCmd.propDir + "_" + l), new File(
					BenCmd.propDir + l))) {
				new File(BenCmd.propDir + "_" + l).delete();
				BenCmdManager.getMCLogger().info("Restoration suceeded!");
			} else {
				BenCmdManager.getMCLogger().warning("Failed to restore from backup!");
			}
		}
	}
	
	protected final boolean loadFile() {
		if (_prop != null)
			unloadFile();
		_prop = new Properties();
		File file = new File(BenCmd.propDir + l); // Prepare the file
		if (!file.exists()) {
			try {
				file.createNewFile(); // If the file doesn't exist, create it!
			} catch (IOException ex) {
				// If you can't, produce an error.
				BenCmdManager.getBCLogger().severe("Problem loading " + l + ":");
				ex.printStackTrace();
				return false;
			}
		}
		try {
			_prop.load(new FileInputStream(file)); // Load the values
			return true;
		} catch (IOException ex) {
			// If you can't, produce an error.
			BenCmdManager.getBCLogger().severe("Problem loading " + l + ":");
			ex.printStackTrace();
			return false;
		}
	}
	
	protected final void unloadFile() {
		_prop.clear();
		_prop = null;
	}
	
	protected final boolean saveFile() {
		if (_prop == null)
			throw new UnsupportedOperationException("Cannot save a file that hasn't been loaded!");
		BenCmd plugin = BenCmd.getPlugin();
		try {
			new File(BenCmd.propDir + "_" + l).createNewFile();
			if (!FileUtil.copy(new File(BenCmd.propDir + l), new File(
					BenCmd.propDir + "_" + l))) {
				plugin.log.warning("Failed to back up bank database!");
			}
		} catch (IOException e) {
			plugin.log.warning("Failed to back up bank database!");
		}
		File file = new File(BenCmd.propDir + l); // Prepare the file
		if (!file.exists()) {
			try {
				file.createNewFile(); // If the file doesn't exist, create it!
			} catch (IOException ex) {
				// If you can't, produce an error.
				BenCmdManager.getBCLogger().severe("Problem saving " + l + ":");
				ex.printStackTrace();
				return false;
			}
		}
		try {
			// Save the values
			_prop.store(new FileOutputStream(file), h);
			try {
				new File(BenCmd.propDir + "_" + l).delete();
			} catch (Exception e) { }
			return true;
		} catch (IOException ex) {
			// If you can't, produce an error.
			BenCmdManager.getBCLogger().severe("Problem saving " + l + ":");
			ex.printStackTrace();
			try {
				new File(BenCmd.propDir + "_" + l).delete();
			} catch (Exception e) { }
			return false;
		}
	}
	
	public abstract void saveAll();
	public abstract void loadAll();
	
	public final void reload(boolean save) {
		if (save)
			saveAll();
		loadAll();
	}
	
	protected final Properties getFile() {
		return _prop;
	}
}
