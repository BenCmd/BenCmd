package ben_dude56.plugins.bencmd.invtools;

import org.bukkit.Material;

public class BCItem {
	private Material mat;
	private int damageValue;
	private boolean damageInit;

	public BCItem(Material material) {
		mat = material;
		damageValue = 0;
		damageInit = false;
	}

	public BCItem(Material material, int Damage) {
		mat = material;
		damageValue = Damage;
		damageInit = true;
	}

	public boolean damageSet() {
		return damageInit;
	}

	public Material getMaterial() {
		return mat;
	}

	public int getDamage() {
		return damageValue;
	}

	public void setMaterial(Material material) {
		mat = material;
	}

	public void setDamage(int damage) {
		damageValue = damage;
		damageInit = true;
	}
}
