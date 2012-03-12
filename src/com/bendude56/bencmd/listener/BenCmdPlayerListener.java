package com.bendude56.bencmd.listener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.block.CraftChest;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.*;

import com.bendude56.bencmd.*;
import com.bendude56.bencmd.advanced.Grave;
import com.bendude56.bencmd.advanced.Shelf;
import com.bendude56.bencmd.advanced.ViewableInventory;
import com.bendude56.bencmd.advanced.npc.BankManagerNPC;
import com.bendude56.bencmd.advanced.npc.BankerNPC;
import com.bendude56.bencmd.advanced.npc.BlacksmithNPC;
import com.bendude56.bencmd.advanced.npc.Clickable;
import com.bendude56.bencmd.advanced.npc.EntityNPC;
import com.bendude56.bencmd.advanced.npc.NPC;
import com.bendude56.bencmd.advanced.npc.StaticNPC;
import com.bendude56.bencmd.invtools.InventoryBackend;
import com.bendude56.bencmd.lots.Corner;
import com.bendude56.bencmd.lots.sparea.GroupArea;
import com.bendude56.bencmd.lots.sparea.MsgArea;
import com.bendude56.bencmd.lots.sparea.PVPArea;
import com.bendude56.bencmd.lots.sparea.SPArea;
import com.bendude56.bencmd.lots.sparea.TRArea;
import com.bendude56.bencmd.money.Currency;
import com.bendude56.bencmd.permissions.PermissionUser;
import com.bendude56.bencmd.protect.ProtectedBlock;
import com.bendude56.bencmd.recording.RecordEntry.BlockPlaceEntry;
import com.bendude56.bencmd.recording.RecordEntry.ChestOpenEntry;
import com.bendude56.bencmd.warps.HomePortal;
import com.bendude56.bencmd.warps.Portal;
import com.bendude56.bencmd.warps.Warp;

public class BenCmdPlayerListener implements Listener, EventExecutor {

	// Singleton instancing

	private static BenCmdPlayerListener	instance	= null;

	public static BenCmdPlayerListener getInstance() {
		if (instance == null) {
			return instance = new BenCmdPlayerListener();
		} else {
			return instance;
		}
	}

	public static void destroyInstance() {
		instance = null;
	}

	public HashMap<String, Corner>			corner	= new HashMap<String, Corner>();
	private HashMap<Player, List<SPArea>>	areas	= new HashMap<Player, List<SPArea>>();
	private List<Player>					ignore	= new ArrayList<Player>();
	private HashMap<Player, List<NPC>>		sent	= new HashMap<Player, List<NPC>>();

	public void checkPlayer(String player) {
		if (!corner.containsKey(player)) {
			corner.put(player, new Corner());
		}
	}

	private void sendSkins(Player p, Location l) {
		if (BenCmd.isSpoutConnected()) {
			if (!sent.containsKey(p)) {
				sent.put(p, new ArrayList<NPC>());
			}
			for (NPC n : BenCmd.getNPCFile().allNPCs()) {
				if (n.isSpawned() && l.getWorld().equals(n.getCurrentLocation().getWorld()) && l.distance(n.getCurrentLocation()) < 50) {
					if (!sent.get(p).contains(n)) {
						sent.get(p).add(n);
						BenCmd.getSpoutConnector().sendSkin(p, n.getEntityId(), n.getSkinURL());
					}
				} else {
					if (sent.get(p).contains(n)) {
						sent.get(p).remove(n);
						BenCmd.getSpoutConnector().sendSkin(p, n.getEntityId(), "http://s3.amazonaws.com/MinecraftSkins/" + n.getName() + ".png");
					}
				}
			}
		}
	}

