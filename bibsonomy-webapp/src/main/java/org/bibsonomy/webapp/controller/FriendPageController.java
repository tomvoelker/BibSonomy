package org.bibsonomy.webapp.controller;

import java.util.List;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.model.Resource;
import org.bibsonomy.webapp.command.UserResourceViewCommand;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * @author Steffen Kress
 * @version $Id$
 */
public class FriendPageController extends SingleResourceListControllerWithTags implements MinimalisticController<UserResourceViewCommand> {
	private static final Logger LOGGER = Logger.getLogger(FriendPageController.class);

	public View workOn(final UserResourceViewCommand command) {
		LOGGER.debug(this.getClass().getSimpleName());
		this.startTiming(this.getClass(), command.getFormat());

		// no user given -> error
		if (command.getRequestedUser() == null) {
			LOGGER.error("Invalid query /friend without friendname");
			throw new MalformedURLSchemeException("error.friend_page_without_friendname");
		}

		// set grouping entity, grouping name, tags
		final GroupingEntity groupingEntity = GroupingEntity.FRIEND;//GroupingEntity.FRIEND;
		final String groupingName = command.getRequestedUser();
		final List<String> requTags = command.getRequestedTagsList();

		// determine which lists to initalize depending on the output format
		// and the requested resourcetype
		this.chooseListsToInitialize(command.getFormat(), command.getResourcetype());

		// retrieve and set the requested resource lists, along with total
		// counts
		for (final Class<? extends Resource> resourceType : listsToInitialise) {
			this.setList(command, resourceType, groupingEntity, groupingName, requTags, null, null, null, null, command.getListCommand(resourceType).getEntriesPerPage());
			this.postProcessAndSortList(command, resourceType);
		}
		// set page title
		command.setPageTitle("friend :: " + groupingName);

		// html format - retrieve tags and return HTML view
		if (command.getFormat().equals("html")) {
			this.setTags(command, Resource.class, groupingEntity, groupingName, null, null, null, null, 0, 20000, null);

			// log if a user has reached threshold
			if (command.getTagcloud().getTags().size() > 19999) {
				LOGGER.error("User " + groupingName + " has reached threshold of 20000 tags on friend page");
			}
			
			/*
			 * add user details to command, if loginUser is admin
			 */
			if (Role.ADMIN.equals(logic.getAuthenticatedUser().getRole())) {
				command.setUser(logic.getUserDetails(groupingName));
			}

			this.endTiming();
			return Views.FRIENDPAGE; 
		}
		this.endTiming();
		// export - return the appropriate view
		return Views.getViewByFormat(command.getFormat());
	}

	public UserResourceViewCommand instantiateCommand() {
		return new UserResourceViewCommand();
	}
}
