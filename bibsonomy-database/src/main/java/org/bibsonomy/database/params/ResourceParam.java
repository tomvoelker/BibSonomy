package org.bibsonomy.database.params;

import org.bibsonomy.common.enums.RatingAverage;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.enums.GoldStandardRelation;

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
	protected GoldStandardRelation relation;

	/**
	 * @return the relation between the posts
	 */
	public GoldStandardRelation getRelation() {
		return relation;
	}
	
	/**
	 * @param relation the relation between the posts
	 */
	public void setRelation(final GoldStandardRelation relation){
		this.relation = relation;
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