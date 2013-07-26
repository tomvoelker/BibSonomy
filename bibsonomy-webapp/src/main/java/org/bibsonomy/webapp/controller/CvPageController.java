package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Collections;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.exceptions.ObjectNotFoundException;
import org.bibsonomy.database.systemstags.markup.MyOwnSystemTag;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.webapp.command.UserResourceViewCommand;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * Controller for the cv page:
 * - /cv/user/USERNAME
 * 
 * 
 * @author Philipp Beau
 * @version $Id$
 */
public class CvPageController extends ResourceListController implements MinimalisticController<UserResourceViewCommand> {

	/**
	 * implementation of {@link MinimalisticController} interface
	 */
	@Override
	public View workOn(final UserResourceViewCommand command) {
		final String requestedUser = command.getRequestedUser();
		if (!present(requestedUser)) {
			throw new MalformedURLSchemeException("error.cvpage_without_username");
		}
		
		final User requestedUserWithDetails = this.logic.getUserDetails(requestedUser);
		
		// don't render cv pages of deleted users
		if (!present(requestedUserWithDetails.getName()) || requestedUserWithDetails.getRole() == Role.DELETED) {
			throw new ObjectNotFoundException(requestedUser);
		}
		
		command.setPageTitle("Curriculum vitae");
		command.setUser(requestedUserWithDetails);

		final GroupingEntity groupingEntity = GroupingEntity.USER;

		this.setTags(command, Resource.class, groupingEntity, requestedUser, null, command.getRequestedTagsList(), null, 1000, null);

		/*
		 * retrieve and set the requested publication(s) / bookmark(s) with the "myown" tag
		 */
		for (final Class<? extends Resource> resourceType : this.getListsToInitialize(command.getFormat(), command.getResourcetype())) {
			final int entriesPerPage = command.getListCommand(resourceType).getEntriesPerPage();		
			this.setList(command, resourceType, groupingEntity, requestedUser, Collections.singletonList(MyOwnSystemTag.NAME), null, null, null, Order.ADDED, null, null, entriesPerPage);
		}
		
		return Views.CVPAGE;
	}

	/**
	 * implementation of {@link MinimalisticController} interface
	 */
	@Override
	public UserResourceViewCommand instantiateCommand() {
		return new UserResourceViewCommand();
	}	
}
