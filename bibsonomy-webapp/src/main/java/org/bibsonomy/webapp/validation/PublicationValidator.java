package org.bibsonomy.webapp.validation;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;

import org.bibsonomy.bibtex.parser.PostBibTeXParser;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.util.UrlUtils;
import org.bibsonomy.webapp.util.Validator;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;

import bibtex.parser.ParseException;

/**
 * @author fba
 * @author dzo
 * @version $Id$
 */
public class PublicationValidator implements Validator<BibTex> {
	
	@SuppressWarnings("rawtypes")
	@Override
	public boolean supports(final Class clazz) {
		return BibTex.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(final Object obj, final Errors errors) {
		Assert.notNull(obj);
		
		if (obj instanceof BibTex) {
			final BibTex bibtex = (BibTex) obj;
			
			/*
			 * clean url
			 * FIXME: a validator MUST NOT modify objects!
			 */
			bibtex.setUrl(UrlUtils.cleanUrl(bibtex.getUrl()));
			
			/*
			 * check url
			 */
//			final String url = resource.getUrl();
//			if (url == null || url.equals("http://") || url.startsWith(UrlUtils.BROKEN_URL)) {
//				errors.rejectValue("post.resource.url", "error.field.valid.url");
//			}

			/*
			 * entrytype
			 */
			if (!present(bibtex.getEntrytype()) || containsWhiteSpace(bibtex.getEntrytype())) {
				errors.rejectValue("entrytype", "error.field.valid.entrytype");
			}
			/*
			 * key
			 */
			if (!present(bibtex.getBibtexKey()) || containsWhiteSpace(bibtex.getBibtexKey())) {
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

			/*
			 * test validity using the BibTeXParser
			 */
			final String bibTexAsString = BibTexUtils.toBibtexString(bibtex);
			try {
				parser.parseBibTeX(bibTexAsString);
			} catch (ParseException ex) {
				/*
				 * parsing failed
				 */
				errors.reject("error.parse.bibtex.failed", new Object[]{bibTexAsString, ex.getMessage()}, "Error parsing your post:\n\n{0}\n\nMessage was: {1}");
			} catch (IOException ex) {
				/*
				 * parsing failed
				 */
				errors.reject("error.parse.bibtex.failed", new Object[]{bibTexAsString, ex.getMessage()}, "Error parsing your post:\n\n{0}\n\nMessage was: {1}");
			}
		}
	}
	
	private static boolean containsWhiteSpace(final String s) {
		return s.matches("\\s");
	}
}
