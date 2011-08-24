package ben_dude56.plugins.bencmd.advanced.npc;

import org.bukkit.entity.Entity;

public interface Damageable {
	public void onDamage(Entity e, int amount);
	public void onDeath();
}
