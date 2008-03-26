package org.bibsonomy.webapp.controller;

import org.apache.log4j.Logger;
import org.bibsonomy.model.User;
import org.bibsonomy.model.UserSettings;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.webapp.command.AjaxCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * Controller for ajax requests
 * 
 * @author Stefan Stützer
 * @version $Id$
 */
public class AjaxController implements MinimalisticController<AjaxCommand> {

	private static final Logger log = Logger.getLogger(AjaxController.class);
	
	private LogicInterface logic;
	
	private UserSettings userSettings;

	public View workOn(AjaxCommand command) {
		final String action = command.getAction();
		
		if ("flag_spammer".equals(action)) {
			this.flagSpammer(command, true);
			this.setResponse(command, command.getUserName() + " flagged as spammer");
		} else if ("unflag_spammer".equals(action)) {
			this.flagSpammer(command, false);
			this.setResponse(command, command.getUserName() + " flagged as nonspammer");
		}
		return Views.AJAX;
	}		

	/**
	 * flags a user as spammer
	 * @param cmd
	 */
	private void flagSpammer(AjaxCommand cmd, boolean spammer) {		
		if (cmd.getUserName() != null) {
			User user = new User(cmd.getUserName());
			user.setToClassify(0);
			user.setAlgorithm("admin");
			user.setSpammer(spammer ? 1 : 0);
			
			this.logic.updateUser(user);
		}
	}
	
	public void setResponse(AjaxCommand cmd, String response) {
		cmd.setResponse(response);
	}

	public AjaxCommand instantiateCommand() {
		return new AjaxCommand();
	}

	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}

	public void setUserSettings(UserSettings userSettings) {
		this.userSettings = userSettings;
	}	
}