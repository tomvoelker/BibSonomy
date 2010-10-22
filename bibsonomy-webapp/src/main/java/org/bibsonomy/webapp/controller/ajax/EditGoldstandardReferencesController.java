package org.bibsonomy.webapp.controller.ajax;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Set;

import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.webapp.command.ajax.EditGoldstandardReferencesCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * @author dzo
 * @version $Id$
 */
public class EditGoldstandardReferencesController extends AjaxController implements MinimalisticController<EditGoldstandardReferencesCommand> {
	
	@Override
	public EditGoldstandardReferencesCommand instantiateCommand() {
		return new EditGoldstandardReferencesCommand();
	}

	@Override
	public View workOn(final EditGoldstandardReferencesCommand command) {
		final RequestWrapperContext context = command.getContext();
		if (!context.isUserLoggedIn() || !Role.ADMIN.equals(context.getLoginUser().getRole())) {
			throw new AccessDeniedException("You are not allowed to edit references of a goldstandard");
		}
		
		final String hash = command.getHash();
		final Set<String> references = command.getReferences();
		
		if (!present(hash) || !present(references)) {
			return Views.AJAX_TEXT;
		}
		
		final String action = command.getAction();
		if ("add".equals(action)) {
			this.logic.createReferences(hash, references);
		} else if ("remove".equals(action)) {
			this.logic.deleteReferences(hash, references);
		} else {
			command.setResponseString("error");
		}
		
		return Views.AJAX_TEXT;
	}

}
