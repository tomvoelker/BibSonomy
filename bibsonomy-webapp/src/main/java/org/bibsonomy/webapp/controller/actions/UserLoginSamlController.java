package org.bibsonomy.webapp.controller.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.webapp.command.actions.UserLoginSamlCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;

/**
 * @author nilsraabe
 * @version $Id$
 */

public class UserLoginSamlController implements MinimalisticController<UserLoginSamlCommand> {

	private static final Log log = LogFactory.getLog(UserLoginController.class);
	
	@Override
	public UserLoginSamlCommand instantiateCommand() {

		log.info("UserLoginSamlCommand in UserLoginSamlController initialized");
		return new UserLoginSamlCommand();
	}

	@Override
	public View workOn(final UserLoginSamlCommand command) {
		
		log.info("UserLoginSamlController starts WorkOn");
		
		return new ExtendedRedirectView("/");
	}

}
