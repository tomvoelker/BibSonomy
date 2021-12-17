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

	/**
	 * post search query to limit the distinct field values and counts to a search result
	 */
	private PostSearchQuery<? extends Resource> postQuery;

	/**
	 * the bucket size
	 */
	private int size = 10;

	/**
	 * default constructor
	 * @param clazz
	 * @param fieldGetter
	 */
	public DistinctFieldQuery(Class<T> clazz, FieldDescriptor<T, E> fieldGetter) {
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

	/**
	 * @return the post query
	 */
	public PostSearchQuery<? extends Resource> getPostQuery() {
		return postQuery;
	}

	/**
	 * @param postQuery the post search query to set
	 */
	public void setPostQuery(PostSearchQuery<? extends Resource> postQuery) {
		this.postQuery = postQuery;
	}

	/**
	 * @return the bucket size
	 */
	public int getSize() {
		return size;
	}

	/**
	 * @param size the bucket size to set
	 */
	public void setSize(int size) {
		this.size = size;
	}
}
