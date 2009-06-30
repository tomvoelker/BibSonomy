package org.bibsonomy.webapp.controller.actions;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.webapp.command.actions.DeletePostCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

/**
 * @author Christian Kramer
 * @version $Id$
 */
public class DeletePostController implements MinimalisticController<DeletePostCommand>, ErrorAware{
	private static final Logger log = Logger.getLogger(DeletePostController.class);
	
	private RequestLogic requestLogic;
	private LogicInterface logic;
	private Errors errors;

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
		if (context.isValidCkey() && !errors.hasErrors()){
			log.debug("User is logged in, ckey is valid");
			
			// create list for resourcehashes
			List<String> posts = new ArrayList<String>();
			
			// add the resource hash
			posts.add(command.getResourceHash());
			
			// delete it
			logic.deletePosts(context.getLoginUser().getName(), posts);
			
		} else {
			errors.reject("error.field.valid.ckey");
		}
		

		/*
		 * if there are errors, show them
		 */
		if (errors.hasErrors()){
			return Views.ERROR;
		}
		
		// go back where you've come from
		return new ExtendedRedirectView(requestLogic.getReferer());
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
	public Errors getErrors() {
		return this.errors;
	}

	/**
	 * @param errors
	 */
	public void setErrors(final Errors errors) {
		this.errors = errors;
	}
	
	
	/**
	 * @param requestLogic
	 */
	public void setRequestLogic(final RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}	

}