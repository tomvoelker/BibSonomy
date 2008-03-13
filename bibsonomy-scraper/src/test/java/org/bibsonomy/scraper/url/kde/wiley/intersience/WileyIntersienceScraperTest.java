package org.bibsonomy.scraper.url.kde.wiley.intersience;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Scraper URL tests #33 #34 for WileyIntersienceScraper
 * @author tst
 *
 */
public class WileyIntersienceScraperTest {
	
	/**
	 * starts URL test with id url_33
	 */
	@Test
	@Ignore
	public void url1TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_33"));
	}

	/**
	 * starts URL test with id url_34
	 */
	@Test
	@Ignore
	public void url2TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_34"));
	}

}
