package ben_dude56.plugins.bencmd;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.TreeType;
import org.bukkit.block.Block;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;

import ben_dude56.plugins.bencmd.permissions.*;
import ben_dude56.plugins.bencmd.permissions.MaxPlayers.JoinType;
import ben_dude56.plugins.bencmd.protect.*;
import ben_dude56.plugins.bencmd.reporting.*;
import ben_dude56.plugins.bencmd.warps.*;
import ben_dude56.plugins.bencmd.chat.*;
import ben_dude56.plugins.bencmd.chat.channels.*;
import ben_dude56.plugins.bencmd.invisible.*;
import ben_dude56.plugins.bencmd.invtools.*;
import ben_dude56.plugins.bencmd.invtools.kits.*;
import ben_dude56.plugins.bencmd.lots.*;
import ben_dude56.plugins.bencmd.nofly.*;
import ben_dude56.plugins.bencmd.weather.*;
import ben_dude56.plugins.bencmd.money.*;

/**
 * BenCmd for Bukkit
 * 
 * @author ben_dude56
 * 
 */
@SuppressWarnings("unused")
public class BenCmd extends JavaPlugin {
	public final static boolean debug = false;
	private final PermLoginListener permLoginListener = new PermLoginListener(
			this);
	private final InventoryBlockListener invBlockListen = new InventoryBlockListener(
			this);
	private final DeathListener death = new DeathListener(this);
	private final InventoryPlayerListener invPlayerListen = new InventoryPlayerListener(
			this);
	private final ProtectPlayerListener ppListen = new ProtectPlayerListener(
			this);
	private final ProtectBlockListener pbListen = new ProtectBlockListener(this);
	private final EntityPermListen entListen = new EntityPermListen(this);
	public final LotPlayerListener lotListener = new LotPlayerListener(this);
	public final LotBlockListener lotBListener = new LotBlockListener(this);
	private final WeatherPListener wpListen = new WeatherPListener(this);
	public final HashMap<Player, Boolean> godmode = new HashMap<Player, Boolean>();
	public final List<Player> invisible = new ArrayList<Player>();
	public final List<Player> noinvisible = new ArrayList<Player>();
	public final List<ActionableUser> offline = new ArrayList<ActionableUser>();
	public final String propDir = "plugins/BenCmd/";
	public final File mainProp = new File(propDir + "main.properties");
	public final File itemAlias = new File(propDir + "items.txt");
	public final File unlDisp = new File(propDir + "disp.db");
	public final File dispChests = new File(propDir + "chest.db");
	public final File lotFile = new File(propDir + "lots.db");
	public final File kitFile = new File(propDir + "kits.db");
	public final File proFile = new File(propDir + "protection.db");
	public final File chatFile = new File(propDir + "channels.db");
	public final File ticketFile = new File(propDir + "tickets.db");
	public final File pricesFile = new File(propDir + "prices.db");
	public PluginProperties mainProperties;
	public PluginProperties itemAliases;
	public LotFile lots;
	private Timer FreezeTimer = new Timer();
	public boolean timeRunning = true;
	public long timeFrozenAt = 0;
	public boolean heroActive = false;
	public MainPermissions perm;
	public WarpList warps;
	public HomeWarps homes;
	public PreWarp checkpoints;
	public ChatPlayerListener chatListen;
	public BlockChecker blockCheck;
	public Jail jail;
	public UnlimitedDisp dispensers;
	public DispChest chests;
	public KitList kits;
	public CreeperListener creeperListen;
	public Invisibility inv;
	public ProtectFile protectFile;
	public MaxPlayers maxPlayers;
	public ChatChannelController channels;
	public ReportFile reports;
	public FlyDetect flyDetect;
	public WeatherBinding strikeBind;
	public PriceFile prices;
	public KickList kicked;
	public Logger log = Logger.getLogger("minecraft");

	public boolean checkID(int id) {
		for (Material item : Material.values()) {
			if (item.getId() == id) {
				return true;
			}
		}
		return false;
	}

