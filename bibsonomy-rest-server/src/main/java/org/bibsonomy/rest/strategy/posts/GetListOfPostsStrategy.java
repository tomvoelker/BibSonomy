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

import java.util.List;

import org.bibsonomy.common.enums.SortKey;
import org.bibsonomy.common.enums.SortOrder;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.logic.query.PostQuery;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.model.util.BookmarkUtils;
import org.bibsonomy.rest.ViewModel;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.util.SortUtils;
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
		// TODO: why not sort in DBLogic? (Maybe refactoring LogicInterface with
		// a smarter parameter object to keep parameter lists and sorting clear)
		final List<SortKey> sortKeys = SortUtils.getSortKeys(this.sortCriteria);
		final List<SortOrder> sortOrders = SortUtils.getSortOrders(this.sortCriteria);

		if ((this.resourceType != null) && BibTex.class.isAssignableFrom(this.resourceType)) {
			final List<? extends Post<? extends BibTex>> bibtexList = getList((Class<? extends BibTex>) this.resourceType);

			BibTexUtils.sortBibTexList(bibtexList, sortKeys, sortOrders);
			return bibtexList;
		} else if ((resourceType != null) && Bookmark.class.isAssignableFrom(resourceType)) {
			final List<? extends Post<? extends Bookmark>> bookmarkList = getList((Class<? extends Bookmark>) this.resourceType);
			BookmarkUtils.sortBookmarkList(bookmarkList, sortKeys, sortOrders);
			return bookmarkList;
		}

		// return other resource types without ordering
		return getList(resourceType);
	}

	protected <T extends Resource> List<Post<T>> getList(Class<T> resourceType) {
		final PostQuery<T> query = new PostQuery<>(resourceType);
		query.setGrouping(this.grouping);
		query.setGroupingName(this.groupingValue);
		query.setTags(this.tags);
		query.setHash(this.hash);
		query.setSearch(this.search);
		query.setScope(this.searchType);
		query.setSortCriteria(this.sortCriteria);
		query.setBeforeChangeDate(this.beforeChangeDate);
		query.setAfterChangeDate(this.afterChangeDate);
		final ViewModel view = this.getView();
		query.setStart(view.getStartValue());
		query.setEnd(view.getEndValue());
		return this.getLogic().getPosts(query);
	}

}