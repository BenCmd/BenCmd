package com.bendude56.bencmd.recording;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Location;

import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.BenCmdFile;
import com.bendude56.bencmd.User;
import com.bendude56.bencmd.recording.RecordEntry.SuspicionLevel;

public class RecordingFile extends BenCmdFile {

	private HashMap<String, Recording>	loaded;
	private List<String>				wand;
	private List<Recording>				recording;
	private Recording					temp;

	public RecordingFile() {
		super("recording.db", "--BenCmd Recording Database--", true);
		loaded = new HashMap<String, Recording>();
		recording = new ArrayList<Recording>();
		new File(BenCmd.propDir + "record").mkdirs();
		if (new File(BenCmd.propDir + "record/~temp.rec").exists()) {
			try {
				temp = new Recording("temp", true);
			} catch (Exception e) {
				BenCmd.log(Level.SEVERE, "Failed to load auto-recording!");
				BenCmd.log(e);
			}
		} else {
			try {
				temp = new Recording(true);
				temp.saveAs("temp");
			} catch (Exception e) {
				BenCmd.log(Level.SEVERE, "Failed to create auto-recording!");
				BenCmd.log(e);
			}
		}
		wand = new ArrayList<String>();
		loadFile();
		loadAll();
	}

	public boolean wandEnabled(String user) {
		return wand.contains(user);
	}

	public void enableWand(String user) {
		wand.add(user);
	}

	public void disableWand(String user) {
		wand.remove(user);
	}

	public boolean loadRecording(String user, String recording) {
		if (getFile().containsKey(recording)) {
			try {
				loaded.put(user, new Recording(recording, false));
				return true;
			} catch (Exception e) {
				BenCmd.log(Level.SEVERE, "Failed to load recording " + recording + "!");
				BenCmd.log(e);
				return false;
			}
		} else if (getFile().containsKey("~" + recording)) {
			for (Recording r : this.recording) {
				if (r.getFileName(false).equals(recording)) {
					loaded.put(user, r);
					return true;
				}
			}
			return false;
		} else {
			return false;
		}
	}

	public void unloadRecording(String user) {
		if (loaded.containsKey(user)) {
			loaded.remove(user);
		}
	}

	public Recording getLoaded(String user) {
		if (loaded.containsKey(user)) {
			return loaded.get(user);
		} else {
			return temp;
		}
	}

	public boolean hasRecordingLoaded(String user) {
		return loaded.containsKey(user);
	}

	public boolean recordingExists(String rec) {
		return getFile().containsKey(rec) || getFile().containsKey("~" + rec);
	}

	@Override
	public void saveAll() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void loadAll() {
		for (Entry<Object, Object> e : getFile().entrySet()) {
			if (((String) e.getKey()).startsWith("~")) {
				try {
					recording.add(new Recording(((String) e.getKey()).replaceFirst("~", ""), true));
				} catch (Exception ex) {
					BenCmd.log(Level.SEVERE, "Failed to load recording " + (String) e.getKey() + "!");
					BenCmd.log(ex);
				}
			}
		}
	}

	public void saveSignature(Recording r) {
		getFile().put(r.getFileName(true), "");
		saveFile();
	}

	public void turnPermanent(Recording r) throws IOException {
		if (r.getFileName(false).equals("temp")) {
			saveTemporary();
			return;
		}
		delete(r);
		List<String> user = new ArrayList<String>();
		for (Entry<String, Recording> e : loaded.entrySet()) {
			if (e.getValue().getFileName(false).equals(r.getFileName(false))) {
				user.add(e.getKey());
			}
		}
		r.savePermanent();
		saveSignature(r);
		for (String u : user) {
			loaded.put(u, r);
		}
	}

	public void saveTemporary() throws IOException {
		Recording r = (Recording) temp.clone();
		r.savePermanent();
		saveSignature(r);
	}

	public void rename(Recording r, String n) throws IOException {
		delete(r);
		r.saveAs(n);
		saveSignature(r);
	}

	public void copy(Recording r, String n) throws IOException {
		Recording r2 = (Recording) r.clone();
		r2.saveAs(n);
		saveSignature(r2);
		if (r2.isTemporary()) {
			recording.add(r2);
		}
	}

