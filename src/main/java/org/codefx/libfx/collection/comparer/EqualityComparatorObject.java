package org.codefx.libfx.collection.comparer;

/**
 * Wraps an object in order to use an {@link EqualityComparator}'s methods for {@link Object#equals(Object) equals} and
 * {@link Object#hashCode() hashCode}.
 * <p>
 * Two instances which implement this interface can only be equal if they share the same equality comparator (otherwise
 * symmetry can not be guaranteed).
 *
 * @param <T>
 *            The type of the wrapped objects.
 */
interface EqualityComparatorObject<T> {

	@Override
	boolean equals(Object obj);

	@Override
	int hashCode();

	/**
	 * @return the wrapped object
	 */
	T getObject();

}
