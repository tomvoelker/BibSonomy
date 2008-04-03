package org.bibsonomy.webapp.controller;

import org.apache.log4j.Logger;
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

	private static final Logger log = Logger.getLogger(AjaxController.class);	
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