package org.bibsonomy.search.management;

import org.bibsonomy.model.Resource;

/**
 *
 * @author dzo
 * @param <R> 
 * @param <T> 
 * @param <I> 
 * @param <M> 
 */
public abstract class SearchIndex<R extends Resource, T, I extends SearchIndex<R, T, I, M>, M> {
	private final SearchIndexContainer<R, T, I, M> container;

	/**
	 * @param container
	 * @param resourceType 
	 */
	public SearchIndex(SearchIndexContainer<R, T, I, M> container) {
		this.container = container;
	}

	/**
	 * @return the container
	 */
	public SearchIndexContainer<R, T, I, M> getContainer() {
		return this.container;
	}
}
