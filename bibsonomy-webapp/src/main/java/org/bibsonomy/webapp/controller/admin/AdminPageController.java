package org.bibsonomy.webapp.controller.admin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.User;
import org.bibsonomy.model.UserSettings;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.webapp.command.admin.AdminCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * Controller for admin page
 * 
 * @author Beate Krause
 * @version $Id$
 */
public class AdminPageController implements	MinimalisticController<AdminCommand> {
	private static final Log log = LogFactory.getLog(AdminPageController.class);

	
	private LogicInterface logic;
	
	@SuppressWarnings("unused") // FIXME: currently unused
	private UserSettings userSettings;

	@Override
	public View workOn(AdminCommand command) {
		log.debug(this.getClass().getSimpleName());

		final RequestWrapperContext context = command.getContext();
		final User loginUser = context.getLoginUser();

		/* Check user role
		 * If user is not logged in or not an admin: show error message */
		if (!context.isUserLoggedIn() || !Role.ADMIN.equals(loginUser.getRole())) {
			throw new AccessDeniedException("error.method_not_allowed");
		}
		
		command.setPageTitle("admin");

		/*
		 * get information about a specific user
		 */
		if (command.getAclUserInfo() != null) {
			log.debug("Get information for: " + command.getAclUserInfo());
			command.setUser(logic.getUserDetails(command.getAclUserInfo()));
		}
		
		/*
		 * add a group to the system
		 */
		log.debug("Group name " + command.getRequestedGroupName());
		if (command.getRequestedGroupName() != null){
			// create the new group
			Group newGroup = new Group(command.getRequestedGroupName());
			newGroup.setPrivlevel(command.getSelPrivlevel());
			// update group
			logic.createGroup(newGroup);	
			// inform user about success
			command.setAdminResponse("Successfully created a group");
		}
		
		return Views.ADMIN;
	}

	@Override
	public AdminCommand instantiateCommand() {
		return new AdminCommand();
	}

	/**
	 * @param logic the logic to set
	 */
	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}

	/**
	 * @param userSettings the userSettings to set
	 */
	public void setUserSettings(UserSettings userSettings) {
		this.userSettings = userSettings;
	}

}