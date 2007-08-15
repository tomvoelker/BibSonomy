package org.bibsonomy.rest.client.queries.get;

import java.io.Reader;
import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
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
	public List<Post<? extends Resource>> getResult() throws BadRequestOrResponseException, IllegalStateException {
		if (this.downloadedDocument == null) throw new IllegalStateException("Execute the query first.");
		return RendererFactory.getRenderer(getRenderingFormat()).parsePostList(this.downloadedDocument);
	}

	@Override
	protected List<Post<? extends Resource>> doExecute() throws ErrorPerformingRequestException {
		String url = URL_POSTS + "/" + URL_POSTS_ADDED + "?start=" + this.start + "&end=" + this.end;

		if (this.resourceType != Resource.class) {
			url += "&resourcetype=" + this.resourceType.toString().toLowerCase();
		}

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

		this.downloadedDocument = performGetRequest(url + "&format=" + getRenderingFormat().toString().toLowerCase());
		return null;
	}
}