package com.pi.common.util;

import java.util.HashMap;

/**
 * Utility class for managing pairs of integers and classes.
 * 
 * @param <E> the class to be represented
 * @author Westin
 */
public final class ClassIntPairManager<E> {
	/**
	 * The next available ID to this manager.
	 */
	private int currentID = 0;

	/**
	 * The packet id to class mapping array of this packet manager.
	 */
	private final ObjectHeap<Class<? extends E>> idMapping =
			new ObjectHeap<Class<? extends E>>();
	/**
	 * The packet class to id mapping array of this packet manager.
	 */
	private final HashMap<Class<? extends E>, Integer> classMapping =
			new HashMap<Class<? extends E>, Integer>();

	/**
	 * Trims the allocated maps for this manager down to the minimum size.
	 */
	public void trimMaps() {
		idMapping.trimToSize();
	}

	/**
	 * Determines the id of an object by it's class using a lookup table.
	 * 
	 * @param clazz the class the integer is paired to
	 * @return the packet's id
	 */
	public int getPairID(final Class<? extends E> clazz) {
		Integer i = classMapping.get(clazz);
		if (i != null) {
			return i.intValue();
		} else {
			return -1;
		}
	}

	/**
	 * Registers a packet with this pair manager.
	 * 
	 * @param clazz the pair class
	 */
	public void registerPair(final Class<? extends E> clazz) {
		idMapping.set(currentID, clazz);
		classMapping.put(clazz, currentID);
		currentID++;
	}

	/**
	 * Gets the pair class for the given ID number, or <code>null</code> if none
	 * exists.
	 * 
	 * @param id the id number
	 * @return the pair class
	 */
	public Class<? extends E> getPairClass(final int id) {
		return idMapping.get(id);
	}

	/**
	 * Gets the maximum ID number plus 1 registered to this pair manager.
	 * 
	 * This should mainly be used when allocation an array that needs to be able
	 * to represent every pair.
	 * 
	 * @return the maximum id number
	 */
	public int getPairCount() {
		return idMapping.capacity();
	}

	/**
	 * Creates a basic class-integer pair manager.
	 */
	public ClassIntPairManager() {
	}
}
