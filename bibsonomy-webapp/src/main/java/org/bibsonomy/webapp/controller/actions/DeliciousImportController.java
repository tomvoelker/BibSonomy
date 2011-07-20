/**
 * 
 */
package org.bibsonomy.webapp.controller.actions;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.importer.bookmark.service.DeliciousSignPost;
import org.bibsonomy.importer.bookmark.service.DeliciousSignPostManager;
import org.bibsonomy.webapp.command.SettingsViewCommand;
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
public class DeliciousImportController implements MinimalisticController<SettingsViewCommand>, ErrorAware {
	
	static final Log log = LogFactory.getLog(DeliciousImportController.class);

	private Errors errors = null;
	
	private DeliciousSignPostManager signPostManager;
	/**
	 * Path to login page for redirect if user not logged in
	 */
	private String loginPath;
	/**
	 * Path to this controller for referrer to login page
	 */
	private String controllerPath;

	@Override
	public SettingsViewCommand instantiateCommand() {
		SettingsViewCommand settingsViewCommand = new SettingsViewCommand();
		settingsViewCommand.setSelTab(SettingsViewCommand.IMPORTS_IDX);
		return settingsViewCommand;
	}

	@Override
	public View workOn(SettingsViewCommand command) {
		final RequestWrapperContext context = command.getContext();
		//check user logged in
		if(command.getContext().isUserLoggedIn() == false) {
			String redirectURI;
			try {
				redirectURI = loginPath + URLEncoder.encode(controllerPath + createRefererQuery(command), "UTF-8");
				return new ExtendedRedirectView(redirectURI );
			} catch (UnsupportedEncodingException ex) {
				throw new InternServerException(ex.getMessage());
			}
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
		
		validate(command, errors);

		if (errors.hasErrors()) {
			return Views.SETTINGSPAGE;
		}
		
		String redirectURI = createRedirect(command, context, errors);

		if (errors.hasErrors()) {
			return Views.SETTINGSPAGE;
		}
		
		return new ExtendedRedirectView(redirectURI);
		
	}
	
	protected String createRefererQuery(SettingsViewCommand command) {
		return 
		"&" + "overwriteV2=" + command.isOverwriteV2()
		+ "&" + "importDataV2=" + command.getImportDataV2();
	}
	
	protected String createRedirect(SettingsViewCommand command, RequestWrapperContext context, Errors errors) {
		
		DeliciousSignPost oAuth = signPostManager.createDeliciousSignPost();
		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		attr.setAttribute(signPostManager.getoAuthKey(), oAuth, ServletRequestAttributes.SCOPE_SESSION);
		
		String redirectURI = null;
		
	    try {
	    	redirectURI = oAuth.getRequestToken(signPostManager.getCallbackBaseUrl()
	    				+ "?" + "ckey=" + context.getCkey()
						+ "&" + "overwrite=" + command.isOverwriteV2()
						+ "&" + "importData=" + command.getImportDataV2());
		} catch (Exception ex) {
			attr.removeAttribute(signPostManager.getoAuthKey(), ServletRequestAttributes.SCOPE_SESSION);
			errors.reject("error.furtherInformations", new Object[]{ex.getMessage()}, "The following error occurred: {0}");
			log.warn("Delicious-Import failed: " + ex.getMessage());
		}
		
		return redirectURI;
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
	 * @param loginPath
	 */
	public void setLoginPath(String loginPath) {
		this.loginPath = loginPath;
	}

	/**
	 * @return String that represents the path to the login page including the referers request
	 * parameter and '='.
	 * 
	 * '/login?referer='
	 */
	public String getLoginPath() {
		return loginPath;
	}

	/**
	 * @param controllerPath
	 */
	public void setControllerPath(String controllerPath) {
		this.controllerPath = controllerPath;
	}
	
	/**
	 * @param target
	 * @param errors
	 */
	public void validate(SettingsViewCommand target, Errors errors) {
		
		if (!present(target.getImportDataV2()) || ( !"posts".equals(target.getImportDataV2()) && !"bundles".equals(target.getImportDataV2()) )) {
			errors.rejectValue("importDataV2", "error.field.required");
		}
	}

}
