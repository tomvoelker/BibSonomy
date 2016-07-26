/**
 * BibSonomy-Rest-Server - The REST-server.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.rest.strategy.posts;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.Writer;
import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.SortKey;
import org.bibsonomy.common.enums.SortOrder;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.model.factories.ResourceFactory;
import org.bibsonomy.rest.RESTConfig;
import org.bibsonomy.rest.RESTUtils;
import org.bibsonomy.rest.strategy.AbstractGetListStrategy;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.util.SortUtils;

/**
 * @author Jens Illig
 */
public abstract class AbstractListOfPostsStrategy extends AbstractGetListStrategy<List<? extends Post<? extends Resource>>> {
	protected final Class<? extends Resource> resourceType;
	protected final String hash;
	protected final GroupingEntity grouping;
	protected final String groupingValue;
	protected final String tagString;
	protected final List<String> tags;
	protected final String search;
	protected final Order order;
	protected final List<SortKey> sortKeys;
	protected final List<SortOrder> sortOrders;
	
	/**
	 * @param context
	 */
	public AbstractListOfPostsStrategy(final Context context) {
		super(context);
		this.tagString = context.getStringAttribute(RESTConfig.TAGS_PARAM, null);
		this.resourceType = ResourceFactory.getResourceClass(context.getStringAttribute(RESTConfig.RESOURCE_TYPE_PARAM, ResourceFactory.RESOURCE_CLASS_NAME));
		this.hash = context.getStringAttribute(RESTConfig.RESOURCE_PARAM, null);
		this.search = context.getStringAttribute(RESTConfig.SEARCH_PARAM, null);
		this.order = context.getEnumAttribute(RESTConfig.ORDER_PARAM, Order.class, null);
		this.sortKeys = SortUtils.parseSortKeys(context.getStringAttribute(RESTConfig.SORTKEY_PARAM, null));
		this.sortOrders = SortUtils.parseSortOrders(context.getStringAttribute(RESTConfig.SORTORDER_PARAM, null));
		this.grouping = this.chooseGroupingEntity();
		this.tags = context.getTags(RESTConfig.TAGS_PARAM);
		String groupingValue;
		if (this.grouping != GroupingEntity.ALL) {
			groupingValue = context.getStringAttribute(this.grouping.toString().toLowerCase(), null);
			if (this.grouping == GroupingEntity.USER) {
				groupingValue = RESTUtils.normalizeUser(groupingValue, context);
			}
		} else {
			groupingValue = null;
		}
		
		this.groupingValue = groupingValue;
	}

	@Override
	protected void render(final Writer writer, final List<? extends Post<? extends Resource>> resultList) {
		this.getRenderer().serializePosts(writer, resultList, this.getView());
	}

	@Override
	protected abstract StringBuilder getLinkPrefix();

	@Override
	protected final String getContentType() {
		return "posts";
	}

	@Override
	protected void appendLinkPostFix(final StringBuilder sb) {
		// FIXME: urlencode
		if (this.resourceType != Resource.class) {
			sb.append("&").append(RESTConfig.RESOURCE_TYPE_PARAM).append("=").append(ResourceFactory.getResourceName(this.resourceType).toLowerCase());
		}
		if (present(this.tagString)) {
			sb.append("&").append(RESTConfig.TAGS_PARAM).append("=").append(this.tagString);
		}
		if (present(this.hash)) {
			sb.append("&").append(RESTConfig.RESOURCE_PARAM).append("=").append(this.hash);
		}
		if ((this.grouping != GroupingEntity.ALL) && (present(this.groupingValue))) {
			sb.append('&').append(this.grouping.toString().toLowerCase()).append('=').append(this.groupingValue);
		}
		if (present(this.search)) {
			sb.append("&").append(RESTConfig.SEARCH_PARAM).append("=").append(this.search);
		}
	}
}