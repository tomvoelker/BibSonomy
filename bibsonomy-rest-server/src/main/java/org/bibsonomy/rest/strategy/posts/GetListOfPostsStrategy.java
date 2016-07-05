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

import org.bibsonomy.common.enums.SearchType;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.model.util.BookmarkUtils;
import org.bibsonomy.rest.RESTConfig;
import org.bibsonomy.rest.strategy.Context;

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
	protected StringBuilder getLinkPrefix() {
		return new StringBuilder(this.getUrlRenderer().getApiUrl() + RESTConfig.POSTS_URL);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected List<? extends Post<? extends Resource>> getList() {
		// TODO: why not sort in DBLogic? (Maybe refactoring LogicInterface with
		// a smarter parameter object to keep parameter lists and sorting clear)
		if ((resourceType != null) && BibTex.class.isAssignableFrom(resourceType)) {
			final List<? extends Post<? extends BibTex>> bibtexList = getList((Class<? extends BibTex>) resourceType);
			BibTexUtils.sortBibTexList(bibtexList, sortKeys, sortOrders);
			return bibtexList;
		} else if ((resourceType != null) && Bookmark.class.isAssignableFrom(resourceType)) {
			final List<? extends Post<? extends Bookmark>> bookmarkList = getList((Class<? extends Bookmark>) resourceType);
			BookmarkUtils.sortBookmarkList(bookmarkList, sortKeys, sortOrders);
			return bookmarkList;
		}

		// return other resource types without ordering
		return getList(resourceType);
	}

	protected <T extends Resource> List<Post<T>> getList(Class<T> resourceType) {
		// TODO: support other searchtypes
		return this.getLogic().getPosts(resourceType, this.grouping, this.groupingValue,
				this.tags, this.hash, this.search, SearchType.LOCAL, null, this.order, null, null,
				getView().getStartValue(), getView().getEndValue());
	}
}