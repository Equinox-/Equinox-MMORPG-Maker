package com.pi.common.debug;

/**
 * A utility class to get memory information.
 * 
 * @author Westin
 * 
 */
public final class MemoryUtil {
	/**
	 * Get the amount of total memory currently allocated by the virtual
	 * machine.
	 * 
	 * @return the amount of allocated memory
	 */
	public static long getTotalMemory() {
		return Runtime.getRuntime().totalMemory();
	}

	/**
	 * Get the amount of free memory in the virtual machine.
	 * 
	 * @return the amount of free memory
	 */
	public static long getFreeMemory() {
		return Runtime.getRuntime().freeMemory();
	}

	/**
	 * Get the maximum amount of memory available by the virtual machine.
	 * 
	 * @return the maximum amount of memory available
	 */
	public static long getMaxMemory() {
		return Runtime.getRuntime().maxMemory();
	}

	/**
	 * Get the number of available processors to the virtual machine.
	 * 
	 * @return the number of available processors
	 */
	public static int avaliableProcessors() {
		return Runtime.getRuntime().availableProcessors();
	}

	/**
	 * Get the amount of memory used by the virtual machine.
	 * 
	 * @return the amount of memory used
	 */
	public static long getMemoryUsed() {
		return getTotalMemory() - getFreeMemory();
	}

	/**
	 * Constructor overridden to disallow instances from being created.
	 */
	private MemoryUtil() {
	}
}
