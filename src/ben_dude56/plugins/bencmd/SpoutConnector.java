package ben_dude56.plugins.bencmd;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.player.SpoutPlayer;
import org.getspout.spoutapi.gui.*;

import ben_dude56.plugins.bencmd.advanced.npc.NPC;
import ben_dude56.plugins.bencmd.advanced.npc.SkinnableNPC;

public class SpoutConnector {

	public void sendSkin(Player p, int id, String skin) {
		org.getspout.spoutapi.player.SpoutPlayer player = ((org.getspout.spoutapi.player.SpoutPlayer) p);
		if (player.isSpoutCraftEnabled()) {
			player.sendPacket(new org.getspout.spoutapi.packet.PacketSkinURL(id, skin));
		}
	}
	
	public boolean enabled(Player p) {
		return ((SpoutPlayer)p).isSpoutCraftEnabled();
	}
	
	public void sendNotification(Player p, String title, String message, Material m) {
		if (enabled(p)) {
			((SpoutPlayer)p).sendNotification(title, message, m);
		}
	}
	
	public void showNPCScreen(Player p, NPC n) {
		User u = User.getUser(BenCmd.getPlugin(), p);
		NPCScreen infoscr = new NPCScreen();
		InGameHUD mainscr = ((SpoutPlayer) p).getMainScreen();
		infoscr.npc = n;
		
		// idlabel
		GenericLabel idlabel = new GenericLabel();
		idlabel.setText("NPC ID:     " + n.getID()).setX(10).setY(20).setWidth(40).setHeight(12);
		infoscr.attachWidget(BenCmd.getPlugin(), idlabel);
		
		// namefield
		GenericTextField namefield = new GenericTextField();
		namefield.setText(n.getName()).setMaximumCharacters(20).setEnabled(n instanceof SkinnableNPC && u.hasPerm("bencmd.npc.create")).setX(65).setY(40).setWidth(200).setHeight(12);
		infoscr.attachWidget(BenCmd.getPlugin(), namefield);
		infoscr.name = namefield;
		
		// namelabel
		GenericLabel namelabel = new GenericLabel();
		namelabel.setText("NPC Name:").setX(10).setY(40).setWidth(40).setHeight(12);
		infoscr.attachWidget(BenCmd.getPlugin(), namelabel);
		
		// skinfield
		GenericTextField skinfield = new GenericTextField();
		skinfield.setText(n.getSkinURL()).setMaximumCharacters(500).setEnabled(n instanceof SkinnableNPC && u.hasPerm("bencmd.npc.create")).setX(65).setY(60).setWidth(350).setHeight(12);
		infoscr.attachWidget(BenCmd.getPlugin(), skinfield);
		infoscr.skin = skinfield;
		
		// skinlabel
		GenericLabel skinlabel = new GenericLabel();
		skinlabel.setText("NPC Skin:").setX(10).setY(60).setWidth(40).setHeight(12);
		infoscr.attachWidget(BenCmd.getPlugin(), skinlabel);
		
		// applybutton
		GenericButton applybutton = new GenericButton();
		applybutton.setEnabled((n instanceof SkinnableNPC) && u.hasPerm("bencmd.npc.create"));
		applybutton.setText("Apply").setY(210).setX(325).setWidth(40).setHeight(20);
		infoscr.attachWidget(BenCmd.getPlugin(), applybutton);
		infoscr.apply = applybutton;
		
		// okbutton
		GenericButton okbutton = new GenericButton();
		okbutton.setEnabled((n instanceof SkinnableNPC) && u.hasPerm("bencmd.npc.create"));
		okbutton.setText("OK").setY(210).setX(275).setWidth(40).setHeight(20);
		infoscr.attachWidget(BenCmd.getPlugin(), okbutton);
		infoscr.ok = okbutton;
		
		// cancelbutton
		GenericButton cancelbutton = new GenericButton();
		cancelbutton.setText("Cancel").setY(210).setX(375).setWidth(40).setHeight(20);
		infoscr.attachWidget(BenCmd.getPlugin(), cancelbutton);
		infoscr.cancel = cancelbutton;
		
		// Show pop-up
		mainscr.attachPopupScreen(infoscr);
	}
	
	public class NPCScreen extends GenericPopup {
		public NPC npc;
		public TextField name;
		public TextField skin;
		public Button apply;
		public Button ok;
		public Button cancel;
	}
}
