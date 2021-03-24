package org.bibsonomy.model.logic.query.statistics.meta;

import java.util.Set;

import org.bibsonomy.util.object.FieldDescriptor;

/**
 * query to get the distinct values indicated from a specific field
 *
 * @author dzo
 * @param <T>
 * @param <E>
 */
public class DistinctFieldValuesQuery<T, E> implements MetaDataQuery<Set<E>> {

	private final FieldDescriptor<T, E> fieldDescriptor;
	private final Class<T> clazz;

	/**
	 * default constructor
	 * @param clazz
	 * @param fieldGetter
	 */
	public DistinctFieldValuesQuery(Class<T> clazz, FieldDescriptor<T, E> fieldGetter) {
		this.clazz = clazz;
		this.fieldDescriptor = fieldGetter;
	}

	/**
	 * @return the fieldDescriptor
	 */
	public FieldDescriptor<T, E> getFieldDescriptor() {
		return fieldDescriptor;
	}

	/**
	 * @return the clazz
	 */
	public Class<T> getClazz() {
		return clazz;
	}
}
