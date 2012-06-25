package com.pi.common.contants;

/**
 * Class containing networking constants.
 * 
 * @author Westin
 * 
 */
public final class NetworkConstants {
	/**
	 * The size in bytes of the download cache for updates.
	 */
	public static final int DOWNLOAD_CACHE_SIZE = 1024;

	/**
	 * A class to get the size of primitives.
	 * 
	 * @author Westin
	 * 
	 */
	public static final class SizeOf {
		/**
		 * The size in bytes of a long.
		 */
		public static final int LONG = 8;
		/**
		 * The size in bytes of a double.
		 */
		public static final int DOUBLE = 8;
		/**
		 * The size in bytes of an integer.
		 */
		public static final int INT = 4;
		/**
		 * The size in bytes of a float.
		 */
		public static final int FLOAT = 4;
		/**
		 * The size in bytes of a short.
		 */
		public static final int SHORT = 2;
		/**
		 * The size in bytes of a char.
		 */
		public static final int CHAR = 1;
		/**
		 * The size in bytes of a byte.
		 */
		public static final int BYTE = 1;

		/**
		 * Overridden constructor to forbid the construction of instances.
		 */
		private SizeOf() {
		}
	}

	/**
	 * Overridden constructor to prevent instances of this class from being
	 * created.
	 */
	private NetworkConstants() {
	}
}
