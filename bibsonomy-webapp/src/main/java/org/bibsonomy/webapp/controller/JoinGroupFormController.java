package org.bibsonomy.webapp.controller;

import org.bibsonomy.webapp.command.JoinGroupFormCommand;
import org.bibsonomy.webapp.view.Views;

/**
 * @author schwass
 * @version $Id$
 */
public class JoinGroupFormController extends CheckDispatchingController<JoinGroupFormCommand> {

	/**
	 * Constructor
	 */
	public JoinGroupFormController() {
		super(Views.JOIN_GROUP, Views.JOIN_GROUP);
		addUserLoggedInCheck(new UserLoggedInCheck());
	}

	@Override
	public JoinGroupFormCommand instantiateCommand() {
		return new JoinGroupFormCommand();
	}
}
