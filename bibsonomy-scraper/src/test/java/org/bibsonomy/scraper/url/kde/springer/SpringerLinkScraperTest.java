package org.bibsonomy.scraper.url.kde.springer;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Scraper URL tests #30 #41 for SpringerLinkScraper
 * @author tst
 *
 */
public class SpringerLinkScraperTest {
	
	/**
	 * starts URL test with id url_30
	 */
	@Test
	@Ignore
	public void url1TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_30"));
	}
	
	/**
	 * starts URL test with id url_41
	 */
	@Test
	@Ignore
	public void url2TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_41"));
	}

	/**
	 * starts URL test with id url_142
	 */
	@Test
	@Ignore
	public void url3TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_142"));
	}
	
}
