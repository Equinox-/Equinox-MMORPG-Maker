package com.pi.server.database;

import java.io.File;

import com.pi.common.database.world.SectorLocation;

/**
 * A class defining the locations of everything the server saves to the disk.
 * 
 * @author Westin
 * 
 */
public final class Paths {
	/**
	 * Gets the main directory all server information is saved to.
	 * 
	 * @return the main directory
	 */
	public static File getHomeDirectory() {
		File f = new File("equinox-server");
		if (!f.isDirectory()) {
			f.mkdir();
		}
		return f;
	}

	/**
	 * Gets the directory all sector information is saved to.
	 * 
	 * @return the sector directory
	 */
	public static File getSectorDirectory() {
		File f = new File(getHomeDirectory(), "world");
		if (!f.isDirectory()) {
			f.mkdir();
		}
		return f;
	}

	/**
	 * Gets the directory all definition sub-directories are located in.
	 * 
	 * @return the definitions directory
	 */
	public static File getDefDirectory() {
		File f = new File(getHomeDirectory(), "def");
		if (!f.isDirectory()) {
			f.mkdir();
		}
		return f;
	}

	/**
	 * Gets the directory all databases are saved to.
	 * 
	 * @return the database directory
	 */
	public static File getDatabaseFolder() {
		File f = new File(getHomeDirectory(), "database");
		if (!f.isDirectory()) {
			f.mkdir();
		}
		return f;
	}

	/**
	 * Gets the file the accounts database should be saved to.
	 * 
	 * @return the accounts database file
	 */
	public static File getAccountsDatabase() {
		File f = new File(getDatabaseFolder(), "accounts.db");
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return f;
	}

	/**
	 * Gets the file the sector at the given location should be saved to.
	 * 
	 * @param x the sector's x position
	 * @param plane the sector's plane
	 * @param z the sector's z position
	 * @return the sector's file
	 */
	public static File getSectorFile(final int x,
			final int plane, final int z) {
		return new File(getSectorDirectory(), x + "-" + plane
				+ "-" + z + ".sector");
	}

	/**
	 * Gets the file the sector at the given location should be saved to.
	 * 
	 * @param l the sector's position
	 * @return the sector's file
	 */
	public static File getSectorFile(final SectorLocation l) {
		return getSectorFile(l.getSectorX(), l.getPlane(),
				l.getSectorZ());
	}

	/**
	 * Gets the entity definition directory.
	 * 
	 * @return the entity definition directory
	 */
	public static File getEntityDefDirectory() {
		File f = new File(getDefDirectory(), "entity");
		if (!f.exists()) {
			f.mkdir();
		}
		return f;
	}

	/**
	 * Gets the item definition directory.
	 * 
	 * @return the item definition directory
	 */
	public static File getItemDefDirectory() {
		File f = new File(getDefDirectory(), "item");
		if (!f.exists()) {
			f.mkdir();
		}
		return f;
	}

	/**
	 * Gets the file the item definition with the given identification number is
	 * stored in.
	 * 
	 * @param def the id number
	 * @return the item definition file
	 */
	public static File getItemDef(final int def) {
		return new File(getItemDefDirectory(), def + ".def");
	}

	/**
	 * Gets the file the entity definition with the given identification number
	 * is stored in.
	 * 
	 * @param def the id number
	 * @return the entity definition file
	 */
	public static File getEntityDef(final int def) {
		return new File(getEntityDefDirectory(), def + ".def");
	}

	/**
	 * Gets the directory the log files are created in.
	 * 
	 * @return the log directory
	 */
	public static File getLogDirectory() {
		File f = new File(getHomeDirectory(), "log");
		if (!f.exists()) {
			f.mkdir();
		}
		return f;
	}

	/**
	 * Gets the file the server's logger prints to.
	 * 
	 * @return the log file
	 */
	public static File getLogFile() {
		return new File(getLogDirectory(), "log");
		/*
		 * _" + new SimpleDateFormat( "HH-mm-ss_MM-dd-yyyy") .format
		 * (Calendar.getInstance() .getTime()));
		 */
	}

	/**
	 * Overridden constructor to prevent instances from being created.
	 */
	private Paths() {
	}
}
