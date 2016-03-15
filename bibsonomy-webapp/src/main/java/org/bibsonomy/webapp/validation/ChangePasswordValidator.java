/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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

import org.bibsonomy.webapp.command.SettingsViewCommand;
import org.bibsonomy.webapp.util.Validator;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

/**
 * @author cvo
 */
public class ChangePasswordValidator implements Validator<SettingsViewCommand> {

	@Override
	public boolean supports(final Class<?> clazz) {
		return SettingsViewCommand.class.equals(clazz);
	}

	@Override
	public void validate(final Object target, final Errors errors) {
		final SettingsViewCommand command = (SettingsViewCommand) target;

		Assert.notNull(command);

		// if one of the password fields is empty or contains only whitespace
		// fail
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "oldPassword", "error.field.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "newPassword", "error.field.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "newPasswordRetype", "error.field.required");

		// if there is no field error on newPassword or passwordCheck but they
		// don't equal fail
		if (!errors.hasFieldErrors("newPassword") && !errors.hasFieldErrors("newPasswordRetype")) {
			if (!command.getNewPassword().equals(command.getNewPasswordRetype())) {
				errors.rejectValue("newPasswordRetype", "error.settings.password.match");
			}
		}
	}
}
