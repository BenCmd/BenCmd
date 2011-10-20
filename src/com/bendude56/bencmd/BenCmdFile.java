package com.bendude56.bencmd;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;

import org.bukkit.util.FileUtil;

public abstract class BenCmdFile {
	private final String l;
	private final String h;
	private final boolean r;
	private Properties _prop;
	
	public BenCmdFile(String file, String header, boolean allowRestore) {
		l = file;
		h = header;
		r = allowRestore;
		_prop = null;
		if (allowRestore && new File(BenCmd.propDir + "_" + l).exists()) {
			BenCmd.log(Level.WARNING, "Backup file found (_" + l + ")... Restoring...");
			if (FileUtil.copy(new File(BenCmd.propDir + "_" + l), new File(
					BenCmd.propDir + l))) {
				new File(BenCmd.propDir + "_" + l).delete();
				BenCmd.log("Restoration suceeded!");
			} else {
				BenCmd.log(Level.SEVERE, "Failed to restore from backup!");
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
				BenCmd.log(Level.SEVERE, "Problem loading " + l + ":");
				BenCmd.log(ex);
				return false;
			}
		}
		try {
			_prop.load(new FileInputStream(file)); // Load the values
			return true;
		} catch (IOException ex) {
			// If you can't, produce an error.
			BenCmd.log(Level.SEVERE, "Problem loading " + l + ":");
			BenCmd.log(ex);
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
		if (r)
		{
			try {
				new File(BenCmd.propDir + "_" + l).createNewFile();
				if (!FileUtil.copy(new File(BenCmd.propDir + l), new File(
						BenCmd.propDir + "_" + l))) {
					BenCmd.log(Level.WARNING, "Failed to back up database!");
				}
			} catch (IOException e) {
				BenCmd.log(Level.WARNING, "Failed to back updatabase!");
			}
		}
		File file = new File(BenCmd.propDir + l); // Prepare the file
		if (!file.exists()) {
			try {
				file.createNewFile(); // If the file doesn't exist, create it!
			} catch (IOException ex) {
				// If you can't, produce an error.
				BenCmd.log(Level.SEVERE, "Problem loading " + l + ":");
				BenCmd.log(ex);
				return false;
			}
		}
		try {
			// Save the values
			_prop.store(new FileOutputStream(file), h);
			if (r)
			{
				try {
					new File(BenCmd.propDir + "_" + l).delete();
				} catch (Exception e) { }
			}
			return true;
		} catch (IOException ex) {
			// If you can't, produce an error.
			BenCmd.log(Level.SEVERE, "Problem loading " + l + ":");
			BenCmd.log(ex);
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
