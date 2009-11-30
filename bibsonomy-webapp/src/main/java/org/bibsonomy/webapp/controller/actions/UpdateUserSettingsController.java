package org.bibsonomy.webapp.controller.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.UserUpdateOperation;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.webapp.command.actions.UpdateUserSettingsCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

/**
 * @author cvo
 * @version $Id$
 */
public class UpdateUserSettingsController implements MinimalisticController<UpdateUserSettingsCommand>, ErrorAware {
	
	private static final Log log = LogFactory.getLog(DeletePostController.class);


	private LogicInterface adminLogic;
	private Errors errors;
	private RequestLogic requestLogic;
	
	@Override
	public UpdateUserSettingsCommand instantiateCommand() {
		
		return new UpdateUserSettingsCommand();
	}

	@Override
	public View workOn(UpdateUserSettingsCommand command) {
		// mit regenerate API key
		
		
		RequestWrapperContext context = command.getContext();
		
		/*
		 * user has to be logged in to delete himself
		 */
		if (!context.isUserLoggedIn()){
			errors.reject("error.general.login");
		}
		
		User user = context.getLoginUser(); 
		
		/*
		 * check the ckey
		 */
		if (context.isValidCkey() && !errors.hasErrors()){
			log.debug("User is logged in, ckey is valid");
			
			//do set new settings here
			String action = command.getAction();
			if(action.equals("logging")) {
				// changes the log level
				actionLogging(command, user);
			}else if(action.equals("api")) {
				// changes the api key of a user
				actionAPI(command, user);
			}else if(action.equals("layoutTagPost")) {
				// changes the layout of tag and post for a user
				actionLayoutTagPost(command, user);
			}else {
				errors.reject("error.invalid_parameter");
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
		
		
		// go back where you've come from
		return new ExtendedRedirectView(requestLogic.getReferer());
	}
	
	private void actionLogging(UpdateUserSettingsCommand command, User user) {
		
		user.getSettings().setLogLevel(command.getLogLevel());
		user.getSettings().setConfirmDelete(command.isConfirmDelete());

		String updatedUser = adminLogic.updateUser(user, UserUpdateOperation.UPDATE_SETTINGS);
		
		log.info("logging settings of user " + updatedUser + " has been changed successfully");
	}
	
	private void actionAPI(UpdateUserSettingsCommand command, User user) {
		
		adminLogic.updateUser(user, UserUpdateOperation.UPDATE_API);
		
		log.info("api key of " + user.getName() + " has been changed successfully");
	}
	
	private void actionLayoutTagPost(UpdateUserSettingsCommand command, User user) {
		
		user.getSettings().setDefaultLanguage(command.getDefaultLanguage());
		user.getSettings().setListItemcount(command.getItemcount());
		user.getSettings().setTagboxTooltip(command.getTagboxTooltip());
		user.getSettings().setTagboxMinfreq(command.getTagboxMinfreq());
		user.getSettings().setTagboxSort(command.getTagSort());
		user.getSettings().setTagboxStyle(command.getTagboxStyle());
		
		String updatedUser = adminLogic.updateUser(user, UserUpdateOperation.UPDATE_SETTINGS);
		
		log.info("settings for the layout of tag boxes and post lists of user " + updatedUser + " has been changed successfully");
	}

	@Override
	public Errors getErrors() {
		
		return this.errors;
	}

	@Override
	public void setErrors(Errors errors) {
		
		this.errors = errors;		
	}

	/**
	 * @return requestLogic
	 */
	public RequestLogic getRequestLogic() {
		return this.requestLogic;
	}

	/**
	 * 
	 * @param requestLogic
	 */
	public void setRequestLogic(RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}

	/**
	 * 
	 * @return requestLogic
	 */
	public LogicInterface getAdminLogic() {
		return this.adminLogic;
	}

	/**
	 * 
	 * @param adminLogic
	 */
	public void setAdminLogic(LogicInterface adminLogic) {
		this.adminLogic = adminLogic;
	}
}
