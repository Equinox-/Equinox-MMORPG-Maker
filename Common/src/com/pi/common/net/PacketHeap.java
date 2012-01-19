package com.pi.common.net;

import java.util.Arrays;

public class PacketHeap<E> {
    private static final int capacityIncrement = 10;
    private int writeHead = 0, readHead = 0;
    private Object[] elementData = new Object[10];

    private void grow(int capacity) {
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
	index += readHead;
	return index >= 0 && index < elementData.length ? (E) elementData[index]
		: null;
    }

    public synchronized void addLast(E element) {
	grow(writeHead + 1);
	elementData[writeHead] = element;
	writeHead++;
    }

    public int size() {
	return capacity() - readHead;
    }

    public synchronized E removeFirst() {
	if (!isEmpty()) {
	    E oldValue = elementData(0);
	    elementData[readHead] = null;
	    readHead++;
	    if (readHead >= capacityIncrement) {
		Object[] copy = new Object[elementData.length - readHead];
		System.arraycopy(elementData, readHead, copy, 0,
			elementData.length - readHead);
		elementData = copy;
		writeHead -= readHead;
		readHead = 0;
	    }
	    return oldValue;
	}
	return null;
    }

    public synchronized E peakFirst() {
	return elementData(0);
    }

    public boolean isEmpty() {
	return peakFirst() == null;
    }
}
