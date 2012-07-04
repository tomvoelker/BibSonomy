package org.bibsonomy.scraper.converter;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.testutil.TestUtils;
import org.junit.Test;

/**
 * @author rja
 * @version $Id$
 */
public class OAIConverterTest {

	private static final String PATH_TO_FILES = "org/bibsonomy/scraper/converter/";
	
	@Test
	public void testConvert1() throws Exception {
		this.testFile("arxiv1");
	}

	@Test
	public void testConvert2() throws Exception {
		this.testFile("arxiv2");
	}
	

	private void testFile(final String fileName) throws IOException, ScrapingException {
		final String xml = TestUtils.readEntryFromFile(OAIConverterTest.PATH_TO_FILES + fileName + ".xml");
		final String bib = TestUtils.readEntryFromFile(OAIConverterTest.PATH_TO_FILES + fileName + ".bib");
		
		assertEquals(bib.trim(), OAIConverter.convert(xml).trim());
	}
}
