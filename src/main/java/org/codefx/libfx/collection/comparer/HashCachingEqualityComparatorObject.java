package org.codefx.libfx.collection.comparer;

/**
 * Implementation of {@link EqualityComparatorObject} which caches the hash code during construction. This has the added
 * effect that a {@link ClassCastException} will be thrown during construction if the comparator can not compute the
 * hash code of the specified object.
 *
 * @param <T>
 *            The type of the wrapped objects.
 */
final class HashCachingEqualityComparatorObject<T> extends AbstractEqualityComparatorObject<T> {

	/*
	 * FIELDS
	 */

	/**
	 * The {@link #object}'s hash code.
	 */
	private final int hashCode;

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
	public HashCachingEqualityComparatorObject(EqualityComparator<? super T> comparator, T object) {
		super(comparator, object);
		this.hashCode = comparator.hashCode(object);
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
		if (!(obj instanceof HashCachingEqualityComparatorObject<?>))
			return false;
		HashCachingEqualityComparatorObject<?> other = (HashCachingEqualityComparatorObject<?>) obj;
		// both objects must use the same comparator to ensure symmetry of 'equals'
		if (comparator != other.comparator)
			return false;

		// the comparator objects equal if both reference the same object
		if (object == other.object)
			return true;
		// if the hash codes differ, the wrapped objects are different
		if (hashCode != other.hashCode)
			return false;

		// use 'comparator' to check equality
		@SuppressWarnings("unchecked")
		// since both comparator object instances use the same comparator,
		// the casted object can be safely used as an argument to 'comparator.equals'
		T otherObject = (T) other.object;
		return comparator.equals(object, otherObject);
	}

	@Override
	public int hashCode() {
		return hashCode;
	}

}
