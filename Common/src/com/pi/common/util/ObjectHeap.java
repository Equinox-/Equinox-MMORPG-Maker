package com.pi.common.util;

import java.util.Arrays;
import java.util.Iterator;

/**
 * An array that allows for quick mapping between an integer and an object.
 * 
 * @author Westin
 * 
 * @param <E>
 *            The class this ObjectHeap provides.
 */
public class ObjectHeap<E> implements Iterable<E> {
	/**
	 * The number the capacity is incremented by.
	 */
	private static final int DEFAULT_CAPACITY_INCREMENT = 10;
	/**
	 * The default starting length for the array, unless specified otherwise.
	 */
	private static final int DEFAULT_START_LENGTH = 10;
	/**
	 * The number of set elements in this object heap.
	 */
	private int numElements = 0;
	/**
	 * If this is a fixed-size heap.
	 */
	private final boolean fixedSize;
	/**
	 * The object data.
	 */
	private transient Object[] elementData;

	/**
	 * Creates an object heap with a starting array of the given size.
	 * 
	 * @param startLength
	 *            the starting length
	 */
	public ObjectHeap(final int startLength) {
		this(startLength, false);
	}

	/**
	 * Creates an object heap with a starting array of the given size.
	 * 
	 * @param startLength
	 *            the starting length
	 * @param fixedSize
	 *            if this heap will never grow beyond the starting size
	 */
	public ObjectHeap(final int startLength, final boolean fixedSize) {
		elementData = new Object[startLength];
		this.fixedSize = fixedSize;
	}

	/**
	 * Creates an object heap with the default starting size.
	 * 
	 * @see ObjectHeap#DEFAULT_START_LENGTH
	 */
	public ObjectHeap() {
		this(DEFAULT_START_LENGTH);
	}

	/**
	 * Grows the data array to the minimum length of the given parameter.
	 * 
	 * @param capacity
	 *            the new array size.
	 * @return <code>true</code> if the array can hold the given value,
	 *         <code>false</code> if not
	 */
	private boolean grow(final int capacity) {
		if (elementData.length < capacity) {
			elementData = Arrays.copyOf(elementData, capacity
					+ DEFAULT_CAPACITY_INCREMENT);
		}
		return elementData.length >= capacity;
	}

	/**
	 * Gets the element data at the specified index.
	 * 
	 * @param index
	 *            the global index
	 * @return the element at the given index, or <code>null</code> if out of
	 *         bounds
	 */
	@SuppressWarnings("unchecked")
	protected final E elementData(final int index) {
		if (index >= 0 && index < elementData.length) {
			return (E) elementData[index];
		} else {
			return null;
		}
	}

	/**
	 * Gets the element data at the specified index.
	 * 
	 * @param index
	 *            the global index
	 * @return the element at the given index, or <code>null</code> if out of
	 *         bounds
	 */
	public final synchronized E get(final int index) {
		return elementData(index);
	}

	/**
	 * Sets the element data at the specified index, updating it if necessary,
	 * and growing the array if necessary.
	 * 
	 * @param index
	 *            the global index
	 * @param element
	 *            the element data
	 */
	public final synchronized void set(final int index, final E element) {
		if (element == null) {
			remove(index);
			return;
		}
		if (grow(index + 1)) {
			if (elementData(index) == null && element != null) {
				numElements++;
			}
			elementData[index] = element;
		}
	}

	/**
	 * Sets the element data at the specified index.
	 * 
	 * @param index
	 *            the global index
	 * @return the removed element, or <code>null</code> if out of bounds or a
	 *         null element.
	 */
	public final synchronized E remove(final int index) {
		E oldValue = elementData(index);
		if (index >= 0 && index < elementData.length) {
			if (elementData[index] != null) {
				numElements--;
			}
			elementData[index] = null;
		}
		return oldValue;
	}

	/**
	 * The number of set elements in the heap.
	 * 
	 * @return the number of set elements
	 */
	public final int numElements() {
		return numElements;
	}

	@Override
	public final Iterator<E> iterator() {
		return new Iterator<E>() {
			private int cID = 0;
			private E next = rNext();

			@Override
			public boolean hasNext() {
				return next != null;
			}

			public E rNext() {
				while (cID < elementData.length) {
					if (get(cID) != null) {
						return get(cID++);
					}
					cID++;
				}
				return null;
			}

			@Override
			public E next() {
				E next = this.next;
				this.next = rNext();
				return next;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	/**
	 * Copies the element data into the given array. This method only copys as
	 * many elements as it has room for.
	 * 
	 * @param arr
	 *            the array to copy into
	 * @return the array
	 */
	public E[] toArray(E[] arr) {
		System.arraycopy(elementData, 0, arr, 0, arr.length);
		return arr;
	}

	/**
	 * The size of the current backing array.
	 * 
	 * @return the array size
	 */
	public int capacity() {
		return elementData.length;
	}
}