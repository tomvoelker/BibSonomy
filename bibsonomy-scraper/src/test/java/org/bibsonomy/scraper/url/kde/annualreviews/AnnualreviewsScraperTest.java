package org.bibsonomy.scraper.url.kde.annualreviews;

import static org.junit.Assert.assertTrue;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Scraper URL tests #106 #107 for AnnualreviewsScraper
 * @author tst
 * @version $Id$
 */
public class AnnualreviewsScraperTest {
	
	/**
	 * starts URL test with id url_106
	 */
	@Test
	@Ignore
	public void url1TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_106"));
	}

	/**
	 * starts URL test with id url_107
	 */
	@Test
	@Ignore
	public void url2TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_107"));
	}
	
}
