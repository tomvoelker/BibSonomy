package org.bibsonomy.webapp.controller.ajax;

import static org.bibsonomy.util.ValidationUtils.present;

import javax.servlet.http.HttpServletResponse;

import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.model.Review;
import org.bibsonomy.webapp.command.ajax.ReviewCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

/**
 * - ajax/reviews
 * 
 * @author dzo
 * @version $Id$
 */
public class ReviewAjaxController extends AjaxController implements MinimalisticController<ReviewCommand>, ErrorAware {

	private Errors errors;

	@Override
	public ReviewCommand instantiateCommand() {
		final ReviewCommand reviewCommand = new ReviewCommand();
		reviewCommand.setReview(new Review());
		return reviewCommand;
	}
	
	@Override
	public View workOn(final ReviewCommand command) {
		final RequestWrapperContext context = command.getContext();
		if (!context.isUserLoggedIn()) {
			throw new AccessDeniedException();
		}
		
		if (!context.isValidCkey()) {
			errors.reject("error.field.valid.ckey");
			this.responseLogic.setHttpStatus(HttpServletResponse.SC_BAD_REQUEST);
			return Views.AJAX_XML;
		}
		
		final String hash = command.getHash();
		final Review review = command.getReview();
		final String username;
		if (present(command.getUsername())) {
			username = command.getUsername();
		} else {
			username = context.getLoginUser().getName();
		}
		
		try {
			switch(this.requestLogic.getHttpMethod()) {
				case POST:
					this.logic.createReview(username, hash, review);
					break;
				case PUT:
					this.logic.updateReview(username, hash, review);
					break;
				case DELETE:
					this.logic.deleteReview(username, hash);
					break;
				default:
					this.responseLogic.setHttpStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			}
		} catch (final ValidationException ex) {
			this.responseLogic.setHttpStatus(HttpServletResponse.SC_BAD_REQUEST);
			command.setResponseString("<error>" + ex.getMessage() + "</error>");
		}
		
		return Views.AJAX_XML;
	}

	@Override
	public Errors getErrors() {
		return this.errors;
	}

	@Override
	public void setErrors(final Errors errors) {
		this.errors = errors;
	}
}
