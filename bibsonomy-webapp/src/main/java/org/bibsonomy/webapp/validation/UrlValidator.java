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

import org.bibsonomy.util.UrlUtils;
import org.bibsonomy.webapp.command.ajax.AjaxURLCommand;
import org.bibsonomy.webapp.util.Validator;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

/**
 * 
 * FIXME: duplicate of {@link BookmarkValidator}.
 * 
 * @author rja
 */
public class UrlValidator implements Validator<AjaxURLCommand> {
	
	@Override
	public boolean supports(final Class<?> clazz) {
		return AjaxURLCommand.class.equals(clazz);
	}
	
	@Override
	public void validate(final Object obj, final Errors errors) {
		Assert.notNull(obj);
		
		if (obj instanceof AjaxURLCommand) {
			final AjaxURLCommand command = (AjaxURLCommand) obj;
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "url", "error.field.required");

			
			/*
			 * clean url
			 * 
			 * FIXME: a validator MUST NOT modify objects
			 */
			command.setUrl(UrlUtils.cleanUrl(command.getUrl()));
			
			/*
			 * check url
			 */
			final String url = command.getUrl();
			if (url == null || url.equals("http://") || url.startsWith(UrlUtils.BROKEN_URL)) {
				errors.rejectValue("url", "error.field.valid.url");
			}
		}
	}

}
