package org.codefx.libfx.collection.comparer;

/**
 * Straight-forward implementation of {@link EqualityComparatorObject}.
 *
 * @param <T>
 *            The type of the wrapped objects.
 */
final class SimpleEqualityComparatorObject<T> extends AbstractEqualityComparatorObject<T> {

	/*
	 * CONSTRUCTOR
	 */

	/**
	 * Creates a new comparator object.
	 *
	 * @param comparator
	 *            the {@link EqualityComparator} used by this instance
	 * @param object
	 *            the wrapped object
	 */
	public SimpleEqualityComparatorObject(EqualityComparator<? super T> comparator, T object) {
		super(comparator, object);
	}

	/*
	 * EQUALS & HASHCODE
	 */

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		// check whether both are of the same type, ignoring the erased generic type
		if (!(obj instanceof SimpleEqualityComparatorObject<?>))
			return false;
		SimpleEqualityComparatorObject<?> other = (SimpleEqualityComparatorObject<?>) obj;
		// both objects must use the same comparator to ensure symmetry of 'equals'
		if (comparator != other.comparator)
			return false;

		// the comparator objects equal if both reference the same object
		if (object == other.object)
			return true;

		// use 'comparator' to check equality
		@SuppressWarnings("unchecked")
		// since both comparator object instances use the same comparator,
		// the casted object can be safely used as an argument to 'comparator.equals'
		T otherObject = (T) other.object;
		return comparator.equals(object, otherObject);
	}

	@Override
	public int hashCode() {
		return comparator.hashCode(object);
	}

}
