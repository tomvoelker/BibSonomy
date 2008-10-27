package org.bibsonomy.scraper.url.kde.citeulike;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Scraper URL tests #131 #132 for CiteulikeScraper
 * @author tst
 */
public class CiteulikeScraperTest {

	/**
	 * starts URL test with id url_131
	 */
	@Test
	@Ignore
	public void urlTestRun1(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_131"));
	}
	
	/**
	 * starts URL test with id url_132
	 */
	@Test
	@Ignore
	public void urlTestRun2(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_132"));
	}
	
}
