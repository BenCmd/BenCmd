package com.bendude56.bencmd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bendude56.bencmd.advanced.AdvancedCommands;
import com.bendude56.bencmd.advanced.Grave;
import com.bendude56.bencmd.advanced.ShelfFile;
import com.bendude56.bencmd.advanced.bank.BankCommands;
import com.bendude56.bencmd.advanced.bank.BankFile;
import com.bendude56.bencmd.advanced.npc.NPC;
import com.bendude56.bencmd.advanced.npc.NPCCommands;
import com.bendude56.bencmd.advanced.npc.NPCFile;
import com.bendude56.bencmd.advanced.redstone.RedstoneCommands;
import com.bendude56.bencmd.advanced.redstone.RedstoneFile;
import com.bendude56.bencmd.chat.ChatCommands;
import com.bendude56.bencmd.chat.channels.ChatChannelCommands;
import com.bendude56.bencmd.chat.channels.ChatChannelController;
// import com.bendude56.bencmd.comms.MainServer;
import com.bendude56.bencmd.invisible.Invisibility;
import com.bendude56.bencmd.invisible.InvisibleCommands;
import com.bendude56.bencmd.invtools.DispChest;
import com.bendude56.bencmd.invtools.InventoryBackend;
import com.bendude56.bencmd.invtools.InventoryCommands;
import com.bendude56.bencmd.invtools.UnlimitedDisp;
import com.bendude56.bencmd.invtools.kits.KitList;
import com.bendude56.bencmd.listener.BenCmdBlockListener;
import com.bendude56.bencmd.listener.BenCmdEntityListener;
import com.bendude56.bencmd.listener.BenCmdPlayerListener;
import com.bendude56.bencmd.listener.BenCmdScreenListener;
import com.bendude56.bencmd.listener.BenCmdSpoutListener;
import com.bendude56.bencmd.listener.BenCmdWorldListener;
import com.bendude56.bencmd.listener.BenCmdInventoryListener;
import com.bendude56.bencmd.lots.LotCommands;
import com.bendude56.bencmd.lots.LotFile;
import com.bendude56.bencmd.lots.sparea.SPAreaFile;
import com.bendude56.bencmd.maps.MapCommands;
import com.bendude56.bencmd.money.MoneyCommands;
import com.bendude56.bencmd.money.PriceFile;
import com.bendude56.bencmd.multiworld.PortalCommands;
import com.bendude56.bencmd.multiworld.PortalFile;
import com.bendude56.bencmd.permissions.MainPermissions;
import com.bendude56.bencmd.permissions.PermissionCommands;
import com.bendude56.bencmd.permissions.PermissionUser;
import com.bendude56.bencmd.permissions.MaxPlayers.JoinType;
import com.bendude56.bencmd.protect.ProtectFile;
import com.bendude56.bencmd.protect.ProtectedCommands;
import com.bendude56.bencmd.recording.RecordCommands;
import com.bendude56.bencmd.recording.RecordingFile;
import com.bendude56.bencmd.reporting.ReportCommands;
import com.bendude56.bencmd.reporting.ReportFile;
import com.bendude56.bencmd.warps.HomeWarps;
import com.bendude56.bencmd.warps.PreWarp;
import com.bendude56.bencmd.warps.WarpCommands;
import com.bendude56.bencmd.warps.WarpList;
import com.bendude56.bencmd.weather.WeatherBinding;
import com.bendude56.bencmd.weather.WeatherCommands;
import com.sk89q.bukkit.migration.PermissionsProvider;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * BenCmd for Bukkit
 * 
 * @author ben_dude56
 * 
 */
public class BenCmd extends JavaPlugin implements PermissionsProvider {

	// START STATIC FILE METHODS
	private static MainPermissions			pManager;
	private static BankFile					bc;
	private static WarpList					warps;
	private static HomeWarps				homes;
	private static NPCFile					npcs;
	private static PortalFile				portals;
	private static RedstoneFile				rf;
	private static ShelfFile				sf;
	private static ChatChannelController	ccc;
	private static Invisibility				ic;
	private static KitList					kits;
	private static DispChest				disp;
	private static UnlimitedDisp			unl;
	private static LotFile					lots;
	private static SPAreaFile				areas;
	private static PriceFile				prices;
	private static ProtectFile				pro;
	private static ReportFile				reports;
	private static PreWarp					check;
	private static WeatherBinding			wb;
	private static FlyDetect				fd;
	private static SpoutConnector			spout;
	private static PluginProperties			main;
	private static PluginProperties			itemAlias;
	private static PluginProperties			usage;
	private static TimeManager				time;
	private static RecordingFile			record;
	// private static MainServer				mains;

