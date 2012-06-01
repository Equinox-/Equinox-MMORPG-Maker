package com.pi.common.game;

import java.util.Arrays;

public class ObjectHeap<E> {
	private static final int defaultCapacityIncrement = 10;
	private static final int defaultStartLength = 10;

	private int capacityIncrement;

	private int numElements = 0;
	protected Object[] elementData;

	public ObjectHeap(int startLength, int increment) {
		elementData = new Object[startLength];
		capacityIncrement = increment;
	}

	public ObjectHeap(int startLength) {
		this(startLength, defaultCapacityIncrement);
	}

	public ObjectHeap() {
		this(defaultStartLength);
	}

	private void grow(int capacity) {
		// overflow-conscious code
		int newCapacity = elementData.length;
		while (newCapacity < capacity) {
			newCapacity += capacityIncrement;
		}
		if (newCapacity != elementData.length)
			elementData = Arrays.copyOf(elementData, newCapacity);
	}

	public synchronized int capacity() {
		return elementData.length;
	}

	@SuppressWarnings("unchecked")
	protected E elementData(int index) {
		return index >= 0 && index < elementData.length ? (E) elementData[index]
				: null;
	}

	public synchronized E get(int index) {
		return elementData(index);
	}

	public synchronized void set(int index, E element) {
		grow(index + 1);
		if (elementData[index] != null) {
			if (element == null)
				numElements--;
		} else if (element != null) {
			numElements++;
		}
		elementData[index] = element;
	}

	public synchronized E remove(int index) {
		E oldValue = elementData(index);
		if (index >= 0 && index < elementData.length) {
			if (elementData[index] != null)
				numElements--;
			elementData[index] = null;
		}
		return oldValue;
	}

	public int numElements() {
		return numElements;
	}
}