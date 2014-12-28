package org.codefx.libfx.collection.comparer;

/**
 * A comparer encapsulates {@link #equals(Object, Object) equals} and {@link #hashCode(Object) hashCode} methods.
 * <p>
 * Both methods must follow their individual and shared contracts.
 *
 * @param <T>
 *            the type to which these methods can be applied
 */
public interface Comparer<T> {

	/**
	 * Compares obj1 and obj2 for equality.
	 * <p>
	 * The method must adhere to the contract of {@link Object#equals(Object)} and must always return the same result
	 * for the same object pairs as long as those objects' state is unchanged.
	 *
	 * @param obj1
	 *            the first object to compare
	 * @param obj2
	 *            the second object to compare
	 * @return true if the specified objects are considered equal
	 */
	public boolean equals(T obj1, T obj2);

	/**
	 * Computes a hash code for the specified object.
	 * <p>
	 * The method must adhere to the contract of {@link Object#hashCode()} and must always return the same result for
	 * the same object as long as that object's state is unchanged.
	 *
	 * @param obj
	 *            the object for which the hash code will be computed
	 * @return a hash code value for the specified object
	 * @see Object#hashCode()
	 */
	public int hashCode(T obj);

}
