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

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.webapp.command.actions.UserLoginCommand;
import org.bibsonomy.webapp.util.Validator;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

/**
 * @author rja
 */
public class UserLoginValidator implements Validator<UserLoginCommand> {

	@Override
	public boolean supports(final Class<?> clazz) {
		return UserLoginCommand.class.equals(clazz);
	}

	/**
	 * Validates the given loginObj.
	 * 
	 * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
	 */
	@Override
	public void validate(final Object loginObj, final Errors errors) {
		/*
		 * To ensure that the received command is not null, we throw an
		 * exception, if this assertion fails.
		 */
		Assert.notNull(loginObj);
		
		final UserLoginCommand cmd = (UserLoginCommand) loginObj;

		if (!present(cmd.getOpenID()) && !present(cmd.getUsername()) && !present(cmd.getPassword())) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "error.field.required");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "error.field.required");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "openID", "error.field.required");
		}		
	}
}