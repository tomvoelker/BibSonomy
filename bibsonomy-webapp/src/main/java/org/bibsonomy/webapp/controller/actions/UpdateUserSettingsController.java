package org.bibsonomy.webapp.controller.actions;

import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.UserUpdateOperation;
import org.bibsonomy.model.User;
import org.bibsonomy.model.UserSettings;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.util.UserUtils;
import org.bibsonomy.webapp.command.SettingsViewCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

/**
 * @author cvo
 * @version $Id$
 */
public class UpdateUserSettingsController implements MinimalisticController<SettingsViewCommand>, ErrorAware {
	
	private static final Log log = LogFactory.getLog(DeletePostController.class);

	private static final String TAB_URL = "/settings";

	private LogicInterface logic;
	private RequestLogic requestLogic;
	
	private Errors errors;
	
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
			if("logging".equals(action)) {
				/*
				 * change the log level
				 */
				actionLogging(command.getUser().getSettings(), user);
			} else if("api".equals(action)) {
				/*
				 * change the api key of a user
				 */
				actionAPI(user);
			} else if("layoutTagPost".equals(action)) {
				/*
				 * changes the layout of tag and post for a user
				 */
				actionLayoutTagPost(command, user);
			} else {
				errors.reject("error.invalid_parameter");
			}
			
		} else {
			errors.reject("error.field.valid.ckey");
		}
		
		
		/*
		 * if there are errors, show them
		 */
		if (errors.hasErrors()){
			//needed for the right values in the settings columns
			command.setUser(context.getLoginUser());
			
			return Views.SETTINGSPAGE;
		}
		
		
		// success: go back where you've come from
		return new ExtendedRedirectView("/settings?selTab=1");
	}
	
	private void actionLogging(final UserSettings commandSettings, final User user) {
		final UserSettings userSettings = user.getSettings();
		userSettings.setLogLevel(commandSettings.getLogLevel());
		userSettings.setConfirmDelete(commandSettings.isConfirmDelete());

		final String updatedUser = logic.updateUser(user, UserUpdateOperation.UPDATE_SETTINGS);
		log.debug("logging settings of user " + updatedUser + " has been changed successfully");
	}
	
	private void actionAPI(final User user) {
		logic.updateUser(user, UserUpdateOperation.UPDATE_API);
		
		log.debug("api key of " + user.getName() + " has been changed successfully");
	}
	
	private void actionLayoutTagPost(final SettingsViewCommand command, final User user) {
		final UserSettings commandSettings = command.getUser().getSettings();
		
		if(!commandSettings.isShowBibtex() && !commandSettings.isShowBookmark()) {
			errors.rejectValue("user.settings.showBookmark", "error.field.oneResourceMin");
			return;
		}
		final UserSettings userSettings = user.getSettings();
		
		userSettings.setDefaultLanguage(commandSettings.getDefaultLanguage());
		userSettings.setListItemcount(commandSettings.getListItemcount());
		userSettings.setTagboxTooltip(commandSettings.getTagboxTooltip());
		userSettings.setShowBookmark(commandSettings.isShowBookmark());
		userSettings.setShowBibtex(commandSettings.isShowBibtex());
		
		userSettings.setSimpleInterface(commandSettings.isSimpleInterface());

		userSettings.setIsMaxCount(commandSettings.getIsMaxCount());
		if (userSettings.getIsMaxCount()) {
			userSettings.setTagboxMaxCount(command.getChangeTo());
		} else {
			userSettings.setTagboxMinfreq(command.getChangeTo());
		}
		userSettings.setTagboxSort(commandSettings.getTagboxSort());
		userSettings.setTagboxStyle(commandSettings.getTagboxStyle());
		
		final String updatedUser = logic.updateUser(user, UserUpdateOperation.UPDATE_SETTINGS);
		log.debug("settings for the layout of tag boxes and post lists of user " + updatedUser + " has been changed successfully");
		/*
		 * trigger locale change
		 * 
		 * FIXME: There is code in InitUserFilter to change the locale and we
		 * have Spring classes (i.e., LocaleChangeInterceptor, SessionLocaleResolver) 
		 * to do this. We must unify this handling!
		 * 
		 * Another problem is, that we use low level setSessionAttribute() methods 
		 * instead of SessionLocaleResolver.setLocale(), because for the latter
		 * we would need the request + response which we don't have. 
		 */
		requestLogic.setSessionAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, new Locale(userSettings.getDefaultLanguage()));
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
	 * 
	 * @return requestLogic
	 */
	public LogicInterface getLogic() {
		return this.logic;
	}

	/**
	 * 
	 * @param logic
	 */
	public void setLogic(final LogicInterface logic) {
		this.logic = logic;
	}

	public RequestLogic getRequestLogic() {
		return this.requestLogic;
	}

	public void setRequestLogic(RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}
}
