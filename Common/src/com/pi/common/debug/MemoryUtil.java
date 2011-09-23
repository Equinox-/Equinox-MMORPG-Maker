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
}
