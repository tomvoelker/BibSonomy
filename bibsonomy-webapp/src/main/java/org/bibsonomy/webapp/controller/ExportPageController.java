package org.bibsonomy.webapp.controller;

import org.bibsonomy.webapp.command.ResourceViewCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * @author Christian
 * @version $Id$
 */
public class ExportPageController implements MinimalisticController<ResourceViewCommand> {

	/** 
	 * Returns an instance of the command the controller handles.
	 * 
	 * @see org.bibsonomy.webapp.util.MinimalisticController#instantiateCommand()
	 */
	public ResourceViewCommand instantiateCommand() {
		
		return new ResourceViewCommand();
	}

	/** Main method which does the registration.
	 * 
	 * @see org.bibsonomy.webapp.util.MinimalisticController#workOn(java.lang.Object)
	 */
	public View workOn(ResourceViewCommand command) {
		
		return Views.EXPORT;
	}

}
