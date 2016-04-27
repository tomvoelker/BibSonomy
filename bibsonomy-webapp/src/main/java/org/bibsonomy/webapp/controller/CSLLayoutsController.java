package org.bibsonomy.webapp.controller;

import org.bibsonomy.webapp.command.CSLLayoutCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * TODO: add documentation to this class
 *
 * @author jp
 */
public class CSLLayoutsController implements MinimalisticController<CSLLayoutCommand>{

	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.util.MinimalisticController#instantiateCommand()
	 */
	@Override
	public CSLLayoutCommand instantiateCommand() {
		return new CSLLayoutCommand();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.util.MinimalisticController#workOn(org.bibsonomy.webapp.command.ContextCommand)
	 */
	@Override
	public View workOn(CSLLayoutCommand command) {
		command.setDebug(command.getContext().getQueryString());
		return Views.CSL_LAYOUT;
	}
}