	private BenCmdPlayerListener() {
		PlayerChatEvent.getHandlerList().register(new RegisteredListener(this, this, EventPriority.HIGHEST, BenCmd.getPlugin(), false));
		PlayerJoinEvent.getHandlerList().register(new RegisteredListener(this, this, EventPriority.HIGHEST, BenCmd.getPlugin(), false));
		PlayerQuitEvent.getHandlerList().register(new RegisteredListener(this, this, EventPriority.HIGHEST, BenCmd.getPlugin(), false));
		PlayerKickEvent.getHandlerList().register(new RegisteredListener(this, this, EventPriority.HIGHEST, BenCmd.getPlugin(), false));
		PlayerInteractEvent.getHandlerList().register(new RegisteredListener(this, this, EventPriority.LOWEST, BenCmd.getPlugin(), false));
		PlayerInteractEntityEvent.getHandlerList().register(new RegisteredListener(this, this, EventPriority.NORMAL, BenCmd.getPlugin(), false));
		PlayerLoginEvent.getHandlerList().register(new RegisteredListener(this, this, EventPriority.LOWEST, BenCmd.getPlugin(), false));
		PlayerPickupItemEvent.getHandlerList().register(new RegisteredListener(this, this, EventPriority.LOWEST, BenCmd.getPlugin(), false));
		PlayerDropItemEvent.getHandlerList().register(new RegisteredListener(this, this, EventPriority.LOWEST, BenCmd.getPlugin(), false));
		PlayerBucketEmptyEvent.getHandlerList().register(new RegisteredListener(this, this, EventPriority.LOWEST, BenCmd.getPlugin(), false));
		PlayerBucketFillEvent.getHandlerList().register(new RegisteredListener(this, this, EventPriority.LOWEST, BenCmd.getPlugin(), false));
		PlayerRespawnEvent.getHandlerList().register(new RegisteredListener(this, this, EventPriority.LOWEST, BenCmd.getPlugin(), false));
		PlayerTeleportEvent.getHandlerList().register(new RegisteredListener(this, this, EventPriority.NORMAL, BenCmd.getPlugin(), false));
		PlayerPortalEvent.getHandlerList().register(new RegisteredListener(this, this, EventPriority.LOWEST, BenCmd.getPlugin(), false));
		PlayerMoveEvent.getHandlerList().register(new RegisteredListener(this, this, EventPriority.NORMAL, BenCmd.getPlugin(), false));
	}

	private void chat(PlayerChatEvent event) {
		// If external chat is enabled, skip this event
		if (BenCmd.getMainProperties().getBoolean("externalChat", false)) {
			return;
		}
		String message = event.getMessage();
		User user = User.getUser(event.getPlayer());

		// Check if player is muted
		if (user.isMuted() != null) {
			event.setCancelled(true);
			BenCmd.getLocale().sendMessage(user, "misc.talk.muted");
			return;
		}
		if (user.inChannel()) {
			user.getActiveChannel().sendChat(user, message);
		} else {
			BenCmd.getLocale().sendMessage(user, "misc.talk.noChannel");
		}
		event.setCancelled(true);
	}

	private void userInit(PlayerJoinEvent event) {
		// Prepare the user instance
		ViewableInventory.replInv((CraftPlayer) event.getPlayer());
		User user = User.getUser(event.getPlayer());

		// Disable cheats + flymod if applicable
		String cheatstring = "";
		if (!user.hasPerm("bencmd.allowfly")) {
			// No Zombe-mod flying
			cheatstring += "&f &f &1 &0 &2 &4 ";

			// No CJB flying
			cheatstring += "&3 &9 &2 &0 &0 &1 ";
		}
		if (!user.hasPerm("bencmd.allowcheat")) {
			// No Zombe-mod cheats
			cheatstring += "&f &f &2 &0 &4 &8 ";

			// No CJB cheats
			cheatstring += "&3 &9 &2 &0 &0 &2 ";

			// No entities on minimap
			cheatstring += "&3 &9 &2 &0 &0 &3 ";
		}
		if (!cheatstring.isEmpty()) {
			cheatstring = cheatstring.replaceAll("(&([a-f0-9]))", "\u00A7$2");
			user.sendMessage(cheatstring);
		}

		// Announce updates to admins
		if (BenCmd.updateAvailable && user.hasPerm("bencmd.update")) {
			BenCmd.getLocale().sendMessage(user, "basic.updateNag");
		}

		// List players
		if (user.hasPerm("bencmd.chat.list")) {
			Bukkit.dispatchCommand(user.getHandle(), "list");
		}

		// Send chat status notifications
		if (user.isMuted() != null) {
			BenCmd.getLocale().sendMessage(user, "misc.talk.muted");
		}
		if (user.hasPerm("bencmd.ticket.readall") && BenCmd.getReports().unreadTickets()) {
			BenCmd.getLocale().sendMessage(user, "basic.unreadTickets");
		}

		// Join general channel
		if (BenCmd.getChatChannels() != null) {
			if (BenCmd.getChatChannels().getChannel(user.getVar("bencmd.chat.defaultchannel", "general")) != null) {
				user.joinChannel(BenCmd.getChatChannels().getChannel(user.getVar("bencmd.chat.defaultchannel", "general")), false);
			}
		}
		event.setJoinMessage(BenCmd.getLocale().getString("basic.join", user.getColor() + user.getDisplayName()));

		// Check for jailing/unjailing
		if (BenCmd.getPermissionManager().getActionFile().isUnjailed(user) != null) {
			user.spawn();
			BenCmd.getPermissionManager().getActionFile().removeAction(BenCmd.getPermissionManager().getActionFile().isUnjailed(user));
		}
		if (user.isJailed() != null) {
			user.warpTo(BenCmd.getPermissionManager().getJailWarp());
		}

		// Special effects for devs
		if (user.isDev()) {
			user.getPlayerHandle().getWorld().strikeLightningEffect(user.getPlayerHandle().getLocation());
			Bukkit.broadcastMessage(BenCmd.getLocale().getString("basic.joinDev"));
		}
	}

