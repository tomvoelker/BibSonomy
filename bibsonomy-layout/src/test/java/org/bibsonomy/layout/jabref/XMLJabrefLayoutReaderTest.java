/**
 *  
 *  BibSonomy-Layout - Layout engine for the webapp.
 *   
 *  Copyright (C) 2006 - 2010 Knowledge & Data Engineering Group, 
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *  
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.layout.jabref;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
 * 
 */
public class XMLJabrefLayoutReaderTest {

	@Test
	public void testGetJabrefLayouts() throws IOException {
		
		final InputStream stream = XMLJabrefLayoutReader.class.getClassLoader().getResourceAsStream("org/bibsonomy/layout/jabref/JabrefLayouts.xml");
		
		final BufferedReader buf = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
		
		final XMLJabrefLayoutReader reader = new XMLJabrefLayoutReader(buf);
		
		Assert.assertTrue(reader.getJabrefLayoutsDefinitions().size() > 10);
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
		
		final BufferedReader buf = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
		
		final XMLJabrefLayoutReader reader = new XMLJabrefLayoutReader(buf);
		
		final List<JabrefLayout> jabrefLayoutsDefinitions = reader.getJabrefLayoutsDefinitions();
		
		Assert.assertTrue(jabrefLayoutsDefinitions.size() > 10);
		
		/*
		 * find SE layout
		 */
		boolean found = false;
		for(final JabrefLayout layout: jabrefLayoutsDefinitions) {
			if (layout.getName().equals("se")) {
				found = true;
				
				Assert.assertEquals("se", layout.getBaseFileName());
				Assert.assertEquals("text/html", layout.getMimeType());
				Assert.assertEquals(".html", layout.getExtension());
				Assert.assertEquals("SE Kassel", layout.getDisplayName());
				Assert.assertFalse(layout.isPublicLayout());
			}
		}
		
		Assert.assertTrue(found);
		
		
		
	}
}

