package org.bibsonomy.webapp.controller.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.UserUpdateOperation;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.util.UserUtils;
import org.bibsonomy.webapp.command.SettingsViewCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

/**
 * @author cvo
 * @version $Id$
 */
public class UpdateUserSettingsController implements MinimalisticController<SettingsViewCommand>, ErrorAware {
	
	private static final Log log = LogFactory.getLog(DeletePostController.class);

	private static final String TAB_URL = "/settings";

	private LogicInterface adminLogic;
	private Errors errors;
	private RequestLogic requestLogic;
	
	@Override
	public SettingsViewCommand instantiateCommand() {
		final SettingsViewCommand command = new SettingsViewCommand();
		command.setTabURL(TAB_URL);
		command.setUser(new User());
		return command;
	}

	@Override
	public View workOn(final SettingsViewCommand command) {
		// mit regenerate API key
				
		final RequestWrapperContext context = command.getContext();

		/*
		 * user has to be logged in to delete himself
		 */
		if (!context.isUserLoggedIn()) {
			errors.reject("error.general.login");
			return Views.SETTINGSPAGE;
		}
		
		final User user = context.getLoginUser(); 
		
		//check whether the user is a group		
		if(UserUtils.userIsGroup(user)) {
			command.setHasOwnGroup(true);
			command.showGroupTab(true);
		}
		
		/*
		 * check the ckey
		 */
		if (context.isValidCkey() && !errors.hasErrors()){
			log.debug("User is logged in, ckey is valid");
			
			//do set new settings here
			final String action = command.getAction();
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
			return Views.SETTINGSPAGE;
		}
		
		
		// go back where you've come from
		return Views.SETTINGSPAGE;
	}
	
	private void actionLogging(final SettingsViewCommand command, final User user) {
		user.getSettings().setLogLevel(command.getUser().getSettings().getLogLevel());
		user.getSettings().setConfirmDelete(command.getUser().getSettings().isConfirmDelete());

		final String updatedUser = adminLogic.updateUser(user, UserUpdateOperation.UPDATE_SETTINGS);
		log.info("logging settings of user " + updatedUser + " has been changed successfully");
	}
	
	private void actionAPI(final SettingsViewCommand command, final User user) {
		adminLogic.updateUser(user, UserUpdateOperation.UPDATE_API);
		
		log.info("api key of " + user.getName() + " has been changed successfully");
	}
	
	private void actionLayoutTagPost(final SettingsViewCommand command, final User user) {
		user.getSettings().setDefaultLanguage(command.getUser().getSettings().getDefaultLanguage());
		user.getSettings().setListItemcount(command.getUser().getSettings().getListItemcount());
		user.getSettings().setTagboxTooltip(command.getUser().getSettings().getTagboxTooltip());
		if(command.getUser().getSettings().getIsMaxCount()) {
			user.getSettings().setIsMaxCount(true);
			user.getSettings().setTagboxMaxCount(command.getChangeTo());
		} else {
			user.getSettings().setIsMaxCount(false);
			user.getSettings().setTagboxMinfreq(command.getChangeTo());
		}
		user.getSettings().setTagboxSort(command.getUser().getSettings().getTagboxSort());
		user.getSettings().setTagboxStyle(command.getUser().getSettings().getTagboxStyle());
		
		final String updatedUser = adminLogic.updateUser(user, UserUpdateOperation.UPDATE_SETTINGS);
		log.info("settings for the layout of tag boxes and post lists of user " + updatedUser + " has been changed successfully");
	}

	@Override
	public Errors getErrors() {
		return this.errors;
	}

	@Override
	public void setErrors(final Errors errors) {
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
	public void setRequestLogic(final RequestLogic requestLogic) {
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
	public void setAdminLogic(final LogicInterface adminLogic) {
		this.adminLogic = adminLogic;
	}
}
