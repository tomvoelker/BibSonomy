package org.bibsonomy.webapp.controller.ajax;

import org.bibsonomy.webapp.command.ajax.LoggingCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * Controller for ajax clicklog requests 
 * 
 * @author Sven Stefani
 * @version $Id$
 */
public class LoggingController extends AjaxController implements MinimalisticController<LoggingCommand> {

	@Override
	public View workOn(final LoggingCommand command) {
		// TODO: implement me
		return Views.AJAX;
	}	

	@Override
	public LoggingCommand instantiateCommand() {
		return new LoggingCommand();
	}
}