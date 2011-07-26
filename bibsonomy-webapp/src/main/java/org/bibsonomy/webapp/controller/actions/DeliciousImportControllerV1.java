package org.bibsonomy.webapp.controller.actions;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.webapp.command.SettingsViewCommand;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.springframework.validation.Errors;

/**
 * @author schwass
 * @version $Id$
 */
public class DeliciousImportControllerV1 extends DeliciousImportController {
	
	private String importBookmarksPath;
	
	@Override
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
	
	@Override
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
	
	/**
	 * @param target
	 * @param errors
	 */
	@Override
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
