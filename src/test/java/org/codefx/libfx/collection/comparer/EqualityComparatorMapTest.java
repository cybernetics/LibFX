package org.codefx.libfx.collection.comparer;

import java.util.Map;
import java.util.Objects;

/**
 * Implements a concrete map interface test for 'CustomizableHashMap'.
 */
public class EqualityComparatorMapTest extends MapInterfaceTest<TestObject, String> {

	/**
	 * The comparator used to compare test keys.
	 */
	private final EqualityComparator<? super TestObject> comparator;

	/**
	 * Constructor
	 */
	public EqualityComparatorMapTest() {
		super(false, true, true, true, true, true);

		// create a new comparator which only uses the string attribute.
		comparator = new EqualityComparator<TestObject>() {

			@Override
			public boolean equals(TestObject obj1, TestObject obj2) {
				return Objects.equals(obj1.getStringAttribute(), obj2.getStringAttribute());
			}

			@Override
			public int hashCode(TestObject obj) {
				return obj.getStringAttribute().hashCode();
			}

		};
	}

	@Override
	protected Map<TestObject, String> makeEmptyMap() throws UnsupportedOperationException {
		return EqualityComparatorMap.forHashMap(comparator);
	}

	@Override
	protected Map<TestObject, String> makePopulatedMap() throws UnsupportedOperationException {
		Map<TestObject, String> map = EqualityComparatorMap.forHashMap(comparator);
		map.put(new TestObject("a", 0), "A");
		map.put(new TestObject("b", 1), "B");
		map.put(new TestObject("d", 4), "D");
		return map;
	}

	@Override
	protected TestObject getKeyNotInPopulatedMap() throws UnsupportedOperationException {
		return new TestObject("c", 2);
	}

	@Override
	protected String getValueNotInPopulatedMap() throws UnsupportedOperationException {
		return "C";
	}

	/*
	 * TESTS
	 */

	public void test_containsKey_keyOfWrongType_false() {
		Map<TestObject, String> map = makePopulatedMap();

		try {
			map.containsKey("No TestObject");
			fail();
		} catch (ClassCastException e) {
			// expected exception because a string is no valid argument to the used comparator (see above)
		}
	}
}
