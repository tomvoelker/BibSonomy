package org.bibsonomy.webapp.controller;

import org.bibsonomy.model.UserSettings;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.webapp.command.AjaxCommand;

/**
 * Controller for ajax requests
 * 
 * @author Stefan Stützer
 * @version $Id$
 */
public abstract class AjaxController {

	protected LogicInterface logic;	
	protected UserSettings userSettings;
	
	public void setResponse(AjaxCommand cmd, String response) {
		cmd.setResponseString(response);
	}

	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}

	public void setUserSettings(UserSettings userSettings) {
		this.userSettings = userSettings;
	}	
}