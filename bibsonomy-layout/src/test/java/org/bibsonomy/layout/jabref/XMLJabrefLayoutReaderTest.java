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
package org.bibsonomy.layout.jabref;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.bibsonomy.util.StringUtils;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * 
 * @author:  rja
 */
public class XMLJabrefLayoutReaderTest {

	@Test
	public void testGetJabrefLayouts() throws IOException {
		final InputStream stream = XMLJabrefLayoutReader.class.getClassLoader().getResourceAsStream("org/bibsonomy/layout/jabref/JabrefLayouts.xml");
		
		final BufferedReader buf = new BufferedReader(new InputStreamReader(stream, StringUtils.CHARSET_UTF_8));
		
		final XMLJabrefLayoutReader reader = new XMLJabrefLayoutReader(buf);
		
		assertTrue(reader.getJabrefLayoutsDefinitions().size() > 10);
		buf.close();
	}

	/**
	 * Get the SE layout and check if it has been read correctly.
	 * 	<layout name="se" public="false">
	 *	  <displayName>SE Kassel</displayName>
	 *	  <description>
	 *	  	Custom output format provided by the Research Group Software
	 *		Engineering, University of Bochum.
	 *    </description>
	 *	  <baseFileName>se</baseFileName>
	 *	  <mimeType>text/html</mimeType>
	 *	  <extension>.html</extension>
	 *  </layout>
	 * 
	 * @throws SAXException
	 * @throws IOException
	 */
	@Test
	public void testGetJabrefLayoutsSE() throws SAXException, IOException {
		final InputStream stream = XMLJabrefLayoutReader.class.getClassLoader().getResourceAsStream("org/bibsonomy/layout/jabref/JabrefLayouts.xml");
		
		final BufferedReader buf = new BufferedReader(new InputStreamReader(stream, StringUtils.CHARSET_UTF_8));
		final XMLJabrefLayoutReader reader = new XMLJabrefLayoutReader(buf);
		final List<AbstractJabRefLayout> jabrefLayoutsDefinitions = reader.getJabrefLayoutsDefinitions();
		
		assertTrue(jabrefLayoutsDefinitions.size() > 10);
		
		/*
		 * find SE layout
		 */
		boolean found = false;
		for(final AbstractJabRefLayout layout: jabrefLayoutsDefinitions) {
			if (layout.getName().equals("se")) {
				found = true;
				JabrefLayout jabrefLayout = (JabrefLayout) layout;
				assertEquals("se", jabrefLayout.getBaseFileName());
				assertEquals("text/html", layout.getMimeType());
				assertEquals(".html", layout.getExtension());
				assertEquals("SE Kassel", layout.getDisplayName());
				assertFalse(layout.isPublicLayout());
			}
		}
		
		assertTrue(found);
		buf.close();
	}
}

