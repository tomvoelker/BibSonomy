package org.bibsonomy.webapp.controller.ajax;

import org.bibsonomy.model.UserSettings;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.webapp.util.RequestAware;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.ResponseAware;
import org.bibsonomy.webapp.util.ResponseLogic;
import org.springframework.beans.factory.annotation.Required;

/**
 * Controller for ajax requests
 * 
 * @author Stefan Stützer
 * @version $Id$
 */
public abstract class AjaxController implements RequestAware, ResponseAware {

	protected LogicInterface logic;	
	protected UserSettings userSettings;
	
	protected RequestLogic requestLogic;
	protected ResponseLogic responseLogic;
	
	/**
	 * @param logic the logic to set
	 */
	@Required
	public void setLogic(final LogicInterface logic) {
		this.logic = logic;
	}

	/**
	 * @param userSettings the userSettings to set
	 */
	@Required
	public void setUserSettings(final UserSettings userSettings) {
		this.userSettings = userSettings;
	}

	@Override
	@Required
	public void setRequestLogic(RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}
	
	@Override
	@Required
	public void setResponseLogic(ResponseLogic responseLogic) {
		this.responseLogic = responseLogic;
	}
}