	private void quitFinalize(PlayerQuitEvent event) {

		// Remove any of their graves, if applicable
		for (int i = 0; i < Grave.graves.size(); i++) {
			Grave g = Grave.graves.get(i);
			if (g.getPlayer().equals(event.getPlayer())) {
				g.delete();
				Grave.graves.remove(i);
			}
		}
		if (Grave.returns.containsKey(event.getPlayer())) {
			Grave.returns.remove(event.getPlayer());
		}
		User user = User.getUser(event.getPlayer());

		// Format quit message
		if (user.isOffline()) {
			user.goOnlineNoMsg();
			event.setQuitMessage(null);
		} else {
			event.setQuitMessage(BenCmd.getLocale().getString("basic.quit", user.getColor() + user.getDisplayName()));
		}

		// Remove them from the maximum players lists
		BenCmd.getPermissionManager().getMaxPlayerHandler().leave(user);

		// Remove all special stauses from them, if applicable
		if (user.isPoofed()) {
			user.unPoof();
		}
		if (user.isNoPoofed()) {
			user.unNoPoof();
		}
		if (user.isAllPoofed()) {
			user.unAllPoof();
		}
		if (user.inChannel()) {
			user.leaveChannel(false);
		}
		if (user.isGod()) {
			user.makeNonGod();
		}
		user.unspyAll();
		BenCmd.getMonitorController().disconnect(event.getPlayer());

		// Destroy their user instance
		User.finalizeUser(user);
	}

	private void kickFinalize(PlayerKickEvent event) {

		// Remove any of their graves, if applicable
		for (int i = 0; i < Grave.graves.size(); i++) {
			Grave g = Grave.graves.get(i);
			if (g.getPlayer().equals(event.getPlayer())) {
				g.delete();
				Grave.graves.remove(i);
			}
		}
		if (Grave.returns.containsKey(event.getPlayer())) {
			Grave.returns.remove(event.getPlayer());
		}
		User user = User.getUser(event.getPlayer());

		// Format quit message
		if (user.isOffline()) {
			user.goOnlineNoMsg();
			event.setLeaveMessage(null);
		} else {
			event.setLeaveMessage(BenCmd.getLocale().getString("basic.quit", user.getColor() + user.getDisplayName()));
		}

		// Remove them from the maximum players lists
		BenCmd.getPermissionManager().getMaxPlayerHandler().leave(user);

		// Remove all special stauses from them, if applicable
		if (user.isPoofed()) {
			user.unPoof();
		}
		if (user.isNoPoofed()) {
			user.unNoPoof();
		}
		if (user.isAllPoofed()) {
			user.unAllPoof();
		}
		if (user.inChannel()) {
			user.leaveChannel(false);
		}
		if (user.isGod()) {
			user.makeNonGod();
		}
		user.unspyAll();
		BenCmd.getMonitorController().disconnect(event.getPlayer());

		// Destroy their user instance
		User.finalizeUser(user);

		// Announce kick to console (McMyAdmin compatibility)
		BenCmd.log(user.getName() + " lost connection: User was kicked");
	}

