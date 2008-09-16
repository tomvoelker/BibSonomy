package org.bibsonomy.scraper.url.kde.ieee;

import static org.junit.Assert.assertTrue;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Scraper URL tests #104 #128 for IEEEXploreScraper
 * @author tst
 *
 */
public class IEEEXploreScraperTest {

	/**
	 * starts URL test with id url_104
	 */
	@Test
	@Ignore
	public void urlTestRun1(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_104"));
	}
	
	/**
	 * starts URL test with id url_128
	 */
	@Test
	@Ignore
	public void urlTestRun2(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_128"));
	}
}
