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
	public void testGetJabrefLayouts() throws SAXException, IOException {
		
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
				
				Assert.assertEquals("se", layout.getBaseFileName());;
				Assert.assertEquals("text/html", layout.getMimeType());
				Assert.assertEquals(".html", layout.getExtension());
				Assert.assertEquals("SE Kassel", layout.getDisplayName());
				Assert.assertFalse(layout.isPublicLayout());
			}
		}
		
		Assert.assertTrue(found);
		
		
		
	}
}

