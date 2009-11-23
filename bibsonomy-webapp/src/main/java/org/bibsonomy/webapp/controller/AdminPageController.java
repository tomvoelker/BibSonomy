package org.bibsonomy.webapp.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.model.User;
import org.bibsonomy.model.UserSettings;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.webapp.command.admin.AdminCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;

/**
 * Controller for admin page
 * 
 * @author Beate Krause
 * @version $Id$
 */
public class AdminPageController implements MinimalisticController<AdminCommand> {

	private static final Log log = LogFactory.getLog(AdminPageController.class);
	
	private LogicInterface logic;

	private UserSettings userSettings;

	public View workOn(AdminCommand command) {
		log.debug(this.getClass().getSimpleName());

		
		final User loginUser = command.getContext().getLoginUser();
		
		// check if user is logged in and redirect user to login page 
		// if this is not the case
		if(command.getContext().isUserLoggedIn() == false){
			log.info("Trying to access an admin page without being logged in");
			return new ExtendedRedirectView("/login");
		}
		
		// TODO is this how an admin role is supposed to be checked
		if (!Role.ADMIN.equals(loginUser.getRole())) {
			throw(new ValidationException("error.permission_denied"));
		}

		command.setPageTitle("admin");
		
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