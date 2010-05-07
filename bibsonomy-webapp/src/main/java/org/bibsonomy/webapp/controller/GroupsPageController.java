/**
 * 
 */
package org.bibsonomy.webapp.controller;

import org.bibsonomy.webapp.command.GroupsListCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * Controller for group overview:
 * - groups
 * 
 * @author Folke Eisterlehner
 */
public class GroupsPageController extends SingleResourceListController implements MinimalisticController<GroupsListCommand> {

	/**
	 * implementation of {@link MinimalisticController} interface
	 */
	@Override
	public View workOn(final GroupsListCommand command) {
		// fill out title
		command.setPageTitle("groups"); // TODO: i18n
		
		/*
		 * get all groups from db; Integer#MAX_VALUE should be enough
		 */
		command.setList(logic.getGroups(0, Integer.MAX_VALUE));
		
		return Views.GROUPSPAGE;
	}

	/**
	 * implementation of {@link MinimalisticController} interface
	 */
	@Override
	public GroupsListCommand instantiateCommand() {
		return new GroupsListCommand();
	}	
}
