package com.bendude56.bencmd;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.gui.*;

import com.bendude56.bencmd.advanced.npc.BankManagerNPC;
import com.bendude56.bencmd.advanced.npc.BankerNPC;
import com.bendude56.bencmd.advanced.npc.BlacksmithNPC;
import com.bendude56.bencmd.advanced.npc.NPC;
import com.bendude56.bencmd.advanced.npc.Skinnable;
import com.bendude56.bencmd.advanced.npc.StaticNPC;
import com.bendude56.bencmd.permissions.Action;
import com.bendude56.bencmd.permissions.PermissionUser;

public class SpoutConnector {

	public void sendSkin(Player p, int id, String skin) {
		org.getspout.spoutapi.player.SpoutPlayer player = ((org.getspout.spoutapi.player.SpoutPlayer) p);
		if (player.isSpoutCraftEnabled()) {
			player.sendPacket(new org.getspout.spoutapi.packet.PacketSkinURL(id, skin));
		}
	}

	public boolean enabled(Player p) {
		return SpoutManager.getPlayer(p).isSpoutCraftEnabled();
	}

	public void setItemName(Material m, short d, String name) {
		SpoutManager.getItemManager().setItemName(m, d, name);
	}

	public void playMusic(Player p, String loc) {
		if (enabled(p)) {
			SpoutManager.getSoundManager().playCustomMusic(BenCmd.getPlugin(), SpoutManager.getPlayer(p), loc, true);
		}
	}

	public void playSound(Player p, String loc) {
		if (enabled(p)) {
			SpoutManager.getSoundManager().playCustomSoundEffect(BenCmd.getPlugin(), SpoutManager.getPlayer(p), loc, false);
		}
	}

	public void sendNotification(Player p, String title, String message, Material m) {
		if (enabled(p)) {
			SpoutManager.getPlayer(p).sendNotification(title, message, m);
		}
	}

	public List<Integer> getValidIDs() {
		List<Integer> valid = new ArrayList<Integer>();
		for (int i = 0; i <= 2257; i++) {
			if (Material.getMaterial(i) != null)
				valid.add(i);
		}
		return valid;
	}

	public void nextItem(NPCScreen s) {
		int id = s.item.getTypeId();
		List<Integer> valid = getValidIDs();
		if (!valid.contains(id)) {
			return;
		}
		if (valid.lastIndexOf(id) == valid.size() - 1) {
			s.removeWidget(s.item);
			s.item.setTypeId(0);
			s.item.setData((short) 0);
			s.attachWidget(BenCmd.getPlugin(), s.item);
		} else {
			s.removeWidget(s.item);
			s.item.setTypeId(valid.get(valid.lastIndexOf(id) + 1));
			s.item.setData((short) 0);
			s.attachWidget(BenCmd.getPlugin(), s.item);
		}
	}

	public void prevItem(NPCScreen s) {
		int id = s.item.getTypeId();
		List<Integer> valid = getValidIDs();
		if (!valid.contains(id)) {
			return;
		}
		if (valid.lastIndexOf(id) == 0) {
			s.removeWidget(s.item);
			s.item.setTypeId(2257);
			s.item.setData((short) 0);
			s.attachWidget(BenCmd.getPlugin(), s.item);
		} else {
			s.removeWidget(s.item);
			s.item.setTypeId(valid.get(valid.lastIndexOf(id) - 1));
			s.item.setData((short) 0);
			s.attachWidget(BenCmd.getPlugin(), s.item);
		}
	}

	public void showStatusScreen(User u) {
		showStatusScreen(u, u, true);
	}

	public void showStatusScreen(User u, PermissionUser u2, boolean adv) {
		StatusScreen scr = new StatusScreen();
		InGameHUD hud = SpoutManager.getPlayer(((Player) u.getHandle())).getMainScreen();

		// labelbanned
		GenericLabel labelbanned = new GenericLabel();
		Action ban = u2.isBanned();
		labelbanned.setText("Banned: " + ((ban != null) ? ((adv) ? ban.formatTimeLeft() : "YES") : "NO")).setTextColor((ban != null) ? new Color(194F / 255F, 10F / 255F, 28F / 255F) : new Color(100F / 255F, 100F / 255F, 100F / 255F)).setX(30).setY(20).setWidth(100).setHeight(12);
		scr.attachWidget(BenCmd.getPlugin(), labelbanned);

		// labeljailed
		GenericLabel labeljailed = new GenericLabel();
		Action jail = u2.isJailed();
		labeljailed.setText("Jailed: " + ((jail != null) ? ((adv) ? jail.formatTimeLeft() : "YES") : "NO")).setTextColor((jail != null) ? new Color(194F / 255F, 10F / 255F, 28F / 255F) : new Color(100F / 255F, 100F / 255F, 100F / 255F)).setX(30).setY(35).setWidth(100).setHeight(12);
		scr.attachWidget(BenCmd.getPlugin(), labeljailed);

		// labelmuted
		GenericLabel labelmuted = new GenericLabel();
		Action mute = u2.isMuted();
		labelmuted.setText("Muted: " + ((mute != null) ? ((adv) ? mute.formatTimeLeft() : "YES") : "NO")).setTextColor((mute != null) ? new Color(194F / 255F, 10F / 255F, 28F / 255F) : new Color(100F / 255F, 100F / 255F, 100F / 255F)).setX(30).setY(50).setWidth(100).setHeight(12);
		scr.attachWidget(BenCmd.getPlugin(), labelmuted);

		// buttonclose
		GenericButton buttonclose = new GenericButton();
		buttonclose.setText("Close").setY(210).setX(375).setWidth(40).setHeight(20);
		scr.attachWidget(BenCmd.getPlugin(), buttonclose);
		scr.close = buttonclose;

		// Show pop-up
		hud.attachPopupScreen(scr);
	}

