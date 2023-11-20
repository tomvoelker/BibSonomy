/**
 * BibSonomy-Database - Database for BibSonomy.
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
package org.bibsonomy.database.params.relations;

import org.bibsonomy.common.enums.RatingAverage;
import org.bibsonomy.util.ValidationUtils;

/**
 * An object holding the parameters for querying relations for a single person.
 *
 * @author ada
 */
public class GetPersonRelations {

	private String personId;
	private Integer limit;
	private Integer offset;
	private RatingAverage ratingAverage;

	/**
	 * Configures a query that returns <code>limit</code> relations beginning at <code>offset</code>.
	 * If a <code>limit</code> and <code>offset</code> are <code>null</code> all relations will be returned.
	 *
	 * <code>limit</code> and <code>offset</code> must either both be set, or both be absent.
	 *
	 * @param personId      a person id.
	 * @param limit         the number of relations that will be retrieved.
	 * @param offset        the index of the first relation in the result set that will be retrieved.
	 * @param ratingAverage the algorithm used to determine the average rating.
	 */
	public GetPersonRelations(String personId, Integer limit, Integer offset, RatingAverage ratingAverage) {
		ValidationUtils.assertNotNull(personId);

		if ((limit == null && offset != null) || (limit != null && offset == null)) {
			throw new IllegalArgumentException("limit and offset must both be set or both be absent.");
		}

		this.personId = personId;
		this.limit = limit;
		this.offset = offset;
		this.ratingAverage = ratingAverage;
	}


	/**
	 * Configures a query that returns <code>limit</code> relations beginning at <code>offset</code>.
	 * If a <code>limit</code> and <code>offset</code> are <code>null</code> all relations will be returned.
	 * <p>
	 * Sets <code>ratingAverage</code> to the default value {@link RatingAverage#ARITHMETIC_MEAN}.
	 *
	 * <code>limit</code> and <code>offset</code> must either both be set, or both be absent.
	 *
	 * @param personId a person id.
	 * @param limit    the number of relations that will be retrieved.
	 * @param offset   the index of the first relation in the result set that will be retrieved.
	 */
	public GetPersonRelations(String personId, Integer limit, Integer offset) {
		this(personId, limit, offset, RatingAverage.ARITHMETIC_MEAN);
	}


	/**
	 * Configures a query that returns all relations for a given <code>personId</code>.
	 * <p>
	 * Sets <code>ratingAverage</code> to the default value {@link RatingAverage#ARITHMETIC_MEAN}.
	 *
	 * @param personId a person id.
	 */
	public GetPersonRelations(String personId) {
		this(personId, null, null);
	}

	/**
	 * @return the personId
	 */
	public String getPersonId() {
		return personId;
	}

	/**
	 * @return the limit
	 */
	public Integer getLimit() {
		return limit;
	}

	/**
	 * @return the offset
	 */
	public Integer getOffset() {
		return offset;
	}
}
