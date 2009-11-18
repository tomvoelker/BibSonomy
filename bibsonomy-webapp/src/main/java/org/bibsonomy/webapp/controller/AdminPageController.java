package org.bibsonomy.webapp.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.webapp.command.admin.AdminCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
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

	

	public View workOn(AdminCommand command) {
		log.debug(this.getClass().getSimpleName());

		final User loginUser = command.getContext().getLoginUser();
		if (loginUser.getRole().equals(Role.DEFAULT)) {
			/** TODO: redirect to login page as soon as it is available */
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

}