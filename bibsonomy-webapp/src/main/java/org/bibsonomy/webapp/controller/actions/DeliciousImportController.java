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
import org.bibsonomy.webapp.command.SettingsViewCommand;
import org.bibsonomy.webapp.controller.SettingsPageController;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.Errors;


/**
 * @author schwass
 * @version $Id$
 */
public class DeliciousImportController extends SettingsPageController implements MinimalisticController<SettingsViewCommand>, ErrorAware {
	
	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(DeliciousImportController.class);

	private Errors errors = null;
	
	private String importBookmarksPath;

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
		try {
			return 
			"&" + "overwriteV1=" + command.isOverwriteV1()
			+ "&" + "importDataV1=" + command.getImportDataV1()
			+ "&" + "userName=" + URLEncoder.encode(command.getUserName(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			throw new InternServerException(ex);
		}
	}
	
	@SuppressWarnings("unused")
	protected String createRedirect(SettingsViewCommand command, RequestWrapperContext context, Errors errors) {
		
		/*
		 * FIXME: it is horrible security to put a password as GET parameter into the URL!
		 * 
		 * Please don't do this using redirects. Instead, directly process the import.
		 * 
		 */
		try {
			return importBookmarksPath
			+ "?" + "ckey=" + context.getCkey()
			+ "&" + "overwrite=" + command.isOverwriteV1()
			+ "&" + "importData=" + command.getImportDataV1()
			+ "&" + "passWord=" + URLEncoder.encode(command.getPassWord(), "UTF-8")
			+ "&" + "userName=" + URLEncoder.encode(command.getUserName(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			throw new InternServerException(ex.getMessage());
		}
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
	 * @param target
	 * @param errors
	 */
	protected void validate(SettingsViewCommand target, Errors errors) {
		if (!present(target.getUserName())) {
			errors.rejectValue("userName", "error.field.required");
		}
		if (!present(target.getPassWord())) {
			errors.rejectValue("passWord", "error.field.required");
		}
		
		if (!present(target.getImportDataV1()) || ( !"posts".equals(target.getImportDataV1()) && !"bundles".equals(target.getImportDataV1()) )) {
			errors.rejectValue("importDataV1", "error.field.required");
		}
	}

	/**
	 * @param importBookmarksPath
	 */
	public void setImportBookmarksPath(String importBookmarksPath) {
		this.importBookmarksPath = importBookmarksPath;
	}

}
