package com.bendude56.bencmd.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.event.screen.ScreenListener;
import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.SpoutConnector.NPCScreen;
import com.bendude56.bencmd.SpoutConnector.StatusScreen;
import com.bendude56.bencmd.advanced.npc.Skinnable;

public class BenCmdScreenListener extends ScreenListener {

	// Singleton instancing

	private static BenCmdScreenListener	instance	= null;

	public static BenCmdScreenListener getInstance() {
		if (instance == null) {
			return instance = new BenCmdScreenListener();
		} else {
			return instance;
		}
	}

	public static void destroyInstance() {
		instance.enabled = false;
		instance = null;
	}
	
	private boolean enabled = true;

	private BenCmdScreenListener() {
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvent(Event.Type.CUSTOM_EVENT, this, Event.Priority.Normal, BenCmd.getPlugin());
	}

	private void buttonNPC(ButtonClickEvent event) {
		if (event.getButton().getScreen() instanceof NPCScreen) {
			NPCScreen s = (NPCScreen) event.getButton().getScreen();
			if (event.getButton().equals(s.ok) && s.ok.isEnabled()) {
				// Save all changes, then close
				if (s.npc instanceof Skinnable) {
					s.npc.setName(s.name.getText());
					((Skinnable) s.npc).setSkin(s.skin.getText());
					s.npc.setHeldItem(new ItemStack(s.item.getTypeId(), s.item.getData()));
				}
				s.close();
			} else if (event.getButton().equals(s.apply) && s.apply.isEnabled()) {
				// Save all changes, but stay open
				if (s.npc instanceof Skinnable) {
					s.npc.setName(s.name.getText());
					((Skinnable) s.npc).setSkin(s.skin.getText());
					s.npc.setHeldItem(new ItemStack(s.item.getTypeId(), s.item.getData()));
				}
			} else if (event.getButton().equals(s.iup) && s.iup.isEnabled()) {
				BenCmd.getSpoutConnector().nextItem(s);
			} else if (event.getButton().equals(s.idown) && s.idown.isEnabled()) {
				BenCmd.getSpoutConnector().prevItem(s);
			} else if (event.getButton().equals(s.cancel) && s.cancel.isEnabled()) {
				// Don't save any changes
				s.close();
			}
		}
	}

	private void buttonStatus(ButtonClickEvent event) {
		if (event.getButton().getScreen() instanceof StatusScreen) {
			StatusScreen s = (StatusScreen) event.getButton().getScreen();
			if (event.getButton().equals(s.close)) {
				s.close();
			}
		}
	}

	// Split-off events

	public void onButtonClick(ButtonClickEvent event) {
		if (!enabled) {
			return;
		}
		buttonNPC(event);
		buttonStatus(event);
	}
}
