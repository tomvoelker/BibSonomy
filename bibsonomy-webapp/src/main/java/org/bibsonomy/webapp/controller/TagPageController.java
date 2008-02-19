package org.bibsonomy.webapp.controller;

import java.util.List;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.logic.Order;
import org.bibsonomy.webapp.command.RelatedTagCommand;
import org.bibsonomy.webapp.command.RelatedUserCommand;
import org.bibsonomy.webapp.command.TagResourceViewCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * Controller for tag pages
 * /tag/TAGNAME
 * 
 * 
 * @author Michael Wagner
 * @version $Id$
 */

public class TagPageController extends MultiResourceListController implements MinimalisticController<TagResourceViewCommand>{
	private static final Logger LOGGER = Logger.getLogger(TagPageController.class);

	
	public View workOn(final TagResourceViewCommand command) {
		LOGGER.debug(this.getClass().getSimpleName());
		
		//if no tags given return
		if(command.getRequestedTags().length() == 0) return null;
		
		final List<String> requTags = command.getRequestedTagsList();
		
		// determine which lists to initalize depending on the output format 
		// and the requested resourcetype
		this.chooseListsToInitialize(command.getFormat(), command.getResourcetype());
		
		// retrieve and set the requested resource lists
		for (final Class<? extends Resource> resourceType : listsToInitialise) {			
			this.setList(command, resourceType, GroupingEntity.ALL, null, requTags, null, Order.FOLKRANK, null, command.getListCommand(resourceType).getEntriesPerPage());
			this.postProcessList(command, resourceType);
		}	
		
		// html format - retrieve tags and return HTML view
		if (command.getFormat().equals("html")) {
			this.setRelatedTags(command, Resource.class, GroupingEntity.ALL, null, null, requTags, Order.FOLKRANK, 0, 20, null);
			this.setRelatedUser(command, requTags, Order.FOLKRANK, 0, 50);
			return Views.TAGPAGE;			
		}

		// export - return the appropriate view
		return Views.getViewByFormat(command.getFormat());
		
	}
	
	public TagResourceViewCommand instantiateCommand() {
		return new TagResourceViewCommand();
	}
	
	/**
     * Retrieve a set of related tags to a list of given tags 
     * from the database logic and add them to the command object
     * 
	 * @param <T> extends Resource, the resource type
	 * @param <V> extends ResourceViewCommand, the command
	 * @param cmd the command
	 * @param resourceType the resource type
	 * @param groupingEntity the grouping entity
	 * @param groupingName the grouping name
	 * @param regex regular expression for tag filtering
	 * @param tags list of tags
	 * @param start start parameter
	 * @param end end parameter
	 **/
	protected <T extends Resource, V extends TagResourceViewCommand> void setRelatedTags(V cmd, Class<T> resourceType, GroupingEntity groupingEntity, String groupingName, String regex, List<String> tags, Order order, int start, int end, String search) {
		RelatedTagCommand relatedTagCommand = cmd.getRelatedTagCommand();
		relatedTagCommand.setRelatedTags(this.logic.getTags(resourceType, groupingEntity, groupingName, regex, tags, order, start, end, search));		
	}
	
	/**
	 * retrieve related user by tag
	 * 
	 * @param <T>
	 * @param <V>
	 * @param cmd
	 * @param tags
	 * @param order
	 * @param start
	 * @param end
	 */
	protected <T extends Resource, V extends TagResourceViewCommand> void setRelatedUser(V cmd, List<String> tags, Order order, int start, int end) {
		RelatedUserCommand relatedUserCommand = cmd.getRelatedUserCommand();
		relatedUserCommand.setRelatedUser(this.logic.getUsers(tags,order,start,end));
	}
	
}
