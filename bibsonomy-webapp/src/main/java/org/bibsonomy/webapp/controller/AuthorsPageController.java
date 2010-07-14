package org.bibsonomy.webapp.controller;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.webapp.command.AuthorsCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * @author Christian Claus
 * @version $Id$
 */
public class AuthorsPageController extends SingleResourceListController implements MinimalisticController<AuthorsCommand>{

	@Override
	public View workOn(AuthorsCommand command) {
		command.setPageTitle("Authors");
		command.setAuthorList(this.logic.getAuthors(GroupingEntity.ALL, null, null, null, null, null, 0, Integer.MAX_VALUE, null));
		return Views.AUTHORSPAGE;
	}

	@Override
	public AuthorsCommand instantiateCommand() {
		return new AuthorsCommand();
	}
		
}
