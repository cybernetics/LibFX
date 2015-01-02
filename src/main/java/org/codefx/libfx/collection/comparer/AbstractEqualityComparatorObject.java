package org.codefx.libfx.collection.comparer;

/**
 * Abstract superclass to implementations of {@link EqualityComparatorObject}.
 *
 * @param <T>
 *            The type of the wrapped objects.
 */
abstract class AbstractEqualityComparatorObject<T> implements EqualityComparatorObject<T> {

	// #region FIELDS

	/**
	 * The comparator used for {@code equals} and {@code hashCode}.
	 */
	protected final EqualityComparator<? super T> comparator;

	/**
	 * The wrapped object.
	 */
	protected final T object;

	// #end FIELDS

	/**
	 * Creates a new comparator object.
	 *
	 * @param comparator
	 *            the {@link EqualityComparator} used by this instance
	 * @param object
	 *            the wrapped object
	 */
	protected AbstractEqualityComparatorObject(EqualityComparator<? super T> comparator, T object) {
		assert comparator != null : "The argument 'comparator' must not be null.";
		assert object != null : "The argument 'object' must not be null.";

		this.comparator = comparator;
		this.object = object;
	}

	@Override
	public final String toString() {
		return "EqualityComparatorObject [" + object.toString() + "]";
	}

	@Override
	public final T getObject() {
		return object;
	}

}
