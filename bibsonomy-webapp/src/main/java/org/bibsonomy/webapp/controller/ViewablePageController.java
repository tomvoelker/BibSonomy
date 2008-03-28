package org.bibsonomy.webapp.controller;

import java.util.List;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.webapp.command.GroupResourceViewCommand;
import org.bibsonomy.webapp.command.RelatedTagCommand;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * @author Christian Kramer
 * @version $Id$
 */
public class ViewablePageController extends MultiResourceListControllerWithTags implements MinimalisticController<GroupResourceViewCommand>{
	private static final Logger LOGGER = Logger.getLogger(GroupPageController.class);
	
	public View workOn(GroupResourceViewCommand command) {
		LOGGER.debug(this.getClass().getSimpleName());
		this.startTiming(this.getClass(), command.getFormat());
		
		// we need to be logged in, and a group needs to be present
		if (command.userLoggedIn() == false) {
			throw new MalformedURLSchemeException("error.viewable_page_not_logged_in");
		}				
		if (command.getRequestedGroup() == null || "".equals(command.getRequestedGroup())) {
			throw new MalformedURLSchemeException("error.viewable_page_without_group");
		}		
						
		// set grouping entity and grouping name
		final GroupingEntity groupingEntity = GroupingEntity.VIEWABLE;
		final String groupingName = command.getRequestedGroup();
		final List<String> requTags = command.getRequestedTagsList();
		
		// set title
		// TODO: localize
		command.setPageTitle("viewable :: " + groupingName);	
		
		// determine which lists to initalize depending on the output format 
		// and the requested resourcetype
		this.chooseListsToInitialize(command.getFormat(), command.getResourcetype());
		
		// retrieve and set the requested resource lists
		for (final Class<? extends Resource> resourceType : listsToInitialise) {			
			this.setList(command, resourceType, groupingEntity, groupingName, requTags, null, null, null, command.getListCommand(resourceType).getEntriesPerPage());
			this.postProcessList(command, resourceType);
		}		
		
		// html format - retrieve tags and return HTML view
		if ("html".equals(command.getFormat())) {
			this.setTags(command, Resource.class, groupingEntity, groupingName, null, null, null, 0, 1000, null);
			
			if (requTags.size() > 0) {
				this.setRelatedTags(command, Resource.class, groupingEntity, groupingName, null, requTags, null,  0, 20, null);
				this.endTiming();
				return Views.VIEWABLETAGPAGE;
			}
			this.endTiming();
			return Views.VIEWABLEPAGE;			
		}
		this.endTiming();
		// export - return the appropriate view
		return Views.getViewByFormat(command.getFormat());		
	}

	public GroupResourceViewCommand instantiateCommand() {
		return new GroupResourceViewCommand();
	}	
	
}
