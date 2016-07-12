/**
 * BibSonomy-Rest-Client - The REST-client.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
package org.bibsonomy.rest.client.queries.get;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Tag;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;

/**
 * Use this Class to get information about the specified concept
 * 
 * @author Stefan Stützer
 */
public class GetConceptDetailsQuery extends AbstractQuery<Tag> {

	private final String conceptName;
	private String groupingName;
	private GroupingEntity grouping = GroupingEntity.ALL;
	
	/**
	 * @param conceptName
	 */
	public GetConceptDetailsQuery(final String conceptName) {
		this.conceptName = conceptName;
	}
	
	@Override
	protected void doExecute() throws ErrorPerformingRequestException {
		final String conceptUrl = this.getUrlRenderer().createHrefForConceptWithSubTag(this.grouping, this.groupingName, this.conceptName, null);
		this.downloadedDocument = performGetRequest(conceptUrl);
	}

	@Override
	protected Tag getResultInternal() throws BadRequestOrResponseException, IllegalStateException {
		return this.getRenderer().parseTag(this.downloadedDocument);
	}
	
	/**
	 * sets an optional grouping. if grouping is null, the GroupingEntity is
	 * defaulted to GroupingEntity.ALL.
	 * @param groupingName 
	 * @param grouping 
	 */
	public void setGrouping(final GroupingEntity grouping, final String groupingName) {
		this.groupingName = groupingName;
		
		if (grouping != null)
			this.grouping = grouping;
		else
			this.grouping = GroupingEntity.ALL;
	}

	/**
	 * sets the userName and the corresponding groupings
	 * @param userName
	 */
	@Deprecated
	public void setUserName(final String userName) {
		this.setGrouping(GroupingEntity.USER, userName);
	}
	
	/**
	 * sets the groupName and the corresponding grouping
	 * @param groupName
	 */
	@Deprecated
	public void setGroupName(final String groupName) {
		this.setGrouping(GroupingEntity.GROUP, groupName);
	}
	
}