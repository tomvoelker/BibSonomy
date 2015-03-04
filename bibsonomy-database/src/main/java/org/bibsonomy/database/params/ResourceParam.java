/**
 * BibSonomy-Database - Database for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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