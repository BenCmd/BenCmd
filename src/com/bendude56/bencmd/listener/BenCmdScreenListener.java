package com.bendude56.bencmd.listener;

import org.bukkit.event.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.*;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.SpoutConnector.NPCScreen;
import com.bendude56.bencmd.SpoutConnector.StatusScreen;
import com.bendude56.bencmd.advanced.npc.Skinnable;

public class BenCmdScreenListener implements Listener, EventExecutor {

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
		instance = null;
	}

	private BenCmdScreenListener() {
		ButtonClickEvent.getHandlerList().register(new RegisteredListener(this, this, EventPriority.NORMAL, BenCmd.getPlugin()));
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
		buttonNPC(event);
		buttonStatus(event);
	}

	@Override
	public void execute(Listener listener, Event event) throws EventException {
		if (event instanceof ButtonClickEvent) {
			ButtonClickEvent e = (ButtonClickEvent) event;
			buttonNPC(e);
			buttonStatus(e);
		}
		
	}
}
