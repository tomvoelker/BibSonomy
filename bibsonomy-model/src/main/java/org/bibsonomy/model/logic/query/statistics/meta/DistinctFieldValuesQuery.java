package org.bibsonomy.model.logic.query.statistics.meta;

import java.util.Set;
import java.util.function.Function;

/**
 * query to get the distinct values indicated from a specific field
 *
 * @author dzo
 * @param <T>
 * @param <E>
 */
public class DistinctFieldValuesQuery<T, E> implements MetaDataQuery<Set<E>> {

	private final Function<T, E> fieldGetter;
	private final Class<T> clazz;

	/**
	 * default constructor
	 * @param clazz
	 * @param fieldGetter
	 */
	public DistinctFieldValuesQuery(Class<T> clazz, Function<T, E> fieldGetter) {
		this.clazz = clazz;
		this.fieldGetter = fieldGetter;
	}

	/**
	 * @return the fieldGetter
	 */
	public Function<T, E> getFieldGetter() {
		return fieldGetter;
	}

	/**
	 * @return the clazz
	 */
	public Class<T> getClazz() {
		return clazz;
	}
}
