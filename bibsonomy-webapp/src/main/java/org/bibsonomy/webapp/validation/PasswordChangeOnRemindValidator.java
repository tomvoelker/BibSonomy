/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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

import org.bibsonomy.webapp.command.actions.PasswordChangeOnRemindCommand;
import org.bibsonomy.webapp.util.Validator;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

/**
 * @author daill
 */
public class PasswordChangeOnRemindValidator implements Validator<PasswordChangeOnRemindCommand>{

	@Override
	public boolean supports(final Class<?> clazz) {
		return PasswordChangeOnRemindCommand.class.equals(clazz);
	}
	
	@Override
	public void validate(Object arg0, Errors errors) {
		// if command is null fail
		Assert.notNull(arg0);
		
		// get the command
		final PasswordChangeOnRemindCommand command = (PasswordChangeOnRemindCommand)arg0;
		
		// if one of the password fields is empty or contains only whitespaces fail
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "newPassword", "error.field.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "passwordCheck", "error.field.required");

		// if there is no field error on newPassword or passwordCheck but they don't equal fail
		if(!errors.hasFieldErrors("newPassword") && !errors.hasFieldErrors("passwordCheck") && !command.getNewPassword().equals(command.getPasswordCheck())){
			errors.reject("error.field.valid.passwordCheck", "passwords don't match");
		}		
	}

}
