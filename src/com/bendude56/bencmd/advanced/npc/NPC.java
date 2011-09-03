package com.bendude56.bencmd.advanced.npc;

import net.minecraft.server.Entity;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.ItemInWorldManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Vec3D;
import net.minecraft.server.WorldServer;

import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.bendude56.bencmd.BenCmd;


public class NPC {
	protected EntityNPC enpc;
	private int id;
	private Location l;
	protected BenCmd plugin;
	private String n;
	private ItemStack itemHeld;
	protected boolean faceNearestPlayer;
	
	public NPC(BenCmd instance, String name, int id, Location l, ItemStack itemHeld) {
		this(instance, name, id, l, itemHeld, true);
	}

	public NPC(BenCmd instance, String name, int id, Location l, ItemStack itemHeld, boolean facePlayer) {
		plugin = instance;
		this.id = id;
		this.l = l;
		this.n = name;
		this.itemHeld = itemHeld;
		this.faceNearestPlayer = facePlayer;
		if (l.getWorld().isChunkLoaded(l.getBlock().getChunk())) {
			spawn();
		}
	}
	
	public boolean isSpawned() {
		return enpc != null;
	}
	
	public int getEntityId() {
		if (isSpawned()) {
			return enpc.id;
		} else {
			return -1;
		}
	}

	public void spawn() {
		if (enpc == null) {
			WorldServer ws = ((CraftWorld) l.getWorld()).getHandle();
			MinecraftServer ms = ((CraftServer) ws.getServer()).getServer();
			enpc = new EntityNPC(ms, ws, n, new ItemInWorldManager(ws));
			enpc.setLocation(l.getX(), l.getY(), l.getZ(), l.getYaw(),
					l.getPitch());
			if (itemHeld.getTypeId() != 0) {
				enpc.inventory.setItem(0, new net.minecraft.server.ItemStack(itemHeld.getTypeId(), itemHeld.getAmount(), itemHeld.getDurability()));
				enpc.inventory.itemInHandIndex = 0;
			}
			ws.addEntity(enpc);
			ws.players.remove(enpc);
			if (plugin.spoutcraft) {
				for (Player p : plugin.getServer().getOnlinePlayers()) {
					plugin.spoutconnect.sendSkin(p, enpc.id, getSkinURL());
				}
			}
		}
	}

	public void despawn() {
		if (enpc != null) {
			if (plugin.spoutcraft) {
				for (Player p : plugin.getServer().getOnlinePlayers()) {
					plugin.spoutconnect.sendSkin(p, enpc.id, "http://s3.amazonaws.com/MinecraftSkins/" + enpc.name + ".png");
				}
			}
			WorldServer ws = ((CraftWorld) l.getWorld()).getHandle();
			enpc.die();
			ws.removeEntity(enpc);
			enpc = null;
		}
	}
	
	public void faceNearest() {
		EntityHuman h;
		if ((h = getClosestPlayer(5.0D)) != null) {
			NPC.faceEntity(enpc, h);
		} else if (this.getLocation().getYaw() != this.getCurrentLocation().getYaw()) {
			enpc.setLocation(enpc.locX, enpc.locY, enpc.locZ, this.getLocation().getYaw(), enpc.pitch);
		}
	}
	
	protected EntityHuman getClosestPlayer(double range) {
		if (range <= 0.0D) {
			return null;
		}
		EntityHuman entityhuman = enpc.world.findNearbyPlayer(enpc, range);
		return entityhuman != null && enpc.e(entityhuman) ? entityhuman : null;
	}
	
	public void tick() {
		if (faceNearestPlayer) {
			this.faceNearest();
		}
	}

	public String getValue() {
		throw new UnsupportedOperationException("getValue() not overridden!");
	}
	
	public String getSkinURL() {
		throw new UnsupportedOperationException("No skin URL provided!");
	}

	public int getID() {
		return id;
	}

	public String getName() {
		return enpc.name;
	}
	
	public ItemStack getHeldItem() {
		return itemHeld;
	}
	
	public void setHeldItem(ItemStack item) {
		despawn();
		itemHeld = item;
		spawn();
		plugin.npcs.saveNPC(this);
	}
	
	public void setName(String name) {
		despawn();
		n = name;
		spawn();
		plugin.npcs.saveNPC(this);
	}

	public Location getCurrentLocation() {
		return new Location(enpc.world.getWorld(), enpc.locX, enpc.locY,
				enpc.locZ, enpc.yaw, enpc.pitch);
	}

	public Location getLocation() {
		return l;
	}
	
	public static void faceEntity(EntityNPC enpc, Entity e) {
		try {
			faceLocation(enpc, Vec3D.a(e.locX, e.locY, e.locZ));
		} catch (Exception ex) { }
	}
	
	public static void faceLocation(EntityNPC enpc, Vec3D loc2) {
		Location loc = enpc.getBukkitEntity().getLocation();
		double xDiff = loc2.a - loc.getX();
		double yDiff = loc2.b - loc.getY();
		double zDiff = loc2.c - loc.getZ();
		double DistanceXZ = Math.sqrt(xDiff * xDiff + zDiff * zDiff);
		double DistanceY = Math.sqrt(DistanceXZ * DistanceXZ + yDiff * yDiff);
		double yaw = (Math.acos(xDiff / DistanceXZ) * 180 / Math.PI);
		double pitch = (Math.acos(yDiff / DistanceY) * 180 / Math.PI) - 90;
		if (zDiff < 0.0) {
			yaw = yaw + (Math.abs(180 - yaw) * 2);
		}
		enpc.setLocation(loc.getX(), loc.getY(), loc.getZ(), (float) yaw - 90,
		(float) pitch);
	}
}
