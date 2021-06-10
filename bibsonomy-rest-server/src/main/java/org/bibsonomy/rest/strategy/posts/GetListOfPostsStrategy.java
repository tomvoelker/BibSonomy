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

import java.util.List;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.logic.query.PostQuery;
import org.bibsonomy.rest.ViewModel;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.util.UrlBuilder;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 */
public class GetListOfPostsStrategy extends AbstractListOfPostsStrategy {
	
	/**
	 * @param context
	 */
	public GetListOfPostsStrategy(final Context context) {
		super(context);
	}

	@Override
	protected UrlBuilder getLinkPrefix() {
		return this.getUrlRenderer().createUrlBuilderForPosts(this.grouping, this.groupingValue, this.resourceType, this.tags, this.hash, this.search, this.sortCriteria, this.searchType);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected List<? extends Post<? extends Resource>> getList() {
		final PostQuery<?> query = new PostQuery<>(this.resourceType);
		query.setGrouping(this.grouping);
		query.setGroupingName(this.groupingValue);
		query.setTags(this.tags);
		query.setHash(this.hash);
		query.setSearch(this.search);
		query.setScope(this.searchType);
		query.setSortCriteria(this.sortCriteria);
		final ViewModel view = this.getView();
		query.setStart(view.getStartValue());
		query.setEnd(view.getEndValue());
		return this.getLogic().getPosts(query);
	}
}