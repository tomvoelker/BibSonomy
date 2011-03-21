package org.bibsonomy.webapp.controller.actions;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.ProfilePrivlevel;
import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.common.enums.UserUpdateOperation;
import org.bibsonomy.common.errors.ErrorMessage;
import org.bibsonomy.common.errors.FieldLengthErrorMessage;
import org.bibsonomy.common.exceptions.DatabaseException;
import org.bibsonomy.model.User;
import org.bibsonomy.model.Wiki;
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
		command.setUser(new User());
		return command;
	}

	@Override
	public View workOn(SettingsViewCommand command) {
		final RequestWrapperContext context = command.getContext();

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

		command.setUserFriends(logic.getUserRelationship(user.getName(), UserRelation.FRIEND_OF, null));
		command.setFriendsOfUser(logic.getUserRelationship(user.getName(), UserRelation.OF_FRIEND, null));

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
			updateCvWiki(command.getUser(), command.getWikiText());
		} else {
			errors.reject("error.field.valid.ckey");
		}

		return Views.SETTINGSPAGE;
	}

	private void updateCvWiki(User user, String wikiText) {
		Wiki wiki = new Wiki();
		wiki.setWikiText(wikiText);
		wiki.setDate(new Date());
		
		logic.updateWiki(user.getName(), wiki);
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
		user.setInstitution(commandUser.getInstitution());
		user.setInterests(commandUser.getInterests());
		user.setHobbies(commandUser.getHobbies());
		user.setPlace(commandUser.getPlace());

		/*
		 * FIXME: use command.user.privlevel instead of string "group"!
		 */
		user.getSettings().setProfilePrivlevel(ProfilePrivlevel.getProfilePrivlevel(profilePrivlevel));

		updateUser(user, errors);
	}

	/**
	 * Updates the user (including field length error checking!).
	 * 
	 * @param user
	 */
	private void updateUser(final User user, final Errors errors) {
		try {
			logic.updateUser(user, UserUpdateOperation.UPDATE_CORE);
		} catch(DatabaseException e) {
			final List<ErrorMessage> messages = e.getErrorMessages().get(user.getName());

			for(final ErrorMessage eMsg : messages) {
				if(eMsg instanceof FieldLengthErrorMessage) {
					final FieldLengthErrorMessage fError = (FieldLengthErrorMessage) eMsg;
					final Iterator<String> it = fError.iteratorFields();
					while(it.hasNext()) {
						final String current = it.next();
						final String[] values = { String.valueOf(fError.getMaxLengthForField(current)) };
						errors.rejectValue("user." + current, "error.field.valid.limit_exceeded", values, fError.getDefaultMessage());
					}
				}
			}
		}
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
