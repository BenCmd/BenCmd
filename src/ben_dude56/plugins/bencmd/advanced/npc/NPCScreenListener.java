package ben_dude56.plugins.bencmd.advanced.npc;

import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.event.screen.ScreenListener;

import ben_dude56.plugins.bencmd.BenCmd;
import ben_dude56.plugins.bencmd.SpoutConnector;
import ben_dude56.plugins.bencmd.SpoutConnector.NPCScreen;

public class NPCScreenListener extends ScreenListener {

	@Override
	public void onButtonClick(ButtonClickEvent event) {
		if (event.getButton().getScreen() instanceof SpoutConnector.NPCScreen) {
			NPCScreen s = (SpoutConnector.NPCScreen)event.getButton().getScreen();
			if (event.getButton().equals(s.ok) && s.ok.isEnabled()) {
				// Save all changes, then close
				if (s.npc instanceof Skinnable) {
					s.npc.setName(s.name.getText());
					((Skinnable)s.npc).setSkin(s.skin.getText());
					s.npc.setHeldItem(new ItemStack(s.item.getTypeId(), s.item.getData()));
				}
				s.close();
			} else if (event.getButton().equals(s.apply) && s.apply.isEnabled()){
				// Save all changes, but stay open
				if (s.npc instanceof Skinnable) {
					s.npc.setName(s.name.getText());
					((Skinnable)s.npc).setSkin(s.skin.getText());
					s.npc.setHeldItem(new ItemStack(s.item.getTypeId(), s.item.getData()));
				}
			} else if (event.getButton().equals(s.iup) && s.iup.isEnabled()) {
				BenCmd.getPlugin().spoutconnect.nextItem(s);
			} else if (event.getButton().equals(s.idown) && s.idown.isEnabled()) {
				BenCmd.getPlugin().spoutconnect.prevItem(s);
			} else if (event.getButton().equals(s.cancel) && s.cancel.isEnabled()) {
				// Don't save any changes
				s.close();
			}
		}
	}

}
