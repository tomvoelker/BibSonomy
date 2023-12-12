package org.bibsonomy.model.logic.query.statistics.meta;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;
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
@Getter
public class DistinctFieldQuery<T, E> implements MetaDataQuery<Set<E>> {

	private final FieldDescriptor<T, E> fieldDescriptor;
	private final Class<T> clazz;

	/**
	 * post search query to limit the distinct field values and counts to a search result
	 */
	@Setter
	private PostSearchQuery<? extends Resource> postQuery;

	/**
	 * the bucket size
	 */
	@Setter
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
	
}
