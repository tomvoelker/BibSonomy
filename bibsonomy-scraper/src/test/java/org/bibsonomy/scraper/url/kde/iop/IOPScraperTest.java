package org.bibsonomy.scraper.url.kde.iop;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Scraper URL tests #16 for IOPScraper
 * @author tst
 *
 */
public class IOPScraperTest {
	
	/**
	 * starts URL test with id url_16
	 */
	@Test
	public void urlTestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_16"));
	}
	
}
