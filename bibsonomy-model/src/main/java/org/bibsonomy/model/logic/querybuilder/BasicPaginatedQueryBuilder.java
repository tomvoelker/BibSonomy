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
package org.bibsonomy.model.logic.querybuilder;

import org.bibsonomy.model.logic.query.BasicPaginatedQuery;

/**
 * abstract builder for {@link BasicPaginatedQuery}
 *
 * @author dzo
 */
public abstract class BasicPaginatedQueryBuilder<B extends BasicPaginatedQueryBuilder<B>> {
	protected int start = 0;
	protected int end = 10;

	/**
	 * @param start the start index of the list
	 * @return the builder
	 */
	public B start(final int start) {
		if (start < 0) {
			throw new IllegalArgumentException(String.format("end must be >=0, was %d", start));
		}
		this.start = start;
		return this.builder();
	}

	/**
	 * @param end the end index of the list
	 * @return the builder
	 */
	public B end(final int end) {
		if (end < 0) {
			throw new IllegalArgumentException(String.format("end must be >=0, was %d", end));
		}
		this.end = end;
		return this.builder();
	}

	/**
	 * Retrieve only resources from [<code>start</code>; <code>end</code>).
	 *
	 * @param start index of the first item.
	 * @param end index of the last item.
	 *
	 * @return the builder.
	 */
	public B fromTo(int start, int end) {
		if (start > end) {
			throw new IllegalArgumentException(String.format("start must be <= end: %d > %d", start, end));
		}

		this.start(start);
		return this.end(end);
	}

	/**
	 * @param entries the number of entries to retrieve
	 * @param start the start index
	 * @return the builder
	 */
	public B entriesStartingAt(final int entries, final int start) {
		this.start(start);

		if (entries < 0) {
			throw new IllegalArgumentException(String.format("number of entries must be >= 0, was %d", entries));
		}

		return this.end(start + entries);
	}

	protected abstract B builder();
}
