package ben_dude56.plugins.bencmd.advanced.npc;

import net.minecraft.server.ItemInWorldManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldServer;

import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import ben_dude56.plugins.bencmd.BenCmd;

public class NPC {
	protected EntityNPC enpc;
	private int id;
	private Location l;
	protected BenCmd plugin;
	private String n;
	private ItemStack itemHeld;

	public NPC(BenCmd instance, String name, int id, Location l, ItemStack itemHeld) {
		plugin = instance;
		this.id = id;
		this.l = l;
		this.n = name;
		this.itemHeld = itemHeld;
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
}
