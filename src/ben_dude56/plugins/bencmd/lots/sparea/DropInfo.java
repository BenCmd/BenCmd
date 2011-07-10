package ben_dude56.plugins.bencmd.lots.sparea;

import java.util.Random;

public class DropInfo {
	private int c;
	private int min;
	private int max;

	public DropInfo(int chance, int minAmt, int maxAmt) {
		if (c == 0) { // Cannot have a change of 1/0!!
			c = 1;
		}
		c = chance;
		if (min > max) { // Minimum cannot be higher than maximum!!
			int m = min;
			min = max;
			max = m;
		}
		min = minAmt;
		max = maxAmt;
	}

	public int getChance() {
		return c;
	}

	public int getMin() {
		return min;
	}

	public int getMax() {
		return max;
	}

	public int getAmount(Random r) {
		if (c == 1 || r.nextInt(c - 1) == 0) {
			if (min == max) {
				return min;
			} else {
				return min + r.nextInt(max - min);
			}
		} else {
			return 0;
		}
	}
}
