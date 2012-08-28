package com.pi.common.database.io;

/**
 * An enum that represents sub directories to the graphics storage system.
 * 
 * @author westin
 * 
 */
public enum GraphicsDirectories {
	/**
	 * The enum representing no subdirectory, or the root of the graphics
	 * folder.
	 */
	NONE(""),
	/**
	 * The enum representing the "entities" subdirectory.
	 */
	ENTITIES("entities"),
	/**
	 * The enum representing the "gui" subdirectory.
	 */
	GUI("gui"),
	/**
	 * The enum representing the "items" subdirectory.
	 */
	ITEMS("items"),
	/**
	 * The enum representing the "tiles" subdirectory.
	 */
	TILES("tiles");
	/**
	 * The directory bit shift to obtain the actual directory ID.
	 */
	public static final int DIRECTORY_BIT_SHIFT = 24;

	/**
	 * The file mask to obtain the actual file ID.
	 */
	public static final int FILE_MASK = 0x00FFFFFF;

	/**
	 * The path of this subdirectory.
	 */
	private final String path;

	/**
	 * Creates a graphics directories enum for the given sub path.
	 * 
	 * @param p the sub path
	 */
	private GraphicsDirectories(final String p) {
		this.path = p;
	}

	/**
	 * The sub path of this directory.
	 * 
	 * @return the sub path
	 */
	public final String getPath() {
		return path;
	}

	/**
	 * The mask of this directory.
	 * 
	 * @return the integer mask
	 */
	public int getMask() {
		return ordinal();
	}

	/**
	 * Applies the directory mask for this subdirectory to the given ID, and
	 * returns the actual ID number.
	 * 
	 * @param id the relative ID
	 * @return the absolute ID
	 */
	public int getFileID(final int id) {
		return (getMask() << DIRECTORY_BIT_SHIFT) | id;
	}
}
