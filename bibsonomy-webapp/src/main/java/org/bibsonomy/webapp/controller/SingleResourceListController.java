package org.bibsonomy.webapp.controller;


import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.TagCloudSort;
import org.bibsonomy.common.enums.TagCloudStyle;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.UserSettings;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.model.util.TagUtils;
import org.bibsonomy.util.SortUtils;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.command.ResourceViewCommand;
import org.bibsonomy.webapp.command.SimpleResourceViewCommand;
import org.bibsonomy.webapp.command.TagCloudCommand;
import org.bibsonomy.webapp.view.Views;

/**
 * controller for retrieving a windowed list with resources. These are currently the bookmark an the bibtex list
 * 
 * @author Jens Illig
 */
public abstract class SingleResourceListController extends ResourceListController {
	private static final Logger log = Logger.getLogger(SingleResourceListController.class);
		
	/**
	 * do some post processing with the retrieved resources
	 * 
	 * @param cmd
	 */
	protected <T extends SimpleResourceViewCommand> void postProcessAndSortList(T cmd, Class<? extends Resource> resourceType) {				
		if (resourceType == BibTex.class) {
			postProcessAndSortList(cmd, cmd.getBibtex().getList());
		}
	}
		
	/**
     * retrieve a list of posts from the database logic and add them to the command object
     * 
	 * @param <T> extends Resource
	 * @param <V> extends ResourceViewComand
	 * @param cmd the command object
	 * @param resourceType the resource type
	 * @param groupingEntity the grouping entity
	 * @param groupingName the grouping name
	 * @param itemsPerPage number of items to be displayed on each page
	 */
	protected <T extends Resource, V extends SimpleResourceViewCommand> void setList(V cmd, Class<T> resourceType, GroupingEntity groupingEntity, String groupingName, List<String> tags, String hash, Order order, FilterEntity filter, String search, int itemsPerPage) {
		ListCommand<Post<T>> listCommand = cmd.getListCommand(resourceType);
		// retrieve posts		
		log.debug("getPosts " + resourceType + " " + groupingEntity + " " + groupingName + " " + listCommand.getStart() + " " + itemsPerPage + " " + filter);
		listCommand.setList( this.logic.getPosts(resourceType, groupingEntity, groupingName, tags, hash, order, filter, listCommand.getStart(), listCommand.getStart() + itemsPerPage, search) );
		// list settings
		listCommand.setEntriesPerPage(itemsPerPage);
	}
	
}