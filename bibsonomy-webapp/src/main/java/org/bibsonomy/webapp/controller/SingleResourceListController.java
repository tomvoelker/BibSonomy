package org.bibsonomy.webapp.controller;


import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.common.enums.FilterEntity;
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
 * @version $Id$
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
				BookmarkUtils.sortBookmarkList(cmd.getBookmark().getList(), SortUtils.parseSortKeys(cmd.getSortPage()), SortUtils.parseSortOrders(cmd.getSortPageOrder()) );
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
	 * 
	 */
	protected int getPostCountForSidebar(final GroupingEntity groupingEntity, final String groupingName, final List<String> requTags) {
		return this.logic.getPostStatistics(BibTex.class, groupingEntity, groupingName, requTags, null, Order.ADDED, FilterEntity.UNFILTERED, 0, 999, null, null)
				+ this.logic.getPostStatistics(Bookmark.class, groupingEntity, groupingName, requTags, null, Order.ADDED, FilterEntity.UNFILTERED, 0, 999, null, null);
	}

}