package org.bibsonomy.scraper.url.kde.anthrosource;

import org.junit.Ignore;
import org.junit.Test;

import org.bibsonomy.scraper.UnitTestRunner;
import static org.junit.Assert.assertTrue;

/**
 * Scraper URL tests #58 & #59 for DBLPScraper
 * @author wbi
 *
 */
public class AnthroSourceScraperTest {
	
	/**
	 * starts URL test with id url_66
	 */
	@Test
	@Ignore
	public void url1TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_66"));
	}
	
	/**
	 * starts URL test with id url_67
	 */
	@Test
	@Ignore
	public void url2TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_67"));
	}
	
}
