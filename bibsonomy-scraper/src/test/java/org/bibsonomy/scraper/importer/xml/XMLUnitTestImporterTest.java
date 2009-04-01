package org.bibsonomy.scraper.importer.xml;

import static org.junit.Assert.*;

import java.util.List;

import org.bibsonomy.scraper.ScraperUnitTest;
import org.bibsonomy.scraper.URLTest.URLScraperUnitTest;
import org.junit.Test;

/**
 * @author rja
 * @version $Id$
 */
public class XMLUnitTestImporterTest {

	@Test
	public void testGetUnitTests() {
		final XMLUnitTestImporter importer = new XMLUnitTestImporter();
		
		
		try {
			final List<ScraperUnitTest> unitTests = importer.getUnitTests();

			/*
			 * well, we have more than 60 scrapers, so we should have more than 100 tests ...
			 */
			assertTrue(unitTests.size() > 100);

			/*
			 * check each test
			 */
			for (final ScraperUnitTest scraperUnitTest : unitTests) {
				if (scraperUnitTest instanceof URLScraperUnitTest) {
					assertNotNull(((URLScraperUnitTest) scraperUnitTest).getExpectedReference());
				}
				
			}
			
			
			
		} catch (Exception ex) {
			ex.printStackTrace();
			fail(ex.getMessage());
		}
		
		
	}

}
