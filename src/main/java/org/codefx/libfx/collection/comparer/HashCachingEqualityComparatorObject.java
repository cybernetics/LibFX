package org.codefx.libfx.collection.comparer;

/**
 * Implementation of {@link EqualityComparatorObject} which caches the hash code during construction. This has the added
 * effect that a cast class exception will be thrown if the comparator can not compute the hash code of the specified
 * object.
 *
 * @param <T>
 *            The type of the wrapped objects.
 */
class HashCachingEqualityComparatorObject<T> implements EqualityComparatorObject<T> {

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
	public HashCachingEqualityComparatorObject(EqualityComparator<? super T> comparator, Object object) {
		assert comparator != null : "The argument 'comparator' must not be null.";
		assert object != null : "The argument 'object' must not be null.";

		this.comparator = comparator;

		@SuppressWarnings("unchecked")
		// due to type erasure, this cast will throw no exception;
		// but if 'object's type prevents it from being an argument to 'comparator.hashCode',
		// that call below will throw an exception
		T typedObject = (T) object;
		this.object = typedObject;
		this.hashCode = comparator.hashCode(typedObject);
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
		if (!(obj instanceof HashCachingEqualityComparatorObject<?>))
			return false;
		HashCachingEqualityComparatorObject<?> other = (HashCachingEqualityComparatorObject<?>) obj;
		// both objects must use the same comparator to ensure symmetry of 'equals'
		if (comparator != other.comparator)
			return false;

		// if the comparator objects equal if both reference the same object
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

	@Override
	public String toString() {
		return "EqualityComparatorObject [" + object.toString() + "]";
	}

	/*
	 * FIELD ACCESS
	 */

	@Override
	public T getObject() {
		return object;
	}

}
