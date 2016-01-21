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

import org.bibsonomy.webapp.command.actions.LimitedAccountActivationCommand;
import org.bibsonomy.webapp.util.Validator;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;

/**
 * @author nilsraabe
 */
public class LimitedAccountActivationValidation implements Validator<LimitedAccountActivationCommand>{

	@Override
	public boolean supports(final Class<?> clazz) {
		return LimitedAccountActivationCommand.class.equals(clazz);
	}

	@Override
	public void validate(Object userObj, Errors errors) {
		
		/**
		 * To ensure that the received command is not null, we throw an
		 * exception, if this assertion fails.
		 */
		Assert.notNull(userObj);
		final LimitedAccountActivationCommand command = (LimitedAccountActivationCommand) userObj;
		
		/**
		 * check if the user accepts our privacy statement about SAML
		 */
		if (!command.isCheckboxAccept()) {
			errors.rejectValue("checkboxAccept", "limited_account.activation.error.checkbox");
		}
		
		errors.pushNestedPath("registerUser");
		UserValidator.validateUser(command.getRegisterUser(), errors);
		errors.popNestedPath();
	}

}
