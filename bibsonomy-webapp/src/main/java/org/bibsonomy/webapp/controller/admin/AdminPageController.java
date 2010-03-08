package org.bibsonomy.webapp.controller.admin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.exceptions.ValidationException;
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
 * @version $Id: AdminPageController.java,v 1.20 2009-11-23 15:06:32 beatekr Exp
 *          $
 */
public class AdminPageController implements	MinimalisticController<AdminCommand> {

	private static final Log log = LogFactory.getLog(AdminPageController.class);

	private LogicInterface logic;

	private UserSettings userSettings;

	public View workOn(AdminCommand command) {
		log.debug(this.getClass().getSimpleName());

		final RequestWrapperContext context = command.getContext();
		final User loginUser = context.getLoginUser();

		/* Check user role
		 * If user is not logged in or not an admin: show error message */
		if (!context.isUserLoggedIn() || !Role.ADMIN.equals(loginUser.getRole())) {
			throw new ValidationException("error.method_not_allowed");
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

	public AdminCommand instantiateCommand() {
		return new AdminCommand();
	}

	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}

	public void setUserSettings(UserSettings userSettings) {
		this.userSettings = userSettings;
	}

	public LogicInterface getLogic() {
		return this.logic;
	}

	public UserSettings getUserSettings() {
		return this.userSettings;
	}

}