package org.bibsonomy.webapp.controller.ajax;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Collections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.PostUpdateOperation;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.webapp.command.ajax.PrivateNoteAjaxCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

/**
 * @author wla
 */
public class PrivateNoteAjaxController extends AjaxController implements MinimalisticController<PrivateNoteAjaxCommand>, ErrorAware {
	private static final Log log = LogFactory.getLog(PrivateNoteAjaxController.class);
	
	private Errors errors;
	
	@Override
	public PrivateNoteAjaxCommand instantiateCommand() {
		return new PrivateNoteAjaxCommand();
	}

	@Override
	public View workOn(final PrivateNoteAjaxCommand command) {
		final RequestWrapperContext context = command.getContext();
		if (!context.isUserLoggedIn()) {
			throw new AccessDeniedException();
		}
		
		if (!context.isValidCkey()) {
			this.errors.reject("error.field.valid.ckey");
		}
		
		final Post<? extends Resource> post = this.logic.getPostDetails(command.getIntraHash(), context.getLoginUser().getName());
		if (!present(post)) {
			this.errors.reject("error.general");
			log.error("post not found");
		}
		
		if (this.errors.hasErrors()) {
			return this.getErrorView();
		}
		
		final BibTex bib = (BibTex) post.getResource();
		bib.setPrivnote(command.getPrivateNote());
		
		this.logic.updatePosts(Collections.<Post<?>>singletonList(post), PostUpdateOperation.UPDATE_ALL);
		
		command.setResponseString("OK");
		return Views.AJAX_JSON;
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