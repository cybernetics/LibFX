package org.codefx.libfx.collection.comparer;

/**
 * Wraps an object in order to use a {@link Comparer}'s methods for {@link Object#equals(Object) equals} and
 * {@link Object#hashCode() hashCode}. Two comparer objects can only be equal if they share the same comparer (otherwise
 * symmetry can not be guaranteed).
 *
 * @param <T>
 *            The type of the wrapped objects.
 */
class ComparerObject<T> {

	/*
	 * FIELDS
	 */

	/**
	 * The comparer used for {@code equals} and {@code hashCode}.
	 */
	private final Comparer<? super T> comparer;

	/**
	 * The wrapped object.
	 */
	private final T object;

	/*
	 * CONSTRUCTOR
	 */

	/**
	 * Creates a new comparer object.
	 *
	 * @param comparer
	 *            the {@link Comparer} used by this instance
	 * @param object
	 *            the wrapped object
	 */
	public ComparerObject(T object, Comparer<? super T> comparer) {
		this.comparer = comparer;
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
		if (!(obj instanceof ComparerObject<?>))
			return false;
		ComparerObject<?> other = (ComparerObject<?>) obj;
		// both objects must use the same comparer to ensure symmetry of 'equals'
		if (comparer != other.comparer)
			return false;

		// if both wrapped objects are null or reference the same object, both comparer objects equal
		if (object == other.object)
			return true;
		// if only one if the wrapped objects is null, they are not equal
		if (object == null || other.object == null)
			return false;

		// use 'comparer' to check equality
		try {
			@SuppressWarnings("unchecked")
			T otherObject = (T) other.object;
			return comparer.equals(object, otherObject);
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
			// use 'comparer' to compute the hash code
			return comparer.hashCode(object);
	}

	@Override
	public String toString() {
		return "ComparerObject of [" + object.toString() + "]";
	}

	/*
	 * FIELD ACCESS
	 */

	/**
	 * @return the wrapped object
	 */
	public T getObject() {
		return object;
	}

}
