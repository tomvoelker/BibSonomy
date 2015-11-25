/**
 * BibSonomy-Layout - Layout engine for the webapp.
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
package org.bibsonomy.layout.standard;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Reads a jabref layout definition XML file and it returns a list of {@link JabrefLayout}s.
 * 
 * @author: lsc
 * @version: $Id$ $Author$
 * 
 */
public class XMLLayoutReader {

	private Reader reader;

	public XMLLayoutReader(final Reader reader) {
		this.reader = reader;
	}

	/**
	 * Reads a list of {@link JabrefLayout}s.
	 * 
	 * @return
	 * @throws IOException
	 */
	public List<StandardLayout> getLayoutsDefinitions() throws IOException {
		try {
			final XMLReader xr = XMLReaderFactory.createXMLReader();
			/*
			 * SAX callback handler
			 */
			final LayoutXMLHandler handler = new LayoutXMLHandler();
			xr.setContentHandler(handler);
			xr.setErrorHandler(handler);
			xr.parse(new InputSource(reader));

			return handler.getLayouts();
		} catch (SAXException e) {
			throw new IOException(e);
		}
	}
}
