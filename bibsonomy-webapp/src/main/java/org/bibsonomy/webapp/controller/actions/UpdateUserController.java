package org.bibsonomy.webapp.controller.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.ProfilePrivlevel;
import org.bibsonomy.common.enums.UserUpdateOperation;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.util.UserUtils;
import org.bibsonomy.webapp.command.SettingsViewCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.ValidationAwareController;
import org.bibsonomy.webapp.util.Validator;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.validation.UserUpdateProfileValidator;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

/**
 * @author cvo
 * @version $Id$
 */
public class UpdateUserController implements MinimalisticController<SettingsViewCommand>, ErrorAware, ValidationAwareController<SettingsViewCommand> {


	private static final Log log = LogFactory.getLog(UpdateUserController.class);

	/**
	 * maps the settings url
	 */
	private static final String TAB_URL = "/settings";

	/**
	 * hold current errors
	 */
	private Errors errors = null;

	/**
	 * logic interface
	 */
	private LogicInterface adminLogic = null;

	@Override
	public SettingsViewCommand instantiateCommand() {
		final SettingsViewCommand command = new SettingsViewCommand();
		command.setTabURL(TAB_URL);
		command.setUser(new User());
		return command;
	}

	@Override
	public View workOn(SettingsViewCommand command) {
		RequestWrapperContext context = command.getContext();
		
		/*
		 * user has to be logged in to delete himself
		 */
		if (!context.isUserLoggedIn()) {
			errors.reject("error.general.login");
			return Views.SETTINGSPAGE;
		}
		
		final User user = context.getLoginUser(); 
		
		// needed to display the user name on the profile tab of the settings site
		command.getUser().setName(user.getName());

		command.setUserFriends(adminLogic.getUserFriends(command.getUser()));
		command.setFriendsOfUser(adminLogic.getFriendsOfUser(command.getUser()));
		
		// check whether the user is a group		
		if (UserUtils.userIsGroup(user)) {
			command.setHasOwnGroup(true);
			command.showGroupTab(true);
		}
		
		/**
		 * go back to the settings page and display errors from command field
		 * validation
		 */
		if (errors.hasErrors()) {
			return Views.SETTINGSPAGE;
		}

		/*
		 * check the ckey
		 */
		if (context.isValidCkey()) {
			log.debug("User is logged in, ckey is valid");

			// update user informations here
			updateUserProfile(command, user);

		} else {
			errors.reject("error.field.valid.ckey");
		}

		return Views.SETTINGSPAGE;
	}

	/**
	 * updates the the profile settings of a user
	 * @param command
	 * @param user
	 */
	private void updateUserProfile(final SettingsViewCommand command, final User user) {
		user.setRealname(command.getUser().getRealname());
		user.setGender(command.getUser().getGender());
		user.setBirthday(command.getUser().getBirthday());
		
		user.setEmail(command.getUser().getEmail());
		user.setHomepage(command.getUser().getHomepage());
		user.setOpenURL(command.getUser().getOpenURL());
		user.setProfession(command.getUser().getProfession());
		user.setInterests(command.getUser().getInterests());
		user.setHobbies(command.getUser().getHobbies());
		user.setPlace(command.getUser().getPlace());
		
		/*
		 * FIXME: use command.user.privlevel instead of string "group"!
		 */
		user.getSettings().setProfilePrivlevel(ProfilePrivlevel.getProfilePrivlevel(command.getProfilePrivlevel()));
		
		final String updatedUser = adminLogic.updateUser(user, UserUpdateOperation.UPDATE_CORE);
		log.info("logging profile of user " + updatedUser + " has been changed successfully");
	}

	@Override
	public Errors getErrors() {
		return this.errors;
	}

	@Override
	public void setErrors(Errors errors) {
		this.errors = errors;		
	}

	@Override
	public Validator<SettingsViewCommand> getValidator() {
		return new UserUpdateProfileValidator();
	}

	@Override
	public boolean isValidationRequired(SettingsViewCommand command) {
		return true;
	}

	/**
	 * @return the adminLogic
	 */
	public LogicInterface getAdminLogic() {
		return this.adminLogic;
	}

	/**
	 * @param adminLogic the adminLogic to set
	 */
	public void setAdminLogic(LogicInterface adminLogic) {
		this.adminLogic = adminLogic;
	}
}
