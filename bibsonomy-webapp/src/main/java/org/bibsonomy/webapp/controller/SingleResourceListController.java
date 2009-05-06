package org.bibsonomy.webapp.controller;


import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.model.util.BookmarkUtils;
import org.bibsonomy.util.SortUtils;
import org.bibsonomy.webapp.command.SimpleResourceViewCommand;

/**
 * controller for retrieving a windowed list with resources. These are currently the bookmark an the bibtex list
 * 
 * @author Jens Illig
 */
public abstract class SingleResourceListController extends ResourceListController {
		
	/**
	 * do some post processing with the retrieved resources
	 * 
	 * @param cmd
	 */
	protected <T extends SimpleResourceViewCommand> void postProcessAndSortList(T cmd, Class<? extends Resource> resourceType) {						
		if (resourceType == BibTex.class) {
			postProcessAndSortList(cmd, cmd.getBibtex().getList());
		}
		if (resourceType == Bookmark.class) {
			if (!"none".equals(cmd.getSortPage())) {
				BookmarkUtils.sortBookmarkList(cmd.getBookmark().getList(), SortUtils.parseSortKeys(cmd.getSortPage()), SortUtils.parseSortOrders(cmd.getSortPageOrder()) );
			}			
		}
	}
	
}