package ben_dude56.plugins.bencmd.warps;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import ben_dude56.plugins.bencmd.BenCmd;

public class HomeList {
	BenCmd plugin;
	HashMap<String, Warp> warps = new HashMap<String, Warp>();
	List<String> warpString = new ArrayList<String>();

	public HomeList(BenCmd instance) {
		plugin = instance;
		LoadHomes();
	}

	public boolean updateHome(Warp warp) {
		int ind = this.getIndex(warp);
		String name = warp.warpName;
		double x = warp.loc.getX();
		double y = warp.loc.getY();
		double z = warp.loc.getZ();
		Double yaw = (double) warp.loc.getYaw();
		Double pitch = (double) warp.loc.getPitch();
		String world = warp.loc.getWorld().getName();
		String group = warp.mustInheritGroup;
		String value = name + ":" + x + "," + y + "," + z + ","
				+ yaw.toString() + "," + pitch.toString() + ":" + world + ":"
				+ group;
		if (ind == -1) {
			warpString.add(value);
		} else {
			warpString.add(ind, value);
		}
		SaveFile();
		return true;
	}

	public boolean remHome(String name) {
		int ind = this.getIndex(name);
		if (ind == -1) {
			return false;
		}
		warpString.remove(ind);
		SaveFile();
		return true;
	}

	public int getIndex(Warp warp) {
		for (int i = 0; i < warpString.size(); i++) {
			String value = warpString.get(i);
			if (value.split(":")[0].equals(warp.warpName)) {
				return i;
			}
		}
		return -1;
	}

	public int getIndex(String name) {
		for (int i = 0; i < warpString.size(); i++) {
			String value = warpString.get(i);
			if (value.split(":")[0].equals(name)) {
				return i;
			}
		}
		return -1;
	}

	public boolean LoadHomes() {
		warpString.clear();
		warps.clear();
		File warpFile;
		warpFile = new File(plugin.propDir + "homes.db");
		String str = "";
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					warpFile)));
		} catch (FileNotFoundException e) {
			plugin.log.warning("homes.db not found. Attempting to create...");
			plugin.bLog.warning("homes.db not found. Attempting to create...");
			try {
				warpFile.createNewFile();
				br = new BufferedReader(new InputStreamReader(
						new FileInputStream(warpFile)));
			} catch (IOException ex) {
				plugin.bLog.log(Level.SEVERE, "Couldn't create homes.db:", e);
				plugin.log.severe("Couldn't create homes.db:");
				ex.printStackTrace();
				return false;
			}
		}
		try {
			while ((str = br.readLine()) != null) {
				try {
					warpString.add(str);
					String name = str.split(":")[0];
					double x = Double
							.parseDouble(str.split(":")[1].split(",")[0]);
					double y = Double
							.parseDouble(str.split(":")[1].split(",")[1]);
					double z = Double
							.parseDouble(str.split(":")[1].split(",")[2]);
					double yaw;
					double pitch;
					try {
						yaw = Double
								.parseDouble(str.split(":")[1].split(",")[3]);
						pitch = Double
								.parseDouble(str.split(":")[1].split(",")[4]);
					} catch (IndexOutOfBoundsException e) {
						yaw = 0;
						pitch = 0;
					}
					String world = str.split(":")[2];
					String group = "";
					if (str.split(":").length == 4) {
						group = str.split(":")[3];
					}
					warps.put(name, new Warp(x, y, z, yaw, pitch, world, name,
							group, plugin));
				} catch (IndexOutOfBoundsException e) {
					plugin.log.warning("Couldn't load one of the homes!");
					plugin.bLog.warning("Couldn't load one of the homes!");
					e.printStackTrace();
				} catch (NumberFormatException e) {
					plugin.log.warning("Couldn't load one of the homes!");
					plugin.bLog.warning("Couldn't load one of the homes!");
					e.printStackTrace();
				}
			}
			br.close();
		} catch (IOException e) {
			plugin.bLog.log(Level.SEVERE, "Couldn't read homes.db:", e);
			plugin.log.severe("Couldn't read homes.db:");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean SaveFile() {
		File warpFile;
		warpFile = new File(plugin.propDir + "homes.db");
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new FileWriter(warpFile));
		} catch (IOException e) {
			plugin.log.severe("Unable to open homes.db for writing:");
			e.printStackTrace();
			return false;
		}
		for (String value : warpString) {
			try {
				bw.write(value);
				if (!warpString.get(warpString.size() - 1).equals(value)) {
					bw.newLine();
				}
			} catch (IOException e) {
				plugin.bLog.log(Level.SEVERE, "BenCmd failed to save home " + value.split(":")[0] + ":", e);
				plugin.log.severe("BenCmd failed to save home " + value.split(":")[0]
						+ ":");
				e.printStackTrace();
			}
		}
		try {
			bw.close();
		} catch (IOException e) {
			plugin.bLog.log(Level.SEVERE, "Failed to save homes:", e);
			plugin.log.severe("Failed to save homes:");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public Warp getHome(String name) {
		if (!warps.containsKey(name)) {
			return null;
		}
		return warps.get(name);
	}

	public boolean addHome(double x, double y, double z, double yaw,
			double pitch, String world, String name, String group) {
		Warp warp;
		try {
			warp = new Warp(x, y, z, yaw, pitch, world, name, group, plugin);
			warps.put(name, warp);
		} catch (Exception e) {
			plugin.bLog.log(Level.SEVERE, "Couldn't add new home:", e);
			plugin.log.severe("Couldn't add new home:");
			e.printStackTrace();
			return false;
		}
		return updateHome(warp);
	}

	public boolean removeHome(String name) {
		if (warps.containsKey(name)) {
			warps.remove(name);
			return remHome(name);
		} else {
			return false;
		}
	}

}
