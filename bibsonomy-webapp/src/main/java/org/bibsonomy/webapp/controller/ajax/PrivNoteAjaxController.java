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
import org.bibsonomy.webapp.command.ajax.PrivNoteAjaxCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

/**
 * @author wla
 * @version $Id$
 */
public class PrivNoteAjaxController extends AjaxController implements MinimalisticController<PrivNoteAjaxCommand>, ErrorAware {

	private static final Log log = LogFactory.getLog(PrivNoteAjaxController.class);
	private Errors errors;
	
	@Override
	public PrivNoteAjaxCommand instantiateCommand() {
		return new PrivNoteAjaxCommand();
	}

	@Override
	public View workOn(PrivNoteAjaxCommand command) {
		final RequestWrapperContext context = command.getContext();
		if (!context.isUserLoggedIn()) {
			throw new AccessDeniedException();
		}
		
		if (!context.isValidCkey()) {
			errors.reject("error.field.valid.ckey");
		}
		
		
		Post<? extends Resource> post = logic.getPostDetails(command.getIntraHash(), context.getLoginUser().getName());
		if(!present(post)) {
			errors.reject("error.general");
			log.error("post not found");
		}
		
		
		BibTex bib = (BibTex) post.getResource();
		bib.setPrivnote(command.getPrivNote());
		
		//need this to update change date
		post.setChangeDate(null);
		
		logic.updatePosts(Collections.<Post<?>>singletonList(post), PostUpdateOperation.UPDATE_ALL);
		
		if(errors.hasErrors()) {
			returnErrorView();
		}
		
		command.setResponseString("");		
		return Views.AJAX_JSON;
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
