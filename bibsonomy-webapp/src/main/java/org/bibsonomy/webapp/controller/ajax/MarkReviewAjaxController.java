package org.bibsonomy.webapp.controller.ajax;

import javax.servlet.http.HttpServletResponse;

import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.webapp.command.ajax.MarkReviewAjaxCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * - ajax/reviews/mark
 * 
 * @author dzo
 * @version $Id$
 */
public class MarkReviewAjaxController extends AjaxController implements MinimalisticController<MarkReviewAjaxCommand>  {

	@Override
	public MarkReviewAjaxCommand instantiateCommand() {
		return new MarkReviewAjaxCommand();
	}

	@Override
	public View workOn(final MarkReviewAjaxCommand command) {
		final RequestWrapperContext context = command.getContext();
		if (!context.isUserLoggedIn()) {
			throw new AccessDeniedException();
		}
		
		if (!context.isValidCkey()) {
			this.responseLogic.setHttpStatus(HttpServletResponse.SC_BAD_REQUEST);
			return Views.ERROR;
		}
		
		try {
			this.logic.markReview(context.getLoginUser().getName(), command.getUsername(), command.getHash(), command.isHelpful());
		} catch (final ValidationException ex) {
			this.responseLogic.setHttpStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		
		return Views.AJAX_XML;
	}

}
