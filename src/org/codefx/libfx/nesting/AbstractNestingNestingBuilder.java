package org.codefx.libfx.nesting;

import java.util.Objects;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.Property;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;

/**
 * An {@link AbstractNestingBuilder} which allows further nesting.
 *
 * @param <T>
 *            the type of the wrapped value
 * @param <O>
 *            the type of observable this builder can build
 */
abstract class AbstractNestingNestingBuilder<T, O extends ObservableValue<T>> extends AbstractNestingBuilder<T, O> {

	// #region CONSTRUCTION

	/**
	 * Creates a new nesting builder which acts as the outer builder.
	 *
	 * @param outerObservable
	 *            the outer observable upon which the constructed nesting depends
	 */
	protected AbstractNestingNestingBuilder(O outerObservable) {
		super(outerObservable);
	}

	/**
	 * Creates a new nesting builder which acts as a nested builder.
	 *
	 * @param <P>
	 *            the type the previous builder wraps
	 * @param previousNestedBuilder
	 *            the previous builder
	 * @param nestingStep
	 *            the function which performs the nesting step from one observable to the next
	 */
	protected <P> AbstractNestingNestingBuilder(AbstractNestingBuilder<P, ?> previousNestedBuilder,
			NestingStep<P, O> nestingStep) {
		super(previousNestedBuilder, nestingStep);
	}

	//#end CONSTRUCTION

	//#region NEST

	/**
	 * Returns a builder for nestings whose inner observable is an {@link ObservableValue}. The created nestings depend
	 * on this builder's outer observable and nesting steps and adds the specified step as the next one.
	 *
	 * @param <N>
	 *            the type wrapped by the created nesting builder
	 * @param nestingStep
	 *            the function which performs the nesting step from one observable to the next
	 * @return an {@link ObservableValueNestingBuilder} which builds a nesting from this builder's settings and the
	 *         specified nesting steps
	 */
	public <N> ObservableValueNestingBuilder<N> nestObservable(NestingStep<T, ObservableValue<N>> nestingStep) {
		Objects.requireNonNull(nestingStep, "The argument 'nestingStep' must not be null.");
		return new ObservableValueNestingBuilder<N>(this, nestingStep);
	}

	/**
	 * Returns a builder for nestings whose inner observable is a {@link Property}. The created nestings depend on this
	 * builder's outer observable and nesting steps and adds the specified step as the next one.
	 *
	 * @param <N>
	 *            the type wrapped by the created nesting builder
	 * @param nestingStep
	 *            the function which performs the nesting step from one observable to the next
	 * @return an {@link ObjectPropertyNestingBuilder} which builds a nesting from this builder's settings and the
	 *         specified nesting steps
	 */
	public <N> ObjectPropertyNestingBuilder<N> nestProperty(NestingStep<T, Property<N>> nestingStep) {
		Objects.requireNonNull(nestingStep, "The argument 'nestingStep' must not be null.");
		return new ObjectPropertyNestingBuilder<N>(this, nestingStep);
	}

	/**
	 * Returns a builder for nestings whose inner observable is a {@link BooleanProperty}. The created nestings depend
	 * on this builder's outer observable and nesting steps and adds the specified step as the next one.
	 *
	 * @param nestingStep
	 *            the function which performs the nesting step from one observable to the next
	 * @return a {@link BooleanPropertyNestingBuilder} which builds a nesting from this builder's settings and the
	 *         specified nesting steps
	 * @throws NullPointerException
	 *             if the specified function is null
	 */
	public BooleanPropertyNestingBuilder nestBooleanProperty(NestingStep<T, BooleanProperty> nestingStep) {
		Objects.requireNonNull(nestingStep, "The argument 'nestingStep' must not be null.");
		return new BooleanPropertyNestingBuilder(this, nestingStep);
	}

	/**
	 * Returns a builder for nestings whose inner observable is an {@link IntegerProperty}. The created nestings depend
	 * on this builder's outer observable and nesting steps and adds the specified step as the next one.
	 *
	 * @param nestingStep
	 *            the function which performs the nesting step from one observable to the next
	 * @return an {@link IntegerPropertyNestingBuilder} which builds a nesting from this builder's settings and the
	 *         specified nesting steps
	 * @throws NullPointerException
	 *             if the specified function is null
	 */
	public IntegerPropertyNestingBuilder nestIntegerProperty(NestingStep<T, IntegerProperty> nestingStep) {
		Objects.requireNonNull(nestingStep, "The argument 'nestingStep' must not be null.");
		return new IntegerPropertyNestingBuilder(this, nestingStep);
	}

	/**
	 * Returns a builder for nestings whose inner observable is a {@link LongProperty}. The created nestings depend on
	 * this builder's outer observable and nesting steps and adds the specified step as the next one.
	 *
	 * @param nestingStep
	 *            the function which performs the nesting step from one observable to the next
	 * @return a {@link LongPropertyNestingBuilder} which builds a nesting from this builder's settings and the
	 *         specified nesting steps
	 * @throws NullPointerException
	 *             if the specified function is null
	 */
	public LongPropertyNestingBuilder nestLongProperty(NestingStep<T, LongProperty> nestingStep) {
		Objects.requireNonNull(nestingStep, "The argument 'nestingStep' must not be null.");
		return new LongPropertyNestingBuilder(this, nestingStep);
	}

	/**
	 * Returns a builder for nestings whose inner observable is a {@link FloatProperty}. The created nestings depend on
	 * this builder's outer observable and nesting steps and adds the specified step as the next one.
	 *
	 * @param nestingStep
	 *            the function which performs the nesting step from one observable to the next
	 * @return a {@link FloatPropertyNestingBuilder} which builds a nesting from this builder's settings and the
	 *         specified nesting steps
	 * @throws NullPointerException
	 *             if the specified function is null
	 */
	public FloatPropertyNestingBuilder nestFloatProperty(NestingStep<T, FloatProperty> nestingStep) {
		Objects.requireNonNull(nestingStep, "The argument 'nestingStep' must not be null.");
		return new FloatPropertyNestingBuilder(this, nestingStep);
	}

	/**
	 * Returns a builder for nestings whose inner observable is a {@link DoubleProperty}. The created nestings depend on
	 * this builder's outer observable and nesting steps and adds the specified step as the next one.
	 *
	 * @param nestingStep
	 *            the function which performs the nesting step from one observable to the next
	 * @return a {@link DoublePropertyNestingBuilder} which builds a nesting from this builder's settings and the
	 *         specified nesting steps
	 * @throws NullPointerException
	 *             if the specified function is null
	 */
	public DoublePropertyNestingBuilder nestDoubleProperty(NestingStep<T, DoubleProperty> nestingStep) {
		Objects.requireNonNull(nestingStep, "The argument 'nestingStep' must not be null.");
		return new DoublePropertyNestingBuilder(this, nestingStep);
	}

	/**
	 * Returns a builder for nestings whose inner observable is a {@link StringProperty}. The created nestings depend on
	 * this builder's outer observable and nesting steps and adds the specified step as the next one.
	 *
	 * @param nestingStep
	 *            the function which performs the nesting step from one observable to the next
	 * @return a {@link StringPropertyNestingBuilder} which builds a nesting from this builder's settings and the
	 *         specified nesting steps
	 * @throws NullPointerException
	 *             if the specified function is null
	 */
	public StringPropertyNestingBuilder nestStringProperty(NestingStep<T, StringProperty> nestingStep) {
		Objects.requireNonNull(nestingStep, "The argument 'nestingStep' must not be null.");
		return new StringPropertyNestingBuilder(this, nestingStep);
	}

	//#end NEST

}
