package org.bibsonomy.scraper.url.kde.worldcat;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Scraper URL tests #59 and #60 for WorldCatScraper
 * @author tst
 *
 */
public class WorldCatScraperTest {
	
	/**
	 * starts URL test with id url_59
	 */
	@Test
	@Ignore
	public void url1TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_59"));
	}

	/**
	 * starts URL test with id url_60
	 */
	@Test
	//@Ignore
	public void url2TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_60"));
	}
	
}
