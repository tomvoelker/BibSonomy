package org.bibsonomy.rest.validation;

import java.io.IOException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.bibtex.parser.SimpleBibTeXParser;
import org.bibsonomy.common.errors.ErrorMessage;
import org.bibsonomy.common.errors.InvalidSourceErrorMessage;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.model.validation.PublicationValidator;

import bibtex.parser.ParseException;

/**
 * XXX: Serverside validator to reduce GPL code (bibtex parser module)
 *
 * @author dzo
 */
public class ServersidePublicationValidator extends PublicationValidator {
	private static final Log log = LogFactory.getLog(ServersidePublicationValidator.class);
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.model.validation.PublicationValidator#validateResource(org.bibsonomy.model.BibTex)
	 */
	@Override
	public List<ErrorMessage> validateResource(final BibTex publication) {
		final List<ErrorMessage> errors = super.validateResource(publication);
		
		/*
		 * parse BibTeX so see whether the entry is valid
		 */
		try {
			/*
			 * FIXME: oh shit, see what this method does:
			 * ServerSideModelValidator.checkPublication(final BibTex publication)
			 * pn = PersonNameUtils.extractList(b.getAuthor())
			 * s = PersonNameUtils.serializePersonNames(pn)
			 * b = SimpleBibTeXParser().parseBibTeX(s);
			 * pn = PersonNameUtils.extractList(b.getAuthor())
			 * PersonNameUtils.serializePersonNames(pn)
			 */
			final BibTex parsedBibTeX = new SimpleBibTeXParser().parseBibTeX(BibTexUtils.toBibtexString(publication));
			
			/*
			 * FIXME: validator is modifying the publication
			 */
			publication.setAuthor(parsedBibTeX.getAuthor());
			publication.setEditor(parsedBibTeX.getEditor());
		} catch (final IOException | ParseException ex) {
			log.error("error parsing publication " + publication.getIntraHash(), ex);
			errors.add(new InvalidSourceErrorMessage());
		}
		
		return errors;
	}
}
