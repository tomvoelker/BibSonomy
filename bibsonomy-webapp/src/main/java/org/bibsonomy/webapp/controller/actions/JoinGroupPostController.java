package org.bibsonomy.webapp.controller.actions;

import org.bibsonomy.webapp.command.actions.JoinGroupPostCommand;
import org.bibsonomy.webapp.controller.CheckDispatchingController;
import org.bibsonomy.webapp.view.Views;

/**
 * @author schwass
 * @version $Id$
 */
public class JoinGroupPostController extends CheckDispatchingController<JoinGroupPostCommand> {
	
	/**
	 * Constructor.
	 */
	public JoinGroupPostController() {
		super(Views.GROUPPAGE, Views.JOIN_GROUP);
		addUserLoggedInCheck(new UserLoggedInCheck());
		addCaptchaCheck(new CaptchaResponseCheck());
		addCheck(new ErrorsExistsCheck());
	}

	@Override
	public JoinGroupPostCommand instantiateCommand() {
		return new JoinGroupPostCommand();
	}

}
