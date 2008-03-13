package org.bibsonomy.scraper.url.kde.citebase;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Scraper URL tests #11 for CiteBaseScraper
 * @author tst
 *
 */
public class CiteBaseScraperTest {
	
	/**
	 * starts URL test with id url_11
	 */
	@Test
	@Ignore
	public void urlTestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_11"));
	}
	
}
