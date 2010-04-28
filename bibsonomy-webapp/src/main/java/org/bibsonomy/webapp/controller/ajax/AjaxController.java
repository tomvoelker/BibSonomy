package org.bibsonomy.webapp.controller.ajax;

import org.bibsonomy.model.UserSettings;
import org.bibsonomy.model.logic.LogicInterface;

/**
 * Controller for ajax requests
 * 
 * @author Stefan Stützer
 * @version $Id$
 */
public abstract class AjaxController {

	protected LogicInterface logic;	
	protected UserSettings userSettings;
	
	/**
	 * @param logic the logic to set
	 */
	public void setLogic(final LogicInterface logic) {
		this.logic = logic;
	}

	/**
	 * @param userSettings the userSettings to set
	 */
	public void setUserSettings(final UserSettings userSettings) {
		this.userSettings = userSettings;
	}	
}