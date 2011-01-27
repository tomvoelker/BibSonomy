package org.bibsonomy.webapp.controller.ajax;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.webapp.command.ajax.EditGoldstandardReferencesCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

/**
 * @author dzo
 * @version $Id$
 */
public class EditGoldstandardReferencesController extends AjaxController implements MinimalisticController<EditGoldstandardReferencesCommand>, ErrorAware {
	
	@Override
	public EditGoldstandardReferencesCommand instantiateCommand() {
		return new EditGoldstandardReferencesCommand();
	}

	private Errors errors;
	
	@Override
	public View workOn(final EditGoldstandardReferencesCommand command) {

		final RequestWrapperContext context = command.getContext();
		if (!context.isUserLoggedIn() || !Role.ADMIN.equals(context.getLoginUser().getRole())) {
			throw new AccessDeniedException("You are not allowed to edit references of a goldstandard");
		}

		//check if ckey is valid
		if (!context.isValidCkey()) {
			errors.reject("error.field.valid.ckey");
			return Views.ERROR;
		}
		
		final String hash = command.getHash();
		final Set<String> references = command.getReferences();
		
		if (!present(hash) || !present(references)) {
			this.responseLogic.setHttpStatus(HttpServletResponse.SC_BAD_REQUEST);
			return Views.AJAX_TEXT;
		}
		
		final HttpMethod httpMethod = this.requestLogic.getHttpMethod();
		
		switch (httpMethod) {
		case POST: 
			this.logic.createReferences(hash, references);
			break;
		case DELETE: 
			this.logic.deleteReferences(hash, references);
			break;
		default: 
			this.responseLogic.setHttpStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		}
		
		return Views.AJAX_TEXT;
	}

	@Override
	public Errors getErrors() {
		return errors;
	}

	@Override
	public void setErrors(Errors errors) {
		this.errors = errors;
	}

}
