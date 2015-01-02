package org.codefx.libfx.collection.comparer;

import java.util.Collection;
import java.util.HashMap;
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
	private final EcmEntrySet<K, V> entries;

	/**
	 * The view on this map's keys.
	 */
	private final EcmKeySet<K, V> keys;

	/**
	 * The view on this map's values.
	 */
	private final EcmValueCollection<K, V> values;

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
		this.entries = new EcmEntrySet<>(this);
		this.keys = new EcmKeySet<>(this);
		this.values = new EcmValueCollection<>(this);
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
			put(entry.getKey(), entry.getValue());
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
	 * GET INNER MAP
	 */

	/**
	 * @return the map backing this comparator map
	 */
	Map<EqualityComparatorObject<K>, V> getInnerMap() {
		return innerMap;
	}

}
