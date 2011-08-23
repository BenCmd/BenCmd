package ben_dude56.plugins.bencmd.advanced.npc;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import ben_dude56.plugins.bencmd.BenCmd;

public class SkinnableNPC extends NPC {

	private String skin = "";
	
	public SkinnableNPC(BenCmd instance, String name, String skin, int id, Location l, ItemStack heldItem) {
		super(instance, name, id, l, heldItem);
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
	
	public String getValue() {
		Location l = super.getLocation();
		return "n|" + l.getWorld().getName() + "," + l.getX() + "," + l.getY()
		+ "," + l.getZ() + "," + l.getYaw() + "," + l.getPitch() + "|" + skin + "|" + super.getName() + "|" + getHeldItem().getTypeId() + ":" + getHeldItem().getDurability();
	}
}
