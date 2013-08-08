package org.bibsonomy.webapp.controller.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.webapp.command.BaseCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;

/**
 * @author nilsraabe
 * @version $Id$
 */
public class UserLoginSamlController implements MinimalisticController<BaseCommand> {

	private static final Log log = LogFactory.getLog(UserLoginSamlController.class);
	
	@Override
	public BaseCommand instantiateCommand() {

		log.info("UserLoginSamlCommand in UserLoginSamlController initialized");
		return new BaseCommand();
	}

	@Override
	public View workOn(final BaseCommand command) {
		
		log.info("UserLoginSamlController starts WorkOn");
		
		return new ExtendedRedirectView("/");
	}

}
