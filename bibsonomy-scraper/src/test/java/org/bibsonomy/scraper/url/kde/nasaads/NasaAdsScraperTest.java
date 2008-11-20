package org.bibsonomy.scraper.url.kde.nasaads;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Scraper URL tests #22 #23 for NasaAdsScraper
 * @author tst
 *
 */
@Ignore
public class NasaAdsScraperTest {
	
	/**
	 * starts URL test with id url_22
	 */
	@Test
	public void url1TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_22"));
	}

	/**
	 * starts URL test with id url_23
	 */
	@Test
	public void url2TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_23"));
	}
	
}
