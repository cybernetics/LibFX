package org.codefx.libfx.collection.comparer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ComparerHashMap<K, V> implements Map<K, V> {

	/*
	 * ATTRIBTUES
	 */

	/**
	 * The comparer used for the key's equality and hash code.
	 */
	private final Comparer<K> comparer;

	/**
	 * The actual map used to store the objects.
	 */
	private final HashMap<ComparerObject<K>, V> innerMap;

	/**
	 * The view on this map's entries.
	 */
	private final EntrySet entries;

	/**
	 * The view on this map's keys.
	 */
	private final KeySet keys;

	/**
	 * The view on this map's values.
	 */
	private final ValueCollection values;

	/*
	 * CONSTRUCTORS
	 */

	/**
	 * @param comparer
	 */
	public ComparerHashMap(Comparer<K> comparer) {
		this.comparer = comparer;
		innerMap = new HashMap<ComparerObject<K>, V>();
		entries = new EntrySet(this);
		keys = new KeySet(this);
		values = new ValueCollection(this);
	}

	/*
	 * METHODS
	 */

	/**
	 * Creates a new comparer object for the given key.<br>
	 * If the key is not of type 'K', null is returned which has to be checked by the calling functions.
	 *
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private ComparerObject<K> createKey(Object key) {
		try {
			return new ComparerObject<K>((K) key, comparer);
		} catch (ClassCastException e) {
			return null;
		}
	}

	/*
	 * IMPLEMENTATION OF 'Map<K, V>'
	 */

	@Override
	public int size() {
		return innerMap.size();
	}

	@Override
	public boolean isEmpty() {
		return innerMap.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		ComparerObject<K> comparerKey = createKey(key);
		if (comparerKey == null)
			return false;
		else
			return innerMap.containsKey(comparerKey);
	}

	@Override
	public boolean containsValue(Object value) {
		return innerMap.containsValue(value);
	}

	@Override
	public V get(Object key) {
		ComparerObject<K> comparerKey = createKey(key);
		if (comparerKey == null)
			return null;
		else
			return innerMap.get(comparerKey);
	}

	@Override
	public V put(K key, V value) {
		return innerMap.put(new ComparerObject<K>(key, comparer), value);
	}

	/**
	 * Calls 'put' with the given entry's key and value.
	 *
	 * @param entry
	 * @return the previous value associated with the key, or null if there was no mapping for key
	 */
	public V put(Entry<? extends K, ? extends V> entry) {
		return put(entry.getKey(), entry.getValue());
	}

	@Override
	public V remove(Object key) {
		ComparerObject<K> comparerKey = createKey(key);
		if (comparerKey == null)
			return null;
		else
			return innerMap.remove(comparerKey);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		for (Entry<? extends K, ? extends V> entry : m.entrySet())
			put(entry);
	}

	@Override
	public void clear() {
		innerMap.clear();
	}

	@Override
	public Set<K> keySet() {
		return keys;
	}

	@Override
	public Collection<V> values() {
		return values;
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return entries;
	}

	/*
	 * EQUALS AND HASHCODE
	 */

	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object obj) {
		if (obj == this)
			return true;

		if (!(obj instanceof Map))
			return false;
		Map<K, V> other = (Map<K, V>) obj;
		return CollectionComparer.equals(this.entrySet(), other.entrySet());
	}

	@Override
	public int hashCode() {
		return CollectionComparer.hashCode(this.entrySet());
	}

	/*
	 * PRIVATE CLASSES FOR ENTRY, KEY AND VALUE SETS
	 */

	/**
	 * Skeletal implementation of the collection interface based on wrapping a comparer map.
	 *
	 * @author pan
	 * @param <X>
	 */
	private abstract class WrappingCollection<X> implements Collection<X> {

		/**
		 * The wrapped comparer map.
		 */
		protected ComparerHashMap<K, V> comparerMap;

		/**
		 * Creates a new collection which wraps the given comparer map.
		 *
		 * @param map
		 */
		public WrappingCollection(ComparerHashMap<K, V> map) {
			super();
			this.comparerMap = map;
		}

		// PARTIAL IMPLEMENTATION OF 'Collection<X>'

		@Override
		public int size() {
			return comparerMap.size();
		}

		@Override
		public boolean isEmpty() {
			return comparerMap.isEmpty();
		}

		@Override
		public Object[] toArray() {
			Object[] array = new Object[comparerMap.size()];
			int index = 0;
			for (X entry : this)
				array[index++] = entry;
			return array;
		}

		@Override
		@SuppressWarnings("unchecked")
		public <T> T[] toArray(T[] inputArray) {
			// Implementation inspired by AbstractCollection.toArray

			// create a return array of appropriate size
			int size = size();
			T[] array = inputArray;
			if (inputArray.length < size)
				array = (T[]) java.lang.reflect.Array.newInstance(inputArray.getClass().getComponentType(), size);

			// fill it
			int index = 0;
			for (X entry : this)
				array[index++] = (T) entry;

			// mark the end with 'null'
			if (index < array.length)
				array[index] = null;

			return array;
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			for (Object item : c)
				if (!contains(item))
					return false;
			return true;
		}

		@Override
		public boolean addAll(Collection<? extends X> c) {
			boolean changed = false;
			for (X entry : c)
				changed |= add(entry);
			return changed;
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			boolean changed = false;
			Iterator<X> iterator = iterator();
			while (iterator.hasNext())
				if (!c.contains(iterator.next())) {
					iterator.remove();
					changed = true;
				}
			return changed;
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			boolean changed = false;
			for (Object entry : c)
				changed |= remove(entry);
			return changed;
		}

		@Override
		public void clear() {
			comparerMap.clear();
		}

		// EQUALS AND HASHCODE

		@Override
		public boolean equals(Object obj) {
			return CollectionComparer.equals(this, obj);
		}

		@Override
		public int hashCode() {
			return CollectionComparer.hashCode(this);
		}

	}

	/**
	 * Skeletal implementation of the iterator interface based on wrapping an iterator over an entry set which has
	 * comparer objects as keys.
	 *
	 * @author pan
	 * @param <X>
	 */
	private abstract class WrappingIterator<X> implements Iterator<X> {

		/**
		 * The iterator over the entry set.
		 */
		protected Iterator<Entry<ComparerObject<K>, V>> iterator;

		/**
		 * @param iterator
		 */
		public WrappingIterator(Iterator<Entry<ComparerObject<K>, V>> iterator) {
			super();
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

	/**
	 * A view on the given comparer hash map's entries.
	 *
	 * @author pan
	 */
	private class EntrySet extends WrappingCollection<Entry<K, V>> implements Set<Entry<K, V>> {

		/**
		 * @param map
		 */
		public EntrySet(ComparerHashMap<K, V> map) {
			super(map);
		}

		// IMPLEMENTATION OF 'Set<Entry<K, V>>'

		@Override
		@SuppressWarnings("unchecked")
		public boolean contains(Object o) {
			if (!(o instanceof Entry))
				return false;
			Entry<K, V> entry = (Entry<K, V>) o;
			if (comparerMap.containsKey(entry.getKey())) {
				V currentValue = comparerMap.get(entry.getKey());
				return Objects.equals(currentValue, entry.getValue());
			} else
				return false;
		}

		@Override
		public Iterator<Entry<K, V>> iterator() {
			return new EntrySetIterator(comparerMap.innerMap.entrySet().iterator());
		}

		@Override
		public boolean add(Entry<K, V> e) {
			if (e.getKey() == null && e.getValue() == null)
				throw new UnsupportedOperationException();
			boolean alreadyInMap = comparerMap.containsKey(e.getKey());
			V originalValue = comparerMap.put(e);
			return !(alreadyInMap && Objects.equals(originalValue, e.getValue()));
		}

		@Override
		@SuppressWarnings("unchecked")
		public boolean remove(Object o) {
			if (contains(o)) {
				comparerMap.remove(((Entry<K, V>) o).getKey());
				return true;
			} else
				return false;
		}

	}

	private class ComparerEntry implements Entry<K, V> {

		private Entry<ComparerObject<K>, V> wrappedEntry;

		public ComparerEntry(Entry<ComparerObject<K>, V> entry) {
			Objects.requireNonNull(entry);
			Objects.requireNonNull(entry.getKey());
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
		@SuppressWarnings("unchecked")
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ComparerEntry other = (ComparerEntry) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;

			return Objects.equals(wrappedEntry.getKey().getObject(), other.getKey())
					&& Objects.equals(wrappedEntry.getValue(), other.getValue());
		}

		/**
		 * Returns this comparer entry's outer type.
		 *
		 * @return
		 */
		@SuppressWarnings("rawtypes")
		private ComparerHashMap getOuterType() {
			return ComparerHashMap.this;
		}

		@Override
		public int hashCode() {
			return (wrappedEntry.getKey().getObject() == null ? 0 : wrappedEntry.getKey().getObject().hashCode()) ^
					(wrappedEntry.getValue() == null ? 0 : wrappedEntry.getValue().hashCode());
		}

	}

	/**
	 * An iterator over an entry set.
	 *
	 * @author pan
	 */
	private class EntrySetIterator extends WrappingIterator<Entry<K, V>> {

		/**
		 * @param iterator
		 */
		public EntrySetIterator(Iterator<Entry<ComparerObject<K>, V>> iterator) {
			super(iterator);
		}

		@Override
		public Entry<K, V> next() {
			Entry<ComparerObject<K>, V> entry = iterator.next();
			return new ComparerEntry(entry);
		}

	}

	/**
	 * A view on the given comparer hash map's entries.
	 *
	 * @author pan
	 */
	private class KeySet extends WrappingCollection<K> implements Set<K> {

		/**
		 * @param map
		 */
		public KeySet(ComparerHashMap<K, V> map) {
			super(map);
		}

		// IMPLEMENTATION OF 'Set<K>'

		@Override
		public boolean contains(Object o) {
			return comparerMap.containsKey(o);
		}

		@Override
		public Iterator<K> iterator() {
			return new KeySetIterator(comparerMap.innerMap.entrySet().iterator());
		}

		@Override
		public boolean add(K key) {
			if (comparerMap.containsKey(key))
				return false;

			comparerMap.put(key, null);
			return true;
		}

		@Override
		public boolean remove(Object o) {
			if (comparerMap.containsKey(o)) {
				comparerMap.remove(o);
				return true;
			} else
				return false;
		}

	}

	/**
	 * An iterator over a key set.
	 *
	 * @author pan
	 */
	private class KeySetIterator extends WrappingIterator<K> {

		/**
		 * @param iterator
		 */
		public KeySetIterator(Iterator<Entry<ComparerObject<K>, V>> iterator) {
			super(iterator);
		}

		@Override
		public K next() {
			return iterator.next().getKey().getObject();
		}

	}

	/**
	 * A value collection view for this map.
	 *
	 * @author pan
	 */
	private class ValueCollection extends WrappingCollection<V> {

		public ValueCollection(ComparerHashMap<K, V> map) {
			super(map);
		}

		// IMPLEMENTATION OF 'Collection<V>'

		@Override
		public boolean contains(Object o) {
			for (Entry<K, V> entry : comparerMap.entries)
				if (Objects.equals(entry.getValue(), o))
					return true;
			return false;
		}

		@Override
		public Iterator<V> iterator() {
			return new ValueCollectionIterator(comparerMap.innerMap.entrySet().iterator());
		}

		@Override
		public boolean add(V e) {
			if (comparerMap.containsKey(null))
				throw new IllegalArgumentException(
						"The map backing this collection already contains a null-key, so no value can be added without a key.");
			comparerMap.put(null, e);
			return true;
		}

		@Override
		public boolean remove(Object o) {
			for (Entry<K, V> entry : comparerMap.entries)
				if (Objects.equals(entry.getValue(), o)) {
					comparerMap.remove(entry.getKey());
					return true;
				}
			return false;
		}

	}

	/**
	 * An iterator over a value collection.
	 *
	 * @author pan
	 */
	private class ValueCollectionIterator extends WrappingIterator<V> {

		/**
		 * @param iterator
		 */
		public ValueCollectionIterator(Iterator<Entry<ComparerObject<K>, V>> iterator) {
			super(iterator);
		}

		@Override
		public V next() {
			return iterator.next().getValue();
		}

	}

}
