package org.bibsonomy.webapp.controller.actions;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.ProfilePrivlevel;
import org.bibsonomy.common.enums.UserUpdateOperation;
import org.bibsonomy.common.errors.ErrorMessage;
import org.bibsonomy.common.errors.FieldLengthErrorMessage;
import org.bibsonomy.common.exceptions.DatabaseException;
import org.bibsonomy.model.User;
import org.bibsonomy.webapp.command.SettingsViewCommand;
import org.bibsonomy.webapp.controller.SettingsPageController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.ValidationAwareController;
import org.bibsonomy.webapp.util.Validator;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.util.spring.security.exceptions.AccessDeniedNoticeException;
import org.bibsonomy.webapp.validation.UserUpdateProfileValidator;
import org.springframework.validation.Errors;

/**
 * @author cvo
 * @version $Id$
 */
public class UpdateUserController extends SettingsPageController implements ValidationAwareController<SettingsViewCommand> {
	private static final Log log = LogFactory.getLog(UpdateUserController.class);

	@Override
	public View workOn(SettingsViewCommand command) {
		final RequestWrapperContext context = command.getContext();

		if (!context.isUserLoggedIn()) {
			throw new AccessDeniedNoticeException("please log in", "error.general.login");
		}

		/*
		 * go back to the settings page and display errors from command field
		 * validation
		 */
		if (this.errors.hasErrors()) {
			return super.workOn(command);
		}

		/*
		 * check the ckey
		 */
		if (context.isValidCkey()) {
			// update user profile 
			updateUserProfile(context.getLoginUser(), command.getUser());
		} else {
			this.errors.reject("error.field.valid.ckey");
		}

		return super.workOn(command);
	}



	/**
	 * updates the the profile settings of a user
	 * @param loginUser
	 * @param command
	 */
	private void updateUserProfile(final User loginUser, final User commandUser) {
		loginUser.setRealname(commandUser.getRealname());
		loginUser.setGender(commandUser.getGender());
		loginUser.setBirthday(commandUser.getBirthday());

		loginUser.setEmail(commandUser.getEmail());
		loginUser.setHomepage(commandUser.getHomepage());
		loginUser.setOpenURL(commandUser.getOpenURL());
		loginUser.setProfession(commandUser.getProfession());
		loginUser.setInstitution(commandUser.getInstitution());
		loginUser.setInterests(commandUser.getInterests());
		loginUser.setHobbies(commandUser.getHobbies());
		loginUser.setPlace(commandUser.getPlace());

		final ProfilePrivlevel profilePrivlevel = commandUser.getSettings().getProfilePrivlevel();
		
		loginUser.getSettings().setProfilePrivlevel(profilePrivlevel);

		updateUser(loginUser, errors);
	}

	/**
	 * Updates the user (including field length error checking!).
	 * 
	 * @param user
	 */
	private void updateUser(final User user, final Errors errors) {
		try {
			this.logic.updateUser(user, UserUpdateOperation.UPDATE_CORE);
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
	public Validator<SettingsViewCommand> getValidator() {
		return new UserUpdateProfileValidator();
	}

	@Override
	public boolean isValidationRequired(SettingsViewCommand command) {
		return true;
	}

}