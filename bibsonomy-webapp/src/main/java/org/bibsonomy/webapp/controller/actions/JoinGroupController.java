package org.bibsonomy.webapp.controller.actions;

import org.bibsonomy.webapp.command.actions.JoinGroupCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestAware;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.util.captcha.Captcha;
import org.bibsonomy.webapp.view.Views;
import org.springframework.beans.factory.annotation.Required;

/**
 * @author schwass
 * @version $Id$
 */
public class JoinGroupController implements MinimalisticController<JoinGroupCommand>, RequestAware {
	
	private Captcha captcha;
	private RequestLogic requestLogic;
	
	/**
	 * Constructor.
	 */
	public JoinGroupController() {
	}

	/** Give this controller an instance of {@link Captcha}.
	 * 
	 * @param captcha
	 */
	@Required
	public void setCaptcha(Captcha captcha) {
		this.captcha = captcha;
	}

	/** The logic needed to access the request
	 * @param requestLogic 
	 */
	@Override
	@Required
	public void setRequestLogic(RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}

	@Override
	public JoinGroupCommand instantiateCommand() {
		return new JoinGroupCommand();
	}

	@Override
	public View workOn(JoinGroupCommand command) {
		command.setCaptchaHTML(captcha.createCaptchaHtml(requestLogic.getLocale()));
		return Views.JOIN_GROUP;
	}

}
