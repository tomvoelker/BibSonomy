package org.bibsonomy.database.params;

import org.bibsonomy.common.enums.RatingAverage;
import org.bibsonomy.model.Resource;

/** 
 * Super class for parameter objects that are about resources.
 * 
 * @param <T> resource (e.g. Bookmark, Publication, etc.)
 * 
 * @author Jens Illig
 */
public class ResourceParam<T extends Resource> extends GenericParam {
	
	private RatingAverage ratingAverage = RatingAverage.ARITHMETIC_MEAN;

	protected T resource;
	protected String resourceType;
	/**
	 * @return the resourceType
	 */
	public String getResourceType() {
		return this.resourceType;
	}

	/**
	 * @param resourceType the resourceType to set
	 */
	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

	/**
	 * @param resource the resource to set
	 */
	public void setResource(final T resource) {
		this.resource = resource;
	}

	/**
	 * @return the resource
	 */
	public T getResource() {
		return resource;
	}
	
	/**
	 * @return the ratingAverage
	 */
	public RatingAverage getRatingAverage() {
		return this.ratingAverage;
	}

	/**
	 * @param ratingAverage the ratingAverage to set
	 */
	public void setRatingAverage(final RatingAverage ratingAverage) {
		this.ratingAverage = ratingAverage;
	}
}