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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.util.GroupUtils;
import org.bibsonomy.webapp.command.GroupingCommand;
import org.bibsonomy.webapp.util.GroupingCommandUtils;
import org.bibsonomy.webapp.util.Validator;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;

/**
 * @author dzo
 */
public class GroupingValidator implements Validator<GroupingCommand> {
	private static final Log log = LogFactory.getLog(GroupingValidator.class);
	
	@Override
	public boolean supports(final Class<?> clazz) {
		return clazz != null && GroupingCommand.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(final Object target, final Errors errors) {
		Assert.notNull(target);
		final GroupingCommand command = (GroupingCommand) target;
		final String abstractGrouping = command.getAbstractGrouping();
		final List<String> groups = command.getGroups();
		
		if (GroupUtils.PUBLIC_GROUP_NAME.equals(abstractGrouping) || GroupUtils.PRIVATE_GROUP_NAME.equals(abstractGrouping)) {
			if (present(groups)) {
				/*
				 * "public" or "private" selected, but other group(s) chosen
				 */
				errors.rejectValue("groups", "error.field.valid.groups");
			}
		} else if (GroupingCommandUtils.OTHER_ABSTRACT_GROUPING.equals(abstractGrouping)) {
			log.debug("grouping 'other' found ... checking given groups");
			if (!present(groups)) {
				log.debug("error: no groups given");
				/*
				 * "other" selected, but no group chosen
				 * TODO: more detailed error messages for different errors
				 */
				
				errors.rejectValue("groups", "error.field.valid.groups");
				
				
			} else if (groups.size() > 1) {
				/*
				 * TODO: allow multiple groups
				 */
				errors.rejectValue("groups", "error.field.valid.groups");
			}
		} else {
			log.debug("neither public, private, other chosen");
			/*
			 * neither public, private, other chosen
			 */
			errors.rejectValue("groups", "error.field.valid.groups");
		}
	}

}
