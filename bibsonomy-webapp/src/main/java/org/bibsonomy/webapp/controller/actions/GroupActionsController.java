package org.bibsonomy.webapp.controller.actions;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.webapp.command.actions.GroupActionsCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.springframework.validation.Errors;

/**
 * @author schwass
 * @version $Id$
 * @param <CT> Command Type
 */
public abstract class GroupActionsController<CT extends GroupActionsCommand> implements MinimalisticController<CT>, ErrorAware {

	protected static final Log log = LogFactory.getLog(UserRegistrationController.class);
	
	protected Errors errors = null;

	/**
	 * send as referrer to the login page
	 */
	private String actionURL;

	@Override
	public Errors getErrors() {
		return errors;
	}

	@Override
	public void setErrors(final Errors errors) {
		/*
		 * here: check for binding errors
		 */
		this.errors = errors;
	}
	
	/**
	 * @param actionURL
	 */
	public void setActionURL(String actionURL) {
		this.actionURL = actionURL;
	}

	/**
	 * @return 
	 */
	public String getActionURL() {
		return actionURL;
	}

	@Override
	public View workOn(CT command) {
		
		final RequestWrapperContext context = command.getContext();
		
		if(!context.isUserLoggedIn()) {
			try {
				return new ExtendedRedirectView(
						"/login?referer=" + URLEncoder.encode(
								actionURL + command.toQueryString(), "UTF-8"));
			} catch (UnsupportedEncodingException ex) {
				return new ExtendedRedirectView("/login?referer=" + actionURL + command.toQueryString());
			}
		}
		
		return workOnSpecial(command);
	}
	
	abstract View workOnSpecial(CT command);

}
