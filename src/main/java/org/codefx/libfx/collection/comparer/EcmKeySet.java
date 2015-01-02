package org.codefx.libfx.collection.comparer;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

/**
 * A {@link Set} which wraps the keys of an {@link EqualityComparatorMap}.
 * <p>
 * This set provides a view, so changes to the set are reflected in the map and vice versa.
 *
 * @param <K>
 *            the type of keys contained in the map's entries
 * @param <V>
 *            the type of values contained in the map's entries
 */
class EcmKeySet<K, V>
		extends AbstractEcmWrappingCollection<K, V, K>
		implements Set<K> {

	/**
	 * Creates a new key set which wraps the specified map.
	 *
	 * @param map
	 *            the map to wrap
	 */
	public EcmKeySet(EqualityComparatorMap<K, V> map) {
		super(map);
	}

	// IMPLEMENTATION OF 'AbstractEcmWrappingCollection<K, V, K>'

	@Override
	protected <T> void fillArray(T[] array) {
		int index = 0;
		for (K key : this) {
			@SuppressWarnings("unchecked")
			// due to erasure, this cast can never fail, but writing the reference to the array can;
			// this would throw a ArrayStoreException which is in accordance with the contract of
			// 'Collection.toArray(T[])'
			T unsafeKey = (T) key;
			array[index++] = unsafeKey;
		}
	}

	// IMPLEMENTATION OF 'Set<K>'

	@Override
	public boolean contains(Object o) {
		return comparatorMap.containsKey(o);
	}

	@Override
	public Iterator<K> iterator() {
		Iterator<Entry<EqualityComparatorObject<K>, V>> innerMapIterator =
				comparatorMap.getInnerMap().entrySet().iterator();
		return new EcmKeySetIterator<>(innerMapIterator);
	}

	@Override
	public boolean add(K key) {
		if (comparatorMap.containsKey(key))
			return false;

		comparatorMap.put(key, null);
		return true;
	}

	@Override
	public boolean remove(Object o) {
		if (comparatorMap.containsKey(o)) {
			comparatorMap.remove(o);
			return true;
		} else
			return false;
	}
}
