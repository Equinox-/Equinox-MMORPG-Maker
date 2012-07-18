package com.pi.common.game;

import java.util.Iterator;

/**
 * A frontend for an abstract iterator that filters out objects based on an
 * instance of the {@link Filter} class.
 * 
 * @author Westin
 * 
 * @param <E> the object type to iterate
 */
public final class FilteredIterator<E> implements Iterator<E> {
	/**
	 * The filter for accepting objects.
	 */
	private final Filter<E> filter;
	/**
	 * The temporary next object.
	 */
	private E eNext;
	/**
	 * The iterator backing this one.
	 */
	private final Iterator<E> backing;

	/**
	 * Creates a filtered iterator with the given backing iterator and filter
	 * method.
	 * 
	 * @param sBacking the backing iterator
	 * @param sFilter the filter method
	 */
	public FilteredIterator(final Iterator<E> sBacking,
			final Filter<E> sFilter) {
		this.backing = sBacking;
		this.filter = sFilter;
		this.eNext = getNext();
	}

	@Override
	public boolean hasNext() {
		return eNext != null;
	}

	@Override
	public E next() {
		E cache = eNext;
		eNext = getNext();
		return cache;
	}

	/**
	 * Gets the next object that satisfies the filter directly, without the
	 * cache, or <code>null</code> if there is none.
	 * 
	 * @return the next object
	 */
	private E getNext() {
		while (true) {
			E temp = backing.next();
			if (temp == null) {
				return null;
			} else if (filter.accept(temp)) {
				return temp;
			}
		}
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
};
