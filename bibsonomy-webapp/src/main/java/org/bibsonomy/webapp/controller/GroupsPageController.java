/**
 * 
 */
package org.bibsonomy.webapp.controller;

import org.apache.log4j.Logger;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.webapp.command.GroupsListCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * Controller for group overview
 * 
 * groups
 * 
 * @author Folke Eisterlehner
 */
public class GroupsPageController extends SingleResourceListController implements MinimalisticController<GroupsListCommand> {
	private static final Logger LOGGER = Logger.getLogger(GroupsPageController.class);
	protected LogicInterface logic;

	/**
	 * setter interface for injection
	 * @param logic logic interface
	 */
	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}
	
	/**
	 * implementation of {@link MinimalisticController} interface
	 */
	public View workOn(GroupsListCommand command) {
		// filll out overhead
		command.setPageTitle("groups");
		
		// get group data
		// We want to get all group members. Begin and end value are
		// passed to the MySQL LIMIT statement for which documentation
		// yields:
		//    "To retrieve all rows from a certain offset up to the end
		//     of the result set, you can use some large number for the 
		//     second parameter."
		// The example gives 18446744073709551615 which is out of bound for int.
		command.setList(logic.getGroups(0, Integer.MAX_VALUE));

		// all done.
		return Views.GROUPSPAGE;
	}

	/**
	 * implementation of {@link MinimalisticController} interface
	 */
	public GroupsListCommand instantiateCommand() {
		return new GroupsListCommand();
	}	
}
