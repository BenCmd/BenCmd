package com.bendude56.bencmd;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;

public class PluginProperties extends Properties {
	private static final long serialVersionUID = 0L;
	private String proFile;

	public PluginProperties(String propertiesFile) {
		proFile = propertiesFile;
		loadFile();
	}

	public void loadFile() {
		File file = new File(proFile);
		if (file.exists()) {
			try {
				load(new FileInputStream(file));
			} catch (IOException e) {
				BenCmd.log(Level.SEVERE, "Problem loading " + proFile + ":");
				BenCmd.log(e);
			}
		}
	}

	public void saveFile(String header) {
		File file = new File(proFile);
		if (file.exists()) {
			try {
				store(new FileOutputStream(file), header);
			} catch (IOException e) {
				BenCmd.log(Level.SEVERE, "Problem saving " + proFile + ":");
				BenCmd.log(e);
			}
		}
	}

	public int getInteger(String key, int value) {
		if (containsKey(key)) {
			return Integer.parseInt(getProperty(key));
		}

		put(key, String.valueOf(value));
		saveFile("-BenCmd Main Config-");
		return value;
	}

	public double getDouble(String key, double value) {
		if (containsKey(key)) {
			return Double.parseDouble(getProperty(key));
		}
		put(key, String.valueOf(value));
		saveFile("-BenCmd Main Config-");
		return value;
	}

	public String getString(String key, String value) {
		if (containsKey(key)) {
			return getProperty(key);
		}
		put(key, value);
		saveFile("-BenCmd Main Config-");
		return value;
	}

	/**
	 * 
	 * @deprecated This method is no longer used. Use getString().split(",")
	 *             instead to get an array.
	 */
	public HashMap<String, Boolean> getStringList(String key, String value) {
		String[] strList;
		final HashMap<String, Boolean> hashMap = new HashMap<String, Boolean>();
		if (containsKey(key)) {
			strList = getProperty(key).split(",");
		} else {
			put(key, value);
			saveFile("-BenCmd Main Config-");
			strList = value.split(",");
		}
		int i = 0;
		while (i < strList.length) {
			hashMap.put(strList[i], true);
			i++;
		}
		return hashMap;
	}

	public boolean getBoolean(String key, boolean value) {
		if (containsKey(key)) {
			String boolString = getProperty(key);
			return (boolString.length() > 0)
					&& (boolString.toLowerCase().charAt(0) == 't');
		}
		put(key, value ? "true" : "false");
		saveFile("-BenCmd Main Config-");
		return value;
	}

}
