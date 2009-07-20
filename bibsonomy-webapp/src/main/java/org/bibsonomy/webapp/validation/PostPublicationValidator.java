package org.bibsonomy.webapp.validation;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.util.UrlUtils;
import static org.bibsonomy.util.ValidationUtils.present;
import org.bibsonomy.webapp.command.actions.EditPublicationCommand;
import org.springframework.validation.Errors;
/**
 * @author fba
 * @version $Id$
 */
public class PostPublicationValidator extends PostPostValidator<BibTex> {
	
	@Override
	@SuppressWarnings("unchecked")
	public boolean supports(final Class clazz) {
		return EditPublicationCommand.class.equals(clazz);
	}

	@Override
	protected void validateResource(final Errors errors, final BibTex bibtex) {
		/*
		 * clean url
		 */
		bibtex.setUrl(UrlUtils.cleanUrl(bibtex.getUrl()));
		
		/*
		 * check url
		 */
//		final String url = resource.getUrl();
//		if (url == null || url.equals("http://") || url.startsWith(UrlUtils.BROKEN_URL)) {
//			errors.rejectValue("post.resource.url", "error.field.valid.url");
//		}

		/*
		 * title
		 */
		if (!present(bibtex.getTitle())) {
			errors.rejectValue("post.resource.title", "error.field.valid.title");
		}
		/*
		 * entrytype
		 */
		if (!present(bibtex.getEntrytype()) || containsWhiteSpace(bibtex.getEntrytype())) {
			errors.rejectValue("post.resource.entrytype", "error.field.valid.entrytype");
		}
		/*
		 * key
		 */
		if (!present(bibtex.getBibtexKey()) || containsWhiteSpace(bibtex.getEntrytype())) {
			errors.rejectValue("post.resource.bibtexKey", "error.field.valid.bibtexKey");
		}
		/*
		 * year
		 */
		if (!present(bibtex.getYear())) {
			errors.rejectValue("post.resource.year", "error.field.valid.year");
		}
		/*
		 * author/editor
		 */
		if (!present(bibtex.getAuthor()) && ! present(bibtex.getEditor())) {
			errors.rejectValue("post.resource.author", "error.field.valid.authorOrEditor");
			errors.rejectValue("post.resource.editor", "error.field.valid.authorOrEditor");
		}
		
		/*
		 * TODO: test validity using the BibTeXParser
		 * - It might be a good idea to parse only, if there are no errors, yet (?)
		 * - It would be nice to extract from the parser error message, which field
		 *   is affected and add a corresponding error message to the field. I.e., 
		 *   use global errors only, if we really don't know, what is broken.
		 */
		
	}
	
	private static boolean containsWhiteSpace(final String s) {
		return s.matches("\\s");
	}

}
