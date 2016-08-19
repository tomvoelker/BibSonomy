/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.controller.actions;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Iterator;
import java.util.List;

import org.bibsonomy.common.enums.ProfilePrivlevel;
import org.bibsonomy.common.enums.UserUpdateOperation;
import org.bibsonomy.common.errors.ErrorMessage;
import org.bibsonomy.common.errors.FieldLengthErrorMessage;
import org.bibsonomy.common.exceptions.DatabaseException;
import org.bibsonomy.model.User;
import org.bibsonomy.util.file.ServerDeletedFile;
import org.bibsonomy.util.file.ServerUploadedFile;
import org.bibsonomy.webapp.command.SettingsViewCommand;
import org.bibsonomy.webapp.controller.SettingsPageController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.ValidationAwareController;
import org.bibsonomy.webapp.util.Validator;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.util.spring.security.exceptions.AccessDeniedNoticeException;
import org.bibsonomy.webapp.validation.UserUpdateProfileValidator;
import org.bibsonomy.webapp.view.ExtendedRedirectViewWithAttributes;
import org.springframework.validation.Errors;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author cvo
 */
public class UpdateUserController extends SettingsPageController implements ValidationAwareController<SettingsViewCommand> {

	@Override
	public View workOn(final SettingsViewCommand command) {
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
			this.updateUserProfile(context.getLoginUser(), command.getUser(), command);
		} else {
			this.errors.reject("error.field.valid.ckey");
		}
		
		final ExtendedRedirectViewWithAttributes redirect = new ExtendedRedirectViewWithAttributes(this.urlGenerator.getSettingsUrl());
		if (this.errors.hasErrors()) {
			redirect.addAttribute(ExtendedRedirectViewWithAttributes.ERRORS_KEY, this.errors);
		} else {
			redirect.addAttribute(ExtendedRedirectViewWithAttributes.SUCCESS_MESSAGE_KEY, "settings.user.profile.update.success");
		}
		return redirect;
	}

	/**
	 * updates the the profile settings of a user
	 * @param loginUser
	 * @param command
	 */
	private void updateUserProfile(final User loginUser, final User commandUser, final SettingsViewCommand command) {
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
		
		loginUser.setUseExternalPicture(commandUser.isUseExternalPicture());
		
		updateUserPicture( loginUser, command );

		final ProfilePrivlevel profilePrivlevel = commandUser.getSettings().getProfilePrivlevel();
		
		loginUser.getSettings().setProfilePrivlevel(profilePrivlevel);

		this.updateUser(loginUser, this.errors);
	}
	
	private void updateUserPicture ( final User loginUser, SettingsViewCommand command ) {
		final MultipartFile file = command.getPicturefile();
		
		/*
		 * If a picture file is given -> upload
		 * Else, if delete requested -> delete 
		 */
		if (present(file) && file.getSize() > 0) {
			loginUser.setProfilePicture( new ServerUploadedFile(file) );
		} else if (command.getDeletePicture()) {
			loginUser.setProfilePicture( new ServerDeletedFile() );
		}
	}

	/**
	 * Updates the user (including field length error checking!).
	 * 
	 * @param user
	 */
	private void updateUser(final User user, final Errors errors) {
		try {
			this.logic.updateUser(user, UserUpdateOperation.UPDATE_CORE);
		} catch (final DatabaseException e) {
			final List<ErrorMessage> messages = e.getErrorMessages().get(user.getName());
			for (final ErrorMessage eMsg : messages) {
				if (eMsg instanceof FieldLengthErrorMessage) {
					final FieldLengthErrorMessage fError = (FieldLengthErrorMessage) eMsg;
					final Iterator<String> it = fError.iteratorFields();
					while (it.hasNext()) {
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
	public boolean isValidationRequired(final SettingsViewCommand command) {
		return true;
	}

}