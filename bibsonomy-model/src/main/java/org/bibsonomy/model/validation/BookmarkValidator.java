/**
 * BibSonomy-Model - Java- and JAXB-Model.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.model.validation;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.common.errors.ErrorMessage;
import org.bibsonomy.common.errors.MissingFieldErrorMessage;
import org.bibsonomy.model.Bookmark;

/**
 * validator for {@link Bookmark}s
 *
 * @author dzo
 */
public class BookmarkValidator implements ResourceValidator<Bookmark> {

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.validation.ResourceValidator#validateResource(org.bibsonomy.model.Resource)
	 */
	@Override
	public List<ErrorMessage> validateResource(Bookmark bookmark) {
		final List<ErrorMessage> errors = new LinkedList<>();
		
		if (!present(bookmark.getUrl())) {
			final ErrorMessage errorMessage = new MissingFieldErrorMessage("url");
			errors.add(errorMessage);
		}
		
		if (!present(bookmark.getInterHash()) || !present(bookmark.getIntraHash())) {
			final ErrorMessage errorMessage = new MissingFieldErrorMessage("intraHash");
			errors.add(errorMessage);
		}
		
		return errors;
	}

}
