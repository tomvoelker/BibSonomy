package org.bibsonomy.rest.client.queries.get;

import java.io.Reader;
import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.exceptions.InternServerException;
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
public final class GetPostsQuery extends AbstractQuery<List<Post<? extends Resource>>> {

	private final int start;
	private final int end;
	private Reader downloadedDocument;
	private Class<? extends Resource> resourceType;
	private List<String> tags;
	private GroupingEntity grouping = GroupingEntity.ALL;
	private String groupingValue;
	private String resourceHash;

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

	@Override
	public List<Post<? extends Resource>> getResult() throws BadRequestOrResponseException, IllegalStateException {
		if (this.downloadedDocument == null) throw new IllegalStateException("Execute the query first.");
		try {
			return RendererFactory.getRenderer(getRenderingFormat()).parsePostList(this.downloadedDocument);
		} catch (final InternServerException ex) {
			throw new BadRequestOrResponseException(ex);
		}
	}

	@Override
	protected void doExecute() throws ErrorPerformingRequestException {
		String url = URL_POSTS + "?start=" + this.start + "&end=" + this.end;

		if (this.resourceType != Resource.class) {
			url += "&resourcetype=" + Resource.toString(this.resourceType).toLowerCase();
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

		if (this.tags != null && this.tags.size() > 0) {
			boolean first = true;
			for (final String tag : tags) {
				if (first) {
					url += "&tags=" + tag;
					first = false;
				} else {
					url += "+" + tag;
				}
			}
		}

		if (this.resourceHash != null && this.resourceHash.length() > 0) {
			url += "&resource=" + this.resourceHash;
		}
		this.downloadedDocument = performGetRequest(url + "&format=" + getRenderingFormat().toString().toLowerCase());
	}
}