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
import org.bibsonomy.model.util.ResourceUtils;
import org.bibsonomy.rest.RESTConfig;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;
import org.bibsonomy.util.StringUtils;

/**
 * @author niebler
 * @version $Id$
 */
public final class GetTagRelationQuery extends AbstractQuery<List<Tag>> {
	
	private final TagRelation relation;
	private final List<String> tagNames;
	private final int start;
	private final int end;
	private String filter = null;
	private Order order = null;
	private GroupingEntity grouping = GroupingEntity.ALL;
	private String groupingValue;
	private Class<? extends Resource> resourceType = Resource.class;

	public GetTagRelationQuery() {
		this(0, 19, TagRelation.RELATED, Arrays.asList("myown"));
	}
	
	public GetTagRelationQuery(int start, int end, TagRelation relation, List<String> tagNames) {
		this.start = start < 0 ? 0 : start;
		this.end = end < start ? start : end;
		this.relation = relation == null ? TagRelation.RELATED : relation;
		this.tagNames = tagNames;
	}
	
	@Override
	protected List<Tag> doExecute() throws ErrorPerformingRequestException {
		// /tags/[tags]?...
		String url = URL_TAGS + "/" + StringUtils.implodeStringCollection(tagNames, "+")
				+ "?" + RESTConfig.START_PARAM + "=" + this.start
				+ "&" + RESTConfig.END_PARAM + "=" + this.end;
		
		if (order != null) {
			url += "&" + RESTConfig.ORDER_PARAM + "=" + this.order;
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

		if (present(this.filter)) {
			url += "&" + RESTConfig.FILTER_PARAM + "=" + this.filter;
		}
		
		if (this.resourceType != null && !Resource.class.equals(this.resourceType)) {
			url += "&" + RESTConfig.RESOURCE_TYPE_PARAM + "=" + ResourceUtils.toString(this.resourceType);
		}
		
		// add relation parameter.
		url += "&" + RESTConfig.RELATION_PARAM + "=" + this.relation;
		
		this.downloadedDocument = performGetRequest(url);
		
		return null;
	}
	
	@Override
	public final List<Tag> getResult() {
		if (this.downloadedDocument == null) throw new IllegalStateException("Execute the query first.");
		return this.getRenderer().parseTagList(this.downloadedDocument);
	}

}
