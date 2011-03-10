/**
 * 
 */
package org.bibsonomy.webapp.controller.actions;

import org.bibsonomy.importer.bookmark.service.DeliciousSignPost;
import org.bibsonomy.importer.bookmark.service.DeliciousSignPostManager;
import org.bibsonomy.webapp.command.actions.ImportCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


/**
 * @author schwass
 * @version $Id$
 */
public class DeliciousPinController implements MinimalisticController<ImportCommand>, ErrorAware {

	private Errors errors = null;
	
	private DeliciousSignPostManager signPostManager;

	@Override
	public ImportCommand instantiateCommand() {
		return new ImportCommand();
	}

	@Override
	public View workOn(ImportCommand command) {
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
			return Views.IMPORT;
		}

		if (errors.hasErrors()) {
			return Views.IMPORT;
		}
		
		DeliciousSignPost oAuth = signPostManager.createDeliciousSignPost();
		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		attr.setAttribute(signPostManager.getoAuthKey(), oAuth, ServletRequestAttributes.SCOPE_SESSION);
	    return new ExtendedRedirectView(
	    		oAuth.getRequestToken(
	    				signPostManager.getCallbackBaseUrl()
	    				+ "?" + "ckey=" + context.getCkey()
	    				+ "&" + "overwrite=" + command.isOverwrite()
	    				+ "&" + "importData=" + command.getImportData()));
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

}
