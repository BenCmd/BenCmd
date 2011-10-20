package com.bendude56.bencmd.advanced.npc;

import java.util.List;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.MathHelper;
import net.minecraft.server.PathEntity;
import net.minecraft.server.Packet;
import net.minecraft.server.Vec3D;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.bendude56.bencmd.BenCmd;

public class PathableNPC extends NPC {
	// TODO Complete this class

	private PathEntity path = null;
	private int respawnTime = -2;
	private int maxRespawnTime = -1;
	private List<Location> patrol;
	private int cPatrol;
	
	public PathableNPC(String name, int id, Location l,
			ItemStack itemHeld, boolean facePlayer, int maxRespawnTime) {
		this(name, id, l, itemHeld, facePlayer, maxRespawnTime, null);
	}

	public PathableNPC(String name, int id, Location l,
			ItemStack itemHeld, boolean facePlayer, int maxRespawnTime, List<Location> patrol) {
		super(name, id, l, itemHeld, facePlayer);
		this.maxRespawnTime = maxRespawnTime;
		this.patrol = patrol;
		patrol.add(0, getLocation());
		cPatrol = 0;
	}
	
	public void setPatrol(List<Location> patrol) {
		patrol.add(0, getLocation());
		this.patrol = patrol;
		cPatrol = 0;
		enpc.setPositionRotation(getLocation().getX(), getLocation().getY(), getLocation().getZ(), getLocation().getYaw(), getLocation().getPitch());
	}

	public void setPath(PathEntity path) {
		this.path = path;
		cPatrol = 0;
	}
	
	public void moveTo(Location loc) {
		if (loc == null)
			return;
		else
			moveTo(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
	}
	
	public void moveTo(int x, int y, int z) {
		this.path = enpc.world.a(enpc, x, y, z, 16.0F);
		cPatrol = 0;
	}

	public boolean inLiquid() {
		return enpc.fireTicks > 0 || enpc.airTicks < 20;
	}

	public void jump() {
		jump(inLiquid());
	}

	private void jump(boolean inLiquid) {
		if (inLiquid) {
			enpc.motY += 0.03999999910593033D;
		} else if (enpc.onGround) {
			enpc.motY = 0.41999998688697815D + 0.05D;
		}
	}

	public boolean hasBow() {
		return enpc.inventory.getItem(enpc.inventory.itemInHandIndex).id == 261;
	}
	
	public boolean respawnTicks() {
		if (enpc == null || enpc.dead) {
			if (enpc != null) {
				despawn();
			}
			if (maxRespawnTime == -1) {
				BenCmd.getNPCFile().remNPC(this);
				return true;
			}
			if (respawnTime == -2) {
				respawnTime = maxRespawnTime;
			}
			if (respawnTime == 0) {
				spawn();
				respawnTime = -2;
			} else {
				respawnTime -= 1;
			}
			return true;
		}
		return false;
	}

	public void tick() {
		if (respawnTicks())
			return;
		move();
		enpc.noDamageTicks -= 1;
		enpc.attackTicks -= 1;
	}

	private void move() {
		EntityHuman h;
		if ((h = getClosestPlayer(10.0D)) != null) {
			NPC.faceEntity(enpc, h);
		} else if (this.getLocation().getYaw() != this.getCurrentLocation()
				.getYaw()) {
			enpc.setLocation(enpc.locX, enpc.locY, enpc.locZ, this
					.getLocation().getYaw(), enpc.pitch);
		}
		Vec3D to = getNextPathPos();
		if (to != null) {
			faceLocation(enpc, to);
			possibleJump(to);
			enpc.forward();
		} else if (patrol != null) {
			cPatrol++;
			if (cPatrol >= patrol.size()) {
				cPatrol = 0;
			}
			Location l = patrol.get(cPatrol);
			path = enpc.world.a(enpc, l.getBlockX(), l.getBlockY(), l.getBlockZ(), 16);
			move();
		} else if (faceNearestPlayer) {
			this.faceNearest();
		}
	}

	private Vec3D getNextPathPos() {
		Vec3D vec3d = path.a(enpc);
		double length = (enpc.length * 1.82F);
		while (vec3d != null
				&& vec3d.d(enpc.locX, vec3d.b, enpc.locZ) < length * length) {
			this.path.a();
			if (this.path.b()) {
				vec3d = null;
			} else {
				vec3d = path.a(enpc);
			}
		}
		return vec3d;
	}

	private void possibleJump(Vec3D target) {
		int height = MathHelper.floor(enpc.boundingBox.b + 0.5D);
		if (enpc.getRandom().nextFloat() < 0.75D && inLiquid()) {
			jump();
		} else if (target.b - height > 0.0D) {
			jump();
		}
	}

	public static void sendPacketToPlayer(final Player ply, final Packet packet) {
		((CraftPlayer) ply).getHandle().netServerHandler.sendPacket(packet);
	}

	public static void sendPacketNearby(final Location location,
			final double radius, final Packet packet) {
		sendPacketNearby(location, radius, packet, null);
	}

	public static void sendPacketNearby(final Location location, double radius,
			final Packet packet, final Player except) {
		radius *= radius;
		final World world = location.getWorld();
		for (Player ply : Bukkit.getServer().getOnlinePlayers()) {
			if (ply.equals(except)) {
				continue;
			}
			if (world != ply.getWorld()) {
				continue;
			}
			if (location.distanceSquared(ply.getLocation()) > radius) {
				continue;
			}
			sendPacketToPlayer(ply, packet);
		}
	}
}
