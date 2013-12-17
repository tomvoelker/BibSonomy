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

import java.util.Arrays;
import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.TagRelation;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.model.factories.ResourceFactory;
import org.bibsonomy.rest.RESTConfig;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.util.UrlBuilder;

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

	public GetTagRelationQuery() {
		this(0, 19, TagRelation.RELATED, Arrays.asList("myown"));
	}
	
	public GetTagRelationQuery(final int start, final int end, final TagRelation relation, final List<String> tagNames) {
		this.start = start < 0 ? 0 : start;
		this.end = end < start ? start : end;
		this.relation = relation == null ? TagRelation.RELATED : relation;
		this.tagNames = tagNames;
	}
	
	@Override
	protected List<Tag> doExecute() throws ErrorPerformingRequestException {
		// /tags/[tags]?...
		final UrlBuilder urlBuilder = new UrlBuilder(RESTConfig.TAGS_URL);
		urlBuilder.addPathElement(StringUtils.implodeStringCollection(this.tagNames, "+"));
		urlBuilder.addParameter(RESTConfig.START_PARAM, Integer.toString(this.start));
		urlBuilder.addParameter(RESTConfig.END_PARAM, Integer.toString(this.end));
		
		if (this.order != null) {
			urlBuilder.addParameter(RESTConfig.ORDER_PARAM, this.order.toString());
		}
		AbstractQuery.addGroupingParam(this.grouping, this.groupingValue, urlBuilder);

		if (present(this.filter)) {
			urlBuilder.addParameter(RESTConfig.FILTER_PARAM, this.filter);
		}
		
		if ((this.resourceType != null) && (this.resourceType != Resource.class)) {
			urlBuilder.addParameter(RESTConfig.RESOURCE_TYPE_PARAM, ResourceFactory.getResourceName(this.resourceType));
		}
		
		// add relation parameter.
		urlBuilder.addParameter(RESTConfig.RELATION_PARAM, this.relation.toString());
		this.downloadedDocument = this.performGetRequest(urlBuilder.asString());
		
		return null;
	}
	
	@Override
	public final List<Tag> getResult() {
		if (this.downloadedDocument == null) {
			throw new IllegalStateException("Execute the query first.");
		}
		return this.getRenderer().parseTagList(this.downloadedDocument);
	}

}
