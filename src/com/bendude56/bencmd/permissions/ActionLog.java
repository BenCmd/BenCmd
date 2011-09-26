package com.bendude56.bencmd.permissions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;

import com.bendude56.bencmd.BenCmd;
import com.bendude56.bencmd.User;

public class ActionLog {
	private String l;
	private List<ActionLogEntry> sl;
	
	public ActionLog(String location) {
		l = location;
		loadAll();
	}
	
	public void loadAll() {
		sl = new ArrayList<ActionLogEntry>();
		if (!(new File(l).exists())) {
			try {
				new File(l).createNewFile();
			} catch (Exception e) {
				BenCmd.getPlugin().log.severe("Failed to load BenCmd action log:");
				e.printStackTrace();
				return;
			}
		}
		try {
			BufferedReader br = new BufferedReader(new FileReader(l));
			while (br.ready()) {
				try {
					String s = br.readLine();
					if (!s.isEmpty()) {
						sl.add(new ActionLogEntry(s));
					}
				} catch (Exception e) {
					BenCmd.getPlugin().log.severe("Failed to load an action log entry:");
					e.printStackTrace();
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			throw new AssertionError("File somehow exists and doesn't exist at the same time... Fileception!");
		} catch (IOException e) {
			BenCmd.getPlugin().log.severe("Failed to load BenCmd action log:");
			e.printStackTrace();
			return;
		}
		Collections.sort(sl);
	}
	
	public void log(ActionLogEntry entry) {
		sl.add(entry);
		if (!(new File(l).exists())) {
			try {
				new File(l).createNewFile();
			} catch (Exception e) {
				BenCmd.getPlugin().log.severe("Failed to save BenCmd action log:");
				e.printStackTrace();
				return;
			}
		}
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(l, true));
			bw.write(entry.getEntry() + "\n");
			bw.close();
		} catch (FileNotFoundException e) {
			throw new AssertionError("File somehow exists and doesn't exist at the same time... Fileception!");
		} catch (IOException e) {
			BenCmd.getPlugin().log.severe("Failed to save BenCmd action log:");
			e.printStackTrace();
			return;
		}
	}
	
	public List<ActionLogEntry> getEntries(String u) {
		List<ActionLogEntry> res = new ArrayList<ActionLogEntry>();
		for (ActionLogEntry e : sl) {
			if (e.getUser().equalsIgnoreCase(u)) {
				res.add(e);
			}
		}
		return res;
	}
	
	public List<ActionLogEntry> getEntriesFrom(String u) {
		List<ActionLogEntry> res = new ArrayList<ActionLogEntry>();
		for (ActionLogEntry e : sl) {
			if (e.getSender().equalsIgnoreCase(u)) {
				res.add(e);
			}
		}
		return res;
	}
	
	public List<ActionLogEntry> getEntries() {
		return sl;
	}
	
	public void searchEntries(final User user, final String u, final int page) {
		user.sendMessage(ChatColor.YELLOW + "Please wait... The action logs are being queried...");
		new Thread() {
			public void run() {
				List<ActionLogEntry> e = getEntries(u);
				try {
					if (e.isEmpty()) {
						user.sendMessage(ChatColor.RED + "There are no records pertaining to that user...");
					} else {
						user.sendMessage(ChatColor.GREEN + "Your search returned the following log entries:");
						showEntries(e, page, user);
					}
				} catch (NullPointerException ex) {
					// The user has left, take no action
				}
			}
		}.start();
	}
	
	public void searchEntries(final User user, final int page) {
		user.sendMessage(ChatColor.YELLOW + "Please wait... The action logs are being queried...");
		new Thread() {
			public void run() {
				List<ActionLogEntry> e = getEntries();
				try {
					if (e.isEmpty()) {
						user.sendMessage(ChatColor.RED + "There are no records... At all...");
					} else {
						user.sendMessage(ChatColor.GREEN + "Your search returned the following log entries:");
						showEntries(e, page, user);
					}
				} catch (NullPointerException ex) {
					// The user has left, take no action
				}
			}
		}.start();
	}
	
	public void searchEntriesFrom(final User user, final String u, final int page) {
		user.sendMessage(ChatColor.YELLOW + "Please wait... The action logs are being queried...");
		new Thread() {
			public void run() {
				List<ActionLogEntry> e = getEntriesFrom(u);
				try {
					if (e.isEmpty()) {
						user.sendMessage(ChatColor.RED + "There are no records from that user...");
					} else {
						user.sendMessage(ChatColor.GREEN + "Your search returned the following log entries:");
						showEntries(e, page, user);
					}
				} catch (NullPointerException ex) {
					// The user has left, take no action
				}
			}
		}.start();
	}
	
	public void showEntries(List<ActionLogEntry> entries, int page, User user) {
		HashMap<Integer, List<ActionLogEntry>> pages = new HashMap<Integer, List<ActionLogEntry>>();
		int cpage = 1;
		for (ActionLogEntry e : entries) {
			if (pages.containsKey(cpage)) {
				pages.get(cpage).add(e);
				if (pages.get(cpage).size() == 10) {
					cpage++;
				}
			} else {
				List<ActionLogEntry> t = new ArrayList<ActionLogEntry>();
				t.add(e);
				pages.put(cpage, t);
			}
		}
		if (!pages.containsKey(cpage)) {
			cpage--;
		}
		if (page <= 0) {
			user.sendMessage(ChatColor.RED
					+ "There can be no page lower than 1.");
			return;
		}
		if (page > cpage) {
			user.sendMessage(ChatColor.RED + "There are only " + cpage
					+ " pages...");
			return;
		}
		if (cpage != 1) {
			user.sendMessage(ChatColor.GRAY + "Page " + ChatColor.GREEN + page
					+ ChatColor.GRAY + "/" + ChatColor.GREEN + cpage);
		}
		for (ActionLogEntry l : pages.get(page)) {
			user.sendMessage(ChatColor.YELLOW + l.readShort());
		}
	}
}