	public static MainPermissions getPermissionManager() {
		return pManager;
	}

	public static BankFile getBankController() {
		return bc;
	}

	public static WarpList getWarps() {
		return warps;
	}

	public static HomeWarps getHomes() {
		return homes;
	}

	public static NPCFile getNPCFile() {
		return npcs;
	}

	public static PortalFile getPortalFile() {
		return portals;
	}

	public static RedstoneFile getRedstoneFile() {
		return rf;
	}

	public static ShelfFile getShelfFile() {
		return sf;
	}

	public static ChatChannelController getChatChannels() {
		return ccc;
	}

	public static Invisibility getPoofController() {
		return ic;
	}

	public static KitList getKitList() {
		return kits;
	}

	public static DispChest getDisposals() {
		return disp;
	}

	public static UnlimitedDisp getDispensers() {
		return unl;
	}

	public static LotFile getLots() {
		return lots;
	}

	public static SPAreaFile getAreas() {
		return areas;
	}

	public static PriceFile getMarketController() {
		return prices;
	}

	public static ProtectFile getProtections() {
		return pro;
	}

	public static ReportFile getReports() {
		return reports;
	}

	public static PreWarp getWarpCheckpoints() {
		return check;
	}

	public static WeatherBinding getStrikeBindings() {
		return wb;
	}

	public static FlyDetect getFlymodDetector() {
		return fd;
	}

	public static boolean isSpoutConnected() {
		if (spout != null) {
			return true;
		} else if (Bukkit.getPluginManager().isPluginEnabled("Spout")) {
			spout = new SpoutConnector();
			return true;
		} else {
			return false;
		}
	}

	public static SpoutConnector getSpoutConnector() {
		if (isSpoutConnected()) {
			return spout;
		} else {
			return null;
		}
	}

	public static PluginProperties getMainProperties() {
		return main;
	}

	public static PluginProperties getItemAliases() {
		return itemAlias;
	}

	public static PluginProperties getUsageFile() {
		return usage;
	}

	public static TimeManager getTimeManager() {
		return time;
	}

	public static RecordingFile getRecordingFile() {
		return record;
	}
	
	/*public static MainServer getMainServer() {
		return mains;
	}
	
	public static void disconnectMainServer() {
		try {
			mains.close();
		} catch (IOException e) { }
		mains = null;
	}*/

	public static void log(Exception e) {
		Logger.getLogger("Minecraft").log(Level.SEVERE, e.getMessage(), e);
		Logger.getLogger("Minecraft.BenCmd").log(Level.SEVERE, e.getMessage(), e);
	}

	public static void log(Level l, String message) {
		Logger.getLogger("Minecraft").log(l, "[BenCmd] " + message);
		Logger.getLogger("Minecraft.BenCmd").log(l, message);
	}

	public static void log(String message) {
		log(Level.INFO, message);
	}

	@Deprecated
	public static Logger getMCLogger() {
		return Logger.getLogger("Minecraft");
	}

	@Deprecated
	public static Logger getBCLogger() {
		return Logger.getLogger("Minecraft.BenCmd");
	}

