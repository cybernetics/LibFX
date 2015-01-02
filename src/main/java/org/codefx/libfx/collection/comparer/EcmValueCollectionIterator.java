package org.codefx.libfx.collection.comparer;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * An iterator over an {@link EqualityComparatorMap}'s {@link Map#values() value collection}.
 *
 * @param <K>
 *            the type of the entry's keys
 * @param <V>
 *            the type of the entry's values
 */
class EcmValueCollectionIterator<K, V> extends AbstractEcmEntryWrappingIterator<K, V, V> {

	/**
	 * Creates a new iterator wrapping the specified iterator.
	 *
	 * @param iterator
	 *            the wrapped iterator
	 */
	public EcmValueCollectionIterator(Iterator<Entry<EqualityComparatorObject<K>, V>> iterator) {
		super(iterator);
	}

	@Override
	public V next() {
		return iterator.next().getValue();
	}

}
