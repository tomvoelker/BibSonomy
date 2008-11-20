package org.bibsonomy.scraper.generic;

import static org.junit.Assert.assertTrue;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Scraper URL tests #115 for BibtexScraper
 * @author tst
 * 
 */
public class BibtexScraperTest {
	
	/**
	 * starts URL test with id url_115
	 */
	@Test
	@Ignore
	public void url1TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_115"));
	}
	
}
