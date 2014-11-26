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

import org.bibsonomy.model.DiscussionItem;
import org.bibsonomy.webapp.command.ajax.DiscussionItemAjaxCommand;
import org.bibsonomy.webapp.util.Validator;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

/**
 * @author dzo
 * @param <D> 
 */
public abstract class DiscussionItemValidator<D extends DiscussionItem> implements Validator<DiscussionItemAjaxCommand<D>> {

	protected static final String DISCUSSION_ITEM_PATH = "discussionItem.";
	
	@Override
	public boolean supports(final Class<?> clazz) {
		return DiscussionItemAjaxCommand.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(final Object target, final Errors errors) {
		@SuppressWarnings("unchecked")
		final DiscussionItemAjaxCommand<D> command = (DiscussionItemAjaxCommand<D>) target;
		
		/*
		 * a hash must be provided
		 */
		if (!present(command.getHash())) {
			errors.rejectValue("hash", "error.field.valid.hash");
		}
		
		/*
		 * validate item
		 */
		this.validateDiscussionItem(command.getDiscussionItem(), errors);
		
		/*
		 * validate groups
		 */
		ValidationUtils.invokeValidator(new GroupingValidator(), command, errors);
	}

	protected abstract void validateDiscussionItem(D discussionItem, Errors errors);

}