	public void logEvent(RecordEntry e) {
		try {
			temp.addEntry(e);
			temp.save();
			for (Recording r : recording) {
				r.addEntry(e);
				r.save();
			}
		} catch (IOException ex) {
			BenCmd.log(Level.SEVERE, "Failed to record event!");
			BenCmd.log(ex);
		}
	}

	public void delete(Recording r) throws IOException {
		if (!new File(BenCmd.propDir + "record/" + r.getFileName(true) + ".rec").delete()) {
			throw new IOException("Error deleting " + r.getFileName(true) + "!");
		}
		getFile().remove(r.getFileName(true));
		saveFile();
		if (recording.contains(r)) {
			recording.remove(r);
		}
		if (r.equals(temp)) {
			temp = new Recording(true);
			temp.saveAs("temp");
		}
		List<String> toRemove = new ArrayList<String>();
		for (Entry<String, Recording> e : loaded.entrySet()) {
			if (e.getValue().equals(r)) {
				toRemove.add(e.getKey());
			}
		}
		for (String s : toRemove) {
			User u;
			loaded.remove(s);
			if ((u = User.matchUser(s)) != null) {
				u.sendMessage(ChatColor.RED + "The recording you had loaded has been deleted!");
			}
		}
	}

	public Recording getTemporaryRecording() {
		return temp;
	}

	public void tick() {
		temp.checkTempTime();
	}

	public void newRecording(String r) throws IOException {
		Recording rec = new Recording(true);
		rec.saveAs(r);
		saveSignature(rec);
		recording.add(rec);
	}

	public void showRecords(User user, List<RecordEntry> entries, boolean showLocation, boolean showUser, int page) {
		HashMap<Integer, List<RecordEntry>> pages = new HashMap<Integer, List<RecordEntry>>();
		int cpage = 1;
		for (RecordEntry r : entries) {
			if (pages.containsKey(cpage)) {
				pages.get(cpage).add(r);
				if (pages.get(cpage).size() == 10) {
					cpage++;
				}
			} else {
				List<RecordEntry> t = new ArrayList<RecordEntry>();
				t.add(r);
				pages.put(cpage, t);
			}
		}
		if (!pages.containsKey(cpage)) {
			cpage--;
		}
		if (cpage == 0) {
			user.sendMessage(ChatColor.RED + "Your query returned no results!");
			return;
		}
		if (page <= 0) {
			user.sendMessage(ChatColor.RED + "There can be no page lower than 1.");
			return;
		}
		if (page > cpage) {
			user.sendMessage(ChatColor.RED + "There are only " + cpage + " pages...");
			return;
		}
		if (cpage != 1) {
			user.sendMessage(ChatColor.GRAY + "Page " + ChatColor.GREEN + page + ChatColor.GRAY + "/" + ChatColor.GREEN + cpage);
		}
		for (RecordEntry r : pages.get(page)) {
			r.printTo(user, showUser, showLocation);
		}
	}

	public void listBlock(final User user, final Location loc, final int page) {
		user.sendMessage(ChatColor.YELLOW + "Please wait... The logs are being queried...");
		new Thread() {
			public void run() {
				List<RecordEntry> results = new ArrayList<RecordEntry>();
				for (RecordEntry r : getLoaded(user.getName()).getEntries()) {
					if (r.getLocation().equals(loc)) {
						results.add(r);
					}
				}
				showRecords(user, results, false, true, page);
			}
		}.start();
	}

	public void listLevel(final User user, final SuspicionLevel level, final int page) {
		user.sendMessage(ChatColor.YELLOW + "Please wait... The logs are being queried...");
		new Thread() {
			public void run() {
				List<RecordEntry> results = new ArrayList<RecordEntry>();
				for (RecordEntry r : getLoaded(user.getName()).getEntries()) {
					if (r.getSuspicionLevel().getLevel() >= level.getLevel()) {
						results.add(r);
					}
				}
				showRecords(user, results, true, true, page);
			}
		}.start();
	}

	public void listRecordings(User user) {
		user.sendMessage(ChatColor.YELLOW + "Available recordings:");
		for (Entry<Object, Object> e : getFile().entrySet()) {
			if (((String) e.getKey()).startsWith("~")) {
				user.sendMessage(ChatColor.RED + ((String) e.getKey()).replaceFirst("~", ""));
			} else {
				user.sendMessage(ChatColor.GRAY + (String) e.getKey());
			}
		}
	}

}
