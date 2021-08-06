/**
 * BibSonomy-Model - Java- and JAXB-Model.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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
package org.bibsonomy.model;

import java.util.LinkedList;

/**
 * Extended List with additional properties
 *
 * like the total count, that is returned by the search engine while
 * retrieving objects
 * 
 * @param <T> resource type
 */
public class ResultList<T> extends LinkedList<T> {
	private static final long serialVersionUID = -5889003340930421319L;

	/**
	 * number of total hits in ResultSet
	 */
	private int totalCount;

	/**
	 * the pagination limit of this list
	 */
	private Integer paginationLimit;

	/**
	 * @return the totalCount
	 */
	public int getTotalCount() {
		return this.totalCount;
	}

	/**
	 * @param totalCount the totalCount to set
	 */
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	/**
	 * @return the paginationLimit
	 */
	public Integer getPaginationLimit() {
		return paginationLimit;
	}

	/**
	 * @param paginationLimit the paginationLimit to set
	 */
	public void setPaginationLimit(Integer paginationLimit) {
		this.paginationLimit = paginationLimit;
	}
}