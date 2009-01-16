package org.bibsonomy.layout.jabref;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
}

