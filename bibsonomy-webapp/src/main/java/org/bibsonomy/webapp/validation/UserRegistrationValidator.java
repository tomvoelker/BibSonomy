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
package org.bibsonomy.webapp.validation;

import org.bibsonomy.model.User;
import org.bibsonomy.webapp.command.actions.UserRegistrationCommand;
import org.bibsonomy.webapp.util.Validator;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

/**
 * @author rja
 */
public class UserRegistrationValidator implements Validator<UserRegistrationCommand> {

	private final String projectName;

	/**
	 * @param projectName
	 */
	public UserRegistrationValidator(final String projectName) {
		super();
		this.projectName = projectName;
	}

	@Override
	public boolean supports(final Class<?> clazz) {
		return UserRegistrationCommand.class.equals(clazz);
	}

	/**
	 * Validates the given userObj.
	 * 
	 * @see org.springframework.validation.Validator#validate(java.lang.Object,
	 *      org.springframework.validation.Errors)
	 */
	@Override
	public void validate(final Object userObj, final Errors errors) {
		/*
		 * To ensure that the received command is not null, we throw an
		 * exception, if this assertion fails.
		 */
		Assert.notNull(userObj);
		final UserRegistrationCommand command = (UserRegistrationCommand) userObj;

		/*
		 * Check the user data.
		 */
		final User user = command.getRegisterUser();
		Assert.notNull(user);

		/*
		 * validate user
		 */
		errors.pushNestedPath("registerUser");
		ValidationUtils.invokeValidator(new UserValidator(), user, errors);
		errors.popNestedPath();

		/*
		 * Check the validity of the supplied passwords. Both passwords must be
		 * non-empty and must match each other.
		 */
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "registerUser.password", "error.field.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "passwordCheck", "error.field.required");
		if (!errors.hasFieldErrors("registerUser.password") && !errors.hasFieldErrors("passwordCheck") && !command.getPasswordCheck().equals(user.getPassword())) {
			/*
			 * passwords are not empty and don't match
			 */
			errors.rejectValue("passwordCheck", "error.field.valid.passwordCheck", "passwords don't match");
		}

		/*
		 * check that the user accepts our privacy statement
		 */
		if (!command.isAcceptPrivacy()) {
			errors.rejectValue("acceptPrivacy", "error.field.valid.acceptPrivacy", new Object[] { this.projectName }, null);
		}
	}

}
