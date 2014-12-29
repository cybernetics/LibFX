package org.codefx.libfx.collection.comparer;

/**
 * Straight-forward implementation of {@link EqualityComparatorObject}.
 *
 * @param <T>
 *            The type of the wrapped objects.
 */
class SimpleEqualityComparatorObject<T> implements EqualityComparatorObject<T> {

	/*
	 * FIELDS
	 */

	/**
	 * The comparator used for {@code equals} and {@code hashCode}.
	 */
	private final EqualityComparator<? super T> comparator;

	/**
	 * The wrapped object.
	 */
	private final T object;

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
		// TODO disallow object == null
		this.comparator = comparator;
		this.object = object;
	}

	/*
	 * EQUALS, HASHCODE & TOSTRING
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

		// if both wrapped objects are null or reference the same object, both comparator objects equal
		if (object == other.object)
			return true;
		// if only one if the wrapped objects is null, they are not equal
		if (object == null || other.object == null)
			return false;

		// use 'comparator' to check equality
		try {
			@SuppressWarnings("unchecked")
			T otherObject = (T) other.object;
			return comparator.equals(object, otherObject);
		} catch (ClassCastException e) {
			// if the cast fails, there can be no equality between the two objects.
			return false;
		}
	}

	@Override
	public int hashCode() {
		if (object == null)
			return 0;
		else
			// use 'comparator' to compute the hash code
			return comparator.hashCode(object);
	}

	@Override
	public String toString() {
		return "SimpleEqualityComparatorObject [" + object.toString() + "]";
	}

	/*
	 * FIELD ACCESS
	 */

	@Override
	public T getObject() {
		return object;
	}

}
