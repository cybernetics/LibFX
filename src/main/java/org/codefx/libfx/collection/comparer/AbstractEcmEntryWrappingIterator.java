package org.codefx.libfx.collection.comparer;

import java.util.Iterator;
import java.util.Map.Entry;

/**
 * Skeletal implementation of an {@link Iterator} based on wrapping another iterator over {@link java.util.Map.Entry
 * entries} of {@link EqualityComparatorObject}s and values.
 *
 * @param <K>
 *            the type of keys contained in the iterator's entries' comparator objects
 * @param <V>
 *            the type of values in the wrapped iterator's entries
 * @param <E>
 *            the type of elements returned by this iterator
 */
abstract class AbstractEcmEntryWrappingIterator<K, V, E> implements Iterator<E> {

	/**
	 * The iterator over the comparator objects.
	 */
	protected Iterator<Entry<EqualityComparatorObject<K>, V>> iterator;

	/**
	 * Creates a new iterator wrapping the specified iterator.
	 *
	 * @param iterator
	 *            the iterator to wrap
	 */
	public AbstractEcmEntryWrappingIterator(Iterator<Entry<EqualityComparatorObject<K>, V>> iterator) {
		this.iterator = iterator;
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public void remove() {
		iterator.remove();
	}

}
