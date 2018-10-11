package org.bibsonomy.search.util;

/**
 * the generic converter interface
 * @param <S> the source
 * @param <T> the target
 * @param <O> the option
 */
public interface Converter<S, T, O> {

	/**
	 * convert the our model to the model required by the resource search
	 * @param source
	 * @return the converted model
	 */
	T convert(final S source);

	/**
	 *
	 * @param source
	 * @param options the options to convert this object
	 * @return the model object
	 */
	public S convert(final T source, final O options);
}
