package org.bibsonomy.webapp.controller.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.webapp.command.actions.UserLoginSamlCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;

/**
 * @author nilsraabe
 * @version $Id$
 */

public class UserLoginSamlController implements MinimalisticController<UserLoginSamlCommand> {

	private static final Log log = LogFactory.getLog(UserLoginController.class);
	
	@Override
	public UserLoginSamlCommand instantiateCommand() {

		log.info("UserLoginSamlCommand in UserLoginSamlController initialized");
		
		// TODO Auto-generated method stub
		return new UserLoginSamlCommand();
	}

	@Override
	public View workOn(final UserLoginSamlCommand command) {
		
		log.info("UserLoginSamlController starts WorkOn");
		
		// TODO Auto-generated method stub
		return null;
	}

}
