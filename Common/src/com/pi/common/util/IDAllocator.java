package com.pi.common.util;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import com.sun.xml.internal.bind.v2.model.core.ID;

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
	private Queue<Integer> availableIDs = new LinkedBlockingQueue<Integer>();
	/**
	 * The current head. This provides an identification number, then increments
	 * by one, if the amount of numbers in the available queue is zero.
	 * 
	 * @see #availableIDs
	 */
	private int ID = 0;

	/**
	 * Gets an available ID number by first looking at the queue, and if that
	 * doesn't have one, the ID head.
	 * 
	 * @see #ID
	 * @see #availableIDs
	 * @return the first available ID
	 */
	public int checkOut() {
		if (availableIDs.size() > 0) {
			return availableIDs.poll();
		}
		return ID++;
	}

	/**
	 * Checks the given number in, allowing it to be used again.
	 * 
	 * More specifically, if the ID is equal to the current head minus 1 this
	 * decrements the head by one, otherwise it adds the ID to the available
	 * queue.
	 * 
	 * @see #availableIDs
	 * @see ID
	 * @param id
	 *            the ID number to check in
	 */
	public void checkIn(int id) {
		if (id == ID - 1) {
			ID--;
		} else {
			availableIDs.add(id);
		}
	}
}
