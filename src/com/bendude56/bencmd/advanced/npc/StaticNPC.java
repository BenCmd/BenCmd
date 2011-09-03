package com.bendude56.bencmd.advanced.npc;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import com.bendude56.bencmd.BenCmd;


public class StaticNPC extends NPC implements Skinnable {

	private String skin = "";
	
	public StaticNPC(BenCmd instance, String name, String skin, int id, Location l, ItemStack heldItem, boolean facePlayer) {
		super(instance, name, id, l, heldItem, facePlayer);
		this.skin = skin;
		despawn();
		spawn();
	}
	
	public void setSkin(String skin) {
		this.skin = skin;
		despawn();
		spawn();
		plugin.npcs.saveNPC(this);
	}
	
	public String getSkin() {
		return skin;
	}
	
	public String getSkinURL() {
		if (skin == null) {
			return "";
		}
		if (!skin.contains("/")) {
			return "http://s3.amazonaws.com/MinecraftSkins/" + skin + ".png";
		} else {
			return skin;
		}
	}
	
	public void tick() {
		this.faceNearest();
	}
	
	public String getValue() {
		Location l = super.getLocation();
		return "n|" + l.getWorld().getName() + "," + l.getX() + "," + l.getY()
		+ "," + l.getZ() + "," + l.getYaw() + "," + l.getPitch() + "|" + skin + "|" + super.getName() + "|" + getHeldItem().getTypeId() + ":" + getHeldItem().getDurability();
	}
}
