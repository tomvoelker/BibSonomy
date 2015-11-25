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
package org.bibsonomy.layout.jabref.self;

import java.io.StringWriter;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.sf.jabref.BibtexDatabase;
import net.sf.jabref.BibtexEntry;
import net.sf.jabref.msbib.MSBibDatabase;

import org.bibsonomy.common.exceptions.LayoutRenderingException;
import org.bibsonomy.layout.jabref.AbstractJabRefLayout;
import org.w3c.dom.Document;

/**
 * a self rendering layout for MSOffice XML
 *
 * @author MarcelM
 */
public class MSOfficeXMLLayout extends AbstractJabRefLayout {
	
	/**
	 * @param name
	 */
	public MSOfficeXMLLayout(final String name) {
		super(name);
	}

	@Override
	public StringBuffer render(final BibtexDatabase database, final List<BibtexEntry> sorted, final boolean embeddedLayout) throws LayoutRenderingException {
		try {
			final MSBibDatabase msbibDB = new MSBibDatabase(database);
			final Document doc = msbibDB.getDOMrepresentation();
			
			final StringBuffer output = new StringBuffer();
			return output.append(getStringFromDocument(doc));
		} catch (final TransformerException e) {
			throw new LayoutRenderingException(this.getName());
		}
	}
	
	/**
	 * This method converts a org.w3c.dom.Document to String
	 * @param doc
	 * @return
	 * @throws TransformerException
	 */
	private static String getStringFromDocument(Document doc) throws TransformerException {
		final DOMSource domSource = new DOMSource(doc);
		final StringWriter writer = new StringWriter();
		final StreamResult result = new StreamResult(writer);
		final TransformerFactory tf = TransformerFactory.newInstance();
		final Transformer transformer = tf.newTransformer();
		transformer.transform(domSource, result);
		return writer.toString();
	}
}
