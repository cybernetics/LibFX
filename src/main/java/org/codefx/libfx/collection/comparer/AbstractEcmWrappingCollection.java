package org.codefx.libfx.collection.comparer;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

/**
 * Skeletal implementation of a {@link Collection} which wraps an {@link EqualityComparatorMap}.
 * <p>
 * This implementation provides a view so changes to the collection are reflected in the wrapped map and vice versa.
 *
 * @param <K>
 *            the type of keys in the wrapped map
 * @param <V>
 *            the type of values in the wrapped map
 * @param <E>
 *            the type of elements in this collection
 */
abstract class AbstractEcmWrappingCollection<K, V, E> implements Collection<E> {

	/**
	 * The wrapped comparator map.
	 */
	protected EqualityComparatorMap<K, V> comparatorMap;

	/**
	 * Creates a new collection which wraps the given comparator map.
	 *
	 * @param map
	 *            the map wrapped by the new instance
	 */
	public AbstractEcmWrappingCollection(EqualityComparatorMap<K, V> map) {
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
		fillArray(array);
		return array;
	}

	@Override
	public <T> T[] toArray(T[] inputArray) {
		Objects.requireNonNull(inputArray, "The argument 'inputArray' must not be null.");

		T[] array = createArrayWithMapSize(inputArray);
		fillArray(array);
		markEndWithNull(array);

		return array;
	}

	/**
	 * Provides an array of the same type as the specified array which has at least the length of this collection's
	 * {@link #size() size}. If the input array is sufficiently long, it is returned; otherwise a new array is created.
	 *
	 * @param <T>
	 *            the component type of the array
	 * @param inputArray
	 *            the input array
	 * @return an array {@code T[]} with length equal to or greater than {@link #size() size}
	 */
	private <T> T[] createArrayWithMapSize(T[] inputArray) {
		int size = size();
		boolean arrayHasSufficientLength = size <= inputArray.length;
		if (arrayHasSufficientLength)
			return inputArray;
		else {
			@SuppressWarnings("unchecked")
			// the array created by 'Array.newInstance' is of the correct type
			T[] array = (T[]) Array.newInstance(inputArray.getClass().getComponentType(), size);
			return array;
		}
	}

	/**
	 * Fills the specified array with the elements from this collection as returned by its {@link #iterator() iterator}.
	 *
	 * @param <T>
	 *            the component type of the array
	 * @param array
	 *            the array which is filled with entries
	 */
	protected abstract <T> void fillArray(T[] array);

	/**
	 * If the specified array is longer than the size of this collection, the first position after all elements from
	 * this collection is set to null.
	 *
	 * @param <T>
	 *            the component type of the array
	 * @param array
	 *            the array which might be edited
	 */
	private <T> void markEndWithNull(T[] array) {
		int collectionSize = size();
		if (collectionSize < array.length)
			array[collectionSize] = null;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object item : c)
			if (!contains(item))
				return false;
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends E> otherCollection) {
		boolean changed = false;
		for (E entry : otherCollection)
			changed |= add(entry);
		return changed;
	}

	@Override
	public boolean retainAll(Collection<?> otherCollection) {
		boolean changed = false;
		Iterator<E> iterator = iterator();
		while (iterator.hasNext()) {
			E element = iterator.next();
			boolean remove = !otherCollection.contains(element);
			if (remove) {
				iterator.remove();
				changed = true;
			}
		}
		return changed;
	}

	@Override
	public boolean removeAll(Collection<?> otherCollection) {
		boolean changed = false;
		for (Object entry : otherCollection)
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
