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
