package com.bendude56.bencmd.advanced.npc;

import java.lang.reflect.Field;
import java.net.Socket;

import net.minecraft.server.NetHandler;
import net.minecraft.server.NetworkManager;
import net.minecraft.server.Packet;

public class NPCNetworkManager extends NetworkManager {
	public NPCNetworkManager(Socket paramSocket, String paramString, NetHandler netHandler) {
		super(paramSocket, paramString, netHandler);
		try {
			// the field above the 3 synchronized lists.
			Field f = NetworkManager.class.getDeclaredField("l");
			f.setAccessible(true);
			f.set(this, false);
		} catch (Exception e) {}
	}

	@Override
	public void a(NetHandler nethandler) {}

	@Override
	public void queue(Packet packet) {}

	@Override
	public void a(String s, Object... aobject) {}

	@Override
	public void a() {}

	@Override
	public void b() {}

	@Override
	public int e() {
		return 0;
	}
}
