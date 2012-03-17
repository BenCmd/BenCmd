package com.bendude56.bencmd.advanced;

import org.bukkit.craftbukkit.entity.CraftPlayer;

import net.minecraft.server.ContainerPlayer;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.PlayerInventory;

public class ViewableInventory extends PlayerInventory {

	public static void replInv(CraftPlayer player) {
		EntityPlayer entityplayer = player.getHandle();
		entityplayer.inventory = new ViewableInventory(entityplayer.inventory);
		entityplayer.defaultContainer = new ContainerPlayer(entityplayer.inventory, !entityplayer.world.isStatic);
		entityplayer.activeContainer = entityplayer.defaultContainer;
		player.setHandle(entityplayer);
	}

	public ViewableInventory(PlayerInventory inv) {
		super(inv.player);
		this.armor = inv.armor;
		this.items = inv.items;
		this.itemInHandIndex = inv.itemInHandIndex;
		this.e = inv.e;
		this.setCarried(inv.getCarried());
		inv.player.defaultContainer = new ContainerPlayer(this, !inv.player.world.isStatic);
		inv.player.activeContainer = inv.player.defaultContainer;
	}

	public String getName() {
		return ((EntityPlayer) this.player).name;
	}

	public boolean a(EntityHuman entityHuman) {
		return true;
	}
}
