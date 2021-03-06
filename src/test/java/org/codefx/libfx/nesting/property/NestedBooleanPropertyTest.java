package org.codefx.libfx.nesting.property;

import javafx.beans.property.BooleanProperty;

import org.codefx.libfx.nesting.Nesting;

/**
 * Tests the class {@link NestedBooleanProperty}.
 */
public class NestedBooleanPropertyTest extends AbstractNestedBooleanPropertyTest {

	@Override
	protected NestedProperty<Boolean> createNestedPropertyFromNesting(Nesting<BooleanProperty> nesting) {
		return new NestedBooleanProperty(nesting, null, null);
	}

}
