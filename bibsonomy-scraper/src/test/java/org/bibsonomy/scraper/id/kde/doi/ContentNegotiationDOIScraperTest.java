package org.bibsonomy.scraper.id.kde.doi;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.bibsonomy.scraper.UnitTestRunner;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author lha
 * @version $Id$
 */
public class ContentNegotiationDOIScraperTest {
	
	/**
	 * tests the function of the ContentNegotiationDOIScraper for DOI URLs
	 * @throws ScrapingException
	 * @throws IOException
	 */
	@Ignore
	@Test
	public void testCNDOIScraper1() throws ScrapingException, IOException {
	
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_257"));
		
	}

	/**
	 * tests the function of the ContentNegotiationDOIScraper for selected DOI texts
	 * @throws ScrapingException
	 * @throws IOException
	 */
	@Ignore
	@Test
	public void testCNDOIScraper2() throws ScrapingException, IOException {

		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_258"));
		
	}
}
