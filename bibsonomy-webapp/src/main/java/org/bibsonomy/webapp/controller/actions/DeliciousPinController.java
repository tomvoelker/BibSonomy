/**
 * 
 */
package org.bibsonomy.webapp.controller.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.importer.bookmark.service.DeliciousSignPost;
import org.bibsonomy.importer.bookmark.service.DeliciousSignPostManager;
import org.bibsonomy.webapp.command.SettingsViewCommand;
import org.bibsonomy.webapp.command.actions.DeliciousPinCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.ValidationAwareController;
import org.bibsonomy.webapp.util.Validator;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.validation.DeliciousPinValidator;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


/**
 * @author schwass
 * @version $Id$
 */
public class DeliciousPinController implements MinimalisticController<DeliciousPinCommand>, ErrorAware, ValidationAwareController<DeliciousPinCommand> {
	
	private static final Log log = LogFactory.getLog(DeliciousPinController.class);

	private Errors errors = null;
	
	private DeliciousSignPostManager signPostManager;

	@Override
	public DeliciousPinCommand instantiateCommand() {
		return new DeliciousPinCommand();
	}

	@Override
	public View workOn(DeliciousPinCommand command) {
		command.setSelTab(SettingsViewCommand.IMPORTS_IDX);
		final RequestWrapperContext context = command.getContext();

		/*
		 * only users which are logged in might post -> send them to
		 * login page
		 */
		if (!context.isUserLoggedIn()) {
			/*
			 * FIXME: send user back to this controller
			 */
			return new ExtendedRedirectView("/login");
		}

		/*
		 * check credentials to fight CSRF attacks 
		 * 
		 */
		if (!context.isValidCkey()) {
			errors.reject("error.field.valid.ckey");
			/*
			 * FIXME: correct URL?
			 * FIXME: don't do this on first call of form!
			 */
			return Views.SETTINGSPAGE;
		}

		if (errors.hasErrors()) {
			return Views.SETTINGSPAGE;
		}
		
		DeliciousSignPost oAuth = signPostManager.createDeliciousSignPost();
		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		attr.setAttribute(signPostManager.getoAuthKey(), oAuth, ServletRequestAttributes.SCOPE_SESSION);
		
		String redirectURI = null;
		
	    try {
	    	redirectURI = oAuth.getRequestToken(signPostManager.getCallbackBaseUrl()
	    				+ "?" + "ckey=" + context.getCkey()
						+ "&" + "overwrite=" + command.isOverwrite()
						+ "&" + "importData=" + command.getImportData());
		} catch (Exception ex) {
			attr.removeAttribute(signPostManager.getoAuthKey(), ServletRequestAttributes.SCOPE_SESSION);
			errors.reject("error.furtherInformations", new Object[]{ex.getMessage()}, "The following error occurred: {0}");
			log.warn("Delicious-Import failed: " + ex.getMessage());
		}
		
		return new ExtendedRedirectView(redirectURI);
		
	}

	@Override
	public Errors getErrors() {
		return errors;
	}

	@Override
	public void setErrors(Errors errors) {
		this.errors = errors;
	}

	/**
	 * @param signPostFactory
	 */
	public void setSignPostManager(DeliciousSignPostManager signPostFactory) {
		this.signPostManager = signPostFactory;
	}

	/**
	 * @return the DeliciousSignPostFactory
	 */
	public DeliciousSignPostManager getSignPostManager() {
		return signPostManager;
	}

	@Override
	public boolean isValidationRequired(DeliciousPinCommand command) {
		return true;
	}

	@Override
	public Validator<DeliciousPinCommand> getValidator() {
		return new DeliciousPinValidator();
	}

}
