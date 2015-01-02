package org.codefx.libfx.collection.comparer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/*
 * TODO document: hashCode must not change after instance was added; map does not accept null keys; map throws
 * ClassCastExceptions if key arguments can not be evaluated by 'comparator'
 */

public class EqualityComparatorMap<K, V> implements Map<K, V> {

	/*
	 * Throughout this code, it is important to carefully distinguish keys of type 'K' and their wrapped counterparts of
	 * type 'EqualityComparatorObject<K>'. Therefore the first are usually called 'key', the latter 'eqKey'.
	 */

	/*
	 * ATTRIBTUES
	 */

	/**
	 * The comparator used for the keys' equality and hash code.
	 */
	private final EqualityComparator<? super K> comparator;

	/**
	 * The actual map used to store the objects.
	 */
	private final Map<EqualityComparatorObject<K>, V> innerMap;

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
	 * CONSTRUCTION
	 */

	// TODO comments: map returned by supplier must not be shared with other instances
	private EqualityComparatorMap(
			EqualityComparator<? super K> comparator,
			Supplier<? extends Map<EqualityComparatorObject<K>, V>> mapFactory) {

		Objects.requireNonNull(comparator, "The argument 'comparator' must not be null.");
		Objects.requireNonNull(mapFactory, "The argument 'mapFactory' must not be null.");

		this.comparator = comparator;
		this.innerMap = mapFactory.get();
		this.entries = new EntrySet(this);
		this.keys = new KeySet(this);
		this.values = new ValueCollection(this);
	}

	public static <K, V> Map<K, V> forHashMap(EqualityComparator<? super K> comparator) {
		return new EqualityComparatorMap<>(comparator, HashMap::new);
	}

	/*
	 * METHODS
	 */

	/**
	 * Creates a new comparator object for the specified key.
	 *
	 * @param key
	 *            the key for which a equality comparer wrapper will be constructed
	 * @return a new instance of {@link EqualityComparatorObject}
	 */
	private EqualityComparatorObject<K> createTypeSafeEqualityComparatorKey(K key) {
		Objects.requireNonNull(key, "The argument 'key' must not be null.");
		return new SimpleEqualityComparatorObject<>(comparator, key);
	}

	/**
	 * Creates a new comparator object for the specified key.
	 * <p>
	 * This requires an unchecked cast of the key to the erased type {@code K}. This can lead to
	 * {@link ClassCastException}s when the instance is of the wrong type and actually used by being passed to the
	 * {@link #comparator}. Calling functions have to check whether this is in accordance with the contract.
	 *
	 * @param key
	 *            the key for which a equality comparer wrapper will be constructed
	 * @return a new instance of {@link EqualityComparatorObject} which might throw {@link ClassCastException} when
	 *         {@code equals} or {@code hashCode} is called on it
	 */
	private EqualityComparatorObject<K> createTypeUnsafeEqualityComparatorKey(Object key) {
		Objects.requireNonNull(key, "The argument 'key' must not be null.");

		@SuppressWarnings("unchecked")
		K unsafelyTypedKey = (K) key;
		return new SimpleEqualityComparatorObject<>(comparator, unsafelyTypedKey);
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
		EqualityComparatorObject<K> eqKey = createTypeUnsafeEqualityComparatorKey(key);
		// can throw ClassCastException - this is in accordance with the contract
		return innerMap.containsKey(eqKey);
	}

	@Override
	public boolean containsValue(Object value) {
		return innerMap.containsValue(value);
	}

	@Override
	public V get(Object key) {
		EqualityComparatorObject<K> eqKey = createTypeUnsafeEqualityComparatorKey(key);
		// can throw ClassCastException - this is in accordance with the contract
		return innerMap.get(eqKey);
	}

	@Override
	public V put(K key, V value) {
		EqualityComparatorObject<K> eqKey = createTypeSafeEqualityComparatorKey(key);
		return innerMap.put(eqKey, value);
	}

	/**
	 * Calls 'put' with the given entry's key and value.
	 *
	 * @param entry
	 *            the entry whose key and value will be {@link #put(Object, Object)}
	 * @return the previous value associated with the key, or null if there was no mapping for key
	 */
	private V put(Entry<? extends K, ? extends V> entry) {
		return put(entry.getKey(), entry.getValue());
	}

