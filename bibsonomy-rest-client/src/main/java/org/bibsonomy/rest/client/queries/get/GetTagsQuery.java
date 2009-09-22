/**
 *  
 *  BibSonomy-Rest-Client - The REST-client.
 *   
 *  Copyright (C) 2006 - 2008 Knowledge & Data Engineering Group, 
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *  
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.rest.client.queries.get;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.ResourceType;
import org.bibsonomy.model.Tag;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.renderer.RendererFactory;

/**
 * Use this Class to receive an ordered list of all posts.
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public final class GetTagsQuery extends AbstractQuery<List<Tag>> {

	private final int start;
	private final int end;
	private String filter = null;
	private GroupingEntity grouping = GroupingEntity.ALL;
	private String groupingValue;
	private ResourceType resourceType = ResourceType.ALL;
	public String url2;

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
		if (groupingValue == null || groupingValue.length() == 0) throw new IllegalArgumentException("no grouping value given");

		this.grouping = grouping;
		this.groupingValue = groupingValue;
	}
	
	/**
	 * Set the content type of this query, i.e. whether to retrieve only tags 
	 * beloning to bookmarks or bibtexs
	 * 
	 * @param contentType
	 */
	public void setResourceType(final ResourceType resourceType) {
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
	public List<Tag> getResult() throws BadRequestOrResponseException, IllegalStateException {
		if (this.downloadedDocument == null) throw new IllegalStateException("Execute the query first.");
		return RendererFactory.getRenderer(getRenderingFormat()).parseTagList(this.downloadedDocument);
	}

	@Override
	protected List<Tag> doExecute() throws ErrorPerformingRequestException {
		String url = URL_TAGS + "?start=" + this.start + "&end=" + this.end;

		switch (this.grouping) {
		case USER:
			url += "&user=" + this.groupingValue;
			break;
		case GROUP:
			url += "&group=" + this.groupingValue;
			break;
		case VIEWABLE:
			url += "&viewable=" + this.groupingValue;
			break;
		}

		if (this.filter != null && this.filter.length() > 0) {
			url += "&filter=" + this.filter;
		}
		
		if (! (this.resourceType == null && this.resourceType.equals(ResourceType.ALL)) ) {
			//FIXME: remove this workaround and fix the bug -> https://gforge.cs.uni-kassel.de/tracker/index.php?func=detail&aid=868&group_id=52&atid=278
			if(this.grouping != GroupingEntity.GROUP)
				url += "&resourcetype=" + this.resourceType.getLabel();
		}
		url2 = url + "&format=" + getRenderingFormat().toString().toLowerCase();
		this.downloadedDocument = performGetRequest(url + "&format=" + getRenderingFormat().toString().toLowerCase());
		return null;
	}
}