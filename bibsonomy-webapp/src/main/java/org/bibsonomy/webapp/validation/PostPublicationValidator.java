package org.bibsonomy.webapp.validation;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.bibtex.parser.SimpleBibTeXParser;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.util.UrlUtils;
import org.bibsonomy.webapp.command.actions.EditPublicationCommand;
import org.springframework.validation.Errors;

import bibtex.parser.ParseException;

/**
 * @author fba
 * @version $Id$
 */
public class PostPublicationValidator extends PostPostValidator<BibTex> {
	
	private static final Log logger = LogFactory.getLog(PostPublicationValidator.class);
	
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
		 * test validity using the BibTeXParser
		 */		
		if (!this.parse(bibtex)) {
			// parsing failed
			errors.reject("error.parse.bibtex.failed");
		}
	}
	
	/**
	 * parses the BibTex object to validate it and returns true if no exception
	 * is thrown otherwise false
	 * 
	 * @param	bibtex	the bibtex object to parse
	 * @return	if parsing was successful
	 */
	private boolean parse(final BibTex bibtex) {
		if (bibtex == null) {
			return true;
		}
		
		// get string and init parser
		final String bibTexAsString = BibTexUtils.toBibtexString(bibtex);
		final SimpleBibTeXParser parser = new SimpleBibTeXParser();
		
		try {
			parser.parseBibTeX(bibTexAsString);
		} catch (ParseException ex) {
			// parsing failed
			return false;
		} catch (IOException ex) {
			logger.warn("exception while parsing bibtex string", ex);
			return false;
		}
		
		// parsing successful		
		return true;
	}
	
	
	private static boolean containsWhiteSpace(final String s) {
		return s.matches("\\s");
	}

}
