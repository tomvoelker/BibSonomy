package org.bibsonomy.webapp.controller.actions;

import java.util.Collections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.services.URLGenerator;
import org.bibsonomy.webapp.command.actions.DeletePostCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Errors;

/**
 * @author Christian Kramer
 * @version $Id$
 */
public class DeletePostController implements MinimalisticController<DeletePostCommand>, ErrorAware{
	private static final Log log = LogFactory.getLog(DeletePostController.class);
	
	private RequestLogic requestLogic;
	private LogicInterface logic;
	private Errors errors;
	private URLGenerator urlGenerator;

	@Override
	public DeletePostCommand instantiateCommand() {
		return new DeletePostCommand();
	}

	@Override
	public View workOn(DeletePostCommand command) {
		RequestWrapperContext context = command.getContext();
		
		/*
		 * user has to be logged in to delete himself
		 */
		if (!context.isUserLoggedIn()){
			errors.reject("error.general.login");
		}
		
		/*
		 * check the ckey
		 */
		final String resourceHash = command.getResourceHash();
		final String loginUserName = context.getLoginUser().getName();
		if (context.isValidCkey() && !errors.hasErrors()){
			log.debug("User is logged in, ckey is valid");
			
			try {
				// delete the post
				logic.deletePosts(loginUserName, Collections.singletonList(resourceHash));
			} catch (IllegalStateException e) {
				errors.reject("error.post.notfound", new Object[]{resourceHash}, " The resource with ID [" + resourceHash + "] does not exist and could hence not be deleted.");
			}
		} else {
			errors.reject("error.field.valid.ckey");
		}
		

		/*
		 * if there are errors, show them
		 */
		if (errors.hasErrors()){
			return Views.ERROR;
		}
		
		/*
		 * Redirect to the user page when the user is coming from the page of 
		 * the resource.
		 */
		final String referer = requestLogic.getReferer();
		if (urlGenerator.matchesResourcePage(referer, loginUserName, resourceHash)) {
			return new ExtendedRedirectView(urlGenerator.getUserUrl(loginUserName));
		}
		
		/*
		 * go back where we've come from
		 */
		return new ExtendedRedirectView(referer);
	}

	/**
	 * @param logic
	 */
	public void setLogic(final LogicInterface logic) {
		this.logic = logic;
	}
	
	/**
	 * @return errors
	 */
	@Override
	public Errors getErrors() {
		return this.errors;
	}

	/**
	 * @param errors
	 */
	@Override
	public void setErrors(final Errors errors) {
		this.errors = errors;
	}
	
	
	/**
	 * @param requestLogic
	 */
	public void setRequestLogic(final RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}

	/**
	 * @param urlGenerator
	 */
	@Required
	public void setUrlGenerator(URLGenerator urlGenerator) {
		this.urlGenerator = urlGenerator;
	}	

}