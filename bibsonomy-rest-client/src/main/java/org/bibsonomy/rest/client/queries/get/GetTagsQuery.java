package org.bibsonomy.rest.client.queries.get;

import java.io.Reader;
import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
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
		this.downloadedDocument = performGetRequest(url + "&format=" + getRenderingFormat().toString().toLowerCase());
		return null;
	}
}