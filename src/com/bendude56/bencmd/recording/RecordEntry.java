package com.bendude56.bencmd.recording;

import java.io.Serializable;
import java.text.SimpleDateFormat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;

import com.bendude56.bencmd.User;

public abstract class RecordEntry implements Serializable {
	private static final long	serialVersionUID	= -6140594140769211432L;

	public static abstract class BlockEntry extends RecordEntry {
		private static final long	serialVersionUID	= 1614896813831679841L;
		private Material			mat;

		public BlockEntry(SuspicionLevel level, String user, Location loc, long time, Material mat) {
			super(level, user, loc, time);
			this.mat = mat;
		}

		public Material getMaterial() {
			return mat;
		}

	}

	public static class BlockPlaceEntry extends BlockEntry {
		private static final long	serialVersionUID	= -6494617587023274772L;

		public BlockPlaceEntry(String user, Location loc, long time, Material mat) {
			super(SuspicionLevel.LOW, user, loc, time, mat);
			if (mat == Material.LAVA || mat == Material.FIRE || mat == Material.BEDROCK) {
				super.setSuspicionLevel(SuspicionLevel.EXTREME);
			} else if (mat == Material.WATER || mat == Material.DIAMOND_BLOCK || mat == Material.GOLD_BLOCK || mat == Material.LAPIS_BLOCK|| mat == Material.PISTON_BASE || mat == Material.PISTON_STICKY_BASE) {
				super.setSuspicionLevel(SuspicionLevel.HIGH);
			}
		}

		@Override
		public void printTo(User u, boolean printPlayer, boolean printLocation) {
			String print = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(getTime()) + ": ";
			print += "Block placed";
			if (printPlayer) {
				print += " (" + super.getUser() + ")";
			}
			print += ": " + super.getMaterial().toString();
			if (printLocation) {
				Location l = super.getLocation();
				print += " at (" + l.getWorld().getName() + ", " + l.getX() + ", " + l.getY() + ", " + l.getZ() + ")";
			}
			u.sendMessage(super.getSuspicionLevel().getColor() + print);
		}

	}

	public static class BlockBreakEntry extends BlockEntry {
		private static final long	serialVersionUID	= -110237626883839886L;

		public BlockBreakEntry(String user, Location loc, long time, Material mat) {
			super(SuspicionLevel.MID, user, loc, time, mat);
			if (mat == Material.BEDROCK){
				super.setSuspicionLevel(SuspicionLevel.EXTREME);
			} else if (mat == Material.OBSIDIAN || mat == Material.DIAMOND_BLOCK || mat == Material.GOLD_BLOCK || mat == Material.IRON_BLOCK || mat == Material.LAPIS_BLOCK || mat == Material.REDSTONE_WIRE) {
				super.setSuspicionLevel(SuspicionLevel.HIGH);
			} else if (mat == Material.GRASS || mat == Material.DIRT || mat == Material.LOG || mat == Material.LEAVES || mat == Material.SAND || mat == Material.STONE || mat == Material.COAL_ORE || mat == Material.GRAVEL || mat == Material.ICE || mat == Material.SAPLING || mat == Material.SOIL) {
				super.setSuspicionLevel(SuspicionLevel.LOW);
			}
		}

		@Override
		public void printTo(User u, boolean printPlayer, boolean printLocation) {
			String print = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(getTime()) + ": ";
			print += "Block broken";
			if (printPlayer) {
				print += " (" + super.getUser() + ")";
			}
			print += ": " + super.getMaterial().toString();
			if (printLocation) {
				Location l = super.getLocation();
				print += " at (" + l.getWorld().getName() + ", " + l.getX() + ", " + l.getY() + ", " + l.getZ() + ")";
			}
			u.sendMessage(super.getSuspicionLevel().getColor() + print);
		}

	}

	public static class ChestOpenEntry extends BlockEntry {
		private static final long	serialVersionUID	= 3397458855542861415L;

		public ChestOpenEntry(String user, Location loc, long time) {
			super(SuspicionLevel.MID, user, loc, time, Material.CHEST);
		}

		@Override
		public void printTo(User u, boolean printPlayer, boolean printLocation) {
			String print = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(getTime()) + ": ";
			print += "Chest opened";
			if (printPlayer) {
				print += " (" + super.getUser() + ")";
			}
			if (printLocation) {
				Location l = super.getLocation();
				print += " at (" + l.getWorld().getName() + ", " + l.getX() + ", " + l.getY() + ", " + l.getZ() + ")";
			}
			u.sendMessage(super.getSuspicionLevel().getColor() + print);
		}

	}

	public static class ChatEntry extends RecordEntry {
		private static final long	serialVersionUID	= 6035275018998186871L;
		private String				message;

		public ChatEntry(String user, long time, String message) {
			super(SuspicionLevel.LOW, user, null, time);
			this.message = message;
		}

		public String getMessage() {
			return message;
		}

		@Override
		public void printTo(User u, boolean printPlayer, boolean printLocation) {
			String print = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(getTime()) + ": ";
			print += "Chat";
			if (printPlayer) {
				print += " (" + super.getUser() + ")";
			}
			print += ": " + getMessage();
			u.sendMessage(super.getSuspicionLevel().getColor() + print);
		}

	}

	private SuspicionLevel	level;
	private String			user;
	private String			world;
	private int				x;
	private int				y;
	private int				z;
	private long			time;

	protected RecordEntry(SuspicionLevel level, String user, Location loc, long time) {
		this.level = level;
		this.user = user;
		this.world = loc.getWorld().getName();
		this.x = loc.getBlockX();
		this.y = loc.getBlockY();
		this.z = loc.getBlockZ();
		this.time = time;
	}

	public abstract void printTo(User u, boolean printPlayer, boolean printLocation);

	public long getTime() {
		return time;
	}

	public Location getLocation() {
		return new Location(Bukkit.getWorld(world), x, y, z);
	}

	public String getUser() {
		return user;
	}

	public SuspicionLevel getSuspicionLevel() {
		return level;
	}

	protected void setSuspicionLevel(SuspicionLevel level) {
		this.level = level;
	}

	public enum SuspicionLevel {
		LOW(ChatColor.GREEN, 0), MID(ChatColor.YELLOW, 1), HIGH(ChatColor.RED, 2), EXTREME(ChatColor.DARK_RED, 3);

		private ChatColor	color;
		private int			level;

		private SuspicionLevel(ChatColor color, int level) {
			this.color = color;
			this.level = level;
		}

		public ChatColor getColor() {
			return color;
		}

		public Integer getLevel() {
			return level;
		}
	}
}
