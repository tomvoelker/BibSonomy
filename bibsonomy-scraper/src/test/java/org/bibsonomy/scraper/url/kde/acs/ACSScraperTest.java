package org.bibsonomy.scraper.url.kde.acs;

import org.junit.Ignore;
import org.junit.Test;

import org.bibsonomy.scraper.UnitTestRunner;
import static org.junit.Assert.assertTrue;

/**
 * Scraper URL tests #63 & #64 for DBLPScraper
 * @author wbi
 *
 */
public class ACSScraperTest {
	
	/**
	 * starts URL test with id url_63
	 */
	@Test
	@Ignore
	public void url1TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_63"));
	}
	
	/**
	 * starts URL test with id url_64
	 */
	@Test
	@Ignore
	public void url2TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_64"));
	}
	
}
