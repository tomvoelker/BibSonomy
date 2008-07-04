package org.bibsonomy.scraper.url.kde.cambridge;

import static org.junit.Assert.assertTrue;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Scraper URL test #73 #110 for CambridgeScraper
 * @author wbi
 */
public class CambridgeScraperTest {

	/**
	 * starts URL test with id url_73
	 */
	@Test
	@Ignore
	public void url1TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_73"));
	}
	
	/**
	 * starts URL test with id url_110
	 */
	@Test
	@Ignore
	public void url2TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_110"));
	}
}

