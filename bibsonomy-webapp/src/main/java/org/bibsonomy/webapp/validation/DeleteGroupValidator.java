/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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

import org.bibsonomy.webapp.command.GroupSettingsPageCommand;
import org.bibsonomy.webapp.util.Validator;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

/**
 * @author Mario Holtmüller
 */
public class DeleteGroupValidator implements Validator<GroupSettingsPageCommand>{
	private static final String DELETE_FIELD = "delete";

	@Override
	public boolean supports(final Class<?> clazz) {
		return GroupSettingsPageCommand.class.equals(clazz);
	}

	@Override
	public void validate(final Object commandObject, final Errors errors) {
		// if command is null fail
		Assert.notNull(commandObject);
		
		final GroupSettingsPageCommand command = (GroupSettingsPageCommand) commandObject;
		
		// if the delete security string is empty throw an error
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, DELETE_FIELD, "error.field.required");
		if (!errors.hasFieldErrors(DELETE_FIELD) && !"yes".equalsIgnoreCase(command.getDelete())) {
			errors.reject("error.group.secure.answer");
		}
	}

}
