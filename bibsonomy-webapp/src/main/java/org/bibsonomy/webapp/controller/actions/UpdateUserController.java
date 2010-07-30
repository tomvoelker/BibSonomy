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
public class UpdateUserController implements ErrorAware, ValidationAwareController<SettingsViewCommand> {


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
	private LogicInterface logic = null;

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

		command.setUserFriends(logic.getUserFriends(command.getUser()));
		command.setFriendsOfUser(logic.getFriendsOfUser(command.getUser()));
		
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
			updateUserProfile(user, command.getUser(), command.getProfilePrivlevel());

		} else {
			errors.reject("error.field.valid.ckey");
		}

		return Views.SETTINGSPAGE;
	}

	/**
	 * updates the the profile settings of a user
	 * @param user
	 * @param command
	 */
	private void updateUserProfile(final User user, final User commandUser, final String profilePrivlevel) {
		user.setRealname(commandUser.getRealname());
		user.setGender(commandUser.getGender());
		user.setBirthday(commandUser.getBirthday());
		
		user.setEmail(commandUser.getEmail());
		user.setHomepage(commandUser.getHomepage());
		user.setOpenURL(commandUser.getOpenURL());
		user.setProfession(commandUser.getProfession());
		user.setInterests(commandUser.getInterests());
		user.setHobbies(commandUser.getHobbies());
		user.setPlace(commandUser.getPlace());
		
		/*
		 * FIXME: use command.user.privlevel instead of string "group"!
		 */
		user.getSettings().setProfilePrivlevel(ProfilePrivlevel.getProfilePrivlevel(profilePrivlevel));
		
		final String updatedUser = logic.updateUser(user, UserUpdateOperation.UPDATE_CORE);
		log.debug("logging profile of user " + updatedUser + " has been changed successfully");
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
	public LogicInterface getLogic() {
		return this.logic;
	}

	/**
	 * @param logic the adminLogic to set
	 */
	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}
}
