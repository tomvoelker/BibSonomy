package org.bibsonomy.scraper.url.kde.usenix;

import static org.junit.Assert.assertTrue;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Scraper URL tests #74 #75 #76 for UsenixScraper
 * @author tst
 */
public class UsenixScraperTest {

	/**
	 * starts URL test with id url_74
	 */
	@Test
	@Ignore
	public void url1TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_73"));
	}

	/**
	 * starts URL test with id url_75
	 */
	@Test
	@Ignore
	public void url2TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_74"));
	}
	
	/**
	 * starts URL test with id url_76
	 */
	@Test
	@Ignore
	public void url3TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_75"));
	}
	
}