	protected static void loadAll() {
		try {
			main = new PluginProperties(propDir + "main.properties");
		} catch (Exception e) {
			log(Level.SEVERE, "Failed to load main properties file!");
			log(e);
		}
		try {
			pManager = new MainPermissions();
		} catch (Exception e) {
			log(Level.SEVERE, "Failed to load BenCmd permissions module!");
			log(e);
		}
		try {
			bc = new BankFile();
		} catch (Exception e) {
			log(Level.SEVERE, "Failed to load banks!");
			log(e);
		}
		try {
			warps = new WarpList();
			homes = new HomeWarps();
			check = new PreWarp();
			portals = new PortalFile();
		} catch (Exception e) {
			log(Level.SEVERE, "Failed to load warps!");
			log(e);
		}
		try {
			npcs = new NPCFile();
		} catch (Exception e) {
			log(Level.SEVERE, "Failed to load NPCs!");
			log(e);
		}
		try {
			rf = new RedstoneFile();
		} catch (Exception e) {
			log(Level.SEVERE, "Failed to load timed levers!");
			log(e);
		}
		try {
			sf = new ShelfFile();
		} catch (Exception e) {
			log(Level.SEVERE, "Failed to load shelf writing!");
			log(e);
		}
		try {
			ccc = new ChatChannelController();
		} catch (Exception e) {
			log(Level.SEVERE, "Failed to load chat channels!");
			log(e);
		}
		try {
			ic = new Invisibility();
		} catch (Exception e) {
			log(Level.SEVERE, "Failed to load invisibility controller!");
			log(e);
		}
		try {
			kits = new KitList();
		} catch (Exception e) {
			log(Level.SEVERE, "Failed to load kits!");
			log(e);
		}
		try {
			disp = new DispChest();
			unl = new UnlimitedDisp();
		} catch (Exception e) {
			log(Level.SEVERE, "Failed to load unlimited dispensers and disposal chests!");
			log(e);
		}
		try {
			lots = new LotFile();
			areas = new SPAreaFile();
		} catch (Exception e) {
			log(Level.SEVERE, "Failed to load lots/areas!");
			log(e);
		}
		try {
			prices = new PriceFile();
		} catch (Exception e) {
			log(Level.SEVERE, "Failed to load market manager!");
			log(e);
		}
		try {
			pro = new ProtectFile();
		} catch (Exception e) {
			log(Level.SEVERE, "Failed to load protections!");
			log(e);
		}
		try {
			reports = new ReportFile();
		} catch (Exception e) {
			log(Level.SEVERE, "Failed to load reports!");
			log(e);
		}
		try {
			wb = new WeatherBinding();
		} catch (Exception e) {
			log(Level.SEVERE, "Failed to load strike bind controller!");
			log(e);
		}
		try {
			fd = new FlyDetect();
		} catch (Exception e) {
			log(Level.SEVERE, "Failed to load flymod detection!");
			log(e);
		}
		if (Bukkit.getPluginManager().isPluginEnabled("Spout")) {
			try {
				spout = new SpoutConnector();
			} catch (Exception e) {
				log(Level.SEVERE, "Failed to load spout connection manager!");
				log(e);
			}
		}
		try {
			itemAlias = new PluginProperties(propDir + "items.txt");
		} catch (Exception e) {
			log(Level.SEVERE, "Failed to load item aliases!");
			log(e);
		}
		try {
			usage = new PluginProperties(propDir + "usage.db");
		} catch (Exception e) {
			log(Level.SEVERE, "Failed to load anonymous usage stats!");
			log(e);
		}
		try {
			record = new RecordingFile();
		} catch (Exception e) {
			log(Level.SEVERE, "Failed to load recordings!");
			log(e);
		}
		time = new TimeManager();
		/*if (BenCmd.getMainProperties().getBoolean("connectMainServer", true)) {
			try {
				mains = new MainServer();
			} catch (Exception e) {
				if (e.getCause() != null && e.getCause().getMessage().equals("Connection refused")) {
					log(Level.WARNING, "BenCmd main server is not running on the provided IP! Some functions may no longer work...");
					log(Level.WARNING, "To retry the connection later, use /bencmd connect");
				} else {
					log(Level.SEVERE, "Unknown error connecting to BenCmd main server!");
					log(e);
				}
			}
		} else {
			log(Level.INFO, "Skipping connection to BenCmd server...");
		}*/
	}

	protected static void unloadAll(boolean save) {
		if (save) {
			pManager.saveAll();
			bc.saveAll();
			warps.SaveFile();
			homes.homes.SaveFile();
			npcs.saveAll();
			portals.saveAll();
			rf.saveAll();
			sf.saveAll();
			ccc.saveAll();
			lots.saveAll();
			areas.saveAll();
			prices.saveAll();
			pro.saveAll();
			reports.saveAll();
		}
		pManager = null;
		bc = null;
		warps = null;
		homes = null;
		if (npcs != null) {
			for (NPC n : npcs.allNPCs()) {
				n.despawn();
			}
		}
		npcs = null;
		portals = null;
		rf = null;
		ccc = null;
		ic = null;
		kits = null;
		disp = null;
		unl = null;
		lots = null;
		areas.forceStopTimer();
		areas = null;
		if (prices.isTimerEnabled()) {
			prices.unloadTimer();
		}
		prices = null;
		pro = null;
		reports = null;
		check = null;
		wb = null;
		fd.forceStopTimer();
		fd = null;
		spout = null;
		main = null;
		itemAlias = null;
		usage = null;
		time = null;
		record = null;
		/*if (mains != null) {
			try {
				mains.close();
			} catch (IOException e) { }
		}
		mains = null;*/
	}

	// END STATIC FILE METHODS

