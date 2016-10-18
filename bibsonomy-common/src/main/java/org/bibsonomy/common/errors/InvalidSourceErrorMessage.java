package org.bibsonomy.common.errors;

/**
 * {@link ErrorMessage} for publications with invalid BibTeX
 *
 * @author dzo
 */
public class InvalidSourceErrorMessage extends ErrorMessage {

	/**
	 * default constructor
	 */
	public InvalidSourceErrorMessage() {
		super("Invalid BibTeX for this post.", "database.exception.invalid.bibtex");
	}

}
