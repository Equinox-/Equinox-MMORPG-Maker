package com.pi.common.debug;

public class MemoryUtil {
	public static long getTotalMemory() {
		return Runtime.getRuntime().totalMemory();
	}

	public static long getFreeMemory() {
		return Runtime.getRuntime().freeMemory();
	}

	public static long getMaxMemory() {
		return Runtime.getRuntime().maxMemory();
	}

	public static int avaliableProcessors() {
		return Runtime.getRuntime().availableProcessors();
	}

	public static long getMemoryUse() {
		return getTotalMemory() - getFreeMemory();
	}

	public static interface SizeOf {
		public static final int LONG = 8;
		public static final int DOUBLE = 8;
		public static final int INT = 4;
		public static final int FLOAT = 4;
		public static final int SHORT = 2;
		public static final int CHAR = 1;
		public static final int BYTE = 1;
	}
}
