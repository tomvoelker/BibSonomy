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

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.util.data.NoDataAccessor;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;

/**
 * Use this Class to receive an ordered list of all posts.
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 */
public final class GetAddedPostsQuery extends AbstractQuery<List<Post<? extends Resource>>> {

	private final int start;
	private final int end;
	private Class<? extends Resource> resourceType;
	private GroupingEntity grouping = GroupingEntity.ALL;
	private String groupingValue;

	/**
	 * Gets bibsonomy's posts list.
	 */
	public GetAddedPostsQuery() {
		this(0, 19);
	}

	/**
	 * Gets bibsonomy's posts list ordered by adding date.
	 * 
	 * @param start
	 *            start of the list
	 * @param end
	 *            end of the list
	 */
	public GetAddedPostsQuery(int start, int end) {
		if (start < 0) start = 0;
		if (end < start) end = start;

		this.start = start;
		this.end = end;
	}

	/**
	 * Set the grouping used for this query. If {@link GroupingEntity#ALL} is
	 * chosen, the groupingValue isn't evaluated (-> it can be null or empty).
	 * 
	 * @param grouping
	 *            the grouping to use
	 * @param groupingValue
	 *            the value for the chosen grouping; for example the username if
	 *            grouping is {@link GroupingEntity#USER}
	 * @throws IllegalArgumentException
	 *             if grouping is != {@link GroupingEntity#ALL} and
	 *             groupingValue is null or empty
	 */
	public void setGrouping(final GroupingEntity grouping, final String groupingValue) throws IllegalArgumentException {
		if (grouping == GroupingEntity.ALL) {
			this.grouping = grouping;
			return;
		}
		if (groupingValue == null || groupingValue.length() == 0) throw new IllegalArgumentException("no grouping value given");

		this.grouping = grouping;
		this.groupingValue = groupingValue;
	}

	/**
	 * set the resource type of the resources of the posts
	 * 
	 * @param type
	 *            the type to set
	 */
	public void setResourceType(final Class<? extends Resource> type) {
		this.resourceType = type;
	}

	@Override
	protected List<Post<? extends Resource>> getResultInternal() throws BadRequestOrResponseException, IllegalStateException {
		return getRenderer().parsePostList(this.downloadedDocument, NoDataAccessor.getInstance());
	}

	@Override
	protected void doExecute() throws ErrorPerformingRequestException {
		final String url = this.getUrlRenderer().createHrefForAddedPosts(this.grouping, this.groupingValue, this.resourceType, this.start, this.end);
		this.downloadedDocument = performGetRequest(url);
	}
}