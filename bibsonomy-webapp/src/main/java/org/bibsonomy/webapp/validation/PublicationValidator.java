package org.bibsonomy.webapp.validation;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.util.List;

import org.bibsonomy.bibtex.parser.PostBibTeXParser;
import org.bibsonomy.bibtex.parser.SimpleBibTeXParser;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.util.UrlUtils;
import org.bibsonomy.webapp.util.Validator;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;

import bibtex.expansions.PersonListParserException;
import bibtex.parser.ParseException;

/**
 * @author fba
 * @author dzo
 * @version $Id$
 */
public class PublicationValidator implements Validator<BibTex> {

	private static final String PARSE_ERROR_MESSAGE_KEY = "error.parse.bibtex.failed";
	private static final String DEFAULT_PARSE_ERROR_MESSAGE = "Error parsing your post:\n\n{0}\n\nMessage was: {1}";

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
				errors.reject(PARSE_ERROR_MESSAGE_KEY, new Object[]{bibTexAsString, ex.getMessage()}, DEFAULT_PARSE_ERROR_MESSAGE);
			} catch (IOException ex) {
				/*
				 * parsing failed
				 */
				errors.reject(PARSE_ERROR_MESSAGE_KEY, new Object[]{bibTexAsString, ex.getMessage()}, DEFAULT_PARSE_ERROR_MESSAGE);
			}
			/*
			 * add parser warnings to errors
			 */
			handleParserWarnings(errors, parser, bibTexAsString, "author");

			/*
			 * We add the "simple" checks after replacing the publication with
			 * the parsed one to ensure we catch empty fields.
			 *  
			 * (reason: the BibTeX parser does not recognize certain broken 
			 * author names and then removes them, e.g., "Foo, Bar," resulting
			 * in no errors but an empty author field.)  
			 */

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
			 * (we don't add the error if there is already an error added - it 
			 * might be a more detailed error message from the parser from
			 * handleParserWarnings)
			 */
			if (!present(bibtex.getAuthor()) && !present(bibtex.getEditor()) && !errors.hasFieldErrors("author")) {
				errors.rejectValue("author", "error.field.valid.authorOrEditor");
				// one error is enough
				//errors.rejectValue("post.resource.editor", "error.field.valid.authorOrEditor");
			}

		}
	}

	/**
	 * Checks the parser's warnings for serious errors (e.g., wrong person names)
	 * and adds them to the errors list.
	 * 
	 * @param errors
	 * @param parser
	 * @param bibTexAsString
	 * @param authorPropertyFieldName - if given, person name parsing errors are 
	 * added to this field. If several posts are parsed, we currently can't assign
	 * the errors to the correct post. In this case, set this value to <code>null</code>.  
	 * to this field
	 */
	public static void handleParserWarnings(final Errors errors, final SimpleBibTeXParser parser, final String bibTexAsString, final String authorPropertyFieldName) {
		final List<String> warnings = parser.getWarnings();
		if (present(warnings)) {
			errors.reject(PARSE_ERROR_MESSAGE_KEY, new Object[]{bibTexAsString, warnings.toString()}, DEFAULT_PARSE_ERROR_MESSAGE);
			if (present(authorPropertyFieldName)) {
				for (final String warning : warnings) {
					/*
					 * special handling for name errors that look like 
					 * "bibtex.expansions.PersonListParserException: Name ends with comma: 'Foo, Bar,' - in 'foo'"
					 */
					if (warning.startsWith(PersonListParserException.class.getName())) {
						/*
						 * FIXME: we don't know whether to reject author or editor.
						 * So we pick the author = best guess. Not a good idea but
						 * my quick solution for today. :-( 
						 */
						errors.rejectValue(authorPropertyFieldName, "error.field.valid.authorOrEditor.parseError", new Object[]{warning}, "The author or editor field caused the following parse error: {0}");
					}
				}
			}
		}
	}

	private static boolean containsWhiteSpace(final String s) {
		return s.matches("\\s");
	}
}
