package com.bendude56.bencmd.advanced.npc;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.ItemInWorldManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.NetHandler;
import net.minecraft.server.NetworkManager;
import net.minecraft.server.World;

public class EntityNPC extends EntityPlayer {
	private long	lastBounceTick;
	private int		lastBounceId;
	boolean			said	= false;

	public EntityNPC(MinecraftServer minecraftserver, World world, String s, ItemInWorldManager iteminworldmanager) {
		super(minecraftserver, world, s, iteminworldmanager);
		NetworkManager netMgr = new NPCNetworkManager(new NPCSocket(), "npc mgr", new NetHandler() {
			@Override
			public boolean c() {
				return false;
			}
		});
		this.netServerHandler = new NPCNetHandler(minecraftserver, this, netMgr);
		this.lastBounceId = -1;
		this.lastBounceTick = 0;
	}

	@Override
	public void a_(EntityHuman entity) {
		if (lastBounceId != entity.id || System.currentTimeMillis() - lastBounceTick > 1000) {
			lastBounceTick = System.currentTimeMillis();
		}
		lastBounceId = entity.id;
		super.a_(entity);
	}

	public void applyGravity() {
		if (chunkLoaded() && (!this.onGround || ((Player) this.getBukkitEntity()).getEyeLocation().getY() % 1 <= 0.62)) {
			float yaw = this.yaw;
			float pitch = this.pitch;
			this.a(0, 0);
			this.yaw = yaw;
			this.pitch = pitch;
		}
	}

	public float checkYawDiff(float diff) {
		for (this.aB = this.aJ; diff < -180.0F; diff += 360.0F) {}
		while (diff >= 180.0F) {
			diff -= 360.0F;
		}
		return diff;
	}

	public void forward() {
		this.a(this.aA, this.aB);
	}

	public Random getRandom() {
		return random;
	}

	private boolean chunkLoaded() {
		return this.bukkitEntity.getWorld().isChunkLoaded(this.F.x, this.F.z);
	}

	@Override
	public CraftPlayer getBukkitEntity() {
		return new CraftNPC((CraftServer) Bukkit.getServer(), this);
	}
	
	
}
