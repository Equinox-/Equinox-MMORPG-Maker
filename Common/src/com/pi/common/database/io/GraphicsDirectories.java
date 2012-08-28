package com.pi.common.database.io;

public enum GraphicsDirectories {
	NONE(""),
	ENTITIES("entities"),
	GUI("gui"),
	ITEMS("items"),
	TILES("tiles");
	/**
	 * The directory bit shift to obtain the actual directory ID.
	 */
	public static final int DIRECTORY_BIT_SHIFT = 24;

	/**
	 * The file mask to obtain the actual file ID.
	 */
	public static final int FILE_MASK = 0x00FFFFFF;

	private final String path;

	private GraphicsDirectories(final String p) {
		this.path = p;
	}

	public final String getPath() {
		return path;
	}

	public int getMask() {
		return ordinal();
	}

	public int getFileID(int id){
		return (getMask() << DIRECTORY_BIT_SHIFT) | id;
	}
}
