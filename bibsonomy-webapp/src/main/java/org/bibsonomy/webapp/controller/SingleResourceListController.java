/**
 * BibSonomy-Webapp - The web application for BibSonomy.
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
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.controller;


import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.model.util.BookmarkUtils;
import org.bibsonomy.util.SortUtils;
import org.bibsonomy.webapp.command.SimpleResourceViewCommand;

/**
 * Controller for retrieving a windowed list with resources.
 * These are currently the bookmark and the publication list
 * 
 * @author Jens Illig
 */
public abstract class SingleResourceListController extends ResourceListController {

	/**
	 * do some post processing with the retrieved resources
	 * 
	 * @param cmd
	 */
	protected void postProcessAndSortList(final SimpleResourceViewCommand cmd, final Class<? extends Resource> resourceType) {						
		if (resourceType == BibTex.class) {
			postProcessAndSortList(cmd, cmd.getBibtex().getList());
		}
		if (resourceType == Bookmark.class) {
			if (!"none".equals(cmd.getSortPage())) {
				BookmarkUtils.sortBookmarkList(cmd.getBookmark().getList(), SortUtils.parseSortKeys(cmd.getSortPage()), SortUtils.parseSortOrders(cmd.getSortPageOrder()));
			}
		}
	}

	/** 
	 * returns a list of concepts, namely those tags of the requestedTags that the user groupingName has as concepts
	 * FIXME: cmd unused
	 */
	protected List<Tag> getConceptsForSidebar(final SimpleResourceViewCommand cmd, final GroupingEntity groupingEntity, final String groupingName, final List<String> requTags) {
		final List<Tag> concepts = new ArrayList<Tag>();
		for (final String requTag : requTags) {
			final Tag conceptDetails = this.logic.getConceptDetails(requTag, groupingEntity, groupingName);
			if (present(conceptDetails)) {
				concepts.add(conceptDetails);
			}
		}
		// concepts is not empty if groupingName has at least one of the requested Tags as a concept
		return concepts;
	}


	/** 
	 * returns the number of posts tagged with all of requTags by groupingName. 
	 */
	protected int getPostCountForSidebar(final GroupingEntity groupingEntity, final String groupingName, final List<String> requTags) {
		return this.logic.getPostStatistics(BibTex.class, groupingEntity, groupingName, requTags, null, null, null, Order.ADDED, null, null, 0, 999).getCount()
				+ this.logic.getPostStatistics(Bookmark.class, groupingEntity, groupingName, requTags, null, null, null, Order.ADDED, null, null, 0, 999).getCount();
	}

}