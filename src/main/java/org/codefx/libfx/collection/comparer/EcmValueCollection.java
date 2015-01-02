package org.codefx.libfx.collection.comparer;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Objects;

/**
 * A {@link Collection} which wraps the values of an {@link EqualityComparatorMap}.
 * <p>
 * This collection provides a view, so changes to the set are reflected in the map and vice versa.
 *
 * @param <K>
 *            the type of keys contained in the map entries
 * @param <V>
 *            the type of values contained in the map entries
 */
class EcmValueCollection<K, V> extends AbstractEcmWrappingCollection<K, V, V> {

	/**
	 * Creates a new value collection which wraps the specified map.
	 *
	 * @param map
	 *            the map to wrap
	 */
	public EcmValueCollection(EqualityComparatorMap<K, V> map) {
		super(map);
	}

	// IMPLEMENTATION OF 'AbstractEcmWrappingCollection'

	@Override
	protected <T> void fillArray(T[] array) {
		int index = 0;
		for (V value : this) {
			@SuppressWarnings("unchecked")
			// due to erasure, this cast can never fail, but writing the reference to the array can;
			// this would throw a ArrayStoreException which is in accordance with the contract of
			// 'Collection.toArray(T[])'
			T unsafeValue = (T) value;
			array[index++] = unsafeValue;
		}
	}

	// IMPLEMENTATION OF 'Collection'

	@Override
	public boolean contains(Object object) {
		return comparatorMap.getInnerMap().values().contains(object);
	}

	@Override
	public Iterator<V> iterator() {
		Iterator<Entry<EqualityComparatorObject<K>, V>> innerMapIterator =
				comparatorMap.getInnerMap().entrySet().iterator();
		return new EcmValueCollectionIterator<>(innerMapIterator);
	}

	@Override
	public boolean add(V element) {
		throw new UnsupportedOperationException(
				"The map backing this collection does not allow null-keys, so no element can be added to this view on the values.");
	}

	@Override
	public boolean remove(Object object) {
		for (Entry<EqualityComparatorObject<K>, V> entry : comparatorMap.getInnerMap().entrySet()) {
			boolean entryWithSpecifiedValue = Objects.equals(entry.getValue(), object);
			if (entryWithSpecifiedValue) {
				comparatorMap.getInnerMap().remove(entry.getKey());
				return true;
			}
		}
		return false;
	}

}
