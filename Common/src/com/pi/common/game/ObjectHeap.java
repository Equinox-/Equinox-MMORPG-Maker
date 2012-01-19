package com.pi.common.game;

import java.util.Arrays;

public class ObjectHeap<E> {
    private static final int capacityIncrement = 10;
    protected Object[] elementData = new Object[10];

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
	elementData[index] = element;
    }

    public synchronized E remove(int index) {
	E oldValue = elementData(index);
	if (index >= 0 && index < elementData.length) {
	    elementData[index] = null;
	}
	return oldValue;
    }

    public int size() {
	return capacity();
    }
}