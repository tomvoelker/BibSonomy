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
package org.bibsonomy.rest.client.queries.get;

import java.util.List;

import org.bibsonomy.common.enums.ConceptStatus;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;

/**
 * Use this Class to get concepts 
 * 1) from all users
 * 2) from a specified group or
 * 3) from a specified user
 * 
 * @author Stefan Stützer
 */
public class GetConceptQuery extends AbstractQuery<List<Tag>> {
	private Class<? extends Resource> resourceType;
	private String groupingName;
	private ConceptStatus status = ConceptStatus.ALL;
	private String regex;
	private GroupingEntity grouping = GroupingEntity.ALL;
	private List<String> tags;
	
	@Override
	protected void doExecute() throws ErrorPerformingRequestException {
		final String conceptsUrl = this.getUrlRenderer().createHrefForConcepts(this.grouping, this.groupingName, this.status, this.resourceType, this.tags, this.regex);		
		this.downloadedDocument = performGetRequest(conceptsUrl);
	}

	@Override
	protected List<Tag> getResultInternal() throws BadRequestOrResponseException, IllegalStateException {
		return this.getRenderer().parseTagList(this.downloadedDocument);
	}
	
	/**
	 * sets the user name and the corresponding grouping
	 * @param userName
	 */
	public void setUserName(final String userName) {
		this.groupingName = userName;
		this.grouping = GroupingEntity.USER;
	}
	
	/**
	 * sets the group name and the corresponding grouping
	 * @param groupName
	 */
	public void setGroupName(final String groupName) {
		this.groupingName = groupName;
		this.grouping = GroupingEntity.GROUP;
	}
	
	/**
	 * @param resourceType the resourceType to set
	 */
	public void setResourceType(final Class<? extends Resource> resourceType) {
		this.resourceType = resourceType;
	}
	
	/**
	 * @param status the status to set
	 */
	public void setStatus(final ConceptStatus status) {
		this.status = status;
	}

	/**
	 * @param regex the regex to set
	 */
	public void setRegex(final String regex) {
		this.regex = regex;
	}
	
	/**
	 * @param tags the tags to set
	 */
	public void setTags(final List<String> tags) {
		this.tags = tags;
	}
}