	public final static boolean	debug			= false;
	public final static int		buildId			= 42;
	public final static int		cbbuild			= 1597;
	public final static String	verLoc			= "http://cloud.github.com/downloads/BenCmd/BenCmd/version.txt";
	public static String		devLoc			= "";
	public static String		stableLoc		= "";
	public static boolean		updateAvailable	= false;
	public static String[]		devs;
	public final static String	propDir			= "plugins/BenCmd/";
	public final String[]		files			= { "action.db", "bank.db", "channels.db", "chest.db", "usage.db", "disp.db", "groups.db", "homes.db", "itembw.db", "items.txt", "kits.db", "lever.db", "lots.db", "main.properties", "npc.db", "portals.db", "prices.db", "protection.db", "shelves.db", "sparea.db", "tickets.db", "users.db", "warps.db" };

	// TODO Move into own classes
	public List<Location>		canSpread		= new ArrayList<Location>();
	private Logger				bLog			= Logger.getLogger("Minecraft.BenCmd");
	public FileHandler			fh;
	public Calendar				clog;

	public void logPermFail() {
		incStat("permFail");
	}

	public void incStat(String statName) {
		if (!BenCmd.getMainProperties().getBoolean("anonUsageStats", true)) {
			return;
		}
		BenCmd.getUsageFile().setProperty(statName, String.valueOf(BenCmd.getUsageFile().getInteger(statName, 0) + 1));
		BenCmd.getUsageFile().saveFile("--Anonymous usage stats--");
	}

	public boolean checkID(int id) {
		for (Material item : Material.values()) {
			if (item.getId() == id) {
				return true;
			}
		}
		return false;
	}

