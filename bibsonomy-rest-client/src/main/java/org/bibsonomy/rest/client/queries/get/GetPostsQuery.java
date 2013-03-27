/**
 *
 *  BibSonomy-Rest-Client - The REST-client.
 *
 *  Copyright (C) 2006 - 2013 Knowledge & Data Engineering Group,
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

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.model.factories.ResourceFactory;
import org.bibsonomy.rest.RESTConfig;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;
import org.bibsonomy.util.UrlBuilder;

/**
 * Use this Class to receive an ordered list of all posts.
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public final class GetPostsQuery extends AbstractQuery<List<Post<? extends Resource>>> {
	private static final Log log = LogFactory.getLog(GetPostsQuery.class);

	private final int start;
	private final int end;
	private Order order;
	private String search;
	private Class<? extends Resource> resourceType;
	private List<String> tags;
	private GroupingEntity grouping = GroupingEntity.ALL;
	private String groupingValue;
	private String resourceHash;
	private String userName;

	/**
	 * Gets bibsonomy's posts list.
	 */
	public GetPostsQuery() {
		this(0, 19);
	}

	/**
	 * Gets bibsonomy's posts list.
	 * 
	 * @param start
	 *            start of the list
	 * @param end
	 *            end of the list
	 */
	public GetPostsQuery(int start, int end) {
		if (start < 0) {
			start = 0;
		}
		if (end < start) {
			end = start;
		}

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
		if (groupingValue == null || groupingValue.length() == 0) {
			throw new IllegalArgumentException("no grouping value given");
		}

		this.grouping = grouping;
		this.groupingValue = groupingValue;
	}

	/**
	 * set the resource type of the resources of the posts.
	 * 
	 * @param type
	 *            the type to set
	 */
	public void setResourceType(final Class<? extends Resource> type) {
		this.resourceType = type;
	}

	/**
	 * @param resourceHash
	 *            The resourceHash to set.
	 */
	public void setResourceHash(final String resourceHash) {
		this.resourceHash = resourceHash;
	}

	/**
	 * @param tags
	 *            the tags to set
	 */
	public void setTags(final List<String> tags) {
		this.tags = tags;
	}

	/**
	 * @param order
	 *            the order to set
	 */
	public void setOrder(final Order order) {
		this.order = order;
	}

	/**
	 * @param search
	 *            the search string to set
	 */
	public void setSearch(final String search) {
		// urlencode is done later
		this.search = search.replace(" ", "+");
	}

	@Override
	public List<Post<? extends Resource>> getResult() throws BadRequestOrResponseException, IllegalStateException {
		if (this.downloadedDocument == null) {
			throw new IllegalStateException("Execute the query first.");
		}
		try {
			return this.getRenderer().parsePostList(this.downloadedDocument);
		} catch (final InternServerException ex) {
			throw new BadRequestOrResponseException(ex);
		}
	}

	@Override
	protected List<Post<? extends Resource>> doExecute() throws ErrorPerformingRequestException {
		if (GroupingEntity.CLIPBOARD.equals(this.grouping)) {
			this.downloadedDocument = performGetRequest(RESTConfig.USERS_URL + "/" + userName + "/" + RESTConfig.CLIPBOARD_SUBSTRING);
			return null;
		}
		
		final UrlBuilder urlBuilder = new UrlBuilder(RESTConfig.POSTS_URL);
		urlBuilder.addParameter(RESTConfig.START_PARAM, Integer.toString(this.start));
		urlBuilder.addParameter(RESTConfig.END_PARAM, Integer.toString(this.end));

		if (this.resourceType != Resource.class) {
			urlBuilder.addParameter(RESTConfig.RESOURCE_TYPE_PARAM, ResourceFactory.getResourceName(this.resourceType));
		}

		final String groupingParameterName = getGroupingParameterName();
		if (groupingParameterName != null) {
			urlBuilder.addParameter(groupingParameterName, this.groupingValue);
		}

		if (present(this.tags)) {
			StringBuilder tagsStringBuilder = new StringBuilder();
			for (final String tag : tags) {
				tagsStringBuilder.append(tag).append(' ');
			}
			tagsStringBuilder.setLength(tagsStringBuilder.length() - 1);
			urlBuilder.addParameter(RESTConfig.TAGS_PARAM, tagsStringBuilder.toString());
		}

		if (this.resourceHash != null && this.resourceHash.length() > 0) {
			urlBuilder.addParameter(RESTConfig.RESOURCE_PARAM, this.resourceHash);
		}

		if (this.order != null) {
			urlBuilder.addParameter(RESTConfig.ORDER_PARAM, this.order.toString());
		}

		if (present(this.search)) {
			urlBuilder.addParameter(RESTConfig.SEARCH_PARAM, this.search);
		}
		String url = urlBuilder.toString();
		if (log.isDebugEnabled()) {
			log.debug("GetPostsQuery doExecute() called - URL: " + url);
		}
		this.downloadedDocument = performGetRequest(url);

		return null;
	}

	public String getGroupingParameterName() {
		String groupingParameterName;
		switch (this.grouping) {
		case USER:
			groupingParameterName = "user";
			break;
		case GROUP:
			groupingParameterName = "group";
			break;
		case VIEWABLE:
			groupingParameterName = "viewable";
			break;
		case ALL:
			groupingParameterName = null;
			break;
		case FRIEND:
			groupingParameterName = "friend";
			break;
		// CLIPBOARD is already handled separately and therefore not covered here
		default:
			throw new UnsupportedOperationException("The grouping " + this.grouping + " is currently not supported by this query.");
		}
		return groupingParameterName;
	}

	/**
	 * @param userName
	 *            the userName to set
	 */
	public void setUserName(final String userName) {
		this.userName = userName;
	}
}