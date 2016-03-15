/**
 * BibSonomy-Rest-Client - The REST-client.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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
package org.bibsonomy.rest.client.queries.delete;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.rest.client.AbstractDeleteQuery;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;

/**
 * Use this query to delete a concept or a single relation.
 * 
 * @author Stefan Stützer
 */
public class DeleteConceptQuery extends AbstractDeleteQuery {

	private final String conceptName;
	private final GroupingEntity grouping;
	private final String groupingName;
	
	/** if is set only the relation <em>conceptName <- subTag </em> will be deleted */
	private String subTag;
	
	/**
	 * 
	 * @param conceptName
	 * @param grouping
	 * @param groupingName
	 */
	public DeleteConceptQuery(final String conceptName, final GroupingEntity grouping, final String groupingName) {
		this.conceptName = conceptName;
		this.grouping = grouping;
		this.groupingName = groupingName;
		this.downloadedDocument = null;
		if (GroupingEntity.ALL.equals(this.grouping)) {
			throw new IllegalArgumentException("you can not delete global concepts");
		}
	}
	
	@Override
	protected void doExecute() throws ErrorPerformingRequestException {
		final String url = this.getUrlRenderer().createHrefForConceptWithSubTag(this.grouping, this.groupingName, this.conceptName, this.subTag);
		this.downloadedDocument = performRequest(HttpMethod.DELETE, url, null);
	}

	/**
	 * @param subTag the subTag to set
	 */
	public void setSubTag(final String subTag) {
		this.subTag = subTag;
	}
}