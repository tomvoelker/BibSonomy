package org.bibsonomy.webapp.controller;


import java.util.List;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.command.MultiResourceViewCommand;

/**
 * controller for retrieving multiple windowed lists with resources. These are currently the bookmark an the bibtex list
 * 
 * @author Jens Illig
 */
public abstract class MultiResourceListController extends ResourceListController {
	private static final Logger log = Logger.getLogger(MultiResourceListController.class);

	/**
	 * do some post processing with the retrieved resources
	 * 
	 * @param cmd
	 */
	@SuppressWarnings("unchecked")
	protected <T extends MultiResourceViewCommand> void postProcessAndSortList(T cmd, Class<? extends Resource> resourceType) {				
		for (ListCommand<?> listCommand: cmd.getListCommand(resourceType)) {
			if (resourceType == BibTex.class) {
				// TODO: how can we do this in a clean way without SuppressWarnings?
				postProcessAndSortList(cmd, (List<Post<BibTex>>) listCommand.getList());	
			}
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
	protected <T extends Resource, V extends MultiResourceViewCommand> void addList(V cmd, Class<T> resourceType, GroupingEntity groupingEntity, String groupingName, List<String> tags, String hash, Order order, FilterEntity filter, String search, int itemsPerPage) {
		// new list command to put result list into
		final ListCommand<Post<T>> listCommand = new ListCommand<Post<T>>(cmd);
		// retrieve posts		
		log.debug("getPosts " + resourceType + " " + groupingEntity + " " + groupingName + " " + listCommand.getStart() + " " + itemsPerPage + " " + filter);
		listCommand.setList(this.logic.getPosts(resourceType, groupingEntity, groupingName, tags, hash, order, filter, listCommand.getStart(), listCommand.getStart() + itemsPerPage, search) );
		cmd.getListCommand(resourceType).add(listCommand);

		// list settings
		listCommand.setEntriesPerPage(itemsPerPage);
	}
	
	protected <T extends Resource, V extends MultiResourceViewCommand> void addDescription(V cmd, Class<T> resourceType, String description) {
		cmd.getListsDescription(resourceType).add(description);
	}

}