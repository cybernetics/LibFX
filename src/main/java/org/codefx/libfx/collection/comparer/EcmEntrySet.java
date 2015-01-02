package org.codefx.libfx.collection.comparer;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

/**
 * A {@link Set} which wraps the entries of an {@link EqualityComparatorMap}.
 * <p>
 * This set provides a view, so changes to the set are reflected in the map and vice versa.
 *
 * @param <K>
 *            the type of keys contained in the entries
 * @param <V>
 *            the type of values contained in the entries
 */
class EcmEntrySet<K, V>
		extends AbstractEcmWrappingCollection<K, V, Entry<K, V>>
		implements Set<Entry<K, V>> {

	/**
	 * Creates a new entry set which wraps the specified map.
	 *
	 * @param map
	 *            the map to wrap
	 */
	public EcmEntrySet(EqualityComparatorMap<K, V> map) {
		super(map);
	}

	// IMPLEMENTATION OF 'AbstractEcmWrappingCollection'

	@Override
	protected <T> void fillArray(T[] array) {
		int index = 0;
		for (Entry<K, V> entry : this) {
			@SuppressWarnings("unchecked")
			// due to erasure, this cast can never fail, but writing the reference to the array can;
			// this would throw a ArrayStoreException which is in accordance with the contract of
			// 'Collection.toArray(T[])'
			T unsafeEntry = (T) entry;
			array[index++] = unsafeEntry;
		}
	}

	// IMPLEMENTATION OF 'Set<Entry<K, V>>'

	@Override
	public boolean contains(Object object) {
		// check whether the argument is even an entry
		if (!(object instanceof Entry))
			return false;
		Entry<?, ?> entry = (Entry<?, ?>) object;

		// make sure the key is not null
		if (entry.getKey() == null)
			// TODO throw NPE here?
			return false;

		if (comparatorMap.containsKey(entry.getKey())) {
			V currentValue = comparatorMap.get(entry.getKey());
			return Objects.equals(currentValue, entry.getValue());
		} else
			return false;
	}

	@Override
	public Iterator<Entry<K, V>> iterator() {
		Iterator<Entry<EqualityComparatorObject<K>, V>> innerMapIterator =
				comparatorMap.getInnerMap().entrySet().iterator();
		return new EcmEntrySetIterator<>(innerMapIterator);
	}

	@Override
	public boolean add(Entry<K, V> entry) {
		if (entry.getKey() == null)
			// TODO is this the correct exception?
			throw new UnsupportedOperationException();
		boolean alreadyInMap = comparatorMap.containsKey(entry.getKey());
		V originalValue = comparatorMap.put(entry.getKey(), entry.getValue());

		boolean sameValue = alreadyInMap && Objects.equals(originalValue, entry.getValue());
		return !sameValue;
	}

	@Override
	public boolean remove(Object object) {
		if (contains(object)) {
			Entry<?, ?> entry = (Entry<?, ?>) object;
			comparatorMap.remove(entry.getKey());
			return true;
		} else
			return false;
	}

}
