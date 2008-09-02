package org.bibsonomy.scraper.url.kde.ams;

import static org.junit.Assert.assertTrue;

import org.bibsonomy.scraper.UnitTestRunner;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Scraper URL tests #121 #122 for AmsScraper
 * @author tst
 * @version $Id$
 */
public class AmsScraperTest {
	
	/**
	 * starts URL test with id url_121
	 */
	@Test
	@Ignore
	public void url1TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_121"));
	}

	/**
	 * starts URL test with id url_122
	 */
	@Test
	@Ignore
	public void url2TestRun(){
		UnitTestRunner runner = new UnitTestRunner();
		assertTrue(runner.runSingleTest("url_122"));
	}

}
