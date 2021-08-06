/**
 * BibSonomy-Rest-Server - The REST-server.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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

import org.bibsonomy.common.enums.SortKey;
import org.bibsonomy.common.enums.SortOrder;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.logic.querybuilder.PostQueryBuilder;
import org.bibsonomy.rest.RESTConfig;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.util.SortUtils;
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
		return this.getUrlRenderer().createUrlBuilderForPopularPosts(this.grouping, this.groupingValue, this.resourceType, this.tags, this.hash, this.search, this.sortCriteria);
	}

	@Override
	protected List<? extends Post<? extends Resource>> getList() {
		final List<String> tag = Collections.singletonList("sys:days:" + this.periodIndex); // FIXME: use system tag builder

		final PostQueryBuilder postQueryBuilder = new PostQueryBuilder();
		postQueryBuilder.setGrouping(this.grouping)
				.setGroupingName(this.groupingValue)
				.search(this.search)
				.setTags(tag)
				.setSortCriteria(SortUtils.singletonSortCriteria(SortKey.POPULAR, SortOrder.DESC))
				.setScope(this.searchType)
				.start(this.getView().getStartValue())
				.end(this.getView().getEndValue());

		return this.getLogic().getPosts(postQueryBuilder.createPostQuery(this.resourceType));
	}
}