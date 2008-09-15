package org.bibsonomy.scraper.url.kde.ieee;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Scraper URL tests #13 #127 for IEEEXploreJournalProceedingsScraper
 * @author tst
 *
 */
public class IEEEXploreJournalProceedingsScraperTest {
	
	/**
	 * starts URL test with id url_13
	 */
	@Test
	//@Ignore
	public void urlTestRun1(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_13"));
	}

	/**
	 * starts URL test with id url_127
	 */
	@Test
	//@Ignore
	public void urlTestRun2(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_127"));
	}
}