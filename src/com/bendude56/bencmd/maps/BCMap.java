package com.bendude56.bencmd.maps;

import net.minecraft.server.World;
import net.minecraft.server.WorldMap;

public class BCMap {
	WorldMap	map;
	short		mapID;

	public BCMap(short mapID, World world) {
		map = (WorldMap) world.a(WorldMap.class, "map_" + mapID);
		this.mapID = mapID;
	}

	public void zoomIn() {
		if (map.scale != 0) {
			map.scale -= 1;
		}
		for (int i = 0; i < map.colors.length; i++) {
			map.colors[i] = 0x00;
		}
	}

	public void zoomOut() {
		if (map.scale != 4) {
			map.scale += 1;
		}
		for (int i = 0; i < map.colors.length; i++) {
			map.colors[i] = 0x00;
		}
	}

	public void setCenter(int x, int z) {
		map.centerX = x;
		map.centerZ = z;
	}
}
