/**
 * BibSonomy-Rest-Server - The REST-server.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
import java.util.LinkedList;
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
		final List<ErrorMessage> errors = new LinkedList<>();
		
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
			/* we have modified the publication
			 * we should recalculate the hashes
			 */
			publication.recalculateHashes();
		} catch (final IOException | ParseException ex) {
			log.error("error parsing publication " + publication.getIntraHash(), ex);
			errors.add(new InvalidSourceErrorMessage());
		}
		
		errors.addAll(super.validateResource(publication));
		
		return errors;
	}
}
