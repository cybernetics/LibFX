package org.codefx.libfx.collection.comparer;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * An iterator over an {@link EqualityComparatorMap}'s {@link Map#entrySet() entrySet}.
 *
 * @param <K>
 *            the type of the entry's keys
 * @param <V>
 *            the type of the entry's values
 */
class EcmEntrySetIterator<K, V> extends AbstractEcmEntryWrappingIterator<K, V, Entry<K, V>> {

	/**
	 * Creates a new iterator wrapping the specified iterator.
	 *
	 * @param iterator
	 *            the wrapped iterator
	 */
	public EcmEntrySetIterator(Iterator<Entry<EqualityComparatorObject<K>, V>> iterator) {
		super(iterator);
	}

	@Override
	public Entry<K, V> next() {
		Entry<EqualityComparatorObject<K>, V> entry = iterator.next();
		return new EcmEntry<>(entry);
	}

}
