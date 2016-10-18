package org.bibsonomy.model.validation;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.common.errors.ErrorMessage;
import org.bibsonomy.common.errors.MissingFieldErrorMessage;
import org.bibsonomy.model.BibTex;

/**
 * validator for {@link BibTex}s
 *
 * @author dzo
 */
public class PublicationValidator implements ResourceValidator<BibTex> {

	/* (non-Javadoc)
	 * @see org.bibsonomy.model.validation.ResourceValidator#validateResource(org.bibsonomy.model.Resource)
	 */
	@Override
	public List<ErrorMessage> validateResource(final BibTex publication) {
		final List<ErrorMessage> errors = new LinkedList<>();
		if (!present(publication.getTitle())) {
			final ErrorMessage errorMessage = new MissingFieldErrorMessage("title");
			errors.add(errorMessage);
		}
		if (!present(publication.getYear())) {
			final ErrorMessage errorMessage = new MissingFieldErrorMessage("year");
			errors.add(errorMessage);
		}
		if (!present(publication.getEntrytype())) {
			final ErrorMessage errorMessage = new MissingFieldErrorMessage("entrytype");
			errors.add(errorMessage);
		}
		if (!present(publication.getBibtexKey())) {
			final ErrorMessage errorMessage = new MissingFieldErrorMessage("bibtexKey");
			errors.add(errorMessage);
		}
		if (!present(publication.getAuthor()) && !present(publication.getEditor())) {
			final ErrorMessage errorMessage = new MissingFieldErrorMessage("author/editor");
			errors.add(errorMessage);
		}
		return errors;
	}

}
