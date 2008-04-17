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
	 * starts URL test with id url_58
	 */
	@Test
	@Ignore
	public void url1TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_58"));
	}
	
	/**
	 * starts URL test with id url_59
	 */
	@Test
	@Ignore
	public void url2TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_59"));
	}
	
}
