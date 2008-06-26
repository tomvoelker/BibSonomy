package org.bibsonomy.scraper.url.kde.jmlr;

import static org.junit.Assert.assertTrue;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Scraper URL tests #101, #102, #103 for JMLRScraper
 * @author tst
 * @version $Id$
 */
public class JMLRScraperTest {
	
	/**
	 * starts URL test with id url_101
	 */
	@Test
	@Ignore
	public void url1TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_101"));
	}
	
	/**
	 * starts URL test with id url_102
	 */
	@Test
	@Ignore
	public void url2TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_102"));
	}
	
	/**
	 * starts URL test with id url_103
	 */
	@Test
	@Ignore
	public void url3TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_103"));
	}
	
}
