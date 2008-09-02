package org.bibsonomy.scraper.url.kde.wormbase;

import static org.junit.Assert.assertTrue;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Scraper URL tests #125 for WormbaseScraper
 * @author tst
 * @version $Id$
 */
public class WormbaseScraperTest {

	/**
	 * starts URL test with id url_125
	 */
	@Test
	@Ignore
	public void url1TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_125"));
	}
	
}
