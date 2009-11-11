package org.bibsonomy.webapp.controller.actions;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.webapp.command.actions.RemoveMessageCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

/**
 * With this Controller we delete Messages from a user's inbox
 * TODO: implement it ;-)
 * @author sdo
 * @version $Id$
 */
public class RemoveMessageController implements MinimalisticController<RemoveMessageCommand>, ErrorAware {
	private static final Log log = LogFactory.getLog(DeletePostController.class);
	
	private RequestLogic requestLogic;
	private LogicInterface logic;
	private Errors errors;

	
	public RemoveMessageCommand instantiateCommand() {
		return new RemoveMessageCommand();
	}
	
	public View workOn(RemoveMessageCommand command){
		RequestWrapperContext context = command.getContext();
		System.out.println("youve reached the deleteMessagecontroller");
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
			
			// delete the message with the given contentId
			ArrayList<Integer> contentIds=new ArrayList<Integer>();
			contentIds.add(command.getContentId());
			
			logic.deleteMessages(context.getLoginUser().getName(), contentIds);
			
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
	
	@Override
	public Errors getErrors() {
		return errors;
	}

	@Override
	public void setErrors(Errors errors) {
		this.errors=errors;

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
	
	
	/**
	 * @param requestLogic
	 */
	public void setRequestLogic(final RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}	


}
