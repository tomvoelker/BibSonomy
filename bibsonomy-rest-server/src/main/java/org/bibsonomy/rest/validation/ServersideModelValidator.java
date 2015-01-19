/**
 * BibSonomy-Rest-Server - The REST-server.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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