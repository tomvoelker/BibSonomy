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

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.bibsonomy.common.enums.ClassifierSettings;
import org.bibsonomy.webapp.command.ajax.AdminAjaxCommand;
import org.bibsonomy.webapp.util.Validator;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;

/**
 * Validates fields related to admin tasks
 * @author bkr
 */
public class AdminActionsValidator implements Validator<AdminAjaxCommand> {
	
	@Override
	public boolean supports(final Class<?> clazz) {
		return AdminAjaxCommand.class.equals(clazz);
	}

	@Override
	public void validate(final Object settingsObj, final Errors errors) {
		/*
		 * To ensure that the received command is not null, we throw an
		 * exception, if this assertion fails.
		 */
		Assert.notNull(settingsObj);
		final AdminAjaxCommand command = (AdminAjaxCommand) settingsObj;
		
		/*
		 * Validate regular expression
		 */
		if (command.getKey() != null) {
			if (ClassifierSettings.WHITELIST_EXP.equals(ClassifierSettings
					.getClassifierSettings(command.getKey()))) {
				if (!errors.hasFieldErrors("value"))
					validateRegExForWhitelist(command.getValue(), errors);
			}
		}
	}

	/**
	 * Validates the correctness of the regular expression for the whitelist The
	 * regular expression is compiled - if an exception is thrown, the pattern
	 * is not valid
	 * 
	 * @param regular
	 *            expression
	 * @param errors
	 */
	private void validateRegExForWhitelist(final String regex, final Errors errors) {
		if (!present(regex)) {
			errors.rejectValue("value", "error.field.valid.admin.whitelist");
		} else {

			try {
				Pattern.compile(regex);
			} catch (final PatternSyntaxException e) {
				errors.rejectValue("value", "error.field.valid.admin.whitelist");
			}
		}

	}

}
