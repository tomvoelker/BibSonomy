package org.bibsonomy.scraper.url.kde.pubmedcentral;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Scraper URL tests #26 for PubMedCentralScraper
 * @author tst
 *
 */
public class PubMedCentralScraperTest {
	
	/**
	 * starts URL test with id url_26
	 */
	@Test
	@Ignore
	public void urlTestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_26"));
	}
	
}