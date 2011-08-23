package ben_dude56.plugins.bencmd.advanced.npc;

import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.event.screen.ScreenListener;

import ben_dude56.plugins.bencmd.SpoutConnector;
import ben_dude56.plugins.bencmd.SpoutConnector.NPCScreen;

public class NPCScreenListener extends ScreenListener {

	@Override
	public void onButtonClick(ButtonClickEvent event) {
		if (event.getButton().getScreen() instanceof SpoutConnector.NPCScreen) {
			NPCScreen s = (SpoutConnector.NPCScreen)event.getButton().getScreen();
			if (event.getButton().equals(s.ok) && s.ok.isEnabled()) {
				// Save all changes, then close
				if (s.npc instanceof SkinnableNPC) {
					s.npc.setName(s.name.getText());
					((SkinnableNPC)s.npc).setSkin(s.skin.getText());
				}
				s.close();
			} else if (event.getButton().equals(s.apply) && s.apply.isEnabled()){
				// Save all changes, but stay open
				if (s.npc instanceof SkinnableNPC) {
					s.npc.setName(s.name.getText());
					((SkinnableNPC)s.npc).setSkin(s.skin.getText());
				}
				
			} else if (event.getButton().equals(s.cancel) && s.cancel.isEnabled()) {
				// Don't save any changes
				s.close();
			}
		}
	}

}
