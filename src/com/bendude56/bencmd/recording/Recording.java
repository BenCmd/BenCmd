package com.bendude56.bencmd.recording;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.bendude56.bencmd.BenCmd;

public class Recording implements Cloneable {
	private CopyOnWriteArrayList<RecordEntry>	entries;
	private String								file;
	private boolean								t;

	protected Recording(boolean t) {
		entries = new CopyOnWriteArrayList<RecordEntry>();
		this.t = t;
	}

	public Recording(String file, boolean t) throws IOException, ClassNotFoundException, ClassCastException {
		this.file = file;
		this.t = t;
		load();
	}

	@SuppressWarnings("unchecked")
	private void load() throws IOException, ClassNotFoundException, ClassCastException {
		ObjectInputStream stream = new ObjectInputStream(new FileInputStream(BenCmd.propDir + "record/" + ((t) ? "~" : "") + file + ".rec"));
		entries = (CopyOnWriteArrayList<RecordEntry>) stream.readObject();
		stream.close();
	}

	public void save() throws IOException {
		ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(BenCmd.propDir + "record/" + ((t) ? "~" : "") + file + ".rec"));
		stream.writeObject(entries);
		stream.flush();
		stream.close();
	}

	protected void savePermanent() throws IOException {
		t = false;
		save();
	}

	protected void saveAs(String filename) throws IOException {
		file = filename;
		save();
	}

	protected void unload() {
		entries = null;
	}

	public String getFileName(boolean withTemp) {
		if (withTemp) {
			return ((t) ? "~" : "") + file;
		} else {
			return file;
		}
	}

	public boolean isTemporary() {
		return t;
	}

	public void addEntry(RecordEntry e) {
		entries.add(e);
	}

	public CopyOnWriteArrayList<RecordEntry> getEntries() {
		return entries;
	}
	
	public void trimToUsers(List<String> user) {
		for (RecordEntry e : entries) {
			if (!user.contains(e.getUser())) {
				entries.remove(e);
			}
		}
	}
	
	public void trimToLastHour() {
		for (RecordEntry e : entries) {
			if (new Date().getTime() - e.getTime() > 60000) {
				entries.remove(e);
			}
		}
	}

	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			throw new AssertionError();
		}
	}
}
