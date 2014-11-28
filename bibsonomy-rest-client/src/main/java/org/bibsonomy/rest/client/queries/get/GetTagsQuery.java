/**
 * BibSonomy-Rest-Client - The REST-client.
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
package org.bibsonomy.rest.client.queries.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.model.factories.ResourceFactory;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;

/**
 * Use this Class to receive an ordered list of all posts.
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 */
public final class GetTagsQuery extends AbstractQuery<List<Tag>> {

	private final int start;
	private final int end;
	private String filter = null;
	private Order order = null;
	private GroupingEntity grouping = GroupingEntity.ALL;
	private String groupingValue;
	private Class<? extends Resource> resourceType = Resource.class;

	/**
	 * Gets bibsonomy's tags list
	 */
	public GetTagsQuery() {
		this(0, 19);
	}

	/**
	 * Gets bibsonomy's tags list.
	 * 
	 * @param start
	 *            start of the list
	 * @param end
	 *            end of the list
	 */
	public GetTagsQuery(int start, int end) {
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
	 */
	public void setGrouping(final GroupingEntity grouping, final String groupingValue) {
		if (grouping == GroupingEntity.ALL) {
			this.grouping = grouping;
			return;
		}
		if (!present(groupingValue)) throw new IllegalArgumentException("no grouping value given");

		this.grouping = grouping;
		this.groupingValue = groupingValue;
	}
	
	/**
	 * @param order the order to set
	 */
	public void setOrder(final Order order) {
		this.order = order;
	}
	
	/**
	 * TODO: change to Class<? extends Resource> and reuse methods of the {@link ResourceFactory}
	 * Be careful with 'bibtex' (ensure on rest server that {@link ResourceFactory} is used too)
	 * 
	 * Set the content type of this query, i.e. whether to retrieve only tags 
	 * beloning to bookmarks or bibtexs
	 * 
	 * @param resourceType
	 */
	public void setResourceType(final Class<? extends Resource> resourceType) {
		this.resourceType = resourceType;
	}

	/**
	 * @param filter
	 *            The filter to set.
	 */
	public void setFilter(final String filter) {
		this.filter = filter;
	}

	@Override
	protected List<Tag> getResultInternal() throws BadRequestOrResponseException, IllegalStateException {
		return this.getRenderer().parseTagList(this.downloadedDocument);
	}

	@Override
	protected void doExecute() throws ErrorPerformingRequestException {
		final String url = this.getUrlRenderer().createHrefForTags(this.resourceType, null, this.grouping, this.groupingValue, this.filter, null, this.order, this.start, this.end);
		this.downloadedDocument = performGetRequest(url);
	}
}