	private void bookshelfInteract(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.BOOKSHELF) {
			Shelf shelf;
			User user = User.getUser(event.getPlayer());
			if ((shelf = BenCmd.getShelfFile().getShelf(event.getClickedBlock().getLocation())) != null) {
				event.setCancelled(true);
				BenCmd.getLocale().sendMessage(user, "misc.shelf.read");
				user.sendMessage(ChatColor.YELLOW + shelf.getText());
			}
		}
	}
	
	private void spawnerEggInteract(PlayerInteractEvent event) {
		if ((event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) && (event.getPlayer().getItemInHand().getType() == Material.MONSTER_EGG || event.getPlayer().getItemInHand().getType() == Material.MONSTER_EGGS)) {
			PermissionUser user = BenCmd.getPermissionManager().getUserFile().getUser(event.getPlayer().getName());
			if (user == null) {
				return;
			}
			if (!user.hasPerm("bencmd.spawnmob")) {
				event.setCancelled(true);
				BenCmd.getLocale().sendMessage(User.getUser(event.getPlayer()), "basic.noPermission");
			}
		}
	}

	private void disposalChestInteract(PlayerInteractEvent event) {
		InventoryBackend back = InventoryBackend.getInstance();
		if (!(event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
			return;
		}
		Block block = event.getClickedBlock();
		User user = User.getUser(event.getPlayer());
		if (back.TryDispense(block)) {
			event.setCancelled(true);
		}
		if (block.getType() == Material.CHEST && BenCmd.getDisposals().isDisposalChest(block.getLocation())) {
			CraftChest chest = new CraftChest(block);
			chest.getInventory().clear();
			BenCmd.getLocale().sendMessage(user, "misc.disp.alert");
		}
	}

	private void strikeBind(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) {
			return;
		}
		if (BenCmd.getStrikeBindings().bindEquipped(event.getPlayer())) {
			Bukkit.dispatchCommand(event.getPlayer(), "strike");
		}
	}

	private void lockInteract(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.BED_BLOCK) {
			if (event.getPlayer().getWorld().getEnvironment() == Environment.NETHER) {
				event.setCancelled(true);
			}
		}
		if ((event.getAction() != Action.RIGHT_CLICK_BLOCK && !(event.getAction() == Action.LEFT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.WOODEN_DOOR)) || event.isCancelled()) {
			return;
		}
		int id;
		ProtectedBlock block;
		if ((id = BenCmd.getProtections().getProtection(event.getClickedBlock().getLocation())) != -1) {
			block = BenCmd.getProtections().getProtection(id);
			User user = User.getUser(event.getPlayer());
			if (!block.canUse(user.getName()) && !user.hasPerm("bencmd.lock.peek")) {
				event.setCancelled(true);
				BenCmd.getLocale().sendMultilineMessage(user, "misc.protect.noUse");
			} else {
				if (!user.getName().equalsIgnoreCase(block.getOwner())) {
					BenCmd.log(BenCmd.getLocale().getString("misc.protect.logUse", user.getName(), block.getOwner(), block.getId() + ""));
				}
			}
		}
	}

	private void lotSelectInteract(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			User user = User.getUser(event.getPlayer());
			if (user.hasPerm("bencmd.lot.select") && user.getPlayerHandle().getItemInHand().getType() == Material.WOOD_SPADE) {
				checkPlayer(user.getName());
				if (this.corner.get(user.getName()).corner2set) {
					if (this.corner.get(user.getName()).getCorner2().equals(event.getClickedBlock().getLocation())) {
						return;
					}
				}
				this.corner.get(user.getName()).setCorner2(event.getClickedBlock().getLocation());
				Location corner2 = this.corner.get(user.getName()).getCorner2();
				BenCmd.getLocale().sendMessage(user, "misc.lot.corner2", corner2.getX() + "", corner2.getY() + "", corner2.getZ() + "");
			}
			return;
		} else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
			User user = User.getUser(event.getPlayer());
			if (user.hasPerm("bencmd.lot.select") && user.getPlayerHandle().getItemInHand().getType() == Material.WOOD_SPADE) {
				checkPlayer(user.getName());
				if (this.corner.get(user.getName()).corner1set) {
					if (this.corner.get(user.getName()).getCorner1().equals(event.getClickedBlock().getLocation())) {
						return;
					}
				}
				this.corner.get(user.getName()).setCorner1(event.getClickedBlock().getLocation());
				Location corner1 = this.corner.get(user.getName()).getCorner1();
				BenCmd.getLocale().sendMessage(user, "misc.lot.corner1", corner1.getX() + "", corner1.getY() + "", corner1.getZ() + "");
			}
			return;
		}
	}

	private void lotBucketEmpty(PlayerBucketEmptyEvent event) {
		User user = User.getUser(event.getPlayer());

		if (!BenCmd.getLots().canBuildHere(user.getPlayerHandle(), event.getBlockClicked().getLocation())) {
			event.setCancelled(true);
			BenCmd.getLocale().sendMessage(user, "basic.noBuild");
		}
	}

	private void lotBucketFill(PlayerBucketFillEvent event) {
		User user = User.getUser(event.getPlayer());

		if (!BenCmd.getLots().canBuildHere(user.getPlayerHandle(), event.getBlockClicked().getLocation())) {
			event.setCancelled(true);
			BenCmd.getLocale().sendMessage(user, "basic.noBuild");
		}
	}

	private void jailBucketEmpty(PlayerBucketEmptyEvent event) {
		Player player = event.getPlayer();
		
		if (User.getUser(player).isJailed() != null) {
			event.setCancelled(true);
		}
	}
	
	private void jailBucketFill(PlayerBucketFillEvent event) {
		Player player = event.getPlayer();
		
		if (User.getUser(player).isJailed() != null) {
			event.setCancelled(true);
		}
	}
	
	private void pvpRespawn(PlayerRespawnEvent event) {
		if (Grave.returns.containsKey(event.getPlayer())) {
			for (ItemStack i : Grave.returns.get(event.getPlayer())) {
				event.getPlayer().getInventory().addItem(i);
			}
			Grave.returns.remove(event.getPlayer());
		}
	}

	private void areaMoveCheck(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		if (ignore.contains(p)) {
			event.setCancelled(true);
			return;
		}
		if (!areas.containsKey(p)) {
			areas.put(p, new ArrayList<SPArea>());
		}
		for (SPArea a : BenCmd.getAreas().listAreas()) {
			if (a instanceof PVPArea && a.insideArea(p.getLocation())) {
				int money = 0;
				for (Currency c : BenCmd.getMarketController().getCurrencies()) {
					for (ItemStack i : p.getInventory().all(c.getMaterial()).values()) {
						if (i.getDurability() == c.getDurability()) {
							money += Math.floor(i.getAmount() * c.getPrice());
						}
					}
				}
				if (money < ((PVPArea) a).getMinimumCurrency()) {
					ignore.add(p);
					BenCmd.getLocale().sendMessage(User.getUser(p), "basic.insufficientMoney", ((PVPArea) a).getMinimumCurrency() + "");
					int c = 1;
					while (true) {
						Location f = p.getLocation();
						f.setX(f.getX() + c);
						if (!a.insideArea(f) && f.getBlock().getType() == Material.AIR) {
							event.setTo(f);
							event.setCancelled(false);
							ignore.remove(p);
							return;
						}
						f.setX(f.getX() - (c * 2));
						if (!a.insideArea(f) && f.getBlock().getType() == Material.AIR) {
							event.setTo(f);
							event.setCancelled(false);
							ignore.remove(p);
							return;
						}
						f.setX(f.getX() + c);
						f.setZ(f.getZ() + c);
						if (!a.insideArea(f) && f.getBlock().getType() == Material.AIR) {
							event.setTo(f);
							event.setCancelled(false);
							ignore.remove(p);
							return;
						}
						f.setZ(f.getZ() - (c * 2));
						if (!a.insideArea(f) && f.getBlock().getType() == Material.AIR) {
							event.setTo(f);
							event.setCancelled(false);
							ignore.remove(p);
							return;
						}
						f.setZ(f.getZ() + c);
						f.setY(f.getY() + c);
						if (!a.insideArea(f) && f.getBlock().getType() == Material.AIR) {
							event.setTo(f);
							event.setCancelled(false);
							ignore.remove(p);
							return;
						}
						f.setY(f.getY() - (c * 2));
						if (!a.insideArea(f) && f.getBlock().getType() == Material.AIR) {
							event.setTo(f);
							event.setCancelled(false);
							ignore.remove(p);
							return;
						}
						c += 1;
					}
				}
			}
			if (a instanceof TRArea) {
				if (a.insideArea(p.getLocation()) && ((TRArea) a).isLocked(p)) {
					ignore.add(p);
					BenCmd.getLocale().sendMessage(User.getUser(p), "misc.area.noEnterTime");
					int c = 1;
					while (true) {
						Location f = p.getLocation();
						f.setX(f.getX() + c);
						if (!a.insideArea(f) && f.getBlock().getType() == Material.AIR) {
							event.setTo(f);
							event.setCancelled(false);
							ignore.remove(p);
							return;
						}
						f.setX(f.getX() - (c * 2));
						if (!a.insideArea(f) && f.getBlock().getType() == Material.AIR) {
							event.setTo(f);
							event.setCancelled(false);
							ignore.remove(p);
							return;
						}
						f.setX(f.getX() + c);
						f.setZ(f.getZ() + c);
						if (!a.insideArea(f) && f.getBlock().getType() == Material.AIR) {
							event.setTo(f);
							event.setCancelled(false);
							ignore.remove(p);
							return;
						}
						f.setZ(f.getZ() - (c * 2));
						if (!a.insideArea(f) && f.getBlock().getType() == Material.AIR) {
							event.setTo(f);
							event.setCancelled(false);
							ignore.remove(p);
							return;
						}
						f.setZ(f.getZ() + c);
						f.setY(f.getY() + c);
						if (!a.insideArea(f) && f.getBlock().getType() == Material.AIR) {
							event.setTo(f);
							event.setCancelled(false);
							ignore.remove(p);
							return;
						}
						f.setY(f.getY() - (c * 2));
						if (!a.insideArea(f) && f.getBlock().getType() == Material.AIR) {
							event.setTo(f);
							event.setCancelled(false);
							ignore.remove(p);
							return;
						}
						c += 1;
					}
				}
			}
			if (a instanceof MsgArea) {
				if (a.insideArea(p.getLocation())) {
					if (!areas.get(p).contains(a)) {
						if (((MsgArea) a).getEnterMessage().startsWith("§") && ((MsgArea) a).getEnterMessage().length() == 2) {
							return;
						}
						p.sendMessage(((MsgArea) a).getEnterMessage());
						areas.get(p).add(a);
					}
				} else {
					if (areas.get(p).contains(a)) {
						if (((MsgArea) a).getLeaveMessage().startsWith("§") && ((MsgArea) a).getLeaveMessage().length() == 2) {
							return;
						}
						p.sendMessage(((MsgArea) a).getLeaveMessage());
						areas.get(p).remove(a);
					}
				}
			}
			if (a instanceof GroupArea) {
				if (a.insideArea(p.getLocation()) && !((GroupArea) a).canEnter(User.getUser(p))) {
					ignore.add(p);
					BenCmd.getLocale().sendMessage(User.getUser(p), "misc.area.noEnterGroup");
					int c = 1;
					while (true) {
						Location f = p.getLocation();
						f.setX(f.getX() + c);
						if (!a.insideArea(f) && f.getBlock().getType() == Material.AIR) {
							event.setTo(f);
							event.setCancelled(false);
							ignore.remove(p);
							return;
						}
						f.setX(f.getX() - (c * 2));
						if (!a.insideArea(f) && f.getBlock().getType() == Material.AIR) {
							event.setTo(f);
							event.setCancelled(false);
							ignore.remove(p);
							return;
						}
						f.setX(f.getX() + c);
						f.setZ(f.getZ() + c);
						if (!a.insideArea(f) && f.getBlock().getType() == Material.AIR) {
							event.setTo(f);
							event.setCancelled(false);
							ignore.remove(p);
							return;
						}
						f.setZ(f.getZ() - (c * 2));
						if (!a.insideArea(f) && f.getBlock().getType() == Material.AIR) {
							event.setTo(f);
							event.setCancelled(false);
							ignore.remove(p);
							return;
						}
						f.setZ(f.getZ() + c);
						f.setY(f.getY() + c);
						if (!a.insideArea(f) && f.getBlock().getType() == Material.AIR) {
							event.setTo(f);
							event.setCancelled(false);
							ignore.remove(p);
							return;
						}
						f.setY(f.getY() - (c * 2));
						if (!a.insideArea(f) && f.getBlock().getType() == Material.AIR) {
							event.setTo(f);
							event.setCancelled(false);
							ignore.remove(p);
							return;
						}
						c += 1;
					}
				}
			}
		}
	}

	private void checkPortal(PlayerPortalEvent event) {
		if (!BenCmd.getMainProperties().getBoolean("BenCmdPortals", true)) {
			return;
		}
		User user = User.getUser(event.getPlayer());
		Portal portal;
		Location loc = event.getPlayer().getLocation();
		if ((portal = BenCmd.getPortalFile().getPortalAt(loc)) == null) {
			BenCmd.getLocale().sendMessage(user, "misc.portal.noEnd");
			event.setCancelled(true);
			return;
		}
		if (portal.getGroup() != null && !user.inGroup(portal.getGroup())) {
			BenCmd.getLocale().sendMessage(user, "misc.portal.noPermission");
			event.setCancelled(true);
			return;
		}
		event.useTravelAgent(false);
		if (portal instanceof HomePortal) {
			Warp warp;
			if ((warp = ((HomePortal) portal).getWarp(user)) != null) {
				event.setTo(warp.loc);
			} else {
				event.setCancelled(true);
				BenCmd.getLocale().sendMessage(user, "misc.portal.noHome");
			}
		} else {
			event.setTo(portal.getWarp().loc);
		}
		BenCmd.getWarpCheckpoints().SetPreWarp(event.getPlayer());
	}

	private void flyTeleport(PlayerTeleportEvent event) {
		if (event.isCancelled()) {
			return;
		}
		BenCmd.getFlymodDetector().lastL.put(event.getPlayer(), event.getTo());
		if (BenCmd.isSpoutConnected()) {
			sendSkins(event.getPlayer(), event.getTo());
		}
	}

	private void flyPortal(PlayerPortalEvent event) {
		if (event.isCancelled()) {
			return;
		}
		BenCmd.getFlymodDetector().lastL.put(event.getPlayer(), event.getTo());
		if (BenCmd.isSpoutConnected()) {
			sendSkins(event.getPlayer(), event.getTo());
		}
	}

	private void loginCheck(PlayerLoginEvent event) {
		PermissionUser user;
		if (!BenCmd.getPermissionManager().getUserFile().userExists(event.getPlayer().getName())) {
			if (BenCmd.getMainProperties().getBoolean("disallowNewUsers", false)) {
				event.disallow(Result.KICK_WHITELIST, BenCmd.getLocale().getString("kick.whitelist"));
				return;
			} else {
				BenCmd.getPermissionManager().getUserFile().addUser(user = PermissionUser.newUser(event.getPlayer().getName(), new ArrayList<String>(), new ArrayList<String>()));
			}
		} else {
			user = PermissionUser.matchUserIgnoreCase(event.getPlayer().getName());
			if (!user.getName().equals(event.getPlayer().getName())) {
				BenCmd.getPermissionManager().getUserFile().correctCase(user, event.getPlayer().getName());
			}
		}
		if (BenCmd.getPermissionManager().getGroupFile().getAllUserGroups(user).isEmpty()) {
			BenCmd.getPermissionManager().getGroupFile().getGroup(BenCmd.getMainProperties().getString("defaultGroup", "default")).addUser(user);
		}
		if (user.isBanned() != null) {
			com.bendude56.bencmd.permissions.Action a = user.isBanned();
			if (a.getExpiry() == -1) {
				event.disallow(Result.KICK_BANNED, BenCmd.getLocale().getString("kick.banPerm"));
			} else {
				event.disallow(Result.KICK_BANNED, BenCmd.getLocale().getString("kick.banTemp", a.formatTimeLeft()));
			}
			return;
		}
		long timeLeft;
		if ((timeLeft = BenCmd.getPermissionManager().getKickTracker().isBlocked(event.getPlayer().getName())) > 0) {
			event.disallow(Result.KICK_OTHER, BenCmd.getLocale().getString("kick.kickTimeout", String.valueOf((int) Math.ceil(timeLeft / 60000.0))));
			return;
		}
		switch (BenCmd.getPermissionManager().getMaxPlayerHandler().join(User.getUser(event.getPlayer()))) {
			case NO_SLOT_NORMAL:
				event.disallow(Result.KICK_FULL, BenCmd.getLocale().getString("kick.full"));
				break;
			case NO_SLOT_RESERVED:
				event.disallow(Result.KICK_FULL, BenCmd.getLocale().getString("kick.full"));
				break;
		}
		User.finalizeUser(User.getUser(event.getPlayer()));
	}

	private void jailPickupCheck(PlayerPickupItemEvent event) {
		User user = User.getUser(event.getPlayer());
		if (user.isJailed() != null) {
			event.setCancelled(true);
		}
	}

	private void jailDropCheck(PlayerDropItemEvent event) {
		User user = User.getUser(event.getPlayer());
		if (user.isJailed() != null) {
			event.setCancelled(true);
		}
	}

	private void npcInteractEntity(PlayerInteractEntityEvent event) {
		if (event.getRightClicked() instanceof CraftPlayer) {
			if (((CraftPlayer) event.getRightClicked()).getHandle() instanceof EntityNPC) {
				NPC npc = BenCmd.getNPCFile().getNPC((EntityNPC) ((CraftPlayer) event.getRightClicked()).getHandle());
				if (npc == null) {
					return;
				}
				User user = User.getUser(event.getPlayer());
				if (event.getPlayer().getItemInHand().getType() == Material.STICK && user.hasPerm("bencmd.npc.info")) {
					npcInfo(user, npc);
					return;
				}
				if (npc instanceof Clickable) {
					((Clickable) npc).onRightClick(event.getPlayer());
				}
			}
		}
	}

	private void npcInfo(User u, NPC n) {
		if (BenCmd.isSpoutConnected() && BenCmd.getSpoutConnector().enabled(u.getPlayerHandle())) {
			BenCmd.getSpoutConnector().showNPCScreen(u.getPlayerHandle(), n);
		} else {
			String type;
			if (n instanceof BankerNPC) {
				type = BenCmd.getLocale().getString("command.npc.banker");
			} else if (n instanceof BankManagerNPC) {
				type = BenCmd.getLocale().getString("command.npc.bankManager");
			} else if (n instanceof BlacksmithNPC) {
				type = BenCmd.getLocale().getString("command.npc.blacksmith");
			} else if (n instanceof StaticNPC) {
				type = BenCmd.getLocale().getString("command.npc.static");
			} else {
				type = "";
			}			
			BenCmd.getLocale().sendMultilineMessage(u, "misc.npc.info", n.getID() + "", type, n.getName(), n.getSkinURL());
		}
	}

	private void logWand(PlayerInteractEvent event) {
		if (!event.isCancelled() && event.getItem() != null && event.getItem().getType() == Material.STICK && BenCmd.getRecordingFile().wandEnabled(event.getPlayer().getName())) {
			Bukkit.dispatchCommand(event.getPlayer(), "log block");
			event.setCancelled(true);
		}
	}

	private void chestOpenLog(PlayerInteractEvent event) {
		if (!event.isCancelled() && event.getClickedBlock().getType() == Material.CHEST && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			ChestOpenEntry e = new ChestOpenEntry(event.getPlayer().getName(), event.getClickedBlock().getLocation(), new Date().getTime());
			BenCmd.getRecordingFile().logEvent(e);
		}
	}
	
	private void logBucket(PlayerBucketEmptyEvent event) {
		if (!event.isCancelled()) {
			if (event.getBucket() == Material.LAVA_BUCKET) {
				BlockPlaceEntry e = new BlockPlaceEntry(event.getPlayer().getName(), event.getBlockClicked().getLocation(), new Date().getTime(), Material.LAVA);
				BenCmd.getRecordingFile().logEvent(e);
			} else if (event.getBucket() == Material.WATER_BUCKET) {
				BlockPlaceEntry e = new BlockPlaceEntry(event.getPlayer().getName(), event.getBlockClicked().getLocation(), new Date().getTime(), Material.WATER);
				BenCmd.getRecordingFile().logEvent(e);
			}
		}
	}

	@Override
	public void execute(Listener listener, Event event) throws EventException {
		if (event instanceof PlayerChatEvent) {
			PlayerChatEvent e = (PlayerChatEvent) event;
			chat(e);
		} else if (event instanceof PlayerJoinEvent) {
			PlayerJoinEvent e = (PlayerJoinEvent) event;
			userInit(e);
		} else if (event instanceof PlayerQuitEvent) {
			PlayerQuitEvent e = (PlayerQuitEvent) event;
			quitFinalize(e);
		} else if (event instanceof PlayerKickEvent) {
			PlayerKickEvent e = (PlayerKickEvent) event;
			kickFinalize(e);
		} else if (event instanceof PlayerInteractEvent) {
			PlayerInteractEvent e = (PlayerInteractEvent) event;
			logWand(e);
			if (e.isCancelled()) {
				return;
			}
			bookshelfInteract(e);
			spawnerEggInteract(e);
			lockInteract(e);
			disposalChestInteract(e);
			strikeBind(e);
			lotSelectInteract(e);
			chestOpenLog(e);
		} else if (event instanceof PlayerBucketEmptyEvent) {
			PlayerBucketEmptyEvent e = (PlayerBucketEmptyEvent) event;
			lotBucketEmpty(e);
			jailBucketEmpty(e);
			logBucket(e);
		} else if (event instanceof PlayerBucketFillEvent) {
			PlayerBucketFillEvent e = (PlayerBucketFillEvent) event;
			lotBucketFill(e);
			jailBucketFill(e);
		} else if (event instanceof PlayerRespawnEvent) {
			PlayerRespawnEvent e = (PlayerRespawnEvent) event;
			pvpRespawn(e);
		} else if (event instanceof PlayerPortalEvent) {
			PlayerPortalEvent e = (PlayerPortalEvent) event;
			checkPortal(e);
			flyPortal(e);
		} else if (event instanceof PlayerTeleportEvent) {
			PlayerTeleportEvent e = (PlayerTeleportEvent) event;
			sendSkins(e.getPlayer(), e.getTo());
			flyTeleport(e);
		} else if (event instanceof PlayerMoveEvent) {
			PlayerMoveEvent e = (PlayerMoveEvent) event;
			sendSkins(e.getPlayer(), e.getTo());
			areaMoveCheck(e);
			BenCmd.getMonitorController().playerMove(e.getPlayer());
		} else if (event instanceof PlayerLoginEvent) {
			PlayerLoginEvent e = (PlayerLoginEvent) event;
			loginCheck(e);
		} else if (event instanceof PlayerPickupItemEvent) {
			PlayerPickupItemEvent e = (PlayerPickupItemEvent) event;
			jailPickupCheck(e);
		} else if (event instanceof PlayerDropItemEvent) {
			PlayerDropItemEvent e = (PlayerDropItemEvent) event;
			jailDropCheck(e);
		} else if (event instanceof PlayerInteractEntityEvent) {
			PlayerInteractEntityEvent e = (PlayerInteractEntityEvent) event;
			npcInteractEntity(e);
		}
	}

}
