package org.bibsonomy.rest.validation;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.bibtex.parser.SimpleBibTeXParser;
import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.util.BibTexUtils;

import bibtex.parser.ParseException;

/**
 * Validates the given model.
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 */
public class ServersideModelValidator extends StandardModelValidator {
	private static final Log log = LogFactory.getLog(ServersideModelValidator.class);

	private static final String BIBTEX_IS_INVALID_MSG = "The validation of the BibTeX entry failed: ";
	
	/**
	 * Parses the given publication using the BibTeX parser.
	 * 
	 * Additionally, exchanges author and editor names with normalized versions.
	 * 
	 * @see org.bibsonomy.rest.validation.ModelValidator#checkPublication(org.bibsonomy.model.BibTex)
	 * 
	 * FIXME: oh shit, see what this method does:
	 * 
  ServerSideModelValidator.checkPublication(final BibTex publication)

  pn = PersonNameUtils.extractList(b.getAuthor())

  s = PersonNameUtils.serializePersonNames(pn)


  b = SimpleBibTeXParser().parseBibTeX(s);


  pn = PersonNameUtils.extractList(b.getAuthor())

  PersonNameUtils.serializePersonNames(pn)

	 * 
	 */
	@Override
	public void checkPublication(final BibTex publication) {
		super.checkPublication(publication);
		/*
		 * parse BibTeX so see whether the entry is valid
		 */
		final BibTex parsedBibTeX;
		try {
			parsedBibTeX = new SimpleBibTeXParser().parseBibTeX(BibTexUtils.toBibtexString(publication));
		} catch (ParseException ex) {
			log.error(ex.getMessage());
			throw new ValidationException(BIBTEX_IS_INVALID_MSG + "Error while parsing BibTeX.");
		} catch (final IOException ex) {
			log.error(ex.getMessage());
			throw new ValidationException(BIBTEX_IS_INVALID_MSG + "I/O Error while parsing BibTeX.");
		}
		/*
		 * FIXME: validator is modifying the publication
		 */
		publication.setAuthor(parsedBibTeX.getAuthor());
		publication.setEditor(parsedBibTeX.getEditor());
	}

}