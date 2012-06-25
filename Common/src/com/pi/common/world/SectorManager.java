package com.pi.common.world;

import java.util.Map;

import com.pi.common.database.Sector;
import com.pi.common.database.SectorLocation;

/**
 * An interface specifying the common methods of a sector manager.
 * 
 * @author Westin
 * 
 */
public interface SectorManager {
	/**
	 * Gets the sector at the given location, and adds it to the load queue if
	 * unloaded.
	 * 
	 * @param x the sector's x position
	 * @param plane the sector's plane
	 * @param z the sector's z position
	 * @return the sector, or <code>null</code> if unloaded or empty
	 */
	Sector getSector(int x, int plane, int z);

	/**
	 * Checks if the sector at the given location is empty.
	 * 
	 * @param x the sector's x position
	 * @param plane the sector's plane
	 * @param z the sector's z position
	 * @return <code>true</code> if the sector is not loaded, or is an empty
	 *         sector, otherwise <code>false</code>
	 */
	boolean isEmptySector(int x, int plane, int z);

	/**
	 * Gets the sector storage for the given location, and adds it to the load
	 * queue if unloaded.
	 * 
	 * @param x the sector's x position
	 * @param plane the sector's plane
	 * @param z the sector's z position
	 * @return the sector storage instance
	 */
	SectorStorage getSectorStorage(int x, int plane, int z);

	/**
	 * Gets an unmodifiable map of sector locations to sector storage.
	 * 
	 * @return the unmodifiable map
	 */
	Map<SectorLocation, SectorStorage> loadedMap();

	/**
	 * Container class for storing the various states of sectors in the map.
	 * 
	 * @author Westin
	 * 
	 */
	public static class SectorStorage {
		/**
		 * The last time this sector was used.
		 */
		private long lastUsed;
		/**
		 * The wrapped sector data.
		 */
		private Sector data;
		/**
		 * If this sector is loaded, but empty.
		 */
		private boolean empty;
		/**
		 * If last time that this sector was requested.
		 */
		private long requested = 0;

		/**
		 * Gets the sector instance stored, and updates the last used time.
		 * 
		 * @return the stored sector, or <code>null</code> if empty
		 */
		public final Sector getSector() {
			lastUsed = System.currentTimeMillis();
			return data;
		}

		/**
		 * Gets the sector instance stored, without updating the last used time.
		 * 
		 * @return the stored sector, or <code>null</code> if empty
		 */
		public final Sector getSectorRaw() {
			return data;
		}

		/**
		 * Gets the time this sector was last read.
		 * 
		 * @return the last used time in milliseconds
		 */
		public final long getLastUsedTime() {
			return lastUsed;
		}

		/**
		 * Checks if the empty flag is set for this sector.
		 * 
		 * @return the empty flag
		 */
		public final boolean isEmpty() {
			return empty;
		}

		/**
		 * Gets the time at which the sector was last requested.
		 * 
		 * @return the time in milliseconds
		 */
		public final long getRequestedTime() {
			return requested;
		}

		/**
		 * Sets the stored sector, and updates the last used time.
		 * 
		 * @param s the sector data
		 */
		public final void setSector(final Sector s) {
			lastUsed = System.currentTimeMillis();
			this.data = s;
		}

		/**
		 * Sets the empty flag for this storage object.
		 * 
		 * @param b the new empty value
		 */
		public final void setEmpty(final boolean b) {
			empty = b;
		}

		/**
		 * Updates the last used time to the current time.
		 */
		public final void updateLastTimeUsed() {
			lastUsed = System.currentTimeMillis();
		}
	}
}
