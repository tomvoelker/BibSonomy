/**
 * BibSonomy-Rest-Client - The REST-client.
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
package org.bibsonomy.rest.client.queries.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.model.Group;
import org.bibsonomy.model.logic.query.GroupQuery;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;
import org.bibsonomy.util.UrlBuilder;

/**
 * query to retrieve groups from the server
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 */
public final class GetGroupListQuery extends AbstractQuery<List<Group>> {

	private final GroupQuery groupQuery;

	/**
	 * inits this group list query with the specified query
	 * 
	 * @param groupQuery
	 */
	public GetGroupListQuery(final GroupQuery groupQuery) {
		if (!present(groupQuery)) {
			throw new IllegalArgumentException("No group query given.");
		}

		this.groupQuery = groupQuery;
	}

	@Override
	protected List<Group> getResultInternal() throws BadRequestOrResponseException, IllegalStateException {
		return this.getRenderer().parseGroupList(this.downloadedDocument);
	}

	@Override
	protected void doExecute() throws ErrorPerformingRequestException {
		final UrlBuilder groupsUrlBuilder = this.getUrlRenderer().createUrlBuilderForGroups(this.groupQuery);
		this.downloadedDocument = performGetRequest(groupsUrlBuilder.asString());
	}
}