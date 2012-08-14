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
	 * The size in bytes of the maximum packet size.
	 */
	public static final int MAX_BUFFER = 1024 * 64;

	/**
	 * The time in milliseconds to recalculate the current network speeds.
	 */
	public static final long NETWORK_SPEED_RECALCULATION_TIME =
			1000;

	/**
	 * The time in milliseconds to resend a bad handshake.
	 */
	public static final long HANDSHAKE_EXPIRY_TIME = 1000L;

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
		public static final int CHAR = 2;
		/**
		 * The size in bytes of a byte.
		 */
		public static final int BYTE = 1;

		/**
		 * The size in bits of a long.
		 */
		public static final int LONG_BITS = 64;
		/**
		 * The size in bits of a double.
		 */
		public static final int DOUBLE_BITS = 64;
		/**
		 * The size in bits of an integer.
		 */
		public static final int INT_BITS = 32;
		/**
		 * The size in bits of a float.
		 */
		public static final int FLOAT_BITS = 32;
		/**
		 * The size in bits of a short.
		 */
		public static final int SHORT_BITS = 16;
		/**
		 * The size in bits of a char.
		 */
		public static final int CHAR_BITS = 16;
		/**
		 * The size in bits of a byte.
		 */
		public static final int BYTE_BITS = 8;
		/**
		 * The size in bits of a nibble.
		 */
		public static final int NIBBLE_BITS = 4;

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
