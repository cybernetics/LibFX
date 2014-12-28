package org.codefx.libfx.collection.comparer;

/**
 * A class which will be used as keys in the tests of customizable hash map.<br>
 * The class consists of two simple attributes, which will be set by the constructor. It does *not* override 'equals'
 * and 'hashCode' because that is done by a' TestKeyEqualityComparer'.
 */
class TestObject {

	/**
	 * Just some string attribute.
	 */
	private String stringAttribute;

	/**
	 * Just some integer attribute.
	 */
	private int intAttribute;

	/**
	 * Creates a new test key with the given values.
	 *
	 * @param stringAttribute
	 * @param intAttribute
	 */
	public TestObject(String stringAttribute, int intAttribute) {
		super();
		this.stringAttribute = stringAttribute;
		this.intAttribute = intAttribute;
	}

	/**
	 * @return the stringAttribute
	 */
	public String getStringAttribute() {
		return stringAttribute;
	}

	/**
	 * @return the intAttribute
	 */
	public int getIntAttribute() {
		return intAttribute;
	}

	@Override
	public String toString() {
		return "TestObject [" + stringAttribute + ", " + intAttribute + "]";
	}

}
