package com.pi.common.game;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map.Entry;

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

    public Iterator<Entry<Integer, E>> iterator() {
	return new ObjectHeapIterator<E>(this);
    }

    private static class ObjectHeapIterator<E> implements
	    Iterator<Entry<Integer, E>> {
	private final ObjectHeap<E> heap;
	private int head = 0;

	public ObjectHeapIterator(ObjectHeap<E> heap) {
	    this.heap = heap;
	}

	@Override
	public Entry<Integer, E> next() {
	    while (head < heap.capacity()) {
		E data = heap.get(head);
		head++;
		if (data != null)
		    return new ObjectEntry<Integer, E>(head - 1, data);
	    }
	    return null;
	}

	@Override
	public boolean hasNext() {
	    return head < heap.capacity();
	}

	@Override
	public void remove() {
	    throw new RuntimeException("No such method");
	}
    }

    private static class ObjectEntry<K, V> implements Entry<K, V> {
	private K key;
	private V val;

	public ObjectEntry(K k, V v) {
	    this.key = k;
	    this.val = v;
	}

	@Override
	public K getKey() {
	    return key;
	}

	@Override
	public V getValue() {
	    return val;
	}

	@Override
	public V setValue(V value) {
	    return this.val = value;
	}

    }
}