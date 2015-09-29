package org.bibsonomy.search.management;

import org.bibsonomy.model.Resource;
import org.bibsonomy.model.factories.ResourceFactory;

/**
 *
 * @author dzo
 * @param <R> 
 * @param <T> 
 * @param <I> 
 */
public abstract class SearchIndex<R extends Resource, T, I extends SearchIndex<R, T, I>> {
	private final SearchIndexContainer<R, T, I> container;
	@Deprecated // TODO: use resourceType of container TODODZO
	private final Class<R> resourceType;

	/**
	 * @param container
	 * @param resourceType 
	 */
	public SearchIndex(SearchIndexContainer<R, T, I> container, final Class<R> resourceType) {
		this.container = container;
		this.resourceType = resourceType;
	}

	/**
	 * @return the container
	 */
	public SearchIndexContainer<R, T, I> getContainer() {
		return this.container;
	}

	/**
	 * @return the resourceType
	 */
	public Class<R> getResourceType() {
		return this.resourceType;
	}
	
	/**
	 * @return the resource type as string
	 */
	public String getResourceTypeAsString() {
		return ResourceFactory.getResourceName(this.resourceType);
	}
}
