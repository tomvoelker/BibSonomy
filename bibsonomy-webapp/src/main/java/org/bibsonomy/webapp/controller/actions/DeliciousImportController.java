/**
 * 
 */
package org.bibsonomy.webapp.controller.actions;

import static org.bibsonomy.util.ValidationUtils.present;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.importer.bookmark.service.DeliciousSignPost;
import org.bibsonomy.importer.bookmark.service.DeliciousSignPostManager;
import org.bibsonomy.webapp.command.SettingsViewCommand;
import org.bibsonomy.webapp.controller.SettingsPageController;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.Errors;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


/**
 * @author schwass
 * @version $Id$
 */
public class DeliciousImportController extends SettingsPageController implements MinimalisticController<SettingsViewCommand>, ErrorAware {
	
	private static final Log log = LogFactory.getLog(DeliciousImportController.class);

	private Errors errors = null;
	
	private DeliciousSignPostManager signPostManager;

	@Override
	public View workOn(SettingsViewCommand command) {
		final RequestWrapperContext context = command.getContext();
		
		
		if (!command.getContext().isUserLoggedIn()) {
			throw new AccessDeniedException("please log in");
		}

		/*
		 * check credentials to fight CSRF attacks 
		 * 
		 */
		if (!context.isValidCkey()) {
			errors.reject("error.field.valid.ckey");
			return super.workOn(command);
		}
		
		validate(command, errors);

		if (errors.hasErrors()) {
			return super.workOn(command);
		}
		
		final String redirectURI = createRedirect(command, context, errors);

		if (errors.hasErrors()) {
			return super.workOn(command);
		}
		
		return new ExtendedRedirectView(redirectURI);
		
	}
	
	protected String createRefererQuery(SettingsViewCommand command) {
		return 
		"&" + "overwriteV2=" + command.isOverwriteV2()
		+ "&" + "importDataV2=" + command.getImportDataV2();
	}
	
	protected String createRedirect(SettingsViewCommand command, RequestWrapperContext context, Errors errors) {
		
		final DeliciousSignPost oAuth = signPostManager.createDeliciousSignPost();
		final ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		attr.setAttribute(signPostManager.getoAuthKey(), oAuth, ServletRequestAttributes.SCOPE_SESSION);
		
	    try {
	    	return oAuth.getRequestToken(signPostManager.getCallbackBaseUrl()
	    				+ "?" + "ckey=" + context.getCkey()
						+ "&" + "overwrite=" + command.isOverwriteV2()
						+ "&" + "importData=" + command.getImportDataV2());
		} catch (Exception ex) {
			attr.removeAttribute(signPostManager.getoAuthKey(), ServletRequestAttributes.SCOPE_SESSION);
			errors.reject("error.furtherInformations", new Object[]{ex.getMessage()}, "The following error occurred: {0}");
			log.warn("Delicious-Import failed: " + ex.getMessage());
		}
		
		return null;
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
	
	/**
	 * @param target
	 * @param errors
	 */
	protected void validate(SettingsViewCommand target, Errors errors) {
		if (!present(target.getImportDataV2()) || ( !"posts".equals(target.getImportDataV2()) && !"bundles".equals(target.getImportDataV2()) )) {
			errors.rejectValue("importDataV2", "error.field.required");
		}
	}

}
