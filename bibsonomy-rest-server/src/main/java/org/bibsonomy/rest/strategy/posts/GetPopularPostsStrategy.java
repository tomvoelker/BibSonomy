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

import java.util.Collections;
import java.util.List;

import org.bibsonomy.common.enums.SearchType;
import org.bibsonomy.database.systemstags.SystemTagsUtil;
import org.bibsonomy.database.systemstags.search.DaysSystemTag;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.rest.RESTConfig;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.util.UrlBuilder;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 */
public class GetPopularPostsStrategy extends AbstractListOfPostsStrategy {
	
	private int periodIndex;

	/**
	 * @param context
	 */
	public GetPopularPostsStrategy(final Context context) {
		super(context);
		
		this.periodIndex = context.getIntAttribute(RESTConfig.PERIOD_INDEX, 0);
	}

	@Override
	protected UrlBuilder getLinkPrefix() {
		return this.getUrlRenderer().createUrlBuilderForPopularPosts(this.grouping, this.groupingValue, this.resourceType, this.tags, this.hash, this.search, this.order);
	}

	@Override
	protected List<? extends Post<? extends Resource>> getList() {
		final List<String> tag = Collections.singletonList(SystemTagsUtil.buildSystemTagString(DaysSystemTag.NAME, Integer.valueOf(this.periodIndex)));
		return this.getLogic().getPosts(this.resourceType, this.grouping, this.groupingValue, tag, null, this.search, SearchType.LOCAL, null, Order.POPULAR, null, null, this.getView().getStartValue(), this.getView().getEndValue());
	}
}