	@Override
	/**
	 * Disables the plugin
	 */
	public void onDisable() {
		// Cancel all running timers
		FreezeTimer.cancel();
		FreezeTimer = null;
		chatListen.slow.slowTimer.cancel();
		chatListen.slow.slowTimer = null;
		inv.timer.cancel();
		inv.timer = null;
		flyDetect.flyTime.cancel();
		flyDetect.flyTime = null;
		kicked.killTimer();
		PluginDescriptionFile pdfFile = this.getDescription();
		log.info(pdfFile.getName() + " v" + pdfFile.getVersion()
				+ " has been disabled!");
	}

	@Override
	/**
	 * Initializes the plugin for general use.
	 */
	public void onEnable() {
		// Check for missing files and add them if necessary
		new File(propDir).mkdirs();
		if (!mainProp.exists()) {
			try {
				mainProp.createNewFile();
			} catch (IOException e) {
				System.out.println("BenCmd had a problem:");
				e.printStackTrace();
			}
		}
		if (!itemAlias.exists()) {
			try {
				itemAlias.createNewFile();
			} catch (IOException e) {
				System.out.println("BenCmd had a problem:");
				e.printStackTrace();
			}
		}
		if (!unlDisp.exists()) {
			try {
				unlDisp.createNewFile();
			} catch (IOException e) {
				System.out.println("BenCmd had a problem:");
				e.printStackTrace();
			}
		}
		if (!dispChests.exists()) {
			try {
				dispChests.createNewFile();
			} catch (IOException e) {
				System.out.println("BenCmd had a problem:");
				e.printStackTrace();
			}
		}
		if (!lotFile.exists()) {
			try {
				lotFile.createNewFile();
			} catch (IOException e) {
				System.out.println("BenCmd had a problem:");
				e.printStackTrace();
			}
		}
		if (!kitFile.exists()) {
			try {
				kitFile.createNewFile();
			} catch (IOException e) {
				System.out.println("BenCmd had a problem:");
				e.printStackTrace();
			}
		}
		if (!proFile.exists()) {
			try {
				proFile.createNewFile();
			} catch (IOException e) {
				System.out.println("BenCmd had a problem:");
				e.printStackTrace();
			}
		}
		if (!chatFile.exists()) {
			try {
				chatFile.createNewFile();
			} catch (IOException e) {
				System.out.println("BenCmd had a problem:");
				e.printStackTrace();
			}
		}
		if (!ticketFile.exists()) {
			try {
				ticketFile.createNewFile();
			} catch (IOException e) {
				System.out.println("BenCmd had a problem:");
				e.printStackTrace();
			}
		}
		if (!ticketFile.exists()) {
			try {
				ticketFile.createNewFile();
			} catch (IOException e) {
				System.out.println("BenCmd had a problem:");
				e.printStackTrace();
			}
		}
		// Start loading classes
		perm = new MainPermissions(this);
		warps = new WarpList(this);
		homes = new HomeWarps(this);
		checkpoints = new PreWarp(this);
		blockCheck = new BlockChecker(this);
		mainProperties = new PluginProperties(propDir + "main.properties");
		mainProperties.loadFile();
		itemAliases = new PluginProperties(propDir + "items.txt");
		itemAliases.loadFile();
		chatListen = new ChatPlayerListener(this);
		jail = new Jail(this);
		dispensers = new UnlimitedDisp(propDir + "disp.db");
		chests = new DispChest(propDir + "chest.db");
		lots = new LotFile(this);
		kits = new KitList(this);
		creeperListen = new CreeperListener(this);
		inv = new Invisibility(this);
		protectFile = new ProtectFile(this, propDir + "protection.db");
		channels = new ChatChannelController(propDir + "channels.db", this);
		maxPlayers = new MaxPlayers(this, mainProperties.getInteger(
				"maxPlayers", 10), mainProperties.getInteger("maxReserve", 4),
				mainProperties.getBoolean("reserveActive", true),
				mainProperties.getBoolean("indefActive", true));
		reports = new ReportFile(this);
		flyDetect = new FlyDetect(this);
		strikeBind = new WeatherBinding(this);
		prices = new PriceFile(this, propDir + "prices.db");
		kicked = new KickList(this);
		// SANITY CHECK
		if(!sanityCheck()) {
			this.getServer().getPluginManager().disablePlugin(this);
			return;
		}
		// Check for existing players (on reload) and add them to the maxPlayers
		// class
		for (Player player : this.getServer().getOnlinePlayers()) {
			User user;
			JoinType jt = maxPlayers.join(user = new User(this, player));
			if (jt == JoinType.NO_SLOT_NORMAL
					|| jt == JoinType.NO_SLOT_RESERVED) {
				user.Kick("The server ran out of player slots when reloading... :(");
			}
		}
		// Register all necessary events
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_JOIN, this.chatListen,
				Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_LOGIN, this.permLoginListener,
				Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_CHAT, this.chatListen,
				Event.Priority.Highest, this);
		pm.registerEvent(Event.Type.BLOCK_PLACE, this.blockCheck,
				Event.Priority.High, this);
		pm.registerEvent(Event.Type.BLOCK_BREAK, this.blockCheck,
				Event.Priority.High, this);
		pm.registerEvent(Event.Type.BLOCK_BURN, this.blockCheck,
				Event.Priority.Highest, this);
		pm.registerEvent(Event.Type.BLOCK_IGNITE, this.blockCheck,
				Event.Priority.Highest, this);
		pm.registerEvent(Event.Type.PLAYER_INTERACT, this.invPlayerListen,
				Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_BREAK, this.invBlockListen,
				Event.Priority.Highest, this);
		pm.registerEvent(Event.Type.ENTITY_DEATH, this.death,
				Event.Priority.Highest, this);
		pm.registerEvent(Event.Type.ENTITY_DAMAGE, this.death,
				Event.Priority.Highest, this);
		pm.registerEvent(Event.Type.PLAYER_INTERACT, this.lotListener,
				Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.REDSTONE_CHANGE, this.invBlockListen,
				Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_BREAK, this.lotBListener,
				Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_PLACE, this.lotBListener,
				Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_PICKUP_ITEM, this.permLoginListener,
				Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_DROP_ITEM, this.permLoginListener,
				Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_TARGET, this.creeperListen,
				Event.Priority.Highest, this);
		pm.registerEvent(Event.Type.BLOCK_BREAK, this.pbListen,
				Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_INTERACT, this.ppListen,
				Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_BUCKET_EMPTY, this.lotListener,
				Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_BUCKET_FILL, this.lotListener,
				Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_INTERACT, this.chatListen,
				Event.Priority.Highest, this);
		pm.registerEvent(Event.Type.PLAYER_QUIT, this.chatListen,
				Event.Priority.Highest, this);
		pm.registerEvent(Event.Type.PLAYER_KICK, this.chatListen,
				Event.Priority.Highest, this);
		pm.registerEvent(Event.Type.EXPLOSION_PRIME, this.entListen,
				Event.Priority.Highest, this);
		pm.registerEvent(Event.Type.PLAYER_INTERACT, this.wpListen,
				Event.Priority.Highest, this);
		PluginDescriptionFile pdfFile = this.getDescription();
		// Prepare the time lock timer
		FreezeTimer.schedule(new TimeFreeze(this), 0, 100);
		log.info(pdfFile.getName() + " v" + pdfFile.getVersion()
				+ " has been enabled!");
		if (mainProperties.getBoolean("channelsEnabled", false)) {
			log.warning("BenCmd Chat Channels (Experimental) are active...");
		}
	}

	/**
	 * Used to add or remove a player's god status
	 * 
	 * @param player
	 *            The player who's status should be edited
	 * @param value
	 *            Whether the player would be godded
	 */
	public void setGod(final Player player, final boolean value) {
		if (value) {
			godmode.put(player, value);
			player.setHealth(20);
		} else {
			godmode.remove(player);
		}
	}

	/**
	 * Checks if a player has god status
	 * 
	 * @param player
	 *            The player to check the status of
	 * @return Returns whether or not the player has god status
	 */
	public boolean isGod(final Player player) {
		if (godmode.containsKey(player)) {
			return godmode.get(player);
		} else {
			return false;
		}
	}

	public String[] fatalconflicts = new String[] { "Essentials", "KeepChest",
			"OPImmunity", "Humiliation", "Wrath", "CenZor", "xGive", "getID",
			"ItemDrop", "Reporter", "Spyer", "WeatherGod", "TweakedCycle",
			"DefaultCommands", "Prefixer", "RegexFilter", "iChat", "nChat",
			"ColorMe", "SimpleCensor", "Silence", "Chat Color", "SimpleWhisper",
			"Colors", "On Request", "iOP", "OffLine", "mGold", "StockCraft" };
	public String[] warningconflicts = new String[] { "WorldGuard", "Jail",
			"PlgSetspawn", "GiveTo", "SpawnCreature", "CreatureSpawner",
			"FullChest", "SpawnMob", "SimpleSpawn", "AdminCmd", "StruckDown",
			"Requests", "EasyShout", "DeathTpPlus", "iConomy", "RepairChest" };
	public String[] minorconflicts = new String[] { "MessageChanger",
			"NoMoreRain", "kcpxBukkit", "Regios", "ClothCommand", "ChatCensor",
			"Permissions", "DeathSigns" };

	public boolean sanityCheck() {
		PluginManager pm = getServer().getPluginManager();
		int result = -1;
		for (String plugin : fatalconflicts) {
			if (pm.getPlugin(plugin) != null) {
				log.severe("BenCmd Plugin Check: " + plugin
						+ " can cause major problems with BenCmd.");
				result = 2;
			}
		}
		for (String plugin : warningconflicts) {
			if (pm.getPlugin(plugin) != null) {
				log.warning("BenCmd Plugin Check: " + plugin
						+ " can cause some command conflicts with BenCmd.");
				if (result == -1) {
					result = 1;
				}
			}
		}
		for (String plugin : minorconflicts) {
			if (pm.getPlugin(plugin) != null) {
				log.info("BenCmd Plugin Check: " + plugin
						+ " may cause minor conflicts with BenCmd.");
				if (result == -1) {
					result = 0;
				}
			}
		}
		if (result == 2) {
			if (mainProperties.getInteger("pluginCheckFailLevel", 1) <= 2) {
				log.severe("BenCmd Plugin Conflicts have caused BenCmd to automatically shut down.");
				log.severe("You can override this by changing pluginCheckFailLevel in main.properties to 3 or higher...");
				return false;
			}
		} else if (result == 1) {
			if (mainProperties.getInteger("pluginCheckFailLevel", 1) <= 1) {
				log.severe("BenCmd Plugin Conflicts have caused BenCmd to automatically shut down.");
				log.severe("You can override this by changing pluginCheckFailLevel in main.properties to 2 or higher...");
				return false;
			}
		} else if (result == 0) {
			if (mainProperties.getInteger("pluginCheckFailLevel", 1) <= 0) {
				log.severe("BenCmd Plugin Conflicts have caused BenCmd to automatically shut down.");
				log.severe("You can override this by changing pluginCheckFailLevel in main.properties to 1 or higher...");
				return false;
			}
		}
		return true;
	}

	public boolean onCommand(CommandSender sender, Command command,
			String commandLabel, String[] args) {
		if (new BasicCommands(this).onCommand(sender, command, commandLabel,
				args)) {
			return true;
		} else if (new ChatCommands(this).onCommand(sender, command,
				commandLabel, args)) {
			return true;
		} else if (new PermissionCommands(this).onCommand(sender, command,
				commandLabel, args)) {
			return true;
		} else if (new WarpCommands(this).onCommand(sender, command,
				commandLabel, args)) {
			return true;
		} else if (new InventoryCommands(this).onCommand(sender, command,
				commandLabel, args)) {
			return true;
		} else if (new LotCommands(this).onCommand(sender, command,
				commandLabel, args)) {
			return true;
		} else if (new InvisibleCommands(this).onCommand(sender, command,
				commandLabel, args)) {
			return true;
		} else if (new ProtectedCommands(this).onCommand(sender, command,
				commandLabel, args)) {
			return true;
		} else if (new ChatChannelCommands(this).onCommand(sender, command,
				commandLabel, args)) {
			return true;
		} else if (new ReportCommands(this).onCommand(sender, command,
				commandLabel, args)) {
			return true;
		} else if (new WeatherCommands(this).onCommand(sender, command,
				commandLabel, args)) {
			return true;
		} else if (new MoneyCommands(this).onCommand(sender, command,
				commandLabel, args)) {
			return true;
		} else {
			User user;
			try {
				user = new User(this, (Player) sender);
			} catch (ClassCastException e) {
				user = new User(this);
			}
			user.sendMessage(ChatColor.RED
					+ "You don't have permission to do that!");
			return true;
		}
	}

}
