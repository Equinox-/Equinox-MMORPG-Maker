package com.pi.client.graphics.device;

import java.util.Arrays;
import java.util.Enumeration;

public class GraphicsHeap<E> {
    private static final int capacityIncrement = 10;
    private Object[] elementData = new Object[10];
    private int elements = 0;

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
    E elementData(int index) {
	return index < elementData.length ? (E) elementData[index] : null;
    }

    public synchronized E get(int index) {
	return elementData(index);
    }

    public synchronized void set(int index, E element) {
	grow(index + 1);
	if (elementData[index]==null)
	    elements++;
	elementData[index] = element;
    }

    public synchronized E remove(int index) {
	E oldValue = elementData(index);
	if (index < elementData.length){
	    elementData[index] = null;
	    elements--;
	}
	return oldValue;
    }

    public int size() {
	return elements;
    }
}