package org.codefx.libfx.collection.comparer;

import java.util.Collection;

/**
 * Implements default equals and hashCode methods for collections as used in 'AbstractSet'.
 * <p>
 * TODO turn into an implementation of {@link EqualityComparator}
 */
public class CollectionComparer {

	/**
	 * @see java.util.AbstractSet#equals(java.lang.Object)
	 * @param <T>
	 * @param collection
	 * @param other
	 * @return
	 */
	public static <T> boolean equals(Collection<T> collection, Object other) {
		if (collection == other)
			return true;
		if (collection == null || other == null)
			return false;
		if (!(other instanceof Collection))
			return false;

		Collection<?> otherCollection = (Collection<?>) other;
		if (collection.size() != otherCollection.size())
			return false;
		return collection.containsAll(otherCollection);
	}

	/**
	 * @see java.util.AbstractSet#hashCode()
	 * @param <T>
	 * @param iterable
	 * @return
	 */
	public static <T> int hashCode(Iterable<T> iterable) {
		int hash = 0;
		for (T item : iterable)
			if (item != null)
				hash += item.hashCode();
		return hash;
	}
}
