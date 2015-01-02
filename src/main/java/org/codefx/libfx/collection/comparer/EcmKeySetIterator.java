package org.codefx.libfx.collection.comparer;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * An iterator over a {@link EqualityComparatorMap}'s {@link Map#entrySet() entrySet}.
 *
 * @param <K>
 *            the type of the entry's keys
 * @param <V>
 *            the type of the entry's values
 */
class EcmKeySetIterator<K, V> extends AbstractEcmEntryWrappingIterator<K, V, K> {

	/**
	 * Creates a new iterator wrapping the specified iterator.
	 *
	 * @param iterator
	 *            the wrapped iterator
	 */
	public EcmKeySetIterator(Iterator<Entry<EqualityComparatorObject<K>, V>> iterator) {
		super(iterator);
	}

	@Override
	public K next() {
		return iterator.next().getKey().getObject();
	}

}
