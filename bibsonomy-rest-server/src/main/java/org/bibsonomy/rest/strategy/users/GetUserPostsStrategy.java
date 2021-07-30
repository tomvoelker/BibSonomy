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
package org.bibsonomy.rest.strategy.users;

import java.util.List;

import org.bibsonomy.common.SortCriteria;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.logic.querybuilder.PostQueryBuilder;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.posts.AbstractListOfPostsStrategy;
import org.bibsonomy.util.SortUtils;
import org.bibsonomy.util.UrlBuilder;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 */
public class GetUserPostsStrategy extends AbstractListOfPostsStrategy {

	/** the requested user name */
	protected final String userName;

	/**
	 * @param context
	 * @param userName
	 */
	public GetUserPostsStrategy(final Context context, final String userName) {
		super(context);
		this.userName = userName;
	}

	@Override
	protected UrlBuilder getLinkPrefix() {
		return this.getUrlRenderer().getUrlBuilderForUser(this.userName, this.tagString, this.resourceType);
	}

	@Override
	protected List<? extends Post<? extends Resource>> getList() {
		final List<SortCriteria> sortCriteria = SortUtils.generateSortCriteria(this.sortKeys, this.sortOrders);
		final PostQueryBuilder postQueryBuilder = new PostQueryBuilder();
		postQueryBuilder.setGrouping(GroupingEntity.USER)
				.setGroupingName(this.userName)
				.setTags(this.tags)
				.search(this.search)
				.setScope(this.searchType)
				.setSortCriteria(sortCriteria)
				.start(this.getView().getStartValue())
				.end(this.getView().getEndValue());
		return this.getLogic().getPosts(postQueryBuilder.createPostQuery(this.resourceType));
	}

}