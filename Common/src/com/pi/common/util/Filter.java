package com.pi.common.util;

/**
 * General purpose interface for filtering abstract objects.
 * 
 * @author Westin
 * 
 * @param <E> the object type to filter
 */
public interface Filter<E> {
	/**
	 * Checks to see if the provided object satisfies this filter, and returns
	 * <code>true</code> if it was satisfied, or <code>false</code> if not.
	 * 
	 * @param e the object to check
	 * @return if the filter is satisfied
	 */
	boolean accept(E e);
}
