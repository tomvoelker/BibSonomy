package org.bibsonomy.scraper.url.kde.biomed;

import org.junit.Ignore;
import org.junit.Test;

import org.bibsonomy.scraper.UnitTestRunner;
import static org.junit.Assert.assertTrue;

/**
 * Scraper URL tests #58 & #59 for DBLPScraper
 * @author wbi
 *
 */
public class BioMedCentralScraperTest {
	
	/**
	 * starts URL test with id url_61
	 */
	@Test
	@Ignore
	public void url1TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_61"));
	}
	
	/**
	 * starts URL test with id url_62
	 */
	@Test
	@Ignore
	public void url2TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_62"));
	}
	
}
