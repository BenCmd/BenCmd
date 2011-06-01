package ben_dude56.plugins.bencmd.maps;

import net.minecraft.server.World;
import net.minecraft.server.WorldMap;

public class BCMap {
	WorldMap map;
	short mapID;
	public BCMap(short mapID, World world) {
		map = (WorldMap) world.a(WorldMap.class, "map_" + mapID);
		this.mapID = mapID;
	}
	
	public void zoomIn() {
		if(map.e != 0) {
			map.e -= 1;
		}
		for(int i = 0; i < map.f.length; i++) {
			map.f[i] = 0x00;
		}
	}
	
	public void zoomOut() {
		if(map.e != 4) {
			map.e += 1;
		}
		for(int i = 0; i < map.f.length; i++) {
			map.f[i] = 0x00;
		}
	}
	
	public void setCenter(int x, int z) {
		map.b = x;
		map.c = z;
	}
}
