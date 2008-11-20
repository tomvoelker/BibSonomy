package org.bibsonomy.scraper.url.kde.wileyintersience;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Scraper URL tests #33 #34 #109 for WileyIntersienceScraper
 * @author tst
 *
 */
public class WileyIntersienceScraperTest {
	
	/**
	 * starts URL test with id url_33
	 */
	@Test
	public void url1TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_33"));
	}

	/**
	 * starts URL test with id url_34
	 */
	@Test
	public void url2TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_34"));
	}

	/**
	 * starts URL test with id url_109
	 */
	@Test
	public void url3TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_109"));
	}
	
}
