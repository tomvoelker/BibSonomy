package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.model.Resource;
import org.bibsonomy.webapp.command.UserResourceViewCommand;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;

/**
 * @author Steffen Kress
 * @version $Id$
 */
public class FriendPageController extends SingleResourceListControllerWithTags implements MinimalisticController<UserResourceViewCommand> {
	private static final Log log = LogFactory.getLog(FriendPageController.class);
	
	private static final int THRESHOLD = 20000;

	@Override
	public View workOn(final UserResourceViewCommand command) {
		log.debug(this.getClass().getSimpleName());
		
		if (!command.getContext().isUserLoggedIn()){
			log.info("Trying to access a friendpage without being logged in");
			return new ExtendedRedirectView("/login");
		}
		
		final String format = command.getFormat();
		this.startTiming(this.getClass(), format);
		
		final String groupingName = command.getRequestedUser();

		// no user given -> error
		if (!present(groupingName)) {
			throw new MalformedURLSchemeException("error.friend_page_without_friendname");
		}

		// set grouping entity, grouping name, tags
		final GroupingEntity groupingEntity = GroupingEntity.FRIEND;
		final List<String> requTags = command.getRequestedTagsList();

		// handle the case when tags only are requested
		this.handleTagsOnly(command, groupingEntity, groupingName, null, requTags, null, Integer.MAX_VALUE, null);		
		
		// determine which lists to initalize depending on the output format
		// and the requested resourcetype
		this.chooseListsToInitialize(format, command.getResourcetype());

		// retrieve and set the requested resource lists, along with total
		// counts
		for (final Class<? extends Resource> resourceType : listsToInitialise) {
			this.setList(command, resourceType, groupingEntity, groupingName, requTags, null, null, null, null, command.getListCommand(resourceType).getEntriesPerPage());
			this.postProcessAndSortList(command, resourceType);
		}
		// set page title
		command.setPageTitle("friend :: " + groupingName);  // TODO: i18n

		// html format - retrieve tags and return HTML view
		if ("html".equals(format)) {
			this.setTags(command, Resource.class, groupingEntity, groupingName, null, requTags, null, 20000, null);

			// log if a user has reached threshold
			if (command.getTagcloud().getTags().size() >= THRESHOLD) {
				log.error("User " + groupingName + " has reached threshold of " + THRESHOLD + " tags on friend page");
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
		return Views.getViewByFormat(format);
	}

	@Override
	public UserResourceViewCommand instantiateCommand() {
		return new UserResourceViewCommand();
	}
}
