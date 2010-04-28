package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Collections;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Resource;
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
	private static final String TAG_MYOWN = "myown";
	
	/**
	 * implementation of {@link MinimalisticController} interface
	 */
	@Override
	public View workOn(final UserResourceViewCommand command) {
		command.setPageTitle("Curriculum vitae");
		
		final String requUser = command.getRequestedUser();

		if (!present(requUser)) {
			throw new MalformedURLSchemeException("error.cvpage_without_username");
		}

		command.setUser(this.logic.getUserDetails(requUser));

		final GroupingEntity groupingEntity = GroupingEntity.USER;

		this.setTags(command, Resource.class, groupingEntity, requUser, null, command.getRequestedTagsList(), null, 1000, null);

		/*
		 * retrieve and set the requested publication(s) / bookmark(s) with the "myown" tag
		 */
		for (final Class<? extends Resource> resourceType : this.listsToInitialise) {
			final int entriesPerPage = command.getListCommand(resourceType).getEntriesPerPage();		
			this.setList(command, resourceType, groupingEntity, requUser, Collections.singletonList(TAG_MYOWN), null, Order.ADDED, null, null, entriesPerPage);
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