	public boolean canIgnite(Location loc) {
		for (Location loc2 : canSpread) {
			if (loc.getBlockX() < loc2.getBlockX() + 3 && loc.getBlockX() > loc2.getBlockX() - 3) {
				if (loc.getBlockY() < loc2.getBlockY() + 3 && loc.getBlockY() > loc2.getBlockY() - 3) {
					if (loc.getBlockZ() < loc2.getBlockZ() + 3 && loc.getBlockZ() > loc2.getBlockZ() - 3) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	/**
	 * Disables the plugin
	 */
	public void onDisable() {
		bLog.info("BenCmd is shutting down...");
		getServer().getScheduler().cancelTasks(this);
		for (Grave g : Grave.graves) {
			g.delete();
		}
		Grave.graves.clear();
		BenCmd.unloadAll(true);
		BenCmdBlockListener.destroyInstance();
		BenCmdPlayerListener.destroyInstance();
		BenCmdEntityListener.destroyInstance();
		if (BenCmd.isSpoutConnected()) {
			BenCmdScreenListener.destroyInstance();
			BenCmdSpoutListener.destroyInstance();
			BenCmdInventoryListener.destroyInstance();
		}
		BenCmdWorldListener.destroyInstance();
		InventoryBackend.destroyInstance();
		PluginDescriptionFile pdfFile = this.getDescription();
		BenCmd.log(pdfFile.getName() + " v" + pdfFile.getVersion() + " has been disabled!");
	}

	public void rotateLogFile() {
		BenCmd.log("BenCmd log file rotating...");
		try {
			String logName = "";
			Calendar c = Calendar.getInstance();
			clog = c;
			logName += c.get(Calendar.MONTH);
			logName += "-" + c.get(Calendar.DAY_OF_MONTH);
			logName += "-" + c.get(Calendar.YEAR);
			bLog.setLevel(Level.INFO);
			bLog.setUseParentHandlers(false);
			fh = new FileHandler(propDir + logName + ".log", true);
			fh.setFormatter(new LogFormatter());
			bLog.addHandler(fh);
			bLog.info("BenCmd log file rotated...");
		} catch (IOException e) {
			BenCmd.log(Level.SEVERE, "Unable to create/load log files. Some logging functions may not work properly!");
			BenCmd.log(e);
		}
	}

	@Override
	/**
	 * Initializes the plugin for general use.
	 */
	public void onEnable() {
		this.setNaggable(false);
		try {
			String logName = "";
			Calendar c = Calendar.getInstance();
			clog = c;
			logName += c.get(Calendar.MONTH);
			logName += "-" + c.get(Calendar.DAY_OF_MONTH);
			logName += "-" + c.get(Calendar.YEAR);
			bLog.setLevel(Level.INFO);
			bLog.setUseParentHandlers(false);
			fh = new FileHandler(propDir + logName + ".log", true);
			fh.setFormatter(new LogFormatter());
			bLog.addHandler(fh);
		} catch (IOException e) {
			BenCmd.log(Level.SEVERE, "Unable to create/load log files. Some logging functions may not work properly!");
			BenCmd.log(e);
		}
		BenCmd.log("BenCmd log ready! Running BenCmd v" + getDescription().getVersion());
		// Check for Spout
		BenCmd.log("Checking for Spout plugin...");
		if (getServer().getPluginManager().isPluginEnabled("Spout")) {
			BenCmd.log("Spout found!");
		} else {
			BenCmd.log(Level.WARNING, "Your server doesn't have Spout! Some functions may not work properly!");
		}
		// Check for missing files and add them if necessary
		bLog.info("Checking for missing database files...");
		new File(propDir).mkdirs();
		for (String f : files) {
			File file;
			if (!(file = new File(propDir + f)).exists()) {
				bLog.info("\"" + f + "\" missing... Attempting to create...");
				try {
					file.createNewFile();
				} catch (IOException e) {
					BenCmd.log(Level.SEVERE, "Error creating \"" + f + "\"!");
					BenCmd.log(e);
				}
			}
		}
		// Get some static methods ready
		User.finalizeAll();
		// Start loading classes
		BenCmd.log("Loading databases...");
		try {
			BenCmd.loadAll();
		} catch (Exception e) {
			BenCmd.log(Level.SEVERE, "Failed to load one or more databases! Aborting startup...");
			BenCmd.log(e);
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		BenCmd.log("Performing configuration check...");
		// SANITY CHECK
		if (!sanityCheck()) {
			this.getServer().getPluginManager().disablePlugin(this);
			return;
		}
		BenCmd.log("Retreiving dev list... Please wait...");
		try {
			URL devlistloc = new URL("http://cloud.github.com/downloads/BenCmd/BenCmd/devlist.txt");
			BufferedReader r = new BufferedReader(new InputStreamReader((InputStream) devlistloc.getContent()));
			String l;
			while ((l = r.readLine()) != null) {
				if (l.startsWith("$")) {
					l = l.substring(1);
					devs = l.split(",");
					break;
				}
			}
			r.close();
		} catch (Exception e) {
			BenCmd.log(Level.WARNING, "Failed to retreive dev list! (Will assume default)");
			devs = new String[] { "ben_dude56", "Deaboy" };
		}
		// Check for existing players (on reload) and add them to the maxPlayers
		// class and join them to the general channel
		for (Player player : this.getServer().getOnlinePlayers()) {
			User user;
			JoinType jt = BenCmd.getPermissionManager().getMaxPlayerHandler().join(user = User.getUser(player));
			if (jt == JoinType.NO_SLOT_NORMAL || jt == JoinType.NO_SLOT_RESERVED) {
				user.kick("The server ran out of player slots when reloading... :(");
			}
			if (BenCmd.isSpoutConnected() && BenCmd.getSpoutConnector().enabled(player)) {
				for (NPC n : BenCmd.getNPCFile().allNPCs()) {
					if (n.isSpawned()) {
						BenCmd.getSpoutConnector().sendSkin(player, n.getEntityId(), n.getSkinURL());
					}
				}
			}
			if (BenCmd.getMainProperties().getBoolean("channelsEnabled", true)) {
				getServer().dispatchCommand(player, "channel join general");
			}
		}
		if (!BenCmd.getMainProperties().getBoolean("channelsEnabled", true)) {
			BenCmd.log(Level.WARNING, "Non-channel chat using BenCmd is being phased out... Please move to channel-based chat...");
		}
		BenCmd.log("Registering events...");
		// Register all necessary events
		try {
			BenCmdPlayerListener.getInstance();
		} catch (Exception e) {
			BenCmd.log(Level.SEVERE, "Failed to register player events!");
			BenCmd.log(e);
		}
		try {
			BenCmdBlockListener.getInstance();
		} catch (Exception e) {
			BenCmd.log(Level.SEVERE, "Failed to register block events!");
			BenCmd.log(e);
		}
		try {
			BenCmdEntityListener.getInstance();
		} catch (Exception e) {
			BenCmd.log(Level.SEVERE, "Failed to register entity events!");
			BenCmd.log(e);
		}
		try {
			BenCmdWorldListener.getInstance();
		} catch (Exception e) {
			BenCmd.log(Level.SEVERE, "Failed to register world events!");
			BenCmd.log(e);
		}
		if (BenCmd.isSpoutConnected()) {
			try {
				BenCmdSpoutListener.getInstance();
				BenCmdScreenListener.getInstance();
				BenCmdInventoryListener.getInstance();
			} catch (Exception e) {
				BenCmd.log(Level.SEVERE, "Failed to register Spout events!");
				BenCmd.log(e);
			}
		}
		PluginDescriptionFile pdfFile = this.getDescription();
		// Prepare the update timer...
		BenCmd.log("Loading timers...");
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Update(), 36000, 36000);
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new TimeFreeze(), 100, 100);
		BenCmd.log(pdfFile.getName() + " v" + pdfFile.getVersion() + " has been enabled! (BenCmd Build ID: " + buildId + ")");
		for (World w : getServer().getWorlds()) {
			w.setPVP(true);
		}
	}

	public boolean update(boolean force) {
		if (!checkForUpdates() && !force) {
			return false;
		}
		getServer().broadcastMessage(ChatColor.RED + "BenCmd is updating... Some features may reset after it is updated...");
		BenCmd.log("BenCmd update in progress...");
		BenCmd.log("Opening connection...");
		try {
			URL loc;
			if (BenCmd.getMainProperties().getBoolean("downloadDevUpdates", false)) {
				loc = new URL(devLoc);
			} else {
				loc = new URL(stableLoc);
			}
			ReadableByteChannel rbc = Channels.newChannel(loc.openStream());
			FileOutputStream fos = new FileOutputStream("plugins/BenCmd.jar");
			BenCmd.log("Downloading new JAR file...");
			fos.getChannel().transferFrom(rbc, 0, 1 << 24);
			BenCmd.log("Download complete! Server is reloading...");
			getServer().reload();
			return true;
		} catch (Exception e) {
			BenCmd.log(Level.SEVERE, "Failed to download update:");
			BenCmd.log(e);
			BenCmd.log(Level.SEVERE, "BenCmd may be in an unstable state! You are advised to try downloading BenCmd manually...");
			getServer().broadcastMessage(ChatColor.RED + "BenCmd failed to update properly! Some features may cease to function...");
			return false;
		}
	}

	public boolean checkForUpdates() {
		if (updateAvailable) {
			return true; // Skip the version check
		}
		BenCmd.log("Checking for BenCmd updates...");
		URL u;
		try {
			u = new URL(verLoc);
		} catch (MalformedURLException e) {
			BenCmd.log(Level.SEVERE, "Could not process download URL... Maybe your copy of BenCmd is corrupted?");
			return false;
		}
		int stableBuild = 0;
		int devBuild = 0;
		try {
			BufferedReader r = new BufferedReader(new InputStreamReader((InputStream) u.getContent()));
			String l;
			while ((l = r.readLine()) != null) {
				try {
					if (l.startsWith("vdev:")) {
						devBuild = Integer.parseInt(l.split(":")[1]);
					} else if (l.startsWith("vstable:")) {
						stableBuild = Integer.parseInt(l.split(":")[1]);
					} else if (l.startsWith("ldev:")) {
						devLoc = l.split(":", 2)[1];
					} else if (l.startsWith("lstable:")) {
						stableLoc = l.split(":", 2)[1];
					} else {
						BenCmd.log(Level.WARNING, "Failed to get info from line: ");
						BenCmd.log(Level.WARNING, l);
					}
				} catch (NumberFormatException e) {
					BenCmd.log(Level.WARNING, "Failed to get info from line: ");
					BenCmd.log(Level.WARNING, l);
				}
			}
		} catch (IOException e) {
			BenCmd.log(Level.SEVERE, "BenCmd failed to check for updates:");
			BenCmd.log(e);
		}
		if (BenCmd.getMainProperties().getBoolean("downloadDevUpdates", false)) {
			if (buildId < devBuild) {
				BenCmd.log("A new BenCmd update is available! Use \"bencmd update\" to update your server...");
				for (User user : BenCmd.getPermissionManager().getUserFile().allWithPerm("bencmd.update")) {
					user.sendMessage(ChatColor.RED + "A new BenCmd update was detected! Use \"/bencmd update\" to update your server...");
				}
				return true;
			}
		} else {
			if (buildId < stableBuild) {
				BenCmd.log("A new BenCmd update is available! Use \"bencmd update\" to update your server...");
				for (User user : BenCmd.getPermissionManager().getUserFile().allWithPerm("bencmd.update")) {
					user.sendMessage(ChatColor.RED + "A new BenCmd update was detected! Use \"/bencmd update\" to update your server...");
				}
				return true;
			}
		}
		BenCmd.log("BenCmd is up to date!");
		return false;
	}

	public String[]	fatalconflicts		= new String[] { "Essentials", "KeepChest", "OPImmunity", "Humiliation", "Wrath", "CenZor", "xGive", "getID", "ItemDrop", "Reporter", "Spyer", "WeatherGod", "TweakedCycle", "DefaultCommands", "Prefixer", "RegexFilter", "iChat", "nChat", "ColorMe", "SimpleCensor", "Silence", "Chat Color", "SimpleWhisper", "Colors", "On Request", "iOP", "OffLine", "mGold", "StockCraft" };
	public String[]	warningconflicts	= new String[] { "WorldGuard", "Jail", "PlgSetspawn", "GiveTo", "SpawnCreature", "CreatureSpawner", "FullChest", "SpawnMob", "SimpleSpawn", "AdminCmd", "StruckDown", "Requests", "EasyShout", "DeathTpPlus", "iConomy", "RepairChest", "BukkitCompat" };
	public String[]	minorconflicts		= new String[] { "MessageChanger", "NoMoreRain", "kcpxBukkit", "Regios", "ClothCommand", "ChatCensor", "Permissions", "DeathSigns" };

	public boolean sanityCheck() {
		if (debug) {
			BenCmd.log(Level.WARNING, "You are running a version of BenCmd marked for DEBUGGING ONLY! Use of this version may cause world/database corruption. Use at your own risk!");
		}
		checkForUpdates();
		Integer v = null;
		try {
			v = Integer.parseInt(getServer().getVersion().split("-")[4].split(" ")[0].replace("b", "").replace("jnks", ""));
		} catch (IndexOutOfBoundsException e) {
			BenCmd.log(Level.WARNING, "Cannot determine CraftBukkit build... Version check will be skipped...");
		} catch (NumberFormatException e) {
			BenCmd.log(Level.WARNING, "Cannot determine CraftBukkit build... Version check will be skipped...");
		}
		if (v == null) {
			// Do nothing
		} else if (v < cbbuild) {
			BenCmd.log(Level.WARNING, "You are using a version of CraftBukkit that is earlier than this version of BenCmd was built against. This may cause unexpected problems... Run AT YOUR OWN RISK!");
		} else if (v > cbbuild) {
			BenCmd.log(Level.WARNING, "You are using a version of CraftBukkit that is newer than this version of BenCmd was built against. This may cause unexpected problems... Run AT YOUR OWN RISK!");
		}
		PluginManager pm = getServer().getPluginManager();
		int result = -1;
		for (String plugin : fatalconflicts) {
			if (pm.getPlugin(plugin) != null) {
				BenCmd.log(Level.SEVERE, "BenCmd Plugin Check: " + plugin + " can cause major problems with BenCmd.");
				result = 2;
			}
		}
		for (String plugin : warningconflicts) {
			if (pm.getPlugin(plugin) != null) {
				BenCmd.log(Level.WARNING, "BenCmd Plugin Check: " + plugin + " can cause some command conflicts with BenCmd.");
				if (result == -1) {
					result = 1;
				}
			}
		}
		for (String plugin : minorconflicts) {
			if (pm.getPlugin(plugin) != null) {
				BenCmd.log(Level.WARNING, "BenCmd Plugin Check: " + plugin + " may cause minor conflicts with BenCmd.");
				if (result == -1) {
					result = 0;
				}
			}
		}
		if (result == 2) {
			if (BenCmd.getMainProperties().getInteger("pluginCheckFailLevel", 1) <= 2) {
				BenCmd.log(Level.SEVERE, "BenCmd Plugin Conflicts have caused BenCmd to automatically shut down.");
				BenCmd.log(Level.SEVERE, "You can override this by changing pluginCheckFailLevel in main.properties to 3 or higher...");
				return false;
			}
		} else if (result == 1) {
			if (BenCmd.getMainProperties().getInteger("pluginCheckFailLevel", 1) <= 1) {
				BenCmd.log(Level.SEVERE, "BenCmd Plugin Conflicts have caused BenCmd to automatically shut down.");
				BenCmd.log(Level.SEVERE, "You can override this by changing pluginCheckFailLevel in main.properties to 2 or higher...");
				return false;
			}
		} else if (result == 0) {
			if (BenCmd.getMainProperties().getInteger("pluginCheckFailLevel", 1) <= 0) {
				BenCmd.log(Level.SEVERE, "BenCmd Plugin Conflicts have caused BenCmd to automatically shut down.");
				BenCmd.log(Level.SEVERE, "You can override this by changing pluginCheckFailLevel in main.properties to 1 or higher...");
				return false;
			}
		}
		return true;
	}

	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		incStat("cmd," + commandLabel);
		if (new BasicCommands().onCommand(sender, command, commandLabel, args)) {
			return true;
		} else if (new ChatCommands().onCommand(sender, command, commandLabel, args)) {
			return true;
		} else if (new PermissionCommands().onCommand(sender, command, commandLabel, args)) {
			return true;
		} else if (new WarpCommands().onCommand(sender, command, commandLabel, args)) {
			return true;
		} else if (new InventoryCommands().onCommand(sender, command, commandLabel, args)) {
			return true;
		} else if (new LotCommands().onCommand(sender, command, commandLabel, args)) {
			return true;
		} else if (new InvisibleCommands().onCommand(sender, command, commandLabel, args)) {
			return true;
		} else if (new ProtectedCommands().onCommand(sender, command, commandLabel, args)) {
			return true;
		} else if (new ChatChannelCommands().onCommand(sender, command, commandLabel, args)) {
			return true;
		} else if (new ReportCommands().onCommand(sender, command, commandLabel, args)) {
			return true;
		} else if (new WeatherCommands().onCommand(sender, command, commandLabel, args)) {
			return true;
		} else if (new MoneyCommands().onCommand(sender, command, commandLabel, args)) {
			return true;
		} else if (new MapCommands().onCommand(sender, command, commandLabel, args)) {
			return true;
		} else if (new PortalCommands().onCommand(sender, command, commandLabel, args)) {
			return true;
		} else if (new AdvancedCommands().onCommand(sender, command, commandLabel, args)) {
			return true;
		} else if (new BankCommands().onCommand(sender, command, commandLabel, args)) {
			return true;
		} else if (new NPCCommands().onCommand(sender, command, commandLabel, args)) {
			return true;
		} else if (new RedstoneCommands().onCommand(sender, command, commandLabel, args)) {
			return true;
		} else if (new RecordCommands().onCommand(sender, command, commandLabel, args)) {
			return true;
		} else {
			User user;
			try {
				user = User.getUser((Player) sender);
			} catch (ClassCastException e) {
				user = User.getUser();
			}
			user.sendMessage(ChatColor.RED + "You don't have permission to do that!");
			this.logPermFail();
			return true;
		}
	}

	// START WEPIF FUNCTIONS
	@Override
	public boolean hasPermission(String name, String permission) {
		return PermissionUser.matchUser(name).hasPerm(permission);
	}

	@Override
	public boolean hasPermission(String worldName, String name, String permission) {
		return hasPermission(name, permission);
	}

	@Override
	public boolean inGroup(String player, String group) {
		return PermissionUser.matchUser(player).inGroup(getPermissionManager().getGroupFile().getGroup(group));
	}

	@Override
	public String[] getGroups(String player) {
		List<String> sl;
		String[] sa = new String[(sl = getPermissionManager().getGroupFile().listGroups()).size()];
		for (int i = 0; i < sa.length; i++) {
			sa[i] = sl.get(i);
		}
		return sa;
	}

	// END WEPIF FUNCTIONS

	public class Update implements Runnable {
		@Override
		public void run() {
			checkForUpdates(); // Check for new BenCmd versions
		}
	}

	public class TimeFreeze implements Runnable {
		int t;
		
		@Override
		public void run() {
			if (Calendar.getInstance().get(Calendar.DAY_OF_MONTH) != clog.get(Calendar.DAY_OF_MONTH)) {
				rotateLogFile();
			}
			BenCmd.getRedstoneFile().timeTick();
			time.tick();
			record.getTemporaryRecording().trimToLastHour();
			if (t == 20) {
				for (Grave g : Grave.graves) {
					g.tick();
				}
				t = 0;
			} else {
				t++;
			}
		}
	}

	public static int getMajorBuildNum() {
		if (BenCmd.isRunning()) {
			return Integer.parseInt(BenCmd.getPlugin().getDescription().getVersion().split("\\.")[0]);
		} else {
			return 0;
		}
	}

	public static int getMinorBuildNum() {
		if (BenCmd.isRunning()) {
			return Integer.parseInt(BenCmd.getPlugin().getDescription().getVersion().split("\\.")[1]);
		} else {
			return 0;
		}
	}

	public static int getRevNum() {
		if (BenCmd.isRunning()) {
			return Integer.parseInt(BenCmd.getPlugin().getDescription().getVersion().split("\\.")[2]);
		} else {
			return 0;
		}
	}

	public static boolean isRunning() {
		return Bukkit.getServer().getPluginManager().isPluginEnabled("BenCmd");
	}

	public static BenCmd getPlugin() {
		return (BenCmd) Bukkit.getServer().getPluginManager().getPlugin("BenCmd");
	}
}
