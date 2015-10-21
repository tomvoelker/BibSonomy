package org.bibsonomy.search.management;

import org.bibsonomy.model.Resource;

/**
 *
 * @author dzo
 * @param <R> 
 * @param <T> 
 * @param <I> 
 */
public abstract class SearchIndex<R extends Resource, T, I extends SearchIndex<R, T, I>> {
	private final SearchIndexContainer<R, T, I> container;

	/**
	 * @param container
	 * @param resourceType 
	 */
	public SearchIndex(SearchIndexContainer<R, T, I> container) {
		this.container = container;
	}

	/**
	 * @return the container
	 */
	public SearchIndexContainer<R, T, I> getContainer() {
		return this.container;
	}
}
