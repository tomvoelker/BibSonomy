package org.bibsonomy.model.logic.query.statistics.meta;

import java.util.Set;

import org.bibsonomy.model.Resource;
import org.bibsonomy.services.searcher.PostSearchQuery;
import org.bibsonomy.util.object.FieldDescriptor;

/**
 * query to get the distinct values indicated from a specific field
 *
 * @author dzo
 * @param <T>
 * @param <E>
 */
public class DistinctFieldQuery<T, E> implements MetaDataQuery<Set<E>> {

	private final FieldDescriptor<T, E> fieldDescriptor;
	private final Class<T> clazz;
	private final PostSearchQuery<? extends Resource> postQuery;

	/**
	 * default constructor
	 * @param clazz
	 * @param fieldGetter
	 */
	public DistinctFieldQuery(Class<T> clazz, FieldDescriptor<T, E> fieldGetter) {
		this.clazz = clazz;
		this.fieldDescriptor = fieldGetter;
		this.postQuery = null;
	}

	/**
	 * constructor for distinct field queries with an additional post query
	 * @param clazz
	 * @param fieldGetter
	 * @param postQuery
	 */
	public DistinctFieldQuery(Class<T> clazz, FieldDescriptor<T, E> fieldGetter, PostSearchQuery<?> postQuery) {
		this.clazz = clazz;
		this.fieldDescriptor = fieldGetter;
		this.postQuery = postQuery;
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

	/**
	 * @return the post query
	 */
	public PostSearchQuery<? extends Resource> getPostQuery() {
		return postQuery;
	}
}
