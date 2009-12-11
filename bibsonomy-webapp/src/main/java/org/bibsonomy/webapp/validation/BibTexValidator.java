package org.bibsonomy.webapp.validation;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;

import org.bibsonomy.bibtex.parser.PostBibTeXParser;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.util.UrlUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import bibtex.parser.ParseException;

/**
 * @author ema
 * @version $Id$
 */
public class BibTexValidator implements Validator {

	@Override
	public boolean supports(Class clazz) {
		return BibTex.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		//errors.pushNestedPath("resource");
		
		BibTex bibtex = (BibTex) target;
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
		 * title (ignored, if there are already errors - title is already 
		 * checked in super class)
		 */
		if (!present(bibtex.getTitle())) {
			errors.rejectValue("title", "error.field.valid.title");
		}
		/*
		 * entrytype
		 */
		if (!present(bibtex.getEntrytype()) || StringUtils.containsWhitespace(bibtex.getEntrytype())) {
			errors.rejectValue("entrytype", "error.field.valid.entrytype");
		}
		/*
		 * key
		 */
		if (!present(bibtex.getBibtexKey()) || StringUtils.containsWhitespace(bibtex.getBibtexKey())) {
			errors.rejectValue("bibtexKey", "error.field.valid.bibtexKey");
		}
		/*
		 * year
		 */
		if (!present(bibtex.getYear())) {
			errors.rejectValue("year", "error.field.valid.year");
		}
		/*
		 * author/editor
		 */
		if (!present(bibtex.getAuthor()) && !present(bibtex.getEditor())) {
			errors.rejectValue("author", "error.field.valid.authorOrEditor");
			// one error is enough
			//errors.rejectValue("post.resource.editor", "error.field.valid.authorOrEditor");
		}
		
		
		/*
		 * initialize parser
		 * 
		 * XXX: if the parser is thread safe, we can use a single instance for
		 * several calls.
		 */
		final PostBibTeXParser parser = new PostBibTeXParser();

		/*
		 * test misc field using the BibTeXParser
		 */
		final String misc = bibtex.getMisc();
		if (present(misc)) {
			/*
			 * parse a bibtex string only with attributes of the misc field 
			 */
			try {
				parser.parseBibTeX("@misc{id,\n" + misc + "\n}");
			} catch (ParseException ex) {
				errors.rejectValue("misc", "error.field.valid.misc");
				/*
				 * stop parsing remaining entry - would fail anyway.
				 */
				return;
			} catch (IOException ex) {
				errors.rejectValue("misc", "error.field.valid.misc");
				/*
				 * stop parsing remaining entry - would fail anyway.
				 */
				return;
			}
		}
		//errors.popNestedPath();
	}

}
