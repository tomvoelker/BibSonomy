package org.bibsonomy.webapp.controller;

import java.net.MalformedURLException;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.exceptions.ResourceNotFoundException;
import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.model.Resource;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.webapp.command.PageCommand;
import org.bibsonomy.webapp.command.ResourceViewCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * Controller for user pages 
 * /user/USERNAME
 *
 * @author Dominik Benz
 * @version $Id$
 */
public class UserPageController extends MultiResourceListController implements MinimalisticController<ResourceViewCommand>{
	private static final Logger LOGGER = Logger.getLogger(UserPageController.class);

	public View workOn(final ResourceViewCommand command) {
		LOGGER.debug(this.getClass().getSimpleName());

		// set grouping entity and grouping name
		final GroupingEntity groupingEntity;
		final String groupingName;
		if (command.getRequestedUser() == null) {
			LOGGER.error("Invalid query /user without username");
			throw new MalformedURLSchemeException("error.user_page_without_username");
		}
		groupingEntity = GroupingEntity.USER;
		groupingName = command.getRequestedUser();
		
		// determine which lists to initalize depending on the output format 
		// and the requested resourcetype
		this.chooseListsToInitialize(command.getFormat(), command.getResourcetype());

		// retrieve and set the requested resource lists, along with total counts
		for (final Class<? extends Resource> resourceType : listsToInitialise) {
			this.setList(command, resourceType, groupingEntity, groupingName, null, null, null, null, command.getListCommand(resourceType).getEntriesPerPage(), 100);
			this.postProcessList(command, resourceType);
			
			int totalCount = this.logic.getStatistics(resourceType, groupingEntity, groupingName, null, null);
			command.getListCommand(resourceType).setTotalCount(totalCount);
		}

		// html format - retrieve tags and return HTML view
		if (command.getFormat().equals("html")) {
			this.setTags(command, Resource.class, groupingEntity, groupingName, null, null, 0, 1000);
			return Views.USERPAGE;			
		}

		// export - return the appropriate view
		return Views.getViewByFormat(command.getFormat());		
	}

	public ResourceViewCommand instantiateCommand() {
		return new ResourceViewCommand();
	}
}
