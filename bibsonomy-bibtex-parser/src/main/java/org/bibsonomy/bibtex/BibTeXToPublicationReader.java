/**
 * BibSonomy-BibTeX-Parser - BibTeX Parser from http://www-plan.cs.colorado.edu/henkel/stuff/javabib/
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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
package org.bibsonomy.bibtex;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.bibtex.parser.SimpleBibTeXParser;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.ImportResource;
import org.bibsonomy.model.util.BibTexReader;
import org.bibsonomy.model.util.data.Data;

import bibtex.parser.ParseException;

/**
 * reader to parse publications from BibTeX
 *
 * @author dzo
 */
public class BibTeXToPublicationReader implements BibTexReader {
	private static final Log log = LogFactory.getLog(BibTeXToPublicationReader.class);
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.model.util.BibTexReader#read(org.bibsonomy.model.ImportResource)
	 */
	@Override
	public Collection<BibTex> read(final ImportResource importRes) {
		final Data data = importRes.getData();
		final SimpleBibTeXParser parser = new SimpleBibTeXParser();
		
		final List<BibTex> list = new LinkedList<>();
		
		try {
			final BibTex parseBibTeX = parser.parseBibTeX(data.getReader());
			list.add(parseBibTeX);
			
			return list;
		} catch (final ParseException | IOException e) {
			log.error("error while reading BibTeX.", e);
			throw new RuntimeException(e); // TODO: maybe we want to create and throw a specific exception
		}
	}

}