	@Override
	public V remove(Object key) {
		EqualityComparatorObject<K> eqKey = createTypeUnsafeEqualityComparatorKey(key);
		// can throw ClassCastException - this is in accordance with the contract
		return innerMap.remove(eqKey);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		// TODO is there a way to implement this so innerMap.putAll can be called?
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
	 * Skeletal implementation of the collection interface based on wrapping a comparator map.
	 */
	private abstract class WrappingCollection<X> implements Collection<X> {

		/**
		 * The wrapped comparator map.
		 */
		protected EqualityComparatorMap<K, V> comparatorMap;

		/**
		 * Creates a new collection which wraps the given comparator map.
		 *
		 * @param map
		 */
		public WrappingCollection(EqualityComparatorMap<K, V> map) {
			this.comparatorMap = map;
		}

		// PARTIAL IMPLEMENTATION OF 'Collection<X>'

		@Override
		public int size() {
			return comparatorMap.size();
		}

		@Override
		public boolean isEmpty() {
			return comparatorMap.isEmpty();
		}

		@Override
		public Object[] toArray() {
			Object[] array = new Object[comparatorMap.size()];
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
			comparatorMap.clear();
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
	 * comparator objects as keys.
	 */
	private abstract class WrappingIterator<X> implements Iterator<X> {

		/**
		 * The iterator over the entry set.
		 */
		protected Iterator<Entry<EqualityComparatorObject<K>, V>> iterator;

		public WrappingIterator(Iterator<Entry<EqualityComparatorObject<K>, V>> iterator) {
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
	 * A view on the given comparator hash map's entries.
	 */
	private class EntrySet extends WrappingCollection<Entry<K, V>> implements Set<Entry<K, V>> {

		/**
		 * @param map
		 */
		public EntrySet(EqualityComparatorMap<K, V> map) {
			super(map);
		}

		// IMPLEMENTATION OF 'Set<Entry<K, V>>'

		@Override
		@SuppressWarnings("unchecked")
		public boolean contains(Object o) {
			// check whether the argument is even an entry
			if (!(o instanceof Entry))
				return false;
			Entry<K, V> entry = (Entry<K, V>) o;

			// make sure the key is not null
			if (entry.getKey() == null)
				return false;

			if (comparatorMap.containsKey(entry.getKey())) {
				V currentValue = comparatorMap.get(entry.getKey());
				return Objects.equals(currentValue, entry.getValue());
			} else
				return false;
		}

		@Override
		public Iterator<Entry<K, V>> iterator() {
			return new EntrySetIterator(comparatorMap.innerMap.entrySet().iterator());
		}

		@Override
		public boolean add(Entry<K, V> e) {
			if (e.getKey() == null && e.getValue() == null)
				throw new UnsupportedOperationException();
			boolean alreadyInMap = comparatorMap.containsKey(e.getKey());
			V originalValue = comparatorMap.put(e);
			return !(alreadyInMap && Objects.equals(originalValue, e.getValue()));
		}

		@Override
		@SuppressWarnings("unchecked")
		public boolean remove(Object o) {
			if (contains(o)) {
				comparatorMap.remove(((Entry<K, V>) o).getKey());
				return true;
			} else
				return false;
		}

	}

	private class EqualityComparatorEntry implements Entry<K, V> {

		private Entry<EqualityComparatorObject<K>, V> wrappedEntry;

		public EqualityComparatorEntry(Entry<EqualityComparatorObject<K>, V> entry) {
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
			EqualityComparatorEntry other = (EqualityComparatorEntry) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;

			return Objects.equals(wrappedEntry.getKey().getObject(), other.getKey())
					&& Objects.equals(wrappedEntry.getValue(), other.getValue());
		}

		/**
		 * Returns this comparator entry's outer type.
		 *
		 * @return
		 */
		@SuppressWarnings("rawtypes")
		private EqualityComparatorMap getOuterType() {
			return EqualityComparatorMap.this;
		}

		@Override
		public int hashCode() {
			return (wrappedEntry.getKey().getObject() == null ? 0 : wrappedEntry.getKey().getObject().hashCode()) ^
					(wrappedEntry.getValue() == null ? 0 : wrappedEntry.getValue().hashCode());
		}

	}

	/**
	 * An iterator over an entry set.
	 */
	private class EntrySetIterator extends WrappingIterator<Entry<K, V>> {

		/**
		 * @param iterator
		 */
		public EntrySetIterator(Iterator<Entry<EqualityComparatorObject<K>, V>> iterator) {
			super(iterator);
		}

		@Override
		public Entry<K, V> next() {
			Entry<EqualityComparatorObject<K>, V> entry = iterator.next();
			return new EqualityComparatorEntry(entry);
		}

	}

	/**
	 * A view on the given comparator hash map's entries.
	 */
	private class KeySet extends WrappingCollection<K> implements Set<K> {

		/**
		 * @param map
		 */
		public KeySet(EqualityComparatorMap<K, V> map) {
			super(map);
		}

		// IMPLEMENTATION OF 'Set<K>'

		@Override
		public boolean contains(Object o) {
			return comparatorMap.containsKey(o);
		}

		@Override
		public Iterator<K> iterator() {
			return new KeySetIterator(comparatorMap.innerMap.entrySet().iterator());
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

	/**
	 * An iterator over a key set.
	 */
	private class KeySetIterator extends WrappingIterator<K> {

		/**
		 * @param iterator
		 */
		public KeySetIterator(Iterator<Entry<EqualityComparatorObject<K>, V>> iterator) {
			super(iterator);
		}

		@Override
		public K next() {
			return iterator.next().getKey().getObject();
		}

	}

	/**
	 * A value collection view for this map.
	 */
	private class ValueCollection extends WrappingCollection<V> {

		public ValueCollection(EqualityComparatorMap<K, V> map) {
			super(map);
		}

		// IMPLEMENTATION OF 'Collection<V>'

		@Override
		public boolean contains(Object o) {
			for (Entry<K, V> entry : comparatorMap.entries)
				if (Objects.equals(entry.getValue(), o))
					return true;
			return false;
		}

		@Override
		public Iterator<V> iterator() {
			return new ValueCollectionIterator(comparatorMap.innerMap.entrySet().iterator());
		}

		@Override
		public boolean add(V e) {
			if (comparatorMap.containsKey(null))
				throw new IllegalArgumentException(
						"The map backing this collection already contains a null-key, so no value can be added without a key.");
			comparatorMap.put(null, e);
			return true;
		}

		@Override
		public boolean remove(Object o) {
			for (Entry<K, V> entry : comparatorMap.entries)
				if (Objects.equals(entry.getValue(), o)) {
					comparatorMap.remove(entry.getKey());
					return true;
				}
			return false;
		}

	}

	/**
	 * An iterator over a value collection.
	 */
	private class ValueCollectionIterator extends WrappingIterator<V> {

		/**
		 * @param iterator
		 */
		public ValueCollectionIterator(Iterator<Entry<EqualityComparatorObject<K>, V>> iterator) {
			super(iterator);
		}

		@Override
		public V next() {
			return iterator.next().getValue();
		}

	}

}
