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
package org.bibsonomy.webapp.validation.ajax;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.webapp.command.ajax.ClipboardManagerCommand;
import org.bibsonomy.webapp.command.ajax.action.ClipboardAction;
import org.bibsonomy.webapp.util.Validator;
import org.springframework.validation.Errors;

/**
 * validator for ajax clipboard requests
 *
 * @author vhem, dzo
 */
public class ClipboardValidator implements Validator<ClipboardManagerCommand>{

	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(Class<?> clazz) {
		return ClipboardManagerCommand.class.equals(clazz);
	}

	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Object target, Errors errors) {
		final ClipboardManagerCommand command = (ClipboardManagerCommand) target;
		final ClipboardAction action = command.getAction();
		if (!present(action)) {
			errors.reject("error.action.valid");
		}
		
		// only validate user and hash iff the action is not clear all
		if (!ClipboardAction.CLEARALL.equals(action)) {
			final String user = command.getUser();
			if (!present(user)) {
				errors.rejectValue("user", "error.user.valid");
			}
			
			final String hash = command.getHash();
			if (!present(hash)) {
				errors.rejectValue("hash", "error.hash.valid");
			}
		}
	}

}
