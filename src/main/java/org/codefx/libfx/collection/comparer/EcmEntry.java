package org.codefx.libfx.collection.comparer;

import java.util.Map.Entry;
import java.util.Objects;

/**
 * An {@link Entry} of an {@link EqualityComparatorMap} which wraps an entry of the
 * {@link EqualityComparatorMap#getInnerMap() innerMap}.
 * <p>
 * An entry is only a view so changes to the map are reflected in th entry and vice versa.
 *
 * @param <K>
 *            the type of keys
 * @param <V>
 *            the type of values
 */
class EcmEntry<K, V> implements Entry<K, V> {

	/**
	 * The wrapped entry.
	 */
	private final Entry<EqualityComparatorObject<K>, V> wrappedEntry;

	/**
	 * Creates a new entry which wraps the specified entry.
	 *
	 * @param entry
	 *            the ntry to wrap
	 */
	public EcmEntry(Entry<EqualityComparatorObject<K>, V> entry) {
		assert entry != null : "The argument 'entry' must not be null.";
		assert entry.getKey() != null : "The specified entry's key must not be null.";

		wrappedEntry = entry;
	}

	@Override
	public K getKey() {
		return wrappedEntry.getKey().getObject();
	}

	@Override
	public V getValue() {
		return wrappedEntry.getValue();
	}

	@Override
	public V setValue(V value) {
		return wrappedEntry.setValue(value);
	}

	// EQUALS & HASHCODE

	@Override
	public boolean equals(Object object) {
		if (this == object)
			return true;
		if (object == null)
			return false;
		if (!(object instanceof Entry))
			return false;
		Entry<?, ?> other = (Entry<?, ?>) object;

		return Objects.equals(wrappedEntry.getKey().getObject(), other.getKey())
				&& Objects.equals(wrappedEntry.getValue(), other.getValue());
	}

	@Override
	public int hashCode() {
		return (wrappedEntry.getKey().getObject() == null ? 0 : wrappedEntry.getKey().getObject().hashCode()) ^
				(wrappedEntry.getValue() == null ? 0 : wrappedEntry.getValue().hashCode());
	}

}
