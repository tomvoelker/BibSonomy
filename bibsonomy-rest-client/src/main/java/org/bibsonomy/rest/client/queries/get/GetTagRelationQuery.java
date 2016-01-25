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

import java.util.Arrays;
import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.TagRelation;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;

/**
 * @author niebler
 */
public final class GetTagRelationQuery extends AbstractQuery<List<Tag>> {
	
	private final TagRelation relation;
	private final List<String> tagNames;
	private final int start;
	private final int end;
	private final String filter = null;
	private final Order order = null;
	private final GroupingEntity grouping = GroupingEntity.ALL;
	private String groupingValue;
	private final Class<? extends Resource> resourceType = Resource.class;

	/**
	 * Constructs a query for the 20 most related tags to "myown".
	 */
	public GetTagRelationQuery() {
		this(0, 19, TagRelation.RELATED, Arrays.asList("myown"));
	}
	
	/**
	 * Constructs a query for a number of tags, according to a defined relation.
	 * 
	 * @param start
	 *            the start of a segment of the list of tags.
	 * @param end
	 *            the end of a segment of the list of tags.
	 * @param relation
	 *            A relation between tags
	 * @param tagNames
	 *            a list of tags, for which the related tags are to be queried.
	 */
	public GetTagRelationQuery(final int start, final int end, final TagRelation relation, final List<String> tagNames) {
		this.start = start < 0 ? 0 : start;
		this.end = end < start ? start : end;
		this.relation = relation == null ? TagRelation.RELATED : relation;
		this.tagNames = tagNames;
	}
	
	@Override
	protected void doExecute() throws ErrorPerformingRequestException {
		final String tagUrl = this.getUrlRenderer().createHrefForTags(this.resourceType, this.tagNames, this.grouping, this.groupingValue, this.filter, this.relation, this.order, this.start, this.end);
		this.downloadedDocument = this.performGetRequest(tagUrl);
	}
	
	@Override
	protected final List<Tag> getResultInternal() {
		return this.getRenderer().parseTagList(this.downloadedDocument);
	}

}
