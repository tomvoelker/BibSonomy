package org.bibsonomy.webapp.controller;

import org.apache.log4j.Logger;
import org.bibsonomy.webapp.command.AuthorsCommand;
import org.bibsonomy.webapp.command.SimpleResourceViewCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * @author Christian Claus
 * @version $Id$
 */
public class AuthorsPageController extends SingleResourceListController implements MinimalisticController<AuthorsCommand>{
	private static final Logger LOGGER = Logger.getLogger(TagPageController.class);

	public View workOn(AuthorsCommand command) {
		command.setPageTitle("Authors");
		command.setAuthorList(this.logic.getAuthors());
		return Views.AUTHORSPAGE;
	}

	public AuthorsCommand instantiateCommand() {
		return new AuthorsCommand();
	}
		
}
