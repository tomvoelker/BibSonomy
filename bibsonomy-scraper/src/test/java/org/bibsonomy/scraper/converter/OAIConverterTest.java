package org.bibsonomy.scraper.converter;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.junit.Test;

/**
 * @author rja
 * @version $Id$
 */
public class OAIConverterTest {

	private static final String PATH_TO_FILES = "org/bibsonomy/scraper/converter/";
	
	@Test
	public void testConvert1() throws Exception {
		testFile("arxiv1");
	}

	@Test
	public void testConvert2() throws Exception {
		testFile("arxiv2");
	}
	

	private void testFile(final String fileName) throws IOException, ScrapingException {
		final String xml = this.readEntryFromFile(fileName + ".xml");
		final String bib = this.readEntryFromFile(fileName + ".bib");
		
		assertEquals(bib.trim(), OAIConverter.convert(xml).trim());
	}
	
	private String readEntryFromFile(final String fileName) throws IOException {
		final StringBuffer resultString = new StringBuffer();
		final BufferedReader in = new BufferedReader(new InputStreamReader(OAIConverterTest.class.getClassLoader().getResourceAsStream(PATH_TO_FILES + fileName), "UTF-8"));
		String line = null;
		while ((line = in.readLine()) != null) {
			resultString.append(line + "\n");
		}
		return resultString.toString();
	}
	
	
}
