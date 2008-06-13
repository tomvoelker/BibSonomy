package org.bibsonomy.webapp.controller.actions;

import org.bibsonomy.webapp.command.BaseCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/** This controller just returns the user registration success view.
 * 
 * @author rja
 * @version $Id$
 */
public class UserRegistrationSuccessController implements MinimalisticController<BaseCommand> {

	/** 
	 * Returns an instance of the command the controller handles.
	 * 
	 * @see org.bibsonomy.webapp.util.MinimalisticController#instantiateCommand()
	 */
	public BaseCommand instantiateCommand() {
		return new BaseCommand();
	}


	/** Main method which does the registration.
	 * 
	 * @see org.bibsonomy.webapp.util.MinimalisticController#workOn(java.lang.Object)
	 */
	public View workOn(BaseCommand command) {
		return Views.REGISTER_USER_SUCCESS;
	}

}
