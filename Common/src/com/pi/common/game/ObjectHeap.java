package com.pi.common.game;

import java.util.Arrays;
import java.util.Iterator;

/**
 * A growable array with a lower array offset.
 * 
 * @author Westin
 * 
 * @param <E> The class this ObjectHeap provides.
 */
public class ObjectHeap<E> implements Iterable<E> {
	/**
	 * The amount by which the capacity of the array is incremented.
	 */
	private static final int DEFAULT_CAPACITY_INCREMENT = 10;
	/**
	 * The default starting length for the array, unless specified otherwise.
	 */
	private static final int DEFAULT_START_LENGTH = 10;
	/**
	 * The default array offset increment.
	 */
	private static final int DEFAULT_OFFSET_INCREMENT = 10;

	/**
	 * The number of set elements in this object heap.
	 */
	private int numElements = 0;
	/**
	 * The object data.
	 */
	private Object[] elementData;
	/**
	 * The array offset between global indices and array indices.
	 */
	private int arrayOffset = 0;
	/**
	 * The global index of the minimum set element.
	 */
	private int minSetElement = Integer.MAX_VALUE;

	/**
	 * Creates an object heap with a starting array of the given size, and an
	 * array offset of <code>0</code>.
	 * 
	 * @param startLength the starting length
	 */
	public ObjectHeap(final int startLength) {
		elementData = new Object[startLength];
	}

	/**
	 * Creates an object heap with the default starting size, and an array
	 * offset of <code>0</code>.
	 * 
	 * @see ObjectHeap#DEFAULT_START_LENGTH
	 */
	public ObjectHeap() {
		this(DEFAULT_START_LENGTH);
	}

	/**
	 * Grows the data array to the minimum length of the given parameter,
	 * disregarding the array offset.
	 * 
	 * @param capacity the new array size.
	 */
	private void grow(final int capacity) {
		int newCapacity = elementData.length;
		if (newCapacity < capacity) {
			newCapacity =
					(int) (Math.ceil(capacity
							/ DEFAULT_CAPACITY_INCREMENT) * DEFAULT_CAPACITY_INCREMENT);
		}
		if (newCapacity != elementData.length) {
			elementData =
					Arrays.copyOf(elementData, newCapacity);
		}
	}

	/**
	 * Updates the array offset for a new minimum set element.
	 */
	private void updateArrayOffset() {
		int newArrayOffset =
				(int) (Math.floor(minSetElement
						/ DEFAULT_OFFSET_INCREMENT) * DEFAULT_OFFSET_INCREMENT);
		if (newArrayOffset > arrayOffset) {
			if (elementData.length - newArrayOffset
					+ arrayOffset > 0) {
				Object[] newArray =
						new Object[elementData.length
								- newArrayOffset + arrayOffset];
				System.arraycopy(elementData, newArrayOffset
						- arrayOffset, newArray, 0,
						newArray.length);
				elementData = newArray;
			}
			arrayOffset = newArrayOffset;
		} else if (newArrayOffset < arrayOffset) {
			Object[] newArray =
					new Object[elementData.length
							- newArrayOffset + arrayOffset];
			System.arraycopy(elementData, 0, newArray,
					arrayOffset - newArrayOffset,
					elementData.length);
			elementData = newArray;
			arrayOffset = newArrayOffset;
		}
	}

	/**
	 * Recalculates the minimum set element from the given element index in
	 * global index space.
	 * 
	 * @param checkFrom the global index to check from
	 */
	private void updateMinSetElement(final int checkFrom) {
		for (int i = checkFrom - arrayOffset; i < capacity(); i++) {
			if (elementData[i] != null) {
				minSetElement = i + arrayOffset;
				return;
			}
		}
	}

	/**
	 * Gets the current array capacity, disregarding the array offset.
	 * 
	 * @return the array length
	 */
	public final synchronized int capacity() {
		return elementData.length;
	}

	/**
	 * Gets the element data at the specified index, accounting for the array
	 * offset.
	 * 
	 * @param index the global index
	 * @return the element at the given index, or <code>null</code> if out of
	 *         bounds
	 */
	@SuppressWarnings("unchecked")
	protected final E elementData(final int index) {
		if (index - arrayOffset >= 0
				&& index - arrayOffset < elementData.length) {
			return (E) elementData[index - arrayOffset];
		} else {
			return null;
		}
	}

	/**
	 * Gets the element data at the specified index, accounting for the array
	 * offset.
	 * 
	 * @param index the global index
	 * @return the element at the given index, or <code>null</code> if out of
	 *         bounds
	 */
	public final synchronized E get(final int index) {
		return elementData(index);
	}

	/**
	 * Sets the element data at the specified index, accounting for the array
	 * offset, updating it if necessary, and growing the array if necessary.
	 * 
	 * @param index the global index
	 * @param element the element data
	 */
	public final synchronized void set(final int index,
			final E element) {
		if (element != null) {
			minSetElement = Math.min(minSetElement, index);
		}
		updateArrayOffset();
		grow(index + 1 - arrayOffset);
		if (element == null) {
			remove(index);
			return;
		}
		if (elementData(index) == null && element != null) {
			numElements++;
		}
		elementData[index - arrayOffset] = element;
	}

	/**
	 * Sets the element data at the specified index, accounting for the array
	 * offset, and updating the array offset if possible.
	 * 
	 * @param index the global index
	 * @return the removed element, or <code>null</code> if out of bounds or a
	 *         null element.
	 */
	public final synchronized E remove(final int index) {
		E oldValue = elementData(index);
		if (index - arrayOffset >= 0
				&& index - arrayOffset < elementData.length) {
			if (elementData[index - arrayOffset] != null) {
				numElements--;
			}
			elementData[index - arrayOffset] = null;
		}
		if (minSetElement == index) {
			updateMinSetElement(index);
		}
		updateArrayOffset();
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
				while (cID < capacity()) {
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
}