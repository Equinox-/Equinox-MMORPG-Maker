package com.pi.common.util;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A utility class to allocate identification numbers quickly.
 * 
 * @author westin
 * 
 */
public class IDAllocator {
	/**
	 * The available numbers that are less than the current head.
	 */
	private Queue<Integer> availableIDs =
			new LinkedBlockingQueue<Integer>();
	/**
	 * The current head. This provides an identification number, then increments
	 * by one, if the amount of numbers in the available queue is zero.
	 * 
	 * @see #availablecurrentIDs
	 */
	private int currentID = 0;

	/**
	 * Gets an available currentID number by first looking at the queue, and if
	 * that doesn't have one, the currentID head.
	 * 
	 * @see #currentID
	 * @see #availablecurrentIDs
	 * @return the first available currentID
	 */
	public final int checkOut() {
		if (availableIDs.size() > 0) {
			return availableIDs.poll();
		}
		return currentID++;
	}

	/**
	 * Checks the given number in, allowing it to be used again.
	 * 
	 * More specifically, if the currentID is equal to the current head minus 1
	 * this decrements the head by one, otherwise it adds the currentID to the
	 * available queue.
	 * 
	 * @see #availablecurrentIDs
	 * @see currentID
	 * @param id the currentID number to check in
	 */
	public final void checkIn(final int id) {
		if (id == currentID - 1) {
			currentID--;
		} else {
			availableIDs.add(id);
		}
	}
}
