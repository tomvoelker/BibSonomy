package org.bibsonomy.scraper.url.kde.citeseer;

import static org.junit.Assert.assertTrue;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test for old citeseer Scraper. This one is never used.
 * 
 * Scraper URL tests #12 for CiteseerBasicScraper
 * @author tst
 *
 */
public class CiteseerBasicScraperTest {
	
	/**
	 * starts URL test with id url_12
	 */
	@Test
	@Ignore
	public void urlTestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_12"));
	}
	

	
}