	public void showNPCScreen(Player p, NPC n) {
		User u = User.getUser(p);
		NPCScreen infoscr = new NPCScreen();
		InGameHUD mainscr = SpoutManager.getPlayer(p).getMainScreen();
		infoscr.npc = n;

		// idlabel
		GenericLabel idlabel = new GenericLabel();
		idlabel.setText("NPC ID:").setX(10).setY(20).setWidth(40).setHeight(12);
		infoscr.attachWidget(BenCmd.getPlugin(), idlabel);

		// idlabel2
		GenericLabel idlabel2 = new GenericLabel();
		idlabel2.setText(String.valueOf(n.getID())).setTextColor(new Color(50F / 255F, 1F, 50F / 255F)).setX(65).setY(20).setWidth(100).setHeight(12);
		infoscr.attachWidget(BenCmd.getPlugin(), idlabel2);

		// typelabel
		GenericLabel typelabel = new GenericLabel();
		typelabel.setText("NPC Type:").setX(10).setY(40).setWidth(40).setHeight(12);
		infoscr.attachWidget(BenCmd.getPlugin(), typelabel);

		// typelabel2
		GenericLabel typelabel2 = new GenericLabel();
		String text;
		if (n instanceof BankerNPC) {
			text = "Banker";
		} else if (n instanceof BankManagerNPC) {
			text = "Bank Manager";
		} else if (n instanceof BlacksmithNPC) {
			text = "Blacksmith";
		} else if (n instanceof StaticNPC) {
			text = "Static";
		} else {
			text = "Unknown";
		}
		typelabel2.setText(text).setTextColor(new Color(50F / 255F, 50F / 255F, 1F)).setX(65).setY(40).setWidth(100).setHeight(12);
		infoscr.attachWidget(BenCmd.getPlugin(), typelabel2);

		// namefield
		GenericTextField namefield = new GenericTextField();
		namefield.setText(n.getName()).setMaximumCharacters(20).setEnabled(n instanceof Skinnable && u.hasPerm("bencmd.npc.create")).setX(65).setY(60).setWidth(200).setHeight(12);
		infoscr.attachWidget(BenCmd.getPlugin(), namefield);
		infoscr.name = namefield;

		// namelabel
		GenericLabel namelabel = new GenericLabel();
		namelabel.setText("NPC Name:").setX(10).setY(60).setWidth(40).setHeight(12);
		infoscr.attachWidget(BenCmd.getPlugin(), namelabel);

		// skinfield
		GenericTextField skinfield = new GenericTextField();
		skinfield.setText(n.getSkinURL()).setMaximumCharacters(500).setEnabled(n instanceof Skinnable && u.hasPerm("bencmd.npc.create")).setX(65).setY(80).setWidth(350).setHeight(12);
		infoscr.attachWidget(BenCmd.getPlugin(), skinfield);
		infoscr.skin = skinfield;

		// skinlabel
		GenericLabel skinlabel = new GenericLabel();
		skinlabel.setText("NPC Skin:").setX(10).setY(80).setWidth(40).setHeight(12);
		infoscr.attachWidget(BenCmd.getPlugin(), skinlabel);

		// itemlabel
		GenericLabel itemlabel = new GenericLabel();
		itemlabel.setText("Item Held:").setX(10).setY(105).setWidth(40).setHeight(12);
		infoscr.attachWidget(BenCmd.getPlugin(), itemlabel);

		// itemimage
		GenericItemWidget itemimage = new GenericItemWidget();
		itemimage.setTypeId(n.getHeldItem().getTypeId()).setDepth(16).setData(n.getHeldItem().getDurability()).setX(65).setY(100).setWidth(16).setHeight(16);
		infoscr.attachWidget(BenCmd.getPlugin(), itemimage);
		infoscr.item = itemimage;

		// idownbutton
		GenericButton idownbutton = new GenericButton();
		idownbutton.setEnabled((n instanceof Skinnable) && u.hasPerm("bencmd.npc.create"));
		idownbutton.setText("v").setX(90).setY(110).setWidth(12).setHeight(10);
		infoscr.attachWidget(BenCmd.getPlugin(), idownbutton);
		infoscr.idown = idownbutton;

		// iupbutton
		GenericButton iupbutton = new GenericButton();
		iupbutton.setEnabled((n instanceof Skinnable) && u.hasPerm("bencmd.npc.create"));
		iupbutton.setText("^").setX(90).setY(100).setWidth(12).setHeight(10);
		infoscr.attachWidget(BenCmd.getPlugin(), iupbutton);
		infoscr.iup = iupbutton;

		// applybutton
		GenericButton applybutton = new GenericButton();
		applybutton.setEnabled((n instanceof Skinnable) && u.hasPerm("bencmd.npc.create"));
		applybutton.setText("Apply").setY(210).setX(325).setWidth(40).setHeight(20);
		infoscr.attachWidget(BenCmd.getPlugin(), applybutton);
		infoscr.apply = applybutton;

		// okbutton
		GenericButton okbutton = new GenericButton();
		okbutton.setEnabled((n instanceof Skinnable) && u.hasPerm("bencmd.npc.create"));
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

	public class StatusScreen extends GenericPopup {
		public Button	close;
	}

	public class NPCScreen extends GenericPopup {
		public NPC			npc;
		public TextField	name;
		public TextField	skin;
		public Button		apply;
		public Button		ok;
		public Button		cancel;
		public ItemWidget	item;
		public Button		iup;
		public Button		idown;
	}
}
