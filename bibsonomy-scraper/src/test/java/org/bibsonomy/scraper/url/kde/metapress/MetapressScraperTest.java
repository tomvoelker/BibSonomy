package org.bibsonomy.scraper.url.kde.metapress;

import static org.junit.Assert.assertTrue;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Scraper URL tests #111 for MetapressScraper
 * @author tst
 *
 */
public class MetapressScraperTest {
	
	/**
	 * starts URL test with id url_111
	 */
	@Test
	@Ignore
	public void url1TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_111"));
	}

}
