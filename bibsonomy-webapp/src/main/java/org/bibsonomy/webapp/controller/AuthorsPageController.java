package org.bibsonomy.webapp.controller;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.webapp.command.AuthorsCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * @author Christian Claus
 * @version $Id$
 */
public class AuthorsPageController implements MinimalisticController<AuthorsCommand> {

	private LogicInterface logic;
	
	@Override
	public View workOn(AuthorsCommand command) {
		command.setPageTitle("Authors"); // TODO: i18n
		command.setAuthorList(this.logic.getAuthors(GroupingEntity.ALL, null, null, null, null, null, 0, Integer.MAX_VALUE, null));
		return Views.AUTHORSPAGE;
	}

	@Override
	public AuthorsCommand instantiateCommand() {
		return new AuthorsCommand();
	}
	
	/**
	 * @param logic the logic to set
	 */
	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}
}
