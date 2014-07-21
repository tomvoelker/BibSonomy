package org.bibsonomy.scraper.url.kde.mdpi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URL;

import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Test;

/**
 * Scraper URL tests #275 for MDPIScraper
 *
 * @author Haile
 */
public class MDPIScraperTest {
	/**
	 * starts URL test with id url_275
	 */
	@Test
	public void url1TestRun(){
		UnitTestRunner.runSingleTest("url_275");
	}
	@Test
	public void testCitedBy() throws Exception{
		final ScrapingContext sc = new ScrapingContext(new URL("http://www.mdpi.com/2072-4292/5/10/5122"));
		MDPIScraper ms = new MDPIScraper();
		assertTrue(ms.scrape(sc));
		assertTrue(ms.scrapeCitedby(sc));
		final String cby = sc.getCitedBy();
		assertNotNull(cby);
		assertTrue(cby.length() > 100);
		assertEquals("Zhang, L.; Guo, H.; Li, X.; Wang, L. Ecosystem assessment in the Tonle Sap Lake region".trim(), cby.substring(0, 86).trim());
		assertTrue(cby.contains("Zhang, L."));
	}

